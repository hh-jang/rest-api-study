package com.hhjang.restapidemo.events;

import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class EventRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Event, EventDto> {
    /**
     * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and resource type.
     *
     * @param controllerClass must not be {@literal null}.
     * @param resourceType    must not be {@literal null}.
     */
    public EventRepresentationModelAssembler(Class<?> controllerClass, Class<EventDto> resourceType) {
        super(controllerClass, resourceType);
    }

    public EventRepresentationModelAssembler() {
        super(EventController.class, EventDto.class);
    }

    @Override
    public EventDto toModel(Event event) {
        EventDto model = instantiateModel(event);
        WebMvcLinkBuilder linkBuilder = linkTo(Event.class).slash(event.getId());

        model.add(linkTo(EventController.class).withRel("query-events"));
        model.add(linkBuilder.withSelfRel());
        model.add(linkBuilder.withRel("update-event"));
        // TODO gradle 기반으로 구성하기
        model.add(Link.of("docs/index.html#resources-events-create").withRel("profile"));

        return model;
    }
}
