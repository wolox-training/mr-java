package wolox.training.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.exceptions.UserNotFoundException;
import wolox.training.models.Book;
import wolox.training.models.User;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User otherUser;

    private Book book;
    private Book otherBook;

    private Long nonExistingId;

    @Before
    public void setUp() throws BookAlreadyOwnedException {
        nonExistingId = 0L;

        user = new User("Nick", "Nick27", LocalDate.of(1999,3,27), "1234");
        otherUser = new User("Jennifer", "Jenny1995", LocalDate.of(1995,7,9), "1234");

        book =  new Book("J. K. Rowling", "image.png", "Harry Potter and the Philosopher's Stone",
            "-", "Bloomsbury Publishing", "1997", 223, "9780747532743", "Fantasy");

        otherBook =  new Book("J. K. Rowling", "image.png", "Harry Potter and the Chamber of Secrets",
            "-", "Bloomsbury Publishing", "1998", 223, "9780747532743", "Fantasy");

        user.addBook(book);
        user.addBook(otherBook);

        otherUser.addBook(book);

        entityManager.persist(user);
        entityManager.persist(otherUser);
        entityManager.flush();
    }

    //region find all users

    @Test
    public void givenPageSize_whenFindAllUsers_thenReturnUsersListWithSize(){
        assertThat(userRepository.findAllUsers(PageRequest.of(0,5))).hasSize(5);
    }
    //endregion


    //region find user by id
    @Test
    public void givenId_whenFindById_thenReturnUser() throws UserNotFoundException {
        User foundUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);

        assertThat(foundUser).isEqualTo(user);
    }

    @Test(expected = UserNotFoundException.class)
    public void givenNonExistingId_whenFindById_thenReturnUser() throws UserNotFoundException {
        User foundUser = userRepository.findById(nonExistingId).orElseThrow(UserNotFoundException::new);
    }
    //endregion

    //region save user
    @Test
    public void whenSaveUser_thenReturnUser(){
        User newUser = new User("Mike", "Newbie", LocalDate.of(1990, 5, 3), "1234");

        User foundUser = userRepository.save(newUser);

        assertThat(foundUser).isEqualTo(newUser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullAttribute_whenSaveUser_thenThrowIllegalArgument(){
        User newUser = new User("Mike", null, LocalDate.of(1990, 5, 3), "1234");

        User foundUser = userRepository.save(newUser);
    }
    //endregion

    //region delete user
    @Test
    public void whenDeleteUserById(){
        userRepository.deleteById(user.getId());

        assertThat(userRepository.findAll()).doesNotContain(user);
    }

    @Test(expected = UserNotFoundException.class)
    public void givenNonExistingId_whenDeleteUserById() throws UserNotFoundException {
        try{
            userRepository.deleteById(nonExistingId);
        }catch (EmptyResultDataAccessException ex){
            throw new UserNotFoundException();
        }
    }
    //endregion

    //region find user by birthdate between dates and name containing characters
    @Test
    public void givenDatesAndCharacters_whenFindByBirthdateBetweenAndNameContains_thenReturnUsers(){
        assertThat(userRepository.findByBirthdateBetweenAndNameContains(LocalDate.of(1950, 05, 05), LocalDate.of(1996, 05, 05), "en", null)).contains(otherUser);
    }

    @Test
    public void givenNullCharacters_whenFindByBirthdateBetweenAndNameContains_thenReturnUsers(){
        assertThat(userRepository.findByBirthdateBetweenAndNameContains(LocalDate.of(1950, 05, 05), LocalDate.of(1996, 05, 05), null, null)).contains(otherUser);
    }

    @Test
    public void givenNullToDate_whenFindByBirthdateBetweenAndNameContains_thenReturnUsers(){
        assertThat(userRepository.findByBirthdateBetweenAndNameContains( LocalDate.of(1990, 05, 05), null, "i", null)).contains(otherUser).contains(user);
    }

    @Test
    public void givenNullFromDate_whenFindByBirthdateBetweenAndNameContains_thenReturnUsers(){
        assertThat(userRepository.findByBirthdateBetweenAndNameContains( null, LocalDate.of(1996, 05, 05), "i", null)).contains(otherUser);
    }

    @Test
    public void givenNullCharactersButPageable_whenFindByBirthdateBetweenAndNameContains_thenReturnUsers(){
        assertThat(userRepository.findByBirthdateBetweenAndNameContains(LocalDate.of(1994, 05, 05), LocalDate.of(1996, 05, 05), null,PageRequest.of(0, 3,
            Sort.by("birthdate")))).hasSize(3);
    }
    //endregion
}
