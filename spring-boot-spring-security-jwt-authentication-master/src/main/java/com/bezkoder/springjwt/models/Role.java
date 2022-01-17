package com.bezkoder.springjwt.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "id"),
                @UniqueConstraint(columnNames = "name")
        })
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 20)
    private String name;

    private String description;

    @ManyToMany
    @JoinTable(name = "role_actions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id"))
    private Set<Action> actions = new HashSet<>();

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users = new HashSet<>();

    @Column(name = "merchantid")
    private String merchantId;

    @NotNull
    private String roleCreatedBy;

    @NotNull
    @CreationTimestamp
    private Date roleCreatedTime;

    private String roleUpdatedBy;

    @UpdateTimestamp
    private Date roleUpdatedTime;

    @Column(columnDefinition = "boolean default false")
    private Boolean isDeleted = false;

    public Role() {
        super();
    }

    public Role(String name) {
        super();
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Action> getActions() {
        return actions;
    }

    public void setActions(Set<Action> actions) {
        this.actions = actions;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoleCreatedBy() {
        return roleCreatedBy;
    }

    public void setRoleCreatedBy(String roleCreatedBy) {
        this.roleCreatedBy = roleCreatedBy;
    }

    public Date getRoleCreatedTime() {
        return roleCreatedTime;
    }

    public void setRoleCreatedTime(Date roleCreatedTime) {
        this.roleCreatedTime = roleCreatedTime;
    }

    public String getRoleUpdatedBy() {
        return roleUpdatedBy;
    }

    public void setRoleUpdatedBy(String roleUpdatedBy) {
        this.roleUpdatedBy = roleUpdatedBy;
    }

    public Date getRoleUpdatedTime() {
        return roleUpdatedTime;
    }

    public void setRoleUpdatedTime(Date roleUpdatedTime) {
        this.roleUpdatedTime = roleUpdatedTime;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
}