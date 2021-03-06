package com.workshop.translationworkshop.utils;

import com.workshop.translationworkshop.Application;
import javafx.scene.text.Font;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TTFData {

    public Font font, font2x;
    public String name, fileName;
    public boolean isPixelFont = false;
    public int glyphShift = 0;

    public TTFData(String filename) {
        this.fileName = filename;
        this.font = Font.loadFont(Application.class.getResource("fonts/" + filename).toExternalForm(), 72);
        this.name = this.font.getName();
    }

    public TTFData(String filename, double size) {
        this.fileName = filename;
        this.font = Font.loadFont(
                Application.class.getResource("fonts/" + filename).toExternalForm(),
                size
        );
        this.font2x = Font.loadFont(
                Application.class.getResource("fonts/" + filename).toExternalForm(),
                size * 2
        );
        this.name = this.font.getName();
        this.isPixelFont = true;
    }

    public String toString() {
        return fileName;
    }


    static public List<TTFData> fonts = new ArrayList<>();

    // это названия тех шрифтов, которые считаются пиксельными
    static private String[] pixelFontNames = {
            "cc.yal.7w7.ttf",
            "cc.yal.6w4.ttf",
            "Nanoscript.ttf",
            "Tensticks.ttf",
            "Picoscript.ttf",
            "picoscriptbolt.ttf",
            "comic-sans-ms-pixel-rus-eng.ttf",
            "Determination_mono.otf"                // тут не 16 а другое число
    };

    static public void loadTTFFonts() {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource("com/workshop/translationworkshop/fonts");
        String path = url.getPath();
        File[] files = new File(path).listFiles();
        List<String> strList = Arrays.stream(pixelFontNames).toList();
        for (File f : files) {
            String name = f.getName();
            System.out.println(name);
            if(strList.contains(name)) {
                // pixel font
                TTFData ttf = new TTFData(name, 16.0);
                ttf.glyphShift = 1; // эти значения должны быть индивидуальны для каждого шрифта
                fonts.add(ttf);
            } else {
                // normal font
                fonts.add(new TTFData(name));
            }
        }

    }


}
