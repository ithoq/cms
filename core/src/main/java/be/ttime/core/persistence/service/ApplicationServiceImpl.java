package be.ttime.core.persistence.service;


import be.ttime.core.persistence.model.ApplicationConfigEntity;
import be.ttime.core.persistence.model.ApplicationLanguageEntity;
import be.ttime.core.persistence.repository.IApplicationConfigRepository;
import be.ttime.core.persistence.repository.IApplicationLanguageRepository;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(value = "applicationService")
@Transactional
public class ApplicationServiceImpl implements IApplicationService {

    @Autowired
    private IApplicationConfigRepository applicationConfigRepository;

    @Autowired
    private IApplicationLanguageRepository applicationLanguageRepository;

    @Override
    @Cacheable(value = "locale")
    public ApplicationConfigEntity getApplicationConfig() {
        List<ApplicationConfigEntity> all = applicationConfigRepository.findAll();
        return (all.size() > 0) ? all.get(0) : null;
    }

    @Override
    @Cacheable(value = "locale")
    public List<Locale> getSiteLanguages() {
        return toLocale(applicationLanguageRepository.findByEnabledForPublicTrue());
    }

    @Override
    @Cacheable(value = "locale")
    public Map<String, Locale> getSiteLanguagesMap() {
        return langListToLocaleMap((applicationLanguageRepository.findByEnabledForPublicTrue()));
    }

    @Override
    @Cacheable(value = "locale")
    public List<Locale> getAdminlanguages() {
        return toLocale(applicationLanguageRepository.findByEnabledForAdminTrue());
    }

    @Override
    @Cacheable(value = "locale")
    public Map<String, Locale> getAdminlanguagesMap() {
        return langListToLocaleMap(applicationLanguageRepository.findByEnabledForAdminTrue());
    }


    @Override
    @Cacheable(value = "locale")
    public Map<String, ApplicationLanguageEntity> getSiteApplicationLanguageMap() {
        return langListToLangEntityeMap(applicationLanguageRepository.findByEnabledForAdminTrue());
    }

    @Override
    @Cacheable(value = "locale")
    public Map<String, ApplicationLanguageEntity> getAdminApplicationLanguageMap() {
        return langListToLangEntityeMap(applicationLanguageRepository.findByEnabledForPublicTrue());
    }

    @Override
    @Cacheable(value = "locale")
    public List<Locale> getLanguages() {
        return toLocale(applicationLanguageRepository.findByEnabledForPublicTrueOrEnabledForAdminTrue());
    }

    @Override
    @Cacheable(value = "locale")
    public Map<String, Locale> getLanguagesMap() {
        return langListToLocaleMap(applicationLanguageRepository.findByEnabledForPublicTrueOrEnabledForAdminTrue());
    }

    @Override
    @Cacheable(value = "locale")
    public ApplicationLanguageEntity getDefaultSiteApplicationLanguage() {
        return getApplicationConfig().getDefaultPublicLang();
    }

    @Override
    @Cacheable(value = "locale")
    public Locale getDefaultSiteLocale() {
        return LocaleUtils.toLocale(getDefaultSiteApplicationLanguage().getLocale());
    }

    /**
     * @return the locale , eg: FR is (isIsoTwoLetter setted) and fr_FR (otherwise)
     */
    @Override
    public String getDefaultSiteLang() {
        Locale l = getDefaultSiteLocale();
        return getApplicationConfig().isIsoTwoLetter() ? l.getLanguage() : l.toString();
    }

    @Override
    @CacheEvict(value = "locale", allEntries = true)
    public ApplicationLanguageEntity saveApplicationLanguage(ApplicationLanguageEntity lang) {
        return applicationLanguageRepository.save(lang);
    }

    @Override
    @CacheEvict(value = "locale", allEntries = true)
    public ApplicationConfigEntity saveApplicationConfig(ApplicationConfigEntity appConfig) {
        return applicationConfigRepository.save(appConfig);
    }

    @Override
    @CacheEvict(value = "locale", allEntries = true)
    public void deleteApplicationLanguage(ApplicationLanguageEntity lang) {
        applicationLanguageRepository.delete(lang);
    }

    @Override
    @CacheEvict(value = "locale", allEntries = true)
    public void deleteApplicationLanguage(String locale) {
        applicationLanguageRepository.delete(locale);
    }

    private Map<String, Locale> langListToLocaleMap(List<ApplicationLanguageEntity> list) {
        Map<String, Locale> map = new HashMap<>();
        Locale locale;
        for (ApplicationLanguageEntity l : list) {
            locale = LocaleUtils.toLocale(l.getLocale());
            map.put(l.getLocale(), locale);
            map.put(locale.getLanguage(), locale);
        }
        return map;
    }

    private Map<String, ApplicationLanguageEntity> langListToLangEntityeMap(List<ApplicationLanguageEntity> list) {
        Map<String, ApplicationLanguageEntity> map = new HashMap<>();
        Locale locale;
        for (ApplicationLanguageEntity l : list) {
            locale = LocaleUtils.toLocale(l.getLocale());
            map.put(l.getLocale(), l);
            map.put(locale.getLanguage(), l);
        }
        return map;
    }

    private List<Locale> toLocale(List<ApplicationLanguageEntity> list) {
        List<Locale> result = new ArrayList<>();
        for (ApplicationLanguageEntity l : list) {
            result.add(LocaleUtils.toLocale(l.getLocale()));
        }
        return result;
    }
}
