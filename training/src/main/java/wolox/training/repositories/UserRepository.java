package wolox.training.repositories;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import wolox.training.models.User;

@Component
public interface UserRepository extends CrudRepository<User, Long> {

    public User findFirstByUsername(String username);

    @Query(value="SELECT u FROM Users u WHERE (cast(:fromDate as date) is null OR u.birthdate >= :fromDate) AND (cast(:toDate as date) is null OR u.birthdate <= :toDate) AND (:characters is null OR u.name LIKE %:characters%)")
    public List<User> findByBirthdateBetweenAndNameContains(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, @Param("characters") String characters);


}
