package org.sortdc.sortdc.resources.dto;

import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.sortdc.sortdc.Config;

@XmlRootElement(name = "document")
public class DocumentDTO {

    private String id;
    private String href;
    private CategoryDTO category;
    private Map<String, Integer> tokens;

    public DocumentDTO() {
    }

    public DocumentDTO(String classifier_id, String id) {
        this.id = id;
        try {
            this.href = Config.getInstance().getWebserviceURI() + "classifiers/" + classifier_id + "/documents/" + id;
        } catch (Exception e) {
        }
    }

    @XmlAttribute
    public String getId() {
        return this.id;
    }

    @XmlAttribute
    public String getHref() {
        return this.href;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    @XmlElement
    public CategoryDTO getCategory() {
        return this.category;
    }

    public void setTokens(Map<String, Integer> tokens) {
        this.tokens = tokens;
    }

    public Map<String, Integer> getTokens() {
        return this.tokens;
    }
}
