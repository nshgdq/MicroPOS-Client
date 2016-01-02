package ow.micropos.client.desktop.model.report;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DaySalesReport {

    private static final BigDecimal ZERO_DOLLARS = new BigDecimal("0.00");

    public Date start, end;

    public Summary closedSummary = new Summary();
    public Summary openSummary = new Summary();
    public Summary voidSummary = new Summary();

}
