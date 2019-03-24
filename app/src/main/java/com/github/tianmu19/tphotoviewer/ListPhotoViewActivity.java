package com.github.tianmu19.tphotoviewer;


import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.alexvasilkov.gestures.transition.ViewsTransitionAnimator;
import com.github.tianmu19.tphotoviewerlibrary.StatusBarUtil;
import com.github.tianmu19.tphotoviewerlibrary.TImageEntity;
import com.github.tianmu19.tphotoviewerlibrary.TPhotoViewer;
import com.github.tianmu19.tphotoviewerlibrary.adapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListPhotoViewActivity extends AppCompatActivity {


    private List<TImageEntity> imageUrls;//图片地址
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
        FrameLayout rootView = findViewById(R.id.fl_root);
        // Initializing ListView
        //必需
        RecyclerView recyclerView = findViewById(R.id.recycler_list);
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
        animator = TPhotoViewer.getInstance().clickDisplay(this, recyclerView, rootView, imageUrls);
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
        imageUrls = new ArrayList<TImageEntity>();
        String[] arrayImageUrls = getResources().getStringArray(R.array.imageUrls);
        for (int i = 0; i < arrayImageUrls.length; i++) {
            TImageEntity entity = new TImageEntity();
            entity.setThumbUrl(arrayImageUrls[i]);
            entity.setOriginUrl(arrayImageUrls[i]);
            imageUrls.add(entity);
        }
    }

}
