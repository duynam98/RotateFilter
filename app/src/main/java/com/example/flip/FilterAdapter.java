package com.example.flip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zomato.photofilters.imageprocessors.Filter;

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder> {

    private Context context;
    private List<ThumbnailItem> thumbnailItemList;
    private int selection_index = 0;
    private OnClickFilter onClickFilter;

    public void setOnClickFilter(OnClickFilter onClickFilter) {
        this.onClickFilter = onClickFilter;
    }

    public FilterAdapter(Context context, List<ThumbnailItem> thumbnailItemList) {
        this.context = context;
        this.thumbnailItemList = thumbnailItemList;
    }

    @NonNull
    @Override
    public FilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.thumb_list_item, parent, false);
        return new FilterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FilterHolder holder, final int position) {
        final ThumbnailItem thumbnailItem = thumbnailItemList.get(position);
        holder.img_filter.setImageBitmap(thumbnailItem.image);
        holder.img_filter.setScaleType(ImageView.ScaleType.FIT_START);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickFilter != null){
                    onClickFilter.onClickFilter(thumbnailItem.filter);
                }
                selection_index = position;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (thumbnailItemList == null){
            return 0;
        }
        return thumbnailItemList.size();
    }

    public class FilterHolder extends RecyclerView.ViewHolder {
        public ImageView img_filter;
        public FilterHolder(@NonNull View itemView) {
            super(itemView);
            img_filter = itemView.findViewById(R.id.thumbnail);
        }
    }

    public interface OnClickFilter{
        void onClickFilter(Filter filter);
    }

}
