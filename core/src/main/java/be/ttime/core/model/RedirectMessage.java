package be.ttime.core.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fabricecipolla on 9/09/16.
 */
@Getter
@Setter
public class RedirectMessage {

    public static String SUCCESS = "success";
    public static String ERROR = "danger";
    public static String WARNING = "warning";
    public static String INFORMATION = "info";

    private String type;
    private String message;
    private String icon;

    public String getIcon(){
        if(StringUtils.isEmpty(this.icon)){
            if(type.equals(SUCCESS)){
                return "check";
            } else if(type.equals(ERROR)){
                return "exclamation-triangle";
            } else if(type.equals(INFORMATION)){
                return "info-circle";
            } else if(type.equals(WARNING)){
                return "exclamation-circle";
            } else{
                return null;
            }
        } else{
            return icon;
        }
    }
}
