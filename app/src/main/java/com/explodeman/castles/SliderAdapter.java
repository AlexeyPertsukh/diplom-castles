package com.explodeman.castles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.explodeman.castles.models.FirebaseImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.ViewHolder> {

    private final List<FirebaseImage> list;
    private final Context context;

    private final StorageReference storageReference;

    public SliderAdapter(Context context, List<FirebaseImage> list) {
        this.context = context;
        this.list = list;

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_adapter_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseImage img = list.get(position);
        if (img.isNull()) {
            hideProgressBar(holder);
            setPlug(holder);
        } else {
            setFromFirebase(holder, position, img.filename);
        }
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivSliderCastle;
        private final TextView tvSliderPosition;
        private final ProgressBar pbLoad;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSliderCastle = itemView.findViewById(R.id.ivSliderCastle);
            tvSliderPosition = itemView.findViewById(R.id.tvSliderPosition);
            pbLoad = itemView.findViewById(R.id.pbSloderLoad);
        }
    }

    private void setFromFirebase(@NonNull ViewHolder holder, int position, String filename) {
        StorageReference pathReference = storageReference.child(filename);
        @SuppressLint("DefaultLocale")
        String sPos = String.format("%d/%d", (position + 1), getItemCount());

        holder.tvSliderPosition.setText(sPos);

        showProgressBar(holder);
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                hideProgressBar(holder);
                Picasso.with(context)
                        .load(uri)
                        .placeholder(R.drawable.castle_plug_light)
                        .error(R.drawable.castle_plug_light)
                        .into(holder.ivSliderCastle);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                hideProgressBar(holder);
                setPlug(holder);
            }
        });
    }

    private void setPlug(@NonNull ViewHolder holder) {
        holder.ivSliderCastle.setImageResource(R.drawable.castle_plug_light);
    }


    private void showProgressBar(@NonNull ViewHolder holder) {
        holder.pbLoad.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(@NonNull ViewHolder holder) {
        holder.pbLoad.setVisibility(View.GONE);
    }

}
