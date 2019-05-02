package wolox.training.repositories;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import wolox.training.models.Book;

@Component
public interface UserRepository extends CrudRepository {

    public Optional<Book> findFirstByUsername(String username);

}
