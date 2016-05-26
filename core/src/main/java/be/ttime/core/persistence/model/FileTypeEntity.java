package be.ttime.core.persistence.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "file_type")
@Getter
@Setter
@EqualsAndHashCode(of = {"name"})
@NoArgsConstructor
@AllArgsConstructor
public class FileTypeEntity {

    @Id
    @Access(AccessType.PROPERTY)
    private String name;
}