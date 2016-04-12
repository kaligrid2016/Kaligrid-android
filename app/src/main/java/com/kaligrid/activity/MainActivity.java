package com.kaligrid.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaligrid.R;
import com.kaligrid.fragment.DiscoverViewFragment;
import com.kaligrid.fragment.FriendsViewFragment;
import com.kaligrid.fragment.GridViewFragment;
import com.kaligrid.fragment.ListViewFragment;
import com.kaligrid.fragment.ProfileViewFragment;
import com.kaligrid.fragment.TypedBaseFragment;
import com.kaligrid.model.ContentViewType;

import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hirondelle.date4j.DateTime;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.top_toolbar_today_text) TextView topToolbarTodayText;
    @Bind(R.id.bottom_toolbar_list_image) ImageView bottomToolbarListImage;
    @Bind(R.id.bottom_toolbar_grid_image) ImageView bottomToolbarGridImage;
    @Bind(R.id.bottom_toolbar_discover_image) ImageView bottomToolbarDiscoverImage;
    @Bind(R.id.bottom_toolbar_friends_image) ImageView bottomToolbarFriendsImage;
    @Bind(R.id.bottom_toolbar_profile_image) ImageView bottomToolbarProfileImage;

    // Add event buttons
    @Bind(R.id.new_button) ImageView newButton;
    @Bind(R.id.cancel_button_text) TextView cancelButtonText;
    @Bind(R.id.new_fyi_button) ImageView newFyiButton;
    @Bind(R.id.new_fyi_button_text) TextView newFyiButtonText;
    @Bind(R.id.new_reminder_button) ImageView newReminderButton;
    @Bind(R.id.new_reminder_button_text) TextView newReminderButtonText;
    @Bind(R.id.new_event_button) ImageView newEventButton;
    @Bind(R.id.new_event_button_text) TextView newEventButtonText;

    private ContentViewType currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInitialView();
        topToolbarTodayText.setText(String.valueOf(DateTime.today(TimeZone.getDefault()).getDay()));
    }

    @OnClick({R.id.new_button, R.id.cancel_button_text})
    public void onAddButtonClick(View v) {
        // If add button is enabled, expand other add buttons.
        if (cancelButtonText.getVisibility() == View.GONE) {
            expandOtherAddButtons();
        } else {
            collapseOtherAddButtons();
        }
    }

    @OnClick({R.id.new_fyi_button, R.id.new_fyi_button_text})
    public void onAddFyiButtonClick(View v) {
        startActivity(new Intent(this, NewFyiActivity.class));
        collapseOtherAddButtons();
    }

    @OnClick({R.id.new_reminder_button, R.id.new_reminder_button_text})
    public void onAddReminderButtonClick(View v) {
        startActivity(new Intent(this, NewReminderActivity.class));
        collapseOtherAddButtons();
    }

    @OnClick({R.id.new_event_button, R.id.new_event_button_text})
    public void onAddEventButtonClick(View v) {
        startActivity(new Intent(this, NewEventActivity.class));
        collapseOtherAddButtons();
    }

    @OnClick(R.id.bottom_toolbar_list_image)
    public void onListImageClick(View v) {
        if (currentView == ContentViewType.LIST) {
            return;
        }

        resetBottomToolbarImages();
        newButton.setVisibility(View.VISIBLE);
        ((ImageView) v).setImageResource(R.drawable.icon_bottom_list_selected);
        loadViewFragment(ListViewFragment.newInstance(this));
    }

    @OnClick(R.id.bottom_toolbar_grid_image)
    public void onGridImageClick(View v) {
        if (currentView == ContentViewType.GRID) {
            return;
        }

        resetBottomToolbarImages();
        ((ImageView) v).setImageResource(R.drawable.icon_bottom_grid_selected);
        loadViewFragment(new GridViewFragment());
    }

    @OnClick(R.id.bottom_toolbar_discover_image)
    public void onDiscoverImageClick(View v) {
        if (currentView == ContentViewType.DISCOVER) {
            return;
        }

        resetBottomToolbarImages();
        ((ImageView) v).setImageResource(R.drawable.icon_bottom_kali_selected);
        loadViewFragment(new DiscoverViewFragment());
    }

    @OnClick(R.id.bottom_toolbar_friends_image)
    public void onFriendsImageClick(View v) {
        if (currentView == ContentViewType.FRIENDS) {
            return;
        }

        resetBottomToolbarImages();
        ((ImageView) v).setImageResource(R.drawable.icon_bottom_friends_selected);
        loadViewFragment(new FriendsViewFragment());
    }

    @OnClick(R.id.bottom_toolbar_profile_image)
    public void onProfileImageClick(View v) {
        if (currentView == ContentViewType.PROFILE) {
            return;
        }

        resetBottomToolbarImages();
        ((ImageView) v).setImageResource(R.drawable.icon_bottom_me_selected);
        loadViewFragment(new ProfileViewFragment());
    }

    private void loadInitialView() {
        if (findViewById(R.id.content_fragment_container) != null) {
            collapseOtherAddButtons();
            bottomToolbarListImage.setImageResource(R.drawable.icon_bottom_list_selected);
            loadViewFragment(ListViewFragment.newInstance(this));
        }
    }

    private void resetBottomToolbarImages() {
        bottomToolbarListImage.setImageResource(R.drawable.icon_bottom_list);
        bottomToolbarGridImage.setImageResource(R.drawable.icon_bottom_grid);
        bottomToolbarDiscoverImage.setImageResource(R.drawable.icon_bottom_kali);
        bottomToolbarFriendsImage.setImageResource(R.drawable.icon_bottom_friends);
        bottomToolbarProfileImage.setImageResource(R.drawable.icon_bottom_me);
        newButton.setVisibility(View.GONE);
        collapseOtherAddButtons();
    }

    private void loadViewFragment(TypedBaseFragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_fragment_container, fragment).commit();
        currentView = fragment.getType();
    }

    private void expandOtherAddButtons() {
        // Change add button to cancel button
        newButton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background_cancel));
        newButton.setImageResource(R.drawable.icon_new_cancel);
        cancelButtonText.setVisibility(View.VISIBLE);

        newFyiButton.setVisibility(View.VISIBLE);
        newFyiButtonText.setVisibility(View.VISIBLE);

        newReminderButton.setVisibility(View.VISIBLE);
        newReminderButtonText.setVisibility(View.VISIBLE);

        newEventButton.setVisibility(View.VISIBLE);
        newEventButtonText.setVisibility(View.VISIBLE);
    }

    private void collapseOtherAddButtons() {
        newButton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background_new));
        newButton.setImageResource(R.drawable.icon_new);
        cancelButtonText.setVisibility(View.GONE);

        newFyiButton.setVisibility(View.GONE);
        newFyiButtonText.setVisibility(View.GONE);

        newReminderButton.setVisibility(View.GONE);
        newReminderButtonText.setVisibility(View.GONE);

        newEventButton.setVisibility(View.GONE);
        newEventButtonText.setVisibility(View.GONE);
    }
}
