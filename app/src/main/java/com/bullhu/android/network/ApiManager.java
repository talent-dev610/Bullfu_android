package com.bullhu.android.network;


import static com.bullhu.android.common.Commons.ACCESS_TOKEN;
import static com.bullhu.android.common.Commons.g_user;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bullhu.android.common.Commons;
import com.bullhu.android.model.FreightModel;
import com.bullhu.android.model.PaymentModel;
import com.bullhu.android.model.TransactionModel;
import com.bullhu.android.model.UserModel;
import com.bullhu.android.utils.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ApiManager {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType JPEG = MediaType.parse("image/jpeg");
    public static final MediaType PNG = MediaType.parse("image/png");
    public static final MediaType VIDEO = MediaType.parse("video/mp4");

    private static final String HOST = "http://44.198.197.47:3000/";
    public static final String API = HOST + "api/";
    public static final String SIGNUP = "signup";
    public static final String PROFILE = "profile/";
    public static final String UPLOAD_AVATAR = "upload-avatar";
    public static final String SIGNIN = "signin";
    public static final String FORGOT_PASSWORD = "forgot-password";
    public static final String CHECK_CONFIRM_CODE = "check-confirm-code";
    public static final String RESET_PASSWORD = "reset-password";
    public static final String PROFILE_UPDATE = "update";
    public static final String DELIVERY = "delivery/";
    public static final String REQUEST = "request";
    public static final String ACCEPT = "accept";
    public static final String ALL_FOR_DRIVERS = "all-for-drivers";
    public static final String ALL_FOR_CONSIGNOR = "all-for-consignor";
    public static final String PAY = "pay/";
    public static final String INITIALIZE_PAYMENT = "initialize-payment";
    public static final String VERIFY_PAYMENT = "verify";
    public static final String ALERTS = "alerts/";
    public static final String UPLOAD_TOKEN = "upload-token";
    public static final String REFERENCE = "reference=";
    public static final String ALL_TRANSACTION = "all-transaction";

    public static void signUp(String name, String email, String password, String country_code, String phone, int type, final ICallback callback) {

        RequestBody body = new FormBody.Builder()
                .add(PARAMS.NAME, name)
                .add(PARAMS.EMAIL, email)
                .add(PARAMS.PASSWORD, password)
                .add(PARAMS.COUNTRY, country_code)
                .add(PARAMS.PHONE, phone)
                .add(PARAMS.CATEGORY, String.valueOf(type))
                .build();

        WebManager.call(SIGNUP,  WebManager.METHOD.POST, "", body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onCompletion(ICallback.RESULT.FAILURE, null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {

                    JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();
                    boolean resultStatus = json.get(PARAMS.SUCCESS).getAsBoolean();
                    Log.d("SignUp", json.toString());
                    if (resultStatus){


                        JsonObject jsonData = JsonUtils.getJsonObject(json.get(PARAMS.DATA));

                        JsonObject jsonUser = JsonUtils.getJsonObject(jsonData.get(PARAMS.USER));
                        UserModel user = new UserModel();

                        user.setId(jsonUser.get(PARAMS.ID).getAsString());
                        user.setName(jsonUser.get(PARAMS.NAME).getAsString());
                        user.setPhone_country_code(jsonUser.get(PARAMS.PHONE_COUNTRY_CODE).getAsString());
                        user.setPhone(jsonUser.get(PARAMS.PHONE).getAsString());
                        user.setUser_type(jsonUser.get(PARAMS.CATEGORY).getAsInt());

                        Commons.g_user = user;

                        ACCESS_TOKEN = jsonData.get(PARAMS.TOKEN).getAsString();

                        callback.onCompletion(ICallback.RESULT.SUCCESS, jsonData.get(PARAMS.TOKEN).getAsString());
                    }
                    else {
                        String message = json.get(PARAMS.MESSAGE).getAsString();
                        callback.onCompletion(ICallback.RESULT.FAILURE, message);
                    }

                }
                catch (Exception e){
                    callback.onCompletion(ICallback.RESULT.FAILURE, null);
                }
            }
        });

    }

    public static void uploadPhoto(File image, String accessToken, final ICallback callback){

        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(PARAMS.IMAGE, image.getName(), RequestBody.create(image, MediaType.parse("image/*")))
                .build();

        WebManager.call(PROFILE + UPLOAD_AVATAR, WebManager.METHOD.POST, accessToken, body, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onCompletion(ICallback.RESULT.FAILURE, null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                try {
                    JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();

                    boolean success = json.get(PARAMS.SUCCESS).getAsBoolean();

                    if (success){

                        JsonObject jsonData = JsonUtils.getJsonObject(json.get(PARAMS.DATA));
                        JsonObject jsonUser = JsonUtils.getJsonObject(jsonData.get(PARAMS.USER));

                        g_user.setPhoto(jsonUser.get(PARAMS.PHOTO).getAsString());

                        callback.onCompletion(ICallback.RESULT.SUCCESS, jsonUser.get(PARAMS.PHOTO).getAsString());
                    }
                    else {
                        String message = json.get(PARAMS.MESSAGE).getAsString();
                        callback.onCompletion(ICallback.RESULT.FAILURE, message);
                    }
                }
                catch (Exception e){
                    callback.onCompletion(ICallback.RESULT.FAILURE, null);
                }
            }
        });
    }


    public static void login(String email, String password, final ICallback callback){

        RequestBody body = new FormBody.Builder()
                .add(PARAMS.EMAIL, email)
                .add(PARAMS.PASSWORD, password)
                .build();

        WebManager.call(SIGNIN, WebManager.METHOD.POST_AUTH, "", body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onCompletion(ICallback.RESULT.FAILURE, null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {


                    JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();
                    Log.d("LOGIN==>", json.toString());
                    boolean resultStatus = JsonUtils.getBoolean(json.get(PARAMS.SUCCESS));

                    if (resultStatus){

                        JsonObject jsonData = JsonUtils.getJsonObject(json.get(PARAMS.DATA));
                        JsonObject jsonUser = JsonUtils.getJsonObject(jsonData.get(PARAMS.USER));

                        UserModel user = new UserModel();

                        user.setId(jsonUser.get(PARAMS.ID).getAsString());
                        user.setName(jsonUser.get(PARAMS.NAME).getAsString());
                        user.setEmail(jsonUser.get(PARAMS.EMAIL).getAsString());
                        user.setPhone_country_code(jsonUser.get(PARAMS.PHONE_COUNTRY_CODE).getAsString());
                        user.setPhone(jsonUser.get(PARAMS.PHONE).getAsString());
                        user.setUser_type(jsonUser.get(PARAMS.CATEGORY).getAsInt());
                        user.setPhoto(jsonUser.get(PARAMS.PHOTO).getAsString());

                        Commons.ACCESS_TOKEN = jsonData.get(PARAMS.TOKEN).getAsString();

                        g_user = user;

                        callback.onCompletion(ICallback.RESULT.SUCCESS, true);
                    }
                    else {

                        String message = json.get(PARAMS.MESSAGE).getAsString();
                        callback.onCompletion(ICallback.RESULT.FAILURE, message);
                    }
                } catch (Exception e) {
                    callback.onCompletion(ICallback.RESULT.FAILURE, null);
                }
            }
        });
    }

    public static void forgotPassword(String email, final ICallback callback){

        RequestBody body = new FormBody.Builder()
                .add(PARAMS.EMAIL, email)
                .build();

        WebManager.call(FORGOT_PASSWORD, WebManager.METHOD.POST_AUTH, "", body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onCompletion(ICallback.RESULT.FAILURE, null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {

                    JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();
                    boolean resultStatus = JsonUtils.getBoolean(json.get(PARAMS.SUCCESS));

                    if (resultStatus){

                        callback.onCompletion(ICallback.RESULT.SUCCESS, null);
                    }
                    else {

                        String message = json.get(PARAMS.MESSAGE).getAsString();
                        callback.onCompletion(ICallback.RESULT.FAILURE, message);
                    }
                } catch (Exception e) {
                    callback.onCompletion(ICallback.RESULT.FAILURE, null);
                }
            }
        });
    }

    public static void checkConfirmCode(String email, String reset_code, final ICallback callback){

        RequestBody body = new FormBody.Builder()
                .add(PARAMS.EMAIL, email)
                .add(PARAMS.RESET_CODE, reset_code)
                .build();

        WebManager.call(CHECK_CONFIRM_CODE, WebManager.METHOD.POST_AUTH, "", body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onCompletion(ICallback.RESULT.FAILURE, null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {

                    JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();
                    boolean resultStatus = JsonUtils.getBoolean(json.get(PARAMS.SUCCESS));

                    if (resultStatus){

                        JsonObject jsonData = JsonUtils.getJsonObject(json.get(PARAMS.DATA));

                        callback.onCompletion(ICallback.RESULT.SUCCESS, null);
                    }
                    else {

                        String message = json.get(PARAMS.MESSAGE).getAsString();
                        callback.onCompletion(ICallback.RESULT.FAILURE, message);
                    }
                } catch (Exception e) {
                    callback.onCompletion(ICallback.RESULT.FAILURE, null);
                }
            }
        });
    }

    public static void resetPassword(String email, String password, final ICallback callback){

        RequestBody body = new FormBody.Builder()
                .add(PARAMS.EMAIL, email)
                .add(PARAMS.PASSWORD, password)
                .build();

        WebManager.call(RESET_PASSWORD, WebManager.METHOD.POST_AUTH, "", body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onCompletion(ICallback.RESULT.FAILURE, null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                try {


                    JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();
                    boolean resultStatus = JsonUtils.getBoolean(json.get(PARAMS.SUCCESS));

                    if (resultStatus){

                        JsonObject jsonData = JsonUtils.getJsonObject(json.get(PARAMS.DATA));
                        JsonObject jsonUser = JsonUtils.getJsonObject(jsonData.get(PARAMS.USER));

                        UserModel user = new UserModel();

                        user.setId(jsonUser.get(PARAMS.ID).getAsString());
                        user.setName(jsonUser.get(PARAMS.NAME).getAsString());
                        user.setEmail(jsonUser.get(PARAMS.EMAIL).getAsString());
                        user.setPhone_country_code(jsonUser.get(PARAMS.PHONE_COUNTRY_CODE).getAsString());
                        user.setPhone(jsonUser.get(PARAMS.PHONE).getAsString());
                        user.setUser_type(jsonUser.get(PARAMS.CATEGORY).getAsInt());
                        user.setPhoto(jsonUser.get(PARAMS.PHOTO).getAsString());

                        Commons.ACCESS_TOKEN = jsonData.get(PARAMS.TOKEN).getAsString();

                        g_user = user;

                        callback.onCompletion(ICallback.RESULT.SUCCESS, true);
                    }
                    else {

                        String message = json.get(PARAMS.MESSAGE).getAsString();
                        callback.onCompletion(ICallback.RESULT.FAILURE, message);
                    }
                } catch (Exception e) {
                    callback.onCompletion(ICallback.RESULT.FAILURE, null);
                }
            }
        });
    }

    public static void profileUpdate(String name, String country_code, String phone, String accessToken, final ICallback callback){

        RequestBody body = new FormBody.Builder()
                .add(PARAMS.NAME, name)
                .add(PARAMS.COUNTRY_CODE, country_code)
                .add(PARAMS.PHONE, phone)
                .build();

        WebManager.call(PROFILE + PROFILE_UPDATE, WebManager.METHOD.POST, accessToken, body, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onCompletion(ICallback.RESULT.FAILURE, null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();

                boolean resultStatus = JsonUtils.getBoolean(json.get(PARAMS.SUCCESS));

                if (resultStatus){

                    JsonObject jsonData = JsonUtils.getJsonObject(json.get(PARAMS.DATA));
                    JsonObject jsonUser = JsonUtils.getJsonObject(jsonData.get(PARAMS.USER));

                    UserModel user = new UserModel();

                    user.setId(jsonUser.get(PARAMS.ID).getAsString());
                    user.setName(jsonUser.get(PARAMS.NAME).getAsString());
                    user.setEmail(jsonUser.get(PARAMS.EMAIL).getAsString());
                    user.setPhone_country_code(jsonUser.get(PARAMS.PHONE_COUNTRY_CODE).getAsString());
                    user.setPhone(jsonUser.get(PARAMS.PHONE).getAsString());
                    user.setUser_type(jsonUser.get(PARAMS.CATEGORY).getAsInt());
                    user.setPhoto(jsonUser.get(PARAMS.PHOTO).getAsString());

                    g_user = user;

                    callback.onCompletion(ICallback.RESULT.SUCCESS, true);
                }
                else {
                    String message = json.get(PARAMS.MESSAGE).getAsString();
                    callback.onCompletion(ICallback.RESULT.FAILURE, message);
                }
            }
        });
    }

    public static void deliveryRequest(String pick_up_location, String drop_off_location, String return_location, String size, String weight
            , String pick_up_time, boolean tdo_ready, String accessToken, final ICallback callback){

        RequestBody body = new FormBody.Builder()
                .add(PARAMS.PICK_UP_LOCATION, pick_up_location)
                .add(PARAMS.DROP_OFF_LOCATION, drop_off_location)
                .add(PARAMS.EMPTY_RETURN_LOCATION, return_location)
                .add(PARAMS.CONTAINER_SIZE, size)
                .add(PARAMS.CONTAINER_WEIGHT, weight)
                .add(PARAMS.PICK_UP_TIME, pick_up_time)
                .add(PARAMS.TDO_READY, String.valueOf(tdo_ready))
                .build();

        WebManager.call(DELIVERY + REQUEST, WebManager.METHOD.POST, accessToken, body, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onCompletion(ICallback.RESULT.FAILURE, null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();

                boolean resultStatus = JsonUtils.getBoolean(json.get(PARAMS.SUCCESS));

                if (resultStatus){

                    JsonObject jsonData = JsonUtils.getJsonObject(json.get(PARAMS.DATA));


                    callback.onCompletion(ICallback.RESULT.SUCCESS, true);
                }
                else {
                    String message = json.get(PARAMS.MESSAGE).getAsString();
                    callback.onCompletion(ICallback.RESULT.FAILURE, message);
                }
            }
        });
    }

    public static void deliveryAccept(String delivery_id, String price, String accessToken, final ICallback callback){

        RequestBody body = new FormBody.Builder()
                .add(PARAMS.DELIVERY_ID, delivery_id)
                .add(PARAMS.PRICE, price)
                .build();

        WebManager.call(DELIVERY + ACCEPT, WebManager.METHOD.POST, accessToken, body, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onCompletion(ICallback.RESULT.FAILURE, null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();

                boolean resultStatus = JsonUtils.getBoolean(json.get(PARAMS.SUCCESS));

                if (resultStatus){

                    JsonObject jsonData = JsonUtils.getJsonObject(json.get(PARAMS.DATA));


                    callback.onCompletion(ICallback.RESULT.SUCCESS, true);
                }
                else {
                    String message = json.get(PARAMS.MESSAGE).getAsString();
                    callback.onCompletion(ICallback.RESULT.FAILURE, message);
                }
            }
        });
    }

    public static void getDeliveryAllForDrivers(String accessToken, final ICallback callback){

        WebManager.call(DELIVERY + ALL_FOR_DRIVERS, WebManager.METHOD.GET, accessToken, null, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onCompletion(ICallback.RESULT.FAILURE, null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();

                boolean resultStatus = JsonUtils.getBoolean(json.get(PARAMS.SUCCESS));

                if (resultStatus){

                    JsonArray jsonData = JsonUtils.getJsonArray(json.get(PARAMS.DATA));
                    ArrayList<FreightModel> allDelivery = new ArrayList<>();

                    for (int i = 0; i <  jsonData.size(); i++){

                        JsonObject jsonDelivery = jsonData.get(i).getAsJsonObject();

                        FreightModel freight = new FreightModel();

                        freight.setId(jsonDelivery.get(PARAMS.ID).getAsString());
                        freight.setPickup_location(jsonDelivery.get(PARAMS.PICK_UP_LOCATION).getAsString());
                        freight.setDropoff_location(jsonDelivery.get(PARAMS.DROP_OFF_LOCATION).getAsString());
                        freight.setReturn_location(jsonDelivery.get(PARAMS.EMPTY_RETURN_LOCATION).getAsString());
                        freight.setSize(jsonDelivery.get(PARAMS.CONTAINER_SIZE).getAsString());
                        freight.setWeight(jsonDelivery.get(PARAMS.CONTAINER_WEIGHT).getAsString());
                        freight.setPickup_time(jsonDelivery.get(PARAMS.PICK_UP_TIME).getAsString());
                        freight.setTdo_ready(jsonDelivery.get(PARAMS.TDO_READY).getAsBoolean());
                        freight.setAccept_status(jsonDelivery.get(PARAMS.ACCEPT_STATUS).getAsInt());
                        freight.setDelivery_price(jsonDelivery.get(PARAMS.DELIVERY_PRICE).getAsString());

                        JsonObject consignor = jsonDelivery.get(PARAMS.CONSIGNOR_ID).getAsJsonObject();
                        freight.setConsignor_name(consignor.get(PARAMS.NAME).getAsString());

                        allDelivery.add(freight);
                    }

                    callback.onCompletion(ICallback.RESULT.SUCCESS, allDelivery);
                }
                else {
                    String message = json.get(PARAMS.MESSAGE).getAsString();
                    callback.onCompletion(ICallback.RESULT.FAILURE, message);
                }
            }
        });
    }

    public static void getDeliveryAllForConsignor(String accessToken, final ICallback callback){

        WebManager.call(DELIVERY + ALL_FOR_CONSIGNOR, WebManager.METHOD.GET, accessToken, null, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onCompletion(ICallback.RESULT.FAILURE, null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();

                boolean resultStatus = JsonUtils.getBoolean(json.get(PARAMS.SUCCESS));

                if (resultStatus){

                    JsonArray jsonData = JsonUtils.getJsonArray(json.get(PARAMS.DATA));
                    ArrayList<FreightModel> allDelivery = new ArrayList<>();

                    for (int i = 0; i <  jsonData.size(); i++){

                        JsonObject jsonDelivery = jsonData.get(i).getAsJsonObject();

                        FreightModel freight = new FreightModel();

                        freight.setId(jsonDelivery.get(PARAMS.ID).getAsString());
                        freight.setPickup_location(jsonDelivery.get(PARAMS.PICK_UP_LOCATION).getAsString());
                        freight.setDropoff_location(jsonDelivery.get(PARAMS.DROP_OFF_LOCATION).getAsString());
                        freight.setReturn_location(jsonDelivery.get(PARAMS.EMPTY_RETURN_LOCATION).getAsString());
                        freight.setSize(jsonDelivery.get(PARAMS.CONTAINER_SIZE).getAsString());
                        freight.setWeight(jsonDelivery.get(PARAMS.CONTAINER_WEIGHT).getAsString());
                        freight.setPickup_time(jsonDelivery.get(PARAMS.PICK_UP_TIME).getAsString());
                        freight.setTdo_ready(jsonDelivery.get(PARAMS.TDO_READY).getAsBoolean());
                        freight.setAccept_status(jsonDelivery.get(PARAMS.ACCEPT_STATUS).getAsInt());
                        freight.setDelivery_price(jsonDelivery.get(PARAMS.DELIVERY_PRICE).getAsString());

                        JsonObject driver = jsonDelivery.get(PARAMS.DRIVER_ID).getAsJsonObject();

                        freight.setDriver_name(driver.get(PARAMS.NAME).getAsString());
                        freight.setDriver_photo(driver.get(PARAMS.PHOTO).getAsString());

                        allDelivery.add(freight);
                    }

                    callback.onCompletion(ICallback.RESULT.SUCCESS, allDelivery);
                }
                else {
                    String message = json.get(PARAMS.MESSAGE).getAsString();
                    callback.onCompletion(ICallback.RESULT.FAILURE, message);
                }
            }
        });
    }

    public static void initializePayment(int amount, String email, final ICallback callback){

        RequestBody body = new FormBody.Builder()
                .add(PARAMS.AMOUNT, String.valueOf(amount))
                .add(PARAMS.EMAIL, email)
                .build();

        WebManager.call(PAY + INITIALIZE_PAYMENT, WebManager.METHOD.POST_AUTH,"", body, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onCompletion(ICallback.RESULT.FAILURE, null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();

                boolean resultStatus = JsonUtils.getBoolean(json.get(PARAMS.SUCCESS));

                if (resultStatus){

                    JsonObject jsonData = JsonUtils.getJsonObject(json.get(PARAMS.DATA));

                    PaymentModel initPayment = new PaymentModel();
                    initPayment.setAuthorization_url(jsonData.get(PARAMS.AUTHORIZATION_URL).getAsString());
                    initPayment.setReference(jsonData.get(PARAMS.REFERENCE).getAsString());

                    callback.onCompletion(ICallback.RESULT.SUCCESS, initPayment);
                }
                else {
                    String message = json.get(PARAMS.MESSAGE).getAsString();
                    callback.onCompletion(ICallback.RESULT.FAILURE, message);
                }
            }
        });
    }

    public static void verifyPayment(String reference, String delivery_id, final ICallback callback){

        RequestBody body = new FormBody.Builder()
                .add(PARAMS.REFERENCE, reference)
                .add(PARAMS.DELIVERY_ID, delivery_id)
                .build();

        WebManager.call(PAY + VERIFY_PAYMENT, WebManager.METHOD.POST, ACCESS_TOKEN, body, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onCompletion(ICallback.RESULT.FAILURE, null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();
                JsonObject jsonData = JsonUtils.getJsonObject(json.get(PARAMS.DATA));

                boolean resultStatus = JsonUtils.getBoolean(jsonData.get(PARAMS.STATUS));

                if (resultStatus){

                    JsonObject jsonData1 = JsonUtils.getJsonObject(jsonData.get(PARAMS.DATA));

                    String message = jsonData1.get(PARAMS.MESSAGE).getAsString();
                    Log.d("PaymentVerify==>", jsonData1.toString());

                    callback.onCompletion(ICallback.RESULT.SUCCESS, message);
                }
                else {
                    String message = json.get(PARAMS.MESSAGE).getAsString();
                    callback.onCompletion(ICallback.RESULT.FAILURE, message);
                }
            }
        });
    }

    public static void uploadToken(String token, final ICallback callback){

        RequestBody body = new FormBody.Builder()
                .add(PARAMS.FCM_TOKEN, token)
                .build();

        WebManager.call(ALERTS + UPLOAD_TOKEN, WebManager.METHOD.POST, ACCESS_TOKEN, body, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onCompletion(ICallback.RESULT.FAILURE, null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();

                boolean resultStatus = JsonUtils.getBoolean(json.get(PARAMS.SUCCESS));

                if (resultStatus){

                    JsonObject jsonData = JsonUtils.getJsonObject(json.get(PARAMS.DATA));
                    callback.onCompletion(ICallback.RESULT.SUCCESS, true);
                }
                else {
                    String message = json.get(PARAMS.MESSAGE).getAsString();
                    callback.onCompletion(ICallback.RESULT.FAILURE, message);
                }
            }
        });
    }

    public static void allTransaction(final ICallback callback){

        WebManager.call(PAY + ALL_TRANSACTION, ACCESS_TOKEN, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onCompletion(ICallback.RESULT.FAILURE, null);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();

                boolean resultStatus = JsonUtils.getBoolean(json.get(PARAMS.SUCCESS));

                if (resultStatus){

                    JsonArray jsonData = JsonUtils.getJsonArray(json.get(PARAMS.DATA));

                    ArrayList<TransactionModel> transArray = new ArrayList<>();

                    for (int i = 0; i <  jsonData.size(); i++){

                        JsonObject jsonTrans = (JsonObject) jsonData.get(i);
                        TransactionModel trans = new TransactionModel();

                        trans.setId(jsonTrans.get(PARAMS.ID).getAsString());
                        trans.setConsignor_id(jsonTrans.get(PARAMS.CONSIGNOR_ID).getAsString());
                        trans.setDriver_id(jsonTrans.get(PARAMS.DRIVER_ID).getAsString());
                        trans.setReference(jsonTrans.get(PARAMS.REFERENCE).getAsString());
                        trans.setPaidAt(jsonTrans.get(PARAMS.PAID_AT).getAsString());
                        trans.setAmount(jsonTrans.get(PARAMS.AMOUNT).getAsString());

                        transArray.add(trans);
                    }

                    callback.onCompletion(ICallback.RESULT.SUCCESS, transArray);
                }
                else {
                    String message = json.get(PARAMS.MESSAGE).getAsString();
                    callback.onCompletion(ICallback.RESULT.FAILURE, message);
                }
            }
        });
    }


    public static class PARAMS {

        private static final String TOKEN = "token";
        private static final String SUCCESS = "success";
        private static final String STATUS = "status";
        private static final String MESSAGE = "message";
        private static final String DATA = "data";
        public static final String PHONE = "phone";
        public static final String PASSWORD = "password";
        public static final String CATEGORY = "category"; //0: Consignor 1: Driver
        public static final String COUNTRY = "country";
        public static final String USER = "user";
        public static final String ID = "id";
        public static final String PHONE_COUNTRY_CODE = "phone_country_code";
        public static final String COUNTRY_CODE = "country_code";
        public static final String PICK_UP_LOCATION = "pick_up_location";
        public static final String DROP_OFF_LOCATION = "drop_off_location";
        public static final String EMPTY_RETURN_LOCATION = "empty_return_location";
        public static final String CONTAINER_SIZE = "container_size";
        public static final String CONTAINER_WEIGHT = "container_weight";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String PICK_UP_TIME = "pick_up_time";
        public static final String TDO_READY = "tdo_ready";
        public static final String DELIVERY_ID = "delivery_id";
        public static final String PRICE = "price";
        public static final String CONSIGNOR_ID = "consignor_id";
        public static final String ACCEPT_STATUS = "accept_status";
        public static final String DELIVERY_PRICE = "delivery_price";
        public static final String RESET_CODE = "reset_code";
        public static final String DRIVER_ID = "driver_id";
        public static final String AUTHORIZATION_URL = "authorization_url";
        public static final String REFERENCE = "reference";
        public static final String FCM_TOKEN = "fcm_token";
        public static final String PAID_AT = "paidAt";
        public static final String FILE_URLS = "file_urls";
        public static final String STATE = "state";
        public static final String CITY = "city";
        public static final String ZIPCODE = "zipcode";
        public static final String FEES = "fees";
        public static final String BALANCE = "balance";
        //public static final String MONDAY_FRIDAY = "monday_friday";
        public static final String PHOTO = "photo";
        public static final String PROFILE = "profile";
        public static final String MONDAY = "monday";
        public static final String TUESDAY = "tuesday";
        public static final String WEDNESDAY = "wednesday";
        public static final String THURSDAY = "thursday";
        public static final String FRIDAY = "friday";
        public static final String SATURDAY = "saturday";
        public static final String SUNDAY = "sunday";
        public static final String CLASS_PHOTO = "class";
        public static final String CLASS_LIST = "class_list";
        public static final String TOTAL_MARK = "total_mark";
        public static final String MARKED_USERS = "marked_users";
        public static final String DISTANCE = "distance";
        public static final String CLASS_ID = "class_id";
        public static final String MARK = "mark";
        public static final String REVIEW_LIST = "review_list";
        public static final String CLASS = "class";
        public static final String USER_LIST = "user_list";
        public static final String BOOKED_AT = "booked_at";
        public static final String CANCELED_AT = "cancel_requested_at";
        public static final String START_TIME = "start_time";
        public static final String BOOK_END_DATE = "book_end_date";
        public static final String USER_NAME = "user_name";
        public static final String BOOK_ID = "book_id";
        public static final String PAGE = "page";
        public static final String CLASS_NAME = "class_name";
        public static final String AGE_RANGE = "age_range";
        public static final String CLASS_USER_ID = "class_user_id";
        public static final String WAITING_ID = "waiting_id";
        public static final String COMMENT = "comment";
        public static final String CANCEL_COMMENT = "cancel_comment";
        public static final String REPLY_COMMENT = "reply_comment";
        public static final String SUBMIT_COMMENT = "submit_comment";
        public static final String USERID = "userid";
        public static final String LOCATION = "location";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String SCHOOL = "school";
        public static final String CREATE_AT = "created_at";
        public static final String ATTENDANCE = "attendance";
        public static final String CART_LIST = "cart_list";
        public static final String PRODUCT = "product";
        public static final String OWNER = "owner";
        public static final String IMAGE = "image";
        public static final String ORDER_LIST = "order_list";
        public static final String TRANSACTION_LIST = "transaction_list";
        public static final String ORDER_ID = "order_id";
        public static final String COST = "cost";
        public static final String BID_ID = "bid_id";
        public static final String COURIER_LIST = "courier_list";
        public static final String BID_LIST = "bid_list";

        public static final String CODE = "code";
        public static final String RESULT_CODE = "result_code";
        public static final String TIME = "time";
        public static final String TYPE = "type";
        public static final String DESCRIPTION = "description";
        public static final String ADDRESS = "address";
        public static final String CART_ID = "cart_id";
        public static final String COURIER_NAME = "courier_name";
        public static final String BUYER_ID = "buyer_id";
        public static final String BUYER_NAME = "buyer_name";
        public static final String FROM = "from";
        public static final String TO = "to";
        public static final String AMOUNT = "amount";
        public static final String PAY_TYPE = "pay_type";
        public static final String TRANSACTION_ID = "transaction_id";
        public static final String ADDRESS_LIST = "address_list";
        public static final String IS_AWARDED = "is_awarded"; //awarded - is_awarded
        public static final String SELLER_NAME = "seller_name";
        public static final String BID_COUNT = "bid_count";
        public static final String IS_BID = "is_bid";
        public static final String SOLD_COUNT = "sold_count";
        public static final String PICKED_UP = "picked_up";
        public static final String ARRIVING_TIME = "arriving_time";
        public static final String DELIVERED_NEAREST = "delivered_nearest";
        public static final String DELIVERED_SUCCESS = "delivered_success";
        public static final String TRACK_STATUS = "track_status";
        public static final String STRIPE_TOKEN = "stripe_token";
        public static final String IS_PUBLIC = "is_public";
        public static final String COMPLETED_AT = "completed_at";
        public static final String NOTIFICATION_LIST = "notification_list";
        public static final String CONTENT = "content";
        public static final String TITLE = "title";
        public static final String BID = "bid";
        public static final String UPDATED_AT = "updated_at";
        public static final String WEIGHT = "weight";
        public static final String SIZE = "size";

    }
}


