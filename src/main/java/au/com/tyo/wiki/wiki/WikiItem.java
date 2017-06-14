package au.com.tyo.wiki.wiki;

import java.io.Serializable;

/**
 * Created by monfee on 17/5/17.
 */

public class WikiItem implements Serializable {

    public enum ItemType {PAGE, TEMPLATE, FILE, FILE_IMAGE, MEDIA};

    private int index;

    private ItemType type;

    protected String title;

    public WikiItem() {
    }

    public WikiItem(String title) {
        setTitle(title);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
