package org.niias.asrb.kn.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class FileRef {

    public FileRef(){}

    public FileRef(Integer fileId, String fileName, String fileType){
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileType = fileType;
    }

    @Column(name = "file_id")
    private Integer fileId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String toStringForLog() {
        return "FileRef{" +
                "fileId=" + fileId +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}
