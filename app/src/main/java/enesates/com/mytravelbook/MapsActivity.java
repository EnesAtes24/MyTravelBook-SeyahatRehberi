package enesates.com.mytravelbook;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    // Adresi almak için GoogleMap.OnMapLongClickListener'ı manuel olarak ekliyoruz ve bununla birlikte onMapLongClick metodunu implemente ediyoruz.

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    static SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this); // onMapLongClick metodu altındakilerin gerçekleşebilmesi için bunu yaptık.

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if(info.matches("info")){

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                // SharedPreferences kaydetmemiz gereken tek kelimeik küçük şeyleri kaydedebiliyoruz.
                SharedPreferences sharedPreferences = MapsActivity.this.getSharedPreferences("enesates.com.mytravelbook", MODE_PRIVATE);
                boolean firstTimeCheck = sharedPreferences.getBoolean("noFirstTime", false);

                if(!firstTimeCheck) { // !firstTimeCheck demek firstTimeCheck == false manasına geliyor

                    /* Kullanıcı Map'i açtıktan sonra bir yer ararken sürekli güncellenerek kullanıcının yerini göstermesini engellemek için uygulama ilk açıldığına
                    kullanıcının yeri gösterip sharedPreferences.edit().putBoolean("noFirstTime", true).apply(); ile tekrar tekrar güncellemiyoruz. */

                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    sharedPreferences.edit().putBoolean("noFirstTime", true).apply();

                }

                System.out.println("Location : " + location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(Build.VERSION.SDK_INT >= 23) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Eğer izin yoksa
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                // Aşağıdaki kod kullanıcının konumunu almaya başla demek.
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                mMap.clear();

                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastLocation != null) {
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                }

            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, locationListener);

            mMap.clear();

            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastLocation != null) {
                LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
            }

        }

    } else {
            mMap.clear();
            int position = intent.getIntExtra("position",0);
            LatLng location = new LatLng(MainActivity.locations.get(position).latitude, MainActivity.locations.get(position).longitude);
            String placeName = MainActivity.names.get(position);

            mMap.addMarker(new MarkerOptions().position(location).title(placeName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Kullanıcının izni yoksa izin istedik ve daha sonra izin ALDIKTAN sonra ne olacağını yazıcaz.

        if(grantResults.length >= 0) { // Eğer bize verilen sonuçlar(grantResult) 0 dan büyükse
            if(requestCode == 1) {
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, locationListener);


                    Intent intent = getIntent();
                    String info = intent.getStringExtra("info");

                    if(info.matches("new")) {

                        mMap.clear();
                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(lastLocation != null) {
                            LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                        }

                    } else {
                        mMap.clear();
                        int position = intent.getIntExtra("position",0);
                        LatLng location = new LatLng(MainActivity.locations.get(position).latitude, MainActivity.locations.get(position).longitude);
                        String placeName = MainActivity.names.get(position);

                        mMap.addMarker(new MarkerOptions().position(location).title(placeName));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
                    }

                }
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if(addressList != null && addressList.size() > 0) {
                if(addressList.get(0).getThoroughfare() != null){ // .get(0) çünkü maxResult'ı 1 yani tek değer dönmesini istedik.
                    address += addressList.get(0).getThoroughfare();

                    if(addressList.get(0).getSubThoroughfare() != null) {
                        address += addressList.get(0).getSubThoroughfare();
                    }
                } else {
                    address = "New Place(No Address)";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.addMarker(new MarkerOptions().title(address).position(latLng)); // Eklemeyi yaptık
        Toast.makeText(getApplicationContext(), "New Place OK!", Toast.LENGTH_SHORT).show(); // Kullanıcıya Gösterdik

        MainActivity.names.add(address);
        MainActivity.locations.add(latLng);
        MainActivity.arrayAdapter.notifyDataSetChanged();// Bu kod yukarıdaki kodları kullanarak yeni eklemeler yaptım güncelleme yap diyor. (Bu kod güncelleme yapar.)


        try {

            Double l1 = latLng.latitude;
            Double l2 = latLng.longitude;

            String coord1 = l1.toString();
            String coord2 = l2.toString();

            database = this.openOrCreateDatabase("Places", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS places (name VARCHAR, latitude VARCHAR, longitude VARCHAR)");

            String toCompile = "INSERT INTO places(name, latitude, longitude) VALUES(?, ?, ?)";

            // ?'lerine değer atayabilmek için statement oluşturucaz.
            SQLiteStatement sqLiteStatement = database.compileStatement(toCompile);

            sqLiteStatement.bindString(1, address); // 1. ? adress atadık.
            sqLiteStatement.bindString(2, coord1); // 2. ? coord1
            sqLiteStatement.bindString(3, coord2); // 3. ? coord2

            sqLiteStatement.execute();

        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
