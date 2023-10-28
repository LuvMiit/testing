package org.niias.asrb.kn.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.ToString;
import org.niias.asrb.kn.util.CompletionUtil;

import java.util.Objects;

@ToString
public class CompletionMark {

    private int value;

    public CompletionMark(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompletionMark that = (CompletionMark) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public CompletionMark(int value){
        this.value = value;
    }

    public void setMonth(int month){
        value = CompletionUtil.setMonth(value, month);
    }

    public void unsetMonth(int month){
        value = CompletionUtil.unsetMonth(value, month);
    }

    public boolean isMonthSet(int month){
        return CompletionUtil.isMonthSet(value, month);
    }

    public void toggleMonth(int month){
        this.value = CompletionUtil.unsetMonth(value, month);
    }

    public String get12monthFormat(){
        return CompletionUtil.format12month(value);
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
