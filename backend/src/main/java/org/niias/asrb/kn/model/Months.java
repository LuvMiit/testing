package org.niias.asrb.kn.model;

public enum Months {

    january("январь"),
    february("февраль"),
    march("март"),
    april("апрель"),
    may("май"),
    june("июнь"),
    july("июль"),
    august("август"),
    september("сентябрь"),
    october("октябрь"),
    november("ноябрь"),
    december("декабрь");

    private String name;

    Months(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
