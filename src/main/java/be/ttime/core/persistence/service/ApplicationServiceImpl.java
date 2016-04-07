package be.ttime.core.persistence.service;


import be.ttime.core.persistence.model.ApplicationConfigEntity;
import be.ttime.core.persistence.model.ApplicationLanguageEntity;
import be.ttime.core.persistence.repository.IApplicationConfigRepository;
import be.ttime.core.persistence.repository.IApplicationLanguageRepository;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
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
    public ApplicationConfigEntity getApplicationConfig() {
        List<ApplicationConfigEntity> all = applicationConfigRepository.findAll();
        return (all.size() > 0) ? all.get(0) : null;
    }

    @Override
    public List<Locale> getSiteLanguages() {
        return toLocale(applicationLanguageRepository.findByEnabledForPublicTrue());
    }

    @Override
    public Map<String, Locale> getSiteLanguagesMap() {
        return langListToLocaleMap((applicationLanguageRepository.findByEnabledForPublicTrue()));
    }

    @Override
    public List<Locale> getAdminlanguages() {
        return toLocale(applicationLanguageRepository.findByEnabledForAdminTrue());
    }

    @Override
    public Map<String, Locale> getAdminlanguagesMap() {
        return langListToLocaleMap(applicationLanguageRepository.findByEnabledForAdminTrue());
    }

    @Override
    public List<Locale> getLanguages() {
        return toLocale(applicationLanguageRepository.findByEnabledForPublicTrueOrEnabledForAdminTrue());
    }

    @Override
    public Map<String, Locale> getLanguagesMap() {
        return langListToLocaleMap(applicationLanguageRepository.findByEnabledForPublicTrueOrEnabledForAdminTrue());
    }

    @Override
    public ApplicationLanguageEntity saveApplicationLanguage(ApplicationLanguageEntity lang) {
        return applicationLanguageRepository.save(lang);
    }

    @Override
    public ApplicationConfigEntity saveApplicationConfig(ApplicationConfigEntity appConfig) {
        return applicationConfigRepository.save(appConfig);
    }

    @Override
    public void deleteApplicationLanguage(ApplicationLanguageEntity lang) {
        applicationLanguageRepository.delete(lang);
    }

    @Override
    public void deleteApplicationLanguage(String locale) {
        applicationLanguageRepository.delete(locale);
    }

    private Map<String, Locale> langListToLocaleMap(List<ApplicationLanguageEntity> list){
        Map<String, Locale> map = new HashMap<>();
        Locale locale;
        for (ApplicationLanguageEntity l : list) {
            locale = LocaleUtils.toLocale(l.getLocale());
            map.put(l.getLocale(), locale);
            map.put(locale.getLanguage(),locale);
        }
        return map;
    }

    private List<Locale> toLocale(List<ApplicationLanguageEntity> list){
        List<Locale> result = new ArrayList<>();
        for (ApplicationLanguageEntity l : list) {
            result.add(LocaleUtils.toLocale(l.getLocale()));
        }
        return result;
    }
}
