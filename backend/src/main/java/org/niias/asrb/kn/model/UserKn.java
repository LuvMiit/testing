package org.niias.asrb.kn.model;

import org.niias.asrb.model.User;

public interface UserKn {

    VerticalDto getVertical();

    boolean hasRbRegRole();

    User getUser();

}
