package ow.micropos.client.desktop.service;

import ow.micropos.client.desktop.model.auth.Position;
import ow.micropos.client.desktop.model.employee.Employee;
import ow.micropos.client.desktop.model.enums.SalesOrderStatus;
import ow.micropos.client.desktop.model.enums.SalesOrderType;
import ow.micropos.client.desktop.model.menu.*;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.model.report.MonthlySalesReport;
import ow.micropos.client.desktop.model.report.SalesReport;
import ow.micropos.client.desktop.model.target.Customer;
import ow.micropos.client.desktop.model.target.Seat;
import ow.micropos.client.desktop.model.target.Section;
import ow.micropos.client.desktop.model.timecard.TimeCardEntry;
import retrofit.Callback;
import retrofit.http.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RestService {

    /******************************************************************
     *                                                                *
     * TimeCard Controller
     *                                                                *
     ******************************************************************/

    @GET("/timecard/view")
    void getTimeCard(@Query("pin") String pin, Callback<List<TimeCardEntry>> callback);

    @POST("/timecard/clockin")
    void clockIn(@Query("pin") String pin, @Body String image, Callback<TimeCardEntry> callback);

    @POST("/timecard/clockout")
    void clockOut(@Query("pin") String pin, @Body String image, Callback<TimeCardEntry> callback);

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

    /******************************************************************
     *                                                                *
     * Order Controller
     *                                                                *
     ******************************************************************/

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

    @POST("/orders/split")
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

    @Deprecated
    @GET("/settings")
    void getSettings(@Query("keys") String[] keys, Callback<Map<String, String>> callback);

    /******************************************************************
     *                                                                *
     * Reports Controller
     *                                                                *
     ******************************************************************/

    @GET("/reports/sales")
    void getSalesReport(@Query("start") Date start, @Query("end") Date end, @Query("status") SalesOrderStatus status, @Query("type") SalesOrderType type, Callback<SalesReport> callback);

    @GET("/reports/monthly")
    void getMonthlySalesReport(@Query("monthOf") Date monthOf, Callback<MonthlySalesReport> callback);

    /******************************************************************
     *                                                                *
     * Database Controller
     *                                                                *
     ******************************************************************/

    /* Positions ******************************************************/
    @GET("/database/positions")
    void listPositions(Callback<List<Position>> callback);

    @POST("/database/positions")
    void updatePosition(@Body Position position, Callback<Long> callback);

    @DELETE("/database/positions/{id}")
    void removePosition(@Path("id") long id, Callback<Boolean> callback);

    /* Employees ******************************************************/
    @GET("/database/employees")
    void listEmployees(Callback<List<Employee>> callback);

    @POST("/database/employees")
    void updateEmployee(@Body Employee employee, Callback<Long> callback);

    @DELETE("/database/employees/{id}")
    void removeEmployee(@Path("id") long id, Callback<Boolean> callback);

    /* Sales Orders ***************************************************/
    @GET("/database/salesOrders")
    void listSalesOrders(Callback<List<SalesOrder>> callback);

    @DELETE("/database/salesOrders/{id}")
    void removeSalesOrder(@Path("id") long id, Callback<Boolean> callback);

    /* Customers ******************************************************/
    @GET("/database/customers")
    void listCustomers(Callback<List<Customer>> callback);

    @POST("/database/customers")
    void updateCustomer(@Body Customer customer, Callback<Long> callback);

    @DELETE("/database/customers/{id}")
    void removeCustomer(@Path("id") long id, Callback<Boolean> callback);

    /* Menu Items *****************************************************/

    @GET("/database/menuItems")
    void listMenuItems(Callback<List<MenuItem>> callback);

    @POST("/database/menuItems")
    void updateMenuItem(@Body MenuItem menuItem, Callback<Long> callback);

    @DELETE("/database/menuItems/{id}")
    void removeMenuItem(@Path("id") long id, Callback<Boolean> callback);

    /* Categories *****************************************************/

    @GET("/database/categories")
    void listCategories(Callback<List<Category>> callback);

    @POST("/database/categories")
    void updateCategory(@Body Category category, Callback<Long> callback);

    @DELETE("/database/categories/{id}")
    void removeCategory(@Path("id") long id, Callback<Boolean> callback);

    /* Sections *******************************************************/

    @GET("/database/sections")
    void listSections(Callback<List<Section>> callback);

    @POST("/database/sections")
    void updateSection(@Body Section section, Callback<Long> callback);

    @DELETE("/database/sections/{id}")
    void removeSection(@Path("id") long id, Callback<Boolean> callback);

    /* Seats **********************************************************/

    @GET("/database/seats")
    void listSeats(Callback<List<Seat>> callback);

    @POST("/database/seats")
    void updateSeat(@Body Seat seat, Callback<Long> callback);

    @DELETE("/database/seats/{id}")
    void removeSeat(@Path("id") long id, Callback<Boolean> callback);

    /* Modifiers ******************************************************/

    @GET("/database/modifiers")
    void listModifiers(Callback<List<Modifier>> callback);

    @POST("/database/modifiers")
    void updateModifier(@Body Modifier modifier, Callback<Long> callback);

    @DELETE("/database/modifiers/{id}")
    void removeModifier(@Path("id") long id, Callback<Boolean> callback);

    /* Modifier Groups*************************************************/

    @GET("/database/modifierGroups")
    void listModifierGroups(Callback<List<ModifierGroup>> callback);

    @POST("/database/modifierGroups")
    void updateModifierGroup(@Body ModifierGroup modifierGroup, Callback<Long> callback);

    @DELETE("/database/modifierGroups/{id}")
    void removeModifierGroup(@Path("id") long id, Callback<Boolean> callback);

    /* Charges*** *****************************************************/

    @GET("/database/charges")
    void listCharges(Callback<List<Charge>> callback);

    @POST("/database/charges")
    void updateCharge(@Body Charge charge, Callback<Long> callback);

    @DELETE("/database/charges/{id}")
    void removeCharge(@Path("id") long id, Callback<Boolean> callback);

    /* Time Card Entries **********************************************/

    @GET("/database/timeCardEntries")
    void listTimeCardEntries(@Query("employee") Long id, @Query("start") Date start, @Query("end") Date end, Callback<List<TimeCardEntry>> callback);

    @POST("/database/timeCardEntries")
    void updateTimeCardEntry(@Body TimeCardEntry timeCardEntry, Callback<Long> callback);

    @DELETE("/database/timeCardEntries/{id}")
    void removeTimeCardEntry(@Path("id") long id, Callback<Boolean> callback);

}
