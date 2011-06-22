package org.sortdc.sortdc.resources.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import org.sortdc.sortdc.Config;

@XmlRootElement(name = "documents")
public class DocumentsDTO extends ArrayList<DocumentDTO> {

    @XmlAttribute
    public String href;

    public DocumentsDTO() {
    }

    public DocumentsDTO(String classifier_id) {
        try {
            this.href = Config.getInstance().getWebserviceURI() + "classifiers/" + classifier_id + "/documents";
        } catch (Exception e) {
        }
    }

    public DocumentsDTO(String classifier_id, String category_id) {
        try {
            this.href = Config.getInstance().getWebserviceURI() + "classifiers/" + classifier_id + "/categories/" + category_id + "/documents";
        } catch (Exception e) {
        }
    }

    @XmlElementRef(type = DocumentDTO.class)
    public List<DocumentDTO> getList() {
        return (List<DocumentDTO>) this;
    }
}
