package com.github.tianmu19.tphotoviewerlibrary.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.alexvasilkov.gestures.commons.RecyclePagerAdapter;
import com.alexvasilkov.gestures.utils.GlideHelper;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.alexvasilkov.gestures.views.Views;
import com.github.tianmu19.tphotoviewerlibrary.R;
import com.github.tianmu19.tphotoviewerlibrary.TImgBean;

import java.util.List;

/**
 * @author sunwei
 * email：tianmu19@gmail.com
 * date：2019/11/8 22:22
 * version：1.0
 * <p>description：viewpager的适配器   </p>
 */

public class PhotoPagerAdapter extends RecyclePagerAdapter<PhotoPagerAdapter.ViewHolder> {

    private final ViewPager viewPager;
    private List<TImgBean> photos;
    private ImageClickListener clickListener;

    private boolean activated;

    public PhotoPagerAdapter(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public void setPhotos(List<TImgBean> photos1) {
        this.photos = photos1;
        notifyDataSetChanged();
    }

    public TImgBean getPhoto(int pos) {
        return photos == null || pos < 0 || pos >= photos.size() ? null : photos.get(pos);
    }

    public void setImageClickListener(ImageClickListener clickListener) {
        this.clickListener = clickListener;
    }

    /**
     * To prevent ViewPager from holding heavy views (with bitmaps)  while it is not showing
     * we may just pretend there are no items in this adapter ("activate" = false).
     * But once we need to run opening animation we should "activate" this adapter again.<br>
     * Adapter is not activated by default.
     */
    public void setActivated(boolean activated) {
        if (this.activated != activated) {
            this.activated = activated;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return !activated || photos == null ? 0 : photos.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup container) {
        final ViewHolder holder = new ViewHolder(container);
        holder.image.setOnClickListener(view -> onImageClick());
        holder.image.getController().enableScrollInViewPager(viewPager);
        holder.image.getPositionAnimator().addPositionUpdateListener((position, isLeaving) ->
                holder.progress.setVisibility(position == 1f ? View.VISIBLE : View.INVISIBLE));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final long PROGRESS_DELAY = 300L;
        holder.progress.animate().setStartDelay(PROGRESS_DELAY).alpha(1f);
        TImgBean photo = photos.get(position);
        // Loading image
        GlideHelper.loadFull(photo, holder.image,R.drawable.img_placeholder, new GlideHelper.LoadingListener() {
            @Override
            public void onSuccess() {
                holder.progress.animate().cancel();
                holder.progress.animate().alpha(0f);
            }
            @Override
            public void onError() {
                holder.progress.animate().alpha(0f);
            }
        });
    }

    @Override
    public void onRecycleViewHolder(@NonNull ViewHolder holder) {
        super.onRecycleViewHolder(holder);
        GlideHelper.clear(holder.image);
        holder.progress.animate().cancel();
        holder.progress.setAlpha(0f);
        holder.image.setImageDrawable(null);
    }

    private void onImageClick() {
        if (clickListener != null) {
            clickListener.onClickFullImage();
        }
    }

    public static GestureImageView getImage(RecyclePagerAdapter.ViewHolder holder) {
        return ((ViewHolder) holder).image;
    }

    static class ViewHolder extends RecyclePagerAdapter.ViewHolder {
        final GestureImageView image;
        final View progress;
        ViewHolder(ViewGroup parent) {
            super(Views.inflate(parent, R.layout.item_viewpager));
            image = itemView.findViewById(R.id.photo_full_image);
            progress = itemView.findViewById(R.id.photo_full_progress);
        }
    }

    public interface ImageClickListener {
        void onClickFullImage();
    }

}
