package sample.client;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by Evgenij on 24.12.2016.
 */
public class TitlesMessage implements Message{
    public TreeMap<Integer, String> getTitles() {
        return titles;
    }

    public void setTitles(TreeMap<Integer, String> titles) {
        this.titles = titles;
    }

    TreeMap<Integer, String> titles;

    public TitlesMessage(TreeMap<Integer, String> titles){
        this.titles = titles;
    }


}
