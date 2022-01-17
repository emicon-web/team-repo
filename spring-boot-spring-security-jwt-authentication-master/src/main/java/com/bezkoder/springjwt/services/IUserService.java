package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.UserDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IUserService {

    void saveUser(User user);

    User saveUser(UserDto userDto);

    User updateUser(UserDto userDto, User user);

    Optional<User> findByUserName(String userName);

    Optional<User> findByUserEmail(String userEmail);

    Optional<User> findByUserNameOrUserEmail(String userName, String userEmail);

    Boolean existsByUserName(String userName);

    Boolean existsByUserEmail(String userEmail);

    List<User> findAll();

    Collection<UserDto> findAll(boolean isDeleted);

    UserDto findById(long id);

    void deleteUser(long id);

    void changeUserPassword(User user, String password);

    Optional<User> getUserByPasswordResetToken(String token);

    void increaseFailedAttempts(User user);

    void blockUser(User user);

    boolean unblockUserWhenTimeExpired(User user);

    boolean updateLastLogoutTime(User user);
}
