package org.niias.asrb.kn.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("DOC")
public class BlankNormDocument extends BlankNorm{

    public BlankNormDocument(){
        super();
        this.period = Period.selected;
    }

    private Integer docId;

    private CompletionMark plan = new CompletionMark();

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public CompletionMark getPlan() {
        return plan;
    }

    public void setPlan(CompletionMark plan) {
        this.plan = plan;
    }
}
