package wolox.training.controllers;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doThrow;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.CoreMatchers.is;
import static wolox.training.TestUtilities.createDefaultBook;
import static wolox.training.TestUtilities.mapToJsonString;
import static wolox.training.TestUtilities.setBookId;

import wolox.training.exceptions.BookNotFoundException;
import wolox.training.models.Book;
import wolox.training.models.BookDTO;
import wolox.training.repositories.BookRepository;
import wolox.training.security.CustomAuthenticationProvider;
import wolox.training.services.OpenLibraryService;


@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private OpenLibraryService openLibraryService;

    @MockBean
    private CustomAuthenticationProvider customAuthenticationProvider;

    private String baseUrl;
    private String bookNotFoundExReason;
    private String nullAttributesExReason;
    private String idMismatchExReason;
    private Book book;
    private Book otherBook;
    private Long nonExistingId;
    private BookDTO bookDTO;
    private List<String> authors;
    private List<String> publishers;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
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

        publishers = new ArrayList<>();
        publishers.add("Anchor Books");

        authors = new ArrayList<>();
        authors.add("Zhizhong Cai");

        bookDTO = new BookDTO();
        bookDTO.setISBN("0385472579");
        bookDTO.setTitle("Zen speaks");
        bookDTO.setPublishers(publishers);
        bookDTO.setSubtitle("shouts of nothingness");
        bookDTO.setNumberOfPages(159);
        bookDTO.setImage("https://covers.openlibrary.org/b/id/240726-S.jpg");
        bookDTO.setPublishDate("1994");
        bookDTO.setAuthors(authors);

        given(bookRepository.findAll()).willReturn(books);
        given(bookRepository.findById(book.getId())).willReturn(java.util.Optional.of(book));
        given(bookRepository.findById(nonExistingId)).willReturn(Optional.empty());
    }

    //region get all books tests
    @WithMockUser(username = "user", password = "1234")
    @Test
    public void whenGetBooks_thenReturnJsonArray() throws Exception{

        mvc.perform(get(baseUrl)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].title", is(book.getTitle())))
            .andExpect(jsonPath("$[1].title", is(otherBook.getTitle())));
    }

    @Test
    public void givenRequestOnPrivateService_shouldFailWith401() throws Exception {
        mvc.perform(get(baseUrl)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
    //endregion

    //region get one book tests
    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenId_whenGetBook_thenReturnJson() throws Exception {

        mvc.perform(get(baseUrl+"{id}", book.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is(book.getTitle())));
    }

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenNonExistingId_whenGetBook_thenThrowNotFound() throws Exception {

        mvc.perform(get(baseUrl+"{id}",nonExistingId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(bookNotFoundExReason));
    }

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenInBdIsbn_whenFindByIsbn_thenReturnJson() throws Exception {
        given(bookRepository.findByIsbn(book.getIsbn())).willReturn(Optional.ofNullable(book));

        mvc.perform(get(baseUrl+"findOne/{isbn}", book.getIsbn())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("title", is(book.getTitle())))
            .andExpect(jsonPath("author", is(book.getAuthor())))
            .andExpect(jsonPath("publisher", is(book.getPublisher())));
    }

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenInApiIsbn_whenFindByIsbn_thenReturnJson() throws Exception {

        String isbn = bookDTO.getISBN();

        Book newBook = new Book(bookDTO);

        given(bookRepository.findByIsbn(isbn)).willReturn(Optional.empty());
        given(openLibraryService.bookInfo(isbn)).willReturn(bookDTO);
        given(bookRepository.save(new Book(bookDTO))).willReturn(newBook);

        mvc.perform(get(baseUrl+"findOne/{isbn}", isbn)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("author", is(newBook.getAuthor())))
            .andExpect(jsonPath("publisher", is(newBook.getPublisher())))
            .andExpect(jsonPath("title", is(newBook.getTitle())))
            .andExpect(jsonPath("subtitle", is(newBook.getSubtitle())))
            .andExpect(jsonPath("year", is(newBook.getYear())))
            .andExpect(jsonPath("pages", is(newBook.getPages())))
            .andExpect(jsonPath("image", is(newBook.getImage())))
            .andExpect(jsonPath("isbn", is(newBook.getIsbn())));
    }

    /**
     * Complicated attributes makes reference to an entity with a string date that contains more than the year or one with more than one authors or publishers
     */
    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenInApiIsbnWithManyAuthors_whenFindByIsbn_thenReturnJson()
        throws Exception {

        publishers.add("Other publisher");
        authors.add("Other author");
        authors.add("Another author");

        bookDTO.setPublishers(publishers);
        bookDTO.setPublishDate("March 1994");
        bookDTO.setAuthors(authors);

        String isbn = bookDTO.getISBN();

        Book newBook = new Book(bookDTO);

        given(bookRepository.findByIsbn(isbn)).willReturn(Optional.empty());
        given(openLibraryService.bookInfo(isbn)).willReturn(bookDTO);
        given(bookRepository.save(new Book(bookDTO))).willReturn(newBook);

        mvc.perform(get(baseUrl+"findOne/{isbn}", isbn)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("author", is(newBook.getAuthor())))
            .andExpect(jsonPath("publisher", is(newBook.getPublisher())))
            .andExpect(jsonPath("title", is(newBook.getTitle())))
            .andExpect(jsonPath("subtitle", is(newBook.getSubtitle())))
            .andExpect(jsonPath("year", is(newBook.getYear())))
            .andExpect(jsonPath("pages", is(newBook.getPages())))
            .andExpect(jsonPath("image", is(newBook.getImage())))
            .andExpect(jsonPath("isbn", is(newBook.getIsbn())));
    }

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenNonExistingIsbn_whenFindByIsbn_thenThrowBookNotFound() throws Exception {
        String nonExistingIsbn = "000";

        given(bookRepository.findByIsbn(nonExistingIsbn)).willReturn(Optional.empty());
        willThrow(new BookNotFoundException()).given(openLibraryService).bookInfo(any(String.class));

        mvc.perform(get(baseUrl+"findOne/{isbn}","nonExistingIsbn")
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

        mvc.perform(post(baseUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringNewBook))
            .andExpect(status().isBadRequest())
            .andExpect(status().reason(nullAttributesExReason));
    }

    //endregion

    //region update book tests
    @WithMockUser(username = "user", password = "1234")
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
    @WithMockUser(username = "user", password = "1234")
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

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenNonExistingId_whenUpdateBook_thenThrowBookNotFound() throws Exception{
        Book changedBook = createDefaultBook(nonExistingId, "Harry Potter and the Philosopher's Stone");

        String stringChangedBook = mapToJsonString(changedBook);

        mvc.perform(put(baseUrl+"{id}",nonExistingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringChangedBook))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(bookNotFoundExReason));
    }

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenWrongId_whenUpdateBook_thenThrowIdMismatch() throws Exception{
        Book changedBook = createDefaultBook(2L, "Harry Potter and the Philosopher's Stone");

        String stringChangedBook = mapToJsonString(changedBook);

        mvc.perform(put(baseUrl+"{id}",book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringChangedBook))
            .andExpect(status().isConflict())
            .andExpect(status().reason(idMismatchExReason));
    }

    @WithMockUser(username = "user", password = "1234")
    @Test(expected = IllegalArgumentException.class)
    public void givenNullAttribute_whenUpdateBook_thenThrowNullArguments() throws Exception{
        Book changedBook = book;
        changedBook.setTitle(null);

        String stringChangedBook = mapToJsonString(changedBook);

        mvc.perform(put(baseUrl+"{id}",book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringChangedBook))
            .andExpect(status().isBadRequest())
            .andExpect(status().reason(nullAttributesExReason));
    }
    //endregion

    //region delete book tests
    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenId_whenDeleteBook() throws Exception{
        mvc.perform(delete(baseUrl+"{id}",book.getId()))
            .andExpect(status().isOk());
    }

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenNonExistingId_whenDeleteBook_thenThrowNotFound() throws Exception{
        mvc.perform(delete(baseUrl+"{id}", nonExistingId))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(bookNotFoundExReason));
    }
    //endregion

}
