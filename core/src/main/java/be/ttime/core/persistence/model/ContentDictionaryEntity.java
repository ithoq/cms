package be.ttime.core.persistence.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "contentDic")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ContentDictionaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    String name;
    String value;
    String type;

    @ManyToOne
    private ContentEntity content;
}
