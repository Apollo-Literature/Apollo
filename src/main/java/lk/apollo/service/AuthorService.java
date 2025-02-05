package lk.apollo.service;

import lk.apollo.dto.AuthorDTO;
import lk.apollo.model.Author;
import lk.apollo.repository.AuthorRepository;
import lk.apollo.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Adds author
     * @param authorDTO
     * @return
     */
    public AuthorDTO addAuthor(AuthorDTO authorDTO) {
        Author author = mapToEntity(authorDTO);
        Author savedAuthor = authorRepository.save(author);
        return mapToDTO(savedAuthor);
    }

    private AuthorDTO mapToDTO(Author author) {
        return new AuthorDTO(
                author.getFirstName(),
                author.getLastName(),
                author.getEmail(),
                author.getBirthDate(),
                author.getNationality()
                );
    }

    private Author mapToEntity(AuthorDTO dto) {
        return new Author(
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getBirthDate(),
                dto.getNationality()
        );
    }
}