package org.niias.asrb.kn.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import org.niias.asrb.kn.util.CompletionUtil;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static org.niias.asrb.kn.model.TemplateNorm.DOC_NAMES;

@Entity
@Inheritance
@DiscriminatorValue("NOR")
@DiscriminatorColumn(name = "DSC")
@Table(schema = "kn", name = "blank_norm" )
public class BlankNorm {

    private CompletionMark customPeriod = new CompletionMark();

    public BlankNorm(){}

    public BlankNorm(TemplateNorm norm){
        this.period = norm.getPeriod();
        customPeriod = new CompletionMark();
        if (norm.getCustomPeriod() != null && !norm.getCustomPeriod().isEmpty()) {
            final char[] chars = norm.getCustomPeriod().toCharArray();
            for (char c : chars) {
                if (c >= '0' && c <= '9') {
                    customPeriod.setMonth(c - '0' + 1);
                }
                else{
                    customPeriod.setMonth(c - 'A' + 11);
                }
            }
        }

        if (norm.getDocs() != null && !norm.getDocs().isEmpty()) {
            try {
                final List<String> list = new ObjectMapper().readValue(norm.getDocs(), List.class);
                final List<String> names = new ArrayList<>();
                for (String item : list) {
                     String name = DOC_NAMES.get(item);
                    if ("akt".equals(item) && norm.getAktAddon() != null) name += norm.getAktAddon();
                    if ("recording".equals(item) && norm.getRecordingAddon() != null) name += norm.getRecordingAddon();
                    if ("other".equals(item) && norm.getOtherAddon() != null) name += norm.getOtherAddon();
                    names.add(name);
                }
                this.docs = Joiner.on(",").join(names);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        this.name = norm.getName();
        this.templateNormDnchId = norm.getDnchId();
    }

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "blank_id")
//    private Blank blank;

    @Id
    @SequenceGenerator(name="kn.blank_norm_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String name;
    private String docs;
    private Long templateNormDnchId;

    protected Period period;

    private CompletionMark completion = new CompletionMark();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="norm_id", referencedColumnName="id", nullable = false, updatable = false)
    @JsonIgnore
    private List<BlankFile> files = new ArrayList<>();

    public int getSetMonthOfPeriod(int month){
        return CompletionUtil.getSetMonthOfPeriod(completion.getValue(), month, period);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public List<BlankFile> getFiles() {
        return files;
    }

    public void setFiles(List<BlankFile> files) {
        this.files = files;
    }

    public CompletionMark getCompletion() {
        return completion;
    }

    public void setCompletion(CompletionMark completion) {
        this.completion = completion;
    }

    public Long getTemplateNormDnchId() {
        return templateNormDnchId;
    }

    public void setTemplateNormDnchId(Long templateNormDnchId) {
        this.templateNormDnchId = templateNormDnchId;
    }

    public CompletionMark getCustomPeriod() {
        return customPeriod;
    }

    public void setCustomPeriod(CompletionMark customPeriod) {
        this.customPeriod = customPeriod;
    }

    public String getDocs() {
        return docs;
    }

    public void setDocs(String docs) {
        this.docs = docs;
    }
}
