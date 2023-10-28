package org.niias.asrb.kn.model;

import javax.persistence.*;

@Entity
@Table(schema = "kn", name = "file")
public class File {

    @Id
    @SequenceGenerator(name="kn.file_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    private String name;

    @Column(name="mime")
    private String mimeType;

    private byte[] data;

    public FileRef getRef(){
        return new FileRef(id, name, mimeType);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
