package be.ttime.core.persistence.converter;
/*
import be.ttime.core.persistence.model.PageBlockEntity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BlockTypeConverter implements AttributeConverter<PageBlockEntity.BlockType, String> {

    @Override
    public String convertToDatabaseColumn(PageBlockEntity.BlockType attribute) {
        switch (attribute) {
            case Content:
                return "C";
            case FieldSet:
                return "F";
            case System:
                return "S";
            case PageTemplate:
                return "T";
            case Navigation:
                return "N";
            default:
                throw new IllegalArgumentException("Unknown" + attribute);
        }
    }

    @Override
    public PageBlockEntity.BlockType convertToEntityAttribute(String dbData) {
        if(dbData == null )
            return null;
        switch (dbData){
            case "C" :
                return PageBlockEntity.BlockType.Content;
            case "F" :
                return PageBlockEntity.BlockType.FieldSet;
            case "S" :
                return PageBlockEntity.BlockType.System;
            case "T" :
                return PageBlockEntity.BlockType.PageTemplate;
            case "N" :
                return PageBlockEntity.BlockType.Navigation;
            default:
                throw new IllegalArgumentException("Unknown" + dbData);
        }

    }
}
*/