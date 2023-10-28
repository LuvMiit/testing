package org.niias.asrb.kn.model;

import com.querydsl.core.Tuple;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class BlankAndBlankViewDTO {
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
    private Long countB;

    @Getter
    @Setter
    private List<String> blankIdList;

    @Getter
    @Setter
    private Long countBv;

    @Getter
    @Setter
    private List<String> blankViewIdList;


    @Override
    public String toString() {
        return "BlankAndBlankViewDTO{" +
                "year=" + year +
                ", month=" + month +
                ", createdUserId=" + createdUserId +
                '}';
    }
}
