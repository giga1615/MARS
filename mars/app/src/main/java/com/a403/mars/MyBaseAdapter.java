package com.a403.mars;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyBaseAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<ItemDataBox> mItems = new ArrayList<ItemDataBox>();

    public MyBaseAdapter(Context context) {
        mContext = context;
    }

    public void addItem(ItemDataBox item) {
        mItems.add(item);
    }

    public int getCount() {
        return mItems.size();
    }

    public Object getItem(int position) {
        return mItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ItemView dataView;
        if(convertView == null) {
            dataView = new ItemView(mContext, mItems.get(position));
        }
        else {
            dataView = (ItemView) convertView;
            dataView.setText(0, mItems.get(position).getData(0));
            dataView.setText(1, mItems.get(position).getData(1));
            dataView.setText(2, mItems.get(position).getData(2));
        }
        return dataView;
    }
}