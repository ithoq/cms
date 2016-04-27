package be.ttime.core.persistence.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "block_type")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class BlockTypeEntity {

    @Id
    @Access(AccessType.PROPERTY)
    private String name;
}
