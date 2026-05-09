package by.diplom.workspace.worker.model.user.time;

import java.time.ZoneId;

public interface TimeZoneAware {

    String getTimeZone();

    default ZoneId getZoneId() {
        return TimeZoneSupport.toZoneId(getTimeZone());
    }
}
