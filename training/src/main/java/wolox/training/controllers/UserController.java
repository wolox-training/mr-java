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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.exceptions.BookNotFoundException;
import wolox.training.exceptions.NullAttributesException;
import wolox.training.exceptions.UserIdMismatchException;
import wolox.training.exceptions.UserNotFoundException;
import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;

@RequestMapping("/api/users")
@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;


    @Autowired
    BookRepository bookRepository;

    @GetMapping("/")
    public Iterable<User> findAll(){
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User findOne(@PathVariable Long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws UserNotFoundException{
        userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        userRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) throws UserNotFoundException, UserIdMismatchException, NullAttributesException {
        if(!id.equals(user.getId())){
            throw  new UserIdMismatchException();
        }

        userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if(user.anyRequiredAttributeNull()){
            throw new NullAttributesException();
        }

        return userRepository.save(user);
    }
  
    @PostMapping(path="/")
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) throws NullAttributesException {
        if(user.anyRequiredAttributeNull()){
            throw new NullAttributesException();
        }
        return userRepository.save(user);
    }

    @PutMapping("/{userId}/{bookId}")
    public User addBook(@PathVariable("userId") Long userId, @PathVariable("bookId") Long bookId) throws BookNotFoundException, UserNotFoundException, BookAlreadyOwnedException {
        Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        user.addBook(book);

        return userRepository.save(user);
    }

    @DeleteMapping("/{userId}/{bookId}")
    public User removeBook(@PathVariable("userId") Long userId, @PathVariable("bookId") Long bookId) throws BookNotFoundException, UserNotFoundException, BookAlreadyOwnedException {
        Book book = bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        user.removeBook(book);

        return userRepository.save(user);
    }





}
