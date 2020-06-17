package com.cgitsoft.convertgeneration.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.cgitsoft.convertgeneration.Fragments.QrFragment;
import com.cgitsoft.convertgeneration.R;
import com.cgitsoft.convertgeneration.activities.AttendanceDetailActivity;
import com.cgitsoft.convertgeneration.activities.ProfileActivity;
import com.cgitsoft.convertgeneration.activities.QrActivity;
import com.cgitsoft.convertgeneration.activities.QrActivity;
import com.cgitsoft.convertgeneration.models.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class HomeFragment extends Fragment {

    private final static String TAG = HomeFragment.class.getSimpleName();

    private View v;
    private Context context;
    private static final int LocationPermissionCode = 2010;
    private boolean mLocationPermissionGranted = false;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9001;
    private FusedLocationProviderClient providerClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        providerClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        v = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        init();
        return v;
    }

    private void init() {
        CardView cardQR = v.findViewById(R.id.card_scanQR);
        CardView cardAttendance = v.findViewById(R.id.card_attendance);
        CardView viewAttendance = v.findViewById(R.id.viewAttendance);
        CardView viewProfile = v.findViewById(R.id.card_profile);

        if (!Utils.isAdmin(requireContext())) {
            cardAttendance.setVisibility(View.GONE);
        } else {
            cardAttendance.setVisibility(View.VISIBLE);
            viewAttendance.setVisibility(View.GONE);
        }
        cardAttendance.setOnClickListener(click -> {
            Intent intent = new Intent(context, AttendanceDetailActivity.class);
            startActivity(intent);
        });

        viewAttendance.setOnClickListener(click -> {
            Intent intent = new Intent(context, AttendanceDetailActivity.class);
            startActivity(intent);
        });

        viewProfile.setOnClickListener(click -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            startActivity(intent);
        });


        cardQR.setOnClickListener(Qr -> {
            if (Utils.isAdmin(requireContext())) {
                Intent intent = new Intent(context, QrActivity.class);
                startActivity(intent);
            } else {
                checkLocationPermission();
            }

        });
    }

    private void getDashboard() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        providerClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null) {
                Intent intent = new Intent(requireContext(), QrActivity.class);
                intent.putExtra("lat", String.valueOf(location.getLatitude()));
                intent.putExtra("lng", String.valueOf(location.getLongitude()));
                startActivity(intent);
            } else {
                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(4000);
                locationRequest.setFastestInterval(2000);
                providerClient.requestLocationUpdates(locationRequest,
                        new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                Location location1 = locationResult.getLastLocation();
                                if (location1 != null) {
                                    Intent intent = new Intent(requireContext(), QrActivity.class);
                                    intent.putExtra("lat", String.valueOf(location1.getLatitude()));
                                    intent.putExtra("lng", String.valueOf(location1.getLongitude()));
                                    startActivity(intent);
                                    providerClient.removeLocationUpdates(this);
                                } else {
                                    Toast.makeText(context, "No location found", Toast.LENGTH_SHORT).show();
                                    providerClient.removeLocationUpdates(this);
                                }
                            }
                        }, Looper.myLooper());
            }
        });
    }

    private boolean checkMapServices(){
        return isMapsEnabled();
    }

    private void checkLocationPermission() {
        if(checkMapServices())
        {
            if(mLocationPermissionGranted){
                getDashboard();
            }else {
                getLocationPermission();
            }
        }
    }



    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(requireContext().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getDashboard();

        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LocationPermissionCode);
        }
    }
    private boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) requireContext().getSystemService( Context.LOCATION_SERVICE );

        if ( manager != null && !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == LocationPermissionCode) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        if (requestCode == PERMISSIONS_REQUEST_ENABLE_GPS) {
            if (mLocationPermissionGranted) {
                getDashboard();
            } else {
                getLocationPermission();
            }
        }
    }
}