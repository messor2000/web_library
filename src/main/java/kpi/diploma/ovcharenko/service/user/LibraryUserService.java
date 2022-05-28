package kpi.diploma.ovcharenko.service.user;

import kpi.diploma.ovcharenko.config.PasswordEncoder;
import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.status.BookStatus;
import kpi.diploma.ovcharenko.entity.book.status.Status;
import kpi.diploma.ovcharenko.entity.card.BookCard;
import kpi.diploma.ovcharenko.entity.card.CardStatus;
import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.PasswordResetToken;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import kpi.diploma.ovcharenko.entity.user.UserRole;
import kpi.diploma.ovcharenko.entity.user.VerificationToken;
import kpi.diploma.ovcharenko.exception.BookDoesntPresentException;
import kpi.diploma.ovcharenko.repo.BookRepository;
import kpi.diploma.ovcharenko.repo.BookStatusRepository;
import kpi.diploma.ovcharenko.repo.PasswordResetTokenRepository;
import kpi.diploma.ovcharenko.repo.UserRepository;
import kpi.diploma.ovcharenko.repo.VerificationTokenRepository;
import kpi.diploma.ovcharenko.service.amazon.AmazonClient;
import kpi.diploma.ovcharenko.service.book.cards.BookCardService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class LibraryUserService implements UserService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookCardService bookCardService;
    private final BookStatusRepository bookStatusRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final AmazonClient amazonClient;

    public LibraryUserService(UserRepository userRepository, BookRepository bookRepository, PasswordResetTokenRepository resetTokenRepository,
                              BookCardService bookCardService, BookStatusRepository bookStatusRepository, AmazonClient amazonClient,
                              VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.bookStatusRepository = bookStatusRepository;
        this.bookCardService = bookCardService;
        this.amazonClient = amazonClient;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                mapRolesToAuthorities(user.getRoles()));
    }

    @Override
    public AppUser save(UserModel userModel) {
        AppUser user = createUser(userModel);

        return userRepository.save(user);
    }

    @Override
    public void createNewUserByAdmin(UserModel userModel) {
        AppUser user = createUser(userModel);
        user.setEnabled(true);

        userRepository.save(user);
    }

    @Override
    public void saveRegisteredUser(AppUser user) {
        userRepository.save(user);
    }

    @Override
    public void updateUser(Long userId, UserModel userModel, boolean flag) {
        AppUser user = findById(userId);
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setEmail(userModel.getEmail());
        user.setTelephoneNumber(userModel.getTelephoneNumber());

        if (flag && !user.getRoles().contains(new UserRole("ROLE_ADMIN"))) {
            user.setRoles(Collections.singletonList(new UserRole("ROLE_ADMIN")));
            user.setAllPrivileges(true);
        }

        userRepository.save(user);
    }

    @Override
    public void changePhotoImage(MultipartFile file, String email) {
        amazonClient.changeUserImage(file, email);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        List<BookCard> bookCards = bookCardService.findAllUserBookCards(userId);
        log.trace(bookCards);
        PasswordResetToken passwordResetToken = resetTokenRepository.findByUserId(userId);
        log.trace(passwordResetToken);
        VerificationToken verificationToken = verificationTokenRepository.findByUserId(userId);
        log.trace(verificationToken);

        if (!bookCards.isEmpty()) {
            bookCardService.deleteBookCardByUserId(userId);
        }

        if (passwordResetToken != null) {
            resetTokenRepository.deletePasswordResetTokenById(passwordResetToken.getId());
        }

        if (verificationToken != null) {
            verificationTokenRepository.deleteVerificationTokenById(verificationToken.getId());
        }

        userRepository.deleteUserById(userId);
    }

    @Override
    public AppUser findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public AppUser findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
    }

    @Override
    @Transactional
    public void createVerificationTokenForUser(final AppUser user, final String token) {
        final VerificationToken myToken = new VerificationToken(token, user);
        verificationTokenRepository.save(myToken);
    }

    @Override
    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    @Override
    public void createPasswordResetTokenForUser(AppUser user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        resetTokenRepository.save(myToken);
    }

    @Override
    public void changeUserPassword(AppUser user, String password) {
        user.setPassword(PasswordEncoder.passwordEncoder().encode(password));
        userRepository.save(user);
    }

    @Override
    public Optional<AppUser> getUserByPasswordResetToken(final String token) {
        return Optional.ofNullable(resetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public boolean checkIfValidOldPassword(final AppUser user, final String oldPassword) {
        return PasswordEncoder.passwordEncoder().matches(oldPassword, user.getPassword());
    }

    @Override
    public List<AppUser> showAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void bookedBook(Long id, String userEmail) {
        AppUser user = findByEmail(userEmail);
        Book book = bookRepository.findById(id).orElseThrow(() ->
                new BookDoesntPresentException("Book with this id doesnt exist"));

        BookCard bookCard = new BookCard();
        bookCard.setBook(book);
        bookCard.setCardStatus(CardStatus.WAIT_FOR_APPROVE);

        user.addBookCard(bookCard);

        List<BookStatus> bookStatuses = bookStatusRepository.findAllByBookId(id);

        for (BookStatus bookStatus : bookStatuses) {
            if (bookStatus.getStatus().equals(Status.FREE)) {
                bookStatus.setStatus(Status.BOOKED);
                break;
            }
        }

        book.setAmount(book.getAmount() - 1);

        userRepository.save(user);
        bookCardService.saveBookCard(bookCard);
    }

    @Override
    @Transactional
    public void approveBookForUser(Long bookCardId) {
        BookCard bookCard = bookCardService.findBookCardById(bookCardId);
        Book book = bookRepository.findById(bookCard.getBook().getId()).orElseThrow(() ->
                new BookDoesntPresentException("Book with this id doesnt exist"));

        bookCard.setCardStatus(CardStatus.APPROVED);

        List<BookStatus> bookStatuses = bookStatusRepository.findAllByBookId(book.getId());
        for (BookStatus bookStatus : bookStatuses) {
            if (bookStatus.getStatus().equals(Status.BOOKED)) {
                bookStatus.setStatus(Status.TAKEN);
                break;
            }
        }

        bookCardService.saveBookCard(bookCard);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void rejectTheBook(Long bookCardId) {
        BookCard bookCard = bookCardService.findBookCardById(bookCardId);
        Book book = bookRepository.findById(bookCard.getBook().getId()).orElseThrow(() ->
                new BookDoesntPresentException("Book with this id doesnt exist"));

        bookCard.setCardStatus(CardStatus.REJECT);

        List<BookStatus> bookStatuses = bookStatusRepository.findAllByBookId(book.getId());


        for (BookStatus bookStatus : bookStatuses) {
            if (bookStatus.getStatus().equals(Status.BOOKED)) {
                bookStatus.setStatus(Status.FREE);
                break;
            }
        }


        book.setAmount(book.getAmount() + 1);

        bookCardService.saveBookCard(bookCard);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void returnTheBook(Long bookCardId) {
        BookCard bookCard = bookCardService.findBookCardById(bookCardId);
        Book book = bookRepository.findById(bookCard.getBook().getId()).orElseThrow(() ->
                new BookDoesntPresentException("Book with this id doesnt exist"));

        bookCard.setCardStatus(CardStatus.BOOK_RETURNED);

        List<BookStatus> bookStatuses = bookStatusRepository.findAllByBookId(book.getId());
        for (BookStatus bookStatus : bookStatuses) {
            if (bookStatus.getStatus().equals(Status.TAKEN)) {
                bookStatus.setStatus(Status.FREE);
                break;
            }
        }

        book.setAmount(book.getAmount() + 1);

        bookCardService.saveBookCard(bookCard);
        bookRepository.save(book);
    }

    @Override
    public void deleteBookCard(Long bookCardId) {
        bookCardService.deleteBookCardByBookCardId(bookCardId);
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<UserRole> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    private AppUser createUser(UserModel userModel) {
        AppUser user = new AppUser();
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setEmail(userModel.getEmail());
        user.setTelephoneNumber(userModel.getTelephoneNumber());
        user.setPassword(PasswordEncoder.passwordEncoder().encode(userModel.getPassword()));
        user.setRoles(Collections.singletonList(new UserRole("ROLE_USER")));
        user.setAllPrivileges(false);

        return user;
    }
}
