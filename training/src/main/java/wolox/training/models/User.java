package wolox.training.models;

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
    @JoinTable(name = "book_user",
        joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "user_id",
            referencedColumnName = "id"))
    private List<Book> books = new ArrayList<>();

    public User(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String username) {
        this.name = username;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public List<Book> getBooks() {
        return (List<Book>) Collections.unmodifiableCollection(books);
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public void addBook(Book book) throws BookAlreadyOwnedException {

        if(books.contains(book)){
            throw new BookAlreadyOwnedException("This user already has the book");
        }
        books.add(book);
    }

    public void removeBook(Book book) throws BookNotFoundException{
        if(!books.contains(book)){
            throw new BookNotFoundException("This user does not own the book you are trying to delete");
        }

        books.remove(book);

    }





}
