package wolox.training.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import wolox.training.models.User;

@Component
public interface UserRepository extends CrudRepository<User, Long> {

    public User findFirstByUsername(String username);

}
