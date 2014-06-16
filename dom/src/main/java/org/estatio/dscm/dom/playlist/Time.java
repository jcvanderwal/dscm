package org.estatio.dscm.dom.playlist;

import org.joda.time.LocalTime;

public enum Time {
    T0700,
    T0800,
    T0900,
    T1000,
    T1100,
    T1200,
    T1300,
    T1400,
    T1500,
    T1600,
    T1700,
    T1800,
    T1900,
    T2000,
    T2100;

    public LocalTime time() {
        return new LocalTime(title());
    }

    public String title() {
        final String a = name().substring(1, 3);
        final String b = name().substring(3, 5);
        return a.concat(":").concat(b);
    }

}
