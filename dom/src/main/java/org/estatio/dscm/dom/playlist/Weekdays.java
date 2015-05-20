package org.estatio.dscm.dom.playlist;

public enum Weekdays {
    MONDAY("MO"),
    TUESDAY("TU"),
    WEDNESDAY("WE"),
    THURSDAY("TH"),
    FRIDAY("FR"),
    SATURDAY("SA"),
    SUNDAY("SU");

    private String day;

    private Weekdays(String day) {
        this.day = day;
    }

    public String day() {
        return day;
    }

    public static Weekdays stringToWeekdays(String day) {
        for (Weekdays e : Weekdays.values()) {
            if (day.equals(e.day())) {
                return e;
            }
        }

        return null;
    }

    public String lowerCaseName() {
        return this.name().substring(0, 1).concat(this.name().substring(1).toLowerCase());
    }
}
