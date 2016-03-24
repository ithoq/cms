package be.ttime.core.model.field;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Field {

    @Expose
    private String namespace;
    @Expose
    private String name;
    @Expose
    private String description;
    @Expose
    private String blockName;
    @Expose
    private List<Input> inputs = new ArrayList<>();
}