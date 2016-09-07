package be.ttime.core.model.form;

import be.ttime.core.persistence.model.UserEntity;
import be.ttime.core.validator.FieldMatch;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Getter
@Setter
@FieldMatch.List({
        @FieldMatch(first = "password", second = "passwordVerification", message = "error.password.match")
})
public class AdminEditUser {

    private Long id;
    private MultipartFile avatar;
    private String previousFile;
    @NotEmpty
    private String lastName;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String email;
    private Date birthday;
    private UserEntity.Gender gender;
    private String organisation;
    private String street;
    private String city;
    private String zip;
    private String country;
    private boolean enabled;
    private String password;
    private String passwordVerification;
    private String group;
    private String comment;

}
