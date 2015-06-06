package sample.scijoker.com.urclient.app;

import android.app.Application;

import com.scijoker.urclient.URClientService;

/**
 * Created by scijoker on 06.06.15.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        URClientService.init(getApplicationContext(), true);
    }
}
