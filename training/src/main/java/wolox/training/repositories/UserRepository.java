package wolox.training.repositories;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import wolox.training.models.User;

@Component
public interface UserRepository extends CrudRepository {

    public Optional<User> findFirstByUsername(String username);

}
