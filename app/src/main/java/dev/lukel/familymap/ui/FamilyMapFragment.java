package dev.lukel.familymap.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import dev.lukel.familymap.R;

public class FamilyMapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static final String TAG = "FamilyMapFragment";
    private GoogleMap map;
    private MapView mapView;

    public static FamilyMapFragment newInstance() {
        return new FamilyMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View v = layoutInflater.inflate(R.layout.fragment_family_map, viewGroup, false);
        mapView = v.findViewById(R.id.mapview);
        mapView.onCreate(bundle);
        mapView.getMapAsync(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        // add a marker, move camera
        LatLng sydney = new LatLng(-34, 151);
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map.addMarker(new MarkerOptions().position(sydney).title("marker in sydney"));
    }


}
