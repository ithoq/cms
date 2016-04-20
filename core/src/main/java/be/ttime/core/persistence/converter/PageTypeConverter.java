package be.ttime.core.persistence.converter;

import be.ttime.core.persistence.model.PageEntity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PageTypeConverter implements AttributeConverter<PageEntity.Type, String> {

    @Override
    public String convertToDatabaseColumn(PageEntity.Type attribute) {
        switch (attribute){
            case Link:
                return "L";
            case Module:
                return "M";
            case Page:
                return "P";
            default:
                throw new IllegalArgumentException("Unknown" + attribute);
        }
    }

    @Override
    public PageEntity.Type convertToEntityAttribute(String dbData) {
        if(dbData == null )
            return null;
        switch (dbData) {
            case "L" :
                return PageEntity.Type.Link;
            case "H" :
                return PageEntity.Type.Module;
            case "P" :
                return PageEntity.Type.Page;
            default:
                throw new IllegalArgumentException("Unknown" + dbData);
        }
    }
}
