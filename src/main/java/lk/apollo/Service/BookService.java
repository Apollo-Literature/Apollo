package lk.apollo.Service;

import lk.apollo.dto.BookDTO;
import lk.apollo.model.Book;
import lk.apollo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Get all books
     * @return List of BookDTO instances
     */
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Add a book
     * @param bookDTO - BookDTO instance
     * @return BookDTO instance
     */
    public BookDTO addBook(BookDTO bookDTO) {
        Book book = mapToEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        return mapToDTO(savedBook);
    }

    /**
     * Map Book entity to BookDTO
     * @param book - Book entity
     * @return BookDTO instance
     */
    private BookDTO mapToDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle(book.getTitle());
        bookDTO.setDescription(book.getDescription());
        bookDTO.setIsbn(book.getIsbn());
        bookDTO.setPublicationDate(book.getPublicationDate());
        bookDTO.setPageCount(book.getPageCount());
        bookDTO.setLanguage(book.getLanguage());
        bookDTO.setPrice(book.getPrice());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setGenres(book.getGenres());
        bookDTO.setReviews(book.getReviews());
        bookDTO.setUrl(book.getUrl());
        return bookDTO;
    }

    /**
     * Map BookDTO to Book entity
     * @param bookDTO - BookDTO instance
     * @return Book entity
     */
    private Book mapToEntity(BookDTO bookDTO) {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setDescription(bookDTO.getDescription());
        book.setIsbn(bookDTO.getIsbn());
        book.setPublicationDate(bookDTO.getPublicationDate());
        book.setPageCount(bookDTO.getPageCount());
        book.setLanguage(bookDTO.getLanguage());
        book.setPrice(bookDTO.getPrice());
        book.setAuthor(bookDTO.getAuthor());
        book.setGenres(bookDTO.getGenres());
        book.setReviews(bookDTO.getReviews());
        book.setUrl(bookDTO.getUrl());
        return book;
    }
}