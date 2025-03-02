package lk.apollo.service;

import lk.apollo.dto.BookDTO;
import lk.apollo.mapper.BookMapper;
import lk.apollo.model.Book;
import lk.apollo.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    /**
     * Get all books
     *
     * @return List of BookDTO instances
     */
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::mapToDTO) // Use BookMapper to map to DTO
                .collect(Collectors.toList());
    }

    public Optional<BookDTO> getBookById(Long id) {
        return bookRepository.findById(id).map(bookMapper::mapToDTO);
    }


    /**
     * Add a book | Steps = BookDTO is passed -> Mapped to the book entity -> saved -> mapped back to BookDTO -> Returned
     *
     * @param bookDTO - BookDTO instance
     * @return BookDTO instance
     */
    @Transactional // if one method fails in saving to the database the whole method rollsback
    public BookDTO addBook(BookDTO bookDTO) {
        // Map the BookDTO to a Book entity
        Book book = bookMapper.mapToEntity(bookDTO);
        // Save the Book entity
        Book savedBook = bookRepository.save(book);
        // Map back to BookDTO and return
        return bookMapper.mapToDTO(savedBook);
    }
}
