package com.hhjang.restapidemo.events;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventEntityModel extends EntityModel<Event> {

    public EventEntityModel() {}

    public static EntityModel<Event> of(Event event, Link... links) {
        EntityModel<Event> eventEntityModel = EntityModel.of(event, links);
        WebMvcLinkBuilder eventLink = linkTo(EventController.class);
        eventEntityModel.add(eventLink.slash(event.getId()).withSelfRel());

        return eventEntityModel;
    }
}
