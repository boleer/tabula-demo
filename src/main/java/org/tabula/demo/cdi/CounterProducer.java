package org.tabula.demo.cdi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import jakarta.enterprise.inject.Produces;

import javafx.scene.control.ContextMenu;

import org.tabula.demo.CounterNode;

public class CounterProducer {

    private static final Predicate<String> imageNamePredicate = Pattern.compile(".*a.png").asMatchPredicate();
    private static final String IMAGE_PATH = "/images/counters/";

    @Produces
    public List<CounterNode> counters(ContextMenu cm) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("images/counters/images.properties")))) {
            return in.lines()
                .filter(imageNamePredicate)
                .map(p -> p.substring(0, p.length() - 5))
                .map(p -> (resourceAsStream(IMAGE_PATH + p + "b.png") == null) ?
                    new CounterNode(IMAGE_PATH + p + "a.png", null, cm) :
                    new CounterNode(IMAGE_PATH + p + "a.png", IMAGE_PATH + p + "b.png", cm)
                )
                .toList();
        } catch (IOException e) {
            throw new RuntimeException("Unable create counters", e);
        }
    }

    private InputStream resourceAsStream(String path) {
        return getClass().getResourceAsStream(path);
    }

}
