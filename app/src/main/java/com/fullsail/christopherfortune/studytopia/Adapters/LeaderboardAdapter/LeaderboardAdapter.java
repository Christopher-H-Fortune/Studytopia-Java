package com.fullsail.christopherfortune.studytopia.Adapters.LeaderboardAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.DataModels.Leaderboard.Leaderboard;
import com.fullsail.christopherfortune.studytopia.R;
import com.loopj.android.image.SmartImageView;

import java.util.ArrayList;

public class LeaderboardAdapter extends ArrayAdapter<Leaderboard> {

    private final Context context;
    private final int resource;
    private final ArrayList<Leaderboard> leaderboardArrayList;

    public LeaderboardAdapter(Context context, int resource, ArrayList<Leaderboard> leaderboardArrayList) {
        super(context, resource, leaderboardArrayList);
        this.context = context;
        this.resource = resource;
        this.leaderboardArrayList = leaderboardArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View leaderboardRowView = layoutInflater.inflate(R.layout.learderboard_row, null);

        TextView usersPosition = leaderboardRowView.findViewById(R.id.leardboard_placement_txt_vw);
        SmartImageView profileImage = leaderboardRowView.findViewById(R.id.leaderboard_profile_image);
        TextView userNameTextView = leaderboardRowView.findViewById(R.id.learboard_username_txt_vw);
        TextView testTimeTextView = leaderboardRowView.findViewById(R.id.leaderboard_test_time_txt_vw);

        String usersPostionString = Integer.toString(position + 1);

        usersPosition.setText(usersPostionString + ".)");
        profileImage.setImageUrl(leaderboardArrayList.get(position).getUsersProfileImageUrl());
        userNameTextView.setText(leaderboardArrayList.get(position).getUsersUsername());

        String timeInMinutesString = Long.toString(leaderboardArrayList.get(position).getTimeInMinutes());
        String timeInSecondsString = Long.toString(leaderboardArrayList.get(position).getTimeInSeconds());

        testTimeTextView.setText(timeInMinutesString + "m " + timeInSecondsString + "s");
        return leaderboardRowView;
    }
}
