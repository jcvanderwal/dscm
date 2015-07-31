package org.estatio.dscm.services;

import org.estatio.dscm.dom.asset.Asset;
import org.estatio.dscm.dom.playlist.PlaylistItem;
import org.estatio.dscm.dom.playlist.PlaylistType;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PlaylistManagerTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test() {
        PlaylistManager manager = new PlaylistManager(createList("X10", "Y10", "Z10"), createList("A10", "B20", "C10"));
        assertThat(manager.nextItem(new BigDecimal("10"), PlaylistType.MAIN).getAsset().getName(), is("X"));
        assertThat(manager.nextItem(new BigDecimal("10"), PlaylistType.MAIN).getAsset().getName(), is("Y"));
        assertThat(manager.nextItem(new BigDecimal("10"), PlaylistType.MAIN).getAsset().getName(), is("Z"));
        assertThat(manager.nextItem(new BigDecimal("20"), PlaylistType.FILLERS).getAsset().getName(), is("A"));
        assertThat(manager.nextItem(new BigDecimal("10"), PlaylistType.FILLERS).getAsset().getName(), is("C"));
        assertThat(manager.nextItem(new BigDecimal("20"), PlaylistType.FILLERS).getAsset().getName(), is("B"));
        assertTrue(manager.itemsEquallyUsed());
    }

    public static List<PlaylistItem> createList(String... itemsString) {
        List<PlaylistItem> items = new ArrayList<PlaylistItem>();
        int i = 1;
        for (String itemString : itemsString) {
            Asset asset = new Asset();
            asset.setDuration(new BigDecimal(itemString.substring(1)));
            asset.setName(itemString.substring(0, 1));
            PlaylistItem item = new PlaylistItem();
            item.setSequence(i);
            item.setAsset(asset);
            items.add(item);
            i++;
        }
        return items;
    }

}
