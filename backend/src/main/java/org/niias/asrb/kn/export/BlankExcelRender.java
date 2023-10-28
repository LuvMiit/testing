package org.niias.asrb.kn.export;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.niias.asrb.kn.model.PredLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

import static org.niias.asrb.kn.export.ColType.*;

public class BlankExcelRender extends AbstractExcelRender<BlankExportModel>{


    public BlankExcelRender(BlankExportModel model){
        super(model);
    }

    public byte[] render() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        renderTable(model.getTable());
        doc.write(buffer);
        return buffer.toByteArray();
    }

    public void renderTable(BlankTable modelTable)  {
        HSSFSheet sheet = doc.createSheet(modelTable.getModelName() + " за " + model.getYear() + " год");
        HSSFRow headerRow = sheet.createRow(0);
        HSSFCell headerCol = headerRow.createCell(0);
        headerCol.setCellValue(model.getDocName() + " за " + model.getYear() + " год");
        sheet.addMergedRegion(new CellRangeAddress(0,0, 0, modelTable.getColSize() - 1));

        headerCol.setCellStyle(calibri14ptBoldCenter);

        HSSFRow header2Row = sheet.createRow(1);
        HSSFCell header2Col = header2Row.createCell(0);
        header2Col.setCellValue(model.getUserPred() + " " + model.getUserStan() + " – " + model.getUserName());
        sheet.addMergedRegion(new CellRangeAddress(1,1, 0, modelTable.getColSize() - 1 - 3));
        sheet.addMergedRegion(new CellRangeAddress(1,1, modelTable.getColSize() - 1 - 2, modelTable.getColSize() - 1));
        header2Col.setCellStyle(calibri14ptNormalCenter);

        StringBuilder buff = new StringBuilder();
        if (Arrays.asList(PredLevel.regional, PredLevel.linear).contains(model.getLevel()))
            buff.append(model.getMain());
        if (Arrays.asList(PredLevel.linear).contains(model.getLevel()))
            buff.append("-").append(model.getReg());

        HSSFCell header2Col2 = header2Row.createCell(modelTable.getColSize() - 1 - 2);
        header2Col2.setCellValue(buff.toString());
        header2Col2.setCellStyle(calibri11ptNormalCenter);

        int topRow = 3;

        IntStream.range(0, modelTable.getRowSize()).forEach(rowIdx -> {
            BlankRow modelRow = modelTable.getRows().get(rowIdx);
            HSSFRow row = sheet.createRow(topRow + rowIdx);

            IntStream.range(0, modelTable.getColSize()).forEach(colIdx -> {
                BlankCol modelCol = modelRow.getCols().get(colIdx);
                HSSFCell col = row.createCell(colIdx);

                col.setCellStyle(calibri11ptNormalCenter);

                if (Arrays.asList(QUARTER_SCORE, YEAR_SCORE).contains(modelCol.getType()))
                {
                    col.setCellStyle(this.baseStyle);
                    if (modelCol.isCompleted())
                        col.setCellStyle(this.completedStyle);
                    if (modelCol.isFailed())
                        col.setCellStyle(this.failedStyle);
                }
                if (Arrays.asList(MONTH).contains(modelCol.getType()))
                {
                    BlankCol prevCol = modelRow.getCols().get(colIdx - 1);
                    BlankCol nextCol = modelRow.getCols().get(colIdx + 1);
                    //boolean borderLeft = prevCol.getType() == MONTH && prevCol.getHMerge() == Merge.RESTART;
                    boolean borderLeft = modelCol.getHMerge() != Merge.CONTINUE;
                    boolean borderRight = modelCol.getHMerge() != Merge.CONTINUE;
                    StyleKey key = new StyleKey(modelCol.isCompleted(), modelCol.isFailed(), borderLeft, borderRight);
                    col.setCellStyle(getCellStyle(key));

                }

                if (Arrays.asList(HEADER).contains(modelCol.getType())){
                    col.setCellStyle(calibri11ptBoldCenterCenterBorder);
                    col.getCellStyle().setWrapText(true);
                }

                if (Arrays.asList(NUMBER).contains(modelCol.getType())){
                    col.setCellStyle(calibri11ptNormalCenterCenterBorder);
                    col.getCellStyle().setWrapText(true);
                }

                if (Arrays.asList(PLAIN).contains(modelCol.getType())){
                    col.setCellStyle(calibri11ptNormalLeftCenterBorder);
                    col.getCellStyle().setWrapText(true);
                }

                if (modelCol.getHMerge() == Merge.RESTART && colIdx < modelTable.getColSize() - 1){
                    int restartColspanIndex = IntStream.range(colIdx + 1, modelTable.getColSize()).boxed().filter(nextIdx-> modelRow.getCols().get(nextIdx).getHMerge() == Merge.RESTART).findFirst().orElse(colIdx);
                    int delta = restartColspanIndex - colIdx;
                    if (delta > 1)
                        sheet.addMergedRegion(new CellRangeAddress(topRow + rowIdx,topRow + rowIdx,colIdx, colIdx + delta - 1));
                }

                if (modelCol.getVMerge() == Merge.RESTART && rowIdx < modelTable.getRowSize() - 1){
                    int restartRowspanIndex = IntStream.range(rowIdx+1, modelTable.getRowSize()).boxed().filter(nextIdx-> modelTable.getRows().get(nextIdx).getCols().get(colIdx).getVMerge() == Merge.RESTART).findFirst().orElse(rowIdx);
                    int delta = restartRowspanIndex - rowIdx;
                    if (delta > 1)
                        sheet.addMergedRegion(new CellRangeAddress(topRow + rowIdx,topRow + rowIdx + delta - 1, colIdx, colIdx));
                }

                col.setCellValue(modelCol.getValue());


            });
        });

        setWidth(sheet, modelTable, model.getQuarters().size());
    }

    private void setWidth(HSSFSheet sheet, BlankTable modelTable,  int qSize){
        sheet.setColumnWidth(0, 4 * 256 + 160);
        IntStream.range(2, modelTable.getColSize()).forEach(i->sheet.setColumnWidth(i, 9 * 256 + 180));
        if (qSize == 4)
            sheet.setColumnWidth(1, 55 * 256 + 170);
        else if (qSize == 3)
            sheet.setColumnWidth(1, 90 * 256 + 190);
        else if (qSize == 2)
            sheet.setColumnWidth(1, 137 * 256 + 190);
        else if (qSize == 1)
            sheet.setColumnWidth(1, 173 * 256 + 190);

    }




}
