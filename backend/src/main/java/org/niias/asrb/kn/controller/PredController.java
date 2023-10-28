package org.niias.asrb.kn.controller;

import org.niias.asrb.kn.model.*;
import org.niias.asrb.model.User;
import org.niias.asrb.kn.service.VerticalService;
import org.niias.asrb.kn.service.VerticalService.VertInfo;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class PredController {

    @Resource
    private VerticalService vs;

    @Inject
    private UserKn user;

    @RequestMapping("/api/pred/central")
    List<PredValue> getCentral(){
        return Arrays.stream(CentralPred.values())
                .map(it->new PredValue(it.getId(), it.getName()))
                .filter(it->it.predId != CentralPred.CRB.getId())
                .sorted()
                .collect(Collectors.toList());
    }

    @RequestMapping("/api/pred/children/{predId}")
    List<PredValue> getChildrens(@PathVariable  int predId){

        VertInfo info = vs.getVertInfo(predId);
        List<HPred> srcPreds = vs.getChildrens(predId);
        //Для регилнального уровня ЦДИ убираем центральные дирекции расположенные в региона, напр. ЦП ЦДИ ОАО "РЖД"
        if (predId == CentralPred.CDI.getId())
            srcPreds = srcPreds.stream().filter(it->CentralPred.CDI.getId() != predId || !it.getGrId().equals(7))
                    .collect(Collectors.toList());
        //Для линейнго уровня ЦДИ раскрываем дирекции инфраструктуры, напр. П ОКТ ДИ
        if (info.getMain().getId() == CentralPred.CDI.getId() && predId != CentralPred.CDI.getId())
            srcPreds = Stream.concat(vs.getChildrens(srcPreds.stream().filter(it->it.getGrId() == 10).mapToInt(it->it.getId()).boxed().distinct().collect(Collectors.toList())).stream(),
                    srcPreds.stream().filter(it->it.getGrId() != 10)

                    ).collect(Collectors.toList());

        return srcPreds
                .stream()
                .map(it->new PredValue(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    @RequestMapping("/api/pred/regional/{mainId}")
    List<PredValue> getRegional(@PathVariable  int mainId){
        if (!CentralPred.isCentralPred(mainId))
            throw  new IllegalStateException();

        CentralPred centralPred = CentralPred.findById(mainId);

        if (centralPred.isRailway())
            return Collections.emptyList();

        if (centralPred != CentralPred.CDI)
            return vs.getChildrens(mainId).stream()
                .map(it->new PredValue(it.getId(), it.getName()))
                .collect(Collectors.toList());


        return Stream.concat(vs.getChildrens(mainId).stream()
                .filter(it->it.getGrId() != 7), vs.getChildrens(SpecialPred.CDIM.getId()).stream())
                .map(it->new PredValue(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    @RequestMapping("/api/pred/linear/{regId}")
    List<PredValue> getLinear(@PathVariable  int regId){

        VertInfo vi = vs.getVertInfo(regId);
        CentralPred centralPred = CentralPred.findById(vi.getMain().getId());

        if (centralPred.isRailway())
            return Collections.emptyList();

        List<PredValue> result = null;
        if (centralPred == CentralPred.CDI)
        {
            List<HPred> preds = vs.getChildrens(regId);
            result =  Stream.concat(preds.stream().filter(it->it.getGrId() == 10).flatMap(it->vs.getChildrens(it.getId()).stream()),
                    preds.stream().filter(it->it.getGrId() != 10))
                    .map(it->new PredValue(it.getId(), it.getName()))
                    .collect(Collectors.toList());
        }else{
            result =  vs.getChildrens(regId).stream()
                    .map(it->new PredValue(it.getId(), it.getName()))
                    .collect(Collectors.toList());
        }

        //Для РБ определяем дорогу и фильтруем по ней
        if (user.getVertical().mainId == CentralPred.CRB.getId() && user.getVertical().regId != null)
        {
            Map<Integer, Integer> dorKodMap = vs.getDorKodMap(result.stream().map(it->it.predId).collect(Collectors.toList()));
            result = result.stream().filter(it->dorKodMap.get(it.predId).equals(user.getVertical().dorKod)).collect(Collectors.toList());
        }

        return result;
    }

    @RequestMapping("/api/pred/rcku")
    List<PredValue> getPredOnRckuAndLevel(@RequestParam Integer year, @RequestParam Integer railway, @RequestParam(required = false, defaultValue = "all") PredLevel level) {
        return vs.getPredOnRckuAndLevel(year, Railway.valueOf(railway), level);
    }

    public static class PredValue implements Comparable<PredValue>{

        public PredValue(int predId, String name){
            this.predId = predId;
            this.name = name;
        }

        public int predId;
        public String name;

        @Override
        public int compareTo(PredValue o) {
            return name.compareTo(o.name);
        }
    }

}
