package au.com.tyo.wiki.wiki;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import au.com.tyo.io.ItemSerializable;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 17/5/17.
 */

public class WikiItem extends ItemSerializable {

    public enum ItemType {UNKNOWN, PAGE, TEMPLATE, FILE, FILE_IMAGE, MEDIA};

    private int index;

    private ItemType type;

    protected String title;

    public WikiItem() {
        index = -1;
        type = ItemType.UNKNOWN;
        title = null;
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

    @Override
    public void serialise(ObjectOutputStream stream) throws IOException {
        stream.writeInt(index);
        stream.writeObject(type);
        stream.writeObject(title);
    }

    @Override
    public void deserialise(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        index = stream.readInt();
        type = (ItemType) stream.readObject();
        title = (String) stream.readObject();
    }
}
