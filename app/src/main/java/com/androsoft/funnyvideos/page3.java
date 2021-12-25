package com.androsoft.funnyvideos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import static com.androsoft.funnyvideos.MyUtils.counter;
import static com.androsoft.funnyvideos.MyUtils.isOnline;

public class page3 extends AppCompatActivity
        implements
        EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks
        , MainListAdapter.ImageClickListener, MaxAdListener, MaxAdViewAdListener {

    private static final int RC_STORAGE_PERMISSIONS = 123;
    private DatabaseReference mDatabase;
    private RecyclerView rc;
    private TextView tv_warning;
    private boolean isFirstTime = false;
    private String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String url = "";
    private SpinKitView spinKitView;
    private MaxInterstitialAd interstitialAd;
    private MaxAdView adView;
    private int retryAttempt;
    AlertDialog.Builder builder;

    private BroadcastReceiver networkBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isOnline(context)) {
                tv_warning.setVisibility(View.GONE);
                spinKitView.setVisibility(View.VISIBLE);
                isFirstTime = false;

                loadData();
            } else {
                if(isFirstTime) {
                    spinKitView.setVisibility(View.GONE);
                    tv_warning.setVisibility(View.VISIBLE);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);

        isFirstTime = true;
        setContentView(R.layout.recycleview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        spinKitView = findViewById(R.id.spin_kit);
        tv_warning = findViewById(R.id.no_net);
        setSupportActionBar(toolbar);

        //ads start
        adView = new MaxAdView( (getString(R.string.banner_home_footer)), this );
        adView.setListener( this );
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int heightPx = getResources().getDimensionPixelSize( R.dimen.banner_height );
        adView.setLayoutParams( new FrameLayout.LayoutParams( width, heightPx , Gravity.BOTTOM) );
        ViewGroup rootView = findViewById( android.R.id.content );
        rootView.addView( adView );
        adView.loadAd();
        interstitialAd = new MaxInterstitialAd( (getString(R.string.interstitial_full_screen)), this );
        interstitialAd.setListener( this );
        interstitialAd.loadAd();
        //add end






        spinKitView.setVisibility(View.VISIBLE);



        rc = findViewById(R.id.main_list);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("ctg3");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");

        registerReceiver(networkBroadCast, intentFilter);


    }



    private void loadData() {
        final ArrayList<String> urls = new ArrayList<>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    urls.add(dataSnapshot1.getValue().toString());
                }

                rc.setAdapter(new MainListAdapter(page3.this, urls));
                rc.setLayoutManager(new GridLayoutManager(page3.this, 3));
                spinKitView.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError -wait", databaseError.toString());
                spinKitView.setVisibility(View.GONE);

            }
        });
    }






    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!EasyPermissions.hasPermissions(this, perms)) {
            requestPermissions();
        }
        if (!isOnline(this)) {
            if (isFirstTime)
            {
                tv_warning.setVisibility(View.VISIBLE);
            }
        }


    }

    private void requestPermissions() {
        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(this, RC_STORAGE_PERMISSIONS, perms)
                        .build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (!EasyPermissions.hasPermissions(this, this.perms)) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                new AppSettingsDialog.Builder(this).build().show();
            }
            if (EasyPermissions.somePermissionDenied(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions();
            }
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            if (!(resultCode == RESULT_OK)) {
                new AppSettingsDialog.Builder(this).build().show();
            }
        }
    }

    @Override
    public void imageClicked(String url) {
        this.url = url;
        counter++;
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra("image_url", url);
        startActivity(intent);
        if ( interstitialAd.isReady() )
        {
            interstitialAd.showAd();
        }

    }
    // MAX Ad Listener 2nd
    @Override
    public void onAdLoaded(final MaxAd maxAd)
    {
        retryAttempt = 0;
    }
    @Override
    public void onAdLoadFailed(final String adUnitId, final MaxError error)
    {
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                interstitialAd.loadAd();
            }
        }, delayMillis );
    }
    @Override
    public void onAdDisplayFailed(final MaxAd maxAd, final MaxError error)
    {
        interstitialAd.loadAd();
    }
    @Override
    public void onAdDisplayed(final MaxAd maxAd) {}
    @Override
    public void onAdClicked(final MaxAd maxAd) {}
    @Override
    public void onAdHidden(final MaxAd maxAd)
    {
        interstitialAd.loadAd();
    }


    @Override
    public void onAdExpanded(MaxAd ad) {

    }
    @Override
    public void onAdCollapsed(MaxAd ad) {
    }
}
