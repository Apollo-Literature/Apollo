package lk.apollo.mapper;

import lk.apollo.dto.BookDTO;
import lk.apollo.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {GenreMapper.class, UserMapper.class})
public interface BookMapper {

    /**
     * Map Book entity to BookDTO
     *
     * @param book - Book entity
     * @return BookDTO instance
     */
    @Mapping(source = "user", target = "author")
    @Mapping(source = "genres", target = "genres")
    BookDTO mapToDTO(Book book);

    /**
     * Map BookDTO to Book entity
     *
     * @param bookDTO - BookDTO instance
     * @return Book entity
     */
    @Mapping(source = "author", target = "user")
    @Mapping(source = "genres", target = "genres")
    Book mapToEntity(BookDTO bookDTO);
}
