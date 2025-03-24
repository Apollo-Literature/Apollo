package lk.apollo.service;

import lk.apollo.dto.BookDTO;
import lk.apollo.exception.book.BookIdMissingException;
import lk.apollo.exception.book.BookNotFoundException;
import lk.apollo.exception.book.BookNotValidException;
import lk.apollo.exception.book.NoBooksFoundException;
import lk.apollo.mapper.BookMapper;
import lk.apollo.model.Book;
import lk.apollo.repository.BookRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private BookDTO testBookDTO;
    private List<Book> bookList;
    private List<BookDTO> bookDTOList;

    @BeforeEach
    void setUp() {
        // Set up test book
        testBook = new Book();
        testBook.setBookId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setDescription("Test Description");
        testBook.setIsbn("9781234567897");
        testBook.setPublicationDate(LocalDate.of(2020, 1, 1));
        testBook.setPageCount(200);
        testBook.setLanguage("English");
        testBook.setPrice(new BigDecimal("29.99"));
        testBook.setThumbnail("https://example.com/thumbnail.jpg");
        testBook.setUrl("https://example.com/book");

        // Set up test book DTO
        testBookDTO = new BookDTO();
        testBookDTO.setBookId(1L);
        testBookDTO.setTitle("Test Book");
        testBookDTO.setAuthor("Test Author");
        testBookDTO.setDescription("Test Description");
        testBookDTO.setIsbn("9781234567897");
        testBookDTO.setPublicationDate(LocalDate.of(2020, 1, 1));
        testBookDTO.setPageCount(200);
        testBookDTO.setLanguage("English");
        testBookDTO.setPrice(new BigDecimal("29.99"));
        testBookDTO.setThumbnail("https://example.com/thumbnail.jpg");
        testBookDTO.setUrl("https://example.com/book");

        // Set up book lists
        bookList = new ArrayList<>();
        bookList.add(testBook);

        bookDTOList = new ArrayList<>();
        bookDTOList.add(testBookDTO);
    }

    @Test
    void getAllBooks_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(bookList, pageable, bookList.size());
        Page<BookDTO> bookDTOPage = new PageImpl<>(bookDTOList, pageable, bookDTOList.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.mapToDTO(testBook)).thenReturn(testBookDTO);

        // Act
        Page<BookDTO> result = bookService.getAllBooks(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testBookDTO, result.getContent().get(0));

        // Verify
        verify(bookRepository).findAll(pageable);
        verify(bookMapper).mapToDTO(testBook);
    }

    @Test
    void getBookById_Success() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookMapper.mapToDTO(testBook)).thenReturn(testBookDTO);

        // Act
        BookDTO result = bookService.getBookById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testBookDTO, result);

        // Verify
        verify(bookRepository).findById(1L);
        verify(bookMapper).mapToDTO(testBook);
    }

    @Test
    void getBookById_NotFound() {
        // Arrange
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(999L));

        // Verify
        verify(bookRepository).findById(999L);
    }

    @Test
    void searchBooks_Success() {
        // Arrange
        when(bookRepository.findByTitleContainingIgnoreCase("Test")).thenReturn(bookList);
        when(bookMapper.mapToDTO(testBook)).thenReturn(testBookDTO);

        // Act
        List<BookDTO> results = bookService.searchBooks("Test");

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testBookDTO, results.get(0));

        // Verify
        verify(bookRepository).findByTitleContainingIgnoreCase("Test");
        verify(bookMapper).mapToDTO(testBook);
    }

    @Test
    void searchBooks_NoResults() {
        // Arrange
        when(bookRepository.findByTitleContainingIgnoreCase("NonExistent")).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(NoBooksFoundException.class, () -> bookService.searchBooks("NonExistent"));

        // Verify
        verify(bookRepository).findByTitleContainingIgnoreCase("NonExistent");
    }

    @Test
    void addBook_Success() {
        // Arrange
        when(bookMapper.mapToEntity(testBookDTO)).thenReturn(testBook);
        when(bookRepository.save(testBook)).thenReturn(testBook);
        when(bookMapper.mapToDTO(testBook)).thenReturn(testBookDTO);

        // Act
        BookDTO result = bookService.addBook(testBookDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testBookDTO, result);

        // Verify
        verify(bookMapper).mapToEntity(testBookDTO);
        verify(bookRepository).save(testBook);
        verify(bookMapper).mapToDTO(testBook);
    }

    @Test
    void updateBook_Success() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        when(bookMapper.mapToDTO(testBook)).thenReturn(testBookDTO);

        BookDTO updateDTO = new BookDTO();
        updateDTO.setBookId(1L);
        updateDTO.setTitle("Updated Title");
        updateDTO.setAuthor("Updated Author");
        updateDTO.setDescription("Updated Description");
        updateDTO.setIsbn("9781234567897");
        updateDTO.setPublicationDate(LocalDate.of(2020, 1, 1));
        updateDTO.setPageCount(250);
        updateDTO.setLanguage("English");
        updateDTO.setPrice(new BigDecimal("39.99"));
        updateDTO.setThumbnail("https://example.com/thumbnail.jpg");
        updateDTO.setUrl("https://example.com/book");

        // Act
        BookDTO result = bookService.updateBook(updateDTO);

        // Assert
        assertNotNull(result);

        // Verify
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
        verify(bookMapper).mapToDTO(any(Book.class));
    }

    @Test
    void updateBook_BookNotFound() {
        // Arrange
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        BookDTO updateDTO = new BookDTO();
        updateDTO.setBookId(999L);
        updateDTO.setTitle("Updated Title");

        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(updateDTO));

        // Verify
        verify(bookRepository).findById(999L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void updateBook_MissingId() {
        // Arrange
        BookDTO updateDTO = new BookDTO();
        updateDTO.setTitle("Updated Title");

        // Act & Assert
        assertThrows(BookIdMissingException.class, () -> bookService.updateBook(updateDTO));

        // Verify
        verify(bookRepository, never()).findById(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void deleteBook_Success() {
        // Arrange
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        // Act
        bookService.deleteBook(1L);

        // Verify
        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_BookNotFound() {
        // Arrange
        when(bookRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(999L));

        // Verify
        verify(bookRepository).existsById(999L);
        verify(bookRepository, never()).deleteById(any());
    }
}