package org.estatio.dscm.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.ObjectUtils;

import org.apache.isis.applib.ApplicationException;

import org.estatio.dscm.dom.playlist.PlaylistItem;

class FillerManager {

    private SortedSet<Filler> fillerObjects;

    FillerManager(List<PlaylistItem> fillerItems) {
        fillerObjects = new TreeSet<FillerManager.Filler>();
        for (PlaylistItem item : fillerItems) {
            fillerObjects.add(new Filler(item));
        }
    }

    PlaylistItem nextFiller(BigDecimal availableDuration) {
        for (Filler filler : fillerObjects) {
            if (filler.getDuration().compareTo(availableDuration) <= 0) {
                filler.incrementUse();
                return filler.getItem();
            }
        }
        return null;
    }

    boolean fillersEquallyUsed() {
        BigInteger lowest = BigInteger.ZERO;
        BigInteger highest = BigInteger.ZERO;
        for (Filler filler : fillerObjects) {
            lowest = ObjectUtils.min(lowest, filler.getTimesUsed());
            highest = ObjectUtils.max(highest, filler.getTimesUsed());
        }
        return lowest == highest && lowest.compareTo(BigInteger.ZERO) > 0;
    }

    class Filler implements Comparable<Filler> {
        private PlaylistItem item;
        private BigInteger timesUsed;

        public Filler(PlaylistItem playlistItem) {
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
            if (timesUsed.compareTo(new BigInteger("100"))==0){
                throw new ApplicationException("Infinite loop");
            }
        }

        @Override
        public int compareTo(Filler o) {
            int x = getTimesUsed().compareTo(o.getTimesUsed());
            if (x == 0) {
                getItem().compareTo(o.getItem());
            }
            return x;
        }

    }

}