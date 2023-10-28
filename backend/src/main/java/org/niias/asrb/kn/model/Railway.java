package org.niias.asrb.kn.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Railway implements Serializable {
    UNKNOWN(0, "", ""),
    OKT(1, "Окт", "Октябрьская"),
    KLNG(10, "Клнг", "Калининградская"),
    MOSK(17, "Моск", "Московская"),
    GORK(24, "Горьк", "Горьковская"),
    SEV(28, "Сев", "Северная"),
    S_KAV(51, "С-Кав", "Северо-Кавказская"),
    U_VOST(58, "Ю-Вост", "Юго-Восточная"),
    PRIV(61, "Прив", "Приволжская"),
    KBSH(63, "Кбш", "Куйбышевская"),
    SVERD(76, "Сверд", "Свердловская"),
    U_UR(80, "Ю-Ур", "Южно-Уральская"),
    Z_SIB(83, "З-Сиб", "Западно-Сибирская"),
    KRAS(88, "Крас", "Красноярская"),
    V_SIB(92, "В-Сиб", "Восточно-Сибирская"),
    ZAB(94, "Заб", "Забайкальская"),
    DVOST(96, "Двост", "Дальневосточная"),
    SAH(99, "Сах", "Сахалинская", true),

    CENTRAL(100, "ЦРБ", "Центральный аппарат");

    public static final List<Railway> ALL = Arrays.asList(
            OKT, KLNG, MOSK, GORK, SEV, S_KAV, U_VOST, PRIV, KBSH, SVERD, U_UR,
            Z_SIB, KRAS, V_SIB, ZAB, DVOST
    );
    private int dorKod;
    private String value;
    private String shortName;
    private boolean deprecated;

    Railway(int dorKod, String shortName, String value) {
        this.value = value;
        this.dorKod = dorKod;
        this.shortName = shortName;
    }

    Railway(int dorKod, String shortName, String value, boolean deprecated) {
        this.value = value;
        this.dorKod = dorKod;
        this.shortName = shortName;
        this.deprecated = deprecated;
    }

    public static Railway valueOf(Integer dorKod) {
        if (dorKod == null)
            return null;

        for (Railway railway : values()) {
            if (railway.dorKod == dorKod)
                return railway;
        }

        return null;
    }


    public int getDorKod() {
        return dorKod;
    }

    public void setDorKod(int dorKod) {
        this.dorKod = dorKod;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }
}
