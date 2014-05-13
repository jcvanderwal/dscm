package org.estatio.dscm.services;

import javax.inject.Inject;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class AVProbe {

    private String fileName;

    private String response;
    
    public AVProbe(String fileName) {
        this.fileName = fileName;
    }

    private void init() {
        response = commandService.executeCommand("ffprobe ".concat(fileName));
    }

    public String getResponse() {
        if (response == null) {
            init();
        }
        return this.response;
    }

    public String getDurationString() {
        final String response2 = getResponse();
        final int startPos = response2.indexOf("Duration");
        if (startPos > 0) {
            return response2.substring(startPos + 10, startPos + 10 + 11);
        }
        return null;
    }

    public Duration getDuration() {
        return parse(getDurationString());
    }

    private Duration parse(String input) {
        if (input == null){
            return null;
        }
        
        PeriodFormatter hoursMinutes = new PeriodFormatterBuilder()
                .appendHours()
                .appendSeparator(":")
                .appendMinutes()
                .appendSeparator(":")
                .appendSeconds()
                .appendSeparator(".")
                .appendMillis()
                .toFormatter();
        return hoursMinutes.parsePeriod(input).toDurationFrom(new Instant());

    }
    
    @Inject
    CommandService commandService;


}
