package com.zlei.secretimage;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.ortiz.touch.TouchImageView;
import com.zlei.secretimage.Constants.Extra;

public class ImageActivity extends Activity {

    private static final String STATE_POSITION = "STATE_POSITION";
    private int mInterval = 2000;
    private Handler handler;

    private TouchImageView imageView;
    private DisplayImageOptions options;
    private ViewPager pager;
    private Button btn_gotit;

    private float zoomTimes;
    private float xCoor;
    private float yCoor;
    private static final float ZOOMMAX = 8;
    private int pagerPosition;
    private ArrayList<String> imagesUri;
    private Runnable mStatusChecker;
    protected ImageLoader imageLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_image_pager);

        imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }

        btn_gotit = (Button) this.findViewById(R.id.got_it);
        handler = new Handler();
        Bundle bundle = getIntent().getExtras();
        String[] imageUrls = new String[100];
        imagesUri = new ArrayList<String>();
        if (bundle != null) {
            imageUrls = bundle.getStringArray(Extra.IMAGES);
        }

        // int pagerPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);
        for (String uri : imageUrls) {
            imagesUri.add(uri);
        }
        pagerPosition = 0;

        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        // randomly generate the initial position
        xCoor = (float) Math.random();
        yCoor = (float) Math.random();
        zoomTimes = ZOOMMAX;

        options =
                new DisplayImageOptions.Builder().resetViewBeforeLoading(true).cacheOnDisk(
                        true).imageScaleType(
                        ImageScaleType.EXACTLY).bitmapConfig(
                        Bitmap.Config.RGB_565).considerExifParams(true).displayer(
                        new FadeInBitmapDisplayer(300)).build();

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ImagePagerAdapter(imagesUri));
        // pager.setCurrentItem(pagerPosition);
        startRepeatingTask();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, pager.getCurrentItem());
    }

    @Override
    public void onBackPressed() {
        stopRepeatingTask();
        imageLoader.stop();
        super.onBackPressed();
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private ArrayList<String> imageUri;
        private LayoutInflater inflater;

        ImagePagerAdapter(ArrayList<String> imagesUri) {
            this.imageUri = imagesUri;
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return imageUri.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout =
                    inflater.inflate(R.layout.item_pager_image, view, false);
            imageView =
                    (TouchImageView) imageLayout.findViewById(R.id.img);
            final ProgressBar spinner =
                    (ProgressBar) imageLayout.findViewById(R.id.loading);

            imageLoader.displayImage(imagesUri.get(pagerPosition), imageView,
                    options,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            spinner.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            String message = null;
                            switch (failReason.getType()) {
                            case IO_ERROR:
                                message = "Input/Output error";
                                break;
                            case DECODING_ERROR:
                                message = "Image can't be decoded";
                                break;
                            case NETWORK_DENIED:
                                message = "Downloads are denied";
                                break;
                            case OUT_OF_MEMORY:
                                message = "Out Of Memory error";
                                break;
                            case UNKNOWN:
                                message = "Unknown error";
                                break;
                            }
                            Toast.makeText(ImageActivity.this, message,
                                    Toast.LENGTH_SHORT).show();

                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            spinner.setVisibility(View.GONE);
                        }
                    });

            view.addView(imageLayout, 0);

            imageView.setMaxZoom(ZOOMMAX);
            if (zoomTimes > 2)
                imageView.setZoom(zoomTimes, xCoor, yCoor);
            else
                imageView.resetZoom();
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    // repeat task

    private void startRepeatingTask() {
        mStatusChecker = new Runnable() {
            @Override
            public void run() {
                updateImage(); // this function can change value of mInterval.
                handler.postDelayed(mStatusChecker, mInterval);
            }
        };
        mStatusChecker.run();
    }

    private void stopRepeatingTask() {
        handler.removeCallbacks(mStatusChecker);
        mStatusChecker = null;
    }

    private void updateImage() {
        if (zoomTimes > 1) {
            if (zoomTimes > 2) {
                zoomTimes -= 1;
                pager.setAdapter(pager.getAdapter());
            }
            else {
                stopRepeatingTask();
                new AlertDialog.Builder(ImageActivity.this).setMessage(
                        "Time's Up!").setPositiveButton(
                        "Next",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (pagerPosition < imagesUri.size() - 1) {
                                    pagerPosition++;
                                    // reset for get new image
                                    zoomTimes = ZOOMMAX;
                                    startRepeatingTask();
                                }
                                else {
                                    new AlertDialog.Builder(ImageActivity.this).setMessage(
                                            "Last Image!").setNeutralButton(
                                            "Exit",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    imageLoader.stop();
                                                    finish();
                                                }
                                            }).show();
                                }
                            }
                        }).setNegativeButton("Exit",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                imageLoader.stop();
                                finish();
                            }
                        }).show();
            }
        }
    }

    public void onGotIt(View view) {
        stopRepeatingTask();
        new AlertDialog.Builder(ImageActivity.this).setMessage(
                "Correct?").setPositiveButton(
                "Yes. Next",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (pagerPosition < imagesUri.size() - 1) {
                            pagerPosition++;
                            // reset for get new image
                            zoomTimes = ZOOMMAX;
                            startRepeatingTask();
                        }
                        else {
                            new AlertDialog.Builder(ImageActivity.this).setMessage(
                                    "Last Image!").setNeutralButton(
                                    "Exit",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            imageLoader.stop();
                                            finish();
                                        }
                                    }).show();
                        }
                    }
                }).setNegativeButton("No. Exit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageLoader.stop();
                        finish();
                    }
                }).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}