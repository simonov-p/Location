package simonov.pk.myapplication;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by petr on 24-Nov-15.
 */
public class GeoFenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "gfservice";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GeoFenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
    }
}
