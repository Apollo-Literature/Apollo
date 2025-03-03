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
    public ResponseEntity<Optional<BookDTO>> getBookById(@PathVariable Long id) {
        Optional<BookDTO> book = bookService.getBookById(id);
        return ResponseEntity.status(HttpStatus.OK).body(book);
    }

    /**
     * Add a book
     * @param bookDTO
     * @return - BookDTO instance
     */
    @PostMapping("/add-book")
    public ResponseEntity<BookDTO> addBook(@RequestBody BookDTO bookDTO) {
        BookDTO newBook = bookService.addBook(bookDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBook);
    }

    /**
     * Update a book
     * @param bookDTO
     * @return - BookDTO instance
     */
    @PutMapping("/update-book")
    public ResponseEntity<BookDTO> updateBook(@RequestBody BookDTO bookDTO) {
        Optional<BookDTO> updatedBookDTO = bookService.updateBook(bookDTO);
        return updatedBookDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Delete a book
     * @param id
     * @return - Void
     */
    @DeleteMapping("/delete-book/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        boolean deleted = bookService.deleteBook(id);
        if (deleted) {
            return ResponseEntity.noContent().build();// 204 No Content typically implies an empty response body.
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found.");
        }
    }
}
