package be.ttime.core.persistence.service;

import be.ttime.core.error.EmailExistsException;
import be.ttime.core.error.IpLockedException;
import be.ttime.core.persistence.dto.UserDto;
import be.ttime.core.persistence.model.*;
import be.ttime.core.persistence.repository.*;
import be.ttime.core.util.CmsUtils;
import com.mysema.query.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service("userService")
@Transactional
public class UserServiceImpl implements IUserService {

    private static final int MAX_LOGIN_ATTEMPTS = 2;
    private static final int MAX_IP_ATTEMPTS = 4;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    @Qualifier("passwordEncoder")
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IUsersAttemptsRepository usersAttemptsRepository;
    @Autowired
    private IIpAttemptsRespository ipAttemptsRespository;
    @Autowired
    private IVerificationTokenRepository verificationTokenRepository;
    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private IPasswordResetTokenRepository passwordResetTokenRepository;
    @PersistenceContext(unitName = "core")
    private EntityManager entityManager;

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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity user = findByUsernameOrEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Username not found");
        }

        return user;
    }

    @Override
    public UserEntity findById(Long id) {
        return userRepository.findOne(id);
    }

    @Override
    @Cacheable(value = "user", key = "#email")
    public UserEntity findByUsernameOrEmail(String email) {

        JPAQuery query = new JPAQuery(entityManager);
        QUserEntity qUserEntity = QUserEntity.userEntity;
        QGroupEntity qGroupEntity = QGroupEntity.groupEntity;
        UserEntity user = query
                .from(qUserEntity)
                .leftJoin(qUserEntity.groups, qGroupEntity).fetch()
                .leftJoin(qGroupEntity.roles).fetch()
                .where(qUserEntity.email.eq(email)).singleResult(qUserEntity);
        return user;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#user.email"),
    })
    public UserEntity save(UserEntity user) {
        UserEntity editedUser = userRepository.save(user);
        if(CmsUtils.userIsLogged(sessionRegistry, editedUser)){
            CmsUtils.updateSessionUser(editedUser);
        }
        return editedUser;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#user.email"),
    })
    public void delete(UserEntity user) {

        CmsUtils.expireSession(sessionRegistry, user);
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

    @Override
    public String jsonAdminContent() {

        List<UserEntity> userEntityList = userRepository.findByOrderByEnabledDescLastNameAsc();
        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;

        for (UserEntity u : userEntityList) {
            row = Json.createObjectBuilder();
            row.add("DT_RowData", Json.createObjectBuilder().add("id", u.getId()));
            row.add("name", CmsUtils.emptyStringIfnull(u.getLastName()));
            row.add("firstname", CmsUtils.emptyStringIfnull(u.getFirstName()));
            row.add("email",  CmsUtils.emptyStringIfnull(u.getEmail()));
            row.add("active", u.isEnabled());
            row.add("locked", !u.isAccountNonLocked());
            StringBuilder rolesBuilder = new StringBuilder();
            /*final Collection<? extends GrantedAuthority> authorities = u.getAuthorities();

            for (GrantedAuthority authority : authorities) {
                if (roles.length() != 0) {
                    roles.append(" ,");
                }
                roles.append(authority.getAuthority().replace("_PRIVILEGE", ""));
            }*/
            Collection<GroupEntity> roles = u.getGroups();
            for (GroupEntity role : roles) {
                if (rolesBuilder.length() != 0) {
                    rolesBuilder.append(" ,");
                }
                rolesBuilder.append(role.getName().replace("ROLE_", ""));

            }
            row.add("role", CmsUtils.emptyStringIfnull(rolesBuilder.toString()));



            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }

}