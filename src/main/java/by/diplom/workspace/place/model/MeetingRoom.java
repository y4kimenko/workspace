package by.diplom.workspace.place.model;

import by.diplom.workspace.place.model.place.Place;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@DiscriminatorValue("MEETINGROOM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingRoom extends Place {

    @NotNull
    @Min(1)
    @Column(name = "capacity")
    private Integer capacity;

    public MeetingRoom(Integer floorNumber, Integer placeNumber, Integer capacity) {
        super(floorNumber, placeNumber);
        this.capacity = capacity;
    }
}
