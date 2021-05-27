package com.a403.mars;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ItemView extends LinearLayout {

    ImageView myImage;
    TextView myText1;
    TextView myText2;

    public ItemView(Context context, ItemDataBox aItem) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
        inflater.inflate(R.layout.listlayout, this, true);

        myImage = (ImageView) findViewById(R.id.imageItem);
        String temp3 = aItem.getData(2);
        Glide.with(this).load(Uri.parse(temp3)).into(myImage);

        myText1 = (TextView) findViewById(R.id.dataItem01);
        String temp1 = aItem.getData(0);
        myText1.setText(temp1);

        myText2 = (TextView) findViewById(R.id.dataItem02);
        String temp2 = aItem.getData(1);
        myText2.setText(temp2);
    }

    public void setText(int index, String data) {
        if (index == 0) {
            myText1.setText(data);
        } else if (index == 1) {
            myText2.setText(data);
        } else if (index == 2) {
            Glide.with(this).load(Uri.parse(data)).into(myImage);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
