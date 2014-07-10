package org.estatio.dscm.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import org.apache.isis.applib.ApplicationException;

import org.estatio.dscm.dom.playlist.PlaylistItem;

class PlaylistManager {

    private List<PlayableItem> playableItemObjects;

    PlaylistManager(List<PlaylistItem> playlistItems) {
        playableItemObjects = new ArrayList<PlaylistManager.PlayableItem>();
        for (PlaylistItem item : playlistItems) {
            playableItemObjects.add(new PlayableItem(item));
        }
    }

    PlaylistItem nextItem(BigDecimal availableDuration) {
        Collections.sort(playableItemObjects);
        for (PlayableItem playableItem : playableItemObjects) {
            if (playableItem.getDuration().compareTo(availableDuration) <= 0) {
                playableItem.incrementUse();
                return playableItem.getItem();
            }
        }
        return null;
    }

    boolean itemsEquallyUsed() {
        BigInteger lowest = null;
        BigInteger highest = null;
        for (PlayableItem playableItem : playableItemObjects) {
            lowest = ObjectUtils.min(lowest, playableItem.getTimesUsed());
            highest = ObjectUtils.max(highest, playableItem.getTimesUsed());
        }
        return lowest == highest && lowest.compareTo(BigInteger.ZERO) > 0;
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
