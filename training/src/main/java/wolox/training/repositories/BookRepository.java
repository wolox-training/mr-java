package wolox.training.repositories;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;
import wolox.training.models.Book;

@Component
public interface BookRepository extends CrudRepository<Book, Long>  {

    public Book findFirstByAuthor(String author);

    public void deleteById(Long id);

    public Book getOne(Long id);

    public List<Book> findAll();


}
