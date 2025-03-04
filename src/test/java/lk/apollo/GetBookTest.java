package lk.apollo;

import lk.apollo.Exception.BookNotFoundException;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class GetBookTest {

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
    public void given_book_exists_when_getBookById_then_return_bookDTO() {
        // Given
        Book book = new Book();
        book.setBookId(1L);
        book.setTitle("Book Name");
        book.setDescription("Book Description");
        book.setIsbn("1234567890");
        book.setPublicationDate(LocalDate.now());
        book.setPageCount(100);
        book.setLanguage("English");
        book.setThumbnail("http://www.example.com/image.jpg");
        book.setUrl("http://www.example.com/book.pdf");

        BookDTO bookDTO = new BookDTO();
        bookDTO.setBookId(book.getBookId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setDescription(book.getDescription());
        bookDTO.setIsbn(book.getIsbn());
        bookDTO.setPublicationDate(book.getPublicationDate());
        bookDTO.setPageCount(book.getPageCount());
        bookDTO.setLanguage(book.getLanguage());
        bookDTO.setThumbnail(book.getThumbnail());
        bookDTO.setUrl(book.getUrl());

        // Stub repository and mapper calls
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.mapToDTO(book)).thenReturn(bookDTO);

        // When
        BookDTO response = bookService.getBookById(1L);

        // Then
        assertEquals(response.getBookId(), book.getBookId());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    public void given_book_does_not_exist_when_getBookById_then_throw_bookNotFound_exception() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(1L));

        // Verify that findById() was called once
        verify(bookRepository, times(1)).findById(1L);
    }
}
