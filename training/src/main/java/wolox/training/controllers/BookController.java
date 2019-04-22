package wolox.training.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;

@Controller
public class BookController {
    
    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model){

        model.addAttribute("name", name);
        return "greeting";
    }

}

