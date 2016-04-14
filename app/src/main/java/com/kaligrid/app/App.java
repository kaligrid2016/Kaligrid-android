package com.kaligrid.app;

import android.app.Application;

public class App extends Application {

    private static App instance;
    private ObjectGraph objectGraph;

    public App() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.Initializer.init();
    }

    public static ObjectGraph getObjectGraph() {
        return instance.objectGraph;
    }
}
