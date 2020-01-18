package com.lr.sia.ui.moudle1.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.BasicApplication;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mvp.presenter.RequestPresenterImp;
import com.lr.sia.ui.moudle.activity.LoginActivity1;

import com.lr.sia.utils.imageload.GlideUtils;
import com.lr.sia.utils.permission.PermissionsUtils;
import com.lr.sia.utils.permission.RePermissionResultBack;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReceiveActivity extends BasicActivity implements View.OnClickListener {
    private ImageView backImg, ivReceiveCode;
    private TextView tvTitle, tvReceiveAddress, tvCopy, tvSaveImage;
    private String mRequestTag;
    private String coinId;
    private Bitmap bitmap;
    private String addressQrcode;

    @Override
    public int getContentView() {
        return R.layout.activity_receive;
    }

    @Override
    public void init() {
        coinId = getIntent().getStringExtra("coinId");
        tvTitle = findViewById(R.id.tvTitle);
        backImg = findViewById(R.id.back_img);
        tvReceiveAddress = findViewById(R.id.tvReceiveAddress);
        tvCopy = findViewById(R.id.tvCopy);
        ivReceiveCode = findViewById(R.id.ivReceiveCode);
        tvSaveImage = findViewById(R.id.tvSaveImage);
        tvTitle.setText(R.string.receipt);
        backImg.setOnClickListener(this);
        tvCopy.setOnClickListener(this);
        tvSaveImage.setOnClickListener(this);

        getReceiveAction();
    }

    private void getReceiveAction() {
        mRequestPresenterImp = new RequestPresenterImp(this, ReceiveActivity.this);
        mRequestTag = MethodUrl.ACCOUNT_GETUSERCOINADDRESS;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(ReceiveActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", coinId);
        mRequestPresenterImp.requestPostToMap(MethodUrl.ACCOUNT_GETUSERCOINADDRESS, map);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap = null;
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void disimissProgress() {

    }

    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {
        Intent intent = null;
        switch (mType) {
            case MethodUrl.ACCOUNT_GETUSERCOINADDRESS:
                switch (tData.get("code") + "") {
                    case "1":
                        Map<String, Object> data = (Map<String, Object>) tData.get("data");
                        String address = data.get("address") + "";
                        addressQrcode = data.get("address_qrcode") + "";
                        tvReceiveAddress.setText(address);
                        Glide.with(ReceiveActivity.this)
                                .load(addressQrcode)
                                .into(ivReceiveCode);
                        break;
                    case "-1":
                        finish();
                        intent = new Intent(ReceiveActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    case "0":
                        showToastMsg(tData.get("msg") + "");
                        break;
                    default:
                }
                break;
            default:
        }
    }

    @Override
    public void loadDataError(Map<String, Object> map, String mType) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.tvCopy:
                ClipboardManager cm = (ClipboardManager) BasicApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                if (!TextUtils.isEmpty(tvReceiveAddress.getText())) {
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", tvReceiveAddress.getText());
                    // 将ClipData内容放到系统剪贴板里。
                    if (cm != null) {
                        cm.setPrimaryClip(mClipData);
                        showToastMsg(R.string.copy_success);
                    }
                } else {
                    showToastMsg(R.string.copy_fail);
                }
                break;
            case R.id.tvSaveImage:
                PermissionsUtils.requsetRunPermission(ReceiveActivity.this, new RePermissionResultBack() {
                    @Override
                    public void requestSuccess() {
                        //toast(R.string.successfully);
//                        String path = Environment.getExternalStorageDirectory() + File.separator + "Download" + File.separator + System.currentTimeMillis() + ".jpg";
//                        saveImageViewBitmap(ivReceiveCode, path);
                        GlideUtils.downloadImage(ReceiveActivity.this,addressQrcode);
                    }

                    @Override
                    public void requestFailer() {
                        showToastMsg(R.string.failure);
                    }
                }, Permission.Group.STORAGE);
                break;
            default:
        }
    }

    public void saveImageViewBitmap(ImageView imageView, String filePath) {
        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            return;
        }
        FileOutputStream outStream = null;
        File file = new File(filePath);
        // 如果是目录不允许保存
        if (file.isDirectory()) {
            return;
        }
        try {
            outStream = new FileOutputStream(file);
            Drawable current = drawable.getCurrent();
            bitmap = ((BitmapDrawable) current).getBitmap();
            if (!bitmap.isRecycled()) {
                boolean compress = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                if (compress) {
                    showToastMsg(getString(R.string.saveSuccess) + filePath);
                } else {
                    showToastMsg(getString(R.string.saveFail));
                }
            }
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bitmap != null) {
                    bitmap.recycle();
                }
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri uri = Uri.fromFile(file);
            BasicApplication.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        }
    }


}
