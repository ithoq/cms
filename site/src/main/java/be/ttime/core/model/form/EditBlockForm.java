package be.ttime.core.model.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
public class EditBlockForm {

    private Long id;
    @NotEmpty
    private String name;
    private String content;
    private boolean enabled = true;
    private boolean dynamic = false;

}