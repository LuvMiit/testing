package org.niias;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

public class PdfTest {

    private static final Float PAGE_HEIGHT = PageSize.A3.getHeight();
    private static final Float PAGE_WIDTH = PageSize.A3.getWidth();
    private static final Float PAGE_MARGIN_RIGHT = 20f;
    private static final Float PAGE_MARGIN_LEFT = 20f;
    private static final Float PAGE_MARGIN_TOP = 20f;
    private static final Float PAGE_MARGIN_BOTTOM = 20f;
    private static final Float TAB_BASE_WIDTH = 70f;
    private static final Float LABEL_BASE_WIDTH = 300f;


    private BaseFont FONT_BASE;
    private Font FONT_HEADER;
    private Font FONT_REGULAR;
    private Font FONT_REGULAR_BOLD;
    private Float PAGE_NUM_LABEL_SIZE;

    private Font FONT_REGULAR_PLUS;
    private Font FONT_REGULAR_MINUS;
    private Font FONT_REGULAR_NORM;
    private Font FONT_BOOKLET_LARGER;
    private Font FONT_BOOKLET_REGULAR;
    private Font FONT_BOOKLET_REGULAR_ITALIC;

    {
        byte[] a = new byte[24632];
        try {
            new DataInputStream(this.getClass().getResource("/font/helvetica.ttf").openStream()).readFully(a);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FONT_BASE = BaseFont.createFont("helvetica.ttf", BaseFont.IDENTITY_H, true, true, a, null);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FONT_REGULAR = new Font(FONT_BASE, 10, Font.NORMAL, new BaseColor(3,3,3));
        FONT_REGULAR_BOLD = new Font(FONT_BASE, 10, Font.BOLD, new BaseColor(3,3,3));
        FONT_HEADER = new Font(FONT_BASE, 14, Font.NORMAL, new BaseColor(3,3,3));
        PAGE_NUM_LABEL_SIZE = new Paragraph("page yo of yoyo", FONT_REGULAR_BOLD).getLeading();

        FONT_REGULAR_PLUS = new Font(FONT_BASE, 10, Font.NORMAL, BaseColor.RED);
        FONT_REGULAR_MINUS = new Font(FONT_BASE, 10, Font.NORMAL, BaseColor.GREEN);
        FONT_REGULAR_NORM = FONT_REGULAR;


        FONT_BOOKLET_LARGER = new Font(FONT_BASE, 20, Font.BOLD, new BaseColor(3,3,3));
        FONT_BOOKLET_REGULAR = new Font(FONT_BASE, 18, Font.BOLD, new BaseColor(3,3,3));
        FONT_BOOKLET_REGULAR_ITALIC = new Font(FONT_BASE, 18, Font.ITALIC, new BaseColor(3,3,3));

    }


    class Heighter {

        Float sum = 0f;
        Float docWidth;

        Heighter(float docWidth){
            this.docWidth = docWidth;
        }

        Paragraph add(Paragraph p){
            BaseFont bf = p.getFont().getBaseFont();
            float ascent = bf.getAscentPoint(p.getContent(), p.getFont().getSize());
            float descent = bf.getDescentPoint(p.getContent(), p.getFont().getSize());
            //def lineHeight = ascent - descent
            float lineHeight = p.getLeading();
            int  lineNum = ((Float)( p.getChunks().stream().map(it->it.getWidthPoint()).reduce((a,b)-> a+b).get() / docWidth)).intValue() + (p.getChunks().stream().map(it->it.getWidthPoint()).reduce((a,b)-> a+b).get() % docWidth == 0 ? 0 : 1);
            sum += lineHeight * lineNum + ascent + descent;

            //+  p.getLeading()* lineNum
            return p;
        }

        Float height(){
            return sum;
        }
    }

    @Test
    public void test() throws IOException {
        java.util.List<Element> pieces = new ArrayList();
        //Po dest = new ByteArrayOutputStream()
        OutputStream buffer = new FileOutputStream("some.pdf");
        Heighter h  = new Heighter(PageSize.A3.getWidth());
        pieces.add(h.add(new Paragraph("Mykonos explore untouched azure water\n" +
                "beaches call now-20% Best price guarantee", FONT_HEADER)));
//        pieces.add(h.add(new Paragraph(globalFilter, FONT_REGULAR)))

        Document doc = new Document(new Rectangle(
                0,
                0,
                PageSize.A3.getWidth(), PageSize.A3.getHeight()));
        doc.setMargins(PAGE_MARGIN_LEFT, PAGE_MARGIN_RIGHT, 0, 0);

        try {
            PdfWriter writer = PdfWriter.getInstance(doc, buffer);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        doc.open();
        pieces.stream().forEach(it-> {
            try {
                doc.add(it);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        });

        doc.close();
        buffer.close();

    }


}
