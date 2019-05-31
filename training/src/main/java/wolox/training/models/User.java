package wolox.training.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import org.postgresql.shaded.com.ongres.scram.common.util.Preconditions;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.exceptions.BookNotFoundException;

@Entity(name="users")
public class User {


    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String username;

    @NotNull @Column(length = 60)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @NotNull
    private LocalDate birthdate;

    @ManyToMany(cascade = CascadeType.ALL)
    @JsonProperty("books")
    private List<Book> books = new ArrayList<>();

    public User(){

    }

    public User(String name, String username, LocalDate birthdate, String password){
        this.setName(name);
        this.setUsername(username);
        this.setBirthdate(birthdate);
        this.setPassword(password);
    }

    public Long getId() {
        return id;
    }
  
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {

        Preconditions.checkNotNull(username, "The username cannot be null");
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        Preconditions.checkNotNull(password, "The password cannot be null");
        this.password = encoder().encode(password);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Preconditions.checkNotNull(name, "The name cannot be null");
        this.name = name;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {

        Preconditions.checkArgument(birthdate!=null && birthdate.isBefore(LocalDate.now()), "The birthdate cannot be null");
        this.birthdate = birthdate;
    }

    @JsonIgnore
    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    public void setBooks(List<Book> books) {

        Preconditions.checkNotNull(books, "The books' list cannot be empty");
        this.books = books;
    }

    public void addBook(Book book) throws BookAlreadyOwnedException {

        Preconditions.checkNotNull(book, "The book cannot be empty");

        if(books.contains(book)){
            throw new BookAlreadyOwnedException("This user already has the book");
        }

        books.add(book);
    }

    public void removeBook(Book book) throws BookNotFoundException{

        Preconditions.checkNotNull(book, "The book cannot be empty");

        if(!books.contains(book)){
            throw new BookNotFoundException("This user does not own the book you are trying to delete");
        }

        books.remove(book);

    }

    public Boolean anyRequiredAttributeNull()
    {
        return (username==null || name==null || birthdate == null);
    }
  
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id) &&
            Objects.equals(username, user.username) &&
            Objects.equals(name, user.name) &&
            Objects.equals(birthdate, user.birthdate) &&
            Objects.equals(books, user.books);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, name, birthdate, books);
    }

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
}
