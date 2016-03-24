package be.ttime.core.model.form;

import be.ttime.core.persistence.dao.PageEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
public class CreatePageForm {

    @NotEmpty
    private String name;
    private Long parentId = -1L;
    private PageEntity.Type type;
    private Long moduleId;
    private Long templateId;
    private String url;
    private boolean newWindow;
}
