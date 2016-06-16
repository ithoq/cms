package be.ttime.core.controller;

import be.ttime.core.util.CmsUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.text.SimpleDateFormat;
import java.util.Date;

@ControllerAdvice
public class GlobalInitializer {

    @InitBinder
    public void globalBinder(WebDataBinder webDataBinder) {
        SimpleDateFormat df = new SimpleDateFormat(CmsUtils.DATETIME_FORMAT);
        //indulgence
        df.setLenient(true);
        webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(df, true));
    }
}