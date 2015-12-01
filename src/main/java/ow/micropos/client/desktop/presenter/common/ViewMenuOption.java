package ow.micropos.client.desktop.presenter.common;

import email.com.gmail.ttsai0509.javafx.presenter.ItemPresenter;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import lombok.Builder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ViewMenuOption extends ItemPresenter<String> {

    @FXML Label val;

    @Override
    protected void updateItem(String currentItem, String newItem) {
        if (newItem != null)
            val.setText(newItem);
    }

    @Override
    public void dispose() {

    }

    /*****************************************************************************
     *                                                                           *
     * Menu Option Builder                                                       *
     *                                                                           *
     *****************************************************************************/

    @Builder
    public static class Config {
        private String text = null;
        private EventHandler<MouseEvent> onEvent = null;
        private String cssId = null;
    }

    public static ViewMenuOption create(Config config) {
        ViewMenuOption view = ItemPresenter.load("/view/common/view_menu_option.fxml");
        if (config.text != null) view.setItem(config.text);
        if (config.onEvent != null) view.getView().setOnMouseClicked(config.onEvent);
        if (config.cssId != null) view.getView().setId(config.cssId);
        return view;
    }

    public static List<ViewMenuOption> creates(Config... configs) {
        return Arrays.asList(configs)
                .stream()
                .map(ViewMenuOption::create)
                .collect(Collectors.toList());
    }

}
