package com.southernbox.swipemenulayout.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.southernbox.swipemenulayout.R;
import com.southernbox.swipemenulayout.entity.Entity;
import com.southernbox.swipemenulayout.widget.SwipeDeleteLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SouthernBox on 2017/3/7 0007.
 * 主页列表适配器
 */

public class MainAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<Entity> mList;

    public static ArrayList<SwipeDeleteLayout> mOpenItems = new ArrayList<>();

    public MainAdapter(Context context, List<Entity> List) {
        this.mContext = context;
        this.mList = List;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_swipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;

        final SwipeDeleteLayout layout = viewHolder.swipeDeleteLayout;

        layout.close(false);

        viewHolder.tvName.setText(mList.get(position).getName());

        viewHolder.vDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                mList.remove(position);
                if (mOpenItems.contains(layout)) {
                    mOpenItems.remove(layout);
                }
                notifyItemRemoved(position);
                if (position != mList.size()) {
                    notifyItemRangeChanged(position, mList.size() - position);
                }
            }
        });

        layout.setOnDragStateChangeListener(new SwipeDeleteLayout.OnDragStateChangeListener() {

            @Override
            public void onDragging() {

            }

            @Override
            public void onStartOpen(SwipeDeleteLayout layout) {
                closeAll(layout);
                if (!mOpenItems.contains(layout)) {
                    mOpenItems.add(layout);
                }
            }

            @Override
            public void onViewReleased(SwipeDeleteLayout layout) {

            }

            @Override
            public void onClose(SwipeDeleteLayout layout) {
                if (mOpenItems.contains(layout)) {
                    mOpenItems.remove(layout);
                }
            }

            @Override
            public void onOpen(SwipeDeleteLayout layout) {
                closeAll(layout);
                if (!mOpenItems.contains(layout)) {
                    mOpenItems.add(layout);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static void closeAll() {
        for (SwipeDeleteLayout layout : mOpenItems) {
            layout.close();
        }
    }

    private void closeAll(SwipeDeleteLayout unCloseLayout) {
        for (SwipeDeleteLayout layout : mOpenItems) {
            if (layout != unCloseLayout) {
                layout.close();
            }
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        SwipeDeleteLayout swipeDeleteLayout;
        TextView tvName;
        View vDelete;

        ViewHolder(View itemView) {
            super(itemView);
            swipeDeleteLayout = (SwipeDeleteLayout) itemView.findViewById(R.id.swipe_delete_layout);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            vDelete = itemView.findViewById(R.id.tv_delete);
        }
    }
}
