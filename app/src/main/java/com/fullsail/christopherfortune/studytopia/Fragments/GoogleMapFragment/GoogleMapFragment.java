package com.fullsail.christopherfortune.studytopia.Fragments.GoogleMapFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.R;
import com.fullsail.christopherfortune.studytopia.DataModels.StudyGroup.StudyGroup;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class GoogleMapFragment extends MapFragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    // Tag String to reference when displaying the Fragment
    public static final String TAG = "MapFragment.TAG";

    // PhotoMapInterface variable to call the interface methods
    private GoogleMapInterface googleMapListener;

    // GoogleMap object to manipulate the camera and display the data on the Map
    private GoogleMap studyGroupGoogleMap;

    public static GoogleMapFragment newInstance(){
        return new GoogleMapFragment();
    }

    public interface GoogleMapInterface{
        void passMap(GoogleMap googleMap);
        void displayDetailFragment(Marker markerChosen);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof GoogleMapInterface){
            googleMapListener = (GoogleMapInterface)context;
        }
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        getMapAsync(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.study_group_map_menu, menu);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        View studyGroupInfoWindowView = LayoutInflater.from(getActivity()).inflate(R.layout.google_map_info_window, null);

        ((TextView)studyGroupInfoWindowView.findViewById(R.id.study_group_name_info_wndw_txt_vw)).setText(marker.getTitle());
        ((TextView)studyGroupInfoWindowView.findViewById(R.id.study_group_address_info_wndw_txt_vw)).setText(marker.getSnippet());

        return studyGroupInfoWindowView;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        googleMapListener.displayDetailFragment(marker);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        studyGroupGoogleMap = googleMap;

        studyGroupGoogleMap.setInfoWindowAdapter(this);
        studyGroupGoogleMap.setOnInfoWindowClickListener(this);

        googleMapListener.passMap(googleMap);

        zoomToCurrentLocation();

        addStudyGroupMapMarkers();
    }

    private void zoomToCurrentLocation(){

        if(studyGroupGoogleMap == null){
            return;
        }

        StudyGroupMapActivity studyGroupMapActivity = (StudyGroupMapActivity)getActivity();

        if(studyGroupMapActivity.lastKnownLocation != null){

            Double latitude = studyGroupMapActivity.lastKnownLocation.getLatitude();
            Double longitude = studyGroupMapActivity.lastKnownLocation.getLongitude();

            LatLng lastKnownUserLocation = new LatLng(latitude, longitude);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lastKnownUserLocation, 12);
            studyGroupGoogleMap.animateCamera(cameraUpdate);
        }
    }
    public void addStudyGroupMapMarkers(){

        if(studyGroupGoogleMap == null){
            return;
        }

        StudyGroupMapActivity studyGroupMapActivity = (StudyGroupMapActivity)getActivity();

        if(studyGroupMapActivity.lastKnownLocation != null){

            ArrayList<StudyGroup> studyGroupArrayList = studyGroupMapActivity.studyGroupsArrayList;

            if(studyGroupArrayList != null){

                MarkerOptions markerOptions = new MarkerOptions();

                studyGroupGoogleMap.clear();

                for(StudyGroup studyGroup: studyGroupArrayList){

                    markerOptions.title(studyGroup.getStudyGroupName());

                    markerOptions.snippet(studyGroup.getStudyGroupCompleteAddress());

                    LatLng studyGroupLocation = new LatLng(studyGroup.getStudyGroupLat(), studyGroup.getStudyGroupLong());

                    markerOptions.position(studyGroupLocation);

                    studyGroupGoogleMap.addMarker(markerOptions);
                }
            }
        }

    }

}
