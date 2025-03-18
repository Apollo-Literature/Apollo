package lk.apollo;

import lk.apollo.dto.BookDTO;
import lk.apollo.exception.book.BookNotFoundException;
import lk.apollo.exception.book.BookNotValidException;
import lk.apollo.mapper.BookMapper;
import lk.apollo.model.Book;
import lk.apollo.repository.BookRepository;
import lk.apollo.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for the BookService class.
 *
 * This test suite covers:
 * - Retrieval of all books (both when books exist and when none exist).
 * - Retrieval of a single book by ID (success and failure scenarios).
 * - Adding a book with valid data, and ensuring invalid data results in a BookNotValidException.
 * - Updating a book with valid data and handling the case when the book is not found.
 * - Deleting a book with a valid ID and handling deletion when the book does not exist.
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    // Mock repository simulates database interactions.
    @Mock
    private BookRepository bookRepository;

    // Mock mapper simulates conversion between Book entity and BookDTO.
    @Mock
    private BookMapper bookMapper;

    // Inject mocks into the service under test.
    @InjectMocks
    private BookService bookService;

    // Sample Book entity used across tests.
    private Book book;
    // Corresponding BookDTO for mapping tests.
    private BookDTO bookDTO;

    /**
     * Setup executed before each test.
     * Initializes a sample Book entity and a corresponding BookDTO with predefined values.
     */
    @BeforeEach
    void setUp() {
        // Initialize sample Book with test data.
        book = new Book();
        book.setBookId(1L);
        book.setTitle("Test Book");
        book.setDescription("Description");
        book.setIsbn("1234567890");
        book.setPublicationDate(LocalDate.of(2020, 1, 1));
        book.setPageCount(200);
        book.setLanguage("English");
        book.setPrice(new BigDecimal("29.99"));
        book.setThumbnail("http://example.com/image.jpg");
        book.setUrl("http://example.com/book.pdf");

        // Initialize corresponding BookDTO with the same test data.
        bookDTO = new BookDTO();
        bookDTO.setBookId(1L);
        bookDTO.setTitle("Test Book");
        bookDTO.setDescription("Description");
        bookDTO.setIsbn("1234567890");
        bookDTO.setPublicationDate(LocalDate.of(2020, 1, 1));
        bookDTO.setPageCount(200);
        bookDTO.setLanguage("English");
        bookDTO.setPrice(new BigDecimal("29.99"));
        bookDTO.setThumbnail("http://example.com/image.jpg");
        bookDTO.setUrl("http://example.com/book.pdf");
    }

    /**
     * Test for getAllBooks() when books exist.
     * <p>
     * This test simulates the repository returning a list with one Book.
     * The mapper converts the Book to a BookDTO.
     * The assertions ensure the returned list is non-empty, contains exactly one item,
     * and that the title of the returned BookDTO matches the expected value.
     */
    @Test
    void givenBooksExist_whenGetAllBooks_thenReturnBookList() {
        Pageable pageable = PageRequest.of(0, 10);
        // Create a Page containing the sample Book.
        Page<Book> bookPage = new PageImpl<>(Arrays.asList(book));

        // Stub the repository to return the page containing the sample Book.
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);
        // Stub the mapper to convert the Book to a BookDTO.
        when(bookMapper.mapToDTO(book)).thenReturn(bookDTO);

        // Call the service method to retrieve all books.
        Page<BookDTO> resultPage = bookService.getAllBooks(pageable);
        List<BookDTO> books = resultPage.getContent();

        // Assert the list is not empty.
        assertFalse(books.isEmpty(), "The list of books should not be empty");
        // Assert the list contains exactly one book.
        assertEquals(1, books.size(), "The list should contain exactly one book");
        // Assert the title of the book matches the expected value.
        assertEquals("Test Book", books.get(0).getTitle(), "The title of the returned book should match the expected value");
        // Verify that findAll() was called exactly once on the repository with a Pageable.
        verify(bookRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void givenNoBooksExist_whenGetAllBooks_thenReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        // Stub the repository to return an empty page.
        Page<Book> emptyPage = new PageImpl<>(Collections.emptyList());
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // Call the service method.
        Page<BookDTO> resultPage = bookService.getAllBooks(pageable);
        List<BookDTO> books = resultPage.getContent();

        // Assert that the returned list is empty.
        assertTrue(books.isEmpty(), "The list of books should be empty when no books exist");
        // Verify that findAll() was called exactly once.
        verify(bookRepository, times(1)).findAll(any(Pageable.class));
    }

    /**
     * Test for getBookById() when the book exists.
     * <p>
     * This test stubs the repository to return a valid Book and the mapper to convert it to a BookDTO.
     * It asserts that the returned BookDTO has the expected title.
     */
    @Test
    void givenBookExists_whenGetBookById_thenReturnBookDTO() {
        // Stub the repository to return the sample Book for ID 1L.
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        // Stub the mapper to convert the Book to a BookDTO.
        when(bookMapper.mapToDTO(book)).thenReturn(bookDTO);

        // Call getBookById() with ID 1L.
        BookDTO foundBook = bookService.getBookById(1L);

        // Assert that the title of the returned BookDTO is as expected.
        assertEquals("Test Book", foundBook.getTitle(), "The returned book title should match the expected value");
        // Verify that findById() was called once with ID 1L.
        verify(bookRepository, times(1)).findById(1L);
    }

    /**
     * Test for getBookById() when no book exists.
     * <p>
     * This test stubs the repository to return an empty Optional and asserts that getBookById()
     * throws a BookNotFoundException.
     */
    @Test
    void givenBookDoesNotExist_whenGetBookById_thenThrowException() {
        // Stub the repository to return Optional.empty() for ID 1L.
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert that getBookById(1L) throws a BookNotFoundException.
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(1L),
                "Expected BookNotFoundException when book is not found");
        // Verify that findById() was called once with ID 1L.
        verify(bookRepository, times(1)).findById(1L);
    }

    /**
     * Test for addBook() with a valid BookDTO.
     * <p>
     * This test verifies that a valid BookDTO is mapped to a Book entity, saved, and then mapped back to a BookDTO.
     * It asserts that the returned BookDTO matches the expected values.
     */
    @Test
    void givenValidBookDTO_whenAddBook_thenReturnSavedBookDTO() {
        // Stub the mapper to convert the BookDTO to a Book entity.
        when(bookMapper.mapToEntity(bookDTO)).thenReturn(book);
        // Stub the repository to save the Book entity and return it.
        when(bookRepository.save(book)).thenReturn(book);
        // Stub the mapper to convert the saved Book back to a BookDTO.
        when(bookMapper.mapToDTO(book)).thenReturn(bookDTO);

        // Call addBook() with the valid BookDTO.
        BookDTO savedBook = bookService.addBook(bookDTO);

        // Assert that the returned BookDTO is not null.
        assertNotNull(savedBook, "The saved book should not be null");
        // Assert that the title of the returned BookDTO matches the expected value.
        assertEquals("Test Book", savedBook.getTitle(), "The saved book title should match the expected value");
        // Verify that the repository's save() method was called once.
        verify(bookRepository, times(1)).save(book);
    }

    /**
     * Test for addBook() with an invalid BookDTO.
     * <p>
     * This test invalidates the BookDTO by setting an empty title and asserts that
     * addBook() throws a BookNotValidException due to invalid input.
     */
    @Test
    void givenInvalidBookDTO_whenAddBook_thenThrowException() {
        // Invalidate the BookDTO by setting the title to an empty string.
        bookDTO.setTitle("");

        // Assert that calling addBook() with an invalid BookDTO throws BookNotValidException.
        assertThrows(BookNotValidException.class, () -> bookService.addBook(bookDTO),
                "Expected BookNotValidException when adding a book with an empty title");
    }

    /**
     * Test for updateBook() with a valid BookDTO.
     * <p>
     * This test simulates updating a book by stubbing the repository to find the existing Book,
     * then saving the updated Book, and mapping it back to a BookDTO.
     * The assertions verify that the updated BookDTO contains the expected values.
     */
    @Test
    void givenValidBookDTO_whenUpdateBook_thenReturnUpdatedBookDTO() {
        // Stub the repository to return the sample Book for ID 1L.
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        // Stub the repository to return the Book when saving the update.
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        // Stub the mapper to convert the Book to a BookDTO.
        when(bookMapper.mapToDTO(any(Book.class))).thenReturn(bookDTO);

        // Call updateBook() with the valid BookDTO.
        BookDTO updatedBook = bookService.updateBook(bookDTO);

        // Assert that the returned BookDTO is not null.
        assertNotNull(updatedBook, "The updated book should not be null");
        // Assert that the title of the updated BookDTO matches the expected value.
        assertEquals("Test Book", updatedBook.getTitle(), "The updated book title should match the expected value");
        // Verify that findById() was called once with ID 1L.
        verify(bookRepository, times(1)).findById(1L);
        // Verify that save() was called once on the repository.
        verify(bookRepository, times(1)).save(book);
    }

    /**
     * Test for updateBook() when the book does not exist.
     * <p>
     * This test stubs the repository to return an empty Optional for the given ID and asserts that
     * updateBook() throws a BookNotFoundException.
     */
    @Test
    void givenBookDoesNotExist_whenUpdateBook_thenThrowException() {
        // Stub the repository to return Optional.empty() for ID 1L.
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert that updateBook() throws BookNotFoundException when the book is not found.
        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(bookDTO),
                "Expected BookNotFoundException when updating a non-existent book");
        // Verify that findById() was called once with ID 1L.
        verify(bookRepository, times(1)).findById(1L);
    }

    /**
     * Test for deleteBook() when a valid ID is provided.
     * <p>
     * This test simulates a successful deletion by stubbing the repository to indicate that the book exists,
     * and asserts that deleteBook() executes without throwing an exception.
     * It also verifies that deleteById() is called once.
     */
    @Test
    void givenValidId_whenDeleteBook_thenSuccess() {
        // Stub the repository to indicate that the book exists for ID 1L.
        when(bookRepository.existsById(1L)).thenReturn(true);
        // Stub deleteById() to do nothing when called.
        doNothing().when(bookRepository).deleteById(1L);

        // Assert that deleteBook() does not throw an exception.
        assertDoesNotThrow(() -> bookService.deleteBook(1L), "Deletion should succeed when the book exists");
        // Verify that deleteById() was called once with ID 1L.
        verify(bookRepository, times(1)).deleteById(1L);
    }

    /**
     * Test for deleteBook() when an invalid ID is provided.
     * <p>
     * This test stubs the repository to indicate that the book does not exist for the given ID,
     * and asserts that deleteBook() throws a BookNotFoundException.
     */
    @Test
    void givenInvalidId_whenDeleteBook_thenThrowException() {
        // Stub the repository to indicate that the book does not exist for ID 1L.
        when(bookRepository.existsById(1L)).thenReturn(false);

        // Assert that deleteBook() throws BookNotFoundException when the book is not found.
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L),
                "Expected BookNotFoundException when deleting a non-existent book");
        // Verify that existsById() was called once with ID 1L.
        verify(bookRepository, times(1)).existsById(1L);
    }
}
