package wolox.training;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.CoreMatchers.is;
import static wolox.training.TestUtilities.createDefaultBook;
import static wolox.training.TestUtilities.mapToJsonString;

import wolox.training.controllers.BookController;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;


@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookRepository bookRepository;

    private String baseUrl;
    private String bookNotFoundExReason;
    private String nullAttributesExReason;
    private String idMismatchExReason;
    private Book book;
    private Book otherBook;
    private Long nonExistingId;

    @Before
    public void runBefore() throws NoSuchFieldException, IllegalAccessException {
        baseUrl = "/api/books/";

        nonExistingId = 0L;

        bookNotFoundExReason = "Book Not Found";
        nullAttributesExReason = "Received Null Attributes";
        idMismatchExReason = "Book Id Mismatch";

        book = createDefaultBook(1L, "Harry Potter and the Philosopher's Stone");
        otherBook = createDefaultBook(2L, "Harry Potter 2");

        List<Book> books = new ArrayList<Book>();
        books.add(book);
        books.add(otherBook);

        given(bookRepository.findAll()).willReturn(books);
        given(bookRepository.findById(book.getId())).willReturn(java.util.Optional.of(book));
        given(bookRepository.findById(nonExistingId)).willReturn(Optional.empty());
    }

    //region get all books tests

    @Test
    public void whenGetBooks_thenReturnJsonArray() throws Exception{

        mvc.perform(get(baseUrl)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].title", is(book.getTitle())))
            .andExpect(jsonPath("$[1].title", is(otherBook.getTitle())));
    }

    //endregion

    //region get one book tests

    @Test
    public void givenId_whenGetBook_thenReturnJson() throws Exception {

        mvc.perform(get(baseUrl+"{id}", book.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is(book.getTitle())));
    }

    @Test
    public void givenNonExistingId_whenGetBook_thenThrowNotFound() throws Exception {

        mvc.perform(get(baseUrl+"{id}",nonExistingId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(bookNotFoundExReason));
    }

    //endregion

    //region create book tests
    @Test
    public void givenBook_whenCreateBook_thenReturnJson() throws Exception {
        String stringBook = mapToJsonString(book);

        given(bookRepository.save(book)).willReturn(book);

        mvc.perform(post(baseUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringBook))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title", is(book.getTitle())))
            .andExpect(jsonPath("$.subtitle", is(book.getSubtitle())))
            .andExpect(jsonPath("$.author", is(book.getAuthor())))
            .andExpect(jsonPath("$.genre", is(book.getGenre())))
            .andExpect(jsonPath("$.publisher", is(book.getPublisher())))
            .andExpect(jsonPath("$.year", is(book.getYear())))
            .andExpect(jsonPath("$.pages", is(book.getPages())))
            .andExpect(jsonPath("$.isbn", is(book.getIsbn())))
            .andExpect(jsonPath("$.image", is(book.getImage())));
    }

    @Test
    public void givenBookWithNullGender_whenCreateBook_thenReturnJson() throws Exception {
        Book newBook = book;
        newBook.setGenre(null);

        String stringNewBook = mapToJsonString(newBook);

        given(bookRepository.save(newBook)).willReturn(newBook);

        mvc.perform(post(baseUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringNewBook))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title", is(newBook.getTitle())))
            .andExpect(jsonPath("$.subtitle", is(newBook.getSubtitle())))
            .andExpect(jsonPath("$.author", is(newBook.getAuthor())))
            .andExpect(jsonPath("$.genre", is(newBook.getGenre())))
            .andExpect(jsonPath("$.publisher", is(newBook.getPublisher())))
            .andExpect(jsonPath("$.year", is(newBook.getYear())))
            .andExpect(jsonPath("$.pages", is(newBook.getPages())))
            .andExpect(jsonPath("$.isbn", is(newBook.getIsbn())))
            .andExpect(jsonPath("$.image", is(newBook.getImage())));;
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenBookWithEmptyRequiredValues_whenCreateBook_thenThrowNullAttributeException() throws Exception {
        Book newBook = book;
        newBook.setTitle(null);

        String stringNewBook = mapToJsonString(newBook);

        given(bookRepository.save(newBook)).willReturn(newBook);

        mvc.perform(post(baseUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringNewBook))
            .andExpect(status().isBadRequest())
            .andExpect(status().reason(nullAttributesExReason));
    }

    //endregion

    //region update book tests

    @Test
    public void givenId_whenUpdateBook_thenReturnJson() throws Exception{
        Book changedBook = book;
        changedBook.setTitle("new title");

        given(bookRepository.save(changedBook)).willReturn(changedBook);

        String stringChangedBook = mapToJsonString(changedBook);

        mvc.perform(put(baseUrl+"{id}",book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringChangedBook))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is(changedBook.getTitle())))
            .andExpect(jsonPath("$.subtitle", is(changedBook.getSubtitle())))
            .andExpect(jsonPath("$.author", is(changedBook.getAuthor())))
            .andExpect(jsonPath("$.genre", is(changedBook.getGenre())))
            .andExpect(jsonPath("$.publisher", is(changedBook.getPublisher())))
            .andExpect(jsonPath("$.year", is(changedBook.getYear())))
            .andExpect(jsonPath("$.pages", is(changedBook.getPages())))
            .andExpect(jsonPath("$.isbn", is(changedBook.getIsbn())))
            .andExpect(jsonPath("$.image", is(changedBook.getImage())));
    }

    @Test
    public void givenNullGenre_whenUpdateBook_thenReturnJson() throws Exception{
        Book changedBook = book;
        changedBook.setGenre("null");

        given(bookRepository.save(changedBook)).willReturn(changedBook);

        String stringChangedBook = mapToJsonString(changedBook);

        mvc.perform(put(baseUrl+"{id}",book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringChangedBook))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is(changedBook.getTitle())))
            .andExpect(jsonPath("$.subtitle", is(changedBook.getSubtitle())))
            .andExpect(jsonPath("$.author", is(changedBook.getAuthor())))
            .andExpect(jsonPath("$.genre", is(changedBook.getGenre())))
            .andExpect(jsonPath("$.publisher", is(changedBook.getPublisher())))
            .andExpect(jsonPath("$.year", is(changedBook.getYear())))
            .andExpect(jsonPath("$.pages", is(changedBook.getPages())))
            .andExpect(jsonPath("$.isbn", is(changedBook.getIsbn())))
            .andExpect(jsonPath("$.image", is(changedBook.getImage())));
    }

    @Test
    public void givenNonExistingId_whenUpdateBook_thenThrowBookNotFound() throws Exception{
        Book changedBook = createDefaultBook(nonExistingId, "Harry Potter and the Philosopher's Stone");

        given(bookRepository.findById(nonExistingId)).willReturn(Optional.empty());
        given(bookRepository.save(changedBook)).willReturn(changedBook);

        String stringChangedBook = mapToJsonString(changedBook);

        mvc.perform(put(baseUrl+"{id}",nonExistingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringChangedBook))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(bookNotFoundExReason));
    }

    @Test
    public void givenWrongId_whenUpdateBook_thenThrowIdMismatch() throws Exception{
        Book changedBook = createDefaultBook(2L, "Harry Potter and the Philosopher's Stone");

        given(bookRepository.save(changedBook)).willReturn(changedBook);

        String stringChangedBook = mapToJsonString(changedBook);

        mvc.perform(put(baseUrl+"{id}",book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringChangedBook))
            .andExpect(status().isConflict())
            .andExpect(status().reason(idMismatchExReason));
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullAttribute_whenUpdateBook_thenThrowNullArguments() throws Exception{
        Book changedBook = book;
        changedBook.setTitle(null);

        given(bookRepository.save(changedBook)).willReturn(changedBook);

        String stringChangedBook = mapToJsonString(changedBook);

        mvc.perform(put(baseUrl+"{id}",book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringChangedBook))
            .andExpect(status().isBadRequest())
            .andExpect(status().reason(nullAttributesExReason));
    }
    //endregion

    //region delete book tests
    @Test
    public void givenId_deleteBook() throws Exception{
        mvc.perform(delete(baseUrl+"{id}",book.getId()))
            .andExpect(status().isOk());

    }

    @Test
    public void givenNonExistingId_deleteBook_thenThrowNotFound() throws Exception{
        mvc.perform(delete(baseUrl+"{id}", nonExistingId))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(bookNotFoundExReason));
    }
    //endregion



}