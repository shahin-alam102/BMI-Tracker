package com.voxo.bmitracker.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voxo.bmitracker.data.local.BmiHistoryDao;
import com.voxo.bmitracker.model.BmiHistory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * One-time import of legacy JSON history from SharedPreferences into Room.
 */
public final class BmiHistoryMigrationHelper {

    private static final String PREF_NAME = "bmi_history";
    private static final String HISTORY_KEY = "history_list";
    private static final String MIGRATION_FLAG = "room_migration_done";
    private static final Object LOCK = new Object();

    private BmiHistoryMigrationHelper() {
    }

    public static void migrateOnce(Context appContext, BmiHistoryDao dao, Gson gson) {
        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(MIGRATION_FLAG, false)) {
            return;
        }
        synchronized (LOCK) {
            if (prefs.getBoolean(MIGRATION_FLAG, false)) {
                return;
            }
            String json = prefs.getString(HISTORY_KEY, null);
            if (json != null && !json.isEmpty() && !"[]".equals(json)) {
                try {
                    Type type = new TypeToken<ArrayList<BmiHistory>>() {
                    }.getType();
                    List<BmiHistory> list = gson.fromJson(json, type);
                    if (list != null) {
                        for (BmiHistory h : list) {
                            dao.insert(BmiHistoryMapper.toEntity(h));
                        }
                    }
                } catch (Exception ignored) {
                    // If JSON is corrupt, skip import and still mark migrated.
                }
            }
            prefs.edit().putBoolean(MIGRATION_FLAG, true).remove(HISTORY_KEY).apply();
        }
    }
}
