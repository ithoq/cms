package be.ttime.core.persistence.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "company")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class CompanyEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    private String name;
    private String website;
    private String comment;
    private String nace;
    private String legalForm;
    private String street1;
    private String street2;
    private String street3;
    private String city;
    private String zip;
    private String countyCode;

}
