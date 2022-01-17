package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.enums.ActionCommandEnum;
import com.bezkoder.springjwt.enums.PartnerTypeEnum;
import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.mail.EmailService;
import com.bezkoder.springjwt.mail.Mail;
import com.bezkoder.springjwt.models.Token;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.CommonReponse;
import com.bezkoder.springjwt.payload.JwtResponse;
import com.bezkoder.springjwt.payload.LoginDto;
import com.bezkoder.springjwt.payload.ResetPasswordDto;
import com.bezkoder.springjwt.payload.UserDto;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.jwt.JwtUtils;
import com.bezkoder.springjwt.security.services.UserDetailsImpl;
import com.bezkoder.springjwt.services.ITokenService;
import com.bezkoder.springjwt.services.IUserService;
import com.bezkoder.springjwt.services.MerchantService;
import com.bezkoder.springjwt.utils.AppConstants;
import com.bezkoder.springjwt.utils.Utils;
import com.bezkoder.springjwt.validators.UserValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private ITokenService tokenService;

    @Autowired
    private IUserService userService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private Environment env;

    @Value("${login.fail.maxAttempts}")
    private Integer maxLoginFailedAttempts;

    @Value("${login.fail.blockTime}")
    private Integer blockTimeDuration;

    @Value("${password.reset.url}")
    private String passwordResetUrl;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(
            path = "/signin",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity authenticateUser(@Valid @RequestBody LoginDto loginDto) {

        User user = null;
        try {
            user = userService.findByUserName(loginDto.getUserName())
                    .orElseThrow(() -> new RuntimeException(messages.getMessage("auth.message.badCredentials", null, null)));
        } catch (Exception e) {
            logger.error(messages.getMessage("auth.message.badCredentials", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse(messages.getMessage("auth.message.badCredentials", null, null)));
        }

        if (user.getUserType() != PartnerTypeEnum.MERCHANT) {
            return ResponseEntity.badRequest().body(new CommonReponse("Error: User is not found."));
        }
        if (!user.getUserIsActive() || Utils.isUserLocked(user)) {
            logger.error(messages.getMessage("auth.message.authenticateuser", null, null));
            return ResponseEntity.badRequest()
                    .body(new CommonReponse("Account blocked for " + blockTimeDuration + " Minutes, " +
                            "After " + blockTimeDuration + " Minutes it will be unblocked"));
        }

        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUserName(), loginDto.getUserCred()));
        } catch (BadCredentialsException e) {
            if (user.getUserNoFailedAttempts() == null
                    || user.getUserNoFailedAttempts() < (maxLoginFailedAttempts - 1)) {
                userService.increaseFailedAttempts(user);
                logger.error(messages.getMessage("auth.message.badCredentials", null, null));
                return ResponseEntity.badRequest()
                        .body(new CommonReponse(messages.getMessage("auth.message.badCredentials", null, null)));
                // throw new BadCredentialsException(messages.getMessage("auth.message.badCredentials", null, null));
            } else {
                userService.blockUser(user);
                logger.error(messages.getMessage("auth.message.userattempts", null, null)
                        + " It will be unlocked after " + blockTimeDuration + " Minutes");
                return ResponseEntity.badRequest()
                        .body(new CommonReponse("Your account has been blocked due to 3 failed attempts."
                                + " It will be unlocked after " + blockTimeDuration + " Minutes"));
//                throw new BadCredentialsException("Your account has been blocked due to 3 failed attempts."
//                        + " It will be unlocked after " + blockTimeDuration + " Minutes");
            }
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> actions = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // updating necessary fields on successful login
        user.setUserLastSuccessLogin(new Date());
        user.setUserBlockReleaseTime(null);
        user.setUserIsLocked(false);
        user.setUserNoFailedAttempts(0);
        userService.saveUser(user);
        logger.info(messages.getMessage("auth.message.updateuser", null, null));
//		System.out.println("User successfully updated on login!");
        System.out.println(userDetails);
        String merchantName = null;
        String shortCode = null;
        String merchantType = null;
        if (!Utils.nullOrEmptyString(userDetails.getMerchantId())) {
            merchantName = merchantService.getMerchantName(userDetails.getMerchantId());
            shortCode = merchantService.getMerchantShortCode(userDetails.getMerchantId());
            merchantType= merchantService.getMerchantType(userDetails.getMerchantId());
        }
        return ResponseEntity.ok(
                new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(),
                        userDetails.getUserRoles(), userDetails.getMerchantId(), actions,
                        userDetails.getUserType().name(), merchantName, shortCode,merchantType)
        );
    }

    @PostMapping(
            path = "/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    @PreAuthorize("hasAuthority('ADD_USER')")
    public ResponseEntity registerUser(final HttpServletRequest request,
                                       @Valid @RequestBody UserDto userDto) {
        User loggedInUser = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (loggedInUser == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
        }
        userDto.setMerchantId(loggedInUser.getMerchantId());
        final CommonReponse validationError = userValidator.validateUser(userDto, ActionCommandEnum.Create);
        if (validationError != null) {
            logger.error("Create User validation error ", validationError);
            return ResponseEntity.badRequest().body(validationError);
        }

        if (userService.existsByUserName(userDto.getUserName())) {
            logger.error(messages.getMessage("auth.message.username", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse("Error: Username is already taken!"));
        }

        if (userService.existsByUserEmail(userDto.getUserEmail())) {
            logger.error(messages.getMessage("auth.message.useremail", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse("Error: Email is already in use!"));
        }

        if (PartnerTypeEnum.MERCHANT.getId() == userDto.getUserType().getId()
                && !merchantService.existsByMerchantId(userDto.getMerchantId())) {
            logger.error(messages.getMessage("auth.message.merchant", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse("Error: Merchant not found."));
        }

        User user = userService.saveUser(userDto);

        final String tokenId = Utils.generateToken();
        final Integer tokencred = Utils.generatePasscode();
        final Token token = tokenService.createGeneratePasswordTokenForUser(user, tokenId, tokencred);

        String expiryTime = Integer.parseInt(env.getProperty("token.expire.generateTime")) / 60 + " Hours";
        Mail mail = getMailData(request, user, token, messages.getMessage("message.generatePassword", null, null),
                messages.getMessage("message.generateType", null, null), expiryTime);
//        try {
//            emailService.sendEmail(mail, AppConstants.RESET_PASSWORD_TEMPLATE);
//        } catch (Exception e) {
//            logger.error(messages.getMessage("auth.message.mailfailed", null, null));
//            throw new GenericCustomException("Error: Email failed.", new Date());
//        }
        logger.info(messages.getMessage("auth.message.mailsent", null, null));
        //System.out.println("Create password mail sent successfully");
        return ResponseEntity.ok(new CommonReponse("User registered successfully!"));
    }

    private Mail getMailData(final HttpServletRequest request, User user, final Token token, final String subject,
                             final String type, final String expiryTime) {
        Mail mail = new Mail();
        mail.setFrom(env.getProperty("support.email"));

        mail.setMailTo(user.getUserEmail());// replace with your desired email
        mail.setSubject(subject);

        Map<String, Object> model = new HashMap<>();
        model.put("firstName", user.getUserFirstName());
        model.put("lastName", user.getUserLastName());
        model.put("link", passwordResetUrl + token.getToken());
        model.put("passcode", token.getTokenCred().toString());
        model.put("type", type);
        model.put("expiryTime", expiryTime);
        mail.setProps(model);
        return mail;
    }

    @PostMapping(
            path = "/forgot-password",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity forgotPassword(final HttpServletRequest request,
                                         @RequestParam("email") final String userEmail) {
        if (userEmail == null) {
            logger.error(messages.getMessage("auth.message.requiredmail", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse("Error: User email is required!"));
        }
        User user = userService.findByUserEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("If the information entered is associated with an account we" +
                        " have sent you an email with password reset instructions."));

        if (!user.getUserIsActive() || Utils.isUserLocked(user)) {
            logger.error(messages.getMessage("auth.message.userInfo", null, null));
            return ResponseEntity.badRequest()
                    .body(new CommonReponse("Error: User is not active or user is locked!"));
        }

        if (user != null) {
            final String tokenId = Utils.generateToken();
            final Integer tokencred = Utils.generatePasscode();
            Token token = tokenService.createResetPasswordTokenForUser(user, tokenId, tokencred);

            String expiryTime = Integer.parseInt(env.getProperty("token.expire.resetTime")) + " Minutes";
            Mail mail = getMailData(request, user, token, messages.getMessage("message.resetPassword", null, null),
                    messages.getMessage("message.resetType", null, null), expiryTime);
            try {
                emailService.sendEmail(mail, AppConstants.RESET_PASSWORD_TEMPLATE);
            } catch (Exception e) {
                logger.error(messages.getMessage("auth.message.mailfailed", null, null));
//				throw new RuntimeException("Error: Email failed.");
                throw new GenericCustomException("Error: Email failed.", new Date());
            }
            //System.out.println("Forgot password mail sent successfully");
            logger.info(messages.getMessage("auth.message.forgotpassword", null, null));
        }
        return ResponseEntity.ok(new CommonReponse("User forgot password mail sent successfully!"));
    }

    @PostMapping(
            path = "/reset-password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity resetPassword(final HttpServletRequest request,
                                        @Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        if (resetPasswordDto == null) {
            logger.error(messages.getMessage("auth.message.requiredFields", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse("Error: Required fields can not be empty!"));
        }
        User user = userService.findByUserEmail(resetPasswordDto.getUserEmail())
                .orElseThrow(() -> new RuntimeException("Error: User is not found."));

        if (!user.getUserIsActive() || Utils.isUserLocked(user)) {
            logger.error(messages.getMessage("auth.message.userInfo", null, null));
            return ResponseEntity.badRequest()
                    .body(new CommonReponse("Error: User is not active or user is locked!"));
        }

        if (user != null) {
            final String result = tokenService.validatePasswordResetToken(resetPasswordDto.getToken(),
                    resetPasswordDto.getTokencred());
            if (result != null) {
                return ResponseEntity.badRequest()
                        .body(new CommonReponse(messages.getMessage("auth.message." + result, null, null)));
            }

            if (!resetPasswordDto.getUserCred().equals(resetPasswordDto.getConfirmuserCred())) {
                logger.error(messages.getMessage("auth.message.passconfirmpass", null, null));
                return ResponseEntity.badRequest()
                        .body(new CommonReponse("Error: Password and confirm passoword does not match!"));
            }

            Optional<User> mainUser = userService.getUserByPasswordResetToken(resetPasswordDto.getToken());
            if (mainUser.isPresent()) {
                userService.changeUserPassword(mainUser.get(), resetPasswordDto.getUserCred());
                return ResponseEntity.ok(new CommonReponse(messages.getMessage("message.resetPasswordSuccessfully", null, null)));
            }
        }
        return ResponseEntity.badRequest().body(new CommonReponse(messages.getMessage("auth.message.invalidUser", null, null)));
    }

    @GetMapping(
            path = "/signout/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity signout(@PathVariable("username") String username, final HttpServletRequest request) {
        final String jwt = JwtUtils.parseJwt(request);
        String un = null;
        try {
            if (Utils.nullOrEmptyString(jwt)) {
                if (Utils.nullOrEmptyString(username)) {
                    logger.error(messages.getMessage("error.message.invalidRequest", null, null));
                    return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
                }
            } else {
                un = JwtUtils.getUserNameFromJwt(JwtUtils.parseJwt(request));
            }
        } catch (Exception e) {
            logger.error(" Invalid token, parsing error while getting username {}", e.getMessage());
            if (Utils.nullOrEmptyString(username)) {
                logger.error(messages.getMessage("error.message.invalidRequest", null, null));
                return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
            }
        }

        User user = userRepository.findByUserName(Utils.nullOrEmptyString(un) ? username : un).get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
        }
        boolean updated = userService.updateLastLogoutTime(user);
        if (updated) {
            return ResponseEntity.ok(new CommonReponse(messages.getMessage("message.signoutSuccessfully", null, null)));
        } else {
            return ResponseEntity.badRequest().body(new CommonReponse(messages.getMessage("user.signout.error", null, null)));
        }
    }

}
