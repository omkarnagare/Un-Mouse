package com.nagare.balkrishna.omkar.unmouse.App;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.nagare.balkrishna.omkar.unmouse.GreenDao.DaoMaster;
import com.nagare.balkrishna.omkar.unmouse.GreenDao.DaoSession;

import org.greenrobot.greendao.database.Database;

import java.io.File;
public class UnMouseApp
        extends Application
{
    private static final String TAG = "UnMouseApp";

    public static final boolean ENCRYPTED = false;

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        setUpDB();

        setUpAdds();

    }

    private void setUpAdds() {

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8786806562583765~9737954739");

    }

    private void setUpDB() {

        File   file   = new File(getCacheDir(), "db");
        if (!file.exists())
        {
            if (file.mkdir())
            {
                Log.d(TAG,
                        "Directory is created!");
            }
            else
            {
                Log.d(TAG,
                        "Failed to create directory!");
            }
        }

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? file +"/test-db-encrypted" : file +"/test-db");
        Database                db     = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
