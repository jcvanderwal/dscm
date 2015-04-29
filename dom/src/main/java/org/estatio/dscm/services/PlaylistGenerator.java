package org.estatio.dscm.services;

import org.estatio.dscm.dom.playlist.PlaylistItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PlaylistGenerator {

    public static List<PlaylistItem> generate(
            List<PlaylistItem> commercialItems,
            List<PlaylistItem> fillers,
            BigDecimal cycleDuration) {

        BigDecimal commercialDuration;

        List<PlaylistItem> resultingPlaylist = new ArrayList<PlaylistItem>();

        PlaylistManager playlistManager = new PlaylistManager(fillers);

        commercialDuration = PlaylistManager.totalDurationOf(commercialItems);

        /* Stop after all fillers are equally used */
        do {
            resultingPlaylist.addAll(commercialItems);
            BigDecimal timeLeftInCycle = cycleDuration.subtract(commercialDuration);

            /* Stop after cycle is completely used */
            while (timeLeftInCycle.compareTo(BigDecimal.ZERO) > 0) {
                PlaylistItem nextFiller = playlistManager.nextItem(timeLeftInCycle);
                if (nextFiller != null) {
                    resultingPlaylist.add(nextFiller);
                    timeLeftInCycle = timeLeftInCycle.subtract(nextFiller.getDuration());
                } else {
                    break;
                }
            }
        } while (!playlistManager.itemsEquallyUsed());

        return resultingPlaylist;
    }

}
