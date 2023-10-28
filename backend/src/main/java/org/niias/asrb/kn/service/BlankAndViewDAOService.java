package org.niias.asrb.kn.service;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.niias.asrb.kn.model.BlankAndBlankViewDTO;
import org.niias.asrb.kn.model.BlankDTO;
import org.niias.asrb.kn.model.BlankViewDTO;
import org.niias.asrb.kn.model.QBlankViewMark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.niias.asrb.kn.model.QBlankViewMark.blankViewMark;
import static org.niias.asrb.kn.model.QBlank.blank;
@Service
public class BlankAndViewDAOService {
    @Inject
    JPAQueryFactory qf;

    public List<BlankAndBlankViewDTO> getBlankRes() {
        StringTemplate arr1 = Expressions.stringTemplate("cast(array_agg({0}) as text)", blank.id);
        List<Tuple> res1 = qf.from(blank)
                .select(blank.created.date.year(), blank.created.date.month(), blank.created.id, blank.id.countDistinct(), arr1)
                .groupBy(blank.created.date.year(), blank.created.date.month(), blank.created.id)
                .orderBy(blank.created.date.year().asc(), blank.created.date.month().asc()).fetch();

        StringTemplate arr2 = Expressions.stringTemplate("cast(array_agg({0}) as text)", blankViewMark.id);
        List<Tuple> res2=qf.from(blankViewMark)
                .select(blankViewMark.viewDate.year(), blankViewMark.viewDate.month(), blankViewMark.userId, blankViewMark.id.countDistinct(), arr2)
                .groupBy(blankViewMark.viewDate.year(), blankViewMark.viewDate.month(), blankViewMark.userId)
                .orderBy(blankViewMark.viewDate.year().asc(), blankViewMark.viewDate.month().asc()).fetch();
        int value;
        if (res1.size() > res2.size())
            {
                value=res1.size();
            }
        else
            { value = res2.size();}
        List<BlankAndBlankViewDTO> bvDTOList = new ArrayList<>(value);
        String rawBlankView=null;
        String rawBlank;
        List<String> lnull= new ArrayList<>(0);
        Long cnull=null;



        //System.out.println(bvDTOList);
        return bvDTOList;
        /* List<BlankDTO> blankDTOList = new ArrayList<>(res1.size());
        for(Tuple t:res1){
            blankDTOList.add(new BlankDTO(t.get(0, Integer.class),t.get(1, Integer.class),
                    t.get(2, Integer.class), t.get(3, Long.class), (Collections.singletonList(t.get(4, String.class)))));
        }
        List<BlankViewDTO> blankViewDTOList = new ArrayList<>(res2.size());
        for(Tuple t:res2){
            blankViewDTOList.add(new BlankViewDTO(t.get(0, Integer.class), t.get(1, Integer.class),
                    t.get(2, Integer.class), t.get(3, Long.class), Collections.singletonList(t.get(4, String.class))));
        }

        return blankDTOList;
    }

         */
   /* public void getViewRes(){
        StringTemplate arr2 = Expressions.stringTemplate("cast(array_agg({0}) as text)", blankViewMark.id);
        List<Tuple> res2=qf.from(blankViewMark)
                .select(blankViewMark.viewDate.year(), blankViewMark.viewDate.month(), blankViewMark.userId, blankViewMark.id.countDistinct(), arr2)
                .groupBy(blankViewMark.viewDate.year(), blankViewMark.viewDate.month(), blankViewMark.userId).fetch();
        List<BlankViewDTO> bvDTOList = new ArrayList<>(res2.size());
        for(Tuple t:res2){
            bvDTOList.add(new BlankViewDTO(t.get(0, Integer.class), t.get(1, Integer.class),
                    t.get(2, Integer.class), t.get(3, Long.class), Collections.singletonList(t.get(4, String.class))));
        }

    */

    }





}
