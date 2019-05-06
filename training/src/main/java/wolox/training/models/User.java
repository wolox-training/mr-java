package wolox.training.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import org.postgresql.shaded.com.ongres.scram.common.util.Preconditions;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.exceptions.BookNotFoundException;

@Entity(name="users")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String name;

    @NotNull
    private LocalDate birthdate;

    @ManyToMany(cascade = CascadeType.ALL)
    @JsonProperty("books")
    private List<Book> books = new ArrayList<>();

    public User(){

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
        return (List<Book>) Collections.unmodifiableCollection(books);
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

}
