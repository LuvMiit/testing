package org.niias.asrb.kn.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.niias.asrb.kn.AppContext;
import org.niias.asrb.kn.model.*;
import org.niias.asrb.kn.repository.BlankRepository;
import org.niias.asrb.kn.repository.TemplateRepository;
import org.niias.asrb.kn.service.UserService;
import org.niias.asrb.model.SubsystemRole;
import org.niias.asrb.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.niias.asrb.kn.model.QBlank.blank;
import static org.niias.asrb.kn.model.QBlankViewMark.blankViewMark;

@RestController
public class BlankController {

    @Inject
    private BlankRepository blankRepo;

    @Inject
    private TemplateRepository tplRepo;

    @Inject
    private UserKn user;

    @Inject
    private UserService userService;

    @Inject
    private JPAQueryFactory qf;

    public static class Query {
        public Integer year;
        public String post;
        public String postSearch;
        public String pred;
        public String predSearch;
        @JsonFormat(pattern = "dd.MM.yyyy")
        public LocalDate created;
        @JsonFormat(pattern = "dd.MM.yyyy")
        public LocalDate view;
        public PredLevel level;
        public Railways RKCU;
        public boolean isOtch;
        public Integer predId;
        public String RKCUList;
        Predicate toPredicate(){
            BooleanBuilder bb = new BooleanBuilder();
            bb.and(blank.id.isNotNull());
            final UserKn user = new UserKnImpl(AppContext.getBean(User.class));
            VerticalDto vert = user.getVertical();

            //Если РБ
            if (vert.mainId == CentralPred.CRB.getId() && vert.regId != null  || user.hasRbRegRole())
            {
              // if(year==null){
                List<PredLevel> levels = new ArrayList<>(Arrays.asList(PredLevel.regional, PredLevel.linear));
                if (user.getVertical().level == PredLevel.central)
                    levels.add(PredLevel.central);
                    if(level==null && RKCU==null && !isOtch    ) {
                bb.and(blank.level.in(levels));
                    bb.and(blank.dorKod.eq(vert.dorKod));
                }
            }
            PredLevel queryLevel=level;
            //Если не ЦРБ и не РБ
            if (vert.mainId != CentralPred.CRB.getId() && !user.hasRbRegRole())
            {
                final PredLevel level = user.getVertical().level;
                if (level == PredLevel.regional) {
                    bb.and(blank.level.ne(PredLevel.central));
                    bb.and(blank.dorKod.eq(user.getUser().getRailroad().getDorKod()));
                } else if (level == PredLevel.linear) {
                    bb.and(blank.level.eq(PredLevel.linear));
                    bb.and(blank.predId.eq(user.getUser().getSubdivisionId()));
                }
                if(queryLevel==null && RKCU==null && !isOtch    ) {
                    bb.and(blank.mainId.eq(user.getVertical().mainId));
                }
            }
            if (level != null && !level.equals(PredLevel.all))
                bb.and(blank.level.eq(level));

            if(RKCU!=null && RKCU.getDorkod()!=0){
                bb.and(blank.dorKod.eq(RKCU.getDorkod()));
            }
            if (postSearch != null && !postSearch.isEmpty())
                bb.and(blank.postName.contains(postSearch));

            if (post != null && !post.isEmpty())
                bb.and(blank.postName.eq(post));

            if (predSearch != null && !predSearch.isEmpty())
                bb.and(blank.predName.contains(predSearch));

            if (pred != null && !pred.isEmpty())
                bb.and(blank.predName.eq(pred));

            if (year != null)
                bb.and(blank.year.eq(year));

            if (created != null)
                bb.and(blank.created.date.eq(created));

            if(predId!=null){
                bb.and(blank.predId.eq(predId));
            }
            if (view != null)
                bb.and(blankViewMark.viewDate.eq(view));
            if(RKCUList!=null){
                List<Integer> RKCUid=new ArrayList<>();
                List<String> RKCU=Arrays.asList(RKCUList.split("/"));
                for(String dorkod: RKCU){
                        RKCUid.add(Integer.parseInt(dorkod));
                }
                bb.and(blank.dorKod.in(RKCUid));
            }
            return bb.getValue();
        }

    }

    @RequestMapping(value = "/api/blank/post/query", method = RequestMethod.POST)
    public List<String> queryPost(@RequestBody Query query)
    {
        return qf.from(blank)
                .select(blank.postName)
                .leftJoin(blank.views, blankViewMark)
                .where(query.toPredicate())
                .distinct()
                .fetch();
    }

    @RequestMapping(value = "/api/blank/pred/query", method = RequestMethod.POST)
    public List<String> queryPred(@RequestBody Query query)
    {
        return qf.from(blank)
                .select(blank.predName)
                .leftJoin(blank.views, blankViewMark)
                .where(query.toPredicate())
                .distinct()
                .fetch();
    }

    @RequestMapping(value = "/api/blank/change-doc-name", method = RequestMethod.POST)
    public ResponseEntity changeDocName(@RequestParam Integer blankId, @RequestParam String docName)
    {
        return blankRepo.findById(blankId).map(blank->{
            blank.setDocName(docName);
            blankRepo.save(blank);
            return new ResponseEntity(HttpStatus.OK);
        }).orElse(new ResponseEntity(HttpStatus.NOT_FOUND));
    }


    @RequestMapping(value = "/api/blank/query", method = RequestMethod.POST)
    public Page<BlankTo> query(@RequestBody Query query, Pageable page)
    {
        Page<Blank> blankPage = blankRepo.findAll(query.toPredicate(), page);
        return new PageImpl<BlankTo>(blankPage.getContent().stream().map(blank -> new BlankTo(blank.getId(), blank.getYear(), blank.getPostName(), blank.getPredName(), blank.getCreated().getName(), blank.getCreated().getDate(),
                blank.getViews().isEmpty() ? null : blank.getViews().get(blank.getViews().size() - 1).viewInfo(), blank.getNorms().stream().map(n -> n instanceof BlankNormDocument ? new BlankNormTo((BlankNormDocument) n) : new BlankNormTo(n)).collect(Collectors.toList())
        )).collect(Collectors.toList()), page, blankPage.getTotalElements());
    }

    private boolean isModified(Blank blank){
        return !blank.getViews().isEmpty() || blank.getNorms().stream().anyMatch(norm-> IntStream.range(1, 13).boxed().anyMatch(m->norm.getCompletion().isMonthSet(m)));
    }

    @RequestMapping("/api/blank")
    public List<BlankTo> getBlanks(){
        return StreamSupport.stream(blankRepo.findAll().spliterator(), false).map(blank -> new BlankTo(blank.getId(), blank.getYear(), blank.getPostName(), blank.getPredName(), blank.getCreated().getName(), blank.getCreated().getDate(),
                blank.getViews().isEmpty() ? null :blank.getViews().get(0).viewInfo(), blank.getNorms().stream().map(n -> n instanceof BlankNormDocument ? new BlankNormTo((BlankNormDocument) n) : new BlankNormTo(n)).collect(Collectors.toList())
                )).collect(Collectors.toList());
    }

    @RequestMapping(value = "/api/blank", method = RequestMethod.POST)
    public ResponseEntity createBlank(@RequestParam Integer year, @RequestParam Integer templateId, @RequestParam String docName){
        Template tpl = tplRepo.findById(templateId).get();
        Blank blank = new Blank();
        blank.setTemplateId(tpl.getId());
        blank.setCreated(new UserActionRef(user));
        blank.setPredId(user.getUser().getSubdivisionId());
        blank.setPredName(user.getUser().getSubdivision());
        blank.setLevel(user.getVertical().level);
        blank.setMainId(user.getVertical().mainId);
        blank.setRegId(user.getVertical().regId);
        blank.setDorKod(user.getVertical().dorKod);
        blank.setPostName(tpl.getPostName());
        blank.setYear(year);
        blank.setDocName(docName);
        tpl.getNorms().stream().map(BlankNorm::new).forEach(blank::addNorm);
        blankRepo.save(blank);
        return new ResponseEntity(HttpStatus.OK);
    }

    public static class ViewMarkParam{
        public Integer blankId;
        public String comment;
    }

    @RequestMapping(value = "/api/blank/view-mark", method = RequestMethod.POST)
    public void addViewMark(@RequestBody ViewMarkParam param){
        Blank blank = this.blankRepo.findById(param.blankId).get();
        BlankViewMark viewMark = new BlankViewMark();
        viewMark.setUserId(user.getUser().getId());
        viewMark.setUserPred(user.getUser().getSubdivision());
        viewMark.setUserName(user.getUser().getFio());
        viewMark.setComment(param.comment);
        viewMark.setViewDate(LocalDate.now());
        blank.getViews().add(viewMark);
        this.blankRepo.save(blank);
    }

    @Inject
    private UserService us;


    public Blank getBlank(@PathVariable Integer id){
        Blank blank = blankRepo.findById(id).get();
        blank.setCreatedVertical(us.getVertical(blank.getPredId()));
        blank.setModified(isModified(blank));
        return blank;
    }

    @RequestMapping(value = "/api/blank/{id}", method = RequestMethod.DELETE)
    public ResponseEntity removeBlank(@PathVariable Integer id)
    {
        Blank blank = blankRepo.findById(id).get();
        final Template template = tplRepo.findById(blank.getTemplateId()).orElse(null);
        if (template != null && Status.approved.equals(template.getStatus()) && (isModified(blank) || !blank.getCreated().getId().equals(user.getUser().getId())))
            throw new IllegalStateException("Modified blank can't be deleted");
        blankRepo.delete(blank);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/api/blank/{blankId}/open-month", method = RequestMethod.POST)
    public ResponseEntity toggleMonth(@PathVariable Integer blankId, @RequestParam Integer month)
    {
        Blank blank = blankRepo.findById(blankId).get();
        //РБ + права проверка нлу
        if (user.getVertical().mainId == CentralPred.CRB.getId() && (user.getVertical().regId == null || user.getVertical().regId != null && Objects.equals(user.getVertical().dorKod, blank.getDorKod())) && user.getUser().getAllowedSystems().stream().anyMatch(it-> it.getRole() == SubsystemRole.KN_NLU_CONTROL_USER))
        {
            blank.getOpen().toggleMonth(month);
            blankRepo.save(blank);
            return ResponseEntity.ok().build();
        }

        throw new UnsupportedOperationException();
    }

}
