package com.restproject.mobile.models;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Muscle implements Serializable {
    private Long muscleId;
    private String muscleName;

    public Muscle() {
    }

    public Muscle(Long muscleId, String muscleName) {
        this.muscleId = muscleId;
        this.muscleName = muscleName;
    }

    public Long getMuscleId() {
        return muscleId;
    }

    public void setMuscleId(Long muscleId) {
        this.muscleId = muscleId;
    }

    public String getMuscleName() {
        return muscleName;
    }

    public void setMuscleName(String muscleName) {
        this.muscleName = muscleName;
    }

    public static List<Long> parseStrIdsToList(Object ids) {
        return Arrays.stream(ids.toString()
                .replaceAll("[\\[\\]]", "")
                .split(",")
            ).map(Long::parseLong).collect(Collectors.toList());
    }
    public static Muscle mapping(LinkedTreeMap data) {
        Muscle result = new Muscle();

        if (data.containsKey("muscleId") && Objects.nonNull(data.get("muscleId")))
            result.setMuscleId(((Double) data.get("muscleId")).longValue());

        if (data.containsKey("muscleName") && Objects.nonNull(data.get("muscleName")))
            result.setMuscleName(data.get("muscleName").toString());

        return result;
    }

}
