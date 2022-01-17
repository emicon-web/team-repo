package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.enums.PartnerTypeEnum;
import com.bezkoder.springjwt.models.Action;
import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.payload.ActionDto;
import com.bezkoder.springjwt.payload.RoleDto;
import com.bezkoder.springjwt.repository.ActionRepository;
import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private IActionService actionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Override
    public Collection<RoleDto> findAll() {
        List<Role> roles = roleRepository.findAll();
        return convertToDto(roles);
    }

    @Override
    public Collection<RoleDto> findAll(final boolean isDeleted) {
        User user = userRepository.findByUserName(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername())
                .get();
        List<Role> roles = null;
        if (user != null && user.getUserType().getId() == PartnerTypeEnum.MERCHANT.getId()) {
            roles = roleRepository.findAllByIsDeletedAndMerchantId(isDeleted, user.getMerchantId());
        }
//        else {
//            roles = roleRepository.findAllByIsDeleted(isDeleted);
//        }
        return convertToDto(roles);
    }

    @Override
    public RoleDto findByName(String name) {
        Optional<Role> role = roleRepository.findByName(name);
        if (role.isPresent()) {
            return convertToDto(role.get());
        }
        return null;
    }

    @Override
    public RoleDto findById(Integer id) {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isPresent()) {
            return convertToDto(role.get());
        }
        return null;
    }

    @Override
    public RoleDto convertToDto(Role role) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());
        roleDto.setDescription(role.getDescription());
        roleDto.setRoleCreatedBy(role.getRoleCreatedBy());
        roleDto.setRoleCreatedTime(role.getRoleCreatedTime());
        roleDto.setRoleUpdatedBy(role.getRoleUpdatedBy());
        roleDto.setRoleUpdatedTime(role.getRoleUpdatedTime());
        roleDto.setMerchantId(role.getMerchantId());

        if (!Utils.nullOrEmptyCollection(role.getActions())) {
            roleDto.setActions(actionService.convertToDto(role.getActions()));
        }

        return roleDto;
    }

    @Override
    public Collection<RoleDto> convertToDto(Collection<Role> roles) {

        if (!Utils.nullOrEmptyCollection(roles)) {
            return roles.stream()
                    .map(role -> convertToDto(role))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public RoleDto findByIdAndName(Integer id, String name) {
        Optional<Role> role = roleRepository.findByIdAndName(id, name);
        if (role.isPresent()) {
            return convertToDto(role.get());
        }
        return null;
    }

    @Override
    public Boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    @Override
    public Boolean existsByNameAndIsDeleted(String name, Boolean isDeleted) {
        return roleRepository.existsByNameAndIsDeleted(name, isDeleted);
    }

    @Override
    public Role createRole(RoleDto roleDto) {
        Role role = new Role();

        role.setDescription(roleDto.getDescription());
        role.setName(roleDto.getName());
        role.setRoleCreatedBy(roleDto.getRoleCreatedBy());
        role.setRoleCreatedTime(new Date());
        role.setMerchantId(roleDto.getMerchantId());

        updateActions(roleDto, role);
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(RoleDto roleDto) {
        Optional<Role> roleDb = roleRepository.findById(roleDto.getId());
        Role role = roleDb.get();

        role.setDescription(roleDto.getDescription());
        role.setRoleUpdatedBy(roleDto.getRoleUpdatedBy());
        role.setRoleUpdatedTime(new Date());
        updateActions(roleDto, role);

        return roleRepository.save(role);
    }

    private void updateActions(RoleDto roleDto, Role role) {
        System.out.println(roleDto.getActions());
        if (!Utils.nullOrEmptyCollection(roleDto.getActions())) {
            Set<Action> actions = new HashSet<>();

            for (ActionDto actionDto : roleDto.getActions()) {
                Optional<Action> action = actionRepository.findById(actionDto.getId());
                if (action.isPresent()) {
                    actions.add(action.get());
                }
            }
            role.setActions(actions);
        }
    }

    @Override
    public Boolean existsById(Integer id) {
        return roleRepository.existsById(id);
    }

    @Override
    public void deleteRole(Integer id) {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isPresent()) {
            Role roleDb = role.get();
            roleDb.setIsDeleted(true);
            roleRepository.save(roleDb);
        }
    }
}
