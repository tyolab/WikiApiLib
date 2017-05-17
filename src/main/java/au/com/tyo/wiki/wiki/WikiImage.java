package au.com.tyo.wiki.wiki;

/**
 * Created by monfee on 17/5/17.
 */

public class WikiImage extends WikiItem {

    private String imageUrl;

    public WikiImage(String url) {
        super(url);
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
