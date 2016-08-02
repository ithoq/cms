package be.ttime.core.model.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
public class CreatePageForm {

    @NotEmpty
    private String name;
    private Long parentId = -1L;
    private Long templateId;
    private String pageLangId;
}
