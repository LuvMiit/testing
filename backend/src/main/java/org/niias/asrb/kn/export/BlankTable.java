package org.niias.asrb.kn.export;

import lombok.Data;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Data
public class BlankTable {

    private int rowSize;
    private int colSize;
    private String modelName;

    public BlankTable(int rowSize, int colSize){
        this.rowSize = rowSize;
        this.colSize = colSize;
        this.initCols();
    }

    private void initCols(){
        IntStream.range(0, rowSize).forEach(ri->{
            BlankRow.BlankRowBuilder row = BlankRow.builder();
            IntStream.range(0, colSize).boxed().forEach(ci-> row.col(BlankCol.builder().build()));
            rows.add(row.build());
        });
    }

    @Singular
    List<BlankRow> rows = new ArrayList<>();
}
