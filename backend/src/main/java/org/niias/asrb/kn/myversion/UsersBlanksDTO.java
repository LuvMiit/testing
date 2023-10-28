package org.niias.asrb.kn.myversion;

import lombok.Getter;
import lombok.Setter;

public class UsersBlanksDTO extends BlanksViewsDTO{

    @Getter
    @Setter
    private String railwayShortName;

    @Getter
    @Setter
    private String subdivision;

    @Getter
    @Setter
    private String fio;
}
