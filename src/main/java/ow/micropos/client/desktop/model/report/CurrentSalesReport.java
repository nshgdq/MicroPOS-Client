package ow.micropos.client.desktop.model.report;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class CurrentSalesReport {

    public int orderCount = 0;
    public int openCount = 0;
    public int closedCount = 0;
    public int voidCount = 0;

    public int dineInCount = 0;
    public int takeOutCount = 0;

    public int productCount = 0;
    public int productVoidCount = 0;

    public BigDecimal productTotal = BigDecimal.ZERO;
    public List<CategoryReport> categoryReports = new ArrayList<>();

    public BigDecimal chargeTotal = BigDecimal.ZERO;
    public int chargeCount = 0;
    public List<ChargeReport> chargeReports = new ArrayList<>();

    public BigDecimal subTotal = BigDecimal.ZERO;
    public BigDecimal taxTotal = BigDecimal.ZERO;
    public BigDecimal gratuityTotal = BigDecimal.ZERO;
    public BigDecimal grandTotal = BigDecimal.ZERO;

    public BigDecimal paymentTotal = BigDecimal.ZERO;
    public int paymentCount = 0;
    public List<PaymentReport> paymentReports = new ArrayList<>();

    public BigDecimal changeTotal = BigDecimal.ZERO;

    public BigDecimal netSales = BigDecimal.ZERO;

    @Builder
    public static class CategoryReport {
        public final String categoryName;
        public final BigDecimal categoryTotal;
        public final Integer categoryCount;
    }

    @Builder
    public static class ChargeReport {
        public final String chargeName;
        public final BigDecimal chargeTotal;
        public final Integer chargeCount;
    }

    @Builder
    public static class PaymentReport {
        public final String paymentName;
        public final BigDecimal paymentTotal;
        public final Integer paymentCount;
    }

}
