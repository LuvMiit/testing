package org.niias.asrb.kn.converter;

import org.niias.asrb.kn.model.PredLevel;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PredLevelConverter implements AttributeConverter<PredLevel, String> {

    @Override
    public String convertToDatabaseColumn(PredLevel level) {
        return level.name();
    }

    @Override
    public PredLevel convertToEntityAttribute(String str) {
        return PredLevel.valueOf(str);
    }
}
