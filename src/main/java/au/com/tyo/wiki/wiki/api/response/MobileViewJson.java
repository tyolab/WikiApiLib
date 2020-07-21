package au.com.tyo.wiki.wiki.api.response;

import java.util.List;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 29/6/17.
 */

public class MobileViewJson extends Response {

    public static class MobileView {

        
        public String mainpage;

        
        public List<Section> sections;

    }

    public static class Section {

        
        public int id;

        
        public String text;
    }


    
    public MobileView mobileview;
}
