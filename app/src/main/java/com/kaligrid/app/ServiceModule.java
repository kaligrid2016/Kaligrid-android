package com.kaligrid.app;

import android.content.Context;

import com.kaligrid.data.DBHelper;
import com.kaligrid.service.EventService;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    private final Context context;

    public ServiceModule(Context context) {
        this.context = context;
    }

    @Provides
    public DBHelper provideDBHelper() {
        return new DBHelper(context);
    }

    @Provides
    public EventService provideEventService(DBHelper dbHelper) {
        return new EventService(dbHelper);
    }
}
