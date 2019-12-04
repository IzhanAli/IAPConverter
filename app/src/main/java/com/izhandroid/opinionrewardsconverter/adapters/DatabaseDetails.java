/*
 * Copyright (c) 2019. Izhan Ali. Accessing/leaking/modifying source codes is prohibited. Legal action will be taken for if you do such things.
 */

package com.izhandroid.opinionrewardsconverter.adapters;

public class DatabaseDetails {


    private String datetime;
    private String status;
    private String productprice;
    private String trno;
    public String orderid;

    private String amtpaid;
    private String date;
    private String trid;
    private String mainstatus;
    private String primarystatus;
    private String secondarystatus;

    public DatabaseDetails() {
        // This is default constructor.
    }

    public String getDatetime() {

        return datetime;
    }

    public void setDatetime(String dateTime) {

        this.datetime = dateTime;
    }

    public String getStatus() {

        return status;
    }

    public void setStatus(String newStatus) {

        this.status = newStatus;
    }
    public String getProductprice() {

        return productprice;
    }

    public void setProductprice(String orgPrice) {

        this.productprice = orgPrice;
    }
    public String getOrderid() {

        return orderid;
    }

    public void setOrderid(String orderIdk) {

        this.orderid = orderIdk;
    }
    public String getTrno() {

        return trno;
    }

    public void setTrno(String trNumber) {

        this.trno = trNumber;
    }
    public String getAmtpaid() {
        return amtpaid;
    }

    public void setAmtpaid(String amtpaid) {
        this.amtpaid = amtpaid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTrid() {
        return trid;
    }

    public void setTrid(String trid) {
        this.trid = trid;
    }

    public String getMainstatus() {
        return mainstatus;
    }

    public void setMainstatus(String mainstatus) {
        this.mainstatus = mainstatus;
    }

    public String getPrimarystatus() {
        return primarystatus;
    }

    public void setPrimarystatus(String primarystatus) {
        this.primarystatus = primarystatus;
    }

    public String getSecondarystatus() {
        return secondarystatus;
    }

    public void setSecondarystatus(String secondarystatus) {
        this.secondarystatus = secondarystatus;
    }
}