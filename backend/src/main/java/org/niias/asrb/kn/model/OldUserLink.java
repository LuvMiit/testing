package org.niias.asrb.kn.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class OldUserLink implements Serializable {

    private Integer oldUserId;
    private Integer oldUserDorKod;
    private String oldUserName;

    public Integer getOldUserId() {
        return oldUserId;
    }

    public void setOldUserId(Integer oldUserId) {
        this.oldUserId = oldUserId;
    }

    public Integer getOldUserDorKod() {
        return oldUserDorKod;
    }

    public void setOldUserDorKod(Integer oldUserDorKod) {
        this.oldUserDorKod = oldUserDorKod;
    }

    public String getOldUserName() {
        return oldUserName;
    }

    public void setOldUserName(String oldUserName) {
        this.oldUserName = oldUserName;
    }
}
