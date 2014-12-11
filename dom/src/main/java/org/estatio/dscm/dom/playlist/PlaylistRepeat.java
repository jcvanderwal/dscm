package org.estatio.dscm.dom.playlist;

public enum PlaylistRepeat {

    DAILY("RRULE:FREQ=DAILY"),
    EVERY_WEEKDAY("RRULE:FREQ=DAILY;BYDAY=MO,TU,WE,TH,FR"),
    EVERY_WEEKEND("RRULE:FREQ=DAILY;BYDAY=SA,SU");

    private String rrule;

    private PlaylistRepeat(String rrule) {
        this.rrule = rrule;
    }

    public String rrule() {
        return rrule;
    }

    public String title() {
        return this.name();
    }
    
    public static PlaylistRepeat stringToPlaylistRepeat(String rule) {
        for (PlaylistRepeat e : PlaylistRepeat.values()) {
            if (rule == e.rrule()) {
                return e;
            }
        }
        
        return null;
    }
}
