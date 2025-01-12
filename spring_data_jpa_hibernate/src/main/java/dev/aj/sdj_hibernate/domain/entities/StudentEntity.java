package dev.aj.sdj_hibernate.domain.entities;

import com.opencsv.bean.CsvIgnore;
import dev.aj.sdj_hibernate.domain.entities.auditing.AuditMetaData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "student", schema = "sys_org")
@Builder
@ToString(exclude = {"guide", "hotel"})
@EntityListeners(AuditingEntityListener.class)
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, name = "enrollment_id")
    private String enrollmentId;

    @ManyToOne(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "guide_id")
    private GuideEntity guide;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinColumn(name = "hotel_id")
    private HotelEntity hotel;

    @CsvIgnore
    @Embedded
    @Builder.Default //If not used, will take the value of 'auditMetaData' as null,
    // needs to be non-null, but empty so the Data Jpa can enter auditing information
    private AuditMetaData auditMetaData = new AuditMetaData();

    public void addGuide(GuideEntity guide) {
        this.guide = guide;
//        guide.getStudentEntities().add(this);
    }

    public void addHotel(HotelEntity hotelEntity) {
        this.hotel = hotelEntity;
    }
}
