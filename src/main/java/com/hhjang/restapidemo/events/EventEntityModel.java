package com.hhjang.restapidemo.events;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventEntityModel extends EntityModel<Event> {

    private static WebMvcLinkBuilder eventLink = linkTo(EventController.class);

    public EventEntityModel() {
    }

    public static EntityModel<Event> of(Event event) {
        Link selfRel = getSelfRel(event);
        return EntityModel.of(event, selfRel);
    }

    public static EntityModel<Event> of(Event event, Link... links) {
        List<Link> linkList = new ArrayList<>();
        Link selfRel = getSelfRel(event);
        linkList.add(selfRel);
        linkList.addAll(Arrays.asList(links));
        return EntityModel.of(event, linkList);
    }

    public static Link getSelfRel(Event event) {
        return eventLink.slash(event.getId()).withSelfRel();
    }
}
