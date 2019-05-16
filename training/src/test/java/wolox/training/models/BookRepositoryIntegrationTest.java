package wolox.training.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static wolox.training.TestUtilities.createDefaultBook;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import wolox.training.exceptions.BookNotFoundException;
import wolox.training.repositories.BookRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class BookRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    private Book book;

    @Before
    public void setUp(){
        book =  new Book("J. K. Rowling", "image.png", "Harry Potter and the Philosopher's Stone",
            "-", "Bloomsbury Publishing", "1997", 223, "9780747532743", "Fantasy");

        entityManager.persist(book);
        entityManager.flush();
    }

    //region find book
    @Test
    public void whenFindById_thenReturnBook() throws BookNotFoundException {
        Book bookFound = bookRepository.findById(book.getId()).orElseThrow(BookNotFoundException::new);

        assertThat(bookFound).isEqualTo(book);
    }

    @Test(expected = BookNotFoundException.class)
    public void givenNonExistingId_whenFindById_thenThrowBookNotFound() throws BookNotFoundException {
        Book bookFound = bookRepository.findById(0L).orElseThrow(BookNotFoundException::new);
    }
    //enregion

    //region save book
    @Test
    public void whenSaveBook_thenReturnBook(){
        Book newBook = new Book("J. K. Rowling", "image.png", "Harry Potter and the Chamber of Secrets",
            "-", "Bloomsbury Publishing", "1998", 223, "9780747532743", "Fantasy");

        Book addedBook = bookRepository.save(newBook);

        assertThat(addedBook).isEqualTo(newBook);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullTitle_whenSaveBook_thenThrowIllegalArgument(){
        Book newBook = new Book("J. K. Rowling", "image.png", null,
            "-", "Bloomsbury Publishing", "1998", 223, "9780747532743", "Fantasy");

        Book addedBook = bookRepository.save(newBook);
    }
    //enregion

}
