package com.example.voicecraft;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    String userName;
    List<String> date;

    public HistoryRecyclerViewAdapter(Context context,List<String> date, String userName) {
        this.context = context;
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
        holder.usernameTextView.setText(userName);
        String itemDate = date.get(position);

        // Set the dateTextView with the item's date
        holder.dateTextView.setText(itemDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GraphActivity.class);
                intent.putExtra("Date", itemDate);
                intent.putExtra("userName", userName);
                context.startActivity(intent);

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

        public TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.textView_Username);
            dateTextView  = itemView.findViewById(R.id.textView_Date);
        }
    }
}
