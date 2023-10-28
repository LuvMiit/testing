package org.niias.asrb.kn.model;

import com.google.common.base.Joiner;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ToString
@Entity
@Table(schema = "kn", name = "sync_report_log" )
public class SyncReportLog {

    @Id
    @SequenceGenerator(name="kn.sync_report_log_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "report_document")
    private String reportDocument;
    private String error;
    @Column(name = "dnch_id")
    private Long dnchId;
    @Column(name = "blank_norms")
    private String blankNorms;
    @Column(name = "log_list")
    private String logList = "";
    private String files;

    public SyncReportLog() {
    }

    private SyncReportLog(Map reportDocument, String error) {
        this.reportDocument = mapToString(reportDocument);
        this.error = error;
    }

    public SyncReportLog(Map reportDocument, Long dnchId, List<BlankFile> blankNorms) {
        this.reportDocument = mapToString(reportDocument);
        this.dnchId = dnchId;
        this.blankNorms = blankNorms == null ? "" : Joiner.on("<br/>").join(blankNorms.stream().map(BlankFile::toStringForLog).collect(Collectors.toList()));;
    }

    private String mapToString(Map map) {
        return map == null ? "" : map.toString();
    }

    public static SyncReportLog buildError(Map reportDocument, String error) {
        return new SyncReportLog(reportDocument, error);
    }

    public static SyncReportLog build(Map reportDocument, Long dnchId, List<BlankFile> blankNorms) {
        return new SyncReportLog(reportDocument, dnchId, blankNorms);
    }

    public void addToLogList(String message) {
        if (logList.isEmpty()) {
            logList += message;
        } else {
            logList += "<br/>"+message;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setReportDocument(String reportDocument) {
        this.reportDocument = reportDocument;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setDnchId(Long dnchId) {
        this.dnchId = dnchId;
    }

    public void setBlankNorms(String blankNorms) {
        this.blankNorms = blankNorms;
    }

    public void setLogList(String logList) {
        this.logList = logList;
    }

    public void setFiles(String files) {
        this.files = files;
    }

    public void setFiles(List<Map> files) {
        if (files != null) {
            this.files = files.stream().map(m -> {
                Map map = new HashMap();
                map.put("idapplication", m.get("idapplication"));
                map.put("nameapplication", m.get("nameapplication"));
                map.put("descriptionapplication", m.get("descriptionapplication"));
                return map;
            }).collect(Collectors.toList()).toString();
        }
    }

    public String getReportDocument() {
        return reportDocument;
    }

    public String getError() {
        return error;
    }

    public Long getDnchId() {
        return dnchId;
    }

    public String getBlankNorms() {
        return blankNorms;
    }

    public String getLogList() {
        return logList;
    }

    public String getFiles() {
        return files;
    }
}
