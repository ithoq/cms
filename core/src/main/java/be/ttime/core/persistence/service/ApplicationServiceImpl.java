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
import java.util.stream.Collectors;

@Service(value = "applicationService")
@Transactional
public class ApplicationServiceImpl implements IApplicationService {

    @Autowired
    private IApplicationConfigRepository applicationConfigRepository;

    @Autowired
    private IApplicationLanguageRepository applicationLanguageRepository;

    @Override
    @Cacheable(value = "locale", key = "'getApplicationConfig'")
    public ApplicationConfigEntity getApplicationConfig() {
        List<ApplicationConfigEntity> all = applicationConfigRepository.findAll();
        return (all.size() > 0) ? all.get(0) : null;
    }

    @Override
    @Cacheable(value = "locale", key = "'getSiteLanguages'")
    public List<Locale> getSiteLanguages() {
        return toLocale(applicationLanguageRepository.findByEnabledForPublicTrue());
    }

    @Override
    @Cacheable(value = "locale", key = "'getApplicationLanguagesMap'")
    public Map<String, ApplicationLanguageEntity> getApplicationLanguagesMap() {
        return langListToLangEntityeMap(applicationLanguageRepository.findAll());
    }

    @Override
    @Cacheable(value = "locale", key = "'getSiteLanguagesMap'")
    public Map<String, Locale> getSiteLanguagesMap() {
        return langListToLocaleMap((applicationLanguageRepository.findByEnabledForPublicTrue()));
    }

    @Override
    @Cacheable(value = "locale", key = "'getAdminlanguages'")
    public List<Locale> getAdminlanguages() {
        return toLocale(applicationLanguageRepository.findByEnabledForAdminTrue());
    }

    @Override
    @Cacheable(value = "locale", key = "'getAdminlanguagesMap'")
    public Map<String, Locale> getAdminlanguagesMap() {
        return langListToLocaleMap(applicationLanguageRepository.findByEnabledForAdminTrue());
    }


    @Override
    @Cacheable(value = "locale", key = "'getSiteApplicationLanguageMap'")
    public Map<String, ApplicationLanguageEntity> getSiteApplicationLanguageMap() {
        return langListToLangEntityeMap(applicationLanguageRepository.findByEnabledForAdminTrue());
    }

    @Override
    @Cacheable(value = "locale", key = "'getAdminApplicationLanguageMap'")
    public Map<String, ApplicationLanguageEntity> getAdminApplicationLanguageMap() {
        return langListToLangEntityeMap(applicationLanguageRepository.findByEnabledForPublicTrue());
    }

    @Override
    @Cacheable(value = "locale", key = "'getLanguages'")
    public List<Locale> getLanguages() {
        return toLocale(applicationLanguageRepository.findByEnabledForPublicTrueOrEnabledForAdminTrue());
    }

    @Override
    @Cacheable(value = "locale", key = "'getLanguagesMap'")
    public Map<String, Locale> getLanguagesMap() {
        return langListToLocaleMap(applicationLanguageRepository.findByEnabledForPublicTrueOrEnabledForAdminTrue());
    }

    @Override
    @Cacheable(value = "locale", key = "'getDefaultSiteApplicationLanguage'")
    public ApplicationLanguageEntity getDefaultSiteApplicationLanguage() {
        return getApplicationConfig().getDefaultPublicLang();
    }

    @Override
    @Cacheable(value = "locale", key = "'getDefaultSiteLocale'")
    public Locale getDefaultSiteLocale() {
        return LocaleUtils.toLocale(getDefaultSiteApplicationLanguage().getLocale());
    }

    @Override
    public String getDefaultAdminLang() {
        return getApplicationConfig().getDefaultAdminLang().getLocale();
    }

    /**
     * @return the locale , eg: FR is (isIsoTwoLetter setted) and fr_FR (otherwise)
     */
    @Override
    @Cacheable(value = "locale", key = "'getDefaultSiteLang'")
    public String getDefaultSiteLang() {
        return getDefaultSiteLocale().toString();
    }

    @Override
    @CacheEvict(value = "locale", allEntries = true)
    public ApplicationLanguageEntity saveApplicationLanguage(ApplicationLanguageEntity lang) {
        return applicationLanguageRepository.save(lang);
    }

    @Override
    @CacheEvict(value = "locale", allEntries = true)
    public List<ApplicationLanguageEntity> saveApplicationLanguage(List<ApplicationLanguageEntity> langs) {
        return applicationLanguageRepository.save(langs);
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
        for (ApplicationLanguageEntity l : list) {
            map.put(l.getLocale(), LocaleUtils.toLocale(l.getLocale()));
        }
        return map;
    }

    private Map<String, ApplicationLanguageEntity> langListToLangEntityeMap(List<ApplicationLanguageEntity> list) {
        Map<String, ApplicationLanguageEntity> map = new HashMap<>();
        for (ApplicationLanguageEntity l : list) {
            map.put(l.getLocale(), l);
        }
        return map;
    }

    private List<Locale> toLocale(List<ApplicationLanguageEntity> list) {
        List<Locale> result = list.stream().map(l -> LocaleUtils.toLocale(l.getLocale())).collect(Collectors.toList());
        return result;
    }
}
