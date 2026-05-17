package com.voxo.bmitracker.data;

import com.voxo.bmitracker.data.local.BmiHistoryEntity;
import com.voxo.bmitracker.model.BmiHistory;

public final class BmiHistoryMapper {

    private BmiHistoryMapper() {}

    public static BmiHistoryEntity toEntity(BmiHistory model) {
        BmiHistoryEntity e = new BmiHistoryEntity();
        e.setBmi(model.getBmi());
        e.setCategory(model.getCategory());
        e.setColor(model.getColor());
        e.setHeight(model.getHeight());
        e.setWeight(model.getWeight());
        e.setHeightUnit(model.getHeightUnit());
        e.setWeightUnit(model.getWeightUnit());
        e.setAge(model.getAge());
        e.setGender(model.getGender());
        e.setTimestamp(model.getTimestamp());
        return e;
    }

    public static BmiHistory fromEntity(BmiHistoryEntity e) {
        BmiHistory h = new BmiHistory();
        h.setDatabaseId(e.getId());
        h.setBmi(e.getBmi());
        h.setCategory(e.getCategory());
        h.setColor(e.getColor());
        h.setHeight(e.getHeight());
        h.setWeight(e.getWeight());
        h.setHeightUnit(e.getHeightUnit());
        h.setWeightUnit(e.getWeightUnit());
        h.setAge(e.getAge());
        h.setGender(e.getGender());
        h.setTimestamp(e.getTimestamp());
        return h;
    }
}
