package org.niias.asrb.kn.export;

import lombok.Data;
import org.niias.asrb.kn.model.*;
import org.niias.asrb.kn.repository.UserRepository;
import org.niias.asrb.kn.service.UserService;
import org.niias.asrb.kn.util.CompletionUtil;
import org.niias.asrb.model.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class BlankModelService {

    @Data
    public static class ExportParams {
        List<Integer> quarters;
        String format;
        boolean splitTable = true;
    }

    @Inject
    private UserService us;

    @Inject
    private UserRepository userRepo;

    private final LocalDate curDate = null;
    //private final LocalDate curDate = LocalDate.of(2021, Month.AUGUST, 16);

    public BlankExportModel createBlankModel(Blank blank, ExportParams params){
        BlankExportModel model = new BlankExportModel();
        model.setYear(blank.getYear());
        //Не делим таблицы больше
        params.splitTable = false;
        UserKn user  = userRepo.findById(blank.getCreated().getId()).map(UserKnImpl::new).map(u -> (UserKn) u).orElse(UserKnImpl.EMPTY);
        if (user.getVertical() == null) {
            us.populateVertical(user.getUser());

            model.setLevel(user.getVertical().level);
            model.setUserPred(user.getVertical().predName);
            model.setUserStan(user.getVertical().stanName);
            model.setUserName(user.getUser().getFio());
            model.setReg(user.getVertical().regName);
            model.setMain(user.getVertical().mainName);
        } else {
            model.setUserName(blank.getCreated().getName());
        }
        model.setDocName(blank.getDocName());

        model.setTable(process(blank, params));
        model.setQuarters(params.quarters);

        return model;
    }

    private BlankTable process(Blank blank, ExportParams params){
        params.quarters.sort(Integer::compareTo);
        BlankTable blankTable = new BlankTable(2 + blank.getNorms().size(), 2 + params.quarters.size() * 3 + params.quarters.size() + 1);
        BlankRow topRow = blankTable.getRows().get(0);
        BlankRow bottomRow = blankTable.getRows().get(1);
        blankTable.setModelName(params.quarters.stream().map(qi->Quarters.values()[qi-1].getShortName()).collect(Collectors.joining(",")) + " квартал");

        topRow.getCols().get(0).setValue("№");
        topRow.getCols().get(0).setType(ColType.NUMBER);
        bottomRow.getCols().get(0).setVMerge(Merge.CONTINUE);
        topRow.getCols().get(1).setValue("Наименование работы");
        bottomRow.getCols().get(1).setVMerge(Merge.CONTINUE);

        params.quarters.stream().forEach(q->{
            int qIndex = params.quarters.indexOf(q);
            topRow.getCols().get(2 + qIndex * 3).setValue(Quarters.values()[q - 1].getName());
            topRow.getCols().get(2 + qIndex * 3 + 1).setHMerge(Merge.CONTINUE);
            topRow.getCols().get(2 + qIndex * 3 + 2).setHMerge(Merge.CONTINUE);

            topRow.getCols().get(2 + qIndex * 3).setMarks("quarter-name");
            topRow.getCols().get(2 + qIndex * 3 + 1).setMarks("quarter-name");
            topRow.getCols().get(2 + qIndex * 3 + 2).setMarks("quarter-name");
        });


        params.quarters.stream().forEach(q-> {
            int qIndex = params.quarters.indexOf(q);
            bottomRow.getCols().get(2 + qIndex * 3).setValue(Months.values()[(q-1) * 3].getName());
            bottomRow.getCols().get(2 + qIndex * 3 + 1).setValue(Months.values()[(q-1) * 3 + 1].getName());
            bottomRow.getCols().get(2 + qIndex * 3 + 2).setValue(Months.values()[(q-1) * 3 + 2].getName());

            bottomRow.getCols().get(2 + qIndex * 3).setMarks("month-name");
            bottomRow.getCols().get(2 + qIndex * 3 + 1).setMarks("month-name");
            bottomRow.getCols().get(2 + qIndex * 3 + 2).setMarks("month-name");
        });

        params.quarters.stream().forEach(q->{
            int qIndex = params.quarters.indexOf(q);
            int mapIdx = 2 + params.quarters.size() * 3 + qIndex;

            topRow.getCols().get(mapIdx).setValue("Итого за \n" + Quarters.values()[q - 1].getName());
            bottomRow.getCols().get(mapIdx).setVMerge(Merge.CONTINUE);

            topRow.getCols().get(mapIdx).setMarks("quarter-total");
            bottomRow.getCols().get(mapIdx).setMarks("quarter-total");
        });


        topRow.getCols().get(2 + params.quarters.size() * 3 + params.quarters.size()).setValue("Итого");
        bottomRow.getCols().get(2 + params.quarters.size() * 3 + params.quarters.size()).setVMerge(Merge.CONTINUE);
        topRow.getCols().get(2 + params.quarters.size() * 3 + params.quarters.size()).setMarks("year-total");
        bottomRow.getCols().get(2 + params.quarters.size() * 3 + params.quarters.size()).setMarks("year-total");

        Stream.concat(topRow.getCols().stream(), bottomRow.getCols().stream()).forEach(c->c.setType(ColType.HEADER));

        IntStream.range(0, blank.getNorms().size()).boxed().forEach(normIdx->{
            BlankNorm norm = blank.getNorms().get(normIdx);
            BlankRow row = blankTable.getRows().get(normIdx + 2);
            row.getCols().get(0).setValue(""+ (normIdx + 1));
            row.getCols().get(1).setValue(norm.getName());

            params.quarters.stream().forEach(q->{
                int qIndex = params.quarters.indexOf(q);

                IntStream.range((q-1) * 3, (q-1) * 3 + 3).map(i->i+1).forEach(monthNum->{
                    int mapIdx = 2 + qIndex * 3 + (monthNum - (q-1)*3) -1;
                    BlankCol col = row.getCols().get(mapIdx);

                    if (Arrays.asList(Period.quarter, Period.halfYear).contains(norm.getPeriod()))
                    {
                        int firstMonthOfPeriod = CompletionUtil.getFirstMonthOfPeriod(monthNum, norm.getPeriod());
                        int lastMonthOfPeriod = CompletionUtil.getLastMonthOfPeriod(monthNum, norm.getPeriod());
                        boolean periodCase = monthNum > firstMonthOfPeriod && monthNum <= lastMonthOfPeriod;

                        if (periodCase && monthNum == (q-1 )* 3 + 1)
                            periodCase = periodCase && qIndex != 0 && (q-1 == params.quarters.get(qIndex - 1));
                        if (periodCase)
                            col.setHMerge(Merge.CONTINUE);

                    }

                    col.setCompleted(isPeriodComplete(norm, monthNum));
                    col.setFailed(isPeriodFailed(blank.getYear(), norm, monthNum, curDate));
                    col.setType(ColType.MONTH);
                });
            });


            IntStream.range(0, params.quarters.size()).boxed().map(i-> params.quarters.get(i)).filter(it->it != null).forEach(q->{
                int qIndex = params.quarters.indexOf(q);
                int mapIdx = 2 + params.quarters.size() * 3 + qIndex;
                List<Integer> qScore = quarterScore(blank.getYear(), norm, q);

                BlankCol itogoCol = row.getCols().get(mapIdx);
                itogoCol.setValue(qScore.get(0) + "/" + qScore.get(1));
                itogoCol.setFailed(qScore.get(1) > qScore.get(0));
                itogoCol.setCompleted(qScore.get(0) > qScore.get(1));
                itogoCol.setType(ColType.QUARTER_SCORE);
            });

            List<Integer> yearScore = IntStream.range(0, params.quarters.size()).boxed().map(i-> params.quarters.get(i)).filter(it->it != null).map(q->quarterScore(blank.getYear(), norm, q))
                    .reduce((a,b)-> Arrays.asList(a.get(0) + b.get(0), a.get(1) + b.get(1))).get();

            BlankCol yearScoreCol = row.getCols().get(2 + params.quarters.size() * 3 + params.quarters.size());
            yearScoreCol.setValue(yearScore.get(0) + "/" + yearScore.get(1));
            yearScoreCol.setFailed(yearScore.get(1) > yearScore.get(0));
            yearScoreCol.setCompleted(yearScore.get(0) > yearScore.get(1));
            yearScoreCol.setType(ColType.YEAR_SCORE);

        });

        return blankTable;
    }


    private boolean isPeriodComplete( BlankNorm norm, int month){
        switch (norm.getPeriod()){
            case month:
            case onDemand:
            case selected:
                return norm.getCompletion().isMonthSet(month);
            default:
                int firstMonthOfPeriod = CompletionUtil.getFirstMonthOfPeriod(month, norm.getPeriod());
                return IntStream.range(firstMonthOfPeriod, firstMonthOfPeriod + CompletionUtil.getPeriodLength(norm.getPeriod())).boxed().anyMatch(m-> norm.getCompletion().isMonthSet(m));
        }
    }

    private boolean isPeriodFailed(int year, BlankNorm norm, int month, LocalDate curDate){
        boolean complete = isPeriodComplete(norm, month);
        int lastMonthOfPeriod = CompletionUtil.getLastMonthOfPeriod(month, norm.getPeriod());

        switch (norm.getPeriod()){
            case selected:
                return !complete && (norm instanceof BlankNormDocument ? ((BlankNormDocument) norm).getPlan() : norm.getCustomPeriod()).isMonthSet(month) && CompletionUtil.isLate(year, lastMonthOfPeriod, curDate);
            case onDemand:
                return false;
            default:
                return !complete && CompletionUtil.isLate(year, lastMonthOfPeriod, curDate);
        }
    }

    private List<Integer> quarterScore(int year, BlankNorm norm, int q){
        switch (norm.getPeriod())
        {
            case month:
                return IntStream.range((q-1) * 3, q*3).map(i->i+1).boxed()
                        .map(month->Arrays.asList(norm.getCompletion().isMonthSet(month)?1:0, !norm.getCompletion().isMonthSet(month) && CompletionUtil.isLate(year, month, curDate) ? 1:0))
                        .reduce((a,b)-> Arrays.asList(a.get(0) + b.get(0), a.get(1) + b.get(1))).get();
            case onDemand:
                return IntStream.range((q-1) * 3, q*3).map(i->i+1).boxed()
                        .map(month->Arrays.asList(norm.getCompletion().isMonthSet(month)?1:0, 0))
                        .reduce((a,b)-> Arrays.asList(a.get(0) + b.get(0), a.get(1) + b.get(1))).get();
            case selected:
                return IntStream.range((q-1) * 3, q*3).map(i->i+1).boxed()
                        .map(month->Arrays.asList(norm.getCompletion().isMonthSet(month)?1:0, !norm.getCompletion().isMonthSet(month) && (norm instanceof BlankNormDocument ? ((BlankNormDocument) norm).getPlan() : norm.getCustomPeriod()).isMonthSet(month) && CompletionUtil.isLate(year, month, curDate) ? 1:0 ))
                        .reduce((a,b)-> Arrays.asList(a.get(0) + b.get(0), a.get(1) + b.get(1))).get();
            default:
                int periodLength = CompletionUtil.getPeriodLength(norm.getPeriod());
                int firstMonthOfPeriod = CompletionUtil.getFirstMonthOfPeriod((q-1) * 3 + 1, norm.getPeriod());
                int lastMonthOfPeriod = CompletionUtil.getLastMonthOfPeriod((q-1) * 3 + 1, norm.getPeriod());
                boolean isPeriodComplete = IntStream.range(firstMonthOfPeriod, firstMonthOfPeriod + periodLength).boxed().anyMatch(m->norm.getCompletion().isMonthSet(m));

                return IntStream.range((q-1) * 3, q*3).map(i->i+1).boxed()
                        .map(month-> Arrays.asList(
                                norm.getCompletion().isMonthSet(month) ? 1 : 0,
                                !isPeriodComplete && month == lastMonthOfPeriod && CompletionUtil.isLate(year, month, curDate) ? 1:0
                        )).reduce((a,b)-> Arrays.asList(a.get(0) + b.get(0), a.get(1) + b.get(1))).get();
        }

    }

    private List<Integer> yearScore(int year, BlankNorm norm){
        switch (norm.getPeriod())
        {
            case month:
                return IntStream.range(1, 13).boxed()
                        .map(month->Arrays.asList(norm.getCompletion().isMonthSet(month)?1:0, !norm.getCompletion().isMonthSet(month) && CompletionUtil.isLate(year, month, curDate) ? 1:0))
                        .reduce((a,b)-> Arrays.asList(a.get(0) + b.get(0), a.get(1) + b.get(1))).get();
            case onDemand:
                return IntStream.range(1, 13).boxed()
                        .map(month->Arrays.asList(norm.getCompletion().isMonthSet(month)?1:0, 0))
                        .reduce((a,b)-> Arrays.asList(a.get(0) + b.get(0), a.get(1) + b.get(1))).get();
            case selected:
                return IntStream.range(1, 13).boxed()
                        .map(month->Arrays.asList(norm.getCompletion().isMonthSet(month)?1:0, !norm.getCompletion().isMonthSet(month) && ((BlankNormDocument) norm).getPlan().isMonthSet(month) && CompletionUtil.isLate(year, month, curDate) ? 1:0 ))
                        .reduce((a,b)-> Arrays.asList(a.get(0) + b.get(0), a.get(1) + b.get(1))).get();
            default:
                int periodLength = CompletionUtil.getPeriodLength(norm.getPeriod());
                return IntStream.range(0, 12 / periodLength).boxed().map(i->{
                    int firstMonthOfPeriod = i * periodLength + 1;
                    int lastMonthOfPeriod =  i * periodLength + periodLength;
                    boolean isPeriodComplete = IntStream.range(firstMonthOfPeriod, lastMonthOfPeriod + 1).boxed().anyMatch(m->norm.getCompletion().isMonthSet(m));
                    return Arrays.asList(isPeriodComplete ? 1: 0, !isPeriodComplete && CompletionUtil.isLate(year, lastMonthOfPeriod, curDate) ? 1: 0);
                }).reduce((a,b)-> Arrays.asList(a.get(0) + b.get(0), a.get(1) + b.get(1))).get();
        }

    }

}
