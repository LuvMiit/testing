package org.niias.asrb.kn.model;

import lombok.ToString;

import javax.persistence.*;

@ToString
@Entity
@Table(schema = "kn", name = "sync_norm_log" )
public class SyncNormLog {

    @Id
    @SequenceGenerator(name="kn.sync_norm_log_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String error;
    @Column(name = "norm_plan_id")
    private Long normPlanId;
    @Column(name = "nlu_id")
    private Long nluId;
    @Column(name = "log_list")
    private String logList = "";

    public SyncNormLog() {
    }

    public SyncNormLog(Long normPlanId) {
        this.normPlanId = normPlanId;
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

    public void setError(String error) {
        this.error = error;
    }

    public void setNormPlanId(Long dnchId) {
        this.normPlanId = dnchId;
    }

    public void setLogList(String logList) {
        this.logList = logList;
    }


    public String getError() {
        return error;
    }

    public Long getNormPlanId() {
        return normPlanId;
    }

    public String getLogList() {
        return logList;
    }

    public Long getNluId() {
        return nluId;
    }

    public void setNluId(Long nluId) {
        this.nluId = nluId;
    }

}
