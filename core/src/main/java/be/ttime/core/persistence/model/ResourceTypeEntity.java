package be.ttime.core.persistence.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "resource_type")
@Getter
@Setter
public class ResourceTypeEntity {
    @Id
    @Access(AccessType.PROPERTY)
    private String name;
}
