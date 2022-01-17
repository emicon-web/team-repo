package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.payload.ActionDto;
import com.bezkoder.springjwt.services.IActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/actions")
public class ActionController {

    @Autowired
    IActionService actionService;

    @GetMapping(
            path = "/list",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity getAllActions() {
        Collection<ActionDto> actions = actionService.findAll();
        return ResponseEntity.ok(actions);
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity getAction(@PathVariable("id") int id) {
        ActionDto actionDto = actionService.findById(id);
        if (actionDto != null) {
            return ResponseEntity.ok(actionDto);
        }
        return ResponseEntity.noContent().build();
    }
}
