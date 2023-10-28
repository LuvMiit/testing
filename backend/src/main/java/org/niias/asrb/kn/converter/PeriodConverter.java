package org.niias.asrb.kn.converter;

import org.niias.asrb.kn.model.Period;
import org.niias.asrb.kn.model.Status;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PeriodConverter implements AttributeConverter<Period, String> {

    @Override
    public String convertToDatabaseColumn(Period period) {
        return period.name();
    }

    @Override
    public Period convertToEntityAttribute(String str) {
        return Period.valueOf(str);
    }
}
