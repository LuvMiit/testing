package org.niias.asrb.kn.model;

import com.google.common.collect.ImmutableMap;

import javax.persistence.Embeddable;
import java.util.Map;

@Embeddable
public class TemplateNorm {
    public static Map<String, String> DOC_NAMES = ImmutableMap.<String, String>builder()
            .put("protocol", "протокол")
            .put("order", "приказ")
            .put("docs", "комплект документов")
            .put("akt", "акт ")
            .put("errand", "поручение")
            .put("telegram", "телеграмма")
            .put("help", "справка")
            .put("plan", "план")
            .put("event", "мероприятие")
            .put("technicalConclusion", "техническое заключение")
            .put("report", "отчёт")
            .put("document", "документ")
            .put("analysis", "анализ")
            .put("recording", "запись ")
            .put("stopNote", "докладная записка")
            .put("analytical", "аналитический материал")
            .put("command", "распоряжение")
            .put("roadMap", "дорожная карта")
            .put("calculation", "расчёт целевого показателя")
            .put("other", "")
            .build();


    private String name;
    private Long dnchId;
    private Period period;

    private String docs;
    private String aktAddon;
    private String recordingAddon;
    private String otherAddon;
    private String customPeriod;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Long getDnchId() {
        return dnchId;
    }

    public void setDnchId(Long dnchId) {
        this.dnchId = dnchId;
    }

    public String getDocs() {
        return docs;
    }

    public void setDocs(String docs) {
        this.docs = docs;
    }

    public String getAktAddon() {
        return aktAddon;
    }

    public void setAktAddon(String aktAddon) {
        this.aktAddon = aktAddon;
    }

    public String getRecordingAddon() {
        return recordingAddon;
    }

    public void setRecordingAddon(String recordingAddon) {
        this.recordingAddon = recordingAddon;
    }

    public String getOtherAddon() {
        return otherAddon;
    }

    public void setOtherAddon(String otherAddon) {
        this.otherAddon = otherAddon;
    }

    public String getCustomPeriod() {
        return customPeriod;
    }

    public void setCustomPeriod(String customPeriod) {
        this.customPeriod = customPeriod;
    }
}
