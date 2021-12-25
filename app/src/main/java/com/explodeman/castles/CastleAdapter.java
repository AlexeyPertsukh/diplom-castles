package com.explodeman.castles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.explodeman.castles.models.Castle;
import com.explodeman.castles.utils.UtilAssets;

import java.io.Serializable;
import java.util.ArrayList;

public class CastleAdapter extends RecyclerView.Adapter<CastleAdapter.ViewHolder> implements Serializable {
    private final ArrayList<Castle> castles;
    private OnClickItem onClickItem;
    private final Context context;

    public CastleAdapter(Context context, ArrayList<Castle> castles) {
        this.context = context;
        this.castles = castles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.castle_adapter_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Castle castle = castles.get(position);
        String directory = castle.getDirectory();
        String id = castle.getId();
        String way = String.format("info/%s/%s/", directory, id);

        holder.tvAdName.setText(castle.getName());
        holder.ivAvatar.setImageDrawable(UtilAssets.getImageDrawable(context, way + id + ".jpg"));
        String description = UtilAssets.readTextFile(context, way + id + ".txt");
        holder.tvAdDescription.setText(description);
    }


    @Override
    public int getItemCount() {
        if (castles == null) {
            return 0;
        } else {
            return castles.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAdName;
        private final TextView tvAdDescription;
        private final ImageView ivAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAdName = itemView.findViewById(R.id.tvAdName);
            tvAdDescription = itemView.findViewById(R.id.tvAdDescription);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);

            itemView.setOnClickListener(this::onClick);
        }

        private void onClick(View view) {
            if (onClickItem != null) {
                Castle castle = castles.get(getAdapterPosition());
                onClickItem.onClickItem(castle);
            }
        }
    }

    public void setOnClickItem(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }

    public interface OnClickItem {
        void onClickItem(Castle castle);
    }

}
