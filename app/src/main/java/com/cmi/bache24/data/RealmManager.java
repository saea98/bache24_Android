package com.cmi.bache24.data;

import android.content.Context;

import com.cmi.bache24.data.model.realm.Stretch;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by omar on 5/26/16.
 */
public class RealmManager {

    private static RealmManager mInstance = null;
    private Realm mRealmManager;

    public static synchronized RealmManager getInstance(Context context) {
        if (mInstance == null)
            mInstance = new RealmManager(context);

        return mInstance;
    }

    public RealmManager(Context context) {
        RealmConfiguration configuration = new RealmConfiguration.Builder(context).build();
        mRealmManager = Realm.getInstance(configuration);
    }

    public void close() {
        if (mRealmManager != null)
            mRealmManager.close();
    }

    public List<Stretch> getStretches (String filter) {
        RealmResults<Stretch> stretches = mRealmManager.where(Stretch.class)
                .contains("cleanName", filter, Case.INSENSITIVE)
                .findAll();

        return stretches;
    }
}
