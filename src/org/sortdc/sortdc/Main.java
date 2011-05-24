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
            Classifier classifier = classifiers.get("thematics");
            classifier.train("text1", "La cuisine est l'ensemble des techniques de préparation des aliments en vue de leur consommation par les êtres humains (voir cuisinerie). La cuisine est diverse à travers le monde, fruit des ressources naturelles locales, mais aussi de la culture et des croyances, du perfectionnement des techniques, des échanges entre peuples et cultures.", "cuisine");
            classifier.train("text2", "La cuisine a ainsi dépassé son simple impératif biologique d'alimentation pour devenir un corpus de techniques plus ou moins pointues, un fait culturel, un élément de patrimoine et d'identité national ou familial, un élément de systèmes de valeur, mais aussi un sujet d'étude pour les sciences sociales et la sociologie, voire un enjeu de politique et de santé publique.", "cuisine");
            classifier.train("text3", "La cuisine européenne a été enrichie par les apports des Croisés de retour des Croisades et, bouleversée par les produits rapportés d'Amérique aux xve et xvie siècles, qui sont rentrés dans la tradition européenne (tomate, dindon, pomme de terre, etc.).", "cuisine");
            classifier.train("text4", "Git est un logiciel de gestion de versions décentralisée. C'est un logiciel libre créé par Linus Torvalds, le créateur du noyau Linux, et distribué sous la GNU GPL version 2.", "informatique");
            classifier.train("text5", "Mercurial est un système de gestion de versions permettant en particulier la gestion de version décentralisée. Il est disponible sur la plupart des systèmes Unix et Windows.", "informatique");
            classifier.train("text6", "Solaris est un système d'exploitation UNIX propriétaire développé à l'origine par Sun Microsystems. Ce système s'appelle dorénavant Oracle Solaris depuis le rachat de Sun par Oracle en janvier 2010.", "informatique");
            classifier.train("text7", "Linux est un logiciel libre créé en 1991 par Linus Torvalds et développé sur Internet par des milliers d’informaticiens bénévoles ou salariés1. C'est le noyau de nombreux systèmes d’exploitation. Il est de type UNIX et compatible POSIX.", "informatique");
            System.out.println("Catégorisation 1 (SVN) : " + classifier.categorize("Subversion (abrégé SVN) est un système de gestion de versions visant à remplacer CVS mais ne permettant pas de gestion décentralisée."));
            System.out.println("Catégorisation 2 (Clafouti) : " + classifier.categorize("Le clafouti est un gâteau composé de cerises masquées d'un appareil à flan. Traditionnellement, on enlève pas le noyau des cerises."));
        } catch (Exception e) {
            Log.getInstance().add(e);
        }
    }
}
