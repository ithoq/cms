package be.ttime.core.model.form;

import be.ttime.core.validator.FieldMatch;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

@Getter
@Setter
@FieldMatch.List({
        @FieldMatch(first = "password", second = "confirmPass", message = "error.password.match")
})
public class InstallCmsForm {

    @NotEmpty
    private String sitename;
    @NotEmpty
    private String url;
    @NotEmpty
    private String description;
    @NotEmpty
    private List<String> extraLocale;
    @NotEmpty
    private String firstname;
    @NotEmpty
    private String lastname;
    @NotEmpty
    private String password;
    @NotEmpty
    private String confirmPass;
    @NotEmpty
    private String email;
    @NotEmpty
    private String siteLocale;
    @NotEmpty
    private String adminLocale;

    private boolean langInUrl = false;

    private boolean maintenance = false;
}
