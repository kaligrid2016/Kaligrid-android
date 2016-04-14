package com.kaligrid.app;

import com.kaligrid.fragment.ListViewFragment;

import dagger.Component;

@Component(modules = ServiceModule.class)
public interface ObjectGraph {

    void inject(ListViewFragment fragment);

    final class Initializer {
        @SuppressWarnings("deprecation")
        public static ObjectGraph init() {
            return DaggerObjectGraph.builder()
                    .serviceModule(new ServiceModule())
                    .build();
        }
    }
}
