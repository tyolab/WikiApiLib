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
    public void writeObject(ObjectOutputStream stream) throws IOException {
        super.writeObject(stream);

        stream.writeObject(imageUrl);
    }

    @Override
    public void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        super.readObject(stream);

        imageUrl = (String) stream.readObject();
    }
}
