package lk.apollo.service;

import lk.apollo.dto.BookDTO;
import lk.apollo.mapper.BookMapper;
import lk.apollo.model.Book;
import lk.apollo.model.Genre;
import lk.apollo.repository.BookRepository;
import lk.apollo.repository.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final BookMapper bookMapper;

    public BookService(GenreRepository genreRepository, BookRepository bookRepository, BookMapper bookMapper) {
        this.genreRepository = genreRepository;
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

    /**
     * Add a book | Steps = BookDTO is passed -> Mapped to the book entity -> saved -> mapped back to BookDTO -> Returned
     *
     * @param bookDTO - BookDTO instance
     * @return BookDTO instance
     */
    @Transactional // if one method fails in saving to the database the whole method rollsback
    public BookDTO addBook(BookDTO bookDTO) {
        // Fetch the genres from the database
        Set<Genre> genres = bookDTO.getGenres().stream()
                .map(genreDTO -> genreRepository.findByName(genreDTO.getName())
                        .orElseThrow(() -> new RuntimeException("GenreType not found")))
                .collect(Collectors.toSet());

        // Map the BookDTO to a Book entity
        Book book = bookMapper.mapToEntity(bookDTO);
        book.setGenres(genres);

        // Save the Book entity
        Book savedBook = bookRepository.save(book);

        // Map back to BookDTO and return
        return bookMapper.mapToDTO(savedBook);
    }
}
