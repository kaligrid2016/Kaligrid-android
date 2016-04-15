package com.kaligrid.fragment;

import android.app.Fragment;

import com.kaligrid.model.ContentViewType;

public abstract class TypedBaseViewFragment extends Fragment {
    public abstract ContentViewType getType();
}
