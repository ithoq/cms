package be.ttime.core.persistence.model;


import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_attempts")
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserAttemptsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    private String username;
    private int attempts;
    @Temporal(TemporalType.DATE)
    private Date lastModified;
}
