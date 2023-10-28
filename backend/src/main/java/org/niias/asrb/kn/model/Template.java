package org.niias.asrb.kn.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Table(schema = "kn", name = "template")
public class Template {

    @Id
    @SequenceGenerator(name="kn.template_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private Status status = Status.draft;
    private Integer mainId;
    private PredLevel level;
    @Column(name = "post")
    private String postName;
    @Column(name = "post_full")
    private String postFullName;
    private Integer fromRef;
    private String comment;
    private Long dnchId;



    @Embedded
    @AttributeOverrides({@AttributeOverride(name="id", column = @Column(name = "created_user_id")),
            @AttributeOverride(name="name", column = @Column(name = "created_user_name")),
            @AttributeOverride(name="date", column = @Column(name = "created_date", insertable = false)),
    })
    private UserActionRef created;
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="id", column = @Column(name = "agreed_user_id")),
            @AttributeOverride(name="name", column = @Column(name = "agreed_user_name")),
            @AttributeOverride(name="date", column = @Column(name = "agreed_date")),
    })
    private UserActionRef agreed;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name="id", column = @Column(name = "approved_user_id")),
            @AttributeOverride(name="name", column = @Column(name = "approved_user_name")),
            @AttributeOverride(name="date", column = @Column(name = "approved_date")),
    })
    private UserActionRef approved;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name="id", column = @Column(name = "changed_user_id")),
            @AttributeOverride(name="name", column = @Column(name = "changed_user_name")),
            @AttributeOverride(name="date", column = @Column(name = "changed_date")),
    })
    private UserActionRef changed;

    private LocalDate fromDate;
    private LocalDate toDate;

    @ElementCollection
    @CollectionTable(
            schema = "kn",
            name="template_norm",
            joinColumns=@JoinColumn(name="template_id")
    )
    @OrderColumn(name="index")
    private List<TemplateNorm> norms;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getMainId() {
        return mainId;
    }

    public void setMainId(Integer mainId) {
        this.mainId = mainId;
    }

    public PredLevel getLevel() {
        return level;
    }

    public void setLevel(PredLevel level) {
        this.level = level;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getPostFullName() {
        return postFullName;
    }

    public void setPostFullName(String postFullName) {
        this.postFullName = postFullName;
    }

    public List<TemplateNorm> getNorms() {
        return norms;
    }

    public void setNorms(List<TemplateNorm> norms) {
        this.norms = norms;
    }

    public Integer getFromRef() {
        return fromRef;
    }

    public void setFromRef(Integer fromRef) {
        this.fromRef = fromRef;
    }

    public UserActionRef getCreated() {
        return created;
    }

    public void setCreated(UserActionRef created) {
        this.created = created;
    }

    public UserActionRef getAgreed() {
        return agreed;
    }

    public void setAgreed(UserActionRef agreed) {
        this.agreed = agreed;
    }

    public UserActionRef getApproved() {
        return approved;
    }

    public void setApproved(UserActionRef approved) {
        this.approved = approved;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public String getComment() {return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public Long getDnchId() {
        return dnchId;
    }

    public void setDnchId(Long dnchId) {
        this.dnchId = dnchId;
    }

    public UserActionRef getChanged() {
        return changed;
    }

    public void setChanged(UserActionRef changed) {
        this.changed = changed;
    }

    @JsonIgnore
    public int getHash() {
        return Objects.hash(id, status, mainId, level, postName, postFullName, fromRef, comment, dnchId, created, agreed, approved, fromDate, toDate, norms);
    }
}
