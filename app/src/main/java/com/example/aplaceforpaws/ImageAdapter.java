package com.example.aplaceforpaws;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{

    private Context mContext;
    private List<Upload> mUploads;
    private StorageReference storageReference;
    private ImageView imageViewPet;


    public ImageAdapter(Context mContext, List<Upload> mUploads, StorageReference storageReference) {
        this.mContext = mContext;
        this.mUploads = mUploads;
        this.storageReference = storageReference;
    }

    /*public ImageAdapter(Context context, List<Upload> uploads){
        mContext = context;
        mUploads = uploads;
    }*/

    @NonNull
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.pet_image,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        holder.petName.setText(uploadCurrent.getPetName());


       /* Picasso.with(mContext)
                .load(uploadCurrent.getDownloadUrl())
                .fit()
                .centerInside()
                .into(holder.downloadUrl);*/
        Glide.with(mContext)
                .load(storageReference)
                .into(holder.downloadUrl);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{
        TextView petName;
        ImageView downloadUrl;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            petName = itemView.findViewById(R.id.pet_name);
            downloadUrl = itemView.findViewById(R.id.imageViewPet);



        }
    }
}