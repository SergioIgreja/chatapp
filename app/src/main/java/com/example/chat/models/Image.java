package com.example.chat.models;

import android.graphics.Bitmap;
import android.net.Uri;

public class Image {
    private Uri imageUri;
    private Bitmap imageBitmap;

    public Image(Uri imageUri, Bitmap imageBitmap) {
        this.imageUri = imageUri;
        this.imageBitmap = imageBitmap;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
