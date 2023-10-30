package org.niias.asrb.kn.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
public class BlankTo {
    int id;
    int year;
    String postName;
    String predName;
    String userName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    LocalDate createDate;
    String view;
    List<BlankNormTo> norms;
}
