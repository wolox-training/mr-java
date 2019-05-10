package wolox.training;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.ArrayList;
import java.util.List;
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
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;


@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookRepository bookRepository;


    @Test
    public void givenId_whenGetBook_thenReturnJson() throws Exception {
        Book book = createDefaultBook(1);

        given(bookRepository.findById(book.getId())).willReturn(java.util.Optional.of(book));

        mvc.perform(get("/api/books/{id}",book.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is(book.getTitle())));
    }

    @Test
    public void whenGetBooks_thenReturnJsonArray() throws Exception{
        Book book1 = createDefaultBook(1);
        Book book2 = createDefaultBook(2);

        List<Book> books = new ArrayList<Book>();
        books.add(book1);
        books.add(book2);

        given(bookRepository.findAll()).willReturn(books);

        mvc.perform(get("/api/books/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].title", is(book1.getTitle())))
        .andExpect(jsonPath("$[1].title", is(book2.getTitle())));
    }

    @Test
    public void givenId_whenUpdateBook_thenReturnJson() throws Exception{
        Book book = createDefaultBook(1);

        given(bookRepository.findById(book.getId())).willReturn(java.util.Optional.of(book));
        given(bookRepository.save(book)).willReturn(book);

        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String stringBook=ow.writeValueAsString(book);

        mvc.perform(put("/api/books/{id}",book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringBook))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is(book.getTitle())));
    }

    @Test
    public void givenId_deleteBook() throws Exception{
        Book book = createDefaultBook(1);

        given(bookRepository.findById(book.getId())).willReturn(java.util.Optional.of(book));

        mvc.perform(delete("/api/books/{id}",book.getId()))
            .andExpect(status().isOk());

    }

    private Book createDefaultBook(Integer id) throws NoSuchFieldException, IllegalAccessException{
        Book book = new Book("J. K.", "as", "as", "as", "as", "1999", 25, "142536");

        Field fieldId = book.getClass().getDeclaredField("id");
        fieldId.setAccessible(true);
        fieldId.set(book, new Long(id));

        return book;
    }

}
