package wolox.training.security;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import wolox.training.models.User;
import wolox.training.repositories.UserRepository;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {


    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userRepository.findFirstByUsername(username);

        if (user!=null && BCrypt.checkpw(password, user.getPassword())) {

            return new UsernamePasswordAuthenticationToken(
                username, password, new ArrayList<>());
        } else {
            throw new BadCredentialsException("Wrong user or password.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }


}



