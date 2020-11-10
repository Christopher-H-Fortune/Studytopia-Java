package com.fullsail.christopherfortune.studytopia.Fragments.StudyGroupChosenMapFragment;

import android.os.Bundle;

import com.fullsail.christopherfortune.studytopia.DataModels.StudyGroup.StudyGroup;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupDetailsActivity.StudyGroupDetailsActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class StudyGroupChosenMapFragment extends MapFragment implements OnMapReadyCallback {

    // Tag String to reference when displaying the Fragment
    public static final String TAG = "StudyGroupChosenMapFragment.TAG";

    // GoogleMap object to manipulate the camera and display the data on the Map
    private GoogleMap studyGroupChosenGoogleMap;

    public static StudyGroupChosenMapFragment newInstance(){
        return new StudyGroupChosenMapFragment();
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        studyGroupChosenGoogleMap = googleMap;

        zoomToStudyGroupLocation();

        displayStudyGroupMarker();
    }

    private void zoomToStudyGroupLocation(){

        if(studyGroupChosenGoogleMap != null){

            StudyGroupDetailsActivity studyGroupDetailsActivity = (StudyGroupDetailsActivity) getActivity();

            if(studyGroupDetailsActivity.studyGroupChosen != null){

                StudyGroup studyGroupChosen = studyGroupDetailsActivity.studyGroupChosen;

                Double latitude = studyGroupChosen.getStudyGroupLat();
                Double longitude = studyGroupChosen.getStudyGroupLong();

                LatLng studyGroupLocation = new LatLng(latitude, longitude);

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(studyGroupLocation, 16);

                studyGroupChosenGoogleMap.animateCamera(cameraUpdate);
            }
        }
    }

    private void displayStudyGroupMarker(){

        if(studyGroupChosenGoogleMap != null){

            StudyGroupDetailsActivity studyGroupDetailsActivity = (StudyGroupDetailsActivity) getActivity();

            if(studyGroupDetailsActivity.studyGroupChosen != null){

                MarkerOptions markerOptions = new MarkerOptions();

                StudyGroup studyGroupChosen = studyGroupDetailsActivity.studyGroupChosen;

                markerOptions.title(studyGroupChosen.getStudyGroupName());
                markerOptions.snippet(studyGroupChosen.getStudyGroupCompleteAddress());

                LatLng studyGroupLocation = new LatLng(studyGroupChosen.getStudyGroupLat(), studyGroupChosen.getStudyGroupLong());

                markerOptions.position(studyGroupLocation);

                studyGroupChosenGoogleMap.addMarker(markerOptions);
            }
        }
    }
}
