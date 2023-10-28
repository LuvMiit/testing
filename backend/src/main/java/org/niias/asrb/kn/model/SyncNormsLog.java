package org.niias.asrb.kn.model;

import lombok.ToString;
import org.niias.asrb.kn.util.DateUtil;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//логи процесса синхронизации действующих нормативов
@ToString
@Entity
@Table(schema = "kn", name = "sync_norms_log")
public class SyncNormsLog {
    @Id
    @SequenceGenerator(name="kn.sync_norms_log_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    private String initiator;
    @Column(name = "process_date")
    private Date processDate;
    private String error;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name="sync_norms_log_id", referencedColumnName="id", nullable = false, updatable = false)
    @OrderBy("id")
    private List<SyncNormLog> normLogs = new ArrayList<>();

    public SyncNormsLog() {
        this.processDate = new Date();
    }

    public SyncNormsLog(long startDate, long endDate, SyncInitiator initiator) {
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

    public List<SyncNormLog> getNormLogs() {
        return normLogs;
    }

    public void addLog(SyncNormLog log) {
        normLogs.add(log);
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

    public void setNormLogs(List<SyncNormLog> normLogs) {
        this.normLogs = normLogs;
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
