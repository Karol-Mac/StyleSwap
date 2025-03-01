package com.restapi.styleswap.utils;

import com.restapi.styleswap.utils.enums.ClotheSize;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class SizeConverter implements AttributeConverter<ClotheSize, String> {
    @Override
    public String convertToDatabaseColumn(ClotheSize size) {
        if(size==null) return null;
        return size.getCode();
    }

    @Override
    public ClotheSize convertToEntityAttribute(String code) {
        if(code==null) return null;

        return Stream.of(ClotheSize.values())
                .filter(size -> size.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
