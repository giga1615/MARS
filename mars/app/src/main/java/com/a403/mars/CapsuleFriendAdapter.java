package com.a403.mars;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CapsuleFriendAdapter extends RecyclerView.Adapter<CapsuleFriendAdapter.ViewHolder>{
    ArrayList<CapsuleStory> items = new ArrayList<CapsuleStory>();

    @NonNull
    @Override
    public CapsuleFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_test2, viewGroup, false);

        return new CapsuleFriendAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CapsuleFriendAdapter.ViewHolder viewHolder, int position) {
        CapsuleStory item = items.get(position);
        viewHolder.setItem(item);

        // d-day 잠금
        String minus = "D - ";

        if (viewHolder.open_date.getText().toString().contains(minus)) {
            viewHolder.unlock.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.lock.setVisibility(View.INVISIBLE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = items.get(position).title;
                String created_date = items.get(position).created_date;
                String open_date = items.get(position).open_date;
                String address = items.get(position).address;
                String capsule_friends = items.get(position).capsule_friends;
                double gps_x = items.get(position).gps_x;
                double gps_y = items.get(position).gps_y;
                String jwt = items.get(position).jwt;
                CapsuleStory capsule = new CapsuleStory(title, created_date, open_date, address, capsule_friends, gps_x, gps_y, jwt);

                Context context = v.getContext();
                Intent intent = new Intent(v.getContext(), PlaceActivity.class);
                intent.putExtra("title", capsule.getTitle());
                intent.putExtra("created_date", capsule.getCreated_date());
                intent.putExtra("open_date", capsule.getOpen_date());
                intent.putExtra("address", capsule.getAddress());
                intent.putExtra("capsule_friends", capsule.getCapsule_friends());
                intent.putExtra("gps_x", Double.toString(capsule.getGps_x()));
                intent.putExtra("gps_y", Double.toString(capsule.getGps_y()));
                intent.putExtra("jwt", capsule.getJwt());

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(CapsuleStory item) {
        items.add(item);
    }

    public void setItems(ArrayList<CapsuleStory> items) {
        this.items = items;
    }

    public CapsuleStory getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, CapsuleStory item) {
        items.set(position, item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView created_date;
        TextView open_date;
        TextView address;
        TextView capsule_friends;

        ImageView lock;
        ImageView unlock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            created_date = itemView.findViewById(R.id.create_date);
            open_date = itemView.findViewById(R.id.open_date);
            address = itemView.findViewById(R.id.address);
            capsule_friends = itemView.findViewById(R.id.capsule_friends);

            lock = itemView.findViewById(R.id.lock);
            unlock = itemView.findViewById(R.id.unlock);
        }

        public void setItem(CapsuleStory item) {
            title.setText(item.getTitle());
            created_date.setText(item.getCreated_date());
            open_date.setText(item.getOpen_date());
            address.setText(item.getAddress());
            capsule_friends.setText(item.getCapsule_friends());
        }
    }
}
