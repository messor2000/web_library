package kpi.diploma.ovcharenko.service.user;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.PasswordResetToken;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import kpi.diploma.ovcharenko.entity.user.UserRole;
import kpi.diploma.ovcharenko.repo.BookRepository;
import kpi.diploma.ovcharenko.repo.PasswordResetTokenRepository;
import kpi.diploma.ovcharenko.repo.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LibraryUserService implements UserService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository resetTokenRepository;

    public LibraryUserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                              BookRepository bookRepository, PasswordResetTokenRepository resetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookRepository = bookRepository;
        this.resetTokenRepository = resetTokenRepository;
    }

    @Override
    public AppUser save(UserModel registration){
        AppUser user = new AppUser();
        user.setFirstName(registration.getFirstName());
        user.setLastName(registration.getLastName());
        user.setEmail(registration.getEmail());
        user.setPassword(passwordEncoder.encode(registration.getPassword()));
        user.setRoles(Collections.singletonList(new UserRole("ROLE_USER")));
        return userRepository.save(user);
    }

    @Override
    public void deleteUserByEmail(String email) {
        userRepository.deleteAppUserByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(email);
        if (user == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    @Override
    public AppUser findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(AppUser user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        resetTokenRepository.save(myToken);
    }

    @Override
    public void changeUserPassword(AppUser user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public Optional<AppUser> getUserByPasswordResetToken(final String token) {
        return Optional.ofNullable(resetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public boolean checkIfValidOldPassword(final AppUser user, final String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public List<AppUser> showAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void takeBook(Long id, String userEmail) {
        AppUser user = findByEmail(userEmail);
        Book book = bookRepository.findById(id).get();

        book.setAmount(book.getAmount() - 1);
        if (book.getAmount() == 0) {
            book.setBookStatus("used");
        }

        user.addBook(book);

        userRepository.save(user);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void returnBook(Long id, String userEmail) {
        AppUser user = findByEmail(userEmail);
        Book book = bookRepository.findById(id).get();

        book.setAmount(book.getAmount() + 1);
        book.setBookStatus("unused");

        user.removeBook(book);

        userRepository.save(user);
        bookRepository.save(book);
    }



    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<UserRole> roles){
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
