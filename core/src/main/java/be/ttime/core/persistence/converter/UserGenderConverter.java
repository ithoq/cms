package be.ttime.core.persistence.converter;

import be.ttime.core.persistence.model.UserEntity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class UserGenderConverter implements AttributeConverter<UserEntity.Gender, String> {


    @Override
    public String convertToDatabaseColumn(UserEntity.Gender attribute) {
        switch (attribute){
            case MALE:
                return "M";
            case FEMALE:
                return "F";
            default:
                throw new IllegalArgumentException("Unknown" + attribute);
        }
    }

    @Override
    public UserEntity.Gender convertToEntityAttribute(String dbData) {
        if(dbData == null )
            return null;
        switch (dbData) {
            case "M" :
                return UserEntity.Gender.MALE;
            case "F" :
                return UserEntity.Gender.FEMALE;
            default:
                throw new IllegalArgumentException("Unknown" + dbData);
        }
    }
}
