package sample.client;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

/**
 * Created by Evgenij on 24.12.2016.
 */
public class TitlesMessage implements Message{
    public HashMap<Integer, String> getTitles() {
        return titles;
    }

    public void setTitles(HashMap<Integer, String> titles) {
        this.titles = titles;
    }

    HashMap<Integer, String> titles;

    public TitlesMessage(HashMap<Integer, String> titles){
        this.titles = titles;
    }


}
