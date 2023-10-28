package org.niias.asrb.kn.model;

import org.niias.asrb.model.SubsystemRole;
import org.niias.asrb.model.User;
import org.niias.asrb.model.UserSystem;

import java.util.Optional;

public class UserKnImpl implements UserKn {

    public static final UserKn EMPTY = new UserKnImpl(new User());

    private User user;

    public UserKnImpl(User user) {
        this.user = user;
    }

    private UserKnImpl(){

    }

    @Override
    public VerticalDto getVertical() {
        return (VerticalDto) user.getExtras().get("vertical");
    }

    @Override
    public boolean hasRbRegRole() {
        return user.getAllowedSystems() != null && user.getAllowedSystems().stream().map(UserSystem::getRole).anyMatch(it-> it== SubsystemRole.KN_RB_REG_USER);
    }

    @Override
    public User getUser() {
        return user;
    }
}
