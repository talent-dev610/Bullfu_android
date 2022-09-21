package com.bullhu.android.model;

public class PaymentModel {

    String authorization_url = "";
    String reference = "";

    public String getAuthorization_url() {
        return authorization_url;
    }

    public void setAuthorization_url(String authorization_url) {
        this.authorization_url = authorization_url;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
