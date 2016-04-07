package be.ttime.core.persistence.service;


import be.ttime.core.persistence.model.MessageEntity;

import java.util.List;
import java.util.Map;

public interface IMessageService {

    List<MessageEntity> findAll();

    MessageEntity save(MessageEntity message);

    List<MessageEntity> save(List<MessageEntity> messages);

    void delete(Long id);

    Map<String, Map<String, String>> mapOfTranslation();

    MessageEntity findByMessageKey(String key);
}
