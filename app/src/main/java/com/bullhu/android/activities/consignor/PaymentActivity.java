package com.bullhu.android.activities.consignor;

import static com.bullhu.android.utils.Util.getIntFromDouble;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.bullhu.android.R;
import com.bullhu.android.activities.BaseActivity;
import com.bullhu.android.common.Commons;
import com.bullhu.android.common.Constants;
import com.bullhu.android.model.PaymentModel;
import com.bullhu.android.network.ApiManager;
import com.bullhu.android.network.ICallback;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;

import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class PaymentActivity extends BaseActivity {

    @BindView(R.id.edit_card_number)
    TextInputEditText edit_card_number;

    @BindView(R.id.edt_expiry)
    TextInputEditText edt_expiry;
    @BindView(R.id.edt_ccv)
    TextInputEditText edt_cvv;

    @BindView(R.id.btn_pay_now)
    Button btn_pay_now;

    String cardNumber = "5060666666666666666";
    String str_expiry = "";
    int expiryMonth = 7; //any month in the future
    int expiryYear = 23; // any year in the future. '2018' would work also!
    String cvv = "123";  // cvv of the test card
    Card card;
    double amount = 0.0;
    String delivery_id = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ButterKnife.bind(this);
        amount = Integer.parseInt(getIntent().getStringExtra(Constants.PRICE));
        delivery_id = getIntent().getStringExtra(Constants.DELIVERY_ID);
        loadLayout();
    }

    private void loadLayout(){

        //initialize sdk
        PaystackSdk.initialize(getApplicationContext());
        btn_pay_now.setText("Pay" + " "+ "₦" + " " + amount);
    }

    public void performCharge(){

        //create a Charge object
        Charge charge = new Charge();
        charge.setAmount(getIntFromDouble(amount * 100));
        charge.setEmail(Commons.g_user.getEmail());
        //charge.setReference("ChargedFromAndroid_" + Calendar.getInstance().getTimeInMillis());
        try {
            charge.putCustomField("Charged From", "Android SDK");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        charge.setCard(card); //sets the card to charge

        showHUD();

        PaystackSdk.chargeCard(PaymentActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {

                hideHUD();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processVerifyPayment(transaction.getReference());
                    }
                });

                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                hideHUD();
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
                Log.d("beforeValidate==>", transaction.getReference());
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                hideHUD();
                //handle error here
                Log.d("ErrorTransaction==>", error.getLocalizedMessage());
            }

        });
    }

    @OnClick(R.id.btn_pay_now) void payNow(){


        cardNumber = Objects.requireNonNull(edit_card_number.getText()).toString();
        str_expiry = Objects.requireNonNull(edt_expiry.getText()).toString();

        if (str_expiry.isEmpty())
            str_expiry = "/";

        try {
            String[] cardExpiryArray = str_expiry.split("/");
            expiryMonth = Integer.parseInt(cardExpiryArray[0]);
            expiryYear = Integer.parseInt(cardExpiryArray[1]);

        }catch (Exception exception){
            Log.d("Error==>", exception.getMessage());
        }

        cvv = Objects.requireNonNull(edt_cvv.getText()).toString();

        card = new Card(cardNumber, expiryMonth, expiryYear, cvv);

        if (card.isValid()) {
            performCharge();
        } else {
            //do something
            showToast("Wrong card number");
        }
    }

    private void processVerifyPayment(String reference){
        showHUD();

        ApiManager.verifyPayment(reference, delivery_id, new ICallback() {
            @Override
            public void onCompletion(RESULT result, Object resultParam) {

                hideHUD();
                switch (result){

                    case SUCCESS:
                        showToast((String) resultParam);
                        finish();
                        break;
                    case FAILURE:
                        if (resultParam != null)
                            showToast((String) resultParam);
                        else
                            showToast("Something went wrong.");
                        break;
                }
            }
        });
    }

    private boolean checkPrice(){

        if (edit_card_number.getText().length() == 0){
            showToast("Please input value");
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}