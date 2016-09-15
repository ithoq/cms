package be.ttime.core.persistence.service;


import be.ttime.core.persistence.model.ApplicationConfigEntity;
import be.ttime.core.persistence.model.ApplicationLanguageEntity;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface IApplicationService {

    ApplicationConfigEntity getApplicationConfig();

    List<Locale> getSiteLanguages();

    Map<String, ApplicationLanguageEntity> getApplicationLanguagesMap();

    Map<String, Locale> getSiteLanguagesMap();

    Map<String, ApplicationLanguageEntity> getSiteApplicationLanguageMap();

    List<Locale> getAdminlanguages();

    Map<String, Locale> getAdminlanguagesMap();

    Map<String, ApplicationLanguageEntity> getAdminApplicationLanguageMap();

    List<Locale> getLanguages();

    Map<String, Locale> getLanguagesMap();

    ApplicationConfigEntity saveApplicationConfig(ApplicationConfigEntity appConfig);

    ApplicationLanguageEntity saveApplicationLanguage(ApplicationLanguageEntity lang);

    List<ApplicationLanguageEntity> saveApplicationLanguage(List<ApplicationLanguageEntity> langs);

    void deleteApplicationLanguage(ApplicationLanguageEntity lang);

    void deleteApplicationLanguage(String locale);

    Locale getDefaultSiteLocale();

    String getDefaultSiteLang();

    String getDefaultAdminLang();

    ApplicationLanguageEntity getDefaultSiteApplicationLanguage();
}
