package com.github.tianmu19.tphotoviewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alexvasilkov.gestures.transition.ViewsTransitionAnimator;
import com.alexvasilkov.gestures.utils.GlideHelper;
import com.github.tianmu19.tphotoviewerlibrary.StatusBarUtil;
import com.github.tianmu19.tphotoviewerlibrary.TImgBean;
import com.github.tianmu19.tphotoviewerlibrary.TPhotoViewer;
import com.klogutil.KLog;


public class MainActivity extends AppCompatActivity {
    private final static String photoUrl = "http://t2.hddhhn.com/uploads/tu/201806/9999/bdab122a85.jpg";
    private ViewsTransitionAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setTranslucent(this);
        FrameLayout rootview = findViewById(R.id.rootview);
        ImageView image = findViewById(R.id.single_image_from);
        GlideHelper.loadThumb(image, image.getWidth(), image.getHeight(), photoUrl, (int) Utils.dp2px(10));
        //1.绑定 imageview和上下文，图片地址
//        TphotoUtil.getInstance().click(this, image, photoUrl);
        //2.
        TImgBean entity = new TImgBean();
        entity.setOriginUrl(photoUrl);
        entity.setThumbUrl(photoUrl);
        KLog.e("___oncreate");
        animator = TPhotoViewer.getInstance().clickDisplayOne(this, image, entity);
    }

    @Override
    public void onBackPressed() {
        if (!animator.isLeaving()) {
            animator.exit(true);
        } else {
            super.onBackPressed();
        }
    }

    public void toListActivity(View view){
        startActivity(new Intent(getBaseContext(),ListPhotoViewActivity.class));
    }
    public void toDemoActivity(View view){
    }


}
