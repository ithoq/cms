package be.ttime.core.persistence.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TagEntity {

    @Id
    @Access(AccessType.PROPERTY)
    private String name;

}
