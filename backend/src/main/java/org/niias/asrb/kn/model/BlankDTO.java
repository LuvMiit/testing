package org.niias.asrb.kn.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor

public class BlankDTO {

    @Getter
    @Setter
    private int year;

    @Getter
    @Setter
    private int month;

    @Getter
    @Setter
    private int createdUserId;

    @Getter
    @Setter
    private Long count;

    @Getter
    @Setter
    private List<String> blankIdList;

}
