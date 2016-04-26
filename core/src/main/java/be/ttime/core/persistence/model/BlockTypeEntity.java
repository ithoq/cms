package be.ttime.core.persistence.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "block_type")
@Getter
@Setter
public class BlockTypeEntity {

    @Id
    @Access(AccessType.PROPERTY)
    private String name;
}
