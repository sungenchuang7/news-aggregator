import static org.junit.Assert.*;
import org.junit.Test;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

/**
 * @author ericfouh
 */
public class IndexBuilderTest {
    // ToDo


    @Test
    public void testParseFeed() {
        List<String> feeds = new ArrayList<>();
        feeds.add("https://www.cis.upenn.edu/~cit5940/sample_rss_feed.xml");
        IndexBuilder ib = new IndexBuilder();
        Map<String, List<String>> map = ib.parseFeed(feeds);

//        System.out.println(map);
        assertEquals(5, map.size());

//        System.out.println(map.get("https://www.seas.upenn.edu/~cit5940/page1.html"));
        assertEquals(10, map.get("https://www.seas.upenn.edu/~cit5940/page1.html").size());

//        System.out.println(map.get("https://www.seas.upenn.edu/~cit5940/page2.html"));
        assertEquals(55, map.get("https://www.seas.upenn.edu/~cit5940/page2.html").size());


    }

    @Test
    public void testBuildIndex() {
        List<String> feeds = new ArrayList<>();
        feeds.add("https://www.cis.upenn.edu/~cit5940/sample_rss_feed.xml");
        IndexBuilder ib = new IndexBuilder();
        Map<String, List<String>> fromParseFeed = ib.parseFeed(feeds);

        Map<String, Map<String, Double>> fromBuildIndex = ib.buildIndex(fromParseFeed);

        System.out.println(fromBuildIndex);




    }

}
