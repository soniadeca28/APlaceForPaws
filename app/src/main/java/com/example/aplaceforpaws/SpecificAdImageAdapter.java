package com.example.aplaceforpaws;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.method.ScrollingMovementMethod;
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

public class SpecificAdImageAdapter extends RecyclerView.Adapter<SpecificAdImageAdapter.SpecificAdImageViewHolder>
{

    private final Context mContext;
    private final List<Upload> mUploads;

    public SpecificAdImageAdapter(Context mContext, List<Upload>mUploads){
        this.mContext = mContext;
        this.mUploads = mUploads;
    }

    @NonNull
    @Override
    public SpecificAdImageAdapter.SpecificAdImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.spec_ad_pet_image,parent, false);
        return new SpecificAdImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecificAdImageAdapter.SpecificAdImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        holder.petName.setText(uploadCurrent.getPetName());
        holder.petDescription.setText(uploadCurrent.getPetDescription());
        holder.petAge.setText(uploadCurrent.getPetAge());

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

    public static class SpecificAdImageViewHolder extends RecyclerView.ViewHolder
    {
        TextView petName,petDescription,petAge;
        ImageView downloadUrl;
        Button interested;
        public SpecificAdImageViewHolder(@NonNull View itemView) {
            super(itemView);
            petName = itemView.findViewById(R.id.specAdPetName);
            petDescription = itemView.findViewById(R.id.specAdPetDescription);
            petDescription.setMovementMethod(new ScrollingMovementMethod());
            petAge = itemView.findViewById(R.id.specAdPetAge);
            downloadUrl = itemView.findViewById(R.id.specAdPetImage);
            interested = itemView.findViewById(R.id.interested);
        }
    }

}
