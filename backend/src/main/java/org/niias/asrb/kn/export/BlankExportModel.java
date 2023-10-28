package org.niias.asrb.kn.export;

import lombok.Data;
import org.niias.asrb.kn.model.PredLevel;
import org.niias.asrb.model.User;
import org.niias.asrb.kn.model.VerticalDto;

import java.util.List;

@Data
//@Builder(toBuilder = true)
public class BlankExportModel implements ExportModel{
    private String docName;
    private Integer year;
    private String userStan;
    private String userPred;
    private String userName;
    private String main;
    private String reg;
    private PredLevel level;
    private List<Integer> quarters;

    private BlankTable table;
}
