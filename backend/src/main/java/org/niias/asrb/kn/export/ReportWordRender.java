package org.niias.asrb.kn.export;

import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.niias.asrb.kn.model.Months;
import org.niias.asrb.kn.model.Quarters;
import org.niias.asrb.kn.service.ReportService;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.IntStream;

public class ReportWordRender extends AbstractWordRender<ReportService.ReportResult> {

    public ReportWordRender(ReportService.ReportResult model) {
        super(model);
        pageInit("landscape");
    }

    private void addHead(){
        StringBuilder buff = new StringBuilder();
        buff.append("Параметры отчета: ");
        if (model.getCentralName() != null)
            buff.append(model.getCentralName());
        if (model.getRegionalName() != null)
            buff.append(model.getRegionalName());
        if (model.getLinearName() != null)
            buff.append(model.getLinearName());
        if (model.getParam().level != null)
            buff.append(model.getParam().level.getLabel());
        if (model.getReportType() == ReportService.ReportType.POST)
        {
            buff.append("должности ");
            IntStream.range(0, model.getParam().post.size()).forEach(i->{
                if (i != 0)
                    buff.append(", ");
                buff.append(model.getParam().post.get(i));
            });
        }

        fillParagraph(doc.createParagraph(), buff.toString(),
                TextStyle.builder().center(true).fontSize(14).build(), true);

    }

    private XWPFTable createTable(){
        final XWPFTable table = doc.createTable();
        table.setCellMargins(0, 100, 0, 100);
        table.setWidth("100%");

        final XWPFTableRow topRow = table.getRow(0) == null ? table.createRow() : table.getRow(0);

        XWPFTableCell cell = topRow.getCell(0) == null ? topRow.createCell(): topRow.getCell(0);
        if (!cell.getCTTc().isSetTcPr())
            cell.getCTTc().setTcPr(CTTcPr.Factory.newInstance());
        cell.getCTTc().getTcPr().setVMerge(getVMerge(false));
        fillParagraph(cell.addParagraph(), this.model.getReportType() == ReportService.ReportType.POST ? "Должность": "Наименование подразделения", TextStyle.builder().bold(true).build(), true);

        final XWPFTableRow bottomRow = table.getRow(1) == null ? table.createRow() : table.getRow(1);
        bottomRow.setHeight(800);
        XWPFTableCell tmpCell = bottomRow.getCell(0) == null ? bottomRow.createCell(): bottomRow.getCell(0);
        if (!tmpCell.getCTTc().isSetTcPr())
            tmpCell.getCTTc().setTcPr(CTTcPr.Factory.newInstance());
        tmpCell.getCTTc().getTcPr().setVMerge(getVMerge(true));
        fillParagraph(tmpCell.addParagraph(), "", TextStyle.builder().build(), false);

        IntStream.range(0, 12).forEach(m-> {
            XWPFTableCell col = topRow.getCell(m+1) == null ? topRow.createCell(): topRow.getCell(m+1);

            if (m % 3 == 0)
            {
                if (!col.getCTTc().isSetTcPr())
                    col.getCTTc().setTcPr(CTTcPr.Factory.newInstance());
                col.getCTTc().getTcPr().setHMerge(getHMerge(false));
                fillParagraph(col.addParagraph(), Quarters.values()[m / 3].getName(), TextStyle.builder().bold(true).middle(true).build(), true);
                col.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            }else{
                if (!col.getCTTc().isSetTcPr())
                    col.getCTTc().setTcPr(CTTcPr.Factory.newInstance());
                col.getCTTc().getTcPr().setHMerge(getHMerge(true));
                fillParagraph(col.addParagraph(), "", TextStyle.builder().build(), false);
            }
        });

        IntStream.range(0, 12).forEach(m->{
            XWPFTableCell col = bottomRow.getCell(m+1) == null ? bottomRow.createCell(): bottomRow.getCell(m+1);
            fillParagraph(col.addParagraph(), Months.values()[m].getName(), TextStyle.builder().bold(true).build(), true);
            if (!col.getCTTc().isSetTcPr())
                col.getCTTc().setTcPr(CTTcPr.Factory.newInstance());
            col.getCTTc().getTcPr().addNewTextDirection().setVal(STTextDirection.BT_LR);
        });

        return table;
    }

    private int getColWith(int colIdx){
        if (colIdx == 0)
            return 5670;

        if (colIdx > 0)
            return 652;


        return 0;
    }


    private void addRow(XWPFTable table, int rowIdx) {
        final ReportService.ReportItem item = model.getItems().get(rowIdx);
        final XWPFTableRow row = table.getRow(rowIdx + 2) == null ? table.createRow() : table.getRow(rowIdx + 2);

        XWPFTableCell nameCell = row.getCell(0) == null ? row.createCell(): row.getCell(0);
        if (!nameCell.getCTTc().isSetTcPr())
            nameCell.getCTTc().setTcPr(CTTcPr.Factory.newInstance());

        if (model.getReportType() == ReportService.ReportType.POST)
            fillParagraph(nameCell.addParagraph(), item.getPost() + " " + (item.getUserName() == null ? "": item.getUserName()), TextStyle.builder().build(), false);
        else
            fillParagraph(nameCell.addParagraph(),item.getPredName() + " " + (item.getUserName() == null ? "": item.getUserName()), TextStyle.builder().build(), false);

        setWidth(nameCell, 0);

        IntStream.range(0, 12).forEach(i->{

            XWPFTableCell cell = row.getCell(i+1) == null ? row.createCell(): row.getCell(i+1);
            if (!cell.getCTTc().isSetTcPr())
                cell.getCTTc().setTcPr(CTTcPr.Factory.newInstance());

            ReportService.ValuePair pair = item.getValues().get(i);
            fillParagraph(cell.addParagraph(),pair.getCompleted() + "/" + pair.getFailed(), TextStyle.builder().build(), false);

            setWidth(cell, i+1);

        });

    }

    private void setWidth(XWPFTableCell cell, int index){
        int width = getColWith(index);
        String strWidth = "";
        if (width == 0)
            strWidth = "auto";
        else
            strWidth = "" + width;

        cell.setWidthType(TableWidthType.DXA);
        cell.setWidth(strWidth);

    }

    @Override
    public byte[] render() throws IOException {



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

        XWPFTable table = null;
        int rowIdx = 0;
        while (rowIdx < model.getItems().size()){
            ReportService.ReportItem item = model.getItems().get(rowIdx);
            StringBuilder buff = new StringBuilder();
            buff.append(model.getReportType() == ReportService.ReportType.POST ? item.getPost(): item.getPredName());
            buff.append(" ");
            buff.append(item.getUserName() == null ? "": item.getUserName());
            int linesNum = lineNum(buff.toString(), 5670);
            int h = linesNum * height11;
            if (h + hc.getTotal() >= hc.getPageTotal())
            {
                if (rowIdx != 0)
                    pageBreak();
                hc.nextPage();
                addHead();
                table = createTable();
                //hc.add(1600);
            }

            hc.add(h);
            addRow(table, rowIdx++);
        }


//        XWPFTable table = createTable();
//        addHead();
//        IntStream.range(0, model.getItems().size()).forEach(rowIdx->addRow(table, rowIdx));

        //--
        totalPageNum.setStringValue(String.valueOf(hc.getPage()));

        XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(doc, sectPr);
        headerFooterPolicy.createFooter(STHdrFtr.DEFAULT, paragraphs);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.write(out);
        return out.toByteArray();
    }


}
