package email.com.gmail.ttsai0509.javafx.control;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import email.com.gmail.ttsai0509.utils.Procedure;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;


/******************************************************************

 Sample Usage

 gridView.setCellFactory(param -> {
 ViewSeat presenter = Presenter.load("/view/dinein/view_seat.fxml");
 presenter.getView().setOnMouseClicked(null);
 return presenter;
 });

 gridView.setItems(section.getSeats());
 gridView.setRows(section.getRows());
 gridView.setCols(section.getCols());

 gridView.setPageResolver(s -> 0);
 gridView.setRowResolver(Seat::getRow);
 gridView.setColResolver(Seat::getCol);

 gridView.setPage(0);
 gridView.nextPage();
 gridView.prevPage();

 ******************************************************************/

public class GridView<T> extends GridPane {

    private static final String styleClass = "grid-view";

    public GridView() {
        super();
        getStyleClass().add(styleClass);
    }

    /******************************************************************
     *                                                                *
     * Items                                                          *
     *                                                                *
     ******************************************************************/

    private final ListProperty<T> items = new SimpleListProperty<>(FXCollections.observableArrayList());

    {
        items.addListener((obs, oldVal, newVal) -> {
            this.activeMax.invoke();
            this.activeDraw.invoke();
        });
    }

    public ObservableList<T> getItems() {
        return items.get();
    }

    public ListProperty<T> itemsProperty() {
        return items;
    }

    public void setItems(ObservableList<T> items) {
        this.items.set(items);
    }

    /******************************************************************
     *                                                                *
     * Cell Cache                                                     *
     *                                                                *
     ******************************************************************/

    private final List<ItemPresenter<T>> cellCache = new ArrayList<>();

    private void verifyCache() {
        int cur = cellCache.size();
        int req = cols.get() * rows.get();

        if (getCellFactory() != null)
            for (int i = cur; i < req; i++)
                cellCache.add(getCellFactory().call(this));
    }

    private void clearCache() {
        cellCache.clear();
    }

    private void clearAndVerifyCache() {
        clearCache();
        verifyCache();
    }

    /******************************************************************
     *                                                                *
     * Draw                                                           *
     *                                                                *
     ******************************************************************/

    private Procedure activeDraw = this::listifyDraw;

    private void listifyDraw() {
        getChildren().clear();

        int offset = getPage() * size.get();
        int rows = getRows();
        int cols = getCols();
        boolean horizontal = this.getHorizontal();

        if (rows == 0 || cols == 0)
            return;

        int r = 0, c = 0;
        for (int v = 0; v < size.get(); v++) {

            if (offset + v >= items.size())
                return;

            if (horizontal) {
                if (c >= cols) {
                    c = 0;
                    r++;
                }

                if (r >= rows) {
                    return;
                }

            } else {
                if (r >= rows) {
                    r = 0;
                    c++;
                }

                if (c >= cols) {
                    return;
                }

            }

            ItemPresenter<T> cell = cellCache.get(v);
            cell.setItem(items.get(offset + v));
            add(cell.getView(), c, r);

            if (horizontal) {
                c++;
            } else {
                r++;
            }
        }
    }

    private void resolveDraw() {
        getChildren().clear();

        int page = getPage();
        int limit = cellCache.size();
        ToIntFunction<T> pageResolver = getPageResolver();
        ToIntFunction<T> rowResolver = getRowResolver();
        ToIntFunction<T> colResolver = getColResolver();

        int i = 0;
        for (T item : items) {
            if (i >= limit)
                return;

            if (pageResolver.applyAsInt(item) == page) {
                ItemPresenter<T> cell = cellCache.get(i);
                cell.setItem(item);
                add(cell.getView(), colResolver.applyAsInt(item), rowResolver.applyAsInt(item));
                i++;
            }
        }
    }

    /******************************************************************
     *                                                                *
     * Pagination                                                     *
     *                                                                *
     ******************************************************************/

    private final IntegerProperty page = new SimpleIntegerProperty(0);
    private final IntegerProperty pageCount = new SimpleIntegerProperty(0);

    {
        page.addListener((obs, oldVal, newVal) -> this.activeDraw.invoke());
    }

    public int getPage() {
        return page.get();
    }

    public IntegerProperty pageProperty() {
        return page;
    }

    public void setPage(int page) {
        this.page.set(page);
    }

    public void prevPage() {
        if (getPage() <= 0)
            setPage(0);
        else
            setPage(getPage() - 1);
    }

    public void nextPage() {
        if (getPage() < pageCount.get() - 1)
            setPage(getPage() + 1);
        else
            setPage(pageCount.get() - 1);
    }

    public void firstPage() {
        setPage(0);
    }

    public void lastPage() {
        setPage(pageCount.get() - 1);
    }

    private Procedure activeMax = this::listifyMax;

    private void listifyMax() {
        if (size.get() == 0)
            pageCount.set(1);
        else
            pageCount.set((items.get().size() + size.get() - 1) / size.get());
    }

    private void resolveMax() {
        int max = 0;
        for (T item : items) {
            int curr = pageResolver.get().applyAsInt(item);
            if (curr > max)
                max = curr;
        }
        pageCount.set(max);
    }

    /******************************************************************
     *                                                                *
     * Resize                                                         *
     *                                                                *
     ******************************************************************/

    private final IntegerProperty size = new SimpleIntegerProperty();
    private final IntegerProperty cols = new SimpleIntegerProperty();
    private final IntegerProperty rows = new SimpleIntegerProperty();

    {
        size.bind(cols.multiply(rows));

        cols.addListener((obs, oldVal, newVal) -> {
            ColumnConstraints newCC = new ColumnConstraints();
            newCC.setPercentWidth(100.0 / newVal.doubleValue());

            ObservableList<ColumnConstraints> cc = getColumnConstraints();
            cc.clear();

            for (int c = 0; c < newVal.intValue(); c++)
                cc.add(newCC);

            verifyCache();
            activeDraw.invoke();
            activeMax.invoke();
        });

        rows.addListener((obs, oldVal, newVal) -> {
            RowConstraints newRC = new RowConstraints();
            newRC.setPercentHeight(100.0 / newVal.doubleValue());

            ObservableList<RowConstraints> rc = getRowConstraints();
            rc.clear();

            for (int r = 0; r < newVal.intValue(); r++)
                rc.add(newRC);

            verifyCache();
            activeDraw.invoke();
            activeMax.invoke();
        });
    }

    public int getCols() {
        return cols.get();
    }

    public IntegerProperty colsProperty() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols.set(cols);
    }

    public int getRows() {
        return rows.get();
    }

    public IntegerProperty rowsProperty() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows.set(rows);
    }

    /******************************************************************
     *                                                                *
     * Direction                                                      *
     *                                                                *
     ******************************************************************/

    private final BooleanProperty horizontal = new SimpleBooleanProperty(true);

    {
        horizontal.addListener((obs, oldVal, newVal) -> this.activeDraw.invoke());
    }

    public boolean getHorizontal() {
        return horizontal.get();
    }

    public BooleanProperty horizontalProperty() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal.set(horizontal);
    }

    /******************************************************************
     *                                                                *
     * Cell Factory                                                   *
     *                                                                *
     ******************************************************************/

    private final ObjectProperty<Callback<GridView<T>, ItemPresenter<T>>> cellFactory = new SimpleObjectProperty<>();

    {
        cellFactory.addListener((obs, oldVal, newVal) -> {
            clearAndVerifyCache();
            activeDraw.invoke();
        });
    }

    public Callback<GridView<T>, ItemPresenter<T>> getCellFactory() {
        return cellFactory.get();
    }

    public ObjectProperty<Callback<GridView<T>, ItemPresenter<T>>> cellFactoryProperty() {
        return cellFactory;
    }

    public void setCellFactory(Callback<GridView<T>, ItemPresenter<T>> cellFactory) {
        this.cellFactory.set(cellFactory);
    }

    /******************************************************************
     *                                                                *
     * Resolvers                                                      *
     *                                                                *
     ******************************************************************/

    private final ObjectProperty<ToIntFunction<T>> rowResolver = new SimpleObjectProperty<>();
    private final ObjectProperty<ToIntFunction<T>> colResolver = new SimpleObjectProperty<>();
    private final ObjectProperty<ToIntFunction<T>> pageResolver = new SimpleObjectProperty<>();

    private ChangeListener<ToIntFunction<T>> resolveDrawEnabler = (obs, oldVal, newVal) -> {
        if (getRowResolver() == null || getColResolver() == null || getPageResolver() == null) {
            activeDraw = this::listifyDraw;
            activeMax = this::listifyMax;
        } else {
            activeDraw = this::resolveDraw;
            activeMax = this::resolveMax;
        }
    };

    {
        rowResolver.addListener(resolveDrawEnabler);
        colResolver.addListener(resolveDrawEnabler);
        pageResolver.addListener(resolveDrawEnabler);
    }

    public ToIntFunction<T> getRowResolver() {
        return rowResolver.get();
    }

    public ObjectProperty<ToIntFunction<T>> rowResolverProperty() {
        return rowResolver;
    }

    public void setRowResolver(ToIntFunction<T> rowResolver) {
        this.rowResolver.set(rowResolver);
    }

    public ToIntFunction<T> getColResolver() {
        return colResolver.get();
    }

    public ObjectProperty<ToIntFunction<T>> colResolverProperty() {
        return colResolver;
    }

    public void setColResolver(ToIntFunction<T> colResolver) {
        this.colResolver.set(colResolver);
    }

    public ToIntFunction<T> getPageResolver() {
        return pageResolver.get();
    }

    public ObjectProperty<ToIntFunction<T>> pageResolverProperty() {
        return pageResolver;
    }

    public void setPageResolver(ToIntFunction<T> pageResolver) {
        this.pageResolver.set(pageResolver);
    }

}
