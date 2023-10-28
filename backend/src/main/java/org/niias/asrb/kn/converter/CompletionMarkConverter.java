package org.niias.asrb.kn.converter;

import org.niias.asrb.kn.model.CompletionMark;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CompletionMarkConverter implements AttributeConverter<CompletionMark, Integer> {

    @Override
    public Integer convertToDatabaseColumn(CompletionMark mark) {
        return mark.getValue();
    }

    @Override
    public CompletionMark convertToEntityAttribute(Integer mark) {
        return new CompletionMark(mark);
    }
}
