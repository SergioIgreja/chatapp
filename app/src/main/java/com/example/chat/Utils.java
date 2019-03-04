package com.example.chat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String getUniqueFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        return imageFileName;
    }
}
