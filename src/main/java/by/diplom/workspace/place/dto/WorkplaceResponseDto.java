package by.diplom.workspace.place.dto;

import by.diplom.workspace.place.model.place.PlaceAdvantage;
import by.diplom.workspace.place.model.place.PlaceEquipment;

import java.util.Set;

public record WorkplaceResponseDto(
        long id,
        int floor,
        int placeNumber,
        Set<PlaceEquipment> equipments,
        Set<PlaceAdvantage> advantages,
        StatusBookingPlace statusBooking
) {
}
