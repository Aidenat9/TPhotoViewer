package com.github.tianmu19.tphotoviewerlibrary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alexvasilkov.gestures.animation.ViewPositionAnimator;
import com.alexvasilkov.gestures.commons.RecyclePagerAdapter;
import com.alexvasilkov.gestures.transition.GestureTransitions;
import com.alexvasilkov.gestures.transition.ViewsTransitionAnimator;
import com.alexvasilkov.gestures.transition.tracker.SimpleTracker;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.alexvasilkov.gestures.views.HackyViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.tianmu19.tphotoviewerlibrary.adapter.PhotoPagerAdapter;
import com.github.tianmu19.tphotoviewerlibrary.adapter.RecyclerAdapter;
import com.klogutil.KLog;

import java.io.File;
import java.util.List;

import me.zhanghai.android.systemuihelper.SystemUiHelper;

import static com.github.tianmu19.tphotoviewerlibrary.FileUtil.FOLDER;

/**
 * @author sunwei
 * 邮箱：tianmu19@gmail.com
 * 时间：2019/3/21 21:43
 * 包名：com.github.tianmu19.tphotoviewerlibrary
 * <p>description:    图片浏览工具        </p
 */
public class TPhotoViewer {
    private LayoutInflater inflater;
    private PhotoPagerAdapter pagerAdapter;
    private ContentLoadingProgressBar contentLoadingProgressBar;
    private SystemUiHelper mSystemUiHelper;
    final static String SEP = "/";
    private ViewsTransitionAnimator<Integer> animator;
    private HackyViewPager viewPager;
    private View view;
    private ViewGroup rootViewGroup;

    public static TPhotoViewer getInstance() {
        return InstanceHolder.ourInstance;
    }

    private static class InstanceHolder {
        @SuppressLint("StaticFieldLeak")
        private static final TPhotoViewer ourInstance = new TPhotoViewer();
    }

    private TPhotoViewer() {
    }

    /**
     * 在viewpager中展示图片
     *
     * @param activity     展示的载体
     * @param recyclerView 列表
     * @param imageUrls    图片集合
     */
    public ViewsTransitionAnimator<Integer> clickDisplay(@NonNull Activity activity, @NonNull RecyclerView recyclerView, @NonNull List<TImgBean> imageUrls) {
        inflater = LayoutInflater.from(activity);
        //1.得到当前界面的视图
        rootViewGroup = (ViewGroup) activity.getWindow().getDecorView().getRootView();
        //2.add viewgroup
        view = inflater.inflate(R.layout.layout_viewpager_gallery, rootViewGroup, false);
        rootViewGroup.addView(view);
        //3. init ViewPager
        viewPager = view.findViewById(R.id.recycler_pager);
        TextView tvDot = view.findViewById(R.id.tv_dot);
        View background = view.findViewById(R.id.recycler_full_background);
        ImageView btn = view.findViewById(R.id.iv_download);
        btn.setOnClickListener(v->{
            int i = viewPager.getCurrentItem();
            downloadImg(imageUrls.get(i).getOriginUrl(),activity);
        });
        pagerAdapter = new PhotoPagerAdapter(viewPager);
        viewPager.setAdapter(pagerAdapter);
        pagerAdapter.setPhotos(imageUrls);
        RecyclerAdapter adapter = (RecyclerAdapter) recyclerView.getAdapter();

        //4.init tracker & anim
        final SimpleTracker gridTracker = new SimpleTracker() {
            @Override
            public View getViewAt(int pos) {
                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForLayoutPosition(pos);
                return holder == null ? null : adapter.getImageView(holder);
            }
        };

        final SimpleTracker pagerTracker = new SimpleTracker() {
            @Override
            public View getViewAt(int pos) {
                RecyclePagerAdapter.ViewHolder holder = pagerAdapter.getViewHolder(pos);
                return holder == null ? null : PhotoPagerAdapter.getImage(holder);
            }
        };

        animator = GestureTransitions.from(recyclerView, gridTracker)
                .into(viewPager, pagerTracker);

        animator.addPositionUpdateListener(new ViewPositionAnimator.PositionUpdateListener() {
            @Override
            public void onPositionUpdate(float position, boolean isLeaving) {
                applyImageAnimationState(position, background,btn, tvDot, activity, isLeaving);
            }
        });
        //5.process anim
        int size = imageUrls.size();
        if (null != adapter) {
            adapter.setImageClickListener(new RecyclerAdapter.ImageClickListener() {
                @Override
                public void onClick(int pos) {
                    if (null != tvDot) {
                        tvDot.setText((pos + 1) + SEP + size);
                    }
                    pagerAdapter.setActivated(true);
                    animator.enter(pos, true);
                }
            });
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (null != tvDot) {
                    tvDot.setText((i + 1) + SEP + size);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        pagerAdapter.setImageClickListener(new PhotoPagerAdapter.ImageClickListener() {
            @Override
            public void onClickFullImage() {
                if (!animator.isLeaving()) {
                    animator.exit(true);
                }
            }
        });
        return animator;
    }

    /**
     * 改变viewpager的背景色
     */
    private void applyImageAnimationState(float position, View background,View btn, TextView tvDot
            , Activity activity, boolean isLeaving) {
        if (null == activity) {
            return;
        }
        background.setVisibility(position == 0f ? View.INVISIBLE : View.VISIBLE);
        background.setAlpha(position);
        btn.setVisibility(position == 0f ? View.INVISIBLE : View.VISIBLE);
        btn.setAlpha(position);
        tvDot.setVisibility(position == 0f ? View.INVISIBLE : View.VISIBLE);
        tvDot.setAlpha(position);
        if (isLeaving && position == 0f) {
            if (null != pagerAdapter){
                pagerAdapter.setActivated(false);
            }
        }
    }

    /**
     * ---------------单张图片----
     *
     * @param activity   activity
     * @param imageView  源图
     * @param tImgBean 数据
     * @return ViewsTransitionAnimator
     */
    public ViewsTransitionAnimator clickDisplayOne(@NonNull Activity activity, @NonNull View imageView, @NonNull TImgBean tImgBean) {
        if (!(imageView instanceof ImageView)) {
            return null;
        }
        inflater = LayoutInflater.from(activity);
        ViewGroup rootViewGroup = (ViewGroup) activity.getWindow().getDecorView().getRootView();
        //1.add group
        View gestureview = inflater.inflate(R.layout.layout_one_getstureview, rootViewGroup, false);
        rootViewGroup.addView(gestureview);
        //2.init getstureview
        GestureImageView fullImage = gestureview.findViewById(R.id.single_image_full);
        contentLoadingProgressBar = gestureview.findViewById(R.id.cprogressbar);
        contentLoadingProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.blue_material), PorterDuff.Mode.MULTIPLY);
        hideContentProgress();
        ImageView btn = gestureview.findViewById(R.id.iv_download);
        btn.setOnClickListener(v->{
            downloadImg(tImgBean.getOriginUrl(), activity);
        });

        View fullBackground = gestureview.findViewById(R.id.single_image_back);

        ImageView image = (ImageView) imageView;
        //3.init anim
        ViewsTransitionAnimator animator = GestureTransitions.from(image).into(fullImage);
        //4.listener
        animator.addPositionUpdateListener(new ViewPositionAnimator.PositionUpdateListener() {
            @Override
            public void onPositionUpdate(float position, boolean isLeaving) {
                fullBackground.setAlpha(position);
                fullBackground.setVisibility(position == 0f && isLeaving ? View.INVISIBLE : View.VISIBLE);
                btn.setAlpha(position);
                btn.setVisibility(position == 0f && isLeaving ? View.INVISIBLE : View.VISIBLE);
                fullImage.setVisibility(position == 0f && isLeaving ? View.INVISIBLE : View.VISIBLE);
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Setting image drawable from 'from' view to 'to' to prevent flickering
                if (fullImage.getDrawable() == null) {
                    fullImage.setImageDrawable(image.getDrawable());
                }
                animator.enterSingle(true);
                showOneFullImage();
            }


            private void showOneFullImage() {
                final RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .override(Target.SIZE_ORIGINAL)
                        .dontTransform();
                showContentProgress();
                Glide.with(activity).load(tImgBean.getOriginUrl())
                        .apply(options).thumbnail(0.5f)
                        .into(new DrawableImageViewTarget(fullImage) {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                super.onResourceReady(resource, transition);
                                hideContentProgress();
                                fullImage.setImageDrawable(resource);
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                                hideContentProgress();
                            }

                            @Override
                            public void onStop() {
                                super.onStop();
                                hideContentProgress();
                            }
                        });
            }
        });
        fullImage.setOnClickListener(v -> {
            if (!animator.isLeaving()) {
                hideContentProgress();
                animator.exit(true);
            }
        });

        return animator;
    }


    private void showContentProgress() {
        contentLoadingProgressBar.setVisibility(View.VISIBLE);
        contentLoadingProgressBar.show();
    }

    private void hideContentProgress() {
        if (contentLoadingProgressBar.isShown()) {
            contentLoadingProgressBar.hide();
            contentLoadingProgressBar.setVisibility(View.GONE);
        }
    }


    /**
     * 下载图片
     *
     * @param url
     */
    private void downloadImg(String url, Context context) {
      Activity activity = (Activity) context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(new String[]{Manifest.permission_group.STORAGE},1001);
        }
        if(TextUtils.isEmpty(url)){
            return;
        }
        String fileName = splitrUrl(url);
        File file = new File(Environment.getExternalStorageDirectory() + FOLDER + fileName);
        if (!file.exists() || file.length() <= 0) {
            File dir = new File(Environment.getExternalStorageDirectory()
                    + FOLDER);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if (null != resource) {
                                FileUtil.getInstance().saveIMG(fileName, resource, context);
                                Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            Toast.makeText(context, "图片下载失败", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            KLog.e("图片____yicunzai");
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
        }
    }

    private static String splitrUrl(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        } else {
            int i = s.lastIndexOf("/");
            if(i>0){
                return s.substring(i);
            }else {
                return s;
            }
        }
    }

}
