package org.niias.asrb.kn.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(schema = "IC00", name = "H_STAN")
public class HStan {
    @Id
    @Column(name = "STAN_ID")
    private Integer stanId;

    private Integer dorKod;

    private String name;

    @Column(name = "DATE_ND")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateNd;

    @Column(name = "DATE_KD")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateKd;

    @Column(name = "COR_TIP")
    private String corTip;

    public Integer getStanId() {
        return stanId;
    }

    public void setStanId(Integer stanId) {
        this.stanId = stanId;
    }

    public Integer getDorKod() {
        return dorKod;
    }

    public void setDorKod(Integer dorKod) {
        this.dorKod = dorKod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCorTip() {
        return corTip;
    }

    public void setCorTip(String corTip) {
        this.corTip = corTip;
    }
}




