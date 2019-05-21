package wolox.training.repositories;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import wolox.training.models.Book;

@Component
public interface BookRepository extends CrudRepository<Book, Long> {

    public Optional<Book> findFirstByAuthor(String author);

    public Optional<Book> findByIsbn(String isbn);

}
