package com.hhjang.restapidemo.events;

import com.hhjang.restapidemo.index.IndexController;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
            return badRequest(errors);
        }
        validator.validate(eventDto, errors);
        if(errors.hasErrors()) {
            return badRequest(errors);
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

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> resourcesAssembler) {
        Page<Event> page = this.eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> entityModels = resourcesAssembler.toModel(page);
        return ResponseEntity.ok(entityModels);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(
                EntityModel.of(errors, linkTo(methodOn(IndexController.class).index()).withRel("index")));
    }
}
