package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.enums.PartnerTypeEnum;
import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.RoleDto;
import com.bezkoder.springjwt.payload.UserDto;
import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.repository.TokenRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements IUserService {

    @Value("${login.fail.maxAttempts}")
    private Integer maxLoginFailedAttempts;

    @Value("${login.fail.blockTime}")
    private Integer blockTimeDuration;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IRoleService roleService;

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(tokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changeUserPassword(User user, String password) {
        user.setUserCred(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public Optional<User> findByUserEmail(String userEmail) {
        return userRepository.findByUserEmail(userEmail);
    }

    @Override
    public Optional<User> findByUserNameOrUserEmail(String userName, String userEmail) {
        return userRepository.findByUserNameOrUserEmail(userName, userEmail);
    }

    @Override
    public Boolean existsByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Override
    public Boolean existsByUserEmail(String userEmail) {
        return userRepository.existsByUserEmail(userEmail);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Collection<UserDto> findAll(final boolean isDeleted) {
        User user = this.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        List<User> users = null;
        if (user != null && user.getUserType().getId() == PartnerTypeEnum.MERCHANT.getId()) {
            users = userRepository.findAllByIsDeletedAndMerchantId(isDeleted, user.getMerchantId());
        }
//        else {
//            users = userRepository.findAllByIsDeleted(isDeleted);
//        }
        return convertToDto(users);
    }

    @Override
    public UserDto findById(long id) {
        User user = userRepository.findById(id);
        if (user != null) {
            return convertToDto(user);
        }
        return null;
    }

    private UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUserName(user.getUserName());
        userDto.setUserEmail(user.getUserEmail());
        userDto.setUserFirstName(user.getUserFirstName());
        userDto.setUserLastName(user.getUserLastName());
        userDto.setUserCreatedBy(user.getUserCreatedBy());
        userDto.setUserCreatedDate(user.getUserCreatedDate());
        userDto.setUserIsActive(user.getUserIsActive());
        userDto.setUserIsLocked(user.getUserIsLocked());
        userDto.setUserLastSuccessLogin(user.getUserLastSuccessLogin());
        userDto.setUserPhone(user.getUserPhone());
        userDto.setUserUpdatedBy(user.getUserUpdatedBy());
        userDto.setUserUpdatedDate(user.getUserUpdatedDate());
        userDto.setRoles(roleService.convertToDto(user.getRoles()));
        userDto.setUserType(user.getUserType());
        userDto.setMerchantId(user.getMerchantId());
        return userDto;
    }

    public Collection<UserDto> convertToDto(Collection<User> users) {

        if (!Utils.nullOrEmptyCollection(users)) {
            return users.stream()
                    .map(user -> convertToDto(user))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void deleteUser(long id) {
        User user = userRepository.findById(id);
        if (user != null) {
            user.setIsDeleted(true);
            userRepository.save(user);
        }
    }

    @Override
    public User saveUser(UserDto userDto) {
        User user = new User();
        user.setUserName(userDto.getUserName());
        user.setUserEmail(userDto.getUserEmail());
        user.setUserFirstName(userDto.getUserFirstName());
        user.setUserLastName(userDto.getUserLastName());
        user.setUserPhone(userDto.getUserPhone());
        user.setUserCreatedBy(userDto.getUserCreatedBy());
        user.setUserCreatedDate(new Date());
        user.setUserIsActive(userDto.getUserIsActive());
        user.setUserType(PartnerTypeEnum.MERCHANT);

        if (PartnerTypeEnum.MERCHANT.getId() == userDto.getUserType().getId()) {
            user.setMerchantId(userDto.getMerchantId());
        }

        updateRoles(userDto, user);
        return userRepository.save(user);
    }

    @Override
    public void increaseFailedAttempts(User user) {
        user.setUserNoFailedAttempts(user.getUserNoFailedAttempts() != null
                ? user.getUserNoFailedAttempts() + 1 : 1);
        user.setUserLastFailedLogin(new Date());
        user = userRepository.save(user);
    }

    @Override
    public void blockUser(User user) {
        user.setUserIsLocked(Boolean.TRUE);
        user.setUserBlockReleaseTime(Utils.calculateExpiryDate(blockTimeDuration));
        user.setUserNoFailedAttempts(user.getUserNoFailedAttempts() + 1);
        user.setUserLastFailedLogin(new Date());
        userRepository.save(user);
    }

    @Override
    public boolean unblockUserWhenTimeExpired(User user) {
        final Calendar cal = Calendar.getInstance();
        if (user.getUserBlockReleaseTime().before(cal.getTime())) {
            user.setUserIsLocked(Boolean.FALSE);
            user.setUserBlockReleaseTime(null);
            user.setUserLastFailedLogin(new Date());
            user.setUserNoFailedAttempts(0);

            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User updateUser(UserDto userDto, User user) {

        user.setUserFirstName(userDto.getUserFirstName());
        user.setUserEmail(userDto.getUserEmail());
        user.setUserLastName(userDto.getUserLastName());
        user.setUserPhone(userDto.getUserPhone());
        user.setUserUpdatedBy(userDto.getUserUpdatedBy());
        user.setUserUpdatedDate(new Date());
        user.setUserIsActive(userDto.getUserIsActive());
        user.setUserIsLocked(userDto.getUserIsLocked());

        updateRoles(userDto, user);
        return userRepository.save(user);
    }

    private void updateRoles(UserDto userDto, User user) {
        System.out.println(userDto.getRoles());
        if (!Utils.nullOrEmptyCollection(userDto.getRoles())) {
            Set<Role> roles = new HashSet<>();

            for (RoleDto roleDto : userDto.getRoles()) {
                Optional<Role> role = roleRepository.findById(roleDto.getId());
                if (role.isPresent()) {
                    roles.add(role.get());
                }
            }
            user.setRoles(roles);
        }
    }

    @Override
    public boolean updateLastLogoutTime(User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            User exUser = existingUser.get();
            exUser.setUserLastLogoutTime(new Date());
            userRepository.save(exUser);
            return true;
        } else {
            return false;
        }

    }
}
