package org.niias.asrb.kn.service;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.niias.asrb.kn.controller.PredController;
import org.niias.asrb.kn.model.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static org.niias.asrb.kn.model.QBlank.blank;
import static org.niias.asrb.kn.model.QHPred.hPred;
import static org.niias.asrb.kn.model.QHStan.hStan;
import static org.niias.asrb.kn.model.QHVertSv2.hVertSv2;

@Service
public class VerticalService {

    @Resource
    private JPAQueryFactory qf;

    private PredLevel getLevel(int maxSize, int vSize){
        if (vSize == 1)
            return PredLevel.central;
        if (vSize == 2)
            return PredLevel.regional;
        if (vSize > 2)
            return PredLevel.linear;
        throw new IllegalStateException();
    }

    public HStan getStan(Integer predId)
    {
        return (HStan) qf.from(hStan)
                .where(hStan.stanId.in(JPAExpressions.selectFrom(hPred).select(hPred.stanId).where(hPred.id.eq(predId), hPred.dateKd.gt(new Date()))), hStan.dateKd.gt(new Date()))
                .fetchFirst();
    }

    public Map<Integer, Integer> getDorKodMap(List<Integer> predId)
    {
        return  qf.from(hPred)
                .select(hPred.id, hStan.dorKod)
                .leftJoin(hStan).on(hPred.stanId.eq(hStan.stanId))
                .where(hPred.id.in(predId), hPred.dateKd.gt(new Date()))
                .distinct()
                .fetch()
                .stream()
                .map(it-> Arrays.asList(it.toArray()))
                .collect(Collectors.toMap(it-> (Integer) it.get(0), it-> (Integer) it.get(1)));
    }

    public VertInfo getVertInfo(Integer predId){
        HPred pred = this.getPred(predId);
        VertInfo res =  findMainRec(new VertInfo(pred, 1));
        CentralPred centralPred = CentralPred.findById(res.main.getId());
        if (centralPred == CentralPred.CDI) switch (pred.getGrId())
        {
            case 7:
                return new VertInfo(pred, res.main, null, res.vertSize, PredLevel.central);
            case 10:
                return new VertInfo(pred, res.main, res.reg, res.vertSize, PredLevel.regional);
            default:
                return new VertInfo(pred, res.main, res.reg, res.vertSize, PredLevel.linear);
        }

        return new VertInfo(pred, res.main, res.reg, res.vertSize, getLevel(centralPred.getVertSize(), res.vertSize));
    }

    public List<HPred> getChildrens(int predId){
        return getChildrens(Arrays.asList(predId));
    }

    public List<HPred> getDescendants(Integer predId){
        return getDescendants(Arrays.asList(getPred(predId)));
    }



    private List<HPred> getDescendants(List<HPred> preds){
        List<HPred> ids = (List<HPred>) qf.from(hPred)
                .where(hPred.dateKd.gt(new Date()), hPred.id.in(
                        JPAExpressions.selectFrom(hVertSv2).select(hVertSv2.predNid)
                                .where(hVertSv2.predVid.in(preds.stream().map(it->it.getId()).collect(Collectors.toList())), hVertSv2.corTip.ne("D"), hVertSv2.dateKd.gt(new Date())).distinct()
                )).orderBy(hPred.grId.asc(), hPred.id.asc()).fetch();

        if (!ids.isEmpty())
            ids.addAll(getDescendants(ids));
        return ids;
    }

    public List<HPred> getChildrens(List<Integer> preds){
        return (List<HPred>) qf.from(hPred).where(hPred.dateKd.gt(new Date()), hPred.id.in(
                JPAExpressions.selectFrom(hVertSv2).select(hVertSv2.predNid)
                        .where(hVertSv2.predVid.in(preds), hVertSv2.corTip.ne("D"), hVertSv2.dateKd.gt(new Date())).distinct()
        )).orderBy(hPred.grId.asc(), hPred.id.asc()).fetch();
    }

    public HPred getDCSPredByDorKodAndNom(int dorKod, int nom) {
        List preds =  qf.from(hPred).where(hPred.dateKd.gt(new Date()), hPred.grId.eq(20),
                hPred.vdId.eq(121),
                hPred.nom.eq(nom),
                hPred.corTip.ne("D"),
                hPred.dateKd.gt(new Date()),
                hPred.railway.eq(Railway.valueOf(dorKod))).fetch();
        return preds.isEmpty() ? null : (HPred) preds.get(0);
    }

    public HPred getDPredByDorKod(int dorKod) {
        List preds =  qf.from(hPred).where(hPred.dateKd.gt(new Date()), hPred.grId.eq(10),
                hPred.vdId.in(100, 110),
                hPred.corTip.ne("D"),
                hPred.dateKd.gt(new Date()),
                hPred.railway.eq(Railway.valueOf(dorKod))).fetch();
        return preds.isEmpty() ? null : (HPred) preds.get(0);
    }

    public static final class RouteToUpperEntNotFound extends IllegalStateException{
        public RouteToUpperEntNotFound(Integer id){
            super("Route to upper enterprise not found id=" + id);
        }
    }

    public static final class MultipleRoutesToUpperEnt extends IllegalStateException{
        public MultipleRoutesToUpperEnt(Integer id){
            super("Found multiple routes to upper enterprise with id=" + id);
        }
    }


    public HPred getPred(int predId){
        return (HPred) qf.from(hPred).where(hPred.dateKd.gt(new Date()), hPred.id.eq(predId)).fetchOne();
    }

    public HPred getDSPredByStan(int stan){
        List preds =  qf.from(hPred).where(hPred.dateKd.gt(new Date()), hPred.stanId.eq(stan),
                hPred.corTip.ne("D"),
                hPred.dateKd.gt(new Date()),
                hPred.vdId.in(100, 110)).fetch();
        return preds.isEmpty() ? null : (HPred) preds.get(0);
    }

    public List<HPred>  getUppers(HPred pred){
        return  (List<HPred>)  qf.from(hPred).where(hPred.dateKd.gt(new Date()), hPred.id.in(
                JPAExpressions.selectFrom(hVertSv2).select(hVertSv2.predVid)
                        .where(hVertSv2.predNid.eq(pred.getId()), hVertSv2.corTip.ne("D"),
                                hVertSv2.dateKd.gt(new Date())).distinct()
        )).fetch();
    }

    private VertInfo findMainRec(VertInfo info){
        if (CentralPred.isCentralPred(info.main.getId()))
            return info;
        List<HPred> uppers = getUppers(info.main);

        if (uppers.isEmpty())
            throw new RouteToUpperEntNotFound(info.main.getId());
        HPred upper = uppers.size() == 1 ? uppers.get(0) : null;
        if (uppers.size() > 1){
            System.out.println("many route to upper for pred id =" + info.main.getId());
            List<HPred> upperTest = Collections.emptyList();
            //Здесь мы разруливаем вилку типа
            //ШЧ35 МОСК -> [Ш МОСК ДИ, РЕГ-7 МОСК] выкидываем РЕГ-7 (vd_id=0) так как эта ветка ведет к управлению МОСК Ж.Д. а не к ЦДИ
            //или НКИ ПРИВ -> [ЦКИ, ПРИВ Ж.Д.] выкидываем ПРИВ Ж.Д. (vd_id=0) так как нам нужно ЦКИ
            if (uppers.size() == 2 && uppers.stream().anyMatch(it->it.getVdId().equals(0)))
                upperTest = uppers.stream().filter(it-> !it.getVdId().equals(0)).collect(Collectors.toList());
            //Здесь мы разруливаем вилку типа [ЦП или ГОРК ДИ] берем в этом случае ГОРК ДИ, определяем его по vd_id = 7800
            else if (uppers.size() == 2 && uppers.stream().anyMatch(it->it.getGrId().equals(7) && !it.getVdId().equals(7800)) && uppers.stream().anyMatch(it->it.getGrId() == 10 &&it.getVdId().equals(7800)))
                upperTest = uppers.stream().filter(it-> it.getVdId().equals(7800)).collect(Collectors.toList());
            //Вилка типа -> [Ш ОКТ ДИ, ИЧ-3 ОКТ] берем Ш ОКТ ДИ по vd_id <> 7800 потомучто надо что-то взять
            else if (uppers.size() == 2 && uppers.stream().anyMatch(it->it.getVdId().equals(7800)))
                upperTest = uppers.stream().filter(it-> !it.getVdId().equals(7800)).collect(Collectors.toList());
            // игнорим ДЦС при поиске ДС
            else if (uppers.size() == 2 && uppers.stream().anyMatch(it->it.getGrId().equals(20) && it.getVdId().equals(121)))
                upperTest = uppers.stream().filter(it-> !(it.getGrId().equals(20) && it.getVdId().equals(121))).collect(Collectors.toList());
            if (upperTest.size() != 1)
                throw new MultipleRoutesToUpperEnt(info.main.getId());
            upper = upperTest.get(0);
        }

        if (upper.getId() != SpecialPred.RZD.getId() && CentralPred.isCentralPred(upper.getId()))
            return new VertInfo(upper, info.main.getId() ==  SpecialPred.CDIM.getId() ? info.reg : info.main, info.vertSize + 1);

        if (upper.getId() == SpecialPred.RZD.getId())
        {
            if (!CentralPred.isCentralPred(info.main.getId()))
                throw new IllegalStateException("Not Supported central enterprise id=" + info.main.getId());
            return new VertInfo(info.main, info.vertSize);
        }

        return findMainRec(new VertInfo(upper, info.main, ++info.vertSize));
    }

    public List<PredController.PredValue> getPredOnRckuAndLevel(Integer year, Railway railway, PredLevel level) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(blank.year.eq(year));
        predicates.add(blank.dorKod.eq(railway.getDorKod()));
        if (level != null && !level.equals(PredLevel.all)) predicates.add(blank.level.eq(level));

        List<Tuple> preds = qf.select(blank.predId, blank.predName)
                .from(blank)
                .where(predicates.toArray(new Predicate[0]))
                .distinct()
                .fetch();

        return preds.stream().map(bl -> new PredController.PredValue(bl.get(0,Integer.class), bl.get(1, String.class))).collect(Collectors.toList());
    }


    public static class VertInfo {

        public VertInfo(){}

        public VertInfo(HPred main, HPred reg, int vertSize){
            this.main = main;
            this.reg = reg;
            this.vertSize = vertSize;
        }

        public VertInfo(HPred main, int vertSize){
            this.main = main;
            this.vertSize = vertSize;
        }

        public VertInfo(HPred pred, HPred main, HPred reg, int vertSize, PredLevel level){
            this.pred = pred;
            this.main = main;
            this.reg = reg;
            this.vertSize = vertSize;
            this.level = level;
        }

        private HPred main;
        private HPred reg;
        private HPred pred;
        private PredLevel level;
        private int vertSize;

        public HPred getMain() {
            return main;
        }

        public HPred getReg() {
            return reg;
        }

        public HPred getPred() {
            return pred;
        }

        public PredLevel getLevel() {
            return level;
        }

        public int getVertSize() {
            return vertSize;
        }
    }




}
