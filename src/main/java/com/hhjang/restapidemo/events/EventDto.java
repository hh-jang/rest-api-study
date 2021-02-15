package com.hhjang.restapidemo.events;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hhjang.restapidemo.accounts.Account;
import com.hhjang.restapidemo.accounts.AccountSerializer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class EventDto {

    @Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {
        @NotEmpty
        private String name;
        @NotEmpty
        private String description;
        @NotNull
        private LocalDateTime beginEnrollmentDateTime;
        @NotNull
        private LocalDateTime closeEnrollmentDateTime;
        @NotNull
        private LocalDateTime beginEventDateTime;
        @NotNull
        private LocalDateTime closeEventDateTime;
        @Min(0)
        private int basePrice;
        @Min(0)
        private int maxPrice;
        @Min(0)
        private int limitOfEnrollment;
        private String location;

        @Builder
        public Request(@NotEmpty String name, @NotEmpty String description, @NotNull LocalDateTime beginEnrollmentDateTime,
                       @NotNull LocalDateTime closeEnrollmentDateTime, @NotNull LocalDateTime beginEventDateTime,
                       @NotNull LocalDateTime closeEventDateTime, @Min(0) int basePrice, @Min(0) int maxPrice,
                       @Min(0) int limitOfEnrollment, String location) {
            this.name = name;
            this.description = description;
            this.beginEnrollmentDateTime = beginEnrollmentDateTime;
            this.closeEnrollmentDateTime = closeEnrollmentDateTime;
            this.beginEventDateTime = beginEventDateTime;
            this.closeEventDateTime = closeEventDateTime;
            this.basePrice = basePrice;
            this.maxPrice = maxPrice;
            this.limitOfEnrollment = limitOfEnrollment;
            this.location = location;
        }
    }

    @Getter
    public static class Response {
        private Integer id;
        private String name;
        private String description;
        private LocalDateTime createdDateTime;
        private LocalDateTime updatedDateTime;
        private LocalDateTime beginEnrollmentDateTime;
        private LocalDateTime closeEnrollmentDateTime;
        private LocalDateTime beginEventDateTime;
        private LocalDateTime closeEventDateTime;
        private String location;
        private int basePrice;
        private int maxPrice;
        private int limitOfEnrollment;
        private boolean offline;
        private boolean free;
        private EventStatus eventStatus;
        @JsonSerialize(using = AccountSerializer.class)
        private Account manager;

        public Response(Event event) {
            this.id = event.getId();
            this.name = event.getName();
            this.description = event.getDescription();
            this.createdDateTime = event.getCreatedDateTime();
            this.updatedDateTime = event.getUpdatedDateTime();
            this.beginEnrollmentDateTime = event.getBeginEnrollmentDateTime();
            this.closeEnrollmentDateTime = event.getCloseEnrollmentDateTime();
            this.beginEventDateTime = event.getBeginEventDateTime();
            this.closeEventDateTime = event.getCloseEventDateTime();
            this.location = event.getLocation();
            this.basePrice = event.getBasePrice();
            this.maxPrice = event.getMaxPrice();
            this.limitOfEnrollment = event.getLimitOfEnrollment();
            this.offline = event.isOffline();
            this.free = event.isFree();
            this.eventStatus = event.getEventStatus();
            this.manager = event.getManager();
        }

        public static Response of(Event event) {
            return new Response(event);
        }
    }
}
