package wolox.training.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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


    @Query(value = "SELECT b FROM Book b WHERE (:author is null OR b.author = :author) AND (:genre is null OR b.genre = :genre) AND (:image is null OR b.image = :image) AND "
        + "(:title is null OR b.title = :title) AND (:subtitle is null OR b.subtitle = :subtitle) AND (:publisher is null OR b.publisher = :publisher) AND (:year is null OR b.year = :year) AND "
        + "(:pages is null OR b.pages = :pages) AND (:isbn is null OR b.isbn = :isbn)")
    public List<Book> findAll(@Param("author") String author, @Param("genre") String genre, @Param("image") String image, @Param("title") String title, @Param("subtitle") String subtitle,
        @Param("publisher") String publisher, @Param("year") String year, @Param("pages") Integer pages, @Param("isbn") String isbn, Pageable pageable);

}
