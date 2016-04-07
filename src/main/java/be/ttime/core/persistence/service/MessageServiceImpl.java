package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.ApplicationConfigEntity;
import be.ttime.core.persistence.model.ApplicationLanguageEntity;
import be.ttime.core.persistence.model.MessageEntity;
import be.ttime.core.persistence.model.MessageTranslationsEntity;
import be.ttime.core.persistence.repository.IMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

@Service
@Transactional
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private IMessageRepository messageRepository;

    @Autowired
    private IApplicationService applicationService;

    @Override
    @CacheEvict(value = "localizedMessage", allEntries = true)
    public MessageEntity save(MessageEntity message) {
        return messageRepository.save(message);
    }

    @Override
    @CacheEvict(value = "localizedMessage", allEntries = true)
    public List<MessageEntity> save(List<MessageEntity> messages) {
        return messageRepository.save(messages);
    }

    @Override
    @CacheEvict(value = "localizedMessage", allEntries = true)
    public void delete(Long id) {
        messageRepository.delete(id);
    }

    @Override
    public List<MessageEntity> findAll() {
        return messageRepository.findAll();
    }

    @Override
    @Cacheable(value = "localizedMessage")
    public Map<String, Map<String, String>> mapOfTranslation() {

        ApplicationConfigEntity appConfig = applicationService.getApplicationConfig();
        Map<String, Locale> langsList = applicationService.getLanguagesMap();
        List<MessageEntity> messages = messageRepository.findAll();

        Map<String, Map<String, String>> result = new HashMap<>();
        for (MessageEntity message : messages) {
            Map<String, String> mapLang = new HashMap<>();

            for(MessageTranslationsEntity t : message.getTranslations()) {
                if(langsList.containsKey(t.getLanguage().getLocale())){
                    mapLang.put(t.getLanguage().getLocale(), t.getValue());
                }
            }

            result.put(message.getMessageKey(), mapLang);
        }
        return result;
    }

    @Override
    public MessageEntity findByMessageKey(String key) {
        return messageRepository.findByMessageKey(key);
    }
}
