package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.payload.RoleDto;

import java.util.Collection;

public interface IRoleService {

    RoleDto findById(Integer id);

    RoleDto findByName(String name);

    RoleDto findByIdAndName(Integer id, String name);

    Boolean existsByName(String name);

    Boolean existsByNameAndIsDeleted(String name, Boolean isDeleted);

    Boolean existsById(Integer id);

    Collection<RoleDto> findAll();

    Collection<RoleDto> findAll(boolean isDeleted);

    RoleDto convertToDto(Role role);

    Collection<RoleDto> convertToDto(Collection<Role> roles);

    Role createRole(RoleDto roleDto);

    Role updateRole(RoleDto roleDto);

    void deleteRole(Integer id);

}
