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
    private boolean array;

    private String[] inputsValidation;
    private String[] typeSelect;
    private Integer[] order;
    private Long[] inputDataId;

    private String blockContent;

    private String[] inputsName;
}
