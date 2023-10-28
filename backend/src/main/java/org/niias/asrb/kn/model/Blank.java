package org.niias.asrb.kn.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "kn", name = "blank")
public class Blank {

    @Id
    @SequenceGenerator(name="kn.blank_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    private Integer templateId;

    @Column(name = "post")
    private String postName;

    private Integer mainId;

    private Integer regId;

    private Integer predId;

    private Integer dorKod;

    private PredLevel level;

    private String predName;

    private Integer year;

    private CompletionMark open = new CompletionMark();

    private String docName;

    @Transient
    private boolean modified;

    @Transient
    private VerticalDto createdVertical;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name="id", column = @Column(name = "created_user_id")),
            @AttributeOverride(name="name", column = @Column(name = "created_user_name")),
            @AttributeOverride(name="date", column = @Column(name = "created_date", insertable = false))
    })
    private UserActionRef created;

    /*@ElementCollection
    @OneToMany(
            schema = "kn",
            name="blank_view",
            joinColumns=@JoinColumn(name="blank_id")
    )
    @OrderBy("viewDate")*/
    @OneToMany(cascade=CascadeType.ALL)
    private List<BlankViewMark> views;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="blank_id", referencedColumnName="id", nullable = false, updatable = false)
    @OrderBy("id")
    private List<BlankNorm> norms = new ArrayList<>();

    public Blank() {
    }

    public void addNorm(BlankNorm norm){
        norms.add(norm);
    }

    public List<BlankNorm> getNorms() {
        return norms;
    }

    public void setNorms(List<BlankNorm> norms) {
        this.norms = norms;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public Integer getMainId() {
        return mainId;
    }

    public void setMainId(Integer mainId) {
        this.mainId = mainId;
    }

    public Integer getPredId() {
        return predId;
    }

    public void setPredId(Integer predId) {
        this.predId = predId;
    }

    public String getPredName() {
        return predName;
    }

    public void setPredName(String predName) {
        this.predName = predName;
    }

    public UserActionRef getCreated() {
        return created;
    }

    public void setCreated(UserActionRef created) {
        this.created = created;
    }

    public List<BlankViewMark> getViews() {
        return views;
    }

    public void setViews(List<BlankViewMark> views) {
        this.views = views;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }



    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public PredLevel getLevel() {
        return level;
    }

    public void setLevel(PredLevel level) {
        this.level = level;
    }

    public Integer getRegId() {
        return regId;
    }

    public void setRegId(Integer regId) {
        this.regId = regId;
    }

    public Integer getDorKod() {
        return dorKod;
    }

    public void setDorKod(Integer dorKod) {
        this.dorKod = dorKod;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public CompletionMark getOpen() {
        return open;
    }

    public void setOpen(CompletionMark open) {
        this.open = open;
    }

    public VerticalDto getCreatedVertical() {
        return createdVertical;
    }

    public void setCreatedVertical(VerticalDto createdVertical) {
        this.createdVertical = createdVertical;
    }

}
