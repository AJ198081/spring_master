package dev.aj.kafka.product.domain.entities.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.ZonedDateTime;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AuditMetadata {

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(insertable = false)
    private String lastModifiedBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private ZonedDateTime lastModifiedDate;
}
