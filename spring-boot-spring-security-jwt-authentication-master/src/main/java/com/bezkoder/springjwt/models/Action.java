package com.bezkoder.springjwt.models;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.bezkoder.springjwt.enums.ActionEnum;

@Entity
@Table(name = "actions",
		uniqueConstraints = { 
				@UniqueConstraint(columnNames = "id"),
				@UniqueConstraint(columnNames = "action")
		})
public class Action {

    @Id
    private Integer id;

    @Enumerated(EnumType.STRING)
	@Column(length = 20)
    private ActionEnum action;

    @ManyToMany(mappedBy = "actions")
    private Collection<Role> roles = new HashSet<>();

	public Action() {
		super();
	}

	public Action(Integer id, ActionEnum action) {
		super();
		this.id = id;
		this.action = action;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ActionEnum getAction() {
		return action;
	}

	public void setAction(ActionEnum action) {
		this.action = action;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}
}
