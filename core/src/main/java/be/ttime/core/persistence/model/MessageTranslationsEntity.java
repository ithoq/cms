package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "translation")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "message"})
public class MessageTranslationsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Column(nullable = false)
    private String value;
    @ManyToOne
    private MessageEntity message;
    @ManyToOne
    private ApplicationLanguageEntity language;

    public MessageTranslationsEntity() {
    }

    public MessageTranslationsEntity(ApplicationLanguageEntity language, String value, MessageEntity message) {
        this.language = language;
        this.value = value;
        this.message = message;
    }
}
