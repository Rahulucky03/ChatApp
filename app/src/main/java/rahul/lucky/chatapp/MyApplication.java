package rahul.lucky.chatapp;

import android.os.Handler;
import android.support.multidex.MultiDexApplication;

/**
 * Created by Rahulucky03 on 03-02-2019.
 */
public class MyApplication extends MultiDexApplication {

    private static MyApplication Instance;
    public static volatile Handler applicationHandler = null;

    @Override
    public void onCreate() {
        super.onCreate();

        Instance=this;

        applicationHandler = new Handler(getInstance().getMainLooper());

        NativeLoader.initNativeLibs(MyApplication.getInstance());

    }

    public static MyApplication getInstance()
    {
        return Instance;
    }

}
