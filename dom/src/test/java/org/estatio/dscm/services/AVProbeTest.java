package org.estatio.dscm.services;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class AVProbeTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private CommandService commandService;

    private String happyCase = ""
            + "avprobe version 0.8.10-6:0.8.10-1, Copyright (c) 2007-2013 the Libav developers"
            + "  built on Feb  5 2014 03:52:19 with gcc 4.7.2"
            + "Input #0, mov,mp4,m4a,3gp,3g2,mj2, from '/var/dscm/assets/Levis_FR_1280x720.mp4':"
            + "  Metadata:"
            + "    major_brand     : mp42"
            + "    minor_version   : 0"
            + "    compatible_brands: mp42mp41"
            + "    creation_time   : 2014-02-27 14:22:34"
            + "  Duration: 00:00:33.04, start: 0.000000, bitrate: 8575 kb/s"
            + "    Stream #0.0(eng): Video: h264 (High), yuv420p, 1280x720 [PAR 1:1 DAR 16:9], 8455 kb/s, 23.98 fps, 23.98 tbr, 23976 tbn, 47.95 tbc"
            + "    Metadata:"
            + "      creation_time   : 2014-02-27 14:22:34"
            + "    Stream #0.1(eng): Audio: aac, 48000 Hz, stereo, s16, 125 kb/s"
            + "    Metadata:"
            + "      creation_time   : 2014-02-27 14:22:34";

    private String missingFile = "avprobe version 0.8.10-6:0.8.10-1, Copyright (c) 2007-2013 the Libav developers"
            + "  built on Feb  5 2014 03:52:19 with gcc 4.7.2"
            + "asdas: No such file or directory";

    private String invalidFile = "avprobe version 0.8.10-6:0.8.10-1, Copyright (c) 2007-2013 the Libav developers"
            + "  built on Feb  5 2014 03:52:19 with gcc 4.7.2"
            + "/var/log/alternatives.log: Invalid data found when processing input";

    @Before
    public void setup() {
     }

    @Test
    public void getDurationHappyCase() {
        context.checking(new Expectations() {
            {
                oneOf(commandService).executeCommand("ffprobe dummy.avi");
                will(returnValue(happyCase));
            }
        });
       AVProbe avProbe = new AVProbe("dummy.avi");
        avProbe.commandService = commandService;
        assertThat(avProbe.getDuration(), is(Duration.standardSeconds(33).plus(4)));
    }

    @Test
    public void getDurationWithMissingFile() {
        context.checking(new Expectations() {
            {
                oneOf(commandService).executeCommand("ffprobe asdf");
                will(returnValue(missingFile));
            }
        });
        AVProbe avProbe = new AVProbe("asdf");
        avProbe.commandService = commandService;
        assertNull(avProbe.getDuration());
    }

}
