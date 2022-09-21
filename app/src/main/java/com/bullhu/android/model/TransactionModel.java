package com.bullhu.android.model;

public class TransactionModel {

    String id = "";
    String consignor_id = "";
    String driver_id = "";
    String reference = "";
    String paidAt = "";
    String amount = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConsignor_id() {
        return consignor_id;
    }

    public void setConsignor_id(String consignor_id) {
        this.consignor_id = consignor_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(String paidAt) {
        this.paidAt = paidAt;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
