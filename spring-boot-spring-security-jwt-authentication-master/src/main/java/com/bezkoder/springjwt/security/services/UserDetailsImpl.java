package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.enums.PartnerTypeEnum;
import com.bezkoder.springjwt.models.Action;
import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Column;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private final Long id;

    private final String userName;

    private final String userEmail;

    private String merchantId;

    private PartnerTypeEnum userType;

    @JsonIgnore
	@Column(name = "user_password")
    private final String userCred;

    private final Collection<? extends GrantedAuthority> authorities;

    private Collection<String> userRoles;

    public UserDetailsImpl(Long id, String userName, String userEmail, String merchantId, String userCred,
                           Collection<? extends GrantedAuthority> authorities,
                           Collection<String> userRoles, PartnerTypeEnum userType) {
        this.id = id;
        this.userName = userName;
        this.userEmail = userEmail;
        this.merchantId = merchantId;
        this.userCred = userCred;
        this.authorities = authorities;
        this.userRoles = userRoles;
        this.userType = userType;
    }

    public static UserDetailsImpl build(User user) {
        Collection<Action> actions = new HashSet<>();
        Collection<String> userRoles = new HashSet<>();

        if (!Utils.nullOrEmptyCollection(user.getRoles())) {
            for (Role role : user.getRoles()) {
                userRoles.add(role.getName());
                actions.addAll(role.getActions());
            }
        }

        List<GrantedAuthority> authorities = actions.stream()
                .map(action -> new SimpleGrantedAuthority(action.getAction().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUserName(),
                user.getUserEmail(),
                user.getMerchantId(),
                user.getUserCred(),
                authorities,
                userRoles,
                user.getUserType());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return userEmail;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public PartnerTypeEnum getUserType() {
        return userType;
    }

    public void setUserType(PartnerTypeEnum userType) {
        this.userType = userType;
    }

    public Collection<String> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Collection<String> userRoles) {
        this.userRoles = userRoles;
    }

    @Override
    public String getPassword() {
        return userCred;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
