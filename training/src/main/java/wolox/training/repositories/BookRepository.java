package wolox.training.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import wolox.training.models.Book;

@Component
public interface BookRepository extends CrudRepository<Book, Long> {

    public Optional<Book> findFirstByAuthor(String author);

    public Optional<Book> findByIsbn(String isbn);

    @Query(value = "SELECT b FROM Book b WHERE (:publisher is null OR b.publisher = :publisher) AND (:genre is null OR b.genre=:genre) AND (:year is null OR b.year=:year)")
    public List<Book> findByPublisherAndGenreAndYear(@Param("publisher") String publisher, @Param("genre") String genre, @Param("year") String year);

}
