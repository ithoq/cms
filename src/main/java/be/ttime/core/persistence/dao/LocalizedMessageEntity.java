package be.ttime.core.persistence.dao;


import lombok.*;

import javax.persistence.*;

@Entity
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "localizedMessage", schema = "cognosco", indexes = {
        @Index(name = "idx_messageKey", columnList = "messageKey", unique = true)})
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "messageKey"})
@AllArgsConstructor
@NoArgsConstructor
public class LocalizedMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Column(nullable = false, unique = true)
    private String messageKey;
    @Column
    private String domain;
    @Column
    private String en;
    @Column
    private String fr;

}
