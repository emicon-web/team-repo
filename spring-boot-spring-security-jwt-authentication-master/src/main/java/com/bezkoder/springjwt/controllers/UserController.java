package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.enums.ActionCommandEnum;
import com.bezkoder.springjwt.enums.PartnerTypeEnum;
import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.CommonReponse;
import com.bezkoder.springjwt.payload.UserDto;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.services.IRoleService;
import com.bezkoder.springjwt.services.IUserService;
import com.bezkoder.springjwt.validators.UserValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Date;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private MessageSource messages;

    @Autowired
    IUserService userService;

    @Autowired
    IRoleService roleService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(
            path = "/list",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('READ_USER')")
    public ResponseEntity getAllUsers() {
        Collection<UserDto> users = userService.findAll(false);
        return ResponseEntity.ok(users);
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER_DETAIL')")
    public ResponseEntity getUser(@PathVariable("id") long id) {
        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
        }
        UserDto userDto = userService.findById(id);
        if (userDto != null) {
            if (user.getUserType() != PartnerTypeEnum.MERCHANT || !user.getMerchantId().equals(userDto.getMerchantId())) {
                logger.error(messages.getMessage("error.message.invalidRequest", null, null));
                return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
            }
            return ResponseEntity.ok(userDto);
        } else {
            throw new GenericCustomException("Error: User do Not Exist.", new Date());
//            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(
            path = "/delete/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('DELETE_USER')")
    public ResponseEntity deleteUser(@PathVariable("id") long id) {
        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
        }
        if (id <= 0) {
            logger.error(messages.getMessage("user.message.userId", null, null));
            return ResponseEntity.badRequest()
                    .body(new CommonReponse("Error: User id is required!"));
        }

        final User existingUser = userRepository.findById(id);
        if (existingUser == null || (user.getUserType() != PartnerTypeEnum.MERCHANT || !user.getMerchantId().equals(existingUser.getMerchantId()))) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
        }

        userService.deleteUser(id);
        return ResponseEntity.ok(new CommonReponse("User Deleted successfully!"));
    }

    @PostMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public ResponseEntity updateUser(@PathVariable("id") long id, @Valid @RequestBody UserDto userReq) {
        final CommonReponse validationError = userValidator.validateUser(userReq, ActionCommandEnum.Update);
        if (validationError != null) {
            logger.error("Update User validation error ", validationError);
            return ResponseEntity.badRequest().body(validationError);
        }

        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
        }
        if (userReq.getUserName() == null && userReq.getUserEmail() == null) {
            logger.error(messages.getMessage("user.message.userInformation", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse("Error: Username or email can not be null!"));
        }

        User existingUser = userService.findByUserNameOrUserEmail(userReq.getUserName(), userReq.getUserEmail())
                .orElseThrow(() -> new RuntimeException("Error: User is not found."));

//		if (!user.getUserIsActive() || Utils.isUserLocked(user)) {
//			return ResponseEntity.badRequest()
//					.body(new CommonReponse("Error: User is not active or user is locked!"));
//		}
        if (user.getUserType() != PartnerTypeEnum.MERCHANT || !user.getMerchantId().equals(existingUser.getMerchantId())) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
        }

        userService.updateUser(userReq, user);
        return ResponseEntity.ok(new CommonReponse("User updated successfully!"));
    }

}
