package org.niias.asrb.kn.converter;

import org.niias.asrb.kn.model.Status;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status status) {
        return status.name();
    }

    @Override
    public Status convertToEntityAttribute(String str) {
        return Status.valueOf(str);
    }
}
