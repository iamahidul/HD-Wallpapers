package com.androsoft.funnyvideos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.ybq.android.spinkit.SpinKitView;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.androsoft.funnyvideos.MyUtils.counter;
import static com.androsoft.funnyvideos.MyUtils.getContactBitmapFromURI;
import static com.androsoft.funnyvideos.MyUtils.getInternalFile;
import static com.androsoft.funnyvideos.MyUtils.saveCropedImage;
import static com.androsoft.funnyvideos.MyUtils.setBgOrDownload;
import static com.androsoft.funnyvideos.MyUtils.setHomeOrLock;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener, MaxAdListener, MaxAdViewAdListener {
    private MaxInterstitialAd interstitialAd;
    private MaxAdView adView;
    private int retryAttempt;
    private ImageView imageView;
    private FloatingActionMenu fab_menu;
    private FloatingActionButton fab_download, fab_set_bg, fab_crop, fab_home, fab_lock;
    private SpinKitView spinKitView;

    private String url;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_preview);


        //
        adView = new MaxAdView( (getString(R.string.banner_home_footer)), this );
        adView.setListener( this );
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int heightPx = getResources().getDimensionPixelSize( R.dimen.banner_height );
        adView.setLayoutParams( new FrameLayout.LayoutParams( width, heightPx , Gravity.TOP) );
        ViewGroup rootView = findViewById( android.R.id.content );
        rootView.addView( adView );
        adView.loadAd();



        imageView = findViewById(R.id.preview_imageview);
        fab_menu = findViewById(R.id.menu_labels_right);
        fab_download = findViewById(R.id.fab_download);
        fab_set_bg = findViewById(R.id.fab_set_bg);
        spinKitView = findViewById(R.id.spin_kit);
        fab_crop = findViewById(R.id.fab_crop);
        fab_home = findViewById(R.id.fab_set_home);
        fab_lock = findViewById(R.id.fab_set_lock);
        url = getIntent().getStringExtra("image_url");

        fab_set_bg.setOnClickListener(this);
        fab_download.setOnClickListener(this);
        fab_crop.setOnClickListener(this);
        fab_home.setOnClickListener(this);
        fab_lock.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fab_home.setVisibility(View.VISIBLE);
            fab_lock.setVisibility(View.VISIBLE);
        } else {

            fab_home.setVisibility(View.GONE);
            fab_lock.setVisibility(View.GONE);
        }



        if (counter % 5 == 0) {
            spinKitView.setVisibility(View.VISIBLE);

        } else {
            loadImage();
        }


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab_download:
                fab_menu.close(true);
                spinKitView.setVisibility(View.VISIBLE);
                setBgOrDownload(url, this, true);
                break;
            case R.id.fab_set_bg:
                fab_menu.close(true);
                spinKitView.setVisibility(View.VISIBLE);

                setBgOrDownload(url, this, false);

                break;

            case R.id.fab_set_home:
                fab_menu.close(true);
                spinKitView.setVisibility(View.VISIBLE);

                setHomeOrLock(url, this, false);
                break;
            case R.id.fab_set_lock:
                fab_menu.close(true);
                spinKitView.setVisibility(View.VISIBLE);

                setHomeOrLock(url, this, true);

                break;

            case R.id.fab_crop:
                fab_menu.close(true);
                try {
                    getInternalFile(this, url);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void loadImage() {
        if (url != null && !url.equals("")) {

            Picasso.get().load(url).fit().fit().into(imageView);
        }
    }

    public void wallpaperSet(boolean b) {
        runOnUiThread(() -> {
            if (b) {
                Toast.makeText(this, "Background is set", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Something went wrong! Please try again", Toast.LENGTH_SHORT).show();

            }
            spinKitView.setVisibility(View.GONE);
        });

    }



    public void homeOrLockSet(boolean lock)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(lock)
                {
                    Toast.makeText(PreviewActivity.this, "Set as Lockscreen background", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(PreviewActivity.this, "Set as Home background", Toast.LENGTH_SHORT).show();
                }
                spinKitView.setVisibility(View.GONE);
            }
        });
    }

    public void downloadComplete(boolean downloaded) {
        if (downloaded) {
            Toast.makeText(this, "Download Complete", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Download Failed! Please try again", Toast.LENGTH_SHORT).show();
        }
        spinKitView.setVisibility(View.GONE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                new AlertDialog.Builder(this, R.style.DialogTheme)
                        .setTitle("Save Cropped Image")
                        .setMessage("Do you want to save your cropped image?")


                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            Bitmap bitmap = getContactBitmapFromURI(this, resultUri);
                            saveCropedImage(this, url, bitmap);
                            dialog.dismiss();
                        })

                        .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                        .show();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
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




