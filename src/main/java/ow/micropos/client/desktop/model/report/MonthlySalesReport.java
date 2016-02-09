package ow.micropos.client.desktop.model.report;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class MonthlySalesReport {

    public Date monthOf;

    public List<BigDecimal> dailySales;

    public List<BigDecimal> dailyTax;

    public BigDecimal netSales;

    public BigDecimal netTax;

}