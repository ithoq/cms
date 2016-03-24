package be.ttime.core.persistence.service;

import be.ttime.core.persistence.dao.LocalizedMessageEntity;
import be.ttime.core.persistence.repository.ILocalizedMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LocalizedMessageServiceImpl implements ILocalizedMessageService {

    @Autowired
    private ILocalizedMessageRepository localizedMessageRepository;

    @Override
    @CacheEvict(value = "localizedMessage", allEntries = true)
    public LocalizedMessageEntity save(LocalizedMessageEntity message) {
        return localizedMessageRepository.save(message);
    }

    @Override
    @CacheEvict(value = "localizedMessage", allEntries = true)
    public List<LocalizedMessageEntity> save(List<LocalizedMessageEntity> messages) {
        return localizedMessageRepository.save(messages);
    }

    @Override
    @CacheEvict(value = "localizedMessage", allEntries = true)
    public void delete(Long id) {
        localizedMessageRepository.delete(id);
    }

    @Override
    public List<LocalizedMessageEntity> findAll() {
        return localizedMessageRepository.findAll();
    }

    @Override
    @Cacheable(value = "localizedMessage")
    public Map<String, Map<String, String>> mapOfTranslation() {
        List<LocalizedMessageEntity> messages = localizedMessageRepository.findAll();
        Field[] fields = LocalizedMessageEntity.class.getDeclaredFields();

        // Get lang list dynamically
        int fieldLength;
        List<Field> langsList = new ArrayList<>();
        for (Field field : fields) {
            fieldLength = field.getName().length();
            if (!field.getName().equals("id") && // except id
                    (fieldLength == 2 || fieldLength == 4)) { // iso 2 or iso 4
                langsList.add(field);
            }
        }

        Map<String, Map<String, String>> result = new HashMap<>();
        for (LocalizedMessageEntity message : messages) {
            Map<String, String> mapLang = new HashMap<>();
            for (Field field : langsList) {
                String langValue;
                try {
                    langValue = (String) field.get(message);
                    mapLang.put(field.getName(), langValue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            result.put(message.getMessageKey(), mapLang);
        }
        return result;
    }

    @Override
    public LocalizedMessageEntity findByMessageKey(String key) {
        return localizedMessageRepository.findByMessageKey(key);
    }
}
