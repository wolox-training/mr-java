package wolox.training.controllers;


import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.omg.CORBA.INTERNAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import wolox.training.exceptions.BookNotFoundException;
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
        //already existing book

    //readAll
    @GetMapping("/books")
    public String books(Model model){
        List<Book> books = bookRepository.findAll();

        model.addAttribute("books", books);
        return "books";
    }

    //readOne
    @GetMapping("/read/{id}")
    public String read(@PathVariable("id")Long id, Model model){

        try{
        Book book = bookRepository.getOne(id);

        model.addAttribute("name", book.getTitle());

        }catch(EntityNotFoundException ex){

           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found", ex);
        }
        return "book";

    }

    //update


    //delete
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id")Long id){

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

