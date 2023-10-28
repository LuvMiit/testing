package org.niias.asrb.kn.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.niias.asrb.kn.model.PredLevel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

import static org.niias.asrb.kn.export.ColType.*;

public class BlankPdfRender extends AbstractPdfRender<BlankExportModel>{

    public BlankPdfRender(BlankExportModel model){
        super(model, model.getQuarters().size() > 2 ? "landscape": "portrait");
    }

    private float [] getWidths(){
        float [] widths = new float[model.getTable().getColSize()];

        widths[0] = 30; //~ 1cm
        if (model.getQuarters().size() == 4)
            widths[1] = 300;
        if (model.getQuarters().size() == 3)
            widths[1] = 405;
        if (model.getQuarters().size() == 2)
            widths[1] = 210;
        if (model.getQuarters().size() == 1)
            widths[1] = 360;

        IntStream.range(2, model.getTable().getColSize() - model.getQuarters().size() - 1).forEach(i -> widths[i] = 22.5f);
        IntStream.range(2 + model.getQuarters().size() * 3, model.getTable().getColSize() - 1).forEach(i -> widths[i] = 45f);
        widths[model.getTable().getColSize() - 1] = 43.5f;

        return widths;
    }

    private PdfPTable createTable() throws DocumentException {
        PdfPTable pdfPTable = new PdfPTable(model.getTable().getColSize());
        pdfPTable.setSpacingBefore(5);
        float [] widths = getWidths();
        float totalWidth =  IntStream.range(0, widths.length -1).boxed().map(i-> widths[i]).reduce((a, b)-> a + b).get();

        pdfPTable.setWidths(widths);
        pdfPTable.setTotalWidth(totalWidth);
        pdfPTable.setLockedWidth(true);

        IntStream.range(0, 2).forEach(i->addRow(pdfPTable, i));
        return pdfPTable;
    }

    private void addRow(PdfPTable table, int rowIdx){
        BlankTable modelTable = model.getTable();
        BlankRow modelRow = modelTable.getRows().get(rowIdx);
        IntStream.range(0, modelTable.getColSize()).forEach(colIdx->{
            BlankCol modelCol = modelRow.getCols().get(colIdx);

            if (modelCol.getHMerge() == Merge.CONTINUE || modelCol.getVMerge() == Merge.CONTINUE)
                return;

            Font font = FONT_REGULAR;
            if (modelCol.getType() == HEADER && modelCol.getMarks().indexOf("month-name") == -1 && modelCol.getMarks().indexOf("quarter-total") == -1)
                font = FONT_REGULAR_BOLD;

            Paragraph p = new Paragraph(modelCol.getValue(), font);
//            if (colIdx == 1)
//                h.add(p, getWidths()[1]);
            //PdfPCell pdfCell = new PdfPCell(new Phrase(modelCol.getValue(), font));
            PdfPCell pdfCell = new PdfPCell(p);
            if (modelCol.getType() == HEADER)
            {
                if (modelCol.getMarks().indexOf("month-name") == -1 && modelCol.getMarks().indexOf("quarter-total") == -1)
                    pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                else
                    pdfCell.setRotation(90);
            }


            pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            if (Arrays.asList(QUARTER_SCORE, MONTH).contains(modelCol.getType()))
            {
                pdfCell.setBackgroundColor(baseColor);
                if (modelCol.isCompleted())
                    pdfCell.setBackgroundColor(completed);
                if (modelCol.isFailed())
                    pdfCell.setBackgroundColor(failed);
            }

            if (modelCol.getHMerge() == Merge.RESTART && colIdx < modelTable.getColSize() - 1){
                int restartColspanIndex = IntStream.range(colIdx + 1, modelTable.getColSize()).boxed().filter(nextIdx-> modelRow.getCols().get(nextIdx).getHMerge() == Merge.RESTART).findFirst().orElse(colIdx);
                int delta = restartColspanIndex - colIdx;
                if (delta > 1)
                    pdfCell.setColspan(delta);
            }

            if (modelCol.getVMerge() == Merge.RESTART && rowIdx < modelTable.getRowSize() - 1){
                int restartRowspanIndex = IntStream.range(rowIdx+1, modelTable.getRowSize()).boxed().filter(nextIdx-> modelTable.getRows().get(nextIdx).getCols().get(colIdx).getVMerge() == Merge.RESTART).findFirst().orElse(rowIdx);
                int delta = restartRowspanIndex - rowIdx;
                if (delta > 1)
                    pdfCell.setRowspan(delta);
            }

            table.addCell(pdfCell);

        });

    }

    private void addHead() throws DocumentException {
        StringBuilder buff = new StringBuilder(model.getDocName());
        if (model.getQuarters().size() > 2)
            buff.append( " за " + model.getYear() + " год");
        else
            buff.append(" за " + model.getTable().getModelName() + " " + model.getYear() + " г.");


        Paragraph header = new Paragraph(buff.toString() +  "\n" + model.getUserPred() + " " + model.getUserStan() + " - " + model.getUserName(), FONT_HEADER);
        header.setAlignment(Element.ALIGN_CENTER);
        doc.add(h.add(header));

        buff = new StringBuilder();
        if (Arrays.asList(PredLevel.regional, PredLevel.linear).contains(model.getLevel()))
            buff.append(model.getMain());
        if (Arrays.asList(PredLevel.linear).contains(model.getLevel()))
            buff.append("-").append(model.getReg());

        Paragraph subHeader = new Paragraph(buff.toString(), FONT_HEADER);
        subHeader.setAlignment(Element.ALIGN_RIGHT);

        doc.add(h.add(subHeader));

    }

    @Override
    public byte[] render() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(doc, buffer);
            doc.open();
            BlankTable blankTable = model.getTable();


            PdfPTable table = null;
            int rowIdx = 2;
            while (rowIdx < blankTable.getRowSize())
            {
                h.add(new Paragraph(model.getTable().getRows().get(rowIdx).getCols().get(1).getValue(), FONT_REGULAR), getWidths()[1]);
                if (h.getTotal() >= h.getDocHeight())
                {
                    if (table != null) {
                        doc.add(table);
                        doc.newPage();
                    }
                    h.nextPage();
                    addHead();
                    table = createTable();
                }

                addRow(table, rowIdx++);

            }
            doc.add(table);

            doc.close();
            return stampPages(buffer.toByteArray(), 0);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }



}
