package com.fras.msbm;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.bumptech.glide.request.target.ViewTarget;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.orm.SugarApp;
import com.orm.SugarContext;


public class App extends SugarApp {
    public static final String TAG = App.class.getSimpleName();

    @Override
    public void onCreate() {
        DexClass test = new DexClass();
        test.onCreate();
        test.attachBaseContext(this);

        super.onCreate();
        if (!FirebaseApp.getApps(this).isEmpty()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
//        DexClass test = new DexClass();
//        test.onCreate();
//        test.attachBaseContext(this);
        ViewTarget.setTagId(R.id.glide_tag);
//        SugarContext.init(this);
    }

    public class DexClass extends MultiDexApplication{
        @Override
        protected void attachBaseContext(Context base) {
            super.attachBaseContext(base);
            MultiDex.install(App.this);
        }
    }


}
