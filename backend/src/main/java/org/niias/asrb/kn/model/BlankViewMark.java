package org.niias.asrb.kn.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(schema="kn", name = "blank_view")
public class BlankViewMark {
    @Id
    @SequenceGenerator(name="kn.blank_view_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @Column (name = "blank_id", insertable = false, updatable = false)
    private Integer blank_id;
    private Integer userId;

    private String userName;

    private String userPred;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate viewDate;

    private String comment;

    public Integer getViewId(){return id;}

    public void setViewId(Integer viewId){this.id=id;}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDate getViewDate() {
        return viewDate;
    }

    public void setViewDate(LocalDate viewDate) {
        this.viewDate = viewDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserPred() {
        return userPred;
    }

    public void setUserPred(String userPred) {
        this.userPred = userPred;
    }


    public String viewInfo() {
        final DateTimeFormatter  fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        return (viewDate != null ? fmt.format(viewDate.atStartOfDay())+ " " : "") + userPred + " " +  userName;
    }
}
