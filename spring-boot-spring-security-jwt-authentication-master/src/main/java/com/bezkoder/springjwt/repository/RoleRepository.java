package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIsDeleted(String name, Boolean isDeleted);

    boolean existsById(Integer id);

    Optional<Role> findByIdAndName(Integer id, String name);

    List<Role> findAllByIsDeleted(Boolean isDeleted);

    List<Role> findAllByIsDeletedAndMerchantId(Boolean isDeleted, String merchantId);

//    List<Role> findAllByIsDeletedAndMerchantIdIn(Boolean isDeleted, List<String> merchantIds);
}
