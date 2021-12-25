package com.androsoft.funnyvideos;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.BuildConfig;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.material.navigation.NavigationView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MaxAdListener, MaxAdViewAdListener {
    Button btn1,btn2;

    private MaxInterstitialAd interstitialAd;
    private MaxAdView adView;
    private int retryAttempt;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        //ads start
        AppLovinSdk.getInstance(this).setMediationProvider("max");
        AppLovinSdk.initializeSdk(this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {

            }

        });



        //
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
        // Load the first ad
        interstitialAd.loadAd();
        // first ad end



        btn1=(Button) findViewById(R.id.bt);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ctg.class);
                startActivity(intent);


            }
        });

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.main, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.settings);

        return true;
    }







    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.about) {startActivity(new Intent(this,about.class));
            // Handle the camera action
        } else if (id == R.id.share) {

            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chillbox Apps");
                String shareMessage= "\nCheck Out Best Apps\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Choose One"));
            } catch(Exception e) {

            }




        } else if (id == R.id.feedback) {
            startActivity(new Intent(this, MainActivity.class));


        } else if (id == R.id.moreapp) {
                    Intent intent = new Intent();
                     startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:Chillbox Ltd.")));
                    startActivity(intent);



        } else if (id == R.id.settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Thank You!");
        builder.setMessage("Please Give Us Your Suggestions and Feedback");
        builder.setPositiveButton("QUIT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton("RATE US", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent("android.intent.action.VIEW");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("https://play.google.com/store/apps/details?id=");
                stringBuilder.append(MainActivity.this.getPackageName());
                intent.setData(Uri.parse(stringBuilder.toString()));
                MainActivity.this.startActivity(intent);
                Toast.makeText(MainActivity.this, "Thank you for your Rating", 0).show();
            }
        });
        builder.create().show();

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





