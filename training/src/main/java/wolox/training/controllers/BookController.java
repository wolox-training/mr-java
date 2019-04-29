package wolox.training.controllers;


import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.persistence.RollbackException;
import org.omg.CORBA.INTERNAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import wolox.training.exceptions.BookNotFoundException;
import wolox.training.exceptions.NullArgumentsException;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import org.springframework.dao.EmptyResultDataAccessException;

@Controller
public class BookController {

    @Autowired
    BookRepository bookRepository;
    private Long id;

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model){

        model.addAttribute("name", name);
        return "greeting";
    }



    //create
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(Book book, Model model) {

        try {

            if(book.anyArgumentNull()){
                throw new NullArgumentsException("Atributos vacíos");
            }

            bookRepository.save(book);
        }catch(DataIntegrityViolationException ex){
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage(), ex);
        }catch (NullArgumentsException ex){
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage(), ex);
            //debería mostrar cartelito de error pero como no debería hacer las views, no lo hago
        }

        return "redirect:/books";

    }
    //readAll
    @GetMapping("/books")
    public String books(Model model){
        List<Book> books = bookRepository.findAllByOrderByIdAsc();

        model.addAttribute("books", books);
        return "books";
    }

    //readOne
    @GetMapping("/book")
    public String read(@RequestParam(name="id", required=false, defaultValue="-1") Long id, Model model){

        Book book = new Book();
        if(id!=-1) {
            try {
                book = bookRepository.findById(id).get();
            } catch (EntityNotFoundException ex) {

                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found", ex);
            }
        }

        model.addAttribute("book", book);

        return "book";

    }

    //update


    //delete
    @GetMapping("/delete")
    public String delete(@RequestParam(name="id") Long id){

        try{
           bookRepository.deleteById(id);

        }catch(EntityNotFoundException ex){
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found", ex);

        } catch(EmptyResultDataAccessException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The book does not exist", ex);
        }

        return "redirect:/books";
    }


}

