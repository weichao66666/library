package io.weichao.application;

import android.app.Application;
import android.preference.PreferenceManager;

import org.artoolkit.ar.base.assets.AssetHelper;

public class BaseApplication extends Application {
    private static Application sInstance;

    public static Application getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        ((BaseApplication) sInstance).initializeInstance();
    }

    protected void initializeInstance() {
        PreferenceManager.setDefaultValues(this, org.artoolkit.ar.base.R.xml.preferences, false);

        AssetHelper assetHelper = new AssetHelper(getAssets());
        assetHelper.cacheAssetFolder(getInstance(), "artoolkit/Data");
        assetHelper.cacheAssetFolder(getInstance(), "artoolkit/DataNFT");
        assetHelper.cacheAssetFolder(getInstance(), "artoolkit/OSG");
    }
}
