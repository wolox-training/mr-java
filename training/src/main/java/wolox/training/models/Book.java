package wolox.training.models;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import org.postgresql.shaded.com.ongres.scram.common.util.Preconditions;
import wolox.training.exceptions.CouldNotCreateBookFromDTO;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    private String genre;
    @NotNull
    private String author;
    @NotNull
    private String image;
    @NotNull
    private String title;
    @NotNull
    private String subtitle;
    @NotNull
    private String publisher;
    @NotNull
    private String year;
    @NotNull
    private Integer pages;
    @NotNull
    private String isbn;

    public Book() {
  
    }
  
    public Book(String author, String image, String title, String subtitle, String publisher,
        String year, Integer pages, String isbn, String genre) {
        setAuthor(author);
        setImage(image);
        setTitle(title);
        setSubtitle(subtitle);
        setPublisher(publisher);
        setYear(year);
        setPages(pages);
        setIsbn(isbn);
        setGenre(genre);
    }

    public Book(BookDTO bookDTO) throws CouldNotCreateBookFromDTO {
        try {
            setPublisher(bookDTO.getPublishersAsString());
            setAuthor(bookDTO.getAuthorsAsString());
            setTitle(bookDTO.getTitle());
            setIsbn(bookDTO.getISBN());
            setPages(bookDTO.getNumberOfPages());
            setSubtitle(bookDTO.getSubtitle());
            setImage(bookDTO.getImage());
            setYear(bookDTO.getYear());
        }catch (Exception ex){
            throw new CouldNotCreateBookFromDTO(ex.getMessage());
        }
    }
    
    public Long getId() { return id; }
  
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        Preconditions.checkNotNull(author, "The author cannot be null");
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        Preconditions.checkNotNull(image, "The image cannot be null");
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        Preconditions.checkNotNull(title, "The title cannot be null");
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        Preconditions.checkNotNull(subtitle, "The subtitle cannot be null");
        this.subtitle = subtitle;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        Preconditions.checkNotNull(publisher, "The publisher cannot be null");
        this.publisher = publisher;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        Preconditions.checkArgument(year!=null && Integer.parseInt(year) > 0, "Please enter a valid year");
        this.year = year;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        Preconditions.checkArgument(pages!=null && pages > 0, "The book must have at least 1 page");
        this.pages = pages;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        Preconditions.checkNotNull(title, "The title cannot be null");
        this.isbn = isbn;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Boolean anyRequiredAttributeNull()
    {
        return (author==null || image==null || title ==null || subtitle==null || publisher==null || year==null || pages==null || isbn==null);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Book book = (Book) o;
        return Objects.equals(id, book.id) &&
            Objects.equals(genre, book.genre) &&
            Objects.equals(author, book.author) &&
            Objects.equals(image, book.image) &&
            Objects.equals(title, book.title) &&
            Objects.equals(subtitle, book.subtitle) &&
            Objects.equals(publisher, book.publisher) &&
            Objects.equals(year, book.year) &&
            Objects.equals(pages, book.pages) &&
            Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects
            .hash(id, genre, author, image, title, subtitle, publisher, year, pages, isbn);
    }
}

