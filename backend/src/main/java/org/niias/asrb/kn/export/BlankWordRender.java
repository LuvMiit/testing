package org.niias.asrb.kn.export;

import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.niias.asrb.kn.model.PredLevel;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

import static org.niias.asrb.kn.export.ColType.*;

public class BlankWordRender extends AbstractWordRender<BlankExportModel>{

    public BlankWordRender(BlankExportModel model){
        super(model);
        pageInit(model.getQuarters().size() >2  ? "landscape": "portrait");
    }


    public byte[] render() throws IOException {

        XWPFTable table = null;

        CTP ctp = CTP.Factory.newInstance();
        CTR ctr = ctp.addNewR();
        CTText text = ctr.addNewInstrText();
        text.setStringValue("Лист ");
        text.setSpace(SpaceAttribute.Space.Enum.forInt(2));

        ctp.addNewR().addNewPgNum();

        ctr = ctp.addNewR();
        text = ctr.addNewInstrText();
        text.setStringValue(" из ");
        text.setSpace(SpaceAttribute.Space.Enum.forInt(2));

        CTText totalPageNum = ctp.addNewR().addNewT();

         XWPFParagraph codePara = new XWPFParagraph(ctp, doc);
        XWPFParagraph[] paragraphs = new XWPFParagraph[1];
        paragraphs[0] = codePara;
        codePara.setAlignment(ParagraphAlignment.CENTER);

        CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();

        int rowIdx = 2;
        while (rowIdx < model.getTable().getRowSize()){
            BlankRow mr = model.getTable().getRows().get(rowIdx);
            int linesNum = lineNum(mr.getCols().get(1).getValue(), getColWith(1));
            int h = linesNum * height11;
            if (h + hc.getTotal() >= hc.getPageTotal())
            {
                if (rowIdx != 2)
                    pageBreak();
                hc.nextPage();
                addHead();
                table = createTable();
                //hc.add(1600);
            }

            hc.add(h);
            addRow(table, rowIdx++);
        }

        totalPageNum.setStringValue(String.valueOf(hc.getPage()));

        XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(doc, sectPr);
        headerFooterPolicy.createFooter(STHdrFtr.DEFAULT, paragraphs);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.write(out);
        return out.toByteArray();
    }





    private int getColWith(int colIdx){
        if (colIdx == 0)
            return 567;

        if (colIdx == 1 && model.getQuarters().size() == 4)
            return 5670;


        if (colIdx == 1 && model.getQuarters().size() != 4)
            return 7655;

        if (colIdx > 1 && colIdx < model.getQuarters().size() * 3 + 2)
            return 425;

        if (colIdx > model.getQuarters().size() * 3 + 2 - 1 && colIdx < model.getTable().getColSize() -1 )
            return 652;

        if (colIdx == model.getTable().getColSize() - 1)
            return 822;


        return 0;
    }


    private void addHead(){
        //за III,IV квартал 2021г.
        StringBuilder buff = new StringBuilder(model.getDocName());
        if (model.getQuarters().size() > 2)
            buff.append( " за " + model.getYear() + " год");
        else
            buff.append(" за " + model.getTable().getModelName() + " " + model.getYear() + " г.");

        fillParagraph(doc.createParagraph(), buff.toString() +  "\n" + model.getUserPred() + " " + model.getUserStan() + " - " + model.getUserName(),
                TextStyle.builder().center(true).fontSize(14).build(), true);

        buff = new StringBuilder();
        if (Arrays.asList(PredLevel.regional, PredLevel.linear).contains(model.getLevel()))
            buff.append(model.getMain());
        if (Arrays.asList(PredLevel.linear).contains(model.getLevel()))
            buff.append("-").append(model.getReg());

        fillParagraph(doc.createParagraph(), buff.toString(),
                TextStyle.builder().right(true).fontSize(14).build(), true);
    }


    private XWPFTable createTable(){
        int rowSize = model.getTable().getRowSize();
        //final XWPFTable table = doc.createTable(rowSize, model.getTable().getColSize());
        final XWPFTable table = doc.createTable();
        table.setCellMargins(0, 100, 0, 100);
        table.setWidth("100%");
        IntStream.range(0, 2).forEach(i->addRow(table, i));
        return table;
    }


    private void addRow(XWPFTable table, int rowIdx){
        final BlankRow modelRow = model.getTable().getRows().get(rowIdx);
        final XWPFTableRow row = table.getRow(rowIdx) == null ? table.createRow() : table.getRow(rowIdx);
        if (rowIdx == 1)
            row.setHeight(800);

        IntStream.range(0, modelRow.getCols().size()).forEach(colIdx->{
            BlankCol modelCol = modelRow.getCols().get(colIdx);
            XWPFTableCell cell  = row.getCell(colIdx) == null ? row.createCell(): row.getCell(colIdx);
            if (!cell.getCTTc().isSetTcPr())
                cell.getCTTc().setTcPr(CTTcPr.Factory.newInstance());

            if (Arrays.asList(QUARTER_SCORE, YEAR_SCORE, MONTH).contains(modelCol.getType()))
            {
                cell.setColor("ffffcc");
                if (modelCol.isCompleted())
                    cell.setColor("92d050");
                if (modelCol.isFailed())
                    cell.setColor("e67676");
            }

            TextStyle.TextStyleBuilder style = TextStyle.builder();
            if (modelCol.getType() == HEADER)
            {
                if (modelCol.getMarks().indexOf("month-name") != -1)
                    style.nowrap(true);

                if (modelCol.getMarks().indexOf("month-name") == -1 && modelCol.getMarks().indexOf("quarter-total") == -1) {
                    style.bold(true);
                    style.middle(true);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
                else
                    cell.getCTTc().getTcPr().addNewTextDirection().setVal(STTextDirection.BT_LR);
            }

            fillParagraph(cell.addParagraph(), modelCol.getValue(), style.build(), false);

            int width = getColWith(colIdx);
            int colspan = 1;
            if (modelCol.getHMerge() == Merge.RESTART) {
                int restartColspanIndex = IntStream.range(colIdx + 1, model.getTable().getColSize()).boxed().filter(nextIdx -> modelRow.getCols().get(nextIdx).getHMerge() == Merge.RESTART).findFirst().orElse(colIdx);
                int delta = restartColspanIndex - colIdx;
                colspan = delta;
            }

            String strWidth = "";
            if (width == 0 || colspan > 1)
                strWidth = "auto";
            else
                strWidth = "" + width;

            cell.setWidthType(TableWidthType.DXA);
            cell.setWidth(strWidth);

            cell.getCTTc().getTcPr().setVMerge(modelCol.getVMerge() == Merge.RESTART ? getVMerge(false): getVMerge(true));
            cell.getCTTc().getTcPr().setHMerge(modelCol.getHMerge() == Merge.RESTART ? getHMerge(false): getHMerge(true));

        });
    }





}
