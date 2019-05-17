package wolox.training.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;
import wolox.training.exceptions.BookNotFoundException;
import wolox.training.models.Book;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class BookRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    private Book book;
    private Book otherBook;
    private Long nonExistingId;

    @Before
    public void setUp(){
        nonExistingId = 0L;

        book =  new Book("J. K. Rowling", "image.png", "Harry Potter and the Philosopher's Stone",
            "-", "Bloomsbury Publishing", "1997", 223, "9780747532743", "Fantasy");

        otherBook =  new Book("J. K. Rowling", "image.png", "Harry Potter and the Chamber of Secrets",
            "-", "Bloomsbury Publishing", "1998", 223, "9780747532743", "Fantasy");


        entityManager.persist(book);
        entityManager.persist(otherBook);
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
        Book bookFound = bookRepository.findById(nonExistingId).orElseThrow(BookNotFoundException::new);
    }
    //enregion

    //region save book
    @Test
    public void whenSaveBook_thenReturnBook(){
        Book newBook = new Book("J. K. Rowling", "image.png", "Harry Potter and the Prisoner of Azkaban",
            "-", "Bloomsbury Publishing", "1999", 223, "9780747532743", "Fantasy");

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

    //region delete book
    @Test
    public void whenDeleteBookById(){
        bookRepository.deleteById(otherBook.getId());
        assertThat(bookRepository.findAll()).doesNotContain(otherBook);
    }

    @Test(expected = BookNotFoundException.class)
    public void givenNonExistingId_whenDeleteBookById_thenThrowBookNotFound() throws BookNotFoundException {
        try{
            bookRepository.deleteById(nonExistingId);
        }catch (EmptyResultDataAccessException ex){
            throw new BookNotFoundException();
        }
    }
    //endregion

}
