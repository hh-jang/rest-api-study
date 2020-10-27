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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
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
        event.statusUpdate();
        Event savedEvent = this.eventRepository.save(event);

        WebMvcLinkBuilder linkBuilder = linkTo(Event.class).slash(savedEvent.getId());
        URI createdUri = linkBuilder.toUri();

        EntityModel<Event> resource = EntityModel.of(savedEvent);
        resource.add(linkTo(EventController.class).withRel("query-events"));
        resource.add(linkBuilder.withSelfRel());
        resource.add(linkBuilder.withRel("update-event"));
        // TODO gradle 기반으로 구성하기
        resource.add(Link.of("docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.created(createdUri).body(resource);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> resourcesAssembler) {
        Page<Event> page = this.eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> entityModels = resourcesAssembler.toModel(page, entity -> EventEntityModel.of(entity));
        entityModels.add(Link.of("docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.ok(entityModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvents(@PathVariable Integer id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if(optionalEvent.isEmpty()) return ResponseEntity.notFound().build();

        Event event = optionalEvent.get();
        return ResponseEntity.ok(EventEntityModel.of(
                event,
                Link.of("docs/index.html#resources-events-get").withRel("profile")));
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(
                EntityModel.of(errors, linkTo(methodOn(IndexController.class).index()).withRel("index")));
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors) {
        Optional<Event> eventOptional = this.eventRepository.findById(id);
        if(eventOptional.isEmpty()) return ResponseEntity.notFound().build();

        if(errors.hasErrors()) {
            return badRequest(errors);
        }
        validator.validate(eventDto, errors);
        if(errors.hasErrors()) {
            return badRequest(errors);
        }

        Event savedEvent = eventOptional.get();
        this.modelMapper.map(eventDto, savedEvent);
        Event updatedEvent = this.eventRepository.save(savedEvent);

        WebMvcLinkBuilder linkBuilder = linkTo(Event.class).slash(updatedEvent.getId());
        URI createdUri = linkBuilder.toUri();

        EntityModel<Event> resource = EntityModel.of(updatedEvent);
        resource.add(linkTo(EventController.class).withRel("query-events"));
        resource.add(linkBuilder.withSelfRel());
        resource.add(linkBuilder.withRel("update-event"));
        // TODO gradle 기반으로 구성하기
        resource.add(Link.of("docs/index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.created(createdUri).body(resource);
    }
}
