package com.bullhu.android.activities;

import static java.lang.Integer.parseInt;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

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
import android.widget.EditText;

import com.bullhu.android.R;
import com.bullhu.android.common.Commons;
import com.bullhu.android.common.Constants;
import com.bullhu.android.network.ApiManager;
import com.bullhu.android.network.ICallback;
import com.bullhu.android.utils.BitmapUtils;
import com.bumptech.glide.Glide;
import com.pixplicity.easyprefs.library.Prefs;
import com.rilixtech.CountryCodePicker;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends BaseActivity {

    @BindView(R.id.imv_profile)
    CircleImageView imv_profile;

    @BindView(R.id.edt_user_name)
    EditText edt_user_name;
    @BindView(R.id.edt_mobile)
    EditText edt_mobile;

    @BindView(R.id.ccp)
    CountryCodePicker ccp;
    String str_user_name = "", str_country_code = "", str_mobile = "";
    Uri _imageCaptureUri;
    String _photoPath = "";
    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,  Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ButterKnife.bind(this);
        loadLayout();
    }

    private void loadLayout(){


        Log.d("Photo==>", Commons.g_user.getPhoto());
        edt_user_name.setText(Commons.g_user.getName());
        edt_mobile.setText(Commons.g_user.getPhone());
        ccp.setCountryForPhoneCode(parseInt(Commons.g_user.getPhone_country_code()));
        if (!Commons.g_user.getPhoto().isEmpty())
            Glide.with(this).load(Commons.g_user.getPhoto()).centerCrop().into(imv_profile);

    }

    @OnClick(R.id.btn_update) void updateProfile(){

        str_user_name = edt_user_name.getText().toString();
        str_country_code = ccp.getFullNumberWithPlus();
        str_mobile = edt_mobile.getText().toString();

        if (checkedValid())
            processUpdateProfile();
    }

    private boolean checkedValid(){

        if (str_user_name.isEmpty()){
            showToast(R.string.enter_user_name);
            return false;
        }
        else if (str_mobile.isEmpty()){
            showToast(R.string.enter_mobile_number);
            return false;
        }
        return true;
    }

    void processUpdateProfile(){

        showHUD();

        ApiManager.profileUpdate(str_user_name, str_country_code, str_mobile, Commons.ACCESS_TOKEN, new ICallback() {
            @Override
            public void onCompletion(RESULT result, Object resultParam) {
                switch (result){

                    case SUCCESS:

                        if (_photoPath.isEmpty()) {

                            hideHUD();

                            /*Commons.g_user.setPhone(str_mobile);
                            Commons.g_user.setPhone_country_code(str_country_code);
                            Commons.g_user.setName(str_user_name);*/

                            finish();

                        }
                        else updatePhoto();

                        break;

                    case FAILURE:
                        hideHUD();
                        showToast((String) resultParam);
                        break;
                }
            }
        });
    }

    private void updatePhoto(){

        File photo = null;
        if (!_photoPath.isEmpty()){
            photo = new File(_photoPath);
        }

        ApiManager.uploadPhoto(photo, Commons.ACCESS_TOKEN, new ICallback() {
            @Override
            public void onCompletion(RESULT result, Object resultParam) {

                hideHUD();

                switch (result){

                    case SUCCESS:
                        finish();
                        break;

                    case FAILURE:

                        showToast((String) resultParam);
                        break;
                }
            }
        });
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

                } else return;
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

                        imv_profile.setImageBitmap(w_bmpSizeLimited);

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
}