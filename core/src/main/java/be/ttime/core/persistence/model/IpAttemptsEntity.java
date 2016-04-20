package be.ttime.core.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ip_attempts")
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class IpAttemptsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    private String ip;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
}
