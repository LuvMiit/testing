package org.niias.asrb.kn.export;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlankCol {

    String value;
    boolean completed;
    boolean failed;
    @Builder.Default
    String marks = "";
    @Builder.Default
    ColType type = ColType.PLAIN;
    @Builder.Default
    Merge hMerge = Merge.RESTART;
    @Builder.Default
    Merge vMerge = Merge.RESTART;

}
