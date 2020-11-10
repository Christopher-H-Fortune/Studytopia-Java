package com.fullsail.christopherfortune.studytopia.Adapters.AttendeeListAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.fullsail.christopherfortune.studytopia.DataModels.Attendees.Attendees;
import com.fullsail.christopherfortune.studytopia.R;
import com.loopj.android.image.SmartImageView;

import java.util.ArrayList;

public class AttendeeListAdapter extends ArrayAdapter<Attendees> {

    private final Context context;
    private final int resource;
    private final ArrayList<Attendees> attendeesArrayListArrayList;

    public AttendeeListAdapter(Context context, int resource, ArrayList<Attendees> attendeesArrayListArrayList) {
        super(context, resource, attendeesArrayListArrayList);
        this.context = context;
        this.resource = resource;
        this.attendeesArrayListArrayList = attendeesArrayListArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View attendeeRowView = layoutInflater.inflate(R.layout.attendee_list_row, null);

        SmartImageView userProfileImageSmrtImg = attendeeRowView.findViewById(R.id.user_profile_image_row_smart_img);
        userProfileImageSmrtImg.setImageUrl(attendeesArrayListArrayList.get(position).getUserProfileImage());

        TextView usernameTxtVw = attendeeRowView.findViewById(R.id.attendee_username_row_txt_vw);
        usernameTxtVw.setText(attendeesArrayListArrayList.get(position).getUsername());

        return attendeeRowView;
    }
}
