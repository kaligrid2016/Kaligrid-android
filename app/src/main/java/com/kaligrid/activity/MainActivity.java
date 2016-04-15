package com.kaligrid.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaligrid.R;
import com.kaligrid.fragment.DiscoverViewFragment;
import com.kaligrid.fragment.FriendsViewFragment;
import com.kaligrid.fragment.ListViewFragment;
import com.kaligrid.fragment.ProfileViewFragment;
import com.kaligrid.fragment.TypedBaseViewFragment;
import com.kaligrid.model.ContentViewType;

import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hirondelle.date4j.DateTime;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.top_toolbar_today_text) TextView topToolbarTodayText;
    @Bind(R.id.bottom_toolbar_list_image) ImageView bottomToolbarListImage;
    @Bind(R.id.bottom_toolbar_discover_image) ImageView bottomToolbarDiscoverImage;
    @Bind(R.id.bottom_toolbar_friends_image) ImageView bottomToolbarFriendsImage;
    @Bind(R.id.bottom_toolbar_profile_image) ImageView bottomToolbarProfileImage;

    private ContentViewType currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        loadInitialView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        topToolbarTodayText.setText(String.valueOf(DateTime.today(TimeZone.getDefault()).getDay()));
    }

    @OnClick(R.id.bottom_toolbar_list_image)
    public void onListImageClick(View v) {
        if (currentView == ContentViewType.LIST) {
            return;
        }

        resetBottomToolbarImages();
        ((ImageView) v).setImageResource(R.drawable.icon_bottom_list_selected);
        loadViewFragment(ListViewFragment.newInstance(this));
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
            bottomToolbarListImage.setImageResource(R.drawable.icon_bottom_list_selected);
            loadViewFragment(ListViewFragment.newInstance(this));
        }
    }

    private void resetBottomToolbarImages() {
        bottomToolbarListImage.setImageResource(R.drawable.icon_bottom_list);
        bottomToolbarDiscoverImage.setImageResource(R.drawable.icon_bottom_kali);
        bottomToolbarFriendsImage.setImageResource(R.drawable.icon_bottom_friends);
        bottomToolbarProfileImage.setImageResource(R.drawable.icon_bottom_me);
    }

    private void loadViewFragment(TypedBaseViewFragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_fragment_container, fragment).commit();
        currentView = fragment.getType();
    }


}
