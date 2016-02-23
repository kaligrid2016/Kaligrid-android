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

public class MainActivity extends AppCompatActivity {

    private ImageView listImage;
    private ImageView gridImage;
    private ImageView discoverImage;
    private ImageView friendsImage;
    private ImageView profileImage;
    private FloatingActionButton addButton;
    private ContentViewType currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listImage = (ImageView) findViewById(R.id.toolbar_bottom_list_image);
        gridImage = (ImageView) findViewById(R.id.toolbar_bottom_grid_image);
        discoverImage = (ImageView) findViewById(R.id.toolbar_bottom_discover_image);
        friendsImage = (ImageView) findViewById(R.id.toolbar_bottom_friends_image);
        profileImage = (ImageView) findViewById(R.id.toolbar_bottom_profile_image);
        addButton = (FloatingActionButton) findViewById(R.id.button_add_event);

        setUpTopToolbar();
        setUpBottomToolbar();
        setUpAddEventButton();

        loadInitialView(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        TextView todayText = (TextView) findViewById(R.id.toolbar_top_today_text);
        todayText.setText(String.valueOf(Calendar.getInstance().get(Calendar.DATE)));
        return true;
    }

    private void setUpTopToolbar() {
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbarTop);
    }

    private void setUpBottomToolbar() {
        listImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentView == ContentViewType.LIST) {
                    return;
                }

                addButton.show();
                resetBottomToolbarImages();
                ((ImageView) v).setImageResource(R.drawable.icon_bottom_list_selected);
                loadViewFragment(new ListViewFragment());
            }
        });

        gridImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentView == ContentViewType.GRID) {
                    return;
                }

                resetBottomToolbarImages();
                ((ImageView) v).setImageResource(R.drawable.icon_bottom_grid_selected);
                loadViewFragment(new GridViewFragment());
            }
        });

        discoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentView == ContentViewType.DISCOVER) {
                    return;
                }

                resetBottomToolbarImages();
                ((ImageView) v).setImageResource(R.drawable.icon_bottom_kali_selected);
                loadViewFragment(new DiscoverViewFragment());
            }
        });

        friendsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentView == ContentViewType.FRIENDS) {
                    return;
                }

                resetBottomToolbarImages();
                ((ImageView) v).setImageResource(R.drawable.icon_bottom_friends_selected);
                loadViewFragment(new FriendsViewFragment());
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentView == ContentViewType.PROFILE) {
                    return;
                }

                resetBottomToolbarImages();
                ((ImageView) v).setImageResource(R.drawable.icon_bottom_me_selected);
                loadViewFragment(new ProfileViewFragment());
            }
        });
    }

    private void setUpAddEventButton() {
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
