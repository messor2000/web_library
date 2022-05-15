package kpi.diploma.ovcharenko.service.user;

import kpi.diploma.ovcharenko.entity.user.PasswordResetToken;
import kpi.diploma.ovcharenko.repo.PasswordResetTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Slf4j
@Service
public class LibrarySecurityService implements SecurityService {

    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final LibraryUserService userService;

    public LibrarySecurityService(AuthenticationManager authenticationManager, LibraryUserService userService,
                                  PasswordResetTokenRepository resetTokenRepository) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.resetTokenRepository = resetTokenRepository;
    }

    @Override
    public String findLoggedInEmail() {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (userDetails instanceof UserDetails) {
            return ((UserDetails)userDetails).getUsername();
        }

        return null;
    }

    @Override
    public void autoLogin(String email, String password) {
        log.info(password);
        UserDetails userDetails = userService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            log.debug(String.format("Auto login %s successfully!", email));
        }
    }

    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = resetTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }
}
