package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.enums.ActionCommandEnum;
import com.bezkoder.springjwt.enums.PartnerTypeEnum;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.CommonReponse;
import com.bezkoder.springjwt.payload.RoleDto;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.services.IRoleService;
import com.bezkoder.springjwt.services.MerchantService;
import com.bezkoder.springjwt.services.UserRoleService;
import com.bezkoder.springjwt.utils.Utils;
import com.bezkoder.springjwt.validators.RoleValidator;
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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private static final Logger logger = LogManager.getLogger(RoleController.class);

    @Autowired
    private MessageSource messages;

    @Autowired
    IRoleService roleService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleValidator roleValidator;

    @GetMapping(
            path = "/list",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('READ_ROLE')")
    public ResponseEntity getAllRoles() {
        Collection<RoleDto> roles = roleService.findAll(false);
        return ResponseEntity.ok(roles);
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('ROLE_DETAIL')")
    public ResponseEntity getRole(@PathVariable("id") int id) {
        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
        }
        RoleDto roleDto = roleService.findById(id);
        if (roleDto != null) {
            if (user.getUserType() != PartnerTypeEnum.MERCHANT || !user.getMerchantId().equals(roleDto.getMerchantId())) {
                logger.error(messages.getMessage("error.message.invalidRequest", null, null));
                return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
            }
            return ResponseEntity.ok(roleDto);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping(
            path = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('ADD_ROLE')")
    public ResponseEntity addRole(@Valid @RequestBody RoleDto roleDto) {

        final CommonReponse validationError = roleValidator.validateRole(roleDto, ActionCommandEnum.Create);
        if (validationError != null) {
            logger.error("Create role validation error ", validationError);
            return ResponseEntity.badRequest().body(validationError);
        }

        if (Utils.nullOrEmptyString(roleDto.getName())
                || Utils.nullOrEmptyString(roleDto.getRoleCreatedBy())) {
            logger.error(messages.getMessage("role.message.roleInfo", null, null));
            return ResponseEntity.badRequest()
                    .body(new CommonReponse("Error: Role name and other required fields can not be null!"));
        }

        Boolean existingRole = roleService.existsByNameAndIsDeleted(roleDto.getName(), Boolean.FALSE);
        if (existingRole) {
            logger.error(messages.getMessage("role.message.roleName", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse("Error: Role name is already taken!"));
        }

        if (!merchantService.existsByMerchantId(roleDto.getMerchantId())) {
            logger.error(messages.getMessage("auth.message.merchant", null, null));
            return ResponseEntity.badRequest().body(new CommonReponse("Error: Merchant not found."));
        }

        roleService.createRole(roleDto);
        return ResponseEntity.ok(new CommonReponse("Role created successfully!"));
    }

    @PostMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('UPDATE_ROLE')")
    public ResponseEntity updateRole(@PathVariable("id") int id, @Valid @RequestBody RoleDto roleDto) {

        final CommonReponse validationError = roleValidator.validateRole(roleDto, ActionCommandEnum.Update);
        if (validationError != null) {
            logger.error("Update role validation error ", validationError);
            return ResponseEntity.badRequest().body(validationError);
        }

        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
        }
        if (id <= 0 || roleDto.getId() == null) {
            logger.error(messages.getMessage("role.message.roleId", null, null));
            return ResponseEntity.badRequest()
                    .body(new CommonReponse("Error: Role id is required!"));
        }

        final RoleDto existingRole = roleService.findById(roleDto.getId());
        if (existingRole == null) {
            logger.error(messages.getMessage("role.message.rolenotfound", null, null));
            throw new RuntimeException("Error: Role not found!");
        }

        if (user.getUserType() != PartnerTypeEnum.MERCHANT || !user.getMerchantId().equals(existingRole.getMerchantId())) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
        }

        roleService.updateRole(roleDto);
        return ResponseEntity.ok(new CommonReponse("Role updated successfully!"));
    }

    @PostMapping(
            path = "/delete/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('DELETE_ROLE')")
    public ResponseEntity deleteRole(@PathVariable("id") int id) {
        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        if (user == null) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
        }

        if (id <= 0) {
            logger.error(messages.getMessage("role.message.roleId", null, null));
            return ResponseEntity.badRequest()
                    .body(new CommonReponse("Error: Role id is required!"));
        }
        final RoleDto existingRole = roleService.findById(id);
        if (existingRole == null) {
            logger.error(messages.getMessage("role.message.rolenotfound", null, null));
            throw new RuntimeException("Error: Role not found!");
        }
        if (user.getUserType() != PartnerTypeEnum.MERCHANT || !user.getMerchantId().equals(existingRole.getMerchantId())) {
            logger.error(messages.getMessage("error.message.invalidRequest", null, null));
            return ResponseEntity.badRequest().body(messages.getMessage("error.message.invalidRequest", null, null));
        }

        try {
            int data = userRoleService.userrole(id);
            System.out.println(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }

        roleService.deleteRole(id);
        return ResponseEntity.ok(new CommonReponse("Role deleted successfully!"));
    }
}
