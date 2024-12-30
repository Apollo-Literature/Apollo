package lk.apollo.Service;

import lk.apollo.model.Book;
import lk.apollo.repository.BookRepository;

public class BookService {

    BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }
}
