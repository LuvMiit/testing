package org.niias.asrb.kn.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(schema = "IC00", name = "H_PRED")
public class HPred {

    @Id
    @Column(name = "H_PRED_ID")
    private Integer hid;

    @Column(name = "PRED_ID")
    private Integer id;

    @Column(name = "DOR_KOD")
    private Railway railway;

    @Column(name = "GR_ID")
    private Integer grId;

    @Column(name = "VD_ID")
    private Integer vdId;

    @Column(name = "VNAME")
    private String vname;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SNAME")
    private String sname;

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

    @Column(name = "OKPO_KOD")
    private Integer okpoKod;

    @Column(name = "MESTO")
    private String place;

    @Column(name = "STAN_M_ID")
    private Integer stanId;

    @Column(name = "NOM")
    private Integer nom;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Railway getRailway() {
        return railway;
    }

    public void setRailway(Railway railway) {
        this.railway = railway;
    }

    public Integer getGrId() {
        return grId;
    }

    public void setGrId(Integer grId) {
        this.grId = grId;
    }

    public Integer getVdId() {
        return vdId;
    }

    public void setVdId(Integer vdId) {
        this.vdId = vdId;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOkpoKod() {
        return okpoKod;
    }

    public void setOkpoKod(Integer okpoKod) {
        this.okpoKod = okpoKod;
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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Date getCorTime() {
        return corTime;
    }

    public void setCorTime(Date corTime) {
        this.corTime = corTime;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public Integer getHid() {
        return hid;
    }

    public void setHid(Integer hid) {
        this.hid = hid;
    }

    public String getCorTip() {
        return corTip;
    }

    public void setCorTip(String corTip) {
        this.corTip = corTip;
    }

    public Integer getStanId() {
        return stanId;
    }

    public void setStanId(Integer stanId) {
        this.stanId = stanId;
    }

    public Integer getNom() {
        return nom;
    }

    public void setNom(Integer nom) {
        this.nom = nom;
    }
}
