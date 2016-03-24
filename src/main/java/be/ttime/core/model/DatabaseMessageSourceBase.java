package be.ttime.core.model;

import be.ttime.core.persistence.service.ILocalizedMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.AbstractMessageSource;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

public class DatabaseMessageSourceBase extends AbstractMessageSource {

    @Autowired
    private ILocalizedMessageService localizedMessageService;

    @Value("${locale.default}")
    private String langDefault;

    @Value("${locale.iso3}")
    private boolean iso3;

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {

        return createMessageFormat(getText(code, locale), locale);
    }

    private String getText(String code, Locale locale) {
        Map<String, Map<String, String>> all = localizedMessageService.mapOfTranslation();
        Map<String, String> codeMap = all.get(code);
        String result = null;
        String localeStr = iso3 ? locale.getISO3Language() : locale.getLanguage();
        if (codeMap != null) {
            result = codeMap.get(localeStr);
            if (result == null && !localeStr.equals(langDefault)) {
                result = codeMap.get(langDefault);
            }
        }
        return result != null ? result : code;
    }
}
