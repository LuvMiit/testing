package org.niias.asrb.kn.model;

import javax.persistence.*;

@Entity
@Table(schema = "kn", name = "dnch_to_blank_norm" )
public class DnchNormToBlankRef {
    @Id
    @SequenceGenerator(name="kn.dnch_blank_norm_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    //ид плана (у плана есть отчет)
    private Long dnchId;

    //месяц плана (начинается с 1)
    private Integer month;

    private Integer blankId;

    public DnchNormToBlankRef() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getDnchId() {
        return dnchId;
    }

    public void setDnchId(Long dnchId) {
        this.dnchId = dnchId;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getBlankId() {
        return blankId;
    }

    public void setBlankId(Integer blankId) {
        this.blankId = blankId;
    }
}
