package lk.apollo.controller;

import lk.apollo.dto.BookDTO;
import lk.apollo.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Get all books
     * @param pageable
     * @return - Page of BookDTO instances
     */
    @GetMapping("/all")
    public ResponseEntity<Page<BookDTO>> getAllBooks(Pageable pageable) {
        Page<BookDTO> books = bookService.getAllBooks(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(books);
    }


    /**
     * Get book by ID
     * @param id - the ID of the book that needs to be retrieved
     * @return - BookDTO instance
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    /**
     * Search books by title
     * @param title
     * @return - List of BookDTO instances
     */
    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam("q") String title) {
        return ResponseEntity.ok(bookService.searchBooks(title));
    }

    /**
     * Add a book
     * @param bookDTO
     * @return - BookDTO instance
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PUBLISHER')")
    @PostMapping("/add-book")
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookService.addBook(bookDTO));
    }

    /**
     * Update a book
     * @param bookDTO
     * @return - BookDTO instance
     */
   @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PUBLISHER')")
    @PutMapping("/update-book")
    public ResponseEntity<BookDTO> updateBook(@RequestBody BookDTO bookDTO) {
        return ResponseEntity.ok(bookService.updateBook(bookDTO));
    }

    /**
     * Delete a book
     * @param id
     * @return - Void
     */
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PUBLISHER')")
    @DeleteMapping("/delete-book/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}