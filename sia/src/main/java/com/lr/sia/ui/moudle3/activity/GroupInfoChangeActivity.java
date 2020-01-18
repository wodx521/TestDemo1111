package com.lr.sia.ui.moudle3.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.jaeger.library.StatusBarUtil;
import com.lr.sia.R;
import com.lr.sia.api.MethodUrl;
import com.lr.sia.basic.BasicActivity;
import com.lr.sia.basic.MbsConstans;
import com.lr.sia.mvp.presenter.RequestPresenterImp;
import com.lr.sia.ui.moudle.activity.LoginActivity1;
import com.lr.sia.utils.imageload.GlideApp;
import com.lr.sia.utils.permission.PermissionsUtils;
import com.lr.sia.utils.permission.RePermissionResultBack;
import com.lr.sia.utils.tool.AppUtil;
import com.lr.sia.utils.tool.JSONUtil;
import com.lr.sia.utils.tool.SPUtils;
import com.lr.sia.utils.tool.UtilTools;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class GroupInfoChangeActivity extends BasicActivity implements View.OnClickListener {
    private ImageView backImg;
    private ImageView ivGroupIcon;
    private EditText etNickname;
    private TextView tvConfirm;
    private LinearLayout llGroupIcon;
    private String mRequestTag;
    private String mHeadImgPath;
    private Uri uritempFile;
    private String imgName;
    private String imageData;
    private String groupId;
    private Map<String, Object> groupInfoMap;

    @Override
    public int getContentView() {
        return R.layout.activity_group_info_change;
    }

    @Override
    public void init() {
        StatusBarUtil.setColorForSwipeBack(this, ContextCompat.getColor(this, MbsConstans.TOP_BAR_COLOR), MbsConstans.ALPHA);
        backImg = findViewById(R.id.back_img);
        ivGroupIcon = findViewById(R.id.ivGroupIcon);
        etNickname = findViewById(R.id.etNickname);
        llGroupIcon = findViewById(R.id.llGroupIcon);
        tvConfirm = findViewById(R.id.tvConfirm);
        groupId = getIntent().getStringExtra("DATA");
        groupInfoMap = (Map<String, Object>) getIntent().getSerializableExtra("groupInfoMap");
        backImg.setOnClickListener(this);
        llGroupIcon.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);

        GlideApp.with(GroupInfoChangeActivity.this)
                .load(groupInfoMap.get("group_portrait") + "")
                .transform(new RoundedCornersTransformation(UtilTools.dip2px(GroupInfoChangeActivity.this, 50), 2))
                .placeholder(R.drawable.head)
                .error(R.drawable.head)
                .into(ivGroupIcon);
        etNickname.setHint(groupInfoMap.get("name") + "");
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void disimissProgress() {

    }

    @Override
    public void loadDataSuccess(Map<String, Object> tData, String mType) {
        Intent intent;
        switch (mType) {
            case MethodUrl.COMMON_UPLOAD:
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        imageData = tData.get("data") + "";
                        editUserInfoAction(imageData);
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        finish();
                        intent = new Intent(GroupInfoChangeActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.CHAT_GROUP_GETGROUPPORTRAIT:
                dismissProgressDialog();
                switch (tData.get("code") + "") {
                    case "1": //请求成功
                        String imageUrl;
                        if (imageData.contains("http://") || imageData.contains("https://")) {
                            imageUrl = imageData;
                        } else {
                            imageUrl = MbsConstans.IMAGE_URL + imageData;
                        }
                        GlideApp.with(GroupInfoChangeActivity.this)
                                .load(imageUrl)
                                .transform(new RoundedCornersTransformation(UtilTools.dip2px(GroupInfoChangeActivity.this, 50), 2))
                                .placeholder(R.drawable.head)
                                .error(R.drawable.head)
                                .into(ivGroupIcon);
                        String groupInfo = (String) SPUtils.get(GroupInfoChangeActivity.this, "groupInfo", "");
                        Map<String, Object> stringObjectMap = JSONUtil.getInstance().jsonMap(groupInfo);
                        stringObjectMap.put("group_portrait", imageUrl);
                        SPUtils.put(GroupInfoChangeActivity.this, "groupInfo", JSONUtil.getInstance().objectToJson(stringObjectMap));
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "0": //请求失败
                        showToastMsg(tData.get("msg") + "");
                        break;
                    case "-1": //token过期
                        finish();
                        intent = new Intent(GroupInfoChangeActivity.this, LoginActivity1.class);
                        startActivity(intent);
                        break;
                    default:
                }
                break;
            case MethodUrl.CHAT_GROUP_EDIT_GROUP_NAME:
                switch (tData.get("code") + "") {
                    case "1":
                        showToastMsg(tData.get("msg") + "");
                        String groupInfo = (String) SPUtils.get(GroupInfoChangeActivity.this, "groupInfo", "");
                        Map<String, Object> stringObjectMap = JSONUtil.getInstance().jsonMap(groupInfo);
                        stringObjectMap.put("name", etNickname.getText().toString());
                        SPUtils.put(GroupInfoChangeActivity.this, "groupInfo", JSONUtil.getInstance().objectToJson(stringObjectMap));
                        break;
                    case "-1":
                        closeAllActivity();
                        intent = new Intent(GroupInfoChangeActivity.this, LoginActivity1.class);
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

    private void editUserInfoAction(String value) {
        mRequestPresenterImp = new RequestPresenterImp(this, GroupInfoChangeActivity.this);
        mRequestTag = MethodUrl.CHAT_GROUP_GETGROUPPORTRAIT;
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = com.lr.sia.utils.tool.SPUtils.get(GroupInfoChangeActivity.this, MbsConstans.ACCESS_TOKEN, "").toString();
        }
        String groupInfo = (String) SPUtils.get(GroupInfoChangeActivity.this, "groupInfo", "");
        Map<String, Object> stringObjectMap = JSONUtil.getInstance().jsonMap(groupInfo);
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", groupId);
        map.put("portrait", value);
        mRequestPresenterImp.requestPostToMap(MethodUrl.CHAT_GROUP_GETGROUPPORTRAIT, map);
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
            case R.id.llGroupIcon:
                actionSheetDialogNoTitle();
                break;
            case R.id.tvConfirm:
                String name = etNickname.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    editGroupNameAction(name);
                } else {
                    showToastMsg(R.string.inputGroupName);
                }
                break;
            default:
        }
    }

    private void editGroupNameAction(String name) {
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(GroupInfoChangeActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        String groupInfo = (String) SPUtils.get(GroupInfoChangeActivity.this, "groupInfo", "");
        Map<String, Object> stringObjectMap = JSONUtil.getInstance().jsonMap(groupInfo);
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("id", groupId);
        map.put("name", name);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.requestPostToMap(mHeaderMap, MethodUrl.CHAT_GROUP_EDIT_GROUP_NAME, map);
    }

    private void actionSheetDialogNoTitle() {
        final String[] stringItems = {"拍照", "相册选择"};
        final ActionSheetDialog dialog = new ActionSheetDialog(GroupInfoChangeActivity.this, stringItems, null);
        dialog.isTitleShow(false).show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                switch (position) {
                    case 0:
                        PermissionsUtils.requsetRunPermission(GroupInfoChangeActivity.this, new RePermissionResultBack() {
                            @Override
                            public void requestSuccess() {
                                //toast(R.string.successfully);
                                photoPic();
                            }

                            @Override
                            public void requestFailer() {
                                toast(R.string.failure);
                            }
                        }, Permission.Group.STORAGE, Permission.Group.CAMERA);
                        break;
                    case 1:
                        PermissionsUtils.requsetRunPermission(GroupInfoChangeActivity.this, new RePermissionResultBack() {
                            @Override
                            public void requestSuccess() {
                                //toast(R.string.successfully);
                                localPic();
                            }

                            @Override
                            public void requestFailer() {
                                toast(R.string.failure);
                            }
                        }, Permission.Group.STORAGE);
                        break;
                    default:
                }
            }
        });
    }

    protected void toast(@StringRes int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void photoPic() {
        /**
         * 下面这句还是老样子，调用快速拍照功能，至于为什么叫快速拍照，大家可以参考如下官方
         * 文档，you_sdk_path/docs/guide/topics/media/camera.html
         * 我刚看的时候因为太长就认真看，其实是错的，这个里面有用的太多了，所以大家不要认为
         * 官方文档太长了就不看了，其实是错的，这个地方也错了，必须改正
         */
        Uri uri;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //下面这句指定调用相机拍照后的照片存储的路径
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(GroupInfoChangeActivity.this, AppUtil.getAppProcessName(this) + ".FileProvider", new File(Environment.getExternalStorageDirectory(), "xiaoma.jpg"));
        } else {
            uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "xiaoma.jpg"));
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 2);
    }

    private void localPic() {
        /**
         * 刚开始，我自己也不知道ACTION_PICK是干嘛的，后来直接看Intent源码，
         * 可以发现里面很多东西，Intent是个很强大的东西，大家一定仔细阅读下
         */
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        /**
         * 下面这句话，与其它方式写是一样的效果，如果：
         * intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
         * intent.setType(""image/*");设置数据类型
         * 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
         * 这个地方小马有个疑问，希望高手解答下：就是这个数据URI与类型为什么要分两种形式来写呀？有什么区别？
         */
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = null;
        switch (requestCode) {
            // 如果是直接从相册获取
            case 1:
                if (data != null) {
                    startPhotoZoom(data.getData());
                }
                break;
            // 如果是调用相机拍照时
            case 2:
                File temp = new File(Environment.getExternalStorageDirectory() + "/xiaoma.jpg");
                if (temp.exists()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(GroupInfoChangeActivity.this, AppUtil.getAppProcessName(GroupInfoChangeActivity.this) + ".FileProvider", temp);
                    } else {
                        uri = Uri.fromFile(temp);
                    }
                    startPhotoZoom(uri);
                }
                break;
            // 取得裁剪后的图片
            case 3:
                /**
                 * 非空判断大家一定要验证，如果不验证的话，
                 * 在剪裁之后如果发现不满意，要重新裁剪，丢弃
                 * 当前功能时，会报NullException，小马只
                 * 在这个地方加下，大家可以根据不同情况在合适的
                 * 地方做判断处理类似情况
                 *
                 */
                // 将Uri图片转换为Bitmap
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
                    // TODO，将裁剪的bitmap显示在imageview控件上
                    Drawable dr = new BitmapDrawable(getResources(), bitmap);
                    saveCroppedImage(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        /*
         * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
         * yourself_sdk_path/docs/reference/android/content/Intent.html
         * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能,
         * 是直接调本地库的，小马不懂C C++  这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么
         * 制做的了...吼吼
         */
    /*    Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image*//*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);*/
        File file = new File(MbsConstans.BASE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent.putExtra("crop", "true");
        // intent.putExtra("noFaceDetection", true);
        // 宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪图片宽高
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);

        // intent.putExtra("scale", true);
        // intent.putExtra("return-data", true);
        // this.startActivityForResult(intent, AppFinal.RESULT_CODE_PHOTO_CUT);
        /**
         * 此方法返回的图片只能是小图片（sumsang测试为高宽160px的图片）
         * 故将图片保存在Uri中，调用时将Uri转换为Bitmap，此方法还可解决miui系统不能return data的问题
         */
        imgName = System.currentTimeMillis() + ".jpg";
        uritempFile = Uri.parse("file:///" + MbsConstans.BASE_PATH + "/" + imgName);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, 3);

    }

    private void saveCroppedImage(Bitmap bmp) {
        try {
            File saveFile = new File(MbsConstans.HEAD_IMAGE_PATH);
            mHeadImgPath = MbsConstans.HEAD_IMAGE_PATH + new Date().getTime() + ".png";
            File file = new File(mHeadImgPath);
            if (!saveFile.exists()) {
                saveFile.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            File saveFile2 = new File(mHeadImgPath);
            FileOutputStream fos = new FileOutputStream(saveFile2);
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.flush();
            fos.close();

            // uploadAliPic(new Date().getTime()+".png",filepath);
            //上传头像
            uploadPicAction();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void uploadPicAction() {
        showProgressDialog();
        mRequestTag = MethodUrl.COMMON_UPLOAD;
        Map<String, Object> signMap = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        if (UtilTools.empty(MbsConstans.ACCESS_TOKEN)) {
            MbsConstans.ACCESS_TOKEN = SPUtils.get(GroupInfoChangeActivity.this, MbsConstans.SharedInfoConstans.ACCESS_TOKEN, "").toString();
        }
        map.put("token", MbsConstans.ACCESS_TOKEN);
        map.put("type", "group");
        Map<String, Object> fileMap = new HashMap<>();
        fileMap.put("file", mHeadImgPath);
        Map<String, String> mHeaderMap = new HashMap<String, String>();
        mRequestPresenterImp.postFileToMap(mHeaderMap, MethodUrl.COMMON_UPLOAD, signMap, map, fileMap);
    }
}
