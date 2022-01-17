package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.entity.MerchantConfiguration;
import com.bezkoder.springjwt.enums.ActionCommandEnum;
import com.bezkoder.springjwt.enums.VirtualAccountStatusEnum;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.CommonReponse;
import com.bezkoder.springjwt.payload.VirtualAccountDto;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.services.MerchantConfigurationService;
import com.bezkoder.springjwt.services.VirtualAccountService;
import com.bezkoder.springjwt.utils.FileServiceUtil;
import com.bezkoder.springjwt.utils.Utils;
import com.bezkoder.springjwt.validators.VirtualAccountValidator;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/virtualaccounts")
public class VirtualAccountController {

    private static final Logger logger = LogManager.getLogger(VirtualAccountController.class);

    @Autowired
    private VirtualAccountService virtualAccountService;

    @Autowired
    private VirtualAccountValidator virtualAccountValidator;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    MerchantConfigurationService merchantConfigurationService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private FileServiceUtil fileServiceUtil;

    @GetMapping(
            path = "/list",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('GET_VIRTUAL_ACCOUNTS')")
    public ResponseEntity getAllVirtualAccounts(final HttpServletRequest request,
                                                @RequestParam(value = "status", required = false) String status) {
        if (!Utils.nullOrEmptyString(status)) {
            if (VirtualAccountStatusEnum.getVirtualAccountStatus(status) == null) {
                logger.error("Invalid status ", new Date());
                return ResponseEntity.badRequest().body(new CommonReponse("Invalid status"));
            }
        }
        Collection<VirtualAccountDto> virtualAccounts = virtualAccountService.findAll(status);
        return ResponseEntity.ok(virtualAccounts);
    }

    @PostMapping(
            path = "/approvedClouser",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setapprovedStatus(@RequestBody VirtualAccountDto virtualAccountDto, @RequestParam String action) {

        if (Utils.nullOrEmptyString(virtualAccountDto.getVirtualAccountID())) {
            logger.error("Virtual account ID can not be null or empty ", new Date());
            return ResponseEntity.badRequest().body(new CommonReponse("Virtual account ID can not be null or empty"));
        }
        int clouserstatus = virtualAccountService.updateByVirtualaccountId(virtualAccountDto, action);

        try {
            if (clouserstatus > 0) {
                return ResponseEntity.ok(new CommonReponse("VirtualApproved Status Updated Successfully"));
            } else if (clouserstatus <= 0) {
                return ResponseEntity.badRequest()
                        .body(new CommonReponse("VirtualApproved Status cannot be update"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.getMessage();
        }

        return null;

    }

    @PostMapping(path = "/accountClouser",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('EDIT_VIRTUAL_ACCOUNT')")
    ResponseEntity setaccountCloseStatus(@RequestBody VirtualAccountDto virtualAccountDto) {

        if (Utils.nullOrEmptyString(virtualAccountDto.getVirtualAccountID())) {
            logger.error("Virtual account ID can not be null or empty ", new Date());
            return ResponseEntity.badRequest().body(new CommonReponse("Virtual account ID can not be null or empty"));
        }
        int clouserstatus = 0;

        try {

            clouserstatus = virtualAccountService.virtualAccountDtos(virtualAccountDto);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(new CommonReponse(e.getMessage()));
        }
        
        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
   MerchantConfiguration merchantConfiguration = (MerchantConfiguration) merchantConfigurationService.findBymerchantId(user.getMerchantId());
   System.out.println(merchantConfiguration.isEnableCloseAcctChecker());

        if (clouserstatus > 0 && merchantConfiguration.isEnableCloseAcctChecker()==true) {
            return ResponseEntity.ok(new CommonReponse("Payout account closure Successfully submitted for approval."));
        }
        
        if (clouserstatus > 0 && merchantConfiguration.isEnableCloseAcctChecker()==false) {
            return ResponseEntity.ok(new CommonReponse("Payout account Closed Successfully"));
        }
        else if (clouserstatus <= 0) {
            return ResponseEntity.badRequest()
                    .body(new CommonReponse("VirtualAccount cannot be update"));
        }

        return null;
    }

    @PostMapping(path = "/pendingClouserlist",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ACCOUNT_CLOSURE')")

    public List<Object> getAllPendingVirtualAccounts(final HttpServletRequest request, @RequestBody VirtualAccountDto virtualAccountDto) {

        return virtualAccountService.findAllPendingAccounts(virtualAccountDto.getMerchantid());
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('VIEW_VIRTUAL_ACCOUNTS')")
    public ResponseEntity getVirtualAccount(final HttpServletRequest request, @PathVariable("id") String virtualAccountId) {
        if (Utils.nullOrEmptyString(virtualAccountId)) {
            logger.error("Virtual account ID can not be null or empty ", new Date());
            return ResponseEntity.badRequest().body(new CommonReponse("Virtual account ID can not be null or empty"));
        }
        VirtualAccountDto virtualAccount = virtualAccountService.getVirtualAccount(virtualAccountId, VirtualAccountStatusEnum.ACTIVE.name());
        return ResponseEntity.ok(virtualAccount);
    }

    @PostMapping(
            path = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('ADD_VIRTUAL_ACCOUNT')")
    public ResponseEntity addVirtualAccount(final HttpServletRequest request, @Valid @RequestBody VirtualAccountDto virtualAccountDto) {
        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
        }

        final CommonReponse validationError = virtualAccountValidator.validateVirtualAccount(virtualAccountDto,
                user, ActionCommandEnum.Create);
        if (validationError != null) {
            logger.error("Create virtual account validation error ", validationError);
            return ResponseEntity.badRequest().body(validationError);
        }

        virtualAccountService.createVirtualAccount(virtualAccountDto);
        return ResponseEntity.ok(new CommonReponse("Virtual Account created successfully!"));
    }

}
