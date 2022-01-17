package com.bezkoder.springjwt.services;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bezkoder.springjwt.enums.ActionEnum;
import com.bezkoder.springjwt.models.Action;
import com.bezkoder.springjwt.payload.ActionDto;
import com.bezkoder.springjwt.repository.ActionRepository;
import com.bezkoder.springjwt.utils.Utils;

@Service
@Transactional
public class ActionServiceImpl implements IActionService {
	
	@Autowired
	private ActionRepository actionRepository;

	@Override
	public ActionDto findById(Integer id) {
		Optional<Action> action = actionRepository.findById(id);
		if (action.isPresent()) {
			return convertToDto(action.get());
		}
		return null;
	}

	@Override
	public ActionDto convertToDto(Action action) {
		if (action != null) {
			ActionDto actionDto = new ActionDto();
			actionDto.setId(action.getId());
			ActionEnum actionEnum = ActionEnum.getById(action.getId());
			
			if (actionEnum != null) {
				actionDto.setName(actionEnum.name());
				actionDto.setDescription(actionEnum.getDescription());
			}
			return actionDto;
		}
		return null;
	}

	@Override
	public Collection<ActionDto> convertToDto(Collection<Action> actions) {

		if (!Utils.nullOrEmptyCollection(actions)) {
			return actions.stream()
					.map(action -> convertToDto(action))
					.collect(Collectors.toList());
		}

		return Collections.emptyList();
	}

	@Override
	public Collection<ActionDto> findAll() {
		List<Action> actions = actionRepository.findAll();
		return convertToDto(actions);
	}
}
