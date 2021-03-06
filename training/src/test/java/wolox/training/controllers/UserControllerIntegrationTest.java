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
import java.time.LocalDate;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.test.context.support.WithMockUser;
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
import wolox.training.security.CustomAuthenticationProvider;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private CustomAuthenticationProvider authProvider;

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
    private Pageable defaultPageable;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {

        baseUrl = "/api/users/";
        nonExistingId = 0L;

        defaultPageable = PageRequest.of(0,20);

        userNotFoundExReason = "User Not Found";
        nullAttributesExReason = "Received Null Attributes";
        idMismatchExReason = "User Id Mismatch";
        bookNotFoundExReason = "Book Not Found";
        bookAlreadyOwnedExReason = "Book Already Owned";
        bookNotInUserListExReason = "This user does not own the book you are trying to delete";

        user = createDefaultUser(1L, "user");
        otherUser = createDefaultUser(2L, "Mariana");

        book = createDefaultBook(10L, "Cinderella");

        users.add(user);
        users.add(otherUser);

        given(userRepository.findAllUsers(defaultPageable)).willReturn(users);
        given(userRepository.findById(user.getId())).willReturn(java.util.Optional.ofNullable(user));
        given(userRepository.findById(nonExistingId)).willReturn(Optional.empty());

        given(bookRepository.findById(book.getId())).willReturn(Optional.of(book));
        given(bookRepository.findById(nonExistingId)).willReturn(Optional.empty());
    }

    //region get username
    @WithMockUser(username = "user", password = "1234")
    @Test
    public void whenGetUsername_thenReturnUsername() throws Exception {
        given(userRepository.findFirstByUsername("user")).willReturn(user);

        mvc.perform(get(baseUrl+"username")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is(user.getName())))
            .andExpect(jsonPath("$.username", is(user.getUsername())))
            .andExpect(jsonPath("$.birthdate", is(user.getBirthdate().toString())))
            .andExpect(jsonPath("$.books", hasSize(user.getBooks().size())));
    }

    @Test
    public void notLoggedIn_whenGetUsername_thenReturnUnauthorized() throws Exception {
        given(userRepository.findFirstByUsername("user")).willReturn(user);

        mvc.perform(get(baseUrl+"username")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
    //endregion

    //region get all users
    @WithMockUser(username = "user", password = "1234")
    @Test
    public void whenGetUsers_thenReturnJsonArray() throws Exception {
        mvc.perform(get(baseUrl)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(users.size())))
            .andExpect(jsonPath("$[0].name", is(user.getName())))
            .andExpect(jsonPath("$[1].name", is(otherUser.getName())));
    }

    @Test
    public void givenRequestOnPrivateService_shouldFailWith401() throws Exception {
        mvc.perform(get(baseUrl)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenPageableAndNameSorting_whenGetAllUsers_thenReturnJsonArray()
        throws Exception {
        User newUser = createDefaultUser(3L, "Newbie");
        newUser.setBirthdate(LocalDate.of(1960, 5, 5));

        Integer page = 0;
        Integer size = 5;
        List<Order> sortOrderList = new ArrayList<>();
        sortOrderList.add(new Order(null, "name"));
        sortOrderList.add(new Order(null, "id"));

        List<User> foundUsers = new ArrayList<>();
        foundUsers.add(user);
        foundUsers.add(otherUser);
        foundUsers.add(newUser);

        given(userRepository.findAllUsers(PageRequest.of(page, size, Sort.by(sortOrderList)))).willReturn(foundUsers);

        mvc.perform(get(baseUrl+"?page={page}&size={size}&sort={firstOrder}&sort={secondOrder}", page, size,
            sortOrderList.get(0).getProperty(), sortOrderList.get(1).getProperty())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(foundUsers.size())))
            .andExpect(jsonPath("$[0].name", is(foundUsers.get(0).getName())))
            .andExpect(jsonPath("$[1].name", is(foundUsers.get(1).getName())))
            .andExpect(jsonPath("$[2].name", is(foundUsers.get(2).getName())));
    }

    //endregion

    //region get one user
    @WithMockUser(username = "user", password = "1234")
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

    @WithMockUser(username = "user", password = "1234")
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
    @WithMockUser(username = "user", password = "1234")
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
    @WithMockUser(username = "user", password = "1234")
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
    @WithMockUser(username = "user", password = "1234")
    public void givenNonExistingId_whenUpdateUser_thenThrowNotFound() throws Exception {
        User changedUser = createDefaultUser(nonExistingId, "Marta");

        String stringChangedUser = mapToJsonString(changedUser);

        mvc.perform(put(baseUrl+"{id}", changedUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(stringChangedUser))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(userNotFoundExReason));
    }

    @WithMockUser(username = "user", password = "1234")
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

    //region update password
    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenOldAndNewPass_whenUpdatePassword_thenReturnJson() throws Exception {
        String oldPass = "1234";
        String newPass = "1111";

        otherUser.setPassword(oldPass);

        User changedUser = createDefaultUser(otherUser.getId(), otherUser.getUsername());
        changedUser.setPassword(newPass);

        JSONObject jo = new JSONObject();
        jo.put("oldPassword", oldPass);
        jo.put("newPassword", newPass);

        String jsonString = jo.toString();

        given(userRepository.findById(otherUser.getId())).willReturn(Optional.ofNullable(otherUser));
        given(userRepository.save(changedUser)).willReturn(changedUser);

        mvc.perform(put(baseUrl+"editPass/{userId}", otherUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is(changedUser.getName())))
            .andExpect(jsonPath("$.username", is(changedUser.getUsername())))
            .andExpect(jsonPath("$.birthdate",  is(changedUser.getBirthdate().toString())))
            .andExpect(jsonPath("$.books", hasSize(changedUser.getBooks().size())));
    }

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenWongOldPass_whenUpdatePassword_thenThrowOldPasswordMismatch() throws Exception {
        String wrongOldPass = "2222";
        String newPass = "1111";

        otherUser.setPassword("1234");

        JSONObject jo = new JSONObject();
        jo.put("oldPassword", wrongOldPass);
        jo.put("newPassword", newPass);

        String jsonString = jo.toString();

        given(userRepository.findById(otherUser.getId())).willReturn(Optional.ofNullable(otherUser));

        mvc.perform(put(baseUrl+"editPass/{userId}", otherUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString))
            .andExpect(status().isConflict())
            .andExpect(status().reason("Old Password Mismatch"));
    }
    //endregion

    //region delete user
    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenId_whenDeleteUser() throws Exception {
        mvc.perform(delete(baseUrl+"{id}",user.getId()))
            .andExpect(status().isOk());
    }

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenNonExistingId_whenDeleteUser_thenThrowNotFound() throws Exception{
        mvc.perform(delete(baseUrl+"{id}", nonExistingId))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(userNotFoundExReason));
    }
    //endregion

    //region add book
    @WithMockUser(username = "user", password = "1234")
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

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenNonExistingBookId_givenUserId_whenAddBookToUser_thenThrowBookNotFound() throws Exception {
        mvc.perform(put(baseUrl+"{userId}/{bookId}", user.getId(), nonExistingId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(bookNotFoundExReason));
    }

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenBookId_givenNonExistingUserId_whenAddBookToUser_thenThrowBookNotFound() throws Exception {
        mvc.perform(put(baseUrl+"{userId}/{bookId}", nonExistingId, book.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(userNotFoundExReason));
    }

    @WithMockUser(username = "user", password = "1234")
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
    @WithMockUser(username = "user", password = "1234")
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

    @WithMockUser(username = "user", password = "1234")
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

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenNonExistingBookId_givenUserId_whenRemoveBookFromUser_thenThrowBookNotFound() throws Exception{
        mvc.perform(delete(baseUrl+"{userId}/{bookId}", user.getId(), nonExistingId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(bookNotFoundExReason));
    }

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenBookId_givenNonExistingUserId_whenRemoveBookFromUser_thenThrowBookNotFound() throws Exception{
        mvc.perform(delete(baseUrl+"{userId}/{bookId}", nonExistingId, book.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(status().reason(userNotFoundExReason));
    }
    //endregion

    //region find user by birthdate between and name contains
    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenTwoDatesAndCharacters_whenFindByBirthdateBetweenAndNameContains_thenReturnJsonArray()
        throws Exception {
        User oldUser = createDefaultUser(3L, "oldie");
        oldUser.setBirthdate(LocalDate.of(1960, 5, 5));
        oldUser.setName("oldie");

        String fromDate = "1950-05-05";
        String toDate = "1970-05-05";
        String characters = "o";

        List<User> foundUsers = new ArrayList<>();
        foundUsers.add(oldUser);
        given(userRepository.findByBirthdateBetweenAndNameContains(LocalDate.parse(fromDate), LocalDate.parse(toDate), characters, defaultPageable)).willReturn(foundUsers);

        mvc.perform(get(baseUrl+"birthdateBetweenAndNameContains?fromDate={fromDate}&toDate={toDate}&characters={characters}", fromDate, toDate, characters)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(foundUsers.size())))
            .andExpect(jsonPath("$[0].name", is(oldUser.getName())));
    }

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenWrongDate_whenFindByBirthdateBetweenAndNameContains_thenThrowInvalidDate()
        throws Exception {
        User oldUser = createDefaultUser(3L, "oldie");
        oldUser.setBirthdate(LocalDate.of(1960, 5, 5));
        oldUser.setName("oldie");

        String fromDate = "1950-05-05";
        String toDate = "19770-05-05";

       JSONObject jo = new JSONObject();
        jo.put("fromDate", fromDate);
        jo.put("toDate",toDate);

        String jsonString = jo.toString();

        mvc.perform(get(baseUrl+"birthdateBetweenAndNameContains?fromDate={fromDate}&toDate={toDate}&characters=", fromDate, toDate)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonString))
            .andExpect(status().isConflict())
            .andExpect(status().reason("Invalid date"));
    }

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenNoCharacters_whenFindByBirthdateBetweenAndNameContains_thenReturnJsonArray()
        throws Exception {
        User oldUser = createDefaultUser(3L, "oldie");
        oldUser.setBirthdate(LocalDate.of(1960, 5, 5));
        oldUser.setName("Oldie");

        User otherOldUser = createDefaultUser(3L, "otherOldie");
        oldUser.setBirthdate(LocalDate.of(1968, 5, 5));
        oldUser.setName("Nice name");

        String fromDate = "1950-05-05";
        String toDate = "1970-05-05";

        List<User> foundUsers = new ArrayList<>();
        foundUsers.add(oldUser);
        foundUsers.add(otherOldUser);
        given(userRepository.findByBirthdateBetweenAndNameContains(LocalDate.parse(fromDate), LocalDate.parse(toDate), null, defaultPageable)).willReturn(foundUsers);

        mvc.perform(get(baseUrl+"birthdateBetweenAndNameContains?fromDate={fromDate}&toDate={toDate}", fromDate, toDate)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(foundUsers.size())))
            .andExpect(jsonPath("$[0].name", is(oldUser.getName())))
            .andExpect(jsonPath("$[1].name", is(otherOldUser.getName())));
    }

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenNoFromDate_whenFindByBirthdateBetweenAndNameContains_thenReturnJsonArray()
        throws Exception {
        User oldUser = createDefaultUser(3L, "oldie");
        oldUser.setBirthdate(LocalDate.of(1960, 5, 5));
        oldUser.setName("Oldie");

        User otherOldUser = createDefaultUser(3L, "otherOldie");
        oldUser.setBirthdate(LocalDate.of(1968, 5, 5));
        oldUser.setName("Nice name");

        String toDate = "1970-05-05";
        String characters = "i";

        List<User> foundUsers = new ArrayList<>();
        foundUsers.add(oldUser);
        foundUsers.add(otherOldUser);
        given(userRepository.findByBirthdateBetweenAndNameContains(null, LocalDate.parse(toDate), characters, defaultPageable)).willReturn(foundUsers);

        mvc.perform(get(baseUrl+"birthdateBetweenAndNameContains?toDate={toDate}&characters={characters}", toDate, characters)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(foundUsers.size())))
            .andExpect(jsonPath("$[0].name", is(oldUser.getName())))
            .andExpect(jsonPath("$[1].name", is(otherOldUser.getName())));
    }

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenNoToDate_whenFindByBirthdateBetweenAndNameContains_thenReturnJsonArray()
        throws Exception {
        User oldUser = createDefaultUser(3L, "oldie");
        oldUser.setBirthdate(LocalDate.of(1960, 5, 5));
        oldUser.setName("Oldie");

        User otherOldUser = createDefaultUser(3L, "otherOldie");
        oldUser.setBirthdate(LocalDate.of(1968, 5, 5));
        oldUser.setName("Nice name");

        String fromDate = "1965-05-05";
        String characters = "i";

        List<User> foundUsers = new ArrayList<>();
        foundUsers.add(otherOldUser);
        given(userRepository.findByBirthdateBetweenAndNameContains(LocalDate.parse(fromDate), null, characters, defaultPageable)).willReturn(foundUsers);

        mvc.perform(get(baseUrl+"birthdateBetweenAndNameContains?fromDate={fromDate}&characters={characters}", fromDate, characters)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(foundUsers.size())))
            .andExpect(jsonPath("$[0].name", is(otherOldUser.getName())));
    }

    @WithMockUser(username = "user", password = "1234")
    @Test
    public void givenFromDateAndPageable_whenFindByBirthdateBetweenAndNameContains_thenReturnJsonArray()
        throws Exception {

        User oldUser = createDefaultUser(3L, "oldie");
        oldUser.setBirthdate(LocalDate.of(1960, 5, 5));
        oldUser.setName("Oldie");

        String fromDate = "1960-03-05";

        Integer page = 0;
        Integer size = 2;
        List<Order> sortOrderList = new ArrayList<>();
        sortOrderList.add(new Order(null, "name"));
        sortOrderList.add(new Order(null, "id"));

        List<User> foundUsers = new ArrayList<>();
        foundUsers.add(user);
        foundUsers.add(otherUser);
        foundUsers.add(oldUser);

        given(userRepository.findByBirthdateBetweenAndNameContains(LocalDate.parse(fromDate), null, null, PageRequest.of(page, size, Sort.by(sortOrderList)))).willReturn(foundUsers.subList(0,size));

        mvc.perform(get(baseUrl+"birthdateBetweenAndNameContains?fromDate={fromDate}&page={page}&size={size}&sort={firstOrder}&sort={secondOrder}", fromDate, page, size,
            sortOrderList.get(0).getProperty(), sortOrderList.get(1).getProperty())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(size)))
            .andExpect(jsonPath("$[0].name", is(user.getName())))
            .andExpect(jsonPath("$[1].name", is(otherUser.getName())));
    }
    //enregion

}

