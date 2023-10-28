package org.niias.asrb.kn.model;

public enum SpecialPred {

    RZD(2507, "ОАО \"РЖД\""),
    CDIM(15508, "ЦДИМ");

    private int id;
    private String name;

    SpecialPred(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static SpecialPred findById(int predId){
        for (SpecialPred p: values())
            if (p.id == predId)
                return p;
        return null;
    }

}
