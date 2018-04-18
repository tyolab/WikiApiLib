package au.com.tyo.wiki.wiki;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

    @Override
    public void serialise(ObjectOutputStream stream) throws IOException {
        super.serialise(stream);

        stream.writeObject(imageUrl);
    }

    @Override
    public void deserialise(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        super.deserialise(stream);

        imageUrl = (String) stream.readObject();
    }

}
