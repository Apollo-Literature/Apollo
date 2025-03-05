package lk.apollo.service;

import io.micrometer.common.util.StringUtils;
import lk.apollo.Exception.BookIdMissingException;
import lk.apollo.Exception.BookNotFoundException;
import lk.apollo.Exception.BookNotValidException;
import lk.apollo.Exception.NoBooksFoundException;
import lk.apollo.dto.BookDTO;
import lk.apollo.mapper.BookMapper;
import lk.apollo.model.Book;
import lk.apollo.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
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
    public BookDTO getBookById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::mapToDTO)
                .orElseThrow(() -> new BookNotFoundException());
    }

    /**
     * Search books by title
     * @param title
     * @return - List of BookDTO instances
     */
    public List<BookDTO> searchBooks(String title) {
        List<BookDTO> results = bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(bookMapper::mapToDTO)
                .collect(Collectors.toList());
        if (results.isEmpty()) {
            throw new NoBooksFoundException();
        }
        return results;
    }

    /**
     * Add a book | Steps = BookDTO is passed -> Mapped to the book entity -> saved -> mapped back to BookDTO -> Returned
     *
     * @param bookDTO - BookDTO instance
     * @return BookDTO instance
     */
    @Transactional // if one method fails in saving to the database the whole method rollsback
    public BookDTO addBook(BookDTO bookDTO) {
        // Validate the BookDTO
        validateBook(bookDTO);
        // Map the BookDTO to a Book entity
        Book book = bookMapper.mapToEntity(bookDTO);
        // Save the Book entity
        Book savedBook = bookRepository.save(book);
        // Map back to BookDTO and return
        return bookMapper.mapToDTO(savedBook);
    }

    /**
     * Method to update a book
     * @param bookDTO - Updated Information with the ID of the book that needs updating
     * @return - Updated book information
     */
    @Transactional
    public BookDTO updateBook(BookDTO bookDTO) {

        if (bookDTO.getBookId() == null) {
            throw new BookIdMissingException("Book ID is required to update."); // No ID provided, so we can't update.
        }

        Book existingBook = bookRepository.findById(bookDTO.getBookId())
                .orElseThrow(() -> new BookNotFoundException());

        // Validate the BookDTO
        validateBook(bookDTO);

        updateBookFromDTO(existingBook, bookDTO);
        return bookMapper.mapToDTO(bookRepository.save(existingBook));
    }

    /**
     * Method to delete a book
     * @param id
     * @return - Void
     */
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException();
        }
        bookRepository.deleteById(id);
    }

    //! Helper Methods

    /**
     * Method to update a book with the information from the BookDTO
     * @param book - Book entity where the information needs to be updated
     * @param dto - BookDTO instance with updated information
     */
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

    /**
     * Method to validate the BookDTO
     * @param bookDTO
     */
    private void validateBook(BookDTO bookDTO) {
        // Validate title
        if (StringUtils.isBlank(bookDTO.getTitle())) {
            throw new BookNotValidException("Book title is required.");
        }

        // Validate description
        if (StringUtils.isBlank(bookDTO.getDescription())) {
            throw new BookNotValidException("Book description is required.");
        }

        // Validate ISBN
        if (StringUtils.isBlank(bookDTO.getIsbn())) {
            throw new BookNotValidException("Book ISBN is required.");
        }
        if (!isValidISBN(bookDTO.getIsbn())) {
            throw new BookNotValidException("Invalid ISBN format. Must be 10 or 13 digits.");
        }

        // Validate publication date (LocalDate)
        if (bookDTO.getPublicationDate() == null) {
            throw new BookNotValidException("Book publication Date is required.");
        }
        // Catch invalid date input gracefully
        try {
            if (bookDTO.getPublicationDate().isAfter(LocalDate.now())) {
                throw new BookNotValidException("Book publication Date cannot be in the future.");
            }
        } catch (DateTimeParseException e) {
            throw new BookNotValidException("Invalid publication date format. Please provide a valid date (yyyy-MM-dd).");
        }

        // Validate page count
        if (bookDTO.getPageCount() == null || bookDTO.getPageCount() <= 0) {
            throw new BookNotValidException("Book page count must be greater than zero.");
        }

        // Validate language
        if (StringUtils.isBlank(bookDTO.getLanguage())) {
            throw new BookNotValidException("Book language is required.");
        }

        // Validate price
        if (bookDTO.getPrice() == null || bookDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BookNotValidException("Book price must be greater than zero.");
        }

        // Validate thumbnail URL
        if (StringUtils.isBlank(bookDTO.getThumbnail()) || !isValidURL(bookDTO.getThumbnail())) {
            throw new BookNotValidException("Invalid thumbnail URL.");
        }

        // Validate book URL
        if (StringUtils.isBlank(bookDTO.getUrl()) || !isValidURL(bookDTO.getUrl())) {
            throw new BookNotValidException("Invalid book URL.");
        }
    }

    /**
     * Method to validate ISBN
     * @param isbn
     * @return - boolean
     */
    private boolean isValidISBN(String isbn) {
        // Remove hyphens from the ISBN to get only the digits (and possibly an 'X' for ISBN-10)
        String cleaned = isbn.replaceAll("-", "");

        // Check if it's ISBN-10: exactly 10 characters and matches 9 digits followed by a digit or X (case-insensitive)
        if (cleaned.length() == 10) {
            return cleaned.matches("\\d{9}[\\dXx]");
        }

        // Check if it's ISBN-13: exactly 13 digits
        if (cleaned.length() == 13) {
            return cleaned.matches("\\d{13}");
        }

        // Otherwise, it's invalid
        return false;
    }

    /**
     * Method to validate URL
     * @param url
     * @return - boolean
     */
    private boolean isValidURL(String url) {
        return url.matches("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");
    }

}
