package com.kaligrid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaligrid.R;

public class FriendsViewFragment extends TypedBaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.friends_view_fragment, container, false);
    }

    @Override
    public ContentViewType getType() {
        return ContentViewType.FRIENDS;
    }
}