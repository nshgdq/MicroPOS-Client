package ow.micropos.client.desktop;

import ow.micropos.client.desktop.model.charge.Charge;
import ow.micropos.client.desktop.model.enums.SalesOrderStatus;
import ow.micropos.client.desktop.model.enums.SalesOrderType;
import ow.micropos.client.desktop.model.menu.Category;
import ow.micropos.client.desktop.model.menu.MenuItem;
import ow.micropos.client.desktop.model.menu.ModifierGroup;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.model.people.Customer;
import ow.micropos.client.desktop.model.people.Employee;
import ow.micropos.client.desktop.model.report.CurrentSalesReport;
import ow.micropos.client.desktop.model.seating.Section;
import retrofit.Callback;
import retrofit.http.*;

import java.util.List;
import java.util.Map;

public interface AppApi {

    /******************************************************************
     *                                                                *
     * General Controller
     *                                                                *
     ******************************************************************/

    /* Employee Login *************************************************/
    @GET("/employees")
    void getEmployee(@Query("pin") String pin, Callback<Employee> callback);

    /* Section Requests ***********************************************/

    @GET("/sections")
    void getSections(Callback<List<Section>> callback);

    @GET("/sections/{id}")
    void getSection(@Path(value = "id") long id, Callback<Section> callback);

    /* Customer Requests **********************************************/

    @GET("/customers")
    void getCustomers(Callback<List<Customer>> callback);

    @GET("/customers/{id}")
    void getCustomer(@Path(value = "id") long id, Callback<Customer> callback);

    @POST("/customers")
    void postCustomer(@Body Customer customer, Callback<Long> callback);

    /* Menu Requests **************************************************/

    @GET("/charges")
    void getCharges(Callback<List<Charge>> callback);

    @GET("/categories")
    void getCategories(Callback<List<Category>> callback);

    @GET("/modifierGroups")
    void getModifierGroups(Callback<List<ModifierGroup>> callback);

    /* Order Requests *************************************************/

    @GET("/orders")
    void getSalesOrders(Callback<List<SalesOrder>> callback);

    @GET("/orders")
    void getSalesOrders(@Query("status") SalesOrderStatus status, @Query("type") SalesOrderType type, Callback<List<SalesOrder>> callback);

    @GET("/orders/seat")
    void getSalesOrderBySeat(@Query("id") long id, @Query("status") SalesOrderStatus status, Callback<List<SalesOrder>> callback);

    @GET("/orders/customer")
    void getSalesOrderByCustomer(@Query("id") long id, @Query("status") SalesOrderStatus status, Callback<List<SalesOrder>> callback);

    @POST("/orders")
    void postSalesOrder(@Body SalesOrder salesOrder, Callback<Long> callback);

    @POST("/orders/batch")
    void postSalesOrders(@Body List<SalesOrder> salesOrders, Callback<List<Long>> callback);

    /******************************************************************
     *                                                                *
     * Migration Controller
     *                                                                *
     ******************************************************************/

    @GET("/migration")
    void migrateSalesOrders(Callback<Integer> callback);

    @GET("/migration/closeUnpaid")
    void closeUnpaidSalesOrders(Callback<List<Long>> callback);

    /******************************************************************
     *                                                                *
     * Settings Controller
     *                                                                *
     ******************************************************************/

    @GET("/settings")
    void getSettings(@Query("keys") String[] keys, Callback<Map<String, String>> callback);

    /******************************************************************
     *                                                                *
     * Reports Controller
     *                                                                *
     ******************************************************************/

    @GET("/reports/current")
    void getCurrentReport(Callback<CurrentSalesReport> callback);

    /******************************************************************
     *                                                                *
     * Database API
     *                                                                *
     ******************************************************************/

    @GET("/database/menuItems")
    void getMenuItems(
            Callback<List<MenuItem>> callback
    );

}
