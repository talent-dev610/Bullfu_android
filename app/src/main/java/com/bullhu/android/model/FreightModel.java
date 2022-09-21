package com.bullhu.android.model;

public class FreightModel {

    String id = "";
    String size = "";
    String weight = "";
    String pickup_time = "";
    String pickup_location = "";
    String dropoff_location = "";
    String return_location = "";
    boolean tdo_ready = false;
    String consignor_id = "";
    String delivery_price = "";
    int accept_status = 0; //1 created 2 sent request from driver 3 accepted
    String consignor_name = "";
    String driver_name = "";
    String driver_photo = "";

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPickup_time() {
        return pickup_time;
    }

    public void setPickup_time(String pickup_time) {
        this.pickup_time = pickup_time;
    }

    public String getPickup_location() {
        return pickup_location;
    }

    public void setPickup_location(String pickup_location) {
        this.pickup_location = pickup_location;
    }

    public String getDropoff_location() {
        return dropoff_location;
    }

    public void setDropoff_location(String dropoff_location) {
        this.dropoff_location = dropoff_location;
    }

    public String getReturn_location() {
        return return_location;
    }

    public void setReturn_location(String return_location) {
        this.return_location = return_location;
    }

    public boolean isTdo_ready() {
        return tdo_ready;
    }

    public void setTdo_ready(boolean tdo_ready) {
        this.tdo_ready = tdo_ready;
    }

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


    public int getAccept_status() {
        return accept_status;
    }

    public void setAccept_status(int accept_status) {
        this.accept_status = accept_status;
    }

    public String getDelivery_price() {
        return delivery_price;
    }

    public void setDelivery_price(String delivery_price) {
        this.delivery_price = delivery_price;
    }

    public String getConsignor_name() {
        return consignor_name;
    }

    public void setConsignor_name(String consignor_name) {
        this.consignor_name = consignor_name;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getDriver_photo() {
        return driver_photo;
    }

    public void setDriver_photo(String driver_photo) {
        this.driver_photo = driver_photo;
    }
}
