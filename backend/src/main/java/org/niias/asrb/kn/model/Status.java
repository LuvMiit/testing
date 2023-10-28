package org.niias.asrb.kn.model;

public enum Status {

    draft("черновик"),
    agreement("на согласовании"),
    approval("на утверждении"),
    approved("утвержден"),
    outdated("устаревший");


    private String statusName;

    Status(String statusName){
        this.statusName = statusName;
    }

}
