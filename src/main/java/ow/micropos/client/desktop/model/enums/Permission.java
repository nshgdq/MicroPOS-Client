package ow.micropos.client.desktop.model.enums;


public enum Permission {

    GET_EMPLOYEES,
    GET_CUSTOMERS,
    GET_SECTIONS,
    GET_MENU,
    GET_CHARGES,

    GET_SALES_ORDERS,
    GET_DINE_IN_SALES_ORDERS,
    GET_TAKE_OUT_SALES_ORDERS,

    POST_CUSTOMER,

    CREATE_SALES_ORDER,
    REOPEN_SALES_ORDER,
    CLOSE_SALES_ORDER,
    VOID_SALES_ORDER,

    CREATE_PAYMENT_ENTRY,
    REOPEN_PAYMENT_ENTRY,
    VOID_PAYMENT_ENTRY,

    CREATE_CHARGE_ENTRY,
    REOPEN_CHARGE_ENTRY,
    VOID_CHARGE_ENTRY,

    CREATE_PRODUCT_ENTRY,
    REOPEN_PRODUCT_ENTRY,
    EDIT_PRODUCT_ENTRY,
    HOLD_PRODUCT_ENTRY,
    VOID_PRODUCT_ENTRY,


    SPLIT_SALES_ORDER,

    ACCESS_ALL_EMPLOYEE_ORDER,

    READ_SETTINGS,
    WRITE_SETTINGS,

    CLOSE_UNPAID_ORDERS,
    MIGRATION,
    GET_RECORDS,
    SIMPLE_REPORT,
    CURRENT_REPORT


}
