package com.example.awarex.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.awarex.Model.TvShow;
import com.example.awarex.MySingleton;
import com.example.awarex.R;

import java.util.ArrayList;

public class TvShowAdapter extends RecyclerView.Adapter<TvShowAdapter.ViewHolder> implements Filterable {

    private TextView showName, airingOn;
    private NetworkImageView tvShowImage;
    private ArrayList<TvShow> tvShowList;
    private ArrayList<TvShow> tvShowListFull;
    private ImageLoader imageLoader;
    private Activity activity;


    //Constructor for adapter
    public TvShowAdapter(ArrayList<TvShow> tvShowList,ArrayList<TvShow> tvShowListFull, Activity activity) {
        this.tvShowList = tvShowList;
        this.tvShowListFull = tvShowListFull;
        this.activity = activity;
        imageLoader = MySingleton.getInstance(this.activity).getImageLoader();
    }

    //View creation for each row
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tv_show_item, viewGroup, false);
        showName = v.findViewById(R.id.showName);
        airingOn = v.findViewById(R.id.airingOn);
        tvShowImage = v.findViewById(R.id.tvShowImage);
        return new ViewHolder(v, showName, airingOn, tvShowImage);
    }

    @Override
    public void onBindViewHolder(@NonNull TvShowAdapter.ViewHolder holder, int position) {
        holder.showName.setText(tvShowList.get(position).name);
        holder.airingOn.setText(tvShowList.get(position).air);
        holder.tvShowImage.setImageUrl(tvShowList.get(position).img, imageLoader);
    }

    @Override
    public int getItemCount() {
        return tvShowList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView showName, airingOn;
        private NetworkImageView tvShowImage;


        public ViewHolder(View itemView, TextView showName, TextView airingOn, NetworkImageView tvShowImage) {
            super(itemView);
            this.showName = showName;
            this.airingOn = airingOn;
            this.tvShowImage = tvShowImage;
        }
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    //Search function depending on search characters by managing 2 different lists- one
    // for maintaining original list and other for temporary list
    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<TvShow> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length()==0)
            {
                filteredList.addAll(tvShowListFull);
            } else {
                String searchQuery = constraint.toString().toLowerCase().trim();

                for (TvShow show : tvShowListFull)
                {
                    if (show.getName().toLowerCase().contains(searchQuery))
                    {
                        filteredList.add(show);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            tvShowList.clear();
            tvShowList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };
}
