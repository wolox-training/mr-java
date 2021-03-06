package wolox.training.controllers;


import java.io.IOException;
import java.util.List;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wolox.training.exceptions.BookIdMismatchException;
import wolox.training.exceptions.BookNotFoundException;
import wolox.training.exceptions.ConnectionFailedException;
import wolox.training.exceptions.UnableToCreateBookFromDTOException;
import wolox.training.exceptions.UnableToReadBookFromAPIException;
import wolox.training.exceptions.NullAttributesException;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.services.OpenLibraryService;


@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    OpenLibraryService openLibraryService;

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model){

        model.addAttribute("name", name);
        return "greeting";
    }

    @GetMapping("/")
    public List<Book> findAll(@RequestParam(name="author", required=false) String author, @RequestParam(name="genre", required=false) String genre,
        @RequestParam(name="image", required=false) String image, @RequestParam(name="title", required=false) String title, @RequestParam(name="subtitle", required=false) String subtitle,
        @RequestParam(name="publisher", required=false) String publisher, @RequestParam(name="year", required=false) String year, @RequestParam(name="pages", required=false) Integer pages,
        @RequestParam(name="isbn", required=false) String isbn, Pageable pageable){

        return bookRepository.findAll(author, genre, image, title, subtitle, publisher, year, pages, isbn, pageable);
    }

    @GetMapping("/{id}")
    public Book findOne(@PathVariable Long id) throws BookNotFoundException {
        return bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> findByIsbn(@PathVariable String isbn)
        throws IOException, JSONException, ConnectionFailedException, BookNotFoundException, NullAttributesException, UnableToReadBookFromAPIException, UnableToCreateBookFromDTOException {

        try{
            return new ResponseEntity<>(bookRepository.findByIsbn(isbn).orElseThrow(BookNotFoundException::new),HttpStatus.OK);
        } catch (BookNotFoundException ex){
            Book newBook = new Book(openLibraryService.bookInfo(isbn));
            return new ResponseEntity<>(this.create(newBook), HttpStatus.CREATED);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@RequestBody Book book) throws NullAttributesException {
        if(book.anyRequiredAttributeNull()){
            throw new NullAttributesException();
        }
        return bookRepository.save(book);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws BookNotFoundException {
        bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
        bookRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Book updateBook(@RequestBody Book book, @PathVariable Long id) throws BookNotFoundException, BookIdMismatchException, NullAttributesException {

        if (!id.equals(book.getId())){
            throw new BookIdMismatchException();
        }

        bookRepository.findById(id).orElseThrow(BookNotFoundException::new);

        if(book.anyRequiredAttributeNull()){
            throw new NullAttributesException();
        }

        return bookRepository.save(book);
    }

    @GetMapping("/byPublisherAndByGenreAndByYear")
    public List<Book> getBooksByPublisherAndByGenreAndByYear(@RequestParam(name="publisher", required=false) String publisher, @RequestParam(name="genre", required=false) String genre,
        @RequestParam(name="year", required=false) String year, Pageable pageable){

        return bookRepository.findByPublisherAndGenreAndYear(publisher, genre, year, pageable);
    }
}

