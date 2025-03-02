package lk.apollo.mapper;

import lk.apollo.dto.BookDTO;
import lk.apollo.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

//uses = {GenreMapper.class, UserMapper.class}
@Mapper(componentModel = "spring")
public interface BookMapper {

    /**
     * Map Book entity to BookDTO
     *
     * @param book - Book entity
     * @return BookDTO instance
     */
    BookDTO mapToDTO(Book book);

    /**
     * Map BookDTO to Book entity
     *
     * @param bookDTO - BookDTO instance
     * @return Book entity
     */
    Book mapToEntity(BookDTO bookDTO);
}
