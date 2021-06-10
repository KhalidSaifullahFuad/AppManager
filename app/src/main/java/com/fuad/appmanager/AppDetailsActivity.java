package com.fuad.appmanager;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

public class AppDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });

        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle("#DRIVE");
        toolBarLayout.setExpandedTitleGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);

        // register the floating action Button
        final ExtendedFloatingActionButton extendedFloatingActionButton = findViewById(R.id.floating_action_button);

        NestedScrollView nestedScrollView = findViewById(R.id.nested_scroll_view);
        // handle the nestedScrollView behaviour with OnScrollChangeListener
        // to hide or show the Floating Action Button
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            // the delay of the extension of the FAB is set for 12 items
            if (scrollY > oldScrollY + 12 && extendedFloatingActionButton.isExtended()) {
                extendedFloatingActionButton.shrink();
            }
            // the delay of the extension of the FAB is set for 12 items
            if (scrollY < oldScrollY - 12 && !extendedFloatingActionButton.isExtended()) {
                extendedFloatingActionButton.extend();
            }
            // if the nestedScrollView is at the first item of the list then the
            // floating action should be in show state
            if (scrollY == 0) {
                extendedFloatingActionButton.extend();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_details, menu);
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
}