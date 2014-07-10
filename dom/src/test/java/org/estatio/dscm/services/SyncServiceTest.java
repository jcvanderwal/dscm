package org.estatio.dscm.services;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.estatio.dscm.dom.asset.Asset;
import org.estatio.dscm.dom.playlist.PlaylistItem;

public class SyncServiceTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testCreate() throws Exception {
        List<PlaylistItem> list = createList("A10", "B10", "C20");
        assertThat(list.size(), is(3));
        assertThat(SyncService.totalDurationf(list), is(new BigDecimal("40")));
        assertThat(list.get(2).getAsset().getName(), is("C"));
    }

    @Test
    public void testMultipleCycles() throws Exception {
        List<PlaylistItem> result = SyncService.createPlaylist(
                createList("A10", "B10", "C10", "D10"),
                createList("X10", "Y10", "Z10"),
                new BigDecimal("60"));

        assertThat(result.size(), is(18));
        assertThat(sequenceStringof(result), is("ABCDXYABCDZXABCDYZ"));

    }

    @Test
    public void testMultipleCyclesWithVariableDuration() throws Exception {
        List<PlaylistItem> result = SyncService.createPlaylist(
                createList("A10", "B10", "C10", "D10"),
                createList("X20", "Y10", "Z10"),
                new BigDecimal("60"));

        assertThat(result.size(), is(11));
        assertThat(sequenceStringof(result), is("ABCDXABCDYZ"));

    }

    @Test
    public void testMultipleCyclesWithVariableDurationAndDifferentOrder() throws Exception {
        List<PlaylistItem> result = SyncService.createPlaylist(
                createList("A10", "B10", "C10", "D10"),
                createList("X10", "Y20", "Z10"),
                new BigDecimal("60"));

        assertThat(result.size(), is(11));
        assertThat(sequenceStringof(result), is("ABCDXZABCDY"));

    }

    
    private List<PlaylistItem> createList(String... itemsString) {
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
    
    private String sequenceStringof(List<PlaylistItem> items){
        String result = "";
        for (PlaylistItem item : items){
            result = result.concat(item.getAsset().getName().substring(0, 1));
        }
        return result;
    }
    
}
