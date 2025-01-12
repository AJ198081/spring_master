package dev.aj.sdj_hibernate.domain.services;

import dev.aj.sdj_hibernate.domain.entities.Book;
import dev.aj.sdj_hibernate.domain.entities.Ebook;
import dev.aj.sdj_hibernate.domain.entities.Paperback;
import dev.aj.sdj_hibernate.domain.repositories.EbookRepository;
import dev.aj.sdj_hibernate.domain.repositories.PaperbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final EbookRepository ebookRepository;
    private final PaperbackRepository paperbackRepository;

    @Transactional
    public Book saveABook(Book book) {
        if (book instanceof Ebook) {
            return ebookRepository.save((Ebook) book);
        } else if (book instanceof Paperback) {
            return paperbackRepository.save((Paperback) book);
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        books.addAll(ebookRepository.findAll());
        books.addAll(paperbackRepository.findAll());
        return books;
    }
}
