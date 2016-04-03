package com.sam_chordas.android.stockhawk.rest;

import java.io.Serializable;

/**
 * Created by aabbasal on 3/27/2016.
 */
public class StockDataSet implements Serializable {
    private String symbol;
    private String previousClosePrice;
    private String openPrice;
    private String bidPrice;
    private String daysLowPrice;
    private String daysHighPrice;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPreviousClosePrice() {
        return previousClosePrice;
    }

    public void setPreviousClosePrice(String previousClosePrice) {
        this.previousClosePrice = previousClosePrice;
    }

    public String getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(String openPrice) {
        this.openPrice = openPrice;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String getDaysLowPrice() {
        return daysLowPrice;
    }

    public void setDaysLowPrice(String daysLowPrice) {
        this.daysLowPrice = daysLowPrice;
    }

    public String getDaysHighPrice() {
        return daysHighPrice;
    }

    public void setDaysHighPrice(String daysHighPrice) {
        this.daysHighPrice = daysHighPrice;
    }
}
