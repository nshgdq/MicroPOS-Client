package ow.micropos.client.desktop.model.report;


import ow.micropos.client.desktop.model.enums.SalesOrderStatus;
import ow.micropos.client.desktop.model.enums.SalesOrderType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

public class SalesReport {

    private static final BigDecimal ZERO_DOLLARS = new BigDecimal("0.00");

    public SalesOrderStatus status;
    public SalesOrderType type;
    public Date start;
    public Date end;

    public int orderCount = 0;
    public int dineInCount = 0;
    public int takeOutCount = 0;

    public BigDecimal total = ZERO_DOLLARS;

    public BigDecimal paymentTotal = ZERO_DOLLARS;
    public BigDecimal cashTotal = ZERO_DOLLARS;
    public BigDecimal creditTotal = ZERO_DOLLARS;
    public BigDecimal checkTotal = ZERO_DOLLARS;
    public BigDecimal giftcardTotal = ZERO_DOLLARS;

    public BigDecimal taxTotal = ZERO_DOLLARS;
    public BigDecimal chargeTotal = ZERO_DOLLARS;

    public BigDecimal taxedSalesTotal = ZERO_DOLLARS;
    public BigDecimal untaxedSalesTotal = ZERO_DOLLARS;
    public BigDecimal salesTotal = ZERO_DOLLARS;

    public HashMap<String, BigDecimal> categorySalesTotals = new HashMap<>();

}
