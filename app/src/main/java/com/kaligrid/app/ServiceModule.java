package com.kaligrid.app;

import android.content.Context;

import com.kaligrid.dao.DBHelper;
import com.kaligrid.service.EventService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    private final Context context;

    public ServiceModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public DBHelper provideDBHelper() {
        return new DBHelper(context);
    }

    @Provides
    @Singleton
    public EventService provideEventService(DBHelper dbHelper) {
        return new EventService(dbHelper);
    }
}
