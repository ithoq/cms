package be.ttime.core.persistence.service;

import be.ttime.core.error.EmailExistsException;
import be.ttime.core.error.IpLockedException;
import be.ttime.core.persistence.dto.UserDto;
import be.ttime.core.persistence.model.*;
import be.ttime.core.persistence.repository.*;
import be.ttime.core.util.CmsUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;


@Service("userService")
@Transactional
public class UserServiceImpl implements IUserService {

    private static final int MAX_LOGIN_ATTEMPTS = 2;
    private static final int MAX_IP_ATTEMPTS = 4;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IUsersAttemptsRepository usersAttemptsRepository;
    @Autowired
    private IIpAttemptsRespository ipAttemptsRespository;
    @Autowired
    private IVerificationTokenRepository verificationTokenRepository;
    @Autowired
    private IPasswordResetTokenRepository passwordResetTokenRepository;

    /*

    	 * @param username the username presented to the
	 * <code>DaoAuthenticationProvider</code>
	 * @param password the password that should be presented to the
	 * <code>DaoAuthenticationProvider</code>
	 * @param enabled set to <code>true</code> if the user is enabled
	 * @param accountNonExpired set to <code>true</code> if the account has not expired
	 * @param credentialsNonExpired set to <code>true</code> if the credentials have not
	 * expired
	 * @param accountNonLocked set to <code>true</code> if the account is not locked
	 * @param authorities the authorities that should be granted to the caller if they
	 * presented the correct username and password and the user is enabled. Not null.
	 *
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.print(username);
        UserEntity user = findByUsernameOrEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username not found");
        }

        // force eager loading.
        user.getRoles();

        return user;
    }

    @Override
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    @Override
    public void delete(UserEntity user) {
        userRepository.delete(user);
    }

    @Override
    @Transactional(noRollbackFor = {LockedException.class})
    public void updateFailUserAttempts(String username) {

        UserEntity user = findByUsernameOrEmail(username);

        if (user != null) {
            UserAttemptsEntity userAttemptsEntity = usersAttemptsRepository.findByUsername(user.getUsername());

            if (userAttemptsEntity == null) {
                usersAttemptsRepository.save(new UserAttemptsEntity(0,user.getUsername(), 1 ,new Date()));
            } else {
                userAttemptsEntity.setAttempts(userAttemptsEntity.getAttempts() + 1);
                userAttemptsEntity.setLastModified(new Date());
                usersAttemptsRepository.save(userAttemptsEntity);

                if (userAttemptsEntity.getAttempts() >= MAX_LOGIN_ATTEMPTS) {
                    userRepository.updateAccountLocked(false, user.getUsername());
                    throw new LockedException("User is disabled");
                }
            }
        }
    }

    @Override
    @Transactional(noRollbackFor = {LockedException.class})
    public void checkIpAttempts(String ip) throws IpLockedException{

        LocalDateTime dateTime = LocalDateTime.now();
        dateTime = dateTime.minusDays(1L);
        Date test = CmsUtils.LocalDateTimeToDate(dateTime);
        Long tentative = ipAttemptsRespository.countByIpAndDateAfter(ip, test);
        if(tentative >= (MAX_IP_ATTEMPTS - 1)){
            throw new IpLockedException(test.toString());
        }
        /*
        ipAttemptsRepository.save(ipAttemptsEntity);

        if(ipAttemptsEntity.getAttempts() >= MAX_IP_ATTEMPTS){
            throw new LockedException("ip blocked");
        }*/
    }

    @Override
    public void updateFailIpAttempts(String ip) {
        ipAttemptsRespository.save(new IpAttemptsEntity(0, ip, new Date()));
    }

    @Override
    public void resetFailAttempts(String username) {
        usersAttemptsRepository.resetAttempts(username);
    }

    @Override
    public UserAttemptsEntity getUserAttempts(String username) {
        return usersAttemptsRepository.findByUsername(username);
    }

    @Override
    public UserEntity registerNewUserAccount(UserDto accountDto) throws EmailExistsException {
        if (emailExist(accountDto.getEmail())) {
            throw new EmailExistsException("There is an account with that email address: " + accountDto.getEmail());
        }
        final UserEntity user = new UserEntity();

        user.setFirstName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
        user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        user.setEmail(accountDto.getEmail());

        //user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));
        return userRepository.save(user);
    }

    @Override
    public UserEntity getUserByVerificationToken(String verificationToken) {

        return verificationTokenRepository.findByToken(verificationToken).getUser();
    }

    @Override
    public VerificationTokenEntity getVerificationToken(String verificationToken) {

        return verificationTokenRepository.findByToken(verificationToken);
    }


    @Override
    public void createVerificationTokenForUser(UserEntity user, String token) {
        final VerificationTokenEntity myToken = new VerificationTokenEntity(token, user);
        verificationTokenRepository.save(myToken);
    }

    @Override
    public VerificationTokenEntity generateNewVerificationToken(String token) {
        VerificationTokenEntity vToken = verificationTokenRepository.findByToken(token);
        vToken.updateToken(UUID.randomUUID().toString());
        vToken = verificationTokenRepository.save(vToken);
        return vToken;
    }

    @Override
    public void createPasswordResetTokenForUser(UserEntity user, String token) {
        final PasswordResetTokenEntity myToken = new PasswordResetTokenEntity(token, user);
        passwordResetTokenRepository.save(myToken);
    }

    @Override
    public UserEntity findByUsernameOrEmail(String user) {
        return userRepository.findByEmailOrUsername(user, user);
    }

    @Override
    public PasswordResetTokenEntity getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public UserEntity getUserByPasswordResetToken(String token) {

        return passwordResetTokenRepository.findByToken(token).getUser();
    }

    @Override
    public UserEntity getUserByID(long id) {

        return userRepository.findOne(id);
    }

    @Override
    public void changeUserPassword(UserEntity user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(UserEntity user, String password) {

        return passwordEncoder.matches(password, user.getPassword());
    }

    private boolean isUserExists(String username) {

        boolean result = false;

        long count = userRepository.countByUsername(username);
        if (count > 0) {
            result = true;
        }

        return result;
    }

    private boolean emailExist(final String email) {
        final UserEntity user = userRepository.findByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }

}
