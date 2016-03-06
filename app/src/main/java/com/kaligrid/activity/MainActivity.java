package com.kaligrid.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaligrid.R;
import com.kaligrid.fragment.ContentViewType;
import com.kaligrid.fragment.DiscoverViewFragment;
import com.kaligrid.fragment.FriendsViewFragment;
import com.kaligrid.fragment.GridViewFragment;
import com.kaligrid.fragment.ListViewFragment;
import com.kaligrid.fragment.ProfileViewFragment;
import com.kaligrid.fragment.TypedBaseFragment;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar_top) Toolbar toolbarTop;
    @Bind(R.id.toolbar_bottom_list_image) ImageView listImage;
    @Bind(R.id.toolbar_bottom_grid_image) ImageView gridImage;
    @Bind(R.id.toolbar_bottom_discover_image) ImageView discoverImage;
    @Bind(R.id.toolbar_bottom_friends_image) ImageView friendsImage;
    @Bind(R.id.toolbar_bottom_profile_image) ImageView profileImage;
    @Bind(R.id.button_add_event) FloatingActionButton addButton;
    @Bind(R.id.toolbar_top_today_text) TextView todayText;

    private ContentViewType currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Window window = this.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ButterKnife.bind(this);

        setSupportActionBar(toolbarTop);
        initializeAddEventButton();

        loadInitialView(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        todayText.setText(String.valueOf(Calendar.getInstance().get(Calendar.DATE)));
        return true;
    }

    @OnClick(R.id.toolbar_bottom_list_image)
    public void onListImageClick(View v) {
        if (currentView == ContentViewType.LIST) {
            return;
        }

        addButton.show();
        resetBottomToolbarImages();
        ((ImageView) v).setImageResource(R.drawable.icon_bottom_list_selected);
        loadViewFragment(new ListViewFragment());
    }

    @OnClick(R.id.toolbar_bottom_grid_image)
    public void onGridImageClick(View v) {
        if (currentView == ContentViewType.GRID) {
            return;
        }

        resetBottomToolbarImages();
        ((ImageView) v).setImageResource(R.drawable.icon_bottom_grid_selected);
        loadViewFragment(new GridViewFragment());
    }

    @OnClick(R.id.toolbar_bottom_discover_image)
    public void onDiscoverImageClick(View v) {
        if (currentView == ContentViewType.DISCOVER) {
            return;
        }

        resetBottomToolbarImages();
        ((ImageView) v).setImageResource(R.drawable.icon_bottom_kali_selected);
        loadViewFragment(new DiscoverViewFragment());
    }

    @OnClick(R.id.toolbar_bottom_friends_image)
    public void onFriendsImageClick(View v) {
        if (currentView == ContentViewType.FRIENDS) {
            return;
        }

        resetBottomToolbarImages();
        ((ImageView) v).setImageResource(R.drawable.icon_bottom_friends_selected);
        loadViewFragment(new FriendsViewFragment());
    }

    @OnClick(R.id.toolbar_bottom_profile_image)
    public void onProfileImageClick(View v) {
        if (currentView == ContentViewType.PROFILE) {
            return;
        }

        resetBottomToolbarImages();
        ((ImageView) v).setImageResource(R.drawable.icon_bottom_me_selected);
        loadViewFragment(new ProfileViewFragment());
    }

    private void initializeAddEventButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_add_event);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void loadInitialView(Bundle savedInstanceState) {
        if (findViewById(R.id.content_fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            addButton.show();
            listImage.setImageResource(R.drawable.icon_bottom_list_selected);
            loadViewFragment(new ListViewFragment());
        }
    }

    private void resetBottomToolbarImages() {
        listImage.setImageResource(R.drawable.icon_bottom_list);
        gridImage.setImageResource(R.drawable.icon_bottom_grid);
        discoverImage.setImageResource(R.drawable.icon_bottom_kali);
        friendsImage.setImageResource(R.drawable.icon_bottom_friends);
        profileImage.setImageResource(R.drawable.icon_bottom_me);
        addButton.setVisibility(View.GONE);
    }

    private void loadViewFragment(TypedBaseFragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_fragment_container, fragment).commit();
        currentView = fragment.getType();
    }
}
