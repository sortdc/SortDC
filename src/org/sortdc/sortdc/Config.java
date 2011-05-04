package org.sortdc.sortdc;

import com.esotericsoftware.yamlbeans.YamlReader;
import java.io.FileReader;
import java.util.Map;

public class Config {

    private static Config instance;
    private Map parameters;

    private Config() {
    }

    public static synchronized Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public void loadFile(String filePath) throws Exception {
        YamlReader reader = new YamlReader(new FileReader(filePath));
        this.parameters = (Map) reader.read();
    }

    public Object get(String parameter) {
        return this.parameters.get(parameter);
    }
}
