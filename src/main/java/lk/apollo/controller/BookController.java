package lk.apollo.controller;

import lk.apollo.service.BookService;
import lk.apollo.dto.BookDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//TODO: CORS policy
@RestController
@RequestMapping("/books")
public class BookController {

    private BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Get all books
     *
     * @return List of BookDTO instances
     */
    @GetMapping("/all-books")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks();
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
     * Add a book
     * @param bookDTO
     * @return - BookDTO instance
     */
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
    @PutMapping("/update-book")
    public ResponseEntity<BookDTO> updateBook(@RequestBody BookDTO bookDTO) {
        return ResponseEntity.ok(bookService.updateBook(bookDTO));
    }

    /**
     * Delete a book
     * @param id
     * @return - Void
     */
    @DeleteMapping("/delete-book/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}