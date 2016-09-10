package com.kaligrid.app;

import android.content.Context;

import com.kaligrid.activity.event.edit.EditBaseActivity;
import com.kaligrid.fragment.EventListViewFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = DepedencyModule.class)
@Singleton
public interface ObjectGraph {

    void inject(EventListViewFragment fragment);
    void inject(EditBaseActivity activity);

    final class Initializer {
        public static ObjectGraph init(Context context) {
            return DaggerObjectGraph.builder()
                    .depedencyModule(new DepedencyModule(context))
                    .build();
        }
    }
}
