package org.niias.asrb.kn.controller;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.niias.asrb.kn.model.*;
import org.niias.asrb.kn.repository.TemplateRepository;
import org.niias.asrb.kn.service.DNCHSyncService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static org.niias.asrb.kn.model.QTemplate.template;

@RestController
@RequestMapping
public class TemplateController {

    @Resource
    private TemplateRepository teplateRepo;


    @Inject
    DNCHSyncService dnchSyncService;

    @Inject
    private UserKn user;



    @Inject
    private JPAQueryFactory qf;

    @GetMapping("api/templates")
    public Iterable<Template> getTemplates() {
        VerticalDto vert = user.getVertical();
        final JPAQuery<?> query = qf.from(template);

        CentralPred cp = CentralPred.findById(vert.mainId);

        if (cp.isRailway())
            query.where(template.mainId.in(CentralPred.getRailwayCentralPreds().stream().map(CentralPred::getId).collect(Collectors.toList())));
        else if (cp  != CentralPred.CRB)
            query.where(template.mainId.eq(vert.mainId));

//todo сделать пагинацию
//                .where(template.dnchId.eq(47080704348896l))
        return (List<Template>) query
                        .orderBy(new OrderSpecifier<>(Order.ASC, template.postName))
                        .fetch();
    }

    public static class TemplateTo{

        public TemplateTo(){}

        public TemplateTo(Template tpl){
            this.id = tpl.getId();
            this.post = tpl.getPostName();
        }

        public Integer id;
        public String post;
    }

    @RequestMapping(value = "/api/template/find-user-templates", method = RequestMethod.POST)
    public List<TemplateTo> findUserTemplates(@RequestParam Integer year){

        BooleanBuilder bb = new BooleanBuilder();
        bb.and(template.level.eq(user.getVertical().level));
        bb.and(template.fromDate.year().loe(year));
        bb.and(template.toDate.year().goe(year));
        bb.and(template.toDate.after(LocalDate.now()));
        bb.and(template.status.eq(Status.approved));

        if (CentralPred.findById(user.getVertical().mainId).isRailway())
            bb.and(template.mainId.in(CentralPred.getRailwayCentralPreds().stream().map(CentralPred::getId).collect(Collectors.toList())));
        else
            bb.and(template.mainId.eq(user.getVertical().mainId));

        List<Template> tplList = (List<Template>) qf.from(template)
                .where(bb.getValue()).fetch();

        return tplList.stream().map(TemplateTo::new).collect(Collectors.toList());
    }

    @PostMapping("api/templates")
    public Template saveTemplate(@RequestParam Map formData) {
        Template createdTempl = new Template();
        createdTempl.setStatus(Status.draft);
        createdTempl.setLevel(PredLevel.valueOf((String) formData.get("level")));
        createdTempl.setMainId(user.getVertical().mainId);
        createdTempl.setPostName((String) formData.get("post"));
        createdTempl.setPostFullName((String) formData.get("post_full"));


        UserActionRef uar = new UserActionRef(user.getUser());
        uar.setDate(LocalDate.now());

        createdTempl.setCreated(uar);

   teplateRepo.save(createdTempl);

        return createdTempl;
    }



    @PostMapping(value = "api/templates/copy-existing/{templId}")
    public Iterable<Template> copyFromApproved(@PathVariable int templId) {

        Template createdTempl = new Template();
        Optional<Template> templateToChangeNorms = teplateRepo.findById(templId);

        templateToChangeNorms.ifPresent(value -> {

            createdTempl.setStatus(Status.draft);
            createdTempl.setLevel(value.getLevel());
            createdTempl.setMainId(value.getMainId());
            createdTempl.setPostName(value.getPostName());
            createdTempl.setPostFullName(value.getPostFullName());
            UserActionRef uar = new UserActionRef(user.getUser());
            uar.setDate(LocalDate.now());
            createdTempl.setCreated(uar);

            List normsArray = new ArrayList();

            List<TemplateNorm> normsInExisting = value.getNorms();
            normsInExisting.stream().forEach(i -> {
                TemplateNorm newNorm = new TemplateNorm();
                newNorm.setName(i.getName());
                newNorm.setPeriod(i.getPeriod());
                normsArray.add(newNorm);

            });

            createdTempl.setNorms(normsArray);

            teplateRepo.save(createdTempl);

        });



        return getTemplates();
    }

    @PostMapping(value = "api/templates/norms/{templId}")
    public Iterable<Template> saveNorms(@PathVariable int templId, @RequestBody List<TemplateNorm> normatives) {

        Optional<Template> templateToChangeNorms = teplateRepo.findById(templId);

        templateToChangeNorms.ifPresent(value -> {

                value.setNorms(normatives);

        });



        teplateRepo.save(templateToChangeNorms.get());

        return getTemplates();

    }

    @PostMapping(value = {"api/templates/change/status/{templateId}/{newStatus}",
        "api/templates/change/status/{templateId}/{newStatus}/{validityDateFrom}",
        "api/templates/change/status/{templateId}/{newStatus}/{validityDateFrom}/{validityDateTo}"})
    private Iterable<Template> changeTemplateStatus(@PathVariable String templateId,
                                                    @PathVariable  String newStatus,
                                                    @PathVariable Optional<Date> validityDateFrom,
                                                    @PathVariable Optional<Date> validityDateTo) {


        Optional<Template> templateToChangeNorms = teplateRepo.findById(Integer.parseInt(templateId) );
        Status st = Status.valueOf(newStatus);

        UserActionRef uar = new UserActionRef(user);
        uar.setDate(LocalDate.now());


        templateToChangeNorms.ifPresent(value -> {
            final Status oldSt = value.getStatus();
            value.setStatus(st);

            if (Status.approved.equals(st)) {
                if (oldSt != Status.approved)
                    value.setApproved(uar);

                BooleanBuilder bb = new BooleanBuilder();
                bb.and(template.status.eq(Status.approved));
                bb.and(template.level.eq(value.getLevel()));
                bb.and(template.id.ne(value.getId()));
                bb.and(template.toDate.goe(validityDateFrom.get().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
                bb.and(template.postName.equalsIgnoreCase(value.getPostName()));
                if (CentralPred.getRailwayCentralPreds().contains(CentralPred.findById(value.getMainId())))
                    bb.and(template.mainId.in(CentralPred.getRailwayCentralPreds().stream().map(it->it.getId()).collect(Collectors.toList())));
                else
                    bb.and(template.mainId.eq(value.getMainId()));

                Template OverlapTimeTempl = (Template) qf.from(template)
                        .where(bb.getValue())
                        .fetchFirst();

                if (OverlapTimeTempl != null) {
                    OverlapTimeTempl.setStatus(Status.outdated);

                    if (OverlapTimeTempl.getDnchId() != null) {
                        dnchSyncService.update(OverlapTimeTempl);
                    }
                    teplateRepo.save(OverlapTimeTempl);
                }

            } else if (Status.approval.equals(st) && Status.agreement.equals(oldSt)) {
                value.setAgreed(uar);
            }
            if (st.ordinal() > oldSt.ordinal())
                value.setComment(null);

            if (oldSt != Status.approved) {
                validityDateFrom.ifPresent(vdf -> {
                    LocalDate ldf = vdf.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    value.setFromDate(ldf);
                });
            } else {
                uar.setName(user.getUser().getFio() + " (" + user.getUser().getSubdivision() + ")");
                value.setChanged(uar);
            }
            validityDateTo.ifPresent(vdt -> {
                LocalDate ldt = vdt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                value.setToDate(ldt);

            });
            teplateRepo.save(value);
            if (value.getDnchId() != null) {
                dnchSyncService.update(value);
            }
        });

        return getTemplates();

    }

    @PostMapping("api/templates/change/statuswithcomment/{templateId}/{newStatus}/{comment}")
    private Iterable<Template> changeTemplateStatus(@PathVariable String templateId,
                                                    @PathVariable  String newStatus,
                                                    @PathVariable String comment){

        Optional<Template> templateToChangeNorms = teplateRepo.findById(Integer.parseInt(templateId) );
        Status st = Status.valueOf(newStatus);


        templateToChangeNorms.ifPresent(value -> {
            value.setStatus(st);
            value.setComment(comment);
            teplateRepo.save(templateToChangeNorms.get());
            if (value.getDnchId() != null) {
                dnchSyncService.update(value);
            }
        });

        return getTemplates();

    }

    @PostMapping("api/templates/change/post-full/{templateId}/{newPredLevel}/{newPostFullName}")
    private Iterable<Template> changeTemplatePostFullName(@PathVariable String templateId,  @PathVariable PredLevel newPredLevel,  @PathVariable  String newPostFullName) {

        Optional<Template> templateToChangeNorms = teplateRepo.findById(Integer.parseInt(templateId) );

        templateToChangeNorms.ifPresent(value -> {
            value.setPostFullName(newPostFullName);
            value.setLevel(newPredLevel);
        });


        teplateRepo.save(templateToChangeNorms.get());

        return getTemplates();

    }

    public static class TemplateNotes{
        LocalDate fromDate;
        LocalDate toDate;
        String post;
        String central;
        Status status;

        public LocalDate getFromDate() {
            return fromDate;
        }

        public LocalDate getToDate() {
            return toDate;
        }

        public String getPost() {
            return post;
        }

        public String getCentral() {
            return central;
        }

        public Status getStatus() {
            return status;
        }
    }

    @GetMapping("api/template/{templateId}")
    private TemplateNotes getTemplate(@PathVariable String templateId) {
        return teplateRepo.findById(Integer.parseInt(templateId)).map(tpl->{
            TemplateNotes notes = new TemplateNotes();
            notes.fromDate = tpl.getFromDate();
            notes.toDate = tpl.getToDate();
            notes.post = tpl.getPostName();
            notes.status = tpl.getStatus();
            notes.central = CentralPred.findById(tpl.getMainId()).getName();
            return notes;
        }).orElse(null);
    }

    @PostMapping("api/templates/delete/{templateId}")
    private Iterable<Template> removeTemplate(@PathVariable String templateId) {
        Optional<Template> templateToChangeNorms = teplateRepo.findById(Integer.parseInt(templateId) );

        Template templ = teplateRepo.save(templateToChangeNorms.get());
         teplateRepo.delete(templ);

        return getTemplates();
    }

}
