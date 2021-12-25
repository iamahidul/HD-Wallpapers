package com.androsoft.funnyvideos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;

import java.util.concurrent.TimeUnit;

public class ctg extends AppCompatActivity implements MaxAdListener, MaxAdViewAdListener {
    Button btn1,btn2,btn3,btn4,btn5;

    private MaxInterstitialAd interstitialAd;
    private MaxAdView adView;
    private int retryAttempt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctg);

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


        btn1=(Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctg.this,page1.class);
                startActivity(intent);


            }
        });
        btn2=(Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctg.this,page2.class);
                startActivity(intent);
                if ( interstitialAd.isReady() )
                {
                    interstitialAd.showAd();
                }

            }
        });

        btn3=(Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctg.this,page3.class);
                startActivity(intent);

            }
        });

        btn4=(Button) findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctg.this,page4.class);
                startActivity(intent);

                if ( interstitialAd.isReady() )
                {
                    interstitialAd.showAd();
                }
            }
        });








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
