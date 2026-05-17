package com.voxo.bmitracker.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.voxo.bmitracker.data.BmiHistoryMapper;
import com.voxo.bmitracker.data.BmiHistoryMigrationHelper;
import com.voxo.bmitracker.data.local.AppDatabase;
import com.voxo.bmitracker.data.local.BmiHistoryDao;
import com.voxo.bmitracker.data.local.BmiHistoryEntity;
import com.voxo.bmitracker.model.BmiHistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryViewModel extends ViewModel {

    private BmiHistoryDao dao;
    private final Gson gson = new Gson();
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    // Standard Mediator/Mutable setup to guarantee Null-Safety from Day 1
    private final MutableLiveData<List<BmiHistoryEntity>> rawDatabaseSource = new MutableLiveData<>();

    // Public safe LiveData exposure triggered dynamically via Transformations
    public final LiveData<List<BmiHistory>> historyList = Transformations.switchMap(rawDatabaseSource, dbEntities -> {
        // Safe mapping architecture using non-null wrapper
        MutableLiveData<List<BmiHistory>> mappedData = new MutableLiveData<>();
        mappedData.setValue(mapEntities(dbEntities));
        return mappedData;
    });

    public void initialize(Context context) {
        if (dao != null) {
            return;
        }
        Context app = context.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(app);
        dao = db.bmiHistoryDao();

        // Link the real-time Room DAO stream to our safe LiveData pipeline
        dao.observeAllOrdered().observeForever(rawDatabaseSource::setValue);

        // Run background migration scripts safely
        ioExecutor.execute(() -> BmiHistoryMigrationHelper.migrateOnce(app, dao, gson));
    }

    private List<BmiHistory> mapEntities(List<BmiHistoryEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        List<BmiHistory> out = new ArrayList<>(entities.size());
        for (BmiHistoryEntity e : entities) {
            out.add(BmiHistoryMapper.fromEntity(e));
        }
        return out;
    }

    public void addToHistory(BmiHistory history) {
        if (dao == null) {
            return;
        }
        ioExecutor.execute(() -> {
            BmiHistoryEntity latest = dao.getLatestSync();
            if (latest != null) {
                BmiHistory asModel = BmiHistoryMapper.fromEntity(latest);
                if (history.matchesSnapshot(asModel)) {
                    return;
                }
            }
            dao.insert(BmiHistoryMapper.toEntity(history));
        });
    }

    public void clearHistory() {
        if (dao == null) {
            return;
        }
        ioExecutor.execute(dao::deleteAll);
    }

    public void deleteItem(int position) {
        if (dao == null) {
            return;
        }
        ioExecutor.execute(() -> {
            List<BmiHistoryEntity> rows = dao.getAllOrderedSync();
            if (position >= 0 && position < rows.size()) {
                dao.deleteById(rows.get(position).getId());
            }
        });
    }

    public void deleteHistoryEntry(BmiHistory history) {
        if (dao == null || history == null) {
            return;
        }
        ioExecutor.execute(() -> {
            if (history.getDatabaseId() >= 0) {
                dao.deleteById(history.getDatabaseId());
                return;
            }
            for (BmiHistoryEntity e : dao.getAllOrderedSync()) {
                if (BmiHistoryMapper.fromEntity(e).matchesSnapshot(history)) {
                    dao.deleteById(e.getId());
                    break;
                }
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        ioExecutor.shutdown();
    }
}