package org.sortdc.sortdc;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Config config = Config.getInstance();
        try {
            config.loadFile("config/config.yaml");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            Classifier classifier = config.getClassifier(0);
            classifier.train("Bonjour je fais des confitures", "lolizator");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
