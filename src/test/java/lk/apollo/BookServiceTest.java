package lk.apollo;

import lk.apollo.Exception.BookNotFoundException;
import lk.apollo.Exception.BookNotValidException;
import lk.apollo.Exception.NoBooksFoundException;
import lk.apollo.dto.BookDTO;
import lk.apollo.mapper.BookMapper;
import lk.apollo.model.Book;
import lk.apollo.repository.BookRepository;
import lk.apollo.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());
        List<BookDTO> result = bookService.getAllBooks();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetBookById_BookExists() {
        Book book = new Book();
        book.setBookId(1L);
        BookDTO bookDTO = new BookDTO();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.mapToDTO(book)).thenReturn(bookDTO);
        BookDTO result = bookService.getBookById(1L);
        assertEquals(bookDTO, result);
    }

    @Test
    public void testGetBookById_BookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(1L));
    }

    @Test
    public void testSearchBooks_NoResults() {
        when(bookRepository.findByTitleContainingIgnoreCase("test"))
                .thenReturn(Collections.emptyList());
        assertThrows(NoBooksFoundException.class, () -> bookService.searchBooks("test"));
    }

    @Test
    public void testAddBook_Success() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Test Book");
        bookDTO.setDescription("Description");
        bookDTO.setIsbn("1234567890");
        bookDTO.setPublicationDate(LocalDate.now());
        bookDTO.setPageCount(100);
        bookDTO.setLanguage("English");
        bookDTO.setPrice(BigDecimal.TEN);
        bookDTO.setThumbnail("http://example.com/image.jpg");
        bookDTO.setUrl("http://example.com/book.pdf");

        Book book = new Book();
        when(bookMapper.mapToEntity(bookDTO)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.mapToDTO(book)).thenReturn(bookDTO);

        BookDTO result = bookService.addBook(bookDTO);
        assertEquals(bookDTO, result);
    }

    @Test
    public void testUpdateBook_BookNotFound() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setBookId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(bookDTO));
    }

    @Test
    public void testDeleteBook_BookNotFound() {
        when(bookRepository.existsById(1L)).thenReturn(false);
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L));
    }

    @Test
    public void testValidateBook_InvalidTitle() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle(null);
        assertThrows(BookNotValidException.class, () -> invokeValidateBook(bookDTO));
    }

    private void invokeValidateBook(BookDTO bookDTO) throws Exception {
        // Accessing the private validateBook method using reflection
        try {
            Method method = bookService.getClass().getDeclaredMethod("validateBook", BookDTO.class);
            method.setAccessible(true);
            method.invoke(bookService, bookDTO);
        } catch (InvocationTargetException e) {
            // Extract the actual exception that was thrown by the method
            if (e.getCause() instanceof BookNotValidException) {
                throw (BookNotValidException) e.getCause();
            }
            throw new RuntimeException("Unexpected exception type", e.getCause());
        }
    }
}
