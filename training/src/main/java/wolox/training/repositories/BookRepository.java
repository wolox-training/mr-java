package wolox.training.repositories;

import java.util.List;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;
import wolox.training.models.Book;

@Component
public interface BookRepository extends Repository<Book, Long> {

    public Book findFirstByAuthor(String author);

}
