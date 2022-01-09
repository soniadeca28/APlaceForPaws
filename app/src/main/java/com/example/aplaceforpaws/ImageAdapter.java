package com.example.aplaceforpaws;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{

    private Context mContext;
    private List<Upload> mUploads;
    private StorageReference storageReference;
    private String filename;
    private String imgName;
    private String imgExtension;
    private String[] imgParts;
    private List<Upload> mUploadsAux;


    public ImageAdapter(Context mContext, List<Upload> mUploads) {
        this.mContext = mContext;
        this.mUploads = mUploads;
        this.mUploadsAux=mUploads;
    }

    @NonNull
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.pet_image, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        holder.petName.setText(uploadCurrent.getPetName());
        holder.petDescription.setText(uploadCurrent.getPetDescription());
        holder.petAge.setText(uploadCurrent.getPetAge());

        filename = uploadCurrent.getImgName();
        imgParts = filename.split("\\.");
        imgName = imgParts[0];
        imgExtension = imgParts[1];

        storageReference = FirebaseStorage.getInstance().getReference().child("uploads/" + filename);
        try {
            final File localFile = File.createTempFile(imgName, imgExtension);
            storageReference.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(mContext, "o mer", Toast.LENGTH_SHORT).show();
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        holder.downloadUrl.setImageBitmap(bitmap);

                    }).addOnFailureListener(e -> Toast.makeText(mContext, "kuru", Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView petName, petDescription, petAge;
        ImageView downloadUrl;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            petName = itemView.findViewById(R.id.pet_name);
            downloadUrl = itemView.findViewById(R.id.imageViewPet);
            petDescription = itemView.findViewById(R.id.petDescription);
            petAge = itemView.findViewById(R.id.petAge);

        }
    }


    }