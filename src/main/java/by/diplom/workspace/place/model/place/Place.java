package by.diplom.workspace.place.model.place;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "places")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "place_type",
        discriminatorType = DiscriminatorType.STRING,
        length = 32
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "floor", nullable = false)
    private Integer floor;

    @Column(name = "place_number", nullable = false)
    private Integer number;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "places_equipments",
            joinColumns = @JoinColumn(name = "place_id"),
            inverseJoinColumns = @JoinColumn(name = "equipment_id"),
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_place_equipment",
                            columnNames = {"place_id", "equipment_id"}
                    )
            }
    )
    private final Set<PlaceEquipment> equipments = new HashSet<>();


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "places_advantages",
            joinColumns = @JoinColumn(name = "place_id"),
            inverseJoinColumns = @JoinColumn(name = "advantage_id"),
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_place_advantage",
                            columnNames = {"place_id", "advantage_id"}
                    )
            }

    )
    private final Set<PlaceAdvantage> advantages = new HashSet<>();

    protected Place(int floor, int number) {
        this.floor = floor;
        this.number = number;
    }

}
