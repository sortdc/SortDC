package org.sortdc.sortdc.resources.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.sortdc.sortdc.Config;

@XmlRootElement(name = "category")
public class CategoryDTO {

    private String id;
    private String href;

    public CategoryDTO() {
    }

    public CategoryDTO(String classifier_id, String id) throws Exception {
        this.id = id;
        this.href = Config.getInstance().getWebserviceURI() + "classifiers/" + classifier_id + "/categories/" + id;
    }

    @XmlElement
    public String getId() throws Exception {
        return this.id;
    }

    @XmlElement
    public String getHref() throws Exception {
        return this.href;
    }
}
