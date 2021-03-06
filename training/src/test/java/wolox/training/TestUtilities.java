package wolox.training;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import wolox.training.models.Book;
import wolox.training.models.User;

public abstract class TestUtilities {


    public static Book createDefaultBook(Long id, String title) throws NoSuchFieldException, IllegalAccessException{
        Book book = new Book("J. K. Rowling", "image.png", title,
            "-", "Bloomsbury Publishing", "1997", 223, "9780747532743", "Fantasy");

        Field fieldId = book.getClass().getDeclaredField("id");
        fieldId.setAccessible(true);
        fieldId.set(book, id);

        return book;
    }

    public static Book setBookId(Long id, Book book) throws NoSuchFieldException, IllegalAccessException {
        Field fieldId = book.getClass().getDeclaredField("id");
        fieldId.setAccessible(true);
        fieldId.set(book, id);
        return book;
    }

    public static String mapToJsonString(Object obj) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        return ow.writeValueAsString(obj);
    }

    public static User createDefaultUser(Long id, String username)
        throws IllegalAccessException, NoSuchFieldException {

        LocalDate birthdate =  LocalDate.of(1990, 05, 9);
        User user = new User("default", username, birthdate, "1234");

        user.setBooks(generateBooks());

        Field fieldId = user.getClass().getDeclaredField("id");
        fieldId.setAccessible(true);
        fieldId.set(user, id);

        return user;
    }

    private static List<Book> generateBooks() throws NoSuchFieldException, IllegalAccessException {
        List<Book> books = new ArrayList<>();

        Book book = createDefaultBook(1L, "Harry Potter and the Philosopher's Stone");
        Book oneBook = createDefaultBook(2L, "Harry Potter 2");
        Book otherBook = createDefaultBook(3L, "Harry Potter 3");

        books.add(book);
        books.add(oneBook);
        books.add(otherBook);

        return books;
    }

    @Bean
    public static PasswordEncoder encoder(){

        return new BCryptPasswordEncoder();
    }



}
