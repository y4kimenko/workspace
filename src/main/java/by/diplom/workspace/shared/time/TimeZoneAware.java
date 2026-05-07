package by.diplom.workspace.shared.time;

import java.time.ZoneId;

public interface TimeZoneAware {

    String getTimeZone();

    default ZoneId getZoneId() {
        return TimeZoneSupport.toZoneId(getTimeZone());
    }
}
