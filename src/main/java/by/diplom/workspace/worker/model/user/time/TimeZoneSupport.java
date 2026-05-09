package by.diplom.workspace.worker.model.user.time;

import java.time.DateTimeException;
import java.time.ZoneId;

public final class TimeZoneSupport {

    public static final String DEFAULT_TIME_ZONE = "Europe/Minsk";

    private TimeZoneSupport() {
    }

    public static String normalizeOrDefault(String timeZone) {
        if (timeZone == null || timeZone.isBlank()) {
            return DEFAULT_TIME_ZONE;
        }

        return validateAndNormalize(timeZone);
    }

    public static String validateAndNormalize(String timeZone) {
        if (timeZone == null || timeZone.isBlank()) {
            throw new IllegalArgumentException("Timezone must not be empty");
        }

        try {
            return ZoneId.of(timeZone).getId();
        } catch (DateTimeException exception) {
            throw new IllegalArgumentException("Invalid timezone: " + timeZone);
        }
    }

    public static ZoneId toZoneId(String timeZone) {
        return ZoneId.of(validateAndNormalize(timeZone));
    }
}