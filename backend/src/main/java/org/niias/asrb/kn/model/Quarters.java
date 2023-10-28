package org.niias.asrb.kn.model;

public enum Quarters {

    quarter1("I квартал", "I"),
    quarter2("II квартал", "II"),
    quarter3("III квартал", "III"),
    quarter4("IV квартал", "IV");

    private String name;
    private String shortName;

    Quarters(String name, String shortName){
        this.name = name;
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }
}
