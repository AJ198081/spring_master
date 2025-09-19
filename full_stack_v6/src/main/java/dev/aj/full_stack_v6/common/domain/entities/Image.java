package dev.aj.full_stack_v6.common.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_sequence_generator")
    @SequenceGenerator(name = "image_sequence_generator", sequenceName = "image_sequence", initialValue = 1000, allocationSize = 1)
    @Column(nullable = false, updatable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    private String fileName;
    private String contentType;
    private String downloadUrl;

    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, columnDefinition = "bytea")
    @ToString.Exclude
    @JsonIgnore
    private byte[] contents;

    @ManyToOne(optional = true)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @JsonIgnore
    private Product product;

    @Version
    private Integer version;

    @Builder.Default
    @JsonIgnore
    private AuditMetaData auditMetaData = new AuditMetaData();

    public Image(MultipartFile multipartFile) throws IOException {
        this.contentType = multipartFile.getContentType();
        this.contents = multipartFile.getBytes();
        this.fileName = multipartFile.getResource().getFilename();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Image image)) return false;
        return Objects.equals(getFileName(), image.getFileName())
                && Objects.equals(getContentType(), image.getContentType())
                && Objects.deepEquals(getContents(), image.getContents())
                && Objects.equals(getProduct(), image.getProduct());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getFileName(),
                getContentType(),
                Arrays.hashCode(getContents()),
                getProduct()
        );
    }
}
