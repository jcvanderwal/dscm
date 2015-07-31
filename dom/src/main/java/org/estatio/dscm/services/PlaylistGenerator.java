package org.estatio.dscm.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.estatio.dscm.dom.playlist.PlaylistItem;
import org.estatio.dscm.dom.playlist.PlaylistType;

public class PlaylistGenerator {

    public static List<PlaylistItem> generate(
            List<PlaylistItem> commercialItems,
            List<PlaylistItem> fillers,
            BigDecimal cycleDuration) {

        BigDecimal commercialDuration;

        List<PlaylistItem> resultingPlaylist = new ArrayList<PlaylistItem>();

        PlaylistManager playlistManager = new PlaylistManager(commercialItems, fillers);

        commercialDuration = PlaylistManager.totalDurationOf(commercialItems);
        int offset = 0;

        // TODO: Current implementation assumes that all assets have a duration of 10s (which is true in the current usecase). Better solution required for variable lengths
        /* Stop after all fillers are equally used */
        do {
            BigDecimal timeLeftInCycleMain = new BigDecimal(40);

            while (timeLeftInCycleMain.compareTo(BigDecimal.ZERO) > 0) {
                PlaylistItem nextMain = playlistManager.nextItem(timeLeftInCycleMain, PlaylistType.MAIN);
                if (nextMain != null) {
                    resultingPlaylist.add(nextMain);
                    timeLeftInCycleMain = timeLeftInCycleMain.subtract(nextMain.getDuration());
                } else {
                    break;
                }
            }

            BigDecimal timeLeftInCycleFiller = new BigDecimal(20);

            /* Stop after cycle is completely used */
            while (timeLeftInCycleFiller.compareTo(BigDecimal.ZERO) > 0) {
                PlaylistItem nextFiller = playlistManager.nextItem(timeLeftInCycleFiller, PlaylistType.FILLERS);
                if (nextFiller != null) {
                    resultingPlaylist.add(nextFiller);
                    timeLeftInCycleFiller = timeLeftInCycleFiller.subtract(nextFiller.getDuration());
                } else {
                    break;
                }
            }
        } while (!playlistManager.itemsEquallyUsed());

        return resultingPlaylist;
    }

}
