package com.kaligrid.app;

import android.content.Context;

import com.kaligrid.fragment.ListViewFragment;
import com.kaligrid.service.EventService;

import dagger.Component;

@Component(modules = ServiceModule.class)
public interface ObjectGraph {

    void inject(ListViewFragment fragment);

    final class Initializer {
        public static ObjectGraph init(Context context) {
            return DaggerObjectGraph.builder()
                    .serviceModule(new ServiceModule(context))
                    .build();
        }
    }
}
