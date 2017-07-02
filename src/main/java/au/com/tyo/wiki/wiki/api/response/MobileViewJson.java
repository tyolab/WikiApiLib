package au.com.tyo.wiki.wiki.api.response;

import com.google.api.client.util.Key;

import java.util.List;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 29/6/17.
 */

public class MobileViewJson extends Response {

    public static class MobileView {

        @Key
        public String mainpage;

        @Key
        public List<Section> sections;

    }

    public static class Section {

        @Key
        public int id;

        @Key
        public String text;
    }


    @Key
    public MobileView mobileview;
}
