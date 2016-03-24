package be.ttime.core.persistence.service;

import be.ttime.core.persistence.dao.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
    UserEntity save(UserEntity user);
}
