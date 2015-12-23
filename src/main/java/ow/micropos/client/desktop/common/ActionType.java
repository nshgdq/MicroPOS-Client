package ow.micropos.client.desktop.common;

/*=======================================================================*
 =                                                                       =
 = Top menu button type to be styled
 =                                                                       =
 *=======================================================================*/

public enum ActionType {

    TAB_DEFAULT("defaultItem"),
    TAB_SELECT("selectedItem"),
    BUTTON("buttonItem"),
    FINISH("finishItem");

    private final String cssId;

    ActionType(String cssId) {
        this.cssId = cssId;
    }

    public String getCssId() {
        return cssId;
    }

}
