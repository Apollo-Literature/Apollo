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

    /**
     * Get book by ID
     * @param id - Long id
     * @return - BookDTO instance
     */
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

    @Transactional
    public Optional<BookDTO> editBook(BookDTO bookDTO) {
        if (bookDTO.getBookId() == null) {
            return Optional.empty(); // No ID provided, so we can't update.
        }

        return bookRepository.findById(bookDTO.getBookId())
                .map(existingBook -> {
                    updateBookFromDTO(existingBook, bookDTO);
                    return bookMapper.mapToDTO(bookRepository.save(existingBook));
                });
    }

    //! Helper Methods
    private void updateBookFromDTO(Book book, BookDTO dto) {
        if (dto.getTitle() != null) book.setTitle(dto.getTitle());
        if (dto.getDescription() != null) book.setDescription(dto.getDescription());
        if (dto.getIsbn() != null) book.setIsbn(dto.getIsbn());
        if (dto.getPublicationDate() != null) book.setPublicationDate(dto.getPublicationDate());
        if (dto.getPageCount() > 0) book.setPageCount(dto.getPageCount());
        if (dto.getLanguage() != null) book.setLanguage(dto.getLanguage());
        if (dto.getPrice() != null) book.setPrice(dto.getPrice());
        if (dto.getThumbnail() != null) book.setThumbnail(dto.getThumbnail());
        if (dto.getUrl() != null) book.setUrl(dto.getUrl());
    }
}
