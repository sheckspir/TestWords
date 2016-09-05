package com.example.alexander.testwords.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Word implements Serializable {
    String id;
    String text;
    String translation;
    List<Altrernative> alternatives;
    List<String> images;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getTranslation() {
        return translation;
    }

    public List<Altrernative> getAlternatives() {
        return alternatives;
    }

    public List<String> getVariants(int sizeLimit) {
        List<Altrernative> shuffleAlternatives = new ArrayList<>(getAlternatives());
        Collections.shuffle(shuffleAlternatives);
        List<String> variants = new ArrayList<>();
        for (int i = 0; i < sizeLimit - 1 && i < getAlternatives().size(); i++) {
            variants.add(shuffleAlternatives.get(i).getText());
        }
        variants.add(text);
        Collections.shuffle(variants);
        while (variants.size() < sizeLimit) {
            variants.add("");
        }
        return variants;
    }

    public List<String> getImages() {
        if (images == null) {
            return new ArrayList<>();
        } else {
            return images;
        }
    }

    public String getMainImageUrl() {
        if (images.size() > 0) {
            return images.get(0);
        } else {
            return null;
        }
    }
}
