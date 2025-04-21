package com.restproject.mobile.models;

import java.io.Serializable;
import java.util.Collection;

public class Exercise implements Serializable {
    private Long exerciseId;
    private String name;
    private String levelEnum;
    private Integer basicReps;
    private String imagePublicId;
    private String imageUrl;
    private Collection<Muscle> muscles;

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
    }
}
