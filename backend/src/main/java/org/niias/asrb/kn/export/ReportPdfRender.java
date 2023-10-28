package org.niias.asrb.kn.export;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.niias.asrb.kn.model.Months;
import org.niias.asrb.kn.model.Quarters;
import org.niias.asrb.kn.service.ReportService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.IntStream;

public class ReportPdfRender extends AbstractPdfRender<ReportService.ReportResult>{


    public ReportPdfRender(ReportService.ReportResult model) {
        super(model, "landscape");
    }

    private void addHead() throws DocumentException {
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

        Paragraph header = new Paragraph(buff.toString(), FONT_HEADER);
        header.setAlignment(Element.ALIGN_CENTER);
        doc.add(h.add(header));
    }

    private PdfPTable createTable() throws DocumentException {
        PdfPTable pdfPTable = new PdfPTable(13);
        pdfPTable.setSpacingBefore(5);
        float [] widths = getWidths();
        float totalWidth =  IntStream.range(0, widths.length -1).boxed().map(i-> widths[i]).reduce((a, b)-> a + b).get();

        pdfPTable.setWidths(widths);
        pdfPTable.setTotalWidth(totalWidth);
        pdfPTable.setLockedWidth(true);

        PdfPCell cornerCell = new PdfPCell(new Paragraph(this.model.getReportType() == ReportService.ReportType.POST ? "Должность": "Наименование подразделения", FONT_REGULAR_BOLD));
        cornerCell.setRowspan(2);
        cornerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfPTable.addCell(cornerCell);

        IntStream.range(0, 12).forEach(m-> {
            if (m % 3 == 0)
            {
                PdfPCell cell = new PdfPCell(new Paragraph(Quarters.values()[m / 3].getName(), FONT_REGULAR_BOLD));
                cell.setColspan(3);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                pdfPTable.addCell(cell);
            }
        });

        IntStream.range(0, 12).forEach(m->{
            PdfPCell cell = new PdfPCell(new Paragraph(Months.values()[m].getName(), FONT_REGULAR_BOLD));
            cell.setRotation(90);
            pdfPTable.addCell(cell);
        });
        return pdfPTable;
    }

    private float [] getWidths(){
        float [] widths = new float[13];
        widths[0] = 300;
        IntStream.range(1, 13).forEach(i -> widths[i] = 35f);
        return widths;
    }

    private void addRow(PdfPTable table, int rowIdx){
        ReportService.ReportItem item = model.getItems().get(rowIdx);
        PdfPCell nameCell = new PdfPCell(new Paragraph(getCornerName(item), FONT_REGULAR));
        nameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(nameCell);

        IntStream.range(0, 12).forEach(colIdx->{
            ReportService.ValuePair pair = item.getValues().get(colIdx);
            PdfPCell cell = new PdfPCell(new Paragraph(pair.getCompleted() + "/" + pair.getFailed(), FONT_REGULAR));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        });

    }

    private String getCornerName(ReportService.ReportItem item){
        StringBuilder buff = new StringBuilder();
        buff.append(model.getReportType() == ReportService.ReportType.POST ? item.getPost(): item.getPredName());
        buff.append(" ");
        buff.append(item.getUserName() == null ? "": item.getUserName());
        return buff.toString();
    }


    @Override
    public byte[] render() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(doc, buffer);
            doc.open();

            PdfPTable table = null;
            int rowIdx = 0;
            while (rowIdx < model.getItems().size())
            {
                h.add(new Paragraph(getCornerName(model.getItems().get(rowIdx)), FONT_REGULAR), getWidths()[0]);
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
