package org.niias.asrb.kn.export;

import lombok.Value;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.util.HashMap;

public abstract class AbstractExcelRender<T extends ExportModel> implements Render<T>{


    protected T model;
    protected HSSFWorkbook doc;

    protected HSSFColor failedColor;
    protected HSSFColor completedColor;
    protected HSSFColor baseColor;

    protected CellStyle failedStyle;
    protected CellStyle completedStyle;
    protected CellStyle baseStyle;
    protected CellStyle calibri14ptBoldCenter;
    protected CellStyle calibri14ptNormalCenter;

    protected CellStyle calibri11ptNormalCenter;
    protected CellStyle calibri11ptBoldCenter;
    protected CellStyle calibri11ptNormalCenterCenterBorder;
    protected CellStyle calibri11ptBoldCenterCenterBorder;
    protected CellStyle calibri11ptNormalLeftCenterBorder;


    @Value
    static class StyleKey {
        private boolean completed;
        private boolean failed;
        private boolean borderLeft;
        private boolean borderRight;
    }

    private HashMap<StyleKey, CellStyle> cellStyles = new HashMap<>();

    private CellStyle populateKey(StyleKey key){
        CellStyle cellStyle = doc.createCellStyle();

        if (key.failed)
            cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.INDIGO.getIndex());
        else if (key.completed)
            cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.CORAL.getIndex());
        else
            cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LEMON_CHIFFON.getIndex());

        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

        if (key.borderLeft)
        {
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
        }

        if (key.borderRight)
        {
            cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        }

        cellStyles.put(key, cellStyle);
        return cellStyle;
    }

    public CellStyle getCellStyle(StyleKey key){
        if (!cellStyles.containsKey(key))
            cellStyles.put(key, populateKey(key));
        return cellStyles.get(key);
    }


    public AbstractExcelRender(T model){
        this.model = model;
        this.doc = new HSSFWorkbook();
//        this.sheet = doc.createSheet("blank");



        failedColor = setColor(doc, HSSFColor.HSSFColorPredefined.INDIGO, (byte) 230, (byte) 118, (byte) 118);
        completedColor = setColor(doc, HSSFColor.HSSFColorPredefined.CORAL, (byte) 146, (byte) 208, (byte) 80);
        baseColor = setColor(doc, HSSFColor.HSSFColorPredefined.LEMON_CHIFFON, (byte) 255, (byte) 255, (byte) 204);

        failedStyle = doc.createCellStyle();
        failedStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.INDIGO.getIndex());
        failedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        failedStyle.setAlignment(HorizontalAlignment.CENTER);
        failedStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorder(failedStyle);

        completedStyle = doc.createCellStyle();
        completedStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.CORAL.getIndex());
        completedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        completedStyle.setAlignment(HorizontalAlignment.CENTER);
        completedStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorder(completedStyle);

        baseStyle = doc.createCellStyle();
        baseStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LEMON_CHIFFON.getIndex());
        baseStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        baseStyle.setAlignment(HorizontalAlignment.CENTER);
        baseStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorder(baseStyle);

        calibri14ptBoldCenter = doc.createCellStyle();
        calibri14ptBoldCenter.setAlignment(HorizontalAlignment.CENTER);
        {
            HSSFFont headerFont = doc.createFont();
            headerFont.setFontName("Calibri");
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            calibri14ptBoldCenter.setFont(headerFont);
        }

        calibri14ptNormalCenter = doc.createCellStyle();
        calibri14ptNormalCenter.setAlignment(HorizontalAlignment.CENTER);
        {
            HSSFFont headerFont = doc.createFont();
            headerFont.setFontName("Calibri");
            headerFont.setFontHeightInPoints((short) 14);
            calibri14ptNormalCenter.setFont(headerFont);
        }


        calibri11ptNormalCenter = doc.createCellStyle();
        calibri11ptNormalCenter.setAlignment(HorizontalAlignment.CENTER);
        {
            HSSFFont headerFont = doc.createFont();
            headerFont.setFontName("Calibri");
            headerFont.setFontHeightInPoints((short) 11);
            calibri11ptNormalCenter.setFont(headerFont);
        }

        calibri11ptNormalLeftCenterBorder = doc.createCellStyle();
        calibri11ptNormalLeftCenterBorder.setAlignment(HorizontalAlignment.LEFT);
        calibri11ptNormalLeftCenterBorder.setVerticalAlignment(VerticalAlignment.CENTER);
        {
            HSSFFont headerFont = doc.createFont();
            headerFont.setFontName("Calibri");
            headerFont.setFontHeightInPoints((short) 11);
            calibri11ptNormalLeftCenterBorder.setFont(headerFont);
            addBorder(calibri11ptNormalLeftCenterBorder);
        }

        calibri11ptNormalCenterCenterBorder = doc.createCellStyle();
        calibri11ptNormalCenterCenterBorder.setAlignment(HorizontalAlignment.CENTER);
        calibri11ptNormalCenterCenterBorder.setVerticalAlignment(VerticalAlignment.CENTER);
        {
            HSSFFont headerFont = doc.createFont();
            headerFont.setFontName("Calibri");
            headerFont.setFontHeightInPoints((short) 11);
            calibri11ptNormalCenterCenterBorder.setFont(headerFont);
            addBorder(calibri11ptNormalCenterCenterBorder);
        }

        calibri11ptBoldCenter = doc.createCellStyle();
        calibri11ptBoldCenter.setAlignment(HorizontalAlignment.CENTER);
        {
            HSSFFont headerFont = doc.createFont();
            headerFont.setFontName("Calibri");
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            calibri11ptBoldCenter.setFont(headerFont);
        }

        calibri11ptBoldCenterCenterBorder = doc.createCellStyle();
        calibri11ptBoldCenterCenterBorder.setAlignment(HorizontalAlignment.CENTER);
        calibri11ptBoldCenterCenterBorder.setVerticalAlignment(VerticalAlignment.CENTER);
        {
            HSSFFont headerFont = doc.createFont();
            headerFont.setFontName("Calibri");
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            calibri11ptBoldCenterCenterBorder.setFont(headerFont);
            addBorder(calibri11ptBoldCenterCenterBorder);
        }


    }

    private CellStyle addBorder(CellStyle cellStyle){
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        return cellStyle;
    }


    public HSSFColor setColor(HSSFWorkbook workbook, HSSFColor.HSSFColorPredefined predefined,  byte r,byte g, byte b){
        HSSFPalette palette = workbook.getCustomPalette();
        HSSFColor hssfColor = null;
        try {
            hssfColor = palette.findColor(r, g, b);
            if (hssfColor == null ){
                palette.setColorAtIndex(predefined.getIndex(), r, g,b);
                hssfColor = palette.getColor(predefined.getIndex());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hssfColor;
    }


    @Override
    public String getMime() {
        return "application/excel";
    }



}
