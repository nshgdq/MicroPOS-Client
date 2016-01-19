package ow.micropos.client.desktop.service;

import ow.micropos.client.desktop.model.auth.Position;
import ow.micropos.client.desktop.model.employee.Employee;
import ow.micropos.client.desktop.model.enums.SalesOrderStatus;
import ow.micropos.client.desktop.model.enums.SalesOrderType;
import ow.micropos.client.desktop.model.menu.*;
import ow.micropos.client.desktop.model.orders.SalesOrder;
import ow.micropos.client.desktop.model.report.ActiveSalesReport;
import ow.micropos.client.desktop.model.report.DaySalesReport;
import ow.micropos.client.desktop.model.target.Customer;
import ow.micropos.client.desktop.model.target.Seat;
import ow.micropos.client.desktop.model.target.Section;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class RestServiceProxy {

    private final AtomicBoolean[] methodInUse;
    private final RestService service;

    public static RestServiceProxy from(RestService service) {
        return new RestServiceProxy(service);
    }

    public RestServiceProxy(RestService service) {
        this.service = service;
        this.methodInUse = new AtomicBoolean[52];
        for (int i = 0; i < methodInUse.length; i++)
            methodInUse[i] = new AtomicBoolean(false);
    }

    public void getEmployee(String pin, RestServiceCallback<Employee> callback) {
        callback.injectMethodInfo(0, methodInUse);
        if (methodInUse[0].compareAndSet(false, true))
            service.getEmployee(pin, callback);
        else
            callback.reject();
    }

    public void getSections(RestServiceCallback<List<Section>> callback) {
        callback.injectMethodInfo(1, methodInUse);
        if (methodInUse[1].compareAndSet(false, true))
            service.getSections(callback);
        else
            callback.reject();
    }

    public void getSection(long id, RestServiceCallback<Section> callback) {
        callback.injectMethodInfo(2, methodInUse);
        if (methodInUse[2].compareAndSet(false, true))
            service.getSection(id, callback);
        else
            callback.reject();
    }

    public void getCustomers(RestServiceCallback<List<Customer>> callback) {
        callback.injectMethodInfo(3, methodInUse);
        if (methodInUse[3].compareAndSet(false, true))
            service.getCustomers(callback);
        else
            callback.reject();
    }

    public void getCustomer(long id, RestServiceCallback<Customer> callback) {
        callback.injectMethodInfo(4, methodInUse);
        if (methodInUse[4].compareAndSet(false, true))
            service.getCustomer(id, callback);
        else
            callback.reject();
    }

    public void postCustomer(Customer customer, RestServiceCallback<Long> callback) {
        callback.injectMethodInfo(5, methodInUse);
        if (methodInUse[5].compareAndSet(false, true))
            service.postCustomer(customer, callback);
        else
            callback.reject();
    }

    public void getCharges(RestServiceCallback<List<Charge>> callback) {
        callback.injectMethodInfo(6, methodInUse);
        if (methodInUse[6].compareAndSet(false, true))
            service.getCharges(callback);
        else
            callback.reject();
    }

    public void getCategories(RestServiceCallback<List<Category>> callback) {
        callback.injectMethodInfo(7, methodInUse);
        if (methodInUse[7].compareAndSet(false, true))
            service.getCategories(callback);
        else
            callback.reject();
    }

    public void getModifierGroups(RestServiceCallback<List<ModifierGroup>> callback) {
        callback.injectMethodInfo(8, methodInUse);
        if (methodInUse[8].compareAndSet(false, true))
            service.getModifierGroups(callback);
        else
            callback.reject();
    }

    public void getSalesOrders(RestServiceCallback<List<SalesOrder>> callback) {
        callback.injectMethodInfo(9, methodInUse);
        if (methodInUse[9].compareAndSet(false, true))
            service.getSalesOrders(callback);
        else
            callback.reject();
    }

    public void getSalesOrders(SalesOrderStatus status, SalesOrderType type, RestServiceCallback<List<SalesOrder>> callback) {
        callback.injectMethodInfo(10, methodInUse);
        if (methodInUse[10].compareAndSet(false, true))
            service.getSalesOrders(status, type, callback);
        else
            callback.reject();
    }

    public void getSalesOrderBySeat(long id, SalesOrderStatus status, RestServiceCallback<List<SalesOrder>> callback) {
        callback.injectMethodInfo(11, methodInUse);
        if (methodInUse[11].compareAndSet(false, true))
            service.getSalesOrderBySeat(id, status, callback);
        else
            callback.reject();
    }

    public void getSalesOrderByCustomer(long id, SalesOrderStatus status, RestServiceCallback<List<SalesOrder>> callback) {
        callback.injectMethodInfo(12, methodInUse);
        if (methodInUse[12].compareAndSet(false, true))
            service.getSalesOrderByCustomer(id, status, callback);
        else
            callback.reject();
    }

    public void postSalesOrder(SalesOrder salesOrder, RestServiceCallback<Long> callback) {
        callback.injectMethodInfo(13, methodInUse);
        if (methodInUse[13].compareAndSet(false, true))
            service.postSalesOrder(salesOrder, callback);
        else
            callback.reject();
    }

    public void postSalesOrders(List<SalesOrder> salesOrders, RestServiceCallback<List<Long>> callback) {
        callback.injectMethodInfo(14, methodInUse);
        if (methodInUse[14].compareAndSet(false, true))
            service.postSalesOrders(salesOrders, callback);
        else
            callback.reject();
    }

    public void migrateSalesOrders(RestServiceCallback<Integer> callback) {
        callback.injectMethodInfo(15, methodInUse);
        if (methodInUse[15].compareAndSet(false, true))
            service.migrateSalesOrders(callback);
        else
            callback.reject();
    }

    public void closeUnpaidSalesOrders(RestServiceCallback<List<Long>> callback) {
        callback.injectMethodInfo(16, methodInUse);
        if (methodInUse[16].compareAndSet(false, true))
            service.closeUnpaidSalesOrders(callback);
        else
            callback.reject();
    }

    public void getSettings(String[] keys, RestServiceCallback<Map<String, String>> callback) {
        callback.injectMethodInfo(17, methodInUse);
        if (methodInUse[17].compareAndSet(false, true))
            service.getSettings(keys, callback);
        else
            callback.reject();
    }

    public void getCurrentReport(RestServiceCallback<ActiveSalesReport> callback) {
        callback.injectMethodInfo(18, methodInUse);
        if (methodInUse[18].compareAndSet(false, true))
            service.getCurrentReport(callback);
        else
            callback.reject();
    }

    public void getDayReport(int year, int month, int day, RestServiceCallback<DaySalesReport> callback) {
        callback.injectMethodInfo(19, methodInUse);
        if (methodInUse[19].compareAndSet(false, true))
            service.getDayReport(year, month, day, callback);
        else
            callback.reject();
    }

    public void listPositions(RestServiceCallback<List<Position>> callback) {
        callback.injectMethodInfo(20, methodInUse);
        if (methodInUse[20].compareAndSet(false, true))
            service.listPositions(callback);
        else
            callback.reject();
    }

    public void updatePosition(Position position, RestServiceCallback<Long> callback) {
        callback.injectMethodInfo(21, methodInUse);
        if (methodInUse[21].compareAndSet(false, true))
            service.updatePosition(position, callback);
        else
            callback.reject();
    }

    public void removePosition(long id, RestServiceCallback<Boolean> callback) {
        callback.injectMethodInfo(22, methodInUse);
        if (methodInUse[22].compareAndSet(false, true))
            service.removePosition(id, callback);
        else
            callback.reject();
    }

    public void listEmployees(RestServiceCallback<List<Employee>> callback) {
        callback.injectMethodInfo(23, methodInUse);
        if (methodInUse[23].compareAndSet(false, true))
            service.listEmployees(callback);
        else
            callback.reject();
    }

    public void updateEmployee(Employee employee, RestServiceCallback<Long> callback) {
        callback.injectMethodInfo(24, methodInUse);
        if (methodInUse[24].compareAndSet(false, true))
            service.updateEmployee(employee, callback);
        else
            callback.reject();
    }

    public void removeEmployee(long id, RestServiceCallback<Boolean> callback) {
        callback.injectMethodInfo(25, methodInUse);
        if (methodInUse[25].compareAndSet(false, true))
            service.removeEmployee(id, callback);
        else
            callback.reject();
    }

    public void listSalesOrders(RestServiceCallback<List<SalesOrder>> callback) {
        callback.injectMethodInfo(26, methodInUse);
        if (methodInUse[26].compareAndSet(false, true))
            service.listSalesOrders(callback);
        else
            callback.reject();
    }

    public void removeSalesOrder(long id, RestServiceCallback<Boolean> callback) {
        callback.injectMethodInfo(27, methodInUse);
        if (methodInUse[27].compareAndSet(false, true))
            service.removeSalesOrder(id, callback);
        else
            callback.reject();
    }

    public void listCustomers(RestServiceCallback<List<Customer>> callback) {
        callback.injectMethodInfo(28, methodInUse);
        if (methodInUse[28].compareAndSet(false, true))
            service.listCustomers(callback);
        else
            callback.reject();
    }

    public void updateCustomer(Customer customer, RestServiceCallback<Long> callback) {
        callback.injectMethodInfo(29, methodInUse);
        if (methodInUse[29].compareAndSet(false, true))
            service.updateCustomer(customer, callback);
        else
            callback.reject();
    }

    public void removeCustomer(long id, RestServiceCallback<Boolean> callback) {
        callback.injectMethodInfo(30, methodInUse);
        if (methodInUse[30].compareAndSet(false, true))
            service.removeCustomer(id, callback);
        else
            callback.reject();
    }

    public void listMenuItems(RestServiceCallback<List<MenuItem>> callback) {
        callback.injectMethodInfo(31, methodInUse);
        if (methodInUse[31].compareAndSet(false, true))
            service.listMenuItems(callback);
        else
            callback.reject();
    }

    public void updateMenuItem(MenuItem menuItem, RestServiceCallback<Long> callback) {
        callback.injectMethodInfo(32, methodInUse);
        if (methodInUse[32].compareAndSet(false, true))
            service.updateMenuItem(menuItem, callback);
        else
            callback.reject();
    }

    public void removeMenuItem(long id, RestServiceCallback<Boolean> callback) {
        callback.injectMethodInfo(33, methodInUse);
        if (methodInUse[33].compareAndSet(false, true))
            service.removeMenuItem(id, callback);
        else
            callback.reject();
    }

    public void listCategories(RestServiceCallback<List<Category>> callback) {
        callback.injectMethodInfo(34, methodInUse);
        if (methodInUse[34].compareAndSet(false, true))
            service.listCategories(callback);
        else
            callback.reject();
    }

    public void updateCategory(Category category, RestServiceCallback<Long> callback) {
        callback.injectMethodInfo(35, methodInUse);
        if (methodInUse[35].compareAndSet(false, true))
            service.updateCategory(category, callback);
        else
            callback.reject();
    }

    public void removeCategory(long id, RestServiceCallback<Boolean> callback) {
        callback.injectMethodInfo(36, methodInUse);
        if (methodInUse[36].compareAndSet(false, true))
            service.removeCategory(id, callback);
        else
            callback.reject();
    }

    public void listSections(RestServiceCallback<List<Section>> callback) {
        callback.injectMethodInfo(37, methodInUse);
        if (methodInUse[37].compareAndSet(false, true))
            service.listSections(callback);
        else
            callback.reject();
    }

    public void updateSection(Section section, RestServiceCallback<Long> callback) {
        callback.injectMethodInfo(38, methodInUse);
        if (methodInUse[38].compareAndSet(false, true))
            service.updateSection(section, callback);
        else
            callback.reject();
    }

    public void removeSection(long id, RestServiceCallback<Boolean> callback) {
        callback.injectMethodInfo(39, methodInUse);
        if (methodInUse[39].compareAndSet(false, true))
            service.removeSection(id, callback);
        else
            callback.reject();
    }

    public void listSeats(RestServiceCallback<List<Seat>> callback) {
        callback.injectMethodInfo(40, methodInUse);
        if (methodInUse[40].compareAndSet(false, true))
            service.listSeats(callback);
        else
            callback.reject();
    }

    public void updateSeat(Seat seat, RestServiceCallback<Long> callback) {
        callback.injectMethodInfo(41, methodInUse);
        if (methodInUse[41].compareAndSet(false, true))
            service.updateSeat(seat, callback);
        else
            callback.reject();
    }

    public void removeSeat(long id, RestServiceCallback<Boolean> callback) {
        callback.injectMethodInfo(42, methodInUse);
        if (methodInUse[42].compareAndSet(false, true))
            service.removeSeat(id, callback);
        else
            callback.reject();
    }

    public void listModifiers(RestServiceCallback<List<Modifier>> callback) {
        callback.injectMethodInfo(43, methodInUse);
        if (methodInUse[43].compareAndSet(false, true))
            service.listModifiers(callback);
        else
            callback.reject();
    }

    public void updateModifier(Modifier modifier, RestServiceCallback<Long> callback) {
        callback.injectMethodInfo(44, methodInUse);
        if (methodInUse[44].compareAndSet(false, true))
            service.updateModifier(modifier, callback);
        else
            callback.reject();
    }

    public void removeModifier(long id, RestServiceCallback<Boolean> callback) {
        callback.injectMethodInfo(45, methodInUse);
        if (methodInUse[45].compareAndSet(false, true))
            service.removeModifier(id, callback);
        else
            callback.reject();
    }

    public void listModifierGroups(RestServiceCallback<List<ModifierGroup>> callback) {
        callback.injectMethodInfo(46, methodInUse);
        if (methodInUse[46].compareAndSet(false, true))
            service.listModifierGroups(callback);
        else
            callback.reject();
    }

    public void updateModifierGroup(ModifierGroup modifierGroup, RestServiceCallback<Long> callback) {
        callback.injectMethodInfo(47, methodInUse);
        if (methodInUse[47].compareAndSet(false, true))
            service.updateModifierGroup(modifierGroup, callback);
        else
            callback.reject();
    }

    public void removeModifierGroup(long id, RestServiceCallback<Boolean> callback) {
        callback.injectMethodInfo(48, methodInUse);
        if (methodInUse[48].compareAndSet(false, true))
            service.removeModifierGroup(id, callback);
        else
            callback.reject();
    }

    public void listCharges(RestServiceCallback<List<Charge>> callback) {
        callback.injectMethodInfo(49, methodInUse);
        if (methodInUse[49].compareAndSet(false, true))
            service.listCharges(callback);
        else
            callback.reject();
    }

    public void updateCharge(Charge charge, RestServiceCallback<Long> callback) {
        callback.injectMethodInfo(50, methodInUse);
        if (methodInUse[50].compareAndSet(false, true))
            service.updateCharge(charge, callback);
        else
            callback.reject();
    }

    public void removeCharge(long id, RestServiceCallback<Boolean> callback) {
        callback.injectMethodInfo(51, methodInUse);
        if (methodInUse[51].compareAndSet(false, true))
            service.removeCharge(id, callback);
        else
            callback.reject();
    }

}
