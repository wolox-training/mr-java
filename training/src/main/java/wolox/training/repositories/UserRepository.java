package wolox.training.repositories;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import wolox.training.models.User;

@Component
public interface UserRepository extends CrudRepository<User, Long> {

    public User findFirstByUsername(String username);

    public List<User> findByBirthdateBetweenAndNameContains(LocalDate sinceDate, LocalDate topDate, String characters);


}
