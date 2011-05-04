package org.sortdc.sortdc;

import java.util.List;

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

        List classifiers = (List) config.get(Config.CLASSIFIERS_LIST);
        System.out.println(classifiers);
    }
}
