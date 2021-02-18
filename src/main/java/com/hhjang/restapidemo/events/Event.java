package com.hhjang.restapidemo.events;

import com.hhjang.restapidemo.accounts.Account;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @EqualsAndHashCode(of = "id")
@Entity
public class Event {
    @Id @GeneratedValue
    private Integer id;
    private String name;
    private String description;

    @CreationTimestamp
    private LocalDateTime createdDateTime;
    @UpdateTimestamp
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
    @Enumerated(value = EnumType.STRING) @Builder.Default
    private EventStatus eventStatus = EventStatus.DRAFT;
    @ManyToOne
    private Account manager;

    public void statusUpdate() {
        // Update free
        if (this.basePrice == 0 && this.maxPrice == 0) {
            this.free = true;
        } else {
            this.free = false;
        }
        // Update offline
        if (this.location == null || this.location.isBlank()) {
            this.offline = false;
        } else {
            this.offline = true;
        }
    }

    public void update(EventDto.Request dto) {
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.beginEnrollmentDateTime = dto.getBeginEnrollmentDateTime();
        this.closeEnrollmentDateTime = dto.getCloseEnrollmentDateTime();
        this.beginEventDateTime = dto.getBeginEventDateTime();
        this.closeEventDateTime = dto.getCloseEventDateTime();
        this.location = dto.getLocation();
        this.basePrice = dto.getBasePrice();
        this.maxPrice = dto.getMaxPrice();
        this.limitOfEnrollment = dto.getLimitOfEnrollment();

        statusUpdate();
    }
}
