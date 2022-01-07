package com.example.aplaceforpaws;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MyAdsImageAdapter extends RecyclerView.Adapter<MyAdsImageAdapter.MyAdsImageViewHolder> {

    private final Context mContext;
    private final List<Upload> mUploads;

    public MyAdsImageAdapter(Context mContext, List<Upload> mUploads) {
        this.mContext = mContext;
        this.mUploads = mUploads;
    }

    @NonNull
    @Override
    public MyAdsImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.pet_image_my_ads, parent, false);
            return new MyAdsImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdsImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);

            holder.petName.setText(uploadCurrent.getPetName());

        String filename = uploadCurrent.getImgName();
        String[] imgParts = filename.split("\\.");
        String imgName = imgParts[0];
        String imgExtension = imgParts[1];

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("uploads/" + filename);
            try {
                final File localFile = File.createTempFile(imgName, imgExtension);
                storageReference.getFile(localFile)
                        .addOnSuccessListener(taskSnapshot -> {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            holder.downloadUrl.setImageBitmap(bitmap);

                        }).addOnFailureListener(e -> Toast.makeText(mContext, "Error loading some of the images\n", Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public static class MyAdsImageViewHolder extends RecyclerView.ViewHolder {
        TextView petName;
        ImageView downloadUrl;
        Button myAdButton;
        private final Context context;
        public MyAdsImageViewHolder(@NonNull View itemView) {
            super(itemView);
            petName = itemView.findViewById(R.id.pet_name_my_ads);
            downloadUrl = itemView.findViewById(R.id.myAdsImageViewPet);
            myAdButton = itemView.findViewById(R.id.seeAd);
            context = itemView.getContext();
            myAdButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, SeeMyAdActivity.class);
                context.startActivity(intent);
            });
        }
    }


}
