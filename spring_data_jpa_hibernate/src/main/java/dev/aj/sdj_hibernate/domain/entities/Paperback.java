package dev.aj.sdj_hibernate.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;

@Entity
@Table(name = "paperbook", schema = "sys_org")
public class Paperback extends Book {

    private String size;
    private String weight;

    public Paperback() {
        super();
    }

    @Builder
    public Paperback(String title, String isbn, String author, String publisher, String size, String weight) {
        super(title, isbn, author, publisher, "paperback");
        this.size = size;
        this.weight = weight;
    }
}
