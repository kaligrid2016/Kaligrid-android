package com.kaligrid.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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

    @Bind(R.id.toolbar_main_top) Toolbar topToolbar;
    @Bind(R.id.toolbar_top_today_text) TextView textToday;
    @Bind(R.id.toolbar_bottom_image_list) ImageView toolbarImageList;
    @Bind(R.id.toolbar_bottom_image_grid) ImageView toolbarImageGrid;
    @Bind(R.id.toolbar_bottom_image_discover) ImageView toolbarImageDiscover;
    @Bind(R.id.toolbar_bottom_image_friends) ImageView toolbarImageFriends;
    @Bind(R.id.toolbar_bottom_image_profile) ImageView toolbarImageProfile;

    // Add event buttons
    @Bind(R.id.button_add) FloatingActionButton addButton;
    @Bind(R.id.button_add_cancel_label) TextView addButtonCancelLabel;
    @Bind(R.id.button_add_fyi) FloatingActionButton addButtonFyi;
    @Bind(R.id.button_add_fyi_label) TextView addButtonFyiLabel;
    @Bind(R.id.button_add_reminder) FloatingActionButton addButtonReminder;
    @Bind(R.id.button_add_reminder_label) TextView addButtonReminderLabel;
    @Bind(R.id.button_add_event) FloatingActionButton addButtonEvent;
    @Bind(R.id.button_add_event_label) TextView addButtonEventLabel;

    private ContentViewType currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(topToolbar);
        loadInitialView(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        textToday.setText(String.valueOf(Calendar.getInstance().get(Calendar.DATE)));
        return true;
    }

    @OnClick({R.id.button_add, R.id.button_add_cancel_label})
    public void onAddButtonClick(View v) {
        // If add button is enabled, expand other add buttons.
        if (addButtonCancelLabel.getVisibility() == View.GONE) {
            expandOtherAddButtons();
        } else {
            collapseOtherAddButtons();
        }
    }

    @OnClick({R.id.button_add_fyi, R.id.button_add_fyi_label})
    public void onAddFyiButtonClick(View v) {
        startActivity(new Intent(this, NewFyiActivity.class));
        collapseOtherAddButtons();
    }

    @OnClick({R.id.button_add_reminder, R.id.button_add_reminder_label})
    public void onAddReminderButtonClick(View v) {
        startActivity(new Intent(this, NewReminderActivity.class));
        collapseOtherAddButtons();
    }

    @OnClick({R.id.button_add_event, R.id.button_add_event_label})
    public void onAddEventButtonClick(View v) {
        startActivity(new Intent(this, NewEventActivity.class));
        collapseOtherAddButtons();
    }

    @OnClick(R.id.toolbar_bottom_image_list)
    public void onListImageClick(View v) {
        if (currentView == ContentViewType.LIST) {
            return;
        }

        addButton.show();
        resetBottomToolbarImages();
        ((ImageView) v).setImageResource(R.drawable.icon_bottom_list_selected);
        loadViewFragment(new ListViewFragment());
    }

    @OnClick(R.id.toolbar_bottom_image_grid)
    public void onGridImageClick(View v) {
        if (currentView == ContentViewType.GRID) {
            return;
        }

        resetBottomToolbarImages();
        ((ImageView) v).setImageResource(R.drawable.icon_bottom_grid_selected);
        loadViewFragment(new GridViewFragment());
    }

    @OnClick(R.id.toolbar_bottom_image_discover)
    public void onDiscoverImageClick(View v) {
        if (currentView == ContentViewType.DISCOVER) {
            return;
        }

        resetBottomToolbarImages();
        ((ImageView) v).setImageResource(R.drawable.icon_bottom_kali_selected);
        loadViewFragment(new DiscoverViewFragment());
    }

    @OnClick(R.id.toolbar_bottom_image_friends)
    public void onFriendsImageClick(View v) {
        if (currentView == ContentViewType.FRIENDS) {
            return;
        }

        resetBottomToolbarImages();
        ((ImageView) v).setImageResource(R.drawable.icon_bottom_friends_selected);
        loadViewFragment(new FriendsViewFragment());
    }

    @OnClick(R.id.toolbar_bottom_image_profile)
    public void onProfileImageClick(View v) {
        if (currentView == ContentViewType.PROFILE) {
            return;
        }

        resetBottomToolbarImages();
        ((ImageView) v).setImageResource(R.drawable.icon_bottom_me_selected);
        loadViewFragment(new ProfileViewFragment());
    }

    private void loadInitialView(Bundle savedInstanceState) {
        if (findViewById(R.id.content_fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            addButton.show();
            collapseOtherAddButtons();
            toolbarImageList.setImageResource(R.drawable.icon_bottom_list_selected);
            loadViewFragment(new ListViewFragment());
        }
    }

    private void resetBottomToolbarImages() {
        toolbarImageList.setImageResource(R.drawable.icon_bottom_list);
        toolbarImageGrid.setImageResource(R.drawable.icon_bottom_grid);
        toolbarImageDiscover.setImageResource(R.drawable.icon_bottom_kali);
        toolbarImageFriends.setImageResource(R.drawable.icon_bottom_friends);
        toolbarImageProfile.setImageResource(R.drawable.icon_bottom_me);
        addButton.setVisibility(View.GONE);
        collapseOtherAddButtons();
    }

    private void loadViewFragment(TypedBaseFragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_fragment_container, fragment).commit();
        currentView = fragment.getType();
    }

    private void expandOtherAddButtons() {
        // Change add button to cancel button
        addButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.disabledBackground)));
        addButtonCancelLabel.setVisibility(View.VISIBLE);

        addButtonFyi.setVisibility(View.VISIBLE);
        addButtonFyiLabel.setVisibility(View.VISIBLE);

        addButtonReminder.setVisibility(View.VISIBLE);
        addButtonReminderLabel.setVisibility(View.VISIBLE);

        addButtonEvent.setVisibility(View.VISIBLE);
        addButtonEventLabel.setVisibility(View.VISIBLE);
    }

    private void collapseOtherAddButtons() {
        addButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
        addButtonCancelLabel.setVisibility(View.GONE);

        addButtonFyi.setVisibility(View.GONE);
        addButtonFyiLabel.setVisibility(View.GONE);

        addButtonReminder.setVisibility(View.GONE);
        addButtonReminderLabel.setVisibility(View.GONE);

        addButtonEvent.setVisibility(View.GONE);
        addButtonEventLabel.setVisibility(View.GONE);
    }
}
