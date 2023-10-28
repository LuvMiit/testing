package org.niias;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ExcelTest {


    @Test
    public void test() throws IOException {
        OutputStream buffer = new FileOutputStream("some.xls");
        HSSFWorkbook book = new HSSFWorkbook();
        HSSFSheet sheet = book.createSheet("some");

        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("some");
        //border(cell, book, false, 14, CellStyle.ALIGN_CENTER)

        row = sheet.createRow(1);
        cell = row.createCell(0);
        //border(cell, book, false, 10, CellStyle.ALIGN_LEFT)
        //cell.setCellValue(globalFilter)

//        sheet.addMergedRegion(new CellRangeAddress(0,0,0, table.getBaseWidth() + table.getTabSize()))
//        sheet.addMergedRegion(new CellRangeAddress(1,1,0, table.getBaseWidth() + table.getTabSize()))


        book.write(buffer);
        book.close();
        buffer.close();


    }


}
