package dev.aj.sdj_hibernate.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;

@Entity
@Table(name = "ebook", schema = "sys_org")
public class Ebook extends Book {

    @Builder
    public Ebook(String title, String isbn, String author, String publisher) {
        super(title, isbn, author, publisher, "ebook");
    }

    public Ebook() {
        super();
    }
}
