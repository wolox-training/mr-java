package wolox.training;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import java.lang.reflect.*;

import wolox.training.controllers.BookController;
import wolox.training.exceptions.BookNotFoundException;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;


@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookRepository bookRepository;

    String bookNotFoundReason;
    Book book;
    Book otherBook;
    Long nonExistingId;

    @Before
    public void runBefore() throws NoSuchFieldException, IllegalAccessException {
        nonExistingId = 0L;

        book = createDefaultBook(1L);
        otherBook = createDefaultBook(2L);

        List<Book> books = new ArrayList<Book>();
        books.add(book);
        books.add(otherBook);

        given(bookRepository.findAll()).willReturn(books);
        given(bookRepository.findById(book.getId())).willReturn(java.util.Optional.of(book));
        given(bookRepository.findById(nonExistingId)).willReturn(Optional.empty());

        bookNotFoundReason = "Book Not Found";
    }


    //region find one book tests

    @Test
    public void givenId_whenGetBook_thenReturnJson() throws Exception {

        mvc.perform(get("/api/books/{id}",book.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is(book.getTitle())));
    }

    @Test//(expected = BookNotFoundException.class)
    public void givenNonExistingId_whenGetBook_thenReturnNotFound() throws Exception {

        mvc.perform(get("/api/books/{id}",nonExistingId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(bookNotFoundReason));
    }

    //endregion

    //region get all books tests

    @Test
    public void whenGetBooks_thenReturnJsonArray() throws Exception{

        mvc.perform(get("/api/books/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].title", is(book.getTitle())))
        .andExpect(jsonPath("$[1].title", is(otherBook.getTitle())));
    }

    //endregion

    //region update book tests

    @Test
    public void givenId_whenUpdateBook_thenReturnJson() throws Exception{
        Book changedBook = book;
        changedBook.setTitle("new title");

        given(bookRepository.save(changedBook)).willReturn(changedBook);

        String stringBook = mapToJsonString(changedBook);

        mvc.perform(put("/api/books/{id}",book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringBook))
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

        given(bookRepository.findById(book.getId())).willReturn(java.util.Optional.of(book));
        given(bookRepository.save(changedBook)).willReturn(changedBook);

        String stringBook = mapToJsonString(changedBook);

        mvc.perform(put("/api/books/{id}",book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringBook))
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
    public void givenNonExistingId_whenUpdateBook_thenReturnBookNotFound() throws Exception{
        Book changedBook = createDefaultBook(nonExistingId);

        given(bookRepository.findById(nonExistingId)).willReturn(Optional.empty());
        given(bookRepository.save(changedBook)).willReturn(changedBook);

        String stringBook = mapToJsonString(changedBook);

        mvc.perform(put("/api/books/{id}",nonExistingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringBook))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(bookNotFoundReason));
    }

    @Test
    public void givenWrongId_whenUpdateBook_thenReturnIdMismatch() throws Exception{
        Book changedBook = createDefaultBook(2L);

        given(bookRepository.save(changedBook)).willReturn(changedBook);

        String stringBook = mapToJsonString(changedBook);

        mvc.perform(put("/api/books/{id}",book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringBook))
            .andExpect(status().isConflict())
            .andExpect(status().reason("Book Id Mismatch"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullAttribute_whenUpdateBook_thenReturnNullArguments() throws Exception{
        Book changedBook = book;
        changedBook.setTitle(null);

        given(bookRepository.save(changedBook)).willReturn(changedBook);

        String stringBook = mapToJsonString(changedBook);

        mvc.perform(put("/api/books/{id}",book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringBook))
            .andExpect(status().isBadRequest())
            .andExpect(status().reason("Received Null Attributes"));
    }
    //endregion

    //region delete book tests
    @Test
    public void givenId_deleteBook() throws Exception{
        mvc.perform(delete("/api/books/{id}",book.getId()))
            .andExpect(status().isOk());

    }

    @Test
    public void givenNonExistingId_deleteBook_thenReturnNotFound() throws Exception{
        mvc.perform(delete("/api/books/{id}", nonExistingId))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(bookNotFoundReason));
    }
    //endregion

    private Book createDefaultBook(Long id) throws NoSuchFieldException, IllegalAccessException{
        Book book = new Book("J. K.", "as", "as", "as", "as", "1999", 25, "142536", "Novel");

        Field fieldId = book.getClass().getDeclaredField("id");
        fieldId.setAccessible(true);
        fieldId.set(book, id);

        return book;
    }

    private String mapToJsonString(Book book) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(book);
    }

}
