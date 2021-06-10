package com.fuad.appmanager;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.fuad.appmanager.EnumData.*;

public class MainActivity extends AppCompatActivity {

    private ArrayList<AppItem> appItems;
    private ArrayList<AppItem> selectedApps;
    private EditText searchText;
    private ImageView filter;
    private RecyclerView recyclerView;
    private AppRecyclerViewAdapter adapter;
    private ShimmerFrameLayout shimmerFrameLayout;

    private boolean showUserApps;
    private boolean showSystemApps;
    private String sortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appItems = new ArrayList<>();
        selectedApps = new ArrayList<>();

        searchText = findViewById(R.id.search_text);
        filter = findViewById(R.id.filter_icon);
        shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        new GetAllAppTask().execute();
    }

    class GetAllAppTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            appItems = new ArrayList<>();

            showSystemApps = Boolean.parseBoolean(getFromSharedPref(KEY_SHOW_SYSTEM_APP, MainActivity.this));
            showUserApps = Boolean.parseBoolean(getFromSharedPref(KEY_SHOW_USER_APP, MainActivity.this)) || !showSystemApps;
            sortBy = getFromSharedPref(KEY_SORT_BY, getApplicationContext());
            if (sortBy.isEmpty()) sortBy = SORT_BY_APP_TITLE;
        }

        @Override
        protected Void doInBackground(Void... voids) {
           /* List<PackageInfo> packageList = getPackageManager().getInstalledPackages(0);
//            List<PackageInfo> packageList = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

            for (int i = 0; i < packageList.size(); i++) {
                PackageInfo packageInfo = packageList.get(i);

                boolean isSystemApp = isSystemPackage(packageInfo);
                if (showUserApps && !isSystemApp) {
                    AppItem userApp = getAppFromPackageInfo(packageInfo);
                    if (!appItems.contains(userApp))
                        appItems.add(userApp);

                } else if (showSystemApps && isSystemApp) {
                    AppItem systemApp = getAppFromPackageInfo(packageInfo);
                    if (!appItems.contains(systemApp))
                        appItems.add(systemApp);
                }
            }
            */

            List<ApplicationInfo> applicationInfoList = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
            for(ApplicationInfo activityInfo : applicationInfoList){

                if(!isSystemPackage(activityInfo)){
                    AppItem app = new AppItem();
                    app.setAppIcon(activityInfo.loadIcon(getPackageManager()));
                    app.setAppName(activityInfo.loadLabel(getPackageManager()).toString());
                    app.setAppPackageName(activityInfo.packageName);
                    appItems.add(app);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Apply sort in list
            if (SORT_BY_APP_TITLE.equals(sortBy))
                Collections.sort(appItems, AppItem.appTitleComparator);
            else if (SORT_BY_SIZE.equals(sortBy))
                Collections.sort(appItems, AppItem.appSizeComparator);

            // Setup the recyclerview
            adapter = new AppRecyclerViewAdapter(appItems);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, ""+appItems.size(), Toast.LENGTH_SHORT).show();
            adapter.setOnItemClickListener(new AppRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, View view) {
                    ImageView selectedView = view.findViewById(R.id.selected_view);
                    if (adapter.isSelectMode) {
                        if (selectedApps.contains(appItems.get(position))) {
                            appItems.get(position).isSelected = false;
                            selectedApps.remove(appItems.get(position));
                        } else {
                            appItems.get(position).isSelected = true;
                            selectedApps.add(appItems.get(position));
                        }
                        if(selectedApps.size() == 0)
                            adapter.isSelectMode = false;
                        adapter.notifyDataSetChanged();
                    }else {
                        Intent intent = new Intent(MainActivity.this, AppDetailsActivity.class);
//                      intent.putExtra("app", appItems.get(position));
                        startActivity(intent);
                    }
                }

                @Override
                public void onItemLongClick(int position, View view) {
                    ImageView selectedView = view.findViewById(R.id.selected_view);
                    adapter.isSelectMode = true;
                    if (selectedApps.contains(appItems.get(position))) {
//                            view.setBackgroundColor(Color.TRANSPARENT);
//                            selectedView.setVisibility(View.INVISIBLE);
                        appItems.get(position).isSelected = false;
                        selectedApps.remove(appItems.get(position));
                    } else {
//                            view.setBackgroundResource(R.color.gray);
//                            selectedView.setVisibility(View.VISIBLE);
                        appItems.get(position).isSelected = true;
                        selectedApps.add(appItems.get(position));
                    }
                    if(selectedApps.size() == 0)
                        adapter.isSelectMode = false;
                    adapter.notifyDataSetChanged();
                }
            });

            searchText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            String search = searchText.getText().toString();
            if (!search.isEmpty()) adapter.getFilter().filter(search);

            shimmerFrameLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            filter.setOnClickListener(v -> filterDialog());
        }
    }

    public boolean isSystemPackage(ApplicationInfo applicationInfo){

        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private AppItem getAppFromPackageInfo(PackageInfo packageInfo) {
        String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
        String packageName = packageInfo.packageName;
        Drawable appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
        String version = packageInfo.versionName + " " + packageInfo.versionCode;
        File appFile = new File(packageInfo.applicationInfo.publicSourceDir);
        int size = (int) appFile.length();

        AppItem app = new AppItem();
        app.setAppIcon(appIcon);
        app.setAppName(appName);
        app.setAppPackageName(packageName);
        app.setAppVersion(version);
        app.setAppSize(size);

        return app;
    }

    public void filterDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filter_dialog);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        CheckBox userAppsCheckBox = dialog.findViewById(R.id.checkbox_user_apps);
        CheckBox systemAppsCheckBox = dialog.findViewById(R.id.checkbox_system_apps);

        userAppsCheckBox.setChecked(showUserApps);
        systemAppsCheckBox.setChecked(showSystemApps);

        userAppsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!systemAppsCheckBox.isChecked()) userAppsCheckBox.setChecked(true);
            showUserApps = isChecked;
            setInSharedPref(KEY_SHOW_USER_APP, String.valueOf(isChecked), getApplicationContext());
        });

        systemAppsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!userAppsCheckBox.isChecked()) systemAppsCheckBox.setChecked(true);
            showSystemApps = isChecked;
            setInSharedPref(KEY_SHOW_SYSTEM_APP, String.valueOf(isChecked), getApplicationContext());
        });

        sortBy = getFromSharedPref(KEY_SORT_BY, getApplicationContext());
        if (sortBy.isEmpty()) sortBy = SORT_BY_APP_TITLE;

        RadioButton radioButton = dialog.findViewById(getResources().getIdentifier("radio_" + sortBy, "id", getApplicationContext().getPackageName()));
        if (radioButton != null) radioButton.setChecked(true);

        RadioGroup radioGroup = dialog.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radio_app_title:
                    setInSharedPref(KEY_SORT_BY, SORT_BY_APP_TITLE, getApplicationContext());
                    break;
                case R.id.radio_size:
                    setInSharedPref(KEY_SORT_BY, SORT_BY_SIZE, getApplicationContext());
                    break;
            }
        });

        TextView applyButton = dialog.findViewById(R.id.apply_btn);
        TextView cancelButton = dialog.findViewById(R.id.cancel_btn);

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        applyButton.setOnClickListener(v -> {
            dialog.dismiss();
            new GetAllAppTask().execute();
        });

    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
}