package com.kaligrid.app;

import com.kaligrid.service.EventService;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    @Provides
    static EventService provideEventService() {
        return new EventService();
    }
}
