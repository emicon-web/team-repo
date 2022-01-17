package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String userName);

    Optional<User> findByUserEmail(String userEmail);

    Optional<User> findByUserNameOrUserEmail(String userName, String userEmail);

    Boolean existsByUserName(String userName);

    Boolean existsByUserEmail(String userEmail);

    List<User> findAll();

    User findById(long id);

    List<User> findAllByIsDeleted(Boolean isDeleted);

    List<User> findAllByIsDeletedAndMerchantId(Boolean isDeleted, String merchantId);

//    List<User> findAllByIsDeletedAndMerchantIdIn(Boolean isDeleted, List<String> MerchantIds);
}
