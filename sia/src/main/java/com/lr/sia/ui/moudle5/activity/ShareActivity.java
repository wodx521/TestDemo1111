package com.lr.sia.ui.moudle5.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.BasicApplication;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.utils.permission.PermissionsUtils;
import com.lr.sia.utils.permission.RePermissionResultBack;
import com.lr.sia.utils.tool.JSONUtil;
import com.lr.sia.utils.tool.SPUtils;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Map;

public class ShareActivity extends BasicActivity implements View.OnClickListener, View.OnLongClickListener {
    private ImageView ivUserIcon, ivQRCode, backImg;
    private TextView tvName, tvUserId, tvUrl, tvCopyUrl, tvRedInvite;
    private ClipData clipData;
    private ClipboardManager mClipboardManager;
    private String inviteCode;

    @Override
    public int getContentView() {
        return R.layout.activity_share;
    }

    @Override
    public void init() {
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        String userInfo = (String) SPUtils.get(ShareActivity.this, MbsConstans.SharedInfoConstans.LOGIN_INFO, "");
        Map<String, Object> stringObjectMap = JSONUtil.getInstance().jsonMap(userInfo);
        Map<String, Object> data = (Map<String, Object>) stringObjectMap.get("data");
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, R.color.toolbar_color), MbsConstans.ALPHA);

        ivUserIcon = findViewById(R.id.ivUserIcon);
        tvName = findViewById(R.id.tvName);
        tvRedInvite = findViewById(R.id.tvRedInvite);
        tvUserId = findViewById(R.id.tvUserId);
        ivQRCode = findViewById(R.id.ivQRCode);
        tvUrl = findViewById(R.id.tvUrl);
        tvCopyUrl = findViewById(R.id.tvCopyUrl);
        backImg = findViewById(R.id.back_img);

        backImg.setOnClickListener(this);
        tvUserId.setOnClickListener(this);
        tvCopyUrl.setOnClickListener(this);
        ivQRCode.setOnLongClickListener(this);
        tvRedInvite.setOnClickListener(this);

        String nickName = data.get("nick_name") + "";
        String userCode = data.get("user_code") + "";
        String avatarImage = data.get("avatar_image") + "";
        String inviteQrcode = data.get("invite_qrcode") + "";
        inviteCode = data.get("invite_code") + "";
        String inviteUrl = data.get("invite_url") + "";
        tvName.setText(nickName);
        tvUserId.setText(getString(R.string.inviteCode1) + inviteCode);
        tvUrl.setText(inviteUrl);

        Glide.with(ShareActivity.this)
                .load(avatarImage)
                .placeholder(R.drawable.head)
                .error(R.drawable.head)
                .into(ivUserIcon);
        Glide.with(ShareActivity.this)
                .load(inviteQrcode)
                .placeholder(R.drawable.erweimacode)
                .error(R.drawable.erweimacode)
                .into(ivQRCode);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void disimissProgress() {

    }

    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {

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
            case R.id.tvRedInvite:
                Intent intent = new Intent(ShareActivity.this, RedInviteActivity.class);
                startActivity(intent);
                break;
            case R.id.tvUserId:
                ClipboardManager cm = (ClipboardManager) BasicApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                if (!TextUtils.isEmpty(inviteCode)) {
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", inviteCode);
                    // 将ClipData内容放到系统剪贴板里。
                    if (cm != null) {
                        cm.setPrimaryClip(mClipData);
                        showToastMsg(getString(R.string.copy_success));
                    }
                    finish();
                } else {
                    showToastMsg(getString(R.string.copy_fail));
                }
                break;
            case R.id.tvCopyUrl:
                String url = tvUrl.getText().toString();
                if (!TextUtils.isEmpty(url)) {
                    clipData = ClipData.newPlainText("币友", tvUrl.getText().toString());
                    mClipboardManager.setPrimaryClip(clipData);
                    showToastMsg(getString(R.string.copy_success));
                } else {
                    showToastMsg(getString(R.string.copy_fail));
                }
                break;
            default:
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.ivQRCode:
                PermissionsUtils.requsetRunPermission(ShareActivity.this, new RePermissionResultBack() {
                    @Override
                    public void requestSuccess() {
                        saveCroppedImage();
                    }

                    @Override
                    public void requestFailer() {

                    }
                }, Permission.Group.STORAGE);
                return true;
            default:
        }
        return false;
    }

    private void saveCroppedImage() {
        ivQRCode.setDrawingCacheEnabled(true);
        Bitmap bmp = ivQRCode.getDrawingCache();

        try {
            File saveFile = new File(MbsConstans.PIC_SAVE);

            String filepath = MbsConstans.PIC_SAVE + new Date().getTime() + ".png";
            File file = new File(filepath);
            if (!saveFile.exists()) {
                saveFile.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            File saveFile2 = new File(filepath);

            FileOutputStream fos = new FileOutputStream(saveFile2);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            ivQRCode.setDrawingCacheEnabled(false);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
