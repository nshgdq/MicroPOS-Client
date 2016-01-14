package ow.micropos.client.desktop.service;

import ow.micropos.client.desktop.model.auth.Position;
import ow.micropos.client.desktop.model.employee.Employee;
import ow.micropos.client.desktop.model.enums.SalesOrderStatus;
import ow.micropos.client.desktop.model.enums.SalesOrderType;
import ow.micropos.client.desktop.model.error.ErrorInfo;
import ow.micropos.client.desktop.model.menu.*;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.model.report.ActiveSalesReport;
import ow.micropos.client.desktop.model.report.DaySalesReport;
import ow.micropos.client.desktop.model.target.Customer;
import ow.micropos.client.desktop.model.target.Seat;
import ow.micropos.client.desktop.model.target.Section;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class RestServiceProxy {

    public static enum OnCondition {SERVICE_UNUSED, METHOD_UNUSED, ALWAYS}

    private static int methodCount = 52;

    private final AtomicBoolean[] methodInUse;
    private final RestService service;

    public RestServiceProxy(RestService service) {
        this.service = service;
        this.methodInUse = new AtomicBoolean[methodCount];

        for (int i = 0; i < methodCount; i++)
            methodInUse[i] = new AtomicBoolean(false);
    }

    public void getEmployee(OnCondition condition, String pin, RestServiceCallback<Employee> callback) {

    }

    public void getSections(RestServiceCallback<List<Section>> callback) { }

    public void getSection(long id, RestServiceCallback<Section> callback) { }

    public void getCustomers(RestServiceCallback<List<Customer>> callback) { }

    public void getCustomer(long id, RestServiceCallback<Customer> callback) { }

    public void postCustomer(Customer customer, RestServiceCallback<Long> callback) { }

    public void getCharges(RestServiceCallback<List<Charge>> callback) { }

    public void getCategories(RestServiceCallback<List<Category>> callback) { }

    public void getModifierGroups(RestServiceCallback<List<ModifierGroup>> callback) { }

    public void getSalesOrders(RestServiceCallback<List<SalesOrder>> callback) { }

    public void getSalesOrders(SalesOrderStatus status, SalesOrderType type, RestServiceCallback<List<SalesOrder>> callback) { }

    public void getSalesOrderBySeat(long id, SalesOrderStatus status, RestServiceCallback<List<SalesOrder>> callback) { }

    public void getSalesOrderByCustomer(long id, SalesOrderStatus status, RestServiceCallback<List<SalesOrder>> callback) { }

    public void postSalesOrder(SalesOrder salesOrder, RestServiceCallback<Long> callback) { }

    public void postSalesOrders(List<SalesOrder> salesOrders, RestServiceCallback<List<Long>> callback) { }

    public void migrateSalesOrders(RestServiceCallback<Integer> callback) { }

    public void closeUnpaidSalesOrders(RestServiceCallback<List<Long>> callback) { }

    public void getSettings(String[] keys, RestServiceCallback<Map<String, String>> callback) { }

    public void getCurrentReport(RestServiceCallback<ActiveSalesReport> callback) { }

    public void getDayReport(int year, int month, int day, RestServiceCallback<DaySalesReport> callback) { }

    public void listPositions(RestServiceCallback<List<Position>> callback) { }

    public void updatePosition(Position position, RestServiceCallback<Long> callback) { }

    public void removePosition(long id, RestServiceCallback<Boolean> callback) { }

    public void listEmployees(RestServiceCallback<List<Employee>> callback) { }

    public void updateEmployee(Employee employee, RestServiceCallback<Long> callback) { }

    public void removeEmployee(long id, RestServiceCallback<Boolean> callback) { }

    public void listSalesOrders(RestServiceCallback<List<SalesOrder>> callback) { }

    public void removeSalesOrder(long id, RestServiceCallback<Boolean> callback) { }

    public void listCustomers(RestServiceCallback<List<Customer>> callback) { }

    public void updateCustomer(Customer customer, RestServiceCallback<Long> callback) { }

    public void removeCustomer(long id, RestServiceCallback<Boolean> callback) { }

    public void listMenuItems(RestServiceCallback<List<MenuItem>> callback) { }

    public void updateMenuItem(MenuItem menuItem, RestServiceCallback<Long> callback) { }

    public void removeMenuItem(long id, RestServiceCallback<Boolean> callback) { }

    public void listCategories(RestServiceCallback<List<Category>> callback) { }

    public void updateCategory(Category category, RestServiceCallback<Long> callback) { }

    public void removeCategory(long id, RestServiceCallback<Boolean> callback) { }

    public void listSections(RestServiceCallback<List<Section>> callback) { }

    public void updateSection(Section section, RestServiceCallback<Long> callback) { }

    public void removeSection(long id, RestServiceCallback<Boolean> callback) { }

    public void listSeats(RestServiceCallback<List<Seat>> callback) { }

    public void updateSeat(Seat seat, RestServiceCallback<Long> callback) { }

    public void removeSeat(long id, RestServiceCallback<Boolean> callback) { }

    public void listModifiers(RestServiceCallback<List<Modifier>> callback) { }

    public void updateModifier(Modifier modifier, RestServiceCallback<Long> callback) { }

    public void removeModifier(long id, RestServiceCallback<Boolean> callback) { }

    public void listModifierGroups(RestServiceCallback<List<ModifierGroup>> callback) { }

    public void updateModifierGroup(ModifierGroup modifierGroup, RestServiceCallback<Long> callback) { }

    public void removeModifierGroup(long id, RestServiceCallback<Boolean> callback) { }

    public void listCharges(RestServiceCallback<List<Charge>> callback) { }

    public void updateCharge(Charge charge, RestServiceCallback<Long> callback) { }

    public void removeCharge(long id, RestServiceCallback<Boolean> callback) { }

    private <T> void request(int method, OnCondition condition, Runnable request, RestServiceCallback<T> callback) {

        if (condition == OnCondition.SERVICE_UNUSED) {

            if (serviceInUse.compareAndSet(false, true)) {
                if (methodInUse[method].compareAndSet(false, true)) {
                    callback.method = method;
                    request.run();
                } else {
                    serviceInUse.set(false);
                    callback.reject(OnCondition.SERVICE_UNUSED);
                }
            } else {
                callback.reject(OnCondition.SERVICE_UNUSED);
            }

        }

    }

    public abstract class RestServiceCallback<T> implements Callback<T> {

        int method = 0;

        public abstract void success(T t);

        public abstract void failure(ErrorInfo error);

        public abstract void reject(RestServiceProxy.OnCondition condition);

        @Override
        public void success(T t, Response response) {
            success(t);
            methodInUse[method].set(false);
        }

        @Override
        public void failure(RetrofitError error) {

            ErrorInfo info;

            try {
                info = (ErrorInfo) error.getBodyAs(ErrorInfo.class);
            } catch (Exception e) {
                String msg = e.getMessage();
                if (msg == null || msg.isEmpty())


                    info = new ErrorInfo("Unexpected Error", "");

                String msg = e.getMessage();
                if (msg == null || msg.isEmpty())
                    info.setMessage("Please contact server administrator.");
                else
                    info.setMessage(msg);
            }

            failure(info);
            methodInUse[method].set(false);

        }

    }

}
