package ow.micropos.client.desktop;

import ow.micropos.client.desktop.model.charge.Charge;
import ow.micropos.client.desktop.model.enums.SalesOrderStatus;
import ow.micropos.client.desktop.model.enums.SalesOrderType;
import ow.micropos.client.desktop.model.menu.Category;
import ow.micropos.client.desktop.model.menu.Menu;
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

    @GET("/orders/seat")
    void getSalesOrderBySeat(
            @Query("id") long id,
            @Query("status") SalesOrderStatus status,
            Callback<List<SalesOrder>> callback
    );

    @GET("/orders/customer")
    void getSalesOrderByCustomer(
            @Query("id") long id,
            @Query("status") SalesOrderStatus status,
            Callback<List<SalesOrder>> callback
    );

    @GET("/charges")
    void getCharges(
            Callback<List<Charge>> callback
    );

    @GET("/people/employees")
    void getEmployees(
            Callback<List<Employee>> callback
    );

    @GET("/people/employee")
    void getEmployee(
            @Query("pin") String pin,
            Callback<Employee> callback
    );

    @GET("/people/customers")
    void getCustomers(
            Callback<List<Customer>> callback
    );

    @GET("/seating/sections")
    void getSections(
            Callback<List<Section>> callback
    );

    @GET("/seating/sections/{id}")
    void getSection(
            @Path(value = "id") long id,
            Callback<Section> callback
    );

    @POST("/people/customers")
    void postCustomer(
            @Body Customer customer,
            Callback<Long> callback
    );

    @GET("/menu")
    void getMenu(
            Callback<Menu> callback
    );

    @GET("/menu/categories")
    void getCategories(
            Callback<List<Category>> callback
    );

    @GET("/menu/modifierGroups")
    void getModifierGroups(
            Callback<List<ModifierGroup>> callback
    );


    @GET("/orders")
    void getSalesOrders(
            Callback<List<SalesOrder>> callback
    );

    @GET("/orders")
    void getSalesOrders(
            @Query("status") SalesOrderStatus status,
            @Query("type") SalesOrderType type,
            Callback<List<SalesOrder>> callback
    );

    @POST("/orders")
    void postSalesOrder(
            @Body SalesOrder salesOrder,
            Callback<Long> callback
    );

    @POST("/orders/batch")
    void postSalesOrders(
            @Body List<SalesOrder> salesOrders,
            Callback<List<Long>> callback
    );

    @GET("/migration")
    void migrateSalesOrders(
            Callback<Integer> callback
    );

    @GET("/migration/closeUnpaid")
    void closeUnpaidSalesOrders(
            Callback<List<Long>> callback
    );

    @GET("/settings")
    void getSettings(
            @Query("keys") String[] keys,
            Callback<Map<String, String>> callback
    );

    @GET("/reports/current")
    void getCurrentReport(
            Callback<CurrentSalesReport> callback
    );

}
