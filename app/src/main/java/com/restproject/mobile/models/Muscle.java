package com.restproject.mobile.models;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
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
}
