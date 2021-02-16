package com.hhjang.restapidemo.events;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class EventModelAssembler extends RepresentationModelAssemblerSupport<Event, EventDto.Response> {    // RepresentationModelAssemblerSupport는 언제나 self link를 add함

    public EventModelAssembler() {
        super(EventController.class, EventDto.Response.class);
    }

    @Override
    public EventDto.Response toModel(Event event) {
        EventDto.Response response = EventDto.Response.of(event);
        response.add(getSelfRel(event));

        return response;
    }

    private static Link getSelfRel(Event event) {
        return linkTo(EventController.class).slash(event.getId()).withSelfRel();
    }
}
