package org.sortdc.sortdc.resources.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.sortdc.sortdc.Config;

@XmlRootElement(name = "classifier")
public class ClassifierDTO {

    private String id;
    private String href;
    private String categories_href;

    public ClassifierDTO() {
    }

    public ClassifierDTO(String id) throws Exception {
        this.id = id;
        this.href = Config.getInstance().getWebserviceURI() + "classifiers/" + id;
    }

    @XmlElement
    public String getId() throws Exception {
        return this.id;
    }

    @XmlElement
    public String getHref() throws Exception {
        return this.href;
    }

    @XmlElement
    public String getCategories_href() throws Exception {
        return this.categories_href;
    }

    public void enableCategories_href(boolean enable) {
        this.categories_href = enable ? this.href + "/categories" : null;
    }
}
