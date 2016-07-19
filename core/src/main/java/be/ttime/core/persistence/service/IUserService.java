package be.ttime.core.persistence.service;

import be.ttime.core.error.EmailExistsException;
import be.ttime.core.error.IpLockedException;
import be.ttime.core.persistence.dto.UserDto;
import be.ttime.core.persistence.model.PasswordResetTokenEntity;
import be.ttime.core.persistence.model.UserAttemptsEntity;
import be.ttime.core.persistence.model.UserEntity;
import be.ttime.core.persistence.model.VerificationTokenEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
    UserEntity save(UserEntity user);

    void delete(Long id);

    void delete(UserEntity user);

    void updateFailUserAttempts(String username);

    void updateFailIpAttempts(String ip);

    void checkIpAttempts(String ip) throws IpLockedException;

    void resetFailAttempts(String username);

    UserAttemptsEntity getUserAttempts(String username);

    UserEntity registerNewUserAccount(UserDto accountDto) throws EmailExistsException;

    UserEntity getUserByVerificationToken(String verificationToken);

    void createVerificationTokenForUser(UserEntity user, String token);

    VerificationTokenEntity getVerificationToken(String verificationToken);

    VerificationTokenEntity generateNewVerificationToken(String token);

    void createPasswordResetTokenForUser(UserEntity user, String token);

    UserEntity findByUsernameOrEmail(String email);

    PasswordResetTokenEntity getPasswordResetToken(String token);

    UserEntity getUserByPasswordResetToken(String token);

    UserEntity getUserByID(long id);

    void changeUserPassword(UserEntity user, String password);

    boolean checkIfValidOldPassword(UserEntity user, String password);

    String jsonAdminContent();

    UserEntity findById(Long id);
}
