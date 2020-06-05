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
import org.springframework.data.domain.PageRequest;
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
    private Book anotherBook;
    private Long nonExistingId;

    @Before
    public void setUp(){
        nonExistingId = 0L;

        book =  new Book("J. K. Rowling", "image.png", "Harry Potter and the Philosopher's Stone",
            "-", "Bloomsbury Publishing", "1997", 223, "9780747532743", "Fantasy");

        otherBook =  new Book("J. K. Rowling", "image.png", "Harry Potter and the Chamber of Secrets",
            "-", "Bloomsbury Publishing", "1998", 223, "9780747532743", "Fantasy");

        anotherBook =  new Book("Jorge Luis Borges", "image.png", "The Aleph",
            "-", "Editorial Losada", "1949", 146, "9780307950932", "Short Story");


        entityManager.persist(book);
        entityManager.persist(otherBook);
        entityManager.persist(anotherBook);
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
    //endregion

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
    //endregion

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

    //region find book by publisher, genre and year
    @Test
    public void givenPublisherGenreAndYear_whenFindByPublisherGenreAndYear_thenReturnBooks(){
        assertThat(bookRepository.findByPublisherAndGenreAndYear("Bloomsbury Publishing", "Fantasy", "1998", null)).contains(otherBook).doesNotContain(book);
    }

    @Test
    public void givenNullYear_whenFindByPublisherGenreAndYear_thenReturnBooks(){
        assertThat(bookRepository.findByPublisherAndGenreAndYear("Bloomsbury Publishing", "Fantasy", null, null)).contains(otherBook, book);
    }

    @Test
    public void givenNullPublisher_whenFindByPublisherGenreAndYear_thenReturnBooks(){
        assertThat(bookRepository.findByPublisherAndGenreAndYear(null, "Fantasy", "1998", null)).contains(otherBook);
    }

    @Test
    public void givenNullYearAndPageable_whenFindByPublisherGenreAndYear_thenReturnBooks(){
        assertThat(bookRepository.findByPublisherAndGenreAndYear("Bloomsbury Publishing", "Fantasy", null, PageRequest.of(0,5))).contains(otherBook, book).hasSize(2);
    }
    //endregion

    //region find all books with filters
    @Test
    public void givenAuthorProperty_whenFindAll_thenReturnBooksWithAuthor(){
        assertThat(bookRepository.findAll("J. K. Rowling", null, null, null, null, null, null, null, null, null)).contains(book, otherBook)
            .doesNotContain(anotherBook);
    }

    @Test
    public void givenNoProperties_whenFindAll_thenReturnAllBooks(){
        assertThat(bookRepository.findAll(null, null, null, null, null, null, null, null, null, null)).contains(book, otherBook, anotherBook);
    }

    @Test
    public void givenNoPropertiesButPageable_whenFindAll_thenReturnAllBooks(){
        assertThat(bookRepository.findAll(null, null, null, null, null, null, null, null, null,
            PageRequest.of(0,5))).hasSize(3);
    }
    //endregion

}
