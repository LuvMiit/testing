package org.niias.asrb.kn.export;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Body {
    List<BlankRow> rows = new ArrayList<>();
}
