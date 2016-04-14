package be.ttime.core.persistence.service;


import be.ttime.core.persistence.model.ApplicationConfigEntity;
import be.ttime.core.persistence.model.ApplicationLanguageEntity;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface IApplicationService {

    ApplicationConfigEntity getApplicationConfig();

    List<Locale> getSiteLanguages();

    Map<String, Locale> getSiteLanguagesMap();

    List<Locale> getAdminlanguages();

    Map<String, Locale> getAdminlanguagesMap();

    List<Locale> getLanguages();

    Map<String, Locale> getLanguagesMap();

    ApplicationConfigEntity saveApplicationConfig(ApplicationConfigEntity appConfig);

    ApplicationLanguageEntity saveApplicationLanguage(ApplicationLanguageEntity lang);

    void deleteApplicationLanguage(ApplicationLanguageEntity lang);

    void deleteApplicationLanguage(String locale);
}
