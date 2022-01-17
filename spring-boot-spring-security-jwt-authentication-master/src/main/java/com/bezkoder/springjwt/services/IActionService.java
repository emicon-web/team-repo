package com.bezkoder.springjwt.services;

import java.util.Collection;

import com.bezkoder.springjwt.models.Action;
import com.bezkoder.springjwt.payload.ActionDto;

public interface IActionService {
	
	ActionDto findById(Integer id);

	Collection<ActionDto> findAll();

	ActionDto convertToDto(Action action);

	Collection<ActionDto> convertToDto(Collection<Action> actions);

}
