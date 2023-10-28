package org.niias;

import org.apache.poi.xwpf.usermodel.*;
import org.junit.Test;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.io.FileOutputStream;
import java.io.IOException;

public class WordTest {


    @Test
    public void test() throws IOException {

        XWPFDocument doc =  new XWPFDocument();
        XWPFTable table = doc.createTable(2, 2);

        XWPFTableRow row0 = table.getRow(0);
        XWPFTableCell cell0 = row0.getCell(0);
        fillParagraph(cell0.addParagraph(), "xxx");
        cell0.getCTTc().setTcPr(CTTcPr.Factory.newInstance());
        cell0.getCTTc().getTcPr().setHMerge(getHMerge(false));

        XWPFTableCell cell1 = row0.getCell(1);

        cell1.getCTTc().setTcPr(CTTcPr.Factory.newInstance());
        cell1.getCTTc().getTcPr().setHMerge(getHMerge(true));

        cell1.addParagraph();
        //fillParagraph(cell1.addParagraph(), "yyy");

        XWPFTableRow row1 = table.getRow(1);
        cell0 = row1.getCell(0);
        fillParagraph(cell0.addParagraph(), "xxx");
        cell1 = row1.getCell(1);
        fillParagraph(cell1.addParagraph(), "yyy");


        doc.write(new FileOutputStream("some.docx"));
    }

    private CTHMerge getHMerge(boolean cont){
        CTHMerge merge = CTHMerge.Factory.newInstance();
        merge.setVal(cont ? STMerge.CONTINUE : STMerge.RESTART);
        return merge;
    }

    private CTVMerge getVMerge(boolean cont){
        CTVMerge merge = CTVMerge.Factory.newInstance();
        merge.setVal(cont ? STMerge.CONTINUE : STMerge.RESTART);
        return merge;
    }

    private void fillParagraph(XWPFParagraph p, String text){
        p.setAlignment(ParagraphAlignment.LEFT);
        //p1.setVerticalAlignment(TextAlignment.TOP);

        XWPFRun r1 = p.createRun();
        r1.setBold(true);
        r1.setText(text);
        r1.setTextPosition(100);
    }

}
