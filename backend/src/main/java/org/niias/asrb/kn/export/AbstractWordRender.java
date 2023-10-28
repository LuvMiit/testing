package org.niias.asrb.kn.export;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.math.BigInteger;
import java.util.stream.IntStream;

public abstract class AbstractWordRender<T extends ExportModel> implements Render<T> {

    protected XWPFDocument doc;
    protected T model;
    protected BlankWordRender.HeightCounter hc;

    protected int height11 = 343;
    protected int height14 = 382;

    private static String FONT_FAMILY_TEXT = "Bahnschrift Light Condensed";

    public AbstractWordRender(T model){
        this.model = model;
        this.doc =  new XWPFDocument();
        CTBody body = doc.getDocument().getBody();

        if (!body.isSetSectPr()) {
            body.addNewSectPr();
        }
    }

    //protected void pageInit(String pageOrientation, CTSectPr section, CTPageMar pageMar) {
    protected void pageInit(String pageOrientation) {
        CTSectPr section = doc.getDocument().getBody().getSectPr();
        CTPageMar pageMar = section.addNewPgMar();

        if(!section.isSetPgSz()) {
            section.addNewPgSz();
        }

        //A4 595x842
        if (pageOrientation == "landscape"){
            CTPageSz pageSize = section.getPgSz();
            pageSize.setOrient(STPageOrientation.LANDSCAPE);
            pageSize.setW(BigInteger.valueOf(16840));
            pageSize.setH(BigInteger.valueOf(11900));

            pageMar.setLeft(BigInteger.valueOf(1134L));
            pageMar.setTop(BigInteger.valueOf(850L));
            pageMar.setRight(BigInteger.valueOf(1134L));
            pageMar.setBottom(BigInteger.valueOf(1701L));
            hc = new BlankWordRender.HeightCounter(11900, 850, 1701);
        }else{
            CTPageSz pageSize = section.getPgSz();
            pageSize.setOrient(STPageOrientation.PORTRAIT);
            pageSize.setW(BigInteger.valueOf(11900 ));
            pageSize.setH(BigInteger.valueOf(16840));

            pageMar.setTop(BigInteger.valueOf(1134L));
            pageMar.setBottom(BigInteger.valueOf(1134L));
            pageMar.setLeft(BigInteger.valueOf(1701L));
            pageMar.setRight(BigInteger.valueOf(850L));
            hc = new BlankWordRender.HeightCounter(16840, 1134, 1134);
        }
    }

    public static class HeightCounter{

        public HeightCounter(int pageTotal, int pageTop, int pageBottom){
            this.pageTotal = pageTotal;
            this.pageTop = pageTop;
            this.pageBottom = pageBottom;
            this.total = pageTotal;
        }

        int total;
        int pageTotal;
        int pageTop;
        int pageBottom;
        int page;

        public void nextPage(){
            page++;
            total = pageTop + pageBottom;
        }

        public void add(int s){
            total += s;
        }

        public int getTotal(){
            return total;
        }

        public int getPageTotal() {
            return pageTotal;
        }

        public int getPage() {
            return page;
        }
    }

    protected int lineNum(String str, int colWidth){
        int lines = 1;
        int width = 0;
        String [] words = str.split("\\s+");

        for (String word: words)
        {
            int curWidth = (int) Math.floor((word.length() + 1)* 94.5);
            if (width + curWidth >= colWidth)
            {
                width = curWidth;
                lines++;
            }else
                width += curWidth;

        }

        return lines;
    }

    protected void pageBreak(){
        XWPFParagraph p = this.doc.createParagraph();
        XWPFRun r1 = p.createRun();
        r1.setText(" ");
        r1.addBreak(BreakType.PAGE);
    }

    @Value
    @Builder(toBuilder = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TextStyle{
        boolean bold;
        boolean center;
        boolean right;
        boolean middle;
        boolean vertical;
        boolean nowrap;
        @Builder.Default
        int fontSize = 11;
    }

    protected void fillParagraph(XWPFParagraph p, String text, TextStyle style, boolean count){
        if (text == null)
            text = "";

        p.setAlignment(ParagraphAlignment.LEFT);
        if (style.isCenter())
            p.setAlignment(ParagraphAlignment.CENTER);
        if (style.isRight())
            p.setAlignment(ParagraphAlignment.RIGHT);
        if (style.isMiddle())
            p.setVerticalAlignment(TextAlignment.CENTER);

        p.setSpacingAfter(0);
        p.setSpacingBefore(0);
        p.setSpacingBetween(1);

        if (style.isNowrap())
            p.setWordWrapped(true);

        XWPFRun run = p.createRun();
        String[] lines = text.split("\n");
        run.setText(lines[0]);
        IntStream.range(1, lines.length).forEach(i->{
            run.addBreak();
            run.setText(lines[i]);
        });

        if (style.isBold())
            run.setBold(true);


        if (count && style.fontSize == 14)
            hc.add(lines.length * height14);
        if (count && style.fontSize == 11)
            hc.add(lines.length * height11);

        run.setFontSize(style.fontSize);
        run.setFontFamily(FONT_FAMILY_TEXT);

    }


    protected CTHMerge getHMerge(boolean cont){
        CTHMerge merge = CTHMerge.Factory.newInstance();
        merge.setVal(cont ? STMerge.CONTINUE : STMerge.RESTART);
        return merge;
    }

    protected CTVMerge getVMerge(boolean cont){
        CTVMerge merge = CTVMerge.Factory.newInstance();
        merge.setVal(cont ? STMerge.CONTINUE : STMerge.RESTART);
        return merge;
    }

    @Override
    public String getMime() {
        return "application/msword";
    }

}
