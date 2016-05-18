package be.ttime.core.model.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminFielsetForm {

    private String fieldsetName;
    private String fieldsetDescription;
    private String blockName;
    private String blockDisplayName;

    private String[] inputsValidation;
    private Integer[] order;
    private Long[] inputDataId;
    private boolean[] inputsArray;

    private String blockContent;

    private String[] inputsName;
}
