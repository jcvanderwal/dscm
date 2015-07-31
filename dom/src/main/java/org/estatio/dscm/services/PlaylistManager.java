package org.estatio.dscm.services;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.isis.applib.ApplicationException;
import org.estatio.dscm.dom.playlist.PlaylistItem;
import org.estatio.dscm.dom.playlist.PlaylistType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PlaylistManager {

    private List<PlayableItem> playableMainItemObjects;
    private List<PlayableItem> playableFillerItemObjects;

    PlaylistManager(List<PlaylistItem> mainPlaylistItems, List<PlaylistItem> fillerPlaylistItems) {
        playableMainItemObjects = new ArrayList<PlaylistManager.PlayableItem>();
        playableFillerItemObjects = new ArrayList<PlaylistManager.PlayableItem>();

        for (PlaylistItem item : mainPlaylistItems) {
            playableMainItemObjects.add(new PlayableItem(item));
        }

        for (PlaylistItem item : fillerPlaylistItems) {
            playableFillerItemObjects.add(new PlayableItem(item));
        }
    }

    PlaylistItem nextItem(BigDecimal availableDuration, PlaylistType playlistType) {
        if (playlistType.equals(PlaylistType.MAIN)) {
            Collections.sort(playableMainItemObjects);
            for (PlayableItem playableItem : playableMainItemObjects) {
                if (playableItem.getDuration().compareTo(availableDuration) <= 0) {
                    playableItem.incrementUse();
                    return playableItem.getItem();
                }
            }
            return null;
        }

        else if (playlistType.equals(PlaylistType.FILLERS)) {
            Collections.sort(playableFillerItemObjects);
            for (PlayableItem playableItem : playableFillerItemObjects) {
                if (playableItem.getDuration().compareTo(availableDuration) <= 0) {
                    playableItem.incrementUse();
                    return playableItem.getItem();
                }
            }
            return null;
        }

        return null;
    }

    boolean itemsEquallyUsed() {
        if (playableFillerItemObjects.size() == 0) {
            return true;
        }

        BigInteger mainLowest = null;
        BigInteger mainHighest = null;
        BigInteger fillerLowest = null;
        BigInteger fillerHighest = null;

        for (PlayableItem playableItem : playableMainItemObjects) {
            mainLowest = ObjectUtils.min(mainLowest, playableItem.getTimesUsed());
            mainHighest = ObjectUtils.max(mainHighest, playableItem.getTimesUsed());
        }

        for (PlayableItem playableItem : playableFillerItemObjects) {
            fillerLowest = ObjectUtils.min(fillerLowest, playableItem.getTimesUsed());
            fillerHighest = ObjectUtils.max(fillerHighest, playableItem.getTimesUsed());
        }
        return (fillerLowest == fillerHighest && fillerLowest.compareTo(BigInteger.ZERO) > 0) && (mainLowest == mainHighest && mainLowest.compareTo(BigInteger.ZERO) > 0);
    }

    public static BigDecimal totalDurationOf(List<PlaylistItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (PlaylistItem item : items) {
            total = total.add(item.getDuration());
        }
        return total;
    }

    class PlayableItem implements Comparable<PlayableItem> {
        private PlaylistItem item;
        private BigInteger timesUsed;

        public PlayableItem(PlaylistItem playlistItem) {
            item = playlistItem;
            timesUsed = BigInteger.ZERO;
        }

        public BigDecimal getDuration() {
            return item.getAsset().getDuration();
        }

        public PlaylistItem getItem() {
            return item;
        }

        public BigInteger getTimesUsed() {
            return timesUsed;
        }

        public void incrementUse() {
            timesUsed = timesUsed.add(BigInteger.ONE);
            if (timesUsed.compareTo(new BigInteger("100")) == 0) {
                throw new ApplicationException("Infinite loop");
            }
        }

        @Override
        public int compareTo(PlayableItem other) {
            int compare = getTimesUsed().compareTo(other.getTimesUsed());
            if (compare == 0) {
                return getItem().compareTo(other.getItem());
            }
            return compare;
        }

        public String toString() {
            return item.getAsset().getName()
                    .concat(" - ")
                    .concat(getDuration().toString())
                    .concat("sec (")
                    .concat(timesUsed.toString())
                    .concat(")");
        }

    }

}
