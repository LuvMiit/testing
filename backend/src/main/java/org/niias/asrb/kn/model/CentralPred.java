package org.niias.asrb.kn.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CentralPred {

    CL(2391,"ЦЛ", 2),
    CR(2409,"ЦР", 2),
    CSS(2433,"ЦСС", 3),
    CBZ(2436, "ЦБЗ", 2),
    CRZ(2499,"ЦРЖ", 2),
    COS(2552,"ЦОС", 2),
    CRI(2554,"ЦРИ", 3),
    CI(2559,"ЦИ", 2),
    AHU(2586,"АХУ", 2),
    ZDR(2592,"ЖЕЛДОРРАСЧЕТ", 1),
    CF(2597,"ЦФ", 2),
    CDZ(2610,"ЦДЗ", 3),
    CLDR(2612,"ЦКАДР", 2),
    CO(2619,"ЦО", 2),
    CU(2621,"ЦЮ", 2),
    CTEX(2622,"ЦТЕХ", 2),
    TI(2623,"ТИ", 1),
    CBT(2624,"ЦБТ", 3),
    RZDS(2625,"РЖДС", 3),
    CRB(2629,"ЦРБ", 3),
    CSR(2632,"ЦСР", 3),
    //real 4 vert
    CUKS(2634,"ЦУКС", 3),
    //real 4 vert
    CD(2643,"ЦД", 3),
    GVC(2646,"ГВЦ", 3),
    CMGR(2661,"ЦМ", 3),
    DMED(5672,"ДМЕД", 1),
    DKSS(6427,"ДКСС", 2),
    DLP(6719,"ЦЛП", 3),
    CKI(6747,"ЦКИ", 2),
    CINV(7074,"ЦИНВ", 2),
    DKRS(7230,"ДКРС", 3),
    DRTP(7261,"ЦРТП", 1),
    DMO(7280,"ДМО", 2),
    CRV(7381,"ЦРВ", 1),
    CUDZ(7382,"ЦУДЗ", 2),
    CFTO(7383,"ЦФТО", 3),
    CUNR(7384,"ЦУНР", 2),
    CUEP(7385,"ЦУЭП", 1),
    REFSRV(7386,"РЕФСЕРВИС", 2),
    ZDUCHET(7387,"ЖЕЛДОРУЧЕТ", 3),
    COTEN(7388,"ЦОТЭН", 1),
    //real 4 vert
    EE(7390,"ЭЭ", 3),
    ROSZDSTROI(7392,"РОСЖЕЛДОРСТРОЙ", 3),
    CZDK(7396,"ЦЖДК", 2),
    FPK(7398,"ФПК", 3),
    CISSO(7401,"ЦИССО", 1),
    DSZTU(7402,"ДСЖТЮ", 1),
    ZDRM(7404,"ЖДРМ", 2),
    DZV(7728,"ДЖВ", 3),
    //real 4 vert
    CDRP(8609,"ЦДРП", 3),
    DOSS(8809,"ДОСС", 3),
    //real 4 vert
    CTR(12077,"ЦТР", 3),
    //real 4 vert
    CT(13006,"ЦТ", 3),
    //real 4 vert
    CM(13015,"ЦМ - ФИЛИАЛ ОАО РЖД", 3),
    CDTV(13022,"ЦДТВ", 3),
    //real 4 vert
    VRK1(13028,"ВРК-1", 3),
    //real 4 vert
    VRK2(13029,"ВРК-2", 3),
    //real 4 vert
    VRK3(13030,"ВРК-3", 3),
    //real 4 vert    
    CDMV(13316,"ЦДМВ", 3),
    //real 4 vert
    CDPO(13317,"ЦДПО", 3),
    //Special, 7 real vert
    CDI(13419,"ЦДИ", 3),
    CP(2421,"ЦП", 1),
    CEU(13821,"ЦЭУ", 2),
    CRSU(13832,"ЦРСУ", 1),
    CUU(13834,"ЦУУ", 1),
    CBS(13859,"ЦБС", 2),
    DKRE(13861,"ДКРЭ", 3),
    CZD(14453,"ЦЖД", 1),
    CKO(14457,"ЦКО", 1),
    APSEK(14484,"АППКОРПСЕКР", 1),
    CA(14627,"ЦА", 3),
    CUIP(14630,"ЦУИП", 1),
    CMTP(14756,"ЦМТП", 1),
    CIR(15491,"ЦИР", 1),
    CDM(15773,"ЦДМ ЦДИ", 1),

    OKT(2212, "ОКТ Ж.Д.", 1, 1),
    KLNG(1602, "КЛНГ Ж.Д.", 1, 10),
    MOSK(2705, "МОСК Ж.Д.", 1, 17),
    GORK(1347, "ГОРЬК Ж.Д.", 1, 24),
    SEV(4732, "СЕВ Ж.Д.", 1, 28),
    S_KAV(3479, "С-КАВ Ж.Д.", 1, 51),
    U_VOST(1185, "Ю-ВОСТ Ж.Д", 1, 58),
    PRIV(3784, "ПРИВ Ж.Д.", 1, 61),
    KBSH(2026, "КБШ Ж.Д.", 1, 63),
    SVERD(3991, "СВЕРД Ж.Д.", 1, 76),
    U_UR(4421, "Ю-УР Ж.Д", 1, 80),
    Z_SIB(3121, "З-СИБ Ж.Д.", 1, 83),
    KRAS(313, "КРАС Ж.Д.", 1, 88),
    V_SIB(1511, "В-СИБ Ж.Д.", 1, 92),
    ZAB(4530, "ЗАБ Ж.Д.", 1, 94),
    DVOST(589, "ДВОСТ Ж.Д.", 1, 96);

    private int id;
    private String name;
    private int vertSize;
    private Integer dorKod;

    CentralPred(int id, String name, int vertSize){
        this.id = id;
        this.name = name;
        this.vertSize = vertSize;
    }

    CentralPred(int id, String name, int vertSize, int dorKod){
        this(id, name, vertSize);
        this.dorKod = dorKod;
    }

    public boolean isRailway(){
        return dorKod != null;
    }

    public Integer getDorKod(){
        return dorKod;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVertSize() {
        return vertSize;
    }

    public static CentralPred findById(int predId){
        for (CentralPred p: values())
            if (p.id == predId)
                return p;
        return null;
    }

    public static List<CentralPred> getRailwayCentralPreds(){
        return Arrays.stream(values()).filter(it-> it.isRailway()).collect(Collectors.toList());
    }

    public static boolean isCentralPred(int predId){
        return findById(predId) != null;
    }
}
