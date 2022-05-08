package kpi.diploma.ovcharenko.service.user;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.status.BookStatus;
import kpi.diploma.ovcharenko.entity.book.status.Status;
import kpi.diploma.ovcharenko.entity.card.CardStatus;
import kpi.diploma.ovcharenko.entity.card.BookCard;
import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.PasswordResetToken;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import kpi.diploma.ovcharenko.entity.user.UserRole;
import kpi.diploma.ovcharenko.repo.BookCardRepository;
import kpi.diploma.ovcharenko.repo.BookRepository;
import kpi.diploma.ovcharenko.repo.BookStatusRepository;
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
    private final BookCardRepository bookCardRepository;
    private final BookStatusRepository bookStatusRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository resetTokenRepository;

    public LibraryUserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                              BookRepository bookRepository, PasswordResetTokenRepository resetTokenRepository,
                              BookCardRepository bookCardRepository, BookStatusRepository bookStatusRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookRepository = bookRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.bookStatusRepository = bookStatusRepository;
        this.bookCardRepository = bookCardRepository;
    }

    @Override
    public AppUser save(UserModel userModel){
        AppUser user = new AppUser();
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setEmail(userModel.getEmail());
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        user.setRoles(Collections.singletonList(new UserRole("ROLE_USER")));

        return userRepository.save(user);
    }

    @Override
    public void updateUser(Long userId, UserModel userModel) {
        AppUser user = findById(userId);
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setEmail(userModel.getEmail());
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
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
    public AppUser findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
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

    @Transactional
    public void bookedBook(Long id, String userEmail) {
        AppUser user = findByEmail(userEmail);
        Book book = bookRepository.findById(id).get();

        BookCard bookCard = new BookCard();
        bookCard.setBook(book);
        bookCard.setCardStatus(CardStatus.WAIT_FOR_APPROVE);

        user.addBookCard(bookCard);

        List<BookStatus> bookStatuses = bookStatusRepository.findAllByBookId(id);

        for (BookStatus bookStatus: bookStatuses) {
            if (bookStatus.getStatus().equals(Status.FREE)) {
                bookStatus.setStatus(Status.BOOKED);
                book.setStatus(bookStatus);
                break;
            }
        }

        book.setAmount(book.getAmount() - 1);

        userRepository.save(user);
        bookCardRepository.save(bookCard);
    }

    @Override
    public void approveBookForUser(Long bookCardId) {
        BookCard bookCard = bookCardRepository.findBookCardById(bookCardId);
        Book book = bookRepository.findById(bookCard.getBook().getId()).get();

        bookCard.setCardStatus(CardStatus.APPROVED);

        List<BookStatus> bookStatuses = bookStatusRepository.findAllByBookId(book.getId());
        for (BookStatus bookStatus: bookStatuses) {
            if (bookStatus.getStatus().equals(Status.BOOKED)) {
                bookStatus.setStatus(Status.TAKEN);
                book.setStatus(bookStatus);
                break;
            }
        }

        bookCardRepository.save(bookCard);
        bookRepository.save(book);
    }

    @Override
    public void rejectTheBook(Long bookCardId) {
        BookCard bookCard = bookCardRepository.findBookCardById(bookCardId);
        Book book = bookRepository.findById(bookCard.getBook().getId()).get();

        bookCard.setCardStatus(CardStatus.BOOK_RETURNED);

        List<BookStatus> bookStatuses = bookStatusRepository.findAllByBookId(book.getId());
        for (BookStatus bookStatus: bookStatuses) {
            if (bookStatus.getStatus().equals(Status.BOOKED)) {
                bookStatus.setStatus(Status.FREE);
                book.setStatus(bookStatus);
                break;
            }
        }
        book.setAmount(book.getAmount() + 1);

        bookCardRepository.save(bookCard);
        bookRepository.save(book);
    }

    @Override
    public void returnedTheBook(Long bookCardId) {
        BookCard bookCard = bookCardRepository.findBookCardById(bookCardId);
        Book book = bookRepository.findById(bookCard.getBook().getId()).get();

        bookCard.setCardStatus(CardStatus.BOOK_RETURNED);

        List<BookStatus> bookStatuses = bookStatusRepository.findAllByBookId(book.getId());
        for (BookStatus bookStatus: bookStatuses) {
            if (bookStatus.getStatus().equals(Status.TAKEN)) {
                bookStatus.setStatus(Status.FREE);
                book.setStatus(bookStatus);
                break;
            }
        }
        book.setAmount(book.getAmount() + 1);

        bookCardRepository.save(bookCard);
        bookRepository.save(book);
    }

    @Override
    public void deleteBookCard(Long bookCardId) {
        bookCardRepository.deleteById(bookCardId);
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<UserRole> roles){
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
