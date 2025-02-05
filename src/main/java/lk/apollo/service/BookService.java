package lk.apollo.service;

import lk.apollo.dto.BookDTO;
import lk.apollo.model.Author;
import lk.apollo.model.Book;
import lk.apollo.model.Genre;
import lk.apollo.repository.AuthorRepository;
import lk.apollo.repository.BookRepository;
import lk.apollo.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    public BookService(GenreRepository genreRepository, AuthorRepository authorRepository, BookRepository bookRepository) {
        this.genreRepository = genreRepository;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Get all books
     *
     * @return List of BookDTO instances
     */
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Add a book | Steps = BookDTO is passed -> Mapped to the book entity -> saved -> mapped back to BookDTO -> Returned
     *
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
     *
     * @param book - Book entity
     * @return BookDTO instance
     */
    private BookDTO mapToDTO(Book book) {
        return new BookDTO(
                book.getTitle(),
                book.getDescription(),
                book.getIsbn(),
                book.getPublicationDate(),
                book.getPageCount(),
                book.getLanguage(),
                book.getPrice(),
                book.getAuthor().getAuthorId(),
                book.getGenres().stream().map(Genre::getGenreId).collect(Collectors.toSet()),
                book.getUrl()
        );
    }

    /**
     * Map BookDTO to Book entity
     *
     * @param bookDTO - BookDTO instance
     * @return Book entity
     */
    private Book mapToEntity(BookDTO bookDTO) {
        Author author = authorRepository.findById(bookDTO.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Author ID"));

        Set<Genre> genres = new HashSet<>();
        if (bookDTO.getGenreIds() != null) {
            for (Long genreId : bookDTO.getGenreIds()) {
                Genre genre = genreRepository.findById( genreId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Genre ID: " + genreId));
                genres.add(genre);
            }
        }

        return new Book(
                bookDTO.getTitle(),
                bookDTO.getDescription(),
                bookDTO.getIsbn(),
                bookDTO.getPublicationDate(),
                bookDTO.getPageCount(),
                bookDTO.getLanguage(),
                bookDTO.getPrice(),
                author,
                genres,
                null, // Reviews should be handled separately
                bookDTO.getUrl()
        );
    }

}