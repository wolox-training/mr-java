package wolox.training.controllers;

import com.google.gson.JsonObject;
import com.sun.deploy.net.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
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

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/username")
    public User currentUserName(Authentication authentication) throws UserNotFoundException {
        User user = userRepository.findFirstByUsername(authentication.getName());

        if(user==null){
            throw new UserNotFoundException();
        }
        return user;
    }

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

        User actualUser = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        user.setPassword(actualUser.getPassword());

        if(user.anyRequiredAttributeNull()){
            throw new NullAttributesException();
        }

        return userRepository.save(user);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) throws NullAttributesException {
        if(user.anyRequiredAttributeNull()){
            throw new NullAttributesException();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @PutMapping("/editPass/{userId}")
    public User updatePassword(@PathVariable Long userId, @RequestBody User u)
        throws UserNotFoundException {
        String password = u.getPassword();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.setPassword(passwordEncoder.encode(password));

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

        try {
            user.removeBook(book);
        } catch (BookNotFoundException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }

        return userRepository.save(user);
    }

    @GetMapping("/birthdateBetweenAndNameContains")
    public List<User> getUsersByBirthdateBetweenAndNameContains(@RequestBody String stringParams) throws JSONException {
        JSONObject params = new JSONObject(stringParams);

        try {
            LocalDate startDate = LocalDate.parse(params.get("startDate").toString());
            LocalDate finalDate = LocalDate.parse(params.get("finalDate").toString());
            String characters = params.get("characters").toString();
            return userRepository
                .findByBirthdateBetweenAndNameContains(startDate, finalDate, characters);
        } catch (JSONException ex){
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage(), ex);
        } catch (DateTimeParseException ex){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid date", ex);
        }
    }





}
