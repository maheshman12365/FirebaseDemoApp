package com.app.firebasedemoapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    ArrayList<User> mData = new ArrayList<>();

    public UserAdapter(ArrayList<User> allUsersList, Activity activity) {
        mData = new ArrayList<>(allUsersList);

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new UserViewHolder(layoutInflater.inflate(R.layout.row_user, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = mData.get(position);
        holder.parent.setOnClickListener(null);
        holder.title.setText(user.getTitle());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkBox.performClick();
            }
        });
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(user.isIschecked());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mData.get(position).setIschecked(isChecked);
                HomeScreen.updateValueToDB(mData.get(position));
                notifyItemChanged(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        View parent;
        TextView title;
        CheckBox checkBox;

        public UserViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            checkBox = view.findViewById(R.id.cb_user);
            parent = view.findViewById(R.id.parent);
        }
    }
}
