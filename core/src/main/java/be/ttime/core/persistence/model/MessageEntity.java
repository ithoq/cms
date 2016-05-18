package be.ttime.core.persistence.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "message", indexes = {
        @Index(name = "idx_messageKey", columnList = "messageKey", unique = true)})
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "messageKey"})
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Column(nullable = false, unique = true)
    private String messageKey;
    private String domain;
    @OneToMany(mappedBy = "message", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageTranslationsEntity> translations;

    public MessageEntity() {
    }

    public MessageEntity(String messageKey, String domain) {
        this.messageKey = messageKey;
        this.domain = domain;
    }
}
