package org.niias.asrb.kn.export;

import org.niias.asrb.kn.service.ReportService;

import java.io.IOException;

public interface Render<T extends ExportModel> {

    byte[] render() throws IOException;

    String getMime();

    static Render getRender(String format, ExportModel model){
        if (BlankExportModel.class.isAssignableFrom(model.getClass()))
            switch (format){
                case "pdf":
                    return new BlankPdfRender((BlankExportModel) model);
                case "word":
                    return new BlankWordRender((BlankExportModel) model);
                case "excel":
                    return new BlankExcelRender((BlankExportModel) model);
            }
        if (ReportService.ReportResult.class.isAssignableFrom(model.getClass()))
            switch (format){
                case "pdf":
                    return new ReportPdfRender((ReportService.ReportResult) model);
                case "word":
                    return new ReportWordRender((ReportService.ReportResult) model);
                case "excel":
                    return new ReportExcelRender((ReportService.ReportResult) model);
            }
        throw new UnsupportedOperationException();
    }
}
