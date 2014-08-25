package com.zlei.secretimage;

import static com.zlei.secretimage.Constants.IMAGES;

import java.util.Locale;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sessionm.api.BaseActivity;
import com.sessionm.api.SessionM;
import com.sessionm.api.SessionM.ActivityType;
import com.zlei.secretimage.Constants.Extra;
import com.zlei.secretimage.SessionMTransaction.SessionMTransactionListener;

public class MainActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mListTitles;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getImagePath();
        mTitle = mDrawerTitle = getTitle();
        mListTitles = getResources().getStringArray(R.array.lists_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new MyArrayAdapter(this,
                R.layout.drawer_list_item, mListTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
                ) {
                    public void onDrawerClosed(View view) {
                        getActionBar().setTitle(mTitle);
                        invalidateOptionsMenu();
                    }

                    public void onDrawerOpened(View drawerView) {
                        getActionBar().setTitle(mDrawerTitle);
                        invalidateOptionsMenu();
                    }
                };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        SessionMTransaction txn = new SessionMTransaction(this, "aba6ba56b63680cad063e987df52a71e620dbc77");
        txn.start();
        SessionMTransactionListener listener = new SessionMTransactionListener() {
            @Override
            public void onSuccess(SessionMTransaction txn) {
                Log.i("success ", "yay!");
            }

            @Override
            public void onError(SessionMTransaction txn, int statusCode, Throwable error) {
                Log.i("fail ", String.format("boo! %s %d", error, statusCode));
            }
        };
        txn.setListener(listener);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
    public void onStartClick(View view) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra(Extra.IMAGES, IMAGES);
        startActivity(intent);
    }

    private void getImagePath() {
        String path = Environment.getExternalStorageDirectory().toString();
        String[] projection = { MediaStore.Images.Media.DATA };
        for (String str : projection) {
            Log.d("path: ", str + "\n");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ListFragment.ARG_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mListTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class ListFragment extends Fragment {
        public static final String ARG_NUMBER = "arg_number";

        public ListFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            int i = getArguments().getInt(ARG_NUMBER);
            View rootView = inflater.inflate(R.layout.fragment_home,
                    container, false);
            switch (i) {
            case 1:
                SessionM.getInstance().presentActivity(ActivityType.PORTAL);
                break;
            case 2:
                rootView = inflater.inflate(R.layout.fragment_image,
                        container, false);
                String image = getResources().getStringArray(
                        R.array.lists_array)[i];
                int imageId = getResources().getIdentifier(
                        image.toLowerCase(Locale.getDefault()),
                        "drawable", getActivity().getPackageName());
                ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
                getActivity().setTitle(image);
                break;
            default:
                break;
            }
            return rootView;
        }
    }

    public class MyArrayAdapter extends ArrayAdapter<String> {
        private final int resource;
        private final Context context;
        private final String[] objects;

        public MyArrayAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            this.resource = resource;
            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(resource, parent,
                    false);
            TextView textView = (TextView) rowView.findViewById(R.id.list_text);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.list_icon);
            textView.setText(objects[position]);
            String s = objects[position];

            imageView.setImageResource(R.drawable.ic_launcher);
            return rowView;
        }
    }

}
