package be.ttime.core.model;


import be.ttime.core.persistence.model.ContentTemplateFieldsetEntity;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TemplateData {
    @Expose private Long id;
    @Expose private String name;
    @Expose private List<ContentTemplateFieldsetEntity> contentTemplateFieldsetEntities;
}
