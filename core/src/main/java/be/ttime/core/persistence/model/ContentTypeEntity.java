package be.ttime.core.persistence.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "content_type")
@Getter
@Setter
@EqualsAndHashCode(of = {"name"})
@NoArgsConstructor
@AllArgsConstructor
public class ContentTypeEntity {

    @Id
    @Access(AccessType.PROPERTY)
    private String name;
}
