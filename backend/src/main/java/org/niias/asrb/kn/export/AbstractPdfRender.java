package org.niias.asrb.kn.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public abstract class AbstractPdfRender<T extends ExportModel> implements Render<T>{

    protected T model;
    protected Document doc;
    protected BlankPdfRender.Heighter h;

    protected static final BaseColor failed = new BaseColor(230, 118, 118);
    protected static final BaseColor completed = new BaseColor(146, 208, 80);
    protected static final BaseColor baseColor = new BaseColor(255, 255, 204);


    protected BaseFont FONT_BASE;
    protected Font FONT_HEADER;
    protected Font FONT_REGULAR;
    protected Font FONT_REGULAR_BOLD;

    public AbstractPdfRender(T model, String orient){
        this.model = model;

        if (orient.equals("landscape")){
            this.doc = new Document(new Rectangle(
                    0,
                    0,
                    PageSize.A4.getHeight(), PageSize.A4.getWidth()));
            doc.setMargins(60, 60, 45, 90);
            this.h = new BlankPdfRender.Heighter(PageSize.A4.getWidth(), PageSize.A4.getHeight(), 45, 90, 60, 60);

        }else{
            this.doc = new Document(new Rectangle(
                    0,
                    0,
                    PageSize.A4.getWidth(), PageSize.A4.getHeight()));

            doc.setMargins(90, 45, 60, 60);
            this.h = new BlankPdfRender.Heighter(PageSize.A4.getHeight(), PageSize.A4.getWidth(), 45, 90, 90, 45);
        }

        byte[] a = new byte[371380];
        try {
            new DataInputStream(this.getClass().getResource("/font/bahnschrift.ttf").openStream()).readFully(a);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FONT_BASE = BaseFont.createFont("bahnschrift.ttf", BaseFont.IDENTITY_H, true, true, a, null);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FONT_REGULAR = new Font(FONT_BASE, 11, Font.NORMAL, new BaseColor(0,0,0));
        FONT_REGULAR_BOLD = new Font(FONT_BASE, 11, Font.BOLD, new BaseColor(0,0,0));
        FONT_HEADER = new Font(FONT_BASE, 14, Font.NORMAL, new BaseColor(0,0,0));
    }


    protected byte[] stampPages(byte[] buff, float height) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(new ByteArrayInputStream(buff));
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        int n = reader.getNumberOfPages();

        if (n == 1)
            return buff;
        PdfStamper stamper = new PdfStamper(reader, dest);
        PdfContentByte pagecontent;
        Float pw = new Paragraph("Лист yo из yoyo", FONT_REGULAR).getChunks().stream().map(it->it.getWidthPoint()).reduce((a,b)-> a+b).get();

        for (int i = 0; i < n; ) {
            pagecontent = stamper.getOverContent(++i);
            ColumnText.showTextAligned(pagecontent, Element.ALIGN_LEFT,
                    new Phrase(String.format("Лист %s из %s", i, n), FONT_REGULAR), h.getDocWidth() / 2  - pw/2, h.getPageBottom() / 2, 0.0f);

        }
        stamper.close();
        reader.close();
        return dest.toByteArray();
    }

    @Override
    public String getMime() {
        return "application/pdf";
    }

    public static class Heighter {

        Float total = 0f;
        Float docWidth;
        Float left;
        Float right;

        public Heighter(float docHeight, float docWidth, float pageTop, float pageBottom, float left, float right){
            this.docHeight = docHeight;
            this.docWidth = docWidth;
            this.pageTop = pageTop;
            this.pageBottom = pageBottom;
            this.total = docHeight;
            this.left = left;
            this.right = right;
        }

        float docHeight;
        float pageTop;
        float pageBottom;
        int page;
        float last;

        public void nextPage(){
            page++;
            total = pageTop + pageBottom + last;
        }

        public void add(int s){
            total += s;
        }


        public int getPage() {
            return page;
        }

        public Float getTotal() {
            return total;
        }

        public Float getDocWidth() {
            return docWidth;
        }

        public float getDocHeight() {
            return docHeight;
        }

        public float getPageTop() {
            return pageTop;
        }

        public float getPageBottom() {
            return pageBottom;
        }

        public Float getLeft() {
            return left;
        }

        public Float getRight() {
            return right;
        }

        Paragraph add(Paragraph p){
            return add(p, docWidth);
        }

        Paragraph add(Paragraph p, Float colWidth){
            BaseFont bf = p.getFont().getBaseFont();
            float ascent = bf.getAscentPoint(p.getContent(), p.getFont().getSize());
            float descent = bf.getDescentPoint(p.getContent(), p.getFont().getSize());
            //def lineHeight = ascent - descent
            float lineHeight = p.getLeading();

            int  lineNum = ((Float)( p.getChunks().stream().map(it->it.getWidthPoint()).reduce((a,b)-> a+b).orElse(0f) / colWidth)).intValue() + (p.getChunks().stream().map(it->it.getWidthPoint()).reduce((a,b)-> a+b).orElse(0f) % colWidth == 0 ? 0 : 1);
            last = lineHeight * lineNum + ascent + descent;
            total += last;
            //+  p.getLeading()* lineNum
            return p;
        }

        Float height(){
            return total;
        }
    }

}
