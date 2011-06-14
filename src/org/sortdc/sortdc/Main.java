package org.sortdc.sortdc;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import java.util.HashMap;
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

            Log.getInstance().add("Starting...");

            final String webserviceURI = config.getWebserviceURI();
            final Map<String, String> webserviceParams = new HashMap<String, String>();
            webserviceParams.put("com.sun.jersey.config.property.packages", "org.sortdc.sortdc.resources;org.codehaus.jackson.jaxrs");

            System.out.println("Starting grizzly...");
            SelectorThread threadSelector = GrizzlyWebContainerFactory.create(webserviceURI, webserviceParams);

            Log.getInstance().add("Webservice started: " + webserviceURI);
            System.out.println("Hit enter to stop it...");
            System.in.read();
            threadSelector.stopEndpoint();
            Log.getInstance().add("Webservice stopped.");
            System.exit(0);

        } catch (Exception e) {
            Log.getInstance().add(e);
        }

    }
}