package wolox.training.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.exceptions.BookNotFoundException;
import wolox.training.exceptions.UserIdMismatchException;
import wolox.training.exceptions.UserNotFoundException;
import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;


    @Autowired
    BookRepository bookRepository;

    @GetMapping("/users")
    public Iterable<User> findAll(){
        return userRepository.findAll();
    }

    @GetMapping("/user/{id}")
    public User findOne(@PathVariable Long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @DeleteMapping("/user/{id}")
    public void delete(@PathVariable Long id) throws UserNotFoundException{
        userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        userRepository.deleteById(id);
    }

    @PutMapping("/user/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) throws UserNotFoundException, UserIdMismatchException {
        if(id != user.getId()){
            throw  new UserIdMismatchException();
        }

        userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return userRepository.save(user);
    }

    @PostMapping(path="/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user){
        return userRepository.save(user);
    }

    @PutMapping("/user/{userId}/{bookId}")
    public User addBook(@PathVariable("userId") Long userId, @PathVariable("bookId") Long bookId) throws BookNotFoundException, UserNotFoundException, BookAlreadyOwnedException {
        Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        user.addBook(book);

        return userRepository.save(user);
    }

    @DeleteMapping("/user/{userId}/{bookId}")
    public User removeBook(@PathVariable("userId") Long userId, @PathVariable("bookId") Long bookId) throws BookNotFoundException, UserNotFoundException, BookAlreadyOwnedException {
        Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        user.removeBook(book);

        return userRepository.save(user);
    }





}
