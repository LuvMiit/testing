package org.niias.asrb.kn.model;

public enum Period {

    month("Ежемесячно"),
    quarter("Ежеквартально"),
    halfYear("Один раз в полугодие"),
    year("Один раз в год"),
    onDemand("При необходимости"),
    selected("Выбранные месяцы");

    private String label;

    Period(String label){
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
