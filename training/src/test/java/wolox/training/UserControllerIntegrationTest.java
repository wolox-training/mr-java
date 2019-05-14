package wolox.training;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static wolox.training.TestUtilities.createDefaultUser;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.tomcat.jni.Local;
import org.json.JSONArray;
import org.json.JSONObject;
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

import wolox.training.controllers.UserController;
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
    private Long nonExistingId;
    private String baseUrl;
    private String userNotFoundExReason;
    private String nullAttributesExReason;

    @Before
    public void runBefore() throws NoSuchFieldException, IllegalAccessException {
        baseUrl = "/api/users/";
        nonExistingId = 0L;

        userNotFoundExReason = "User Not Found";
        nullAttributesExReason = "Received Null Attributes";

        user = createDefaultUser(1L, "Ana");
        otherUser = createDefaultUser(2L, "Mariana");

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(otherUser);

        given(userRepository.findAll()).willReturn(users);
        given(userRepository.findById(user.getId())).willReturn(java.util.Optional.ofNullable(user));
        given(userRepository.findById(nonExistingId)).willReturn(Optional.empty());

    }

    //region get all users
    @Test
    public void whenGetUsers_thenReturnJsonArray() throws Exception {
        mvc.perform(get(baseUrl)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
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
            .andExpect(jsonPath("$.books", hasSize(user.getBooks().size())));
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
            .andExpect(jsonPath("$.books", hasSize(user.getBooks().size())));
    }

    @Test (expected = IllegalArgumentException.class)
    public void givenUserWithEmptyValues_whenCreateUser_thenThrowNullAttributes()
        throws Exception {
        User newUser = user;
        newUser.setName(null);
        String userString = mapToJsonString(newUser);

        given(userRepository.save(newUser)).willReturn(newUser);

        mvc.perform(post(baseUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .content(userString))
            .andExpect(status().isBadRequest())
            .andExpect(status().reason(nullAttributesExReason));
    }
    //endregion

    //region update user
    //endregion

    //region delete user
    //endregion

    //region add book
    //endregion

    //region remove book
    //endregion

}
