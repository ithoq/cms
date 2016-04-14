package be.ttime.core.model.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditPagePositionForm {

    private Long parentId;
    private Long id[];
    private Integer level[];
    private Integer position[];
}

