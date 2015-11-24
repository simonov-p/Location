package simonov.pk.myapplication;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity  extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<Status> {

    private final String LOG_TAG = "Location Services Lesson 2-1";
    private TextView locationView;
    private TextView latidudeView;
    private TextView longitudeView;
    private TextView timeView;
    private TextView mStatusText;
    private Button requestUpdatesButton;
    private Button removeUpdatesButton;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;

    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        locationView = (TextView) findViewById(R.id.location_view);
//        latidudeView = (TextView) findViewById(R.id.latitude_view);
//        longitudeView = (TextView) findViewById(R.id.longitude_view);
//        timeView = (TextView) findViewById(R.id.time_view);

        mStatusText = (TextView) findViewById(R.id.detectedActivities);
        requestUpdatesButton = (Button) findViewById(R.id.request_activity_updates_button);
        removeUpdatesButton = (Button) findViewById(R.id.remove_activity_updates_button);

        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        // unregister the broadcastreceiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION));

    }

    @Override
    public void onConnected(Bundle bundle) {
//
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(1000); // Update location every second
//
//        LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient,
//                mLocationRequest,
//                this);
    }

    @Override
    public void onConnectionSuspended(int i) {
//        Log.i(LOG_TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.i(LOG_TAG, "GoogleApiClient connection has failed");
    }

    private void fillViews(Location location) {
        Log.i(LOG_TAG, location.toString());
        locationView.setText(location.toString());
        latidudeView.setText(String.valueOf(location.getLatitude()));
        longitudeView.setText(String.valueOf(location.getLongitude()));
        int hour = Calendar.getInstance().get(Calendar.HOUR);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int second = Calendar.getInstance().get(Calendar.SECOND);
        timeView.setText(hour + ":" + minute + ":" + second);
    }

    @Override
    public void onLocationChanged(Location location) {
//        fillViews(location);

//        locationView.setText(Double.toString(location.getLatitude()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.e(LOG_TAG, "Successfully added activity detection.");

        } else {
            Log.e(LOG_TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        protected static final String TAG = "receiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities =
                    intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);
            String strStatus = "";
            for (DetectedActivity thisActivity : updatedActivities){
                strStatus += getActivityString(thisActivity.getType())  + thisActivity.getConfidence() + "%\n";
            }
            mStatusText.setText(strStatus);
        }
    }

    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    public String getActivityString(int detectedActivityType) {
        Resources resources = this.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void requestActivityUpdatesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this,getString(R.string.not_connected),Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
        requestUpdatesButton.setEnabled(false);
        removeUpdatesButton.setEnabled(true);
    }

    public void removeActivityUpdatesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        // Remove all activity updates for the PendingIntent that was used to request activity
        // updates.
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }
}