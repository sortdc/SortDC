package org.sortdc.sortdc;

import java.util.Map;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Config config = Config.getInstance();
        try {
            config.loadFile("config/config.yaml");
            config.applyLogConfig();
        } catch (Exception e) {
            Log.getInstance().add(e);
        }

        Log.getInstance().add("Starting...");

        try {
            Map<String, Classifier> classifiers = config.getClassifiers();
            classifiers.get("languages").train("text1", "Bonjour je fais des confitures", "lolizator");
        } catch (Exception e) {
            Log.getInstance().add(e);
        }
    }
}
