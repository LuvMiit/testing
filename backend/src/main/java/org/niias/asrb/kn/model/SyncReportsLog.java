package org.niias.asrb.kn.model;

import lombok.ToString;
import org.niias.asrb.kn.util.DateUtil;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//логи процесса синхронизации отчетов
@ToString
@Entity
@Table(schema = "kn", name = "sync_reports_log")
public class SyncReportsLog {
    @Id
    @SequenceGenerator(name="kn.sync_reports_log_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    private String initiator;
    @Column(name = "process_date")
    private Date processDate = new Date();
    private String error;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name="sync_reports_log_id", referencedColumnName="id", nullable = false, updatable = false)
    @OrderBy("id")
    private List<SyncReportLog> reportLogs = new ArrayList<>();

    public SyncReportsLog() {
        this.processDate = new Date();
    }

    public SyncReportsLog(long startDate, long endDate, SyncInitiator initiator) {
        this.startDate = new Date(startDate);
        this.endDate = new Date(endDate);
        this.initiator = initiator.name();
        this.processDate = new Date();
    }

    public String getInitiator() {
        return initiator;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public List<SyncReportLog> getReportLogs() {
        return reportLogs;
    }

    public void addActiveReportInfo(SyncReportLog info) {
        reportLogs.add(info);
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setInitiator(SyncInitiator initiator) {
        this.initiator = initiator.name();
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public void setReportLogs(List<SyncReportLog> activeReportInfo) {
        this.reportLogs = activeReportInfo;
    }

    public String getStartDateStr() {
        return DateUtil.formatDate(startDate);
    }

    public String getEndDateStr() {
        return DateUtil.formatDate(endDate);
    }

    public String getProcessDateStr() {
        return DateUtil.formatDate(processDate);
    }
}
