package com.bullhu.android.activities;

import static com.bullhu.android.BullhuApplication.TAG;
import static com.bullhu.android.common.Commons.ACCESS_TOKEN;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bullhu.android.R;
import com.bullhu.android.activities.consignor.ConsignorMainActivity;
import com.bullhu.android.activities.driver.DriverMainActivity;
import com.bullhu.android.common.Commons;
import com.bullhu.android.common.Constants;
import com.bullhu.android.network.ApiManager;
import com.bullhu.android.network.ICallback;
import com.bullhu.android.utils.BitmapUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pixplicity.easyprefs.library.Prefs;
import com.rilixtech.CountryCodePicker;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends BaseActivity {

    @BindView(R.id.imv_photo)
    CircleImageView imv_photo;
    @BindView(R.id.edt_name)
    TextInputEditText edt_name;
    @BindView(R.id.edt_mobile)
    EditText edt_mobile;
    @BindView(R.id.edt_email)
    TextInputEditText edt_email;
    @BindView(R.id.edt_password)
    TextInputEditText edt_password;
    @BindView(R.id.toggle)
    RadioGroup toggle;
    @BindView(R.id.toggle_driver)
    RadioButton toggle_driver;
    @BindView(R.id.toggle_consignor)
    RadioButton toggle_consignor;
    @BindView(R.id.ccp)
    CountryCodePicker ccp;
    int user_type = 0; // 1:driver 0:consignor

    String str_name = "", country_code = "", str_number = "", str_email = "", str_password = "";

    Uri _imageCaptureUri;
    String _photoPath = "";
    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,  Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log
                        Prefs.putString(Constants.TOKEN, token);
                        Prefs.getString(Constants.TOKEN, "");
                        Log.d(TAG, Prefs.getString(Constants.TOKEN, ""));
                    }
                });

    }


    @OnClick(R.id.btn_register) void register(){

        str_name = edt_name.getText().toString();
        country_code = ccp.getFullNumberWithPlus();
        str_number = edt_mobile.getText().toString();
        str_email = edt_email.getText().toString();
        str_password = edt_password.getText().toString();

        //gotoMain();
        if (checkedValid())
            processSignUp();
    }

    private void processSignUp(){

        showHUD();

        ApiManager.signUp(str_name, str_email, str_password, country_code, str_number, user_type, new ICallback() {
            @Override
            public void onCompletion(RESULT result, Object resultParam) {

                switch (result){

                    case SUCCESS:
                        processUploadPhoto((String) resultParam);
                        break;

                    case FAILURE:

                        hideHUD();
                        showToast((String) resultParam);
                        break;
                }
            }
        });
    }

    void processUploadPhoto(String accessToken){

        File photo = null;
        if (!_photoPath.isEmpty()){
            photo = new File(_photoPath);
        }

        ApiManager.uploadPhoto(photo, accessToken, new ICallback() {
            @Override
            public void onCompletion(RESULT result, Object resultParam) {

                hideHUD();

                switch (result){

                    case SUCCESS:

                        Prefs.putString(Constants.PREFKEY_USEREMAIL, str_email);
                        Prefs.putString(Constants.PREFKEY_USERPWD, str_password);

                        gotoMain();
                        break;

                    case FAILURE:

                        if (resultParam != null)
                            showToast((String) resultParam);
                        else
                            showToast("Something went wrong");
                        break;
                }
            }
        });
    }

    private void gotoMain(){

        //Prefs.putInt("user_type", user_type);

        if (user_type == 1){

            startActivity(new Intent(SignUpActivity.this, DriverMainActivity.class));
        }
        else {

            startActivity(new Intent(SignUpActivity.this, ConsignorMainActivity.class));
        }
        finish();
    }

    @OnClick(R.id.txv_login) void gotoLogin(){

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
    //==================== CARMERA Permission========================================
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.MY_REQUEST_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  gps functionality

        }
    }

    @OnClick(R.id.imv_add) void uploadPhoto(){

        if (hasPermissions(this, PERMISSIONS)){

            selectPhoto();

        }else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 101);
        }

    }

    private void selectPhoto(){

        final String[] items = {"Take photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {
                    doTakePhoto();

                } else if (item == 1){
                    doTakeGallery();
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void doTakePhoto(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        _photoPath = BitmapUtils.getTempFolderPath() + "photo_temp.jpg";
        _imageCaptureUri = Uri.fromFile(new File(_photoPath));
        _imageCaptureUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(_photoPath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageCaptureUri);
        startActivityForResult(intent, Constants.PICK_FROM_CAMERA);

    }

    private void doTakeGallery(){

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, Constants.PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case Crop.REQUEST_CROP: {

                if (resultCode == RESULT_OK){

                    try {

                        File outFile = BitmapUtils.getOutputMediaFile(this, "temp.jpg");

                        InputStream in = getContentResolver().openInputStream(Uri.fromFile(outFile));
                        BitmapFactory.Options bitOpt = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeStream(in, null, bitOpt);
                        in.close();

                        ExifInterface ei = new ExifInterface(outFile.getAbsolutePath());
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);

                        Bitmap returnedBitmap = bitmap;

                        switch (orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                returnedBitmap = BitmapUtils.rotateImage(bitmap, 90);
                                bitmap.recycle();
                                bitmap = null;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                returnedBitmap = BitmapUtils.rotateImage(bitmap, 180);
                                bitmap.recycle();
                                bitmap = null;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                returnedBitmap = BitmapUtils.rotateImage(bitmap, 270);
                                bitmap.recycle();
                                bitmap = null;
                                break;

                            default:
                                break;
                        }

                        Bitmap w_bmpSizeLimited = Bitmap.createScaledBitmap(returnedBitmap, returnedBitmap.getWidth(), returnedBitmap.getHeight(), true);
                        File newFile = BitmapUtils.getOutputMediaFile(this, System.currentTimeMillis() + ".jpg");
                        BitmapUtils.saveOutput(newFile, w_bmpSizeLimited);
                        _photoPath = newFile.getAbsolutePath();

                        // imv_avatar(_photoPath);

                        imv_photo.setImageBitmap(w_bmpSizeLimited);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            }

            case Constants.PICK_FROM_ALBUM:

                if (resultCode == RESULT_OK){
                    _imageCaptureUri = data.getData();

                    beginCrop(_imageCaptureUri);
                }
                break;

            case Constants.PICK_FROM_CAMERA:
            {
                if (resultCode == RESULT_OK){
                    try {
                        String filename = "IMAGE_" + System.currentTimeMillis() + ".jpg";

                        Bitmap bitmap = BitmapUtils.loadOrientationAdjustedBitmap(_photoPath);
                        String w_strFilePath = "";
                        String w_strLimitedImageFilePath = BitmapUtils.getUploadImageFilePath(bitmap, filename);
                        if (w_strLimitedImageFilePath != null) {
                            w_strFilePath = w_strLimitedImageFilePath;
                        }

                        _photoPath = w_strFilePath;
                        _imageCaptureUri = Uri.fromFile(new File(_photoPath));
                        //  _photoPath= BitmapUtils.getSizeLimitedImageFilePath(_photoPath);
                        _imageCaptureUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(_photoPath));

                        beginCrop(_imageCaptureUri);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                }
                break;
            }
            default: break;
        }
    }

    private void beginCrop(Uri source) {

        Uri destination = Uri.fromFile(BitmapUtils.getOutputMediaFile(this, "temp.jpg"));
        Crop.of(source, destination).withMaxSize(840, 1024).start(this);
    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.toggle_driver:
                if (checked)
                    user_type = 1;
                break;

            case R.id.toggle_consignor:
                if (checked)
                    user_type = 0;
                break;
        }
    }

    private boolean checkedValid(){

        if (str_name.isEmpty()){
            showToast(R.string.input_full_name);
            return false;
        }
        else if (str_number.isEmpty()) {
            showToast(R.string.input_mobile_number);
            return false;
        }
        else if (str_email.isEmpty()){
            showToast(R.string.input_email);
            return false;
        }
        else if (str_password.isEmpty()){
            showToast(R.string.input_password);
            return false;
        }
        else if (_photoPath.isEmpty()){
            showToast(R.string.take_photo);
            return false;
        }

        return true;
    }
}