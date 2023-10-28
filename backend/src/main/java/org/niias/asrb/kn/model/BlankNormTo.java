package org.niias.asrb.kn.model;

import lombok.Data;

@Data
public class BlankNormTo {

    CompletionMark completion;
    Period period;
    CompletionMark customPeriod;
    CompletionMark plan;

    public BlankNormTo(BlankNorm full_norm){
        this.completion = full_norm.getCompletion();
        this.period = full_norm.getPeriod();
        this.customPeriod = full_norm.getCustomPeriod();
    }

    public BlankNormTo(BlankNormDocument full_norm){
        this((BlankNorm) full_norm);
        this.plan = full_norm.getPlan();
    }
}
