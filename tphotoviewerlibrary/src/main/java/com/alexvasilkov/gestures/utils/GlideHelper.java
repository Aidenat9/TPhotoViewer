package com.alexvasilkov.gestures.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.tianmu19.tphotoviewerlibrary.TImgBean;
import com.klogutil.KLog;

public class GlideHelper {

    private static RequestOptions OPTIONS = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(Target.SIZE_ORIGINAL)
            .dontAnimate()
            .dontTransform();

    private GlideHelper() {
    }

    public static void loadThumb(@NonNull ImageView imageView,@NonNull int width,@NonNull int height, @NonNull String photoUrl, int roudCorner) {
        loadThumb(imageView,width,height,photoUrl,roudCorner,-1);
    }
    public static void loadThumb(@NonNull ImageView imageView,@NonNull int width,@NonNull int height, @NonNull String photoUrl, int roudCorner,int placeholder) {

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(width,height)
                .transform(new RoundedCorners(roudCorner)).dontAnimate();
        if(-1!=placeholder){
            options = options.placeholder(placeholder).error(placeholder);
        }
        Glide.with(imageView.getContext())
                .load(photoUrl)
                .apply(options)
                .thumbnail(THUMBNAIL)
                .into(imageView);
    }

    public static void loadFull(@NonNull TImgBean photo, @NonNull ImageView image, @NonNull LoadingListener listener) {
        loadFull(photo,image,-1,listener);
    }
    public static void loadFull(@NonNull TImgBean photo, @NonNull ImageView image,int placeholder, @NonNull LoadingListener listener) {
        final String photoUrl = photo.getOriginUrl() == null
                ? photo.getThumbUrl() : photo.getOriginUrl();
        RequestOptions options = OPTIONS;
        final RequestBuilder<Drawable> thumbRequest = Glide.with(image.getContext())
                .load(photo.getThumbUrl()).thumbnail(THUMBNAIL)
                .apply(options);
        if(-1!=placeholder){
            options = options.placeholder(placeholder).error(placeholder);
        }
        Glide.with(image)
                .load(photoUrl)
                .apply(new RequestOptions().apply(options).placeholder(image.getDrawable()))
                .thumbnail(thumbRequest)
                .listener(new RequestListenerWrapper<>(listener))
                .into(image);
    }

    private final static float THUMBNAIL = 0.5F;

    public static void clear(@NonNull ImageView view) {
        try {
            // Clearing current Glide request (if any)
            Glide.with(view.getContext()).clear(view);
            // Cleaning up resources
            view.setImageDrawable(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface LoadingListener {
        void onSuccess();

        void onError();
    }

    private static class RequestListenerWrapper<T> implements RequestListener<T> {

        private final LoadingListener listener;
        RequestListenerWrapper(@Nullable LoadingListener listener) {
            this.listener = listener;
        }
        @Override
        public boolean onResourceReady(T resource, Object model, Target<T> target,
                                       DataSource dataSource, boolean isFirstResource) {
            if (listener != null) {
                listener.onSuccess();
            }
            KLog.e( "onResourceReady: ");
            return false;
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException ex, Object model,
                                    Target<T> target, boolean isFirstResource) {
            if (listener != null) {
                listener.onError();
            }
            return false;
        }
    }

}
