package org.sortdc.sortdc;

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
            Log.getInstance().add(e.getMessage());
        }

        Log.getInstance().add("Starting...");

        try {
            Classifier classifier = config.getClassifier(0);
            classifier.train("text1", "Bonjour je fais des confitures", "lolizator");
        } catch (Exception e) {
            Log.getInstance().add(e.getMessage());
        }
    }
}
