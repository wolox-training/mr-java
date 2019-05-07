package wolox.training;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import wolox.training.controllers.BookController;
import wolox.training.models.Book;



@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookController service;

    @Test
    public void givenId_whenGetBook_thenReturnJsonArray() throws Exception {
        Book book = new Book("J. K.", "as", "as", "as", "as", "1999", 25, "142536");

        given(service.findOne(book.getId())).willReturn(book);

        mvc.perform(get("/api/books/"+book.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
            //.andExpect(jsonPath("$[0].id", is(book.getId())));

        /*List<Book> books = new ArrayList<Book>();
        books.add(book);

        given(service.findAll()).willReturn(books);*/
    }


}
