package be.ttime.core.model;

import be.ttime.core.persistence.model.InputDataEntity;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FieldsetTemplateData {

    @Expose private Long id;
    @Expose private String displayName;
    @Expose private String namespace;
    @Expose private List<InputDataEntity> datas;
}
