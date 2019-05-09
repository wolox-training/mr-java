package wolox.training;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.CoreMatchers.is;

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
        Book book = new Book("J. K.", "as", "as", "as", "as", "1999", 25, "142536");
        Long bookId = Integer.toUnsignedLong(1);

        given(bookRepository.findById(bookId)).willReturn(java.util.Optional.of(book));

        mvc.perform(get("/api/books/"+bookId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is(book.getTitle())));
    }

    @Test
    public void whenGetBooks_thenReturnJsonArray() throws Exception{
        Book book1 = new Book("J. K.", "as", "as", "as", "as", "1999", 25, "142536");
        Book book2 = new Book("Yop", "as", "My life", "as", "as", "1995", 25, "142536");


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
        Book book = new Book("J. K.", "as", "as", "as", "as", "1999", 25, "142536");
        book.setId(Integer.toUnsignedLong(1));

        given(bookRepository.findById(book.getId())).willReturn(java.util.Optional.of(book));
        given(bookRepository.save(book)).willReturn(book);

        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String stringBook=ow.writeValueAsString(book);



        /*JSONObject jsonBook = new JSONObject(stringBook);
        jsonBook.remove("id");
        jsonBook.put("id", bookId);

        stringBook = jsonBook.toString();*/

        mvc.perform(put("/api/books/"+book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringBook))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is(book.getTitle())));
    }

}
