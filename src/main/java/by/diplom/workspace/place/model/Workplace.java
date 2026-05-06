package by.diplom.workspace.place.model;

import by.diplom.workspace.place.model.place.Place;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@DiscriminatorValue("WORKPLACE")
public class Workplace extends Place {
    public Workplace(int floor, int number) {
        super(floor, number);
    }
}
