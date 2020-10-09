package com.hhjang.restapidemo.events;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events/", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator validator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator validator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.validator = validator;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        validator.validate(eventDto, errors);
        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event savedEvent = this.eventRepository.save(event);

        WebMvcLinkBuilder linkBuilder = linkTo(Event.class).slash(savedEvent.getId());
        URI createdUri = linkBuilder.toUri();

        EntityModel<Event> resource = EntityModel.of(savedEvent);
        resource.add(linkTo(EventController.class).withRel("query-events"));
        resource.add(linkBuilder.withSelfRel());
        resource.add(linkBuilder.withRel("update-event"));
        // TODO gradle 기반으로 구성하기
        resource.add(Link.of("docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(resource);
    }
}
