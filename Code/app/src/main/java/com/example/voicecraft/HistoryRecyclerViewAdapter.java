package com.example.voicecraft;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private SharedPreferences sharedPreferences;
    String userName;
    List<String> date;

    public HistoryRecyclerViewAdapter(List<String> date, String userName) {
        this.date = date;
        this.userName = userName;
    }


    @NonNull
    @Override
    public HistoryRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_cardview_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryRecyclerViewAdapter.ViewHolder holder, int position) {
        //User user = userList.get(position);
        holder.usernameTextView.setText(userName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(context, GraphActivity.class);
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG,context+" nothing after clicking on card");
                    // Log the error or take appropriate actions to handle the exception.
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        if (date != null) {
            return date.size();
        } else {
            return 0; // or handle the null case as appropriate for your application
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.textView_Username);
        }
    }
}
