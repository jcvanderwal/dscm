package org.estatio.dscm.services;

import org.estatio.dscm.dom.asset.Asset;
import org.estatio.dscm.dom.playlist.PlaylistItem;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PlaylistGeneratorTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testMultipleCycles() throws Exception {
        List<PlaylistItem> result = PlaylistGenerator.generate(
                createList("A10", "B10", "C10", "D10"),
                createList("X10", "Y10", "Z10"),
                new BigDecimal("60"));
        assertThat(sequenceStringof(result), is("ABCDXYABCDZXABCDYZ"));
    }

    @Test
    public void testMultipleCyclesWithVariableDuration() throws Exception {
        List<PlaylistItem> result = PlaylistGenerator.generate(
                createList("A10", "B10", "C10", "D10"),
                createList("X20", "Y10", "Z10"),
                new BigDecimal("60"));
        assertThat(sequenceStringof(result), is("ABCDXABCDYZ"));
    }

    @Test
    public void testMultipleCyclesWithVariableDurationMoreThanFourtySecondsOfMain() throws Exception {
        List<PlaylistItem> result = PlaylistGenerator.generate(
                createList("A10", "B10", "C10", "D10", "E10"),
                createList("X20", "Y10", "Z10"),
                new BigDecimal("60"));
        assertThat(sequenceStringof(result), is("ABCDXEABCYZDEABXCDEAYZBCDEXABCDYZEABCXDEABYZCDEAXBCDEYZ"));
    }

    @Test
    public void testMultipleCyclesWithVariableDurationAndDifferentOrder() throws Exception {
        List<PlaylistItem> result = PlaylistGenerator.generate(
                createList("A10", "B10", "C10", "D10"),
                createList("X10", "Y20", "Z10"),
                new BigDecimal("60"));
        assertThat(sequenceStringof(result), is("ABCDXZABCDY"));
    }

    @Test
    public void testWithEmptyPlaylist() throws Exception {
        List<PlaylistItem> result = PlaylistGenerator.generate(
                createList("A10", "B10", "C10", "D10"),
                new ArrayList<PlaylistItem>(),
                new BigDecimal("60"));
        assertThat(sequenceStringof(result), is("ABCD"));
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

    private String sequenceStringof(List<PlaylistItem> items) {
        String result = "";
        for (PlaylistItem item : items) {
            result = result.concat(item.getAsset().getName().substring(0, 1));
        }
        return result;
    }

}
