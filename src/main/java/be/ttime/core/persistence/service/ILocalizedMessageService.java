package be.ttime.core.persistence.service;


import be.ttime.core.persistence.dao.LocalizedMessageEntity;

import java.util.List;
import java.util.Map;

public interface ILocalizedMessageService {

    List<LocalizedMessageEntity> findAll();

    LocalizedMessageEntity save(LocalizedMessageEntity message);

    List<LocalizedMessageEntity> save(List<LocalizedMessageEntity> messages);

    void delete(Long id);

    Map<String, Map<String, String>> mapOfTranslation();

    LocalizedMessageEntity findByMessageKey(String key);
}
