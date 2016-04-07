package com.kaligrid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaligrid.R;
import com.kaligrid.model.ContentViewType;

public class ProfileViewFragment extends TypedBaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_view, container, false);
    }

    @Override
    public ContentViewType getType() {
        return ContentViewType.PROFILE;
    }
}
