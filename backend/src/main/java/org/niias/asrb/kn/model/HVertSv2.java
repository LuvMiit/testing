package org.niias.asrb.kn.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(schema = "IC00", name = "H_VERTSV2")
public class HVertSv2 {

    @Id
    @Column(name = "H_VERTSV2_ID")
    private Integer hid;

    @Column(name = "PRED_V_ID")
    private Integer predVid;

    @Column(name = "PRED_N_ID")
    private Integer predNid;

    @Column(name = "VERT_SV_ID")
    private Integer id;

    @Column(name = "VID_PODCH_PRIZ")
    private Integer vidPodch;

    @Column(name = "VERTSV_VID_ID")
    private Integer vid;

    @Column(name = "OPER_ID")
    private Integer operId;

    @Column(name = "REPL_FL")
    private Integer repl;

    @Column(name = "DATE_ND")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateNd;

    @Column(name = "DATE_KD")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateKd;

    @Column(name = "COR_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date corTime;

    @Column(name = "COR_TIP")
    private String corTip;

    public Integer getHid() {
        return hid;
    }

    public void setHid(Integer hid) {
        this.hid = hid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVidPodch() {
        return vidPodch;
    }

    public void setVidPodch(Integer vidPodch) {
        this.vidPodch = vidPodch;
    }

    public Integer getVid() {
        return vid;
    }

    public void setVid(Integer vid) {
        this.vid = vid;
    }

    public Integer getOperId() {
        return operId;
    }

    public void setOperId(Integer operId) {
        this.operId = operId;
    }

    public Integer getRepl() {
        return repl;
    }

    public void setRepl(Integer repl) {
        this.repl = repl;
    }

    public Date getDateNd() {
        return dateNd;
    }

    public void setDateNd(Date dateNd) {
        this.dateNd = dateNd;
    }

    public Date getDateKd() {
        return dateKd;
    }

    public void setDateKd(Date dateKd) {
        this.dateKd = dateKd;
    }

    public Date getCorTime() {
        return corTime;
    }

    public void setCorTime(Date corTime) {
        this.corTime = corTime;
    }

    public Integer getPredVid() {
        return predVid;
    }

    public void setPredVid(Integer predVid) {
        this.predVid = predVid;
    }

    public Integer getPredNid() {
        return predNid;
    }

    public void setPredNid(Integer predNid) {
        this.predNid = predNid;
    }

    public String getCorTip() {
        return corTip;
    }

    public void setCorTip(String corTip) {
        this.corTip = corTip;
    }

}
