package org.niias.asrb.kn.export;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.niias.asrb.kn.model.Months;
import org.niias.asrb.kn.model.Quarters;
import org.niias.asrb.kn.service.ReportService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.IntStream;

public class ReportExcelRender extends AbstractExcelRender<ReportService.ReportResult> {

    public ReportExcelRender(ReportService.ReportResult model) {
        super(model);
    }

    @Override
    public byte[] render() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        renderTable();
        doc.write(buffer);
        return buffer.toByteArray();
    }

    public void renderTable(){
        HSSFSheet sheet = doc.createSheet("Отчет");

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

        HSSFRow headerRow = sheet.createRow(0);
        HSSFCell headerCol = headerRow.createCell(0);
        headerCol.setCellValue(buff.toString());
        sheet.addMergedRegion(new CellRangeAddress(0,0, 0, 12));
        headerCol.setCellStyle(calibri14ptBoldCenter);


        HSSFRow topRow = sheet.createRow(1);
        HSSFCell cell = topRow.createCell(0);
        cell.setCellStyle(calibri11ptBoldCenterCenterBorder);
        cell.getCellStyle().setWrapText(true);
        cell.setCellValue(this.model.getReportType() == ReportService.ReportType.POST ? "Должность": "Наименование подразделения");
        cell.setCellStyle(calibri11ptBoldCenterCenterBorder);


        IntStream.range(1, 5).forEach(q-> {
            HSSFCell col = topRow.createCell(1 + (q-1)*3);
            col.setCellStyle(calibri11ptBoldCenterCenterBorder);
            col.getCellStyle().setWrapText(true);
            col.setCellValue(Quarters.values()[q-1].getName());

            col = topRow.createCell(1 + (q-1)*3 + 1);
            col.setCellValue("");
            col.setCellStyle(calibri11ptBoldCenterCenterBorder);
            col = topRow.createCell(1 + (q-1)*3 + 2);
            col.setCellStyle(calibri11ptBoldCenterCenterBorder);

            sheet.addMergedRegion(new CellRangeAddress(1 ,1, 1 + (q-1)*3,  q*3));
        });

        HSSFRow bottomRow = sheet.createRow(2);
        cell = bottomRow.createCell(0);
        cell.setCellStyle(calibri11ptBoldCenterCenterBorder);
        cell.getCellStyle().setWrapText(true);

        IntStream.range(1, 13).forEach(i->{
            HSSFCell col = bottomRow.createCell(i);
            col.setCellStyle(calibri11ptBoldCenterCenterBorder);
            col.getCellStyle().setWrapText(true);
            col.setCellValue(Months.values()[i-1].getName());
        });

        sheet.addMergedRegion(new CellRangeAddress(1 ,2, 0,0));

        int topSize = 3;
        IntStream.range(0, this.model.getItems().size()).forEach(rowIdx->{
            ReportService.ReportItem item = this.model.getItems().get(rowIdx);
            HSSFRow row = sheet.createRow(topSize + rowIdx);

            HSSFCell nameRow = row.createCell(0);
            nameRow.setCellStyle(calibri11ptNormalLeftCenterBorder);
            if (model.getReportType() == ReportService.ReportType.POST)
                nameRow.setCellValue(item.getPost() + " " + (item.getUserName() == null ? "": item.getUserName()));
            else
                nameRow.setCellValue(item.getPredName() + " " + (item.getUserName() == null ? "": item.getUserName()));

            IntStream.range(0, 12).forEach(colIdx->{
                ReportService.ValuePair pair = item.getValues().get(colIdx);
                HSSFCell col = row.createCell(colIdx + 1);
                col.setCellStyle(calibri11ptNormalCenterCenterBorder);
                col.setCellValue(pair.getCompleted() + "/" +pair.getFailed());
            });

        });

        sheet.setColumnWidth(0, 60 * 256);
    }

}
