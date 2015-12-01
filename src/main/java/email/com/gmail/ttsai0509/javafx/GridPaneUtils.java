package email.com.gmail.ttsai0509.javafx;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class GridPaneUtils {

    private GridPaneUtils() {
        throw new RuntimeException(GridPaneUtils.class.getSimpleName() + " is a utility class.");
    }

    public static void normalizeGridPane(GridPane gp,
                                         int newColCount,
                                         int newRowCount,
                                         ColumnConstraints hideCC,
                                         ColumnConstraints showCC,
                                         RowConstraints hideRC,
                                         RowConstraints showRC) {

        ObservableList<ColumnConstraints> colConstraints = gp.getColumnConstraints();
        ObservableList<RowConstraints> rowConstraints = gp.getRowConstraints();

        showCC.setPercentWidth(100.0 / newColCount);
        showRC.setPercentHeight(100.0 / newRowCount);

        for (int c = colConstraints.size(); c < newColCount; c++)
            colConstraints.add(showCC);
        for (int r = rowConstraints.size(); r < newRowCount; r++)
            rowConstraints.add(showRC);

        for (int c = 0; c < newColCount; c++)
            colConstraints.set(c, showCC);
        for (int c = newColCount; c < colConstraints.size(); c++)
            colConstraints.set(c, hideCC);

        for (int r = 0; r < newRowCount; r++)
            rowConstraints.set(r, showRC);
        for (int r = newRowCount; r < rowConstraints.size(); r++)
            rowConstraints.set(r, hideRC);

    }

    public static void normalizeGridPane(GridPane gp,
                                         int newColCount,
                                         int newRowCount) {
        normalizeGridPane(gp,
                newColCount,
                newRowCount,
                new ColumnConstraints(0),
                new ColumnConstraints(),
                new RowConstraints(0),
                new RowConstraints());
    }

    public static <T> void resolveGridPane(GridPane gp,
                                           List<T> items,
                                           ToIntFunction<T> rowResolver,
                                           ToIntFunction<T> colResolver,
                                           Function<T, Node> viewResolver) {

        gp.getChildren().clear();

        items.stream()
                .forEach(
                        item -> gp.add(viewResolver.apply(item),
                                colResolver.applyAsInt(item),
                                rowResolver.applyAsInt(item))
                );

    }

    public static <T> void listifyGridPane(GridPane gp,
                                           List<T> items,
                                           int columns,
                                           int rows,
                                           boolean horizontal,
                                           Function<T, Node> viewResolver) {

        gp.getChildren().clear();

        int iRng = horizontal ? rows : columns;
        int jRng = horizontal ? columns : rows;
        if (iRng == 0 || jRng == 0)
            return;

        int i = 0, j = 0;
        for (T item : items) {

            if (j >= jRng) {
                j = 0;
                i++;
            }

            if (i >= iRng) {
                return;
            }

            gp.add(viewResolver.apply(item), j, i);

            j++;
        }
    }

    public static <T> void paginateGridPane(GridPane gp,
                                            List<T> items,
                                            int page,
                                            int columns,
                                            int rows,
                                            boolean horizontal,
                                            Function<T, Node> viewResolver) {

        gp.getChildren().clear();

        int pageSize = columns * rows;
        int iRng = horizontal ? rows : columns;
        int jRng = horizontal ? columns : rows;
        if (iRng == 0 || jRng == 0)
            return;

        int start = page * pageSize;
        int finish = start + pageSize;

        int i = 0, j = 0;
        for (int idx = page * pageSize; i < finish; i++) {
            if (idx >= items.size())
                return;

            T item = items.get(idx);

            if (j >= jRng) {
                j = 0;
                i++;
            }

            if (i >= iRng)
                return;

            gp.add(viewResolver.apply(item), j, i);

            j++;
        }
    }

    public static <T> Function<T, Node> cachedResolver(List<? extends ItemPresenter<T>> cache) {
        return new Function<T, Node>() {
            int i = 0;

            @Override
            public Node apply(T item) {
                ItemPresenter<T> view = cache.get(i);
                view.setRefresh(item);
                i++;
                return view.getView();
            }
        };
    }

}