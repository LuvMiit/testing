package org.niias.asrb.kn.myversion;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class BlanksViewsDTO {
    @Getter
    @Setter
    private int createdUserId;

    @Setter
    @Getter
    private Map<Map<Integer, Integer>, List<Integer>> blankIdList;

    @Getter
    @Setter
    private Map<Map<Integer, Integer>, List<Integer>> blankViewIdList;

    @Override
    public String toString() {
        return "BlanksViewsDTO{" +
                "createdUserId=" + createdUserId +
                ", blankIdList=" + blankIdList +
                ", blankViewIdList=" + blankViewIdList +
                '}';
    }
}
