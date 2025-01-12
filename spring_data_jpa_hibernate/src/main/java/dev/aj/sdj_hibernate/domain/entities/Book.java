package dev.aj.sdj_hibernate.domain.entities;

import dev.aj.sdj_hibernate.domain.entities.auditing.AuditMetaData;
import jakarta.persistence.Embedded;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


/**
 * @apiNote <b> <i>Used when you don't want any table for this class, but want to use its attributes in subclasses </b></i>
 * <p>
 * Abstract base class representing a Book entity.
 *
 * This class serves as a mapped superclass in the JPA hierarchy and is intended
 * to be extended by specific types of books (e.g., Ebook, Paperbook).
 * It contains fields and mappings common to all book entities.
 *
 * Fields:
 * - id: Unique identifier for the book entity, auto-generated using the identity strategy.
 * - title: The title or name of the book.
 * - isbn: The ISBN (International Standard Book Number) of the book.
 *
 * The class is marked as abstract to enforce subclass implementation for specific types of books.
 * </p>
 */
@MappedSuperclass
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String isbn;
    private String author;
    private String publisher;
    private String type;

    @Embedded
    private AuditMetaData auditMetaData;

    public Book(String title, String isbn, String author, String publisher, String type) {
                this.title = title;
                this.isbn = isbn;
                this.author = author;
                this.publisher = publisher;
                this.type = type;
                this.auditMetaData = new AuditMetaData();
    }
}
