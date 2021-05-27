package com.a403.mars;

import android.graphics.drawable.Drawable;

public class ItemDataBox {
    String[] myData;

    public ItemDataBox(String myImage, String text1, String text2) {
        myData = new String[3];
        myData[0] = text1;
        myData[1] = text2;
        myData[2] = myImage;
    }

    public String[] getData() {
        return myData;
    }

    public String getData(int index) {
        if (myData == null || index >= myData.length) {
            return null;
        }
        return myData[index];
    }
}
