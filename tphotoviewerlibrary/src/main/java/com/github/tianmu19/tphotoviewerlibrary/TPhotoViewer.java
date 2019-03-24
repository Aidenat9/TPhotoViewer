package com.github.tianmu19.tphotoviewerlibrary;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexvasilkov.gestures.animation.ViewPositionAnimator;
import com.alexvasilkov.gestures.commons.DepthPageTransformer;
import com.alexvasilkov.gestures.commons.RecyclePagerAdapter;
import com.alexvasilkov.gestures.transition.GestureTransitions;
import com.alexvasilkov.gestures.transition.ViewsTransitionAnimator;
import com.alexvasilkov.gestures.transition.tracker.SimpleTracker;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.tianmu19.tphotoviewerlibrary.adapter.PhotoPagerAdapter;
import com.github.tianmu19.tphotoviewerlibrary.adapter.RecyclerAdapter;

import java.util.List;

import me.zhanghai.android.systemuihelper.SystemUiHelper;

/**
 * @author sunwei
 * 邮箱：tianmu19@gmail.com
 * 时间：2019/3/21 21:43
 * 包名：com.github.tianmu19.tphotoviewerlibrary
 * <p>description:    图片浏览工具        </p
 */
public class TPhotoViewer {

    private LayoutInflater inflater;
    private Activity activity;
    private View decorView;
    private PhotoPagerAdapter pagerAdapter;
    private ContentLoadingProgressBar contentLoadingProgressBar;
    private SystemUiHelper mSystemUiHelper;

    public static TPhotoViewer getInstance() {
        return InstanceHolder.ourInstance;
    }

    private static class InstanceHolder {
        private static final TPhotoViewer ourInstance = new TPhotoViewer();
    }


    private TPhotoViewer() {
    }

    /**
     * 在viewpager中展示图片
     *
     * @param context
     * @param recyclerView
     * @param rootViewGroup
     * @param imageUrls
     */
    public ViewsTransitionAnimator<Integer> clickDisplay(@NonNull Context context, @NonNull RecyclerView recyclerView, @NonNull ViewGroup rootViewGroup, @NonNull List<TImageEntity> imageUrls) {
        inflater = LayoutInflater.from(context);
        //1.add viewgroup
        View viewpager_layout = inflater.inflate(R.layout.layout_viewpager_gallery, rootViewGroup, false);
        rootViewGroup.addView(viewpager_layout);
        //得到当前界面的装饰视图
        if (context instanceof Activity) {
            activity = (Activity) context;
            decorView = activity.getWindow().getDecorView();
        }
        //2. init ViewPager
        ViewPager viewPager = viewpager_layout.findViewById(R.id.recycler_pager);
        TextView tvDot = viewpager_layout.findViewById(R.id.tv_dot);
        View background = viewpager_layout.findViewById(R.id.recycler_full_background);

        viewPager.setPageTransformer(true, new DepthPageTransformer());
        pagerAdapter = new PhotoPagerAdapter(viewPager);
        viewPager.setAdapter(pagerAdapter);
        pagerAdapter.setPhotos(imageUrls);
        pagerAdapter.notifyDataSetChanged();
//        ViewPager.SimpleOnPageChangeListener pagerListener = new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//            }
//        };
//        viewPager.addOnPageChangeListener(pagerListener);
        registerSystemUiListener(activity);

        //3.init tracker & anim
        final SimpleTracker gridTracker = new SimpleTracker() {
            @Override
            public View getViewAt(int pos) {
                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForLayoutPosition(pos);
                return holder == null ? null : RecyclerAdapter.getImageView(holder);
            }
        };

        final SimpleTracker pagerTracker = new SimpleTracker() {
            @Override
            public View getViewAt(int pos) {
                RecyclePagerAdapter.ViewHolder holder = pagerAdapter.getViewHolder(pos);
                return holder == null ? null : PhotoPagerAdapter.getImage(holder);
            }
        };

        ViewsTransitionAnimator<Integer> animator = GestureTransitions.from(recyclerView, gridTracker)
                .into(viewPager, pagerTracker);

        animator.addPositionUpdateListener(new ViewPositionAnimator.PositionUpdateListener() {
            @Override
            public void onPositionUpdate(float position, boolean isLeaving) {
                applyImageAnimationState(position, background, tvDot, decorView, activity, isLeaving);
            }
        });
        //3.process anim
        RecyclerAdapter adapter = (RecyclerAdapter) recyclerView.getAdapter();
        adapter.setImageClickListener(new RecyclerAdapter.ImageClickListener() {
            @Override
            public void onClick(int position) {
                if (null != tvDot) {
                    tvDot.setText((position + 1) + "/" + imageUrls.size());
                }
                pagerAdapter.setActivated(true);
                animator.enter(position, true);
                showFullScreen(true);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (null != tvDot) {
                    tvDot.setText((i + 1) + "/" + imageUrls.size());
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        pagerAdapter.setImageClickListener(new PhotoPagerAdapter.ImageClickListener() {
            @Override
            public void onFullImageClick() {
                if (!animator.isLeaving()) {
                    animator.exit(true);
                    showFullScreen(false);
                }
            }
        });
        return animator;
    }

    /**
     * 改变viewpager的背景色
     */
    private void applyImageAnimationState(float position, View background, TextView tvDot
            , View decorView, Activity activity, boolean isLeaving) {
        if (null == decorView || null == activity) {
            return;
        }
        background.setVisibility(position == 0f ? View.INVISIBLE : View.VISIBLE);
        background.setAlpha(position);
        tvDot.setVisibility(position == 0f ? View.INVISIBLE : View.VISIBLE);
        tvDot.setAlpha(position);
        if (isLeaving && position == 0f) {
            if (null != pagerAdapter) pagerAdapter.setActivated(false);
            showFullScreen(false);
        }
    }


    /**
     * ---------------单张图片----
     *
     * @param context       context
     * @param imageView     源图
     * @param rootViewGroup 父布局
     * @param tImageEntity  数据
     * @return ViewsTransitionAnimator
     */
    public ViewsTransitionAnimator clickDisplayOne(@NonNull Context context, @NonNull View imageView, @NonNull ViewGroup rootViewGroup, @NonNull TImageEntity tImageEntity) {
        if (!(imageView instanceof ImageView)) {
            return null;
        }

        inflater = LayoutInflater.from(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
        registerSystemUiListener(activity);
        //1.add group
        View one_gestureview = inflater.inflate(R.layout.layout_one_getstureview, rootViewGroup, false);
        rootViewGroup.addView(one_gestureview);
        //2.init getstureview
        GestureImageView fullImage = one_gestureview.findViewById(R.id.single_image_full);
        contentLoadingProgressBar = one_gestureview.findViewById(R.id.cprogressbar);
        contentLoadingProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.blue_material), PorterDuff.Mode.MULTIPLY);
        hideContentProgress();

        View fullBackground = one_gestureview.findViewById(R.id.single_image_back);

        ImageView image = (ImageView) imageView;
        //3.init anim
        ViewsTransitionAnimator animator = GestureTransitions.from(image).into(fullImage);
        //4.listener
        animator.addPositionUpdateListener(new ViewPositionAnimator.PositionUpdateListener() {
            @Override
            public void onPositionUpdate(float position, boolean isLeaving) {
                fullBackground.setAlpha(position);
                fullBackground.setVisibility(position == 0f && isLeaving ? View.INVISIBLE : View.VISIBLE);
                fullImage.setVisibility(position == 0f && isLeaving ? View.INVISIBLE : View.VISIBLE);
                image.setVisibility(position == 0f && isLeaving ? View.VISIBLE : View.INVISIBLE);
                if (isLeaving && position == 0f) {
                    showFullScreen(false);
                }
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
                showFullScreen(true);
                showOneFullImage();
            }


            private void showOneFullImage() {
                final RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(Target.SIZE_ORIGINAL)
                        .dontTransform();
                showContentProgress();
                Glide.with(context).load(tImageEntity.getOriginUrl())
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
                showFullScreen(false);
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

    private void registerSystemUiListener(Activity activity) {
        mSystemUiHelper = new SystemUiHelper(activity, SystemUiHelper.LEVEL_LOW_PROFILE,
                SystemUiHelper.FLAG_IMMERSIVE_STICKY, new SystemUiHelper.OnVisibilityChangeListener() {
            @Override
            public void onVisibilityChange(boolean visible) {
                if (!visible) {
                    StatusBarUtil.setColor(activity, Color.BLACK);
                } else {
                    StatusBarUtil.setTranslucent(activity);
                }
            }
        });
    }

    /**
     * Shows or hides fullscreen.
     */
    private void showFullScreen(boolean show) {
        if (show) {
            mSystemUiHelper.hide();
        } else {
            mSystemUiHelper.show();
        }
    }

}
