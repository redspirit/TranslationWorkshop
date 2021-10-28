package com.workshop.translationworkshop.utils;

import com.workshop.translationworkshop.Application;
import javafx.scene.text.Font;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TTFData {

    public Font font;
    public String name, fileName;

    public TTFData(String filename) {
        this.fileName = filename;
        this.font = Font.loadFont(Application.class.getResource("fonts/" + filename).toExternalForm(), 72);
        this.name = this.font.getName();
    }

    public String toString() {
        return name;
    }


    static public List<TTFData> fonts = new ArrayList<>();

    static public void loadTTFFonts() {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("com/workshop/translationworkshop/fonts");
        String path = url.getPath();
        File[] files = new File(path).listFiles();
        for (File f : files) {
            fonts.add(new TTFData(f.getName()));
        }

    }


}
