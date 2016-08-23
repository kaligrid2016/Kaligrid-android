package com.kaligrid.app;

import android.content.Context;

import com.kaligrid.activity.NewEventBaseActivity;
import com.kaligrid.fragment.ListViewFragment;
import com.kaligrid.service.EventService;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = ServiceModule.class)
@Singleton
public interface ObjectGraph {

    void inject(ListViewFragment fragment);
    void inject(NewEventBaseActivity activity);

    final class Initializer {
        public static ObjectGraph init(Context context) {
            return DaggerObjectGraph.builder()
                    .serviceModule(new ServiceModule(context))
                    .build();
        }
    }
}
