package be.ttime.core.persistence.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "category")
@Getter
@Setter
public class CategoryEntity {

    @Id
    @Access(AccessType.PROPERTY)
    private String name;
}
