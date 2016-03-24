package be.ttime.core.model.field;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Input {
    @Expose
    private String title;
    @Expose
    private String subtitle;
    @Expose
    private String type;
    @Expose
    private String name;
    @Expose
    private boolean required;
    @Expose
    private String validation;
    @Expose
    private String defaultValue;
    @Expose
    private String hint;
}
