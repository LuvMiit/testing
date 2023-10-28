package org.niias.asrb.kn.model;

public enum Railways {
    ALL(0, ""),
    OKT(1,  "Октябрьская"),
    LKNG(10, "Калининградская"),
    MOSK(17,  "Московская"),
    GORK(24,  "Горьковская"),
    SKAV(51,  "Северо-Кавказская"),
    SEV(28,  "Северная"),
    UVOST(58,  "Юго-Восточная"),
    PRIV (61,  "Приволжская"),
    KBSH (63,  "Куйбышевская"),
    SVERD (76,  "Свердловская"),
    U_UR (80,  "Южно-Уральская"),
    Z_SIB (83,  "Западно-Сибирская"),
    KRAS (88,  "Красноярская"),
    V_SIB (92,  "Восточно-Сибирская"),
    ZAB (94,  "Забайкальская"),
    DVOST (96,  "Дальневосточная"),
    SAH (99,  "Сахалинская" ),
    CENTRAL (100,  "Центральный аппарат");
    
    private Integer dorkod;
    private String name;
    Railways( Integer dorkod,String name){
        this.name = name;
        this.dorkod = dorkod;
    }

    public static Railways isRailway(Integer dorkod) {
        for (Railways e : values()) {
            if (e.dorkod.equals(dorkod)) {
                return e;
            }
        }
        return null;
    }
    public Integer getDorkod() {
        return dorkod;
    }
}