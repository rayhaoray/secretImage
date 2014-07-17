package com.zlei.secretimage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
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

    private int mInterval = 5000;

    private Handler handler;

    private TouchImageView imageView;

    private TextView timer;

    private DisplayImageOptions options;

    private ViewPager pager;

    private float zoomTimes;

    private float xCoor;

    private float yCoor;

    private static final float ZOOMMAX = 12;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_image_pager);

        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        handler = new Handler();
        Bundle bundle = getIntent().getExtras();
        String[] imageUrls = new String[100];
        if (bundle != null) {
            imageUrls = bundle.getStringArray(Extra.IMAGES);
        }
        int pagerPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);

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
        pager.setAdapter(new ImagePagerAdapter(imageUrls));
        pager.setCurrentItem(pagerPosition);
        this.startRepeatingTask();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, pager.getCurrentItem());
    }

    @Override
    public void onBackPressed() {
        imageLoader.stop();
        super.onBackPressed();
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private String[] images;
        private LayoutInflater inflater;

        ImagePagerAdapter(String[] images) {
            this.images = images;
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout =
                    inflater.inflate(R.layout.item_pager_image, view, false);
            imageView =
                    (TouchImageView) imageLayout.findViewById(R.id.img);
            final ProgressBar spinner =
                    (ProgressBar) imageLayout.findViewById(R.id.loading);
            timer = (TextView) imageLayout.findViewById(R.id.count_down);

            imageLoader.displayImage(images[position], imageView, options,
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
            if (zoomTimes > 3)
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
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            updateImage(); // this function can change value of mInterval.
            handler.postDelayed(mStatusChecker, mInterval);
        }
    };

    private void startRepeatingTask() {
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
            }
            else {
                stopRepeatingTask();
            }
            pager.setAdapter(pager.getAdapter());
        }
    }
}