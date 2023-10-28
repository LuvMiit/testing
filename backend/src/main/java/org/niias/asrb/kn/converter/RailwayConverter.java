package org.niias.asrb.kn.converter;

import org.niias.asrb.kn.model.Railway;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RailwayConverter implements AttributeConverter<Railway, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Railway railway) {
        return railway.getDorKod();
    }

    @Override
    public Railway convertToEntityAttribute(Integer dorKod) {
        return Railway.valueOf(dorKod);
    }
}
