package cn.duoduo.flarum;

import android.app.Application;
import org.jetbrains.annotations.Nullable;

public class App extends Application {

    @Nullable
    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

}
