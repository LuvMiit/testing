package org.niias.asrb.kn.service;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.niias.asrb.kn.AppContext;
import org.niias.asrb.kn.controller.PredController;
import org.niias.asrb.kn.export.ExportModel;
import org.niias.asrb.kn.model.*;
import org.niias.asrb.kn.util.CompletionUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static org.niias.asrb.kn.model.QBlank.blank;

@Service
public class ReportService {

    @Inject
    private VerticalService vs;

    @Inject
    private JPAQueryFactory qf;

    @Value
    @Builder(toBuilder = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReportResult implements ExportModel {
        String status = "ok";
        PredLevel level;
        ReportParam param;
        String centralName;
        String regionalName;
        String linearName;
        ReportType reportType;
        List<ReportItem> items;
        boolean RKCU;
    }

    public enum ReportType {
        ALL_CENTRAL,
        ALL_REGIONAL,
        ALL_LINEAR,
        LINER_PRED,
        POST,
        RCKU,
        USR
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReportParam {
        public Integer central;
        public Integer regional;
        public Integer linear;
        public Integer year;
        public List<String> post;
        public PredLevel level;
        public Integer dorKod;
        public Boolean rcku;
        public Boolean usr;
        public Integer predId;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        public LocalDate curDate;

        public ReportType getReportType() {
            if (post != null)
                return ReportType.POST;
            if (rcku != null && rcku)
                return ReportType.RCKU;
            if (usr != null && usr)
                return ReportType.USR;
            if (central == null && regional == null && linear == null)
                return ReportType.ALL_CENTRAL;
            if (central != null && regional == null && linear == null)
                return ReportType.ALL_REGIONAL;
            if (central != null && regional != null && linear == null)
                return ReportType.ALL_LINEAR;
            if (central != null && regional != null && linear != null)
                return ReportType.LINER_PRED;

            throw new IllegalStateException();
        }

        @JsonIgnore
        Predicate[] getGroupPredicates() {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(blank.year.eq(year));
            if (central != null && regional == null) {
                predicates.add(blank.mainId.eq(central));
                predicates.add(blank.level.eq(PredLevel.central));
            }
            if (central != null && regional != null) {
                predicates.add(blank.regId.eq(regional));
                predicates.add(blank.level.eq(PredLevel.regional));
            }
            return predicates.toArray(new Predicate[0]);
        }

        @JsonIgnore
        Predicate[] getPredicates() {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(blank.year.eq(year));

            if (rcku != null && rcku) {
                if (dorKod != null) predicates.add(blank.dorKod.eq(dorKod));
                if (level != null && level != PredLevel.all) predicates.add(blank.level.eq(level));
                if (predId != null && predId != -1) predicates.add(blank.predId.eq(predId));
                return predicates.toArray(new Predicate[0]);
            }

            if (central == null) {
                predicates.add(blank.mainId.in(Arrays.stream(CentralPred.values()).map(it -> it.getId()).collect(Collectors.toList())));
                predicates.add(blank.level.eq(PredLevel.central));
            } else {
                predicates.add(blank.mainId.eq(central));
                UserKn user = AppContext.getBean(UserKn.class);
                if (CentralPred.findById(central) == CentralPred.CRB && user.getVertical().regId != null)
                    predicates.add(blank.dorKod.eq(user.getVertical().dorKod));
            }

            //Post filter
            if (post != null && level != null && central != null) {
                predicates.add(blank.postName.in(post));
                predicates.add(blank.level.eq(level));
            }

            if (post== null && central != null && regional == null && linear == null)
                predicates.add(blank.level.in(PredLevel.regional));

            if (post== null && central != null && regional != null && linear == null) {
                predicates.add(blank.regId.eq(regional));
                predicates.add(blank.level.in(PredLevel.linear));
            }

            if (post== null && central != null && regional != null && linear != null) {
                predicates.add(blank.regId.eq(regional));
                predicates.add(blank.level.in(PredLevel.linear));
                predicates.add(blank.predId.eq(linear));
            }

            return predicates.toArray(new Predicate[0]);
        }
    }

    public ReportResult report(ReportParam param) {
        ReportResult.ReportResultBuilder resultBuilder = ReportResult.builder();
        resultBuilder.param(param);
        if (param.central != null)
            resultBuilder.centralName(vs.getPred(param.central).getSname());
        if (param.regional != null)
            resultBuilder.regionalName(vs.getPred(param.regional).getName());
        if (param.linear != null)
            resultBuilder.linearName(vs.getPred(param.linear).getName());
        if (param.curDate == null)
            param.curDate = LocalDate.now();
        resultBuilder.reportType(param.getReportType());
        resultBuilder.items(process(param));
        resultBuilder.level=param.level;
        return resultBuilder.build();
    }

    @Data
    public static class ReportItem {
        private String rkcuList;
        private Integer linear;
        private String userName;
        private String post;
        private String predName;
        private Integer mainId;
        private Integer regId;
        private Integer predId;
        private List<ValuePair> values;
        private String clazz = "";
        private Integer dorKod;
        private Integer year;
        private Integer blankId;
        private PredLevel predLevel;
        public Railways RKCU;
        public void addClazz(String clazz) {
            this.clazz += " " + clazz;
        }

        @JsonIgnore
        public static final Function<List<ReportItem>, List<ValuePair>> accByMonth = ilist -> IntStream.range(0, 12).boxed().map(i -> ilist.stream().map(it -> it.getValues().get(i))
                .reduce((a, b) -> ValuePair.accumulate(a, b)).orElse(new ValuePair(0,0))).collect(Collectors.toList());

    }

    @Value
    public static class ValuePair {
        public int completed;
        public int failed;

        @JsonValue
        public List<Integer> getValue() {
            return Arrays.asList(completed, failed);
        }

        public static ValuePair accumulate(ValuePair a, ValuePair b) {
            return new ValuePair(a.completed + b.completed, a.failed + b.failed);
        }

    }

    private BiFunction<Blank, LocalDate, ReportItem> blankMap = (blank, curDate) -> {
        ReportItem ri = new ReportItem();
        ri.setPredName(blank.getPredName());
        ri.setPost(blank.getPostName());
        ri.setUserName(blank.getCreated().getName());
        ri.setMainId(blank.getMainId());
        ri.setRegId(blank.getRegId());
        ri.setPredId(blank.getPredId());
        ri.setDorKod(blank.getDorKod());
        ri.setBlankId(blank.getId());
        ri.setPredLevel(blank.getLevel());
        ri.setYear(blank.getYear());
        ri.values = blank.getNorms().stream().map(n -> calcNormValue(blank.getYear(), n, curDate)).reduce((a, b) ->
                IntStream.range(0, 12).boxed().map(i -> ValuePair.accumulate(a.get(i), b.get(i))).collect(Collectors.toList())
        ).orElse(IntStream.range(0, 12).boxed().map(i -> new ValuePair(0,0)).collect(Collectors.toList()));

        return ri;
    };

    private List<ReportItem> process(ReportParam param){
        List<Blank> blankList = (List<Blank>) qf.from(blank)
                .where(param.getPredicates())
                .fetch();

        List<ReportItem> riList = blankList.stream()
                .map(blank-> blankMap.apply(blank, param.curDate))
                .collect(Collectors.toList());


        if (param.getReportType() == ReportType.ALL_CENTRAL)
        {
            //add itogo
            ReportItem itogo = new ReportItem();
            itogo.setPredName("Итого");
            itogo.addClazz("itogo");
            itogo.setPredLevel(riList.get(0).getPredLevel());
            itogo.values = ReportItem.accByMonth.apply(riList);

            riList = riList.stream().collect(Collectors.groupingBy(it-> it.getMainId())).values().stream().map(list-> {
                ReportItem item = new ReportItem();
                item.setPredName(CentralPred.findById(list.get(0).mainId).getName());
                item.setPredId(list.get(0).getPredId());
                item.setYear(list.get(0).getYear());
                item.setPredLevel(list.get(0).getPredLevel());
                item.values = ReportItem.accByMonth.apply(list);
                return item;
            }).collect(Collectors.toList());

            riList.sort(Comparator.comparing(x-> x.getPredName()));
            riList.add(itogo);
        }

        if (param.getReportType() == ReportType.LINER_PRED)
        {
            //add itogo
            ReportItem itogo = new ReportItem();
            itogo.setPredName("Итого");
            itogo.addClazz("itogo");
            itogo.values = ReportItem.accByMonth.apply(riList);
if(param.linear!=null) {
    itogo.setLinear(param.linear);
    itogo.setPredId(param.linear);
    itogo.setPredLevel(PredLevel.linear);
}
            itogo.setYear(param.year);
            List<ReportItem> result = new ArrayList<>();
            riList.sort(Comparator.comparing(x-> x.getPredName()));
            result.addAll(riList);
            result.add(itogo);
            return result;
        }


        if (param.getReportType() == ReportType.ALL_REGIONAL)
        {
            List<ReportItem> result = new ArrayList<>();
            result.addAll(getItogo(param));

            riList.sort(Comparator.comparing(x-> x.getPredName()));
            riList = riList.stream().collect(Collectors.groupingBy(it-> it.getRegId())).values().stream().flatMap(list-> {
                ReportItem item = new ReportItem();
                item.setPredName("Итого по " + vs.getPred(list.get(0).getRegId()).getName());
                item.addClazz("itogo");
                item.setPredId(list.get(0).getPredId());
                item.setYear(list.get(0).getYear());
                item.setPredLevel(list.get(0).getPredLevel());
                item.values = ReportItem.accByMonth.apply(list);
                List<ReportItem> res = new ArrayList<>(Arrays.asList(item));
                res.addAll(list);
                return res.stream();
            }).collect(Collectors.toList());

            result.addAll(riList);
            return result;
        }

        if (param.getReportType() == ReportType.ALL_LINEAR)
        {
            List<ReportItem> result = new ArrayList<>();
            result.addAll(getItogo(param));

            riList.sort(Comparator.comparing(x-> x.getPredName()));
            riList = riList.stream().collect(Collectors.groupingBy(it-> it.getPredId())).values().stream().flatMap(list-> {
                ReportItem item = new ReportItem();
                item.setPredName("Итого по " + vs.getPred(list.get(0).getPredId()).getName());
                item.addClazz("itogo");
                item.setPredId(list.get(0).getPredId());
                item.setYear(list.get(0).getYear());
                item.setPredLevel(list.get(0).getPredLevel());
                item.values = ReportItem.accByMonth.apply(list);
                List<ReportItem> res = new ArrayList<>(Arrays.asList(item));
                res.addAll(list);
                return res.stream();
            }).collect(Collectors.toList());

            result.addAll(riList);
            return result;
        }

        if (param.getReportType() == ReportType.POST)
        {
            List<ReportItem> result = new ArrayList<>();
            riList.sort(Comparator.comparing(x-> x.getPredName()));
            result.addAll(riList);
            return result;
        }

        if (param.getReportType() == ReportType.RCKU)
        {
            if (param.dorKod == null) {
                StringBuilder RKCUList=new StringBuilder();
                HashMap<Integer, List<ReportItem>> map = new HashMap<>();
                riList.forEach(report -> {
                    if(!map.containsKey(report.dorKod)) {
                        map.put(report.dorKod, new ArrayList<>());
                        RKCUList.append(Railways.isRailway(report.dorKod).getDorkod()+"/");
                    }
                    map.get(report.dorKod).add(report);
                });

                List<ReportItem> result = new ArrayList<>();
                map.keySet().stream().sorted(Comparator.comparing(a -> Railway.valueOf(a).getValue())).forEach(dorKod -> {
                    ReportItem item = new ReportItem();
                    item.setPredName(Railway.valueOf(dorKod).getValue());
                    item.setPredLevel(map.get(dorKod).get(0).getPredLevel());
                    item.setRKCU(Railways.isRailway(dorKod));
                    item.setPredId(map.get(dorKod).get(0).getPredId());
                    item.setYear(map.get(dorKod).get(0).getYear());
                    item.values = ReportItem.accByMonth.apply(map.get(dorKod));
                    result.add(item);
                });

                ReportItem item = new ReportItem();
                item.setPredName("Всего");
                item.addClazz("itogo");
                item.setRkcuList(RKCUList.toString());
                item.setYear(riList.get(0).getYear());
                item.values = ReportItem.accByMonth.apply(riList);
                result.add(item);

                return result;
            }
            else {
                ReportItem item = new ReportItem();
                item.setPredName("Всего");
                item.addClazz("itogo");
                if(param.rcku){
                   if(param.level!=null) item.setPredLevel(param.level);
                   if(param.dorKod!=null) {
                       item.setRKCU(Railways.isRailway(blankList.get(0).getDorKod()));
                       item.setRkcuList("1");
                   }
                   if(param.predId!=null) item.setPredId(param.predId);
                   item.setYear(param.year);
                }
                item.values = ReportItem.accByMonth.apply(riList);
                riList.add(item);
                return riList;
            }
        }

        return riList;
   }

   private List<ReportItem> getItogo(ReportParam param){
       List<ReportItem> result = new ArrayList<>();


       List<Blank> blankList  = (List<Blank>) qf.from(blank)
               .where(param.getGroupPredicates())
               .fetch();

       List<ReportItem> mainItemList = blankList.stream()
               .map(blank-> blankMap.apply(blank, param.curDate))
               .collect(Collectors.toList());

       ReportItem mainGroupItem = new ReportItem();
       if(!blankList.isEmpty()){
           mainGroupItem.setPredId(blankList.get(0).getPredId());
           mainGroupItem.setYear(blankList.get(0).getYear());
           mainGroupItem.setPredLevel(blankList.get(0).getLevel());
       }else{
           mainGroupItem.setPredId(-1);
       }
        if (param.central != null && param.regional == null) {
            mainGroupItem.setPredName("Итого по " + CentralPred.findById(param.central).getName());
            mainGroupItem.setPredLevel(PredLevel.central);
        }
        if (param.central != null && param.regional != null) {
            mainGroupItem.setPredName("Итого по " + vs.getPred(param.regional).getName());
            mainGroupItem.setPredLevel(PredLevel.regional);
        }
       mainGroupItem.setClazz("itogo");
       mainGroupItem.values = ReportItem.accByMonth.apply(mainItemList);

       result.add(mainGroupItem);
       result.addAll(mainItemList);
       return result;
   }

   private List<ValuePair> calcNormValue(Integer year, BlankNorm norm, LocalDate date){
        Period period = norm.getPeriod();
        CompletionMark mark = norm.getCompletion();
        switch (period){
            case month:
                return IntStream.range(1, 13).boxed().map(i-> new ValuePair(mark.isMonthSet(i) ? 1:0, !mark.isMonthSet(i) && CompletionUtil.isLate(year, i, date) ? 1:0)).collect(Collectors.toList());
            case quarter:
                return IntStream.range(0, 12/3).map(it-> it*3 + 1).boxed().flatMap(i-> {
                    boolean flag = IntStream.range(i, i+3).boxed().anyMatch(month-> mark.isMonthSet(month));
                    return IntStream.range(i, i+3).boxed().map(month-> new ValuePair(mark.isMonthSet(month) ? 1: 0, !flag && month == i+2 && CompletionUtil.isLate(year, i+2, date) ? 1: 0));
                }).collect(Collectors.toList());
            case halfYear:
                return IntStream.range(0, 12/6).map(it->it*6 + 1).boxed().flatMap(i-> {
                    boolean flag = IntStream.range(i, i+6).boxed().anyMatch(month-> mark.isMonthSet(month));
                    return IntStream.range(i, i+6).boxed().map(month-> new ValuePair(mark.isMonthSet(month) ? 1: 0,  !flag && month == i+5 && CompletionUtil.isLate(year, i+5, date)? 1 : 0));
                }).collect(Collectors.toList());
            case year:
                boolean flag = IntStream.range(0, 12).boxed().anyMatch(month-> mark.isMonthSet(month));
                return IntStream.range(0, 12).boxed().map(month-> new ValuePair(mark.isMonthSet(month) ? 1: 0,  !flag && month == 12 && CompletionUtil.isLate(year, 12, date)? 1 : 0)).collect(Collectors.toList());
            case onDemand://в каком месяце выполнили, в таком и учитываем оба показателя
                return IntStream.range(1, 13).boxed().map(month->new ValuePair(mark.isMonthSet(month) ? 1:0, 0)).collect(Collectors.toList());
            case selected:
                CompletionMark sel = norm instanceof BlankNormDocument ? ((BlankNormDocument) norm).getPlan() : norm.getCustomPeriod();
                return IntStream.range(1, 13).boxed().map(month->new ValuePair(sel.isMonthSet(month) && mark.isMonthSet(month) ? 1 : 0, sel.isMonthSet(month) && !mark.isMonthSet(month) && CompletionUtil.isLate(year, month, date) ? 1: 0)).collect(Collectors.toList());
        }

        throw new IllegalStateException("Unknown period " + period);
   }

}
