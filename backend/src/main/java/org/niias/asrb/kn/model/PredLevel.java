package org.niias.asrb.kn.model;

/**
 * Уровень предприятия
 */
public enum PredLevel {
    all("Все",null),
    central("Центральный", 7),
    regional("Региональный", 10),
    linear("Линейный", null);

    private String label;
    private Integer grId;

    PredLevel(String label, Integer grId){
        this.label = label;
        this.grId = grId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getGrId() {
        return grId;
    }

    public void setGrId(Integer grId) {
        this.grId = grId;
    }
}
