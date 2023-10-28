package org.niias.asrb.kn.export;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlankRow {
    @Singular
    List<BlankCol> cols = new ArrayList<>();
}
