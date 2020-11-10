package com.fullsail.christopherfortune.studytopia.Adapters.ForumChannelsAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.fullsail.christopherfortune.studytopia.DataModels.ForumChannel.ForumChannel;
import com.fullsail.christopherfortune.studytopia.R;
import java.util.ArrayList;

public class ForumChannelsAdapter extends ArrayAdapter<ForumChannel> implements Filterable {

    private final Context context;
    private final int resource;
    private final ArrayList<ForumChannel> forumChannelArrayList;
    private  ArrayList<ForumChannel> filteredListForumChannelArrayList;
    private ChannelFilter channelFilter;

    public ForumChannelsAdapter(Context context, int resource, ArrayList<ForumChannel> forumChannelArrayList) {
        super(context, resource, forumChannelArrayList);
        this.context = context;
        this.resource = resource;
        this.forumChannelArrayList = forumChannelArrayList;
        filteredListForumChannelArrayList = forumChannelArrayList;

        getFilter();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View forumChannelRowView = layoutInflater.inflate(R.layout.forum_channel_row, null);

        TextView forumChannelNameTextView = forumChannelRowView.findViewById(R.id.channel_name_txt_vw);

        TextView forumChannelCreatorUsernameTextView = forumChannelRowView.findViewById(R.id.channel_creator_username_txt_vw);

        TextView forumChannelMessageCountTextView = forumChannelRowView.findViewById(R.id.channel_message_count_txt_vw);

        if(position < filteredListForumChannelArrayList.size()){

            forumChannelNameTextView.setText(filteredListForumChannelArrayList.get(position).getChannelName());

            forumChannelCreatorUsernameTextView.setText(context.getResources().getString(R.string.study_group_creator_username, filteredListForumChannelArrayList.get(position).getCreatorUsername()));

            forumChannelMessageCountTextView.setText(context.getResources().getString(R.string.message_count, filteredListForumChannelArrayList.get(position).getTotalMessages()));
        }

        return forumChannelRowView;
    }


    @Override
    public Filter getFilter() {
        if (channelFilter == null) {
            channelFilter = new ChannelFilter();
        }

        return channelFilter;
    }

    private class ChannelFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();

            if(constraint != null && constraint.length() > 0){

                ArrayList<ForumChannel> forumChannelsTempArrayList = new ArrayList<>();

                for(ForumChannel channel : forumChannelArrayList){
                    if(channel.getChannelName().toLowerCase().contains(constraint.toString().toLowerCase())){

                        forumChannelsTempArrayList.add(channel);
                    }
                }

                filterResults.count = forumChannelsTempArrayList.size() - 1;
                filterResults.values = forumChannelsTempArrayList;
                System.out.println(filterResults.values);
            } else {
                filterResults.count = forumChannelArrayList.size();
                filterResults.values = forumChannelArrayList;
            }

            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredListForumChannelArrayList = (ArrayList<ForumChannel>) results.values;
            notifyDataSetChanged();
        }
    }
}

