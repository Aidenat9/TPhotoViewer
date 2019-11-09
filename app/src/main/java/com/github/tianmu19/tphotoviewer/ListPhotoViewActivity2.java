package com.github.tianmu19.tphotoviewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alexvasilkov.gestures.transition.ViewsTransitionAnimator;
import com.github.tianmu19.tphotoviewerlibrary.StatusBarUtil;
import com.github.tianmu19.tphotoviewerlibrary.TImgBean;
import com.github.tianmu19.tphotoviewerlibrary.TPhotoViewer;
import com.github.tianmu19.tphotoviewerlibrary.adapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListPhotoViewActivity2 extends AppCompatActivity {
    private List<TImgBean> imageShowUrls;//图片地址
    private List<TImgBean> imageUrls;//图片地址
    private ViewsTransitionAnimator<Integer> animator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_photo_view2);
        StatusBarUtil.setTranslucent(this);
        initData();
        initRecyclerView();
    }
    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        RecyclerAdapter adapter = new RecyclerAdapter(imageUrls);
        recyclerView.setAdapter(adapter);
        animator = TPhotoViewer.getInstance().clickDisplay(this, recyclerView, imageShowUrls);
    }

    @Override
    public void onBackPressed() {
        if (!animator.isLeaving()) {
            animator.exit(true);
        } else {
            super.onBackPressed();
        }
    }

    private void initData() {
        //总共4张图，展示3个
        imageShowUrls = new ArrayList<>();
        imageUrls = new ArrayList<>();
        String[] arrayImageUrls = getResources().getStringArray(R.array.imageUrls);
        for (int i = 0; i < arrayImageUrls.length; i++) {
            TImgBean entity = new TImgBean();
            entity.setThumbUrl(arrayImageUrls[i]);
            entity.setOriginUrl(arrayImageUrls[i]);
            if(i<7){
                imageShowUrls.add(entity);
                if(i<4){
                    imageUrls.add(entity);
                }
            }else{
                return;
            }
        }
    }
}
