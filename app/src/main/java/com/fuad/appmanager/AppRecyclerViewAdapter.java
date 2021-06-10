package com.fuad.appmanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AppRecyclerViewAdapter extends RecyclerView.Adapter<AppRecyclerViewAdapter.AppViewHolder> implements Filterable {

    private ArrayList<AppItem> appList;
    private ArrayList<AppItem> filteredList;

    public boolean isSelectMode = false;

    public AppRecyclerViewAdapter(ArrayList<AppItem> appList) {
        this.appList = appList;
        this.filteredList = appList;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item_layout, parent,false);
        return new AppViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppItem model = filteredList.get(position);

        holder.appIcon.setImageDrawable(model.getAppIcon());
        holder.appName.setText(model.getAppName());
        holder.packageName.setText(model.getAppPackageName());
//        holder.packageName.setText(String.format("%.2f",model.getAppSize()/1000000.0));
//        holder.version.setText(model.getAppVersion());

        if(model.isSelected){
//            holder.itemView.setBackgroundResource(R.color.gray);
            holder.selectedView.setVisibility(View.VISIBLE);
            holder.selectedLayout.setVisibility(View.VISIBLE);
        } else {
//            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.selectedView.setVisibility(View.INVISIBLE);
            holder.selectedLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String key = constraint.toString();

                if(key.isEmpty()){
                    filteredList = appList;
                } else {
                    ArrayList<AppItem> tempList = new ArrayList<>();
                    for(AppItem item : appList){
                        if(item.getAppName().toLowerCase().contains(key.toLowerCase())){
                            tempList.add(item);
                        }
                    }
                    filteredList = tempList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<AppItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class AppViewHolder extends RecyclerView.ViewHolder{
        ImageView appIcon;
        TextView appName, packageName, version;
        ImageView selectedView;
        LinearLayout selectedLayout;


        public AppViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            packageName = itemView.findViewById(R.id.package_name);
            selectedLayout = itemView.findViewById(R.id.selected_layout);
            selectedView = itemView.findViewById(R.id.selected_view);
//            version = itemView.findViewById(R.id.app_version);

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemLongClick(position,itemView);
                    }
                }
                return true;
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position,itemView);
                    }
                }
            });
        }
    }

    //Custom RecyclerView On Click Listener
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position, View view);
        void onItemLongClick(int position, View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
