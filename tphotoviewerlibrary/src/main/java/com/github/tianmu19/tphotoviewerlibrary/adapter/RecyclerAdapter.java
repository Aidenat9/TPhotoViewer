package com.github.tianmu19.tphotoviewerlibrary.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alexvasilkov.gestures.utils.GlideHelper;
import com.github.tianmu19.tphotoviewerlibrary.R;
import com.github.tianmu19.tphotoviewerlibrary.TImageEntity;
import com.klogutil.KLog;

import java.util.List;

/**
 * @author sunwei
 * 邮箱：tianmu19@gmail.com
 * 时间：2019/1/28 23:10
 * 包名：com.github.tianmu19.tphotoviewer
 * <p>description:            </p>
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewholder> {
    private List<TImageEntity> imageUrls;
    private ImageClickListener listener;

    public RecyclerAdapter(List<TImageEntity> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setImageClickListener(ImageClickListener listener2) {
        this.listener = listener2;
    }

    public interface ImageClickListener {
        void onClick(int position);
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv, viewGroup, false);
        MyViewholder myViewholder = new MyViewholder(inflate);

        return myViewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder myViewholder, int i) {
        myViewholder.imageView.setOnClickListener(v -> {
            if (null != listener) {
                listener.onClick(i);
            }
        });        KLog.e("holder :"+i);

        GlideHelper.loadThumb(myViewholder.imageView, myViewholder.imageView.getWidth(),myViewholder.imageView.getHeight(), imageUrls.get(i).getThumbUrl(), 20);
    }



    public static ImageView getImageView(RecyclerView.ViewHolder holder) {
        if (holder instanceof MyViewholder) {
            return ((MyViewholder) holder).imageView;
        } else {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls == null ? 0 : imageUrls.size();
    }

    static class MyViewholder extends RecyclerView.ViewHolder {
        ImageView imageView = null;

        public MyViewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
