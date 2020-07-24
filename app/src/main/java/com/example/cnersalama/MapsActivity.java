package com.example.cnersalama;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    SupportMapFragment mapFragment;
    GoogleMap gMap;
    MarkerOptions markerOptions = new MarkerOptions();
    CameraPosition cameraPosition;
    LatLng center, latLng;
    String title;
    String  acc, kills, injured, srsinjured;
    SearchView searchView;

    //variables to catch data from database
    public static final String ID = "NUM_ROU";
    public static final String TITLE = "LIB_ROU";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String NBR_ACC= "NBR_ACC";
    public static final String NBR_TUE= "NBR_TUE";
    public static final String NBR_BLL= "NBR_BLL";
    public static final String NBR_BLG= "NBR_BLG";



    private static String URL_MARKERS = "https://salamacner.000webhostapp.com/markers.php";

    private String url = "https://wisatademak.dedykuncoro.com/Main/json_wisata";

    String tag_json_obj = "json_obj_req";

    ArrayList<LatLng>arraylist= new ArrayList<LatLng>();

    ArrayList<String> titles = new ArrayList<String>();

    Context context = this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //link to xml View
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        searchView = findViewById(R.id.svlocation);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if(location !=null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    gMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText){
                return false;
            }
        });

        mapFragment.getMapAsync(this);

        //add locations


        //add titles



    }

    // Get JSON Marker
    private void getMarkers() {
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_MARKERS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String getObject = jObj.getString("route");
                    JSONArray jsonArray = new JSONArray(getObject);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        title = jsonObject.getString(TITLE);
                        latLng = new LatLng(Double.parseDouble(jsonObject.getString(LAT)), Double.parseDouble(jsonObject.getString(LNG)));
                        kills = jsonObject.getString(NBR_TUE);
                        acc = jsonObject.getString(NBR_ACC);
                        injured = jsonObject.getString(NBR_BLL);
                        srsinjured = jsonObject.getString(NBR_BLG);


                        addMarker(latLng,title,kills,acc,injured,srsinjured);
                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", error.getMessage());
                Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void addMarker(LatLng latlng, final String title, final String kills,
                           final String acc, final String injured, final String srsinjured ) {
        markerOptions.position(latlng);
        markerOptions.title(title);
        markerOptions.snippet("Nombre d'accidents: " +acc+
				"\nNombre de tués: "+kills+	
				"\nNombre de blessés légers: "+injured+
				"\nNombre de blessés graves: "+srsinjured);
        gMap.addMarker(markerOptions);

        System.out.println("kills1"+kills);

        //add click listener on marker
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this,DetailsActivity.class);
                String markerTitle = marker.getTitle();
                String markerSnippet = marker.getSnippet();
                intent.putExtra("title", markerTitle);
                intent.putExtra("snippet",markerSnippet);
                startActivity(intent);
                return false;
            }

        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;

        // set camera position
        center = new LatLng(34.020882, -6.841650);
        cameraPosition = new CameraPosition.Builder().target(center).zoom(10).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        getMarkers();
    }

}