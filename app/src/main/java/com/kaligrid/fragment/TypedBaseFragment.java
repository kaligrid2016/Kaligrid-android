package com.kaligrid.fragment;

import android.app.Fragment;

import com.kaligrid.model.ContentViewType;

public abstract class TypedBaseFragment extends Fragment {
    public abstract ContentViewType getType();
}
