package wolox.training.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static wolox.training.TestUtilities.createDefaultBook;
import static wolox.training.TestUtilities.createDefaultUser;

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
import static wolox.training.TestUtilities.mapToJsonString;

import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookRepository bookRepository;

    private User user;
    private User otherUser;
    private List<User> users = new ArrayList<>();
    private Long nonExistingId;
    private String baseUrl;
    private String userNotFoundExReason;
    private String nullAttributesExReason;
    private String idMismatchExReason;
    private String bookNotFoundExReason;
    private String bookAlreadyOwnedExReason;
    private String bookNotInUserListExReason;
    private Book book;


    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        baseUrl = "/api/users/";
        nonExistingId = 0L;

        userNotFoundExReason = "User Not Found";
        nullAttributesExReason = "Received Null Attributes";
        idMismatchExReason = "User Id Mismatch";
        bookNotFoundExReason = "Book Not Found";
        bookAlreadyOwnedExReason = "Book Already Owned";
        bookNotInUserListExReason = "This user does not own the book you are trying to delete";

        user = createDefaultUser(1L, "Ana");
        otherUser = createDefaultUser(2L, "Mariana");

        book = createDefaultBook(10L, "Cinderella");

        users.add(user);
        users.add(otherUser);

        given(userRepository.findAll()).willReturn(users);
        given(userRepository.findById(user.getId())).willReturn(java.util.Optional.ofNullable(user));
        given(userRepository.findById(nonExistingId)).willReturn(Optional.empty());

        given(bookRepository.findById(book.getId())).willReturn(Optional.of(book));
        given(bookRepository.findById(nonExistingId)).willReturn(Optional.empty());

    }

    //region get all users
    @Test
    public void whenGetUsers_thenReturnJsonArray() throws Exception {
        mvc.perform(get(baseUrl)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(users.size())))
            .andExpect(jsonPath("$[0].name", is(user.getName())))
            .andExpect(jsonPath("$[1].name", is(otherUser.getName())));
    }
    //endregion

    //region get one user
    @Test
    public void givenId_whenGetUser_thenReturnJson() throws Exception {
        mvc.perform(get(baseUrl+"{id}", user.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is(user.getName())))
            .andExpect(jsonPath("$.username", is(user.getUsername())))
            .andExpect(jsonPath("$.birthdate", is(user.getBirthdate().toString())))
            .andExpect(jsonPath("$.books", hasSize(user.getBooks().size())))
            .andExpect(jsonPath("$.books[0].title", is(user.getBooks().get(0).getTitle())))
            .andExpect(jsonPath("$.books[1].title", is(user.getBooks().get(1).getTitle())))
            .andExpect(jsonPath("$.books[2].title", is(user.getBooks().get(2).getTitle())));
    }

    @Test
    public void givenNonExistingId_whenGetUser_thenThrowNotFound() throws Exception {
        mvc.perform(get(baseUrl+"{id}", nonExistingId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(userNotFoundExReason));
    }
    //endregion

    //region create user
    @Test
    public void givenBook_whenCreateUser_thenReturnJson() throws Exception {
        String userString = mapToJsonString(user);

        given(userRepository.save(user)).willReturn(user);

        mvc.perform(post(baseUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .content(userString))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name", is(user.getName())))
            .andExpect(jsonPath("$.username", is(user.getUsername())))
            .andExpect(jsonPath("$.birthdate", is(user.getBirthdate().toString())))
            .andExpect(jsonPath("$.books", hasSize(user.getBooks().size())))
            .andExpect(jsonPath("$.books[0].title", is(user.getBooks().get(0).getTitle())))
            .andExpect(jsonPath("$.books[1].title", is(user.getBooks().get(1).getTitle())))
            .andExpect(jsonPath("$.books[2].title", is(user.getBooks().get(2).getTitle())));
    }

    @Test (expected = IllegalArgumentException.class)
    public void givenUserWithEmptyValues_whenCreateUser_thenThrowNullAttributes()
        throws Exception {
        User newUser = user;
        newUser.setName(null);
        String userString = mapToJsonString(newUser);

        mvc.perform(post(baseUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .content(userString))
            .andExpect(status().isBadRequest())
            .andExpect(status().reason(nullAttributesExReason));
    }
    //endregion

    //region update user
    @Test
    public void givenUser_whenUpdateUser_thenReturnJson() throws Exception {
        User changedUser = user;
        changedUser.setUsername("newbie");

        String stringChangedUser = mapToJsonString(changedUser);

        given(userRepository.save(changedUser)).willReturn(changedUser);

        mvc.perform(put(baseUrl+"{id}", user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringChangedUser))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is(changedUser.getName())))
            .andExpect(jsonPath("$.username", is(changedUser.getUsername())))
            .andExpect(jsonPath("$.birthdate",  is(changedUser.getBirthdate().toString())))
            .andExpect(jsonPath("$.books", hasSize(changedUser.getBooks().size())));
    }

    @Test
    public void givenWrongId_whenUpdateUser_thenThrowIdMismatch() throws Exception {
        User changedUser = createDefaultUser(4L, "Monica");

        String stringChangedUser = mapToJsonString(changedUser);

        mvc.perform(put(baseUrl+"{id}", user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringChangedUser))
            .andExpect(status().isConflict())
            .andExpect(status().reason(idMismatchExReason));
    }

    @Test
    public void givenNonExistingId_whenUpdateUser_thenThrowNotFound() throws Exception {
        User changedUser = createDefaultUser(nonExistingId, "Marta");

        String stringChangedUser = mapToJsonString(changedUser);

        mvc.perform(put(baseUrl+"{id}", changedUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringChangedUser))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(userNotFoundExReason));
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenNullAttribute_whenUpdateUser_thenThrowNullAttribute() throws Exception {
        User changedUser = user;
        changedUser.setName(null);

        String stringChangedBook = mapToJsonString(changedUser);

        mvc.perform(put(baseUrl+"{id}", changedUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringChangedBook))
            .andExpect(status().isBadRequest())
            .andExpect(status().reason(nullAttributesExReason));
    }
    //endregion

    //region delete user
    @Test
    public void givenId_whenDeleteUser() throws Exception {
        mvc.perform(delete(baseUrl+"{id}",user.getId()))
            .andExpect(status().isOk());
    }

    @Test
    public void givenNonExistingId_whenDeleteUser_thenThrowNotFound() throws Exception{
        mvc.perform(delete(baseUrl+"{id}", nonExistingId))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(userNotFoundExReason));
    }
    //endregion

    //region add book
    @Test
    public void givenBookId_givenUserId_whenAddBookToUser_thenReturnJson() throws Exception {
        User changedUser = createDefaultUser(user.getId(), user.getName());
        changedUser.addBook(book);

        given(userRepository.save(user)).willReturn(changedUser);

        mvc.perform(put(baseUrl+"{userId}/{bookId}", user.getId(), book.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.books", hasSize(changedUser.getBooks().size())));
    }

    @Test
    public void givenNonExistingBookId_givenUserId_whenAddBookToUser_thenThrowBookNotFound() throws Exception {
        mvc.perform(put(baseUrl+"{userId}/{bookId}", user.getId(), nonExistingId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(bookNotFoundExReason));
    }

    @Test
    public void givenBookId_givenNonExistingUserId_whenAddBookToUser_thenThrowBookNotFound() throws Exception {
        mvc.perform(put(baseUrl+"{userId}/{bookId}", nonExistingId, book.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(userNotFoundExReason));
    }

    @Test
    public void givenAlreadyOwnedBookId_givenUserId_whenAddBookToUser_thenThrowAlreadyOwned() throws Exception {
        Book alreadyOwnedBook = user.getBooks().get(0);

        given(bookRepository.findById(alreadyOwnedBook.getId())).willReturn(Optional.of(alreadyOwnedBook));

        mvc.perform(put(baseUrl+"{userId}/{bookId}", user.getId(), alreadyOwnedBook.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isAlreadyReported())
            .andExpect(status().reason(bookAlreadyOwnedExReason));
    }
    //endregion

    //region remove book
    @Test
    public void givenBookId_givenUserId_whenRemoveBookFromUser_thenReturnJson() throws Exception {
        Book removeBook = user.getBooks().get(0);

        User changedUser = createDefaultUser(user.getId(), user.getName());
        changedUser.removeBook(removeBook);

        given(bookRepository.findById(removeBook.getId())).willReturn(Optional.of(removeBook));
        given(userRepository.save(user)).willReturn(changedUser);

        mvc.perform(delete(baseUrl+"{userId}/{bookId}", user.getId(), removeBook.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.books", hasSize(changedUser.getBooks().size())));
    }

    @Test
    public void givenNotInUserBookListBookId_givenUserId_whenRemoveBookFromUser_thenThrowBookNotFound() throws Exception{
        Book removeBook = user.getBooks().get(0);

        User changedUser = createDefaultUser(user.getId(), user.getName());
        changedUser.removeBook(removeBook);

        given(bookRepository.findById(removeBook.getId())).willReturn(Optional.of(removeBook));
        given(userRepository.findById(changedUser.getId())).willReturn(Optional.of(changedUser));

        mvc.perform(delete(baseUrl+"{userId}/{bookId}", changedUser.getId(), removeBook.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(bookNotInUserListExReason));
    }

    @Test
    public void givenNonExistingBookId_givenUserId_whenRemoveBookFromUser_thenThrowBookNotFound() throws Exception{
        mvc.perform(delete(baseUrl+"{userId}/{bookId}", user.getId(), nonExistingId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(bookNotFoundExReason));
    }

    @Test
    public void givenBookId_givenNonExistingUserId_whenRemoveBookFromUser_thenThrowBookNotFound() throws Exception{
        mvc.perform(delete(baseUrl+"{userId}/{bookId}", nonExistingId, book.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(userNotFoundExReason));
    }
    //endregion

}
