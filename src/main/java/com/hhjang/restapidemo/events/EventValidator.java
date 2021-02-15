package com.hhjang.restapidemo.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto.Request dto, Errors errors) {
        if(dto.getBasePrice() > dto.getMaxPrice() && dto.getMaxPrice() > 0) {
//            errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong");
//            errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong");
            errors.reject("wrongPrices", "Values prices are wrong");
        }

        LocalDateTime closeEventDateTime = dto.getCloseEventDateTime();
        if(closeEventDateTime.isBefore(dto.getBeginEventDateTime()) ||
            closeEventDateTime.isBefore(dto.getCloseEnrollmentDateTime()) ||
            closeEventDateTime.isBefore(dto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("closeEventDateTime", "wrongValue", "CloseEventDateTime is wrong");
        }

        // TODO BeginEventTime
        // TODO CloseEnrollmentDateTime
    }
}
