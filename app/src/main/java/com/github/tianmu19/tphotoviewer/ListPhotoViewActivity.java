package com.github.tianmu19.tphotoviewer;


import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.alexvasilkov.gestures.transition.ViewsTransitionAnimator;
import com.github.tianmu19.tphotoviewerlibrary.StatusBarUtil;
import com.github.tianmu19.tphotoviewerlibrary.TImgBean;
import com.github.tianmu19.tphotoviewerlibrary.TPhotoViewer;
import com.github.tianmu19.tphotoviewerlibrary.adapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sunwei
 * email：tianmu19@gmail.com
 * date：2019/11/8 22:32
 * version：1.0
 * <p>description：点击列表图片 展示在viewpager中   </p>
 */

public class ListPhotoViewActivity extends AppCompatActivity {
    private List<TImgBean> imageUrls;//图片地址
    private ViewsTransitionAnimator<Integer> animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_photo_view);
        StatusBarUtil.setTranslucent(this);
        initData();
        initRecyclerView();
    }

    private void initRecyclerView() {
        // 1.Initializing RecyclerView
        GridRecyclerView recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int childAdapterPosition = parent.getChildAdapterPosition(view);
                if ((childAdapterPosition + 1) % 3 != 0) {
                    outRect.right = 12;
                }
                if ((imageUrls.size() - childAdapterPosition - 1) / 3 != 0) {
                    outRect.bottom = 12;
                }
            }
        });
        RecyclerAdapter adapter = new RecyclerAdapter(imageUrls);
        recyclerView.setAdapter(adapter);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.grid_layout_animation_scale_random);
        recyclerView.setLayoutAnimation(animationController);
        recyclerView.scheduleLayoutAnimation();
        animator = TPhotoViewer.getInstance().clickDisplay(this, recyclerView, imageUrls);
    }

    @Override
    public void onBackPressed() {
//         We should leave full image mode instead of closing the screen
        if (!animator.isLeaving()) {
            animator.exit(true);
        } else {
            super.onBackPressed();
        }
    }

    private void initData() {
        imageUrls = new ArrayList<>();
        String[] arrayImageUrls = getResources().getStringArray(R.array.imageUrls);
        for (int i = 0; i < arrayImageUrls.length; i++) {
            TImgBean entity = new TImgBean();
            entity.setThumbUrl(arrayImageUrls[i]);
            entity.setOriginUrl(arrayImageUrls[i]);
            imageUrls.add(entity);
        }
    }

}
