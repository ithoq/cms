package be.ttime.core.model.form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
public class EditPageForm {

    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String slug;
    private String seoTitle;
    private String seoTag;
    private String seoDescription;
    private String seoH1;
    private String devIncludeTop;
    private String devIncludeBot;
    private boolean menuItem;
    private boolean enabled;
}
