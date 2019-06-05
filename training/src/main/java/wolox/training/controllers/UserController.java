package wolox.training.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
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
import org.springframework.web.server.ResponseStatusException;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.exceptions.BookNotFoundException;
import wolox.training.exceptions.NullAttributesException;
import wolox.training.exceptions.OldPasswordMistatchException;
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

    @GetMapping("/username")
    public User currentUserName(Authentication authentication) throws UserNotFoundException {
        User user = userRepository.findFirstByUsername(authentication.getName());

        return user;
    }

    @GetMapping("/")
    public List<User> findAll (Pageable pageable){

        return userRepository.findAllUsers(pageable);
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

        if(user.anyRequiredAttributeNull()){
            throw new NullAttributesException();
        }

        User userToSave = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        userToSave.setName(user.getName());
        userToSave.setUsername(user.getUsername());
        userToSave.setBirthdate(user.getBirthdate());
        userToSave.setBooks(user.getBooks());

        return userRepository.save(user);
    }
  
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) throws NullAttributesException {
        if(user.anyRequiredAttributeNull()){
            throw new NullAttributesException();
        }
        return userRepository.save(user);
    }

    @PutMapping("/editPass/{userId}")
    public User updatePassword(@PathVariable Long userId, @RequestBody String stringParams)
        throws UserNotFoundException, OldPasswordMistatchException, JSONException {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        JSONObject params = new JSONObject(stringParams);

        String oldPass = params.getString("oldPassword");
        String newPass = params.getString("newPassword");

        if(!BCrypt.checkpw(oldPass,user.getPassword())) {
            throw new OldPasswordMistatchException();
        }

        user.setPassword(newPass);

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
    public List<User> getUsersByBirthdateBetweenAndNameContains(@RequestParam(name="fromDate", required = false) String stringFromDate,
        @RequestParam(name="toDate", required = false) String stringToDate, @RequestParam(name="characters", required = false) String characters, Pageable pageable)  {

        LocalDate fromDate = null;
        LocalDate toDate = null;

        try{
            if(stringFromDate!=null) {
                fromDate = LocalDate.parse(stringFromDate);
            }

            if(stringToDate!=null){
                toDate = LocalDate.parse(stringToDate);
            }

            return userRepository.findByBirthdateBetweenAndNameContains(fromDate, toDate, characters, pageable);
        }catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid date", ex);
        }
    }
}
