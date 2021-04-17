package org.tabula.demo.cdi;

import javafx.fxml.FXMLLoader;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

public class FXMLLoaderProducer {

    @Inject
    private Instance<Object> instance;

    @Produces
    public FXMLLoader createLoader() {
        FXMLLoader.setDefaultClassLoader(this.getClass().getClassLoader());
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(param -> instance.select(param).get());
        return loader;
    }

}
