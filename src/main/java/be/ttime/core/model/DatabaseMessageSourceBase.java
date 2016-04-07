package be.ttime.core.model;

import be.ttime.core.persistence.service.IMessageService;
import be.ttime.core.persistence.service.MessageServiceImpl;
import com.sun.tools.doclint.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class DatabaseMessageSourceBase extends AbstractMessageSource {

    @Autowired
    private IMessageService messageService;

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {

        return createMessageFormat(getText(code, locale), locale);
    }

    private String getText(String code, Locale locale) {
        Map<String, Map<String, String>> all = messageService.mapOfTranslation();
        Map<String, String> codeMap = all.get(code);
        String result = null;
        if (codeMap != null) {
            result = codeMap.get(locale.toString());
            if(result == null){
                result = codeMap.get(locale.getLanguage());
            }
        }
        return result != null ? result : code;
    }
}
