package com.kaligrid.app;

import android.content.Context;

import com.kaligrid.activity.NewEventBaseActivity;
import com.kaligrid.fragment.ListViewFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = DepedencyModule.class)
@Singleton
public interface ObjectGraph {

    void inject(ListViewFragment fragment);
    void inject(NewEventBaseActivity activity);

    final class Initializer {
        public static ObjectGraph init(Context context) {
            return DaggerObjectGraph.builder()
                    .depedencyModule(new DepedencyModule(context))
                    .build();
        }
    }
}
