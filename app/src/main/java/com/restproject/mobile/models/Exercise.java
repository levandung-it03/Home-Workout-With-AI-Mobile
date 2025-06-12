package com.restproject.mobile.models;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Exercise implements Serializable {
    private Long exerciseId;
    private String name;
    private String levelEnum;
    private Integer basicReps;
    private String imagePublicId;
    private String imageUrl;
    private Collection<Muscle> muscles;
    private String musclesStr;
    public Exercise() {
    }

    public Exercise(Long exerciseId, String name, String levelEnum, Integer basicReps,
                    String imagePublicId, String imageUrl, Collection<Muscle> muscles) {
        this.exerciseId = exerciseId;
        this.name = name;
        this.levelEnum = levelEnum;
        this.basicReps = basicReps;
        this.imagePublicId = imagePublicId;
        this.imageUrl = imageUrl;
        this.muscles = muscles;
        setMusclesStr();
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevelEnum() {
        return levelEnum;
    }

    public void setLevelEnum(String levelEnum) {
        this.levelEnum = levelEnum;
    }

    public Integer getBasicReps() {
        return basicReps;
    }

    public void setBasicReps(Integer basicReps) {
        this.basicReps = basicReps;
    }

    public String getImagePublicId() {
        return imagePublicId;
    }

    public void setImagePublicId(String imagePublicId) {
        this.imagePublicId = imagePublicId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Collection<Muscle> getMuscles() {
        return muscles;
    }

    public void setMuscles(Collection<Muscle> muscles) {
        this.muscles = muscles;
        setMusclesStr();
    }

    public void setMusclesStr() {
        this.musclesStr = this.muscles.stream().map(map -> map.getMuscleName())
                .collect(Collectors.joining(" - "));
    }

    public String getMusclesStr() {
        return musclesStr;
    }

    public static Exercise mapping(LinkedTreeMap data) {
        Exercise result = new Exercise();

        if (data.containsKey("exerciseId") && Objects.nonNull(data.get("exerciseId")))
            result.setExerciseId(((Double) data.get("exerciseId")).longValue());

        if (data.containsKey("name") && Objects.nonNull(data.get("name")))
            result.setName(data.get("name").toString());

        if (data.containsKey("levelEnum") && Objects.nonNull(data.get("levelEnum")))
            result.setLevelEnum(data.get("levelEnum").toString());

        if (data.containsKey("basicReps") && Objects.nonNull(data.get("basicReps")))
            result.setBasicReps(((Double) data.get("basicReps")).intValue());

        if (data.containsKey("imagePublicId") && Objects.nonNull(data.get("imagePublicId")))
            result.setImagePublicId(data.get("imagePublicId").toString());

        if (data.containsKey("imageUrl") && Objects.nonNull(data.get("imageUrl")))
            result.setImageUrl(data.get("imageUrl").toString());

        if (data.containsKey("muscles") && Objects.nonNull(data.get("muscles"))) {
            List<LinkedTreeMap> musclesData = (List<LinkedTreeMap>) data.get("muscles");
            List<Muscle> muscles = musclesData.stream()
                    .map(Muscle::mapping)
                    .collect(Collectors.toList());
            result.setMuscles(muscles);
        }

        return result;
    }
}
