package org.niias.asrb.kn.model;

import javax.persistence.*;

@Entity
@Table(schema = "kn", name = "blank_file")
public class BlankFile {

    public BlankFile() {
    }

    @Id
    @SequenceGenerator(name="kn.blank_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private Integer month;

    @Embedded
    private FileRef fileRef;


    private Long dnchId;

    public BlankFile(Integer id, Integer month, FileRef fileRef, Long dnchId) {
        this.id = id;
        this.month = month;
        this.fileRef = fileRef;
        this.dnchId = dnchId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public FileRef getFileRef() {
        return fileRef;
    }

    public void setFileRef(FileRef fileRef) {
        this.fileRef = fileRef;
    }

    public Long getDnchId() {
        return dnchId;
    }

    public void setDnchId(Long dnchId) {
        this.dnchId = dnchId;
    }

    public String toStringForLog() {
        return "BlankFile{" +
                "id=" + id +
                ", month=" + month +
                ", fileRef=" + fileRef==null ? "" : fileRef.toStringForLog() +
                ", dnchId=" + dnchId +
                '}';
    }
}
