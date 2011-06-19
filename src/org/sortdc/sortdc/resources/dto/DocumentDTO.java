package org.sortdc.sortdc.resources.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.sortdc.sortdc.Config;

@XmlRootElement(name = "document")
public class DocumentDTO {

    @XmlAttribute
    public String id;
    private String classifier_id;
    @XmlElement
    public CategoryDTO category;
    @XmlElement
    public String text;
    @XmlElement
    public String html;
    @XmlElementWrapper(name = "tokens")
    @XmlElement(name = "token")
    public List<TokenDTO> tokens;

    public DocumentDTO() {
    }

    public DocumentDTO(String classifier_id, String id) {
        this.id = id;
        this.classifier_id = classifier_id;
    }

    @XmlAttribute
    public String getHref() {
        if (this.id == null || this.classifier_id == null) {
            return null;
        }
        try {
            return Config.getInstance().getWebserviceURI() + "classifiers/" + this.classifier_id + "/documents/" + this.id;
        } catch (Exception e) {
            return null;
        }
    }

    @XmlTransient
    public Map<String, Integer> getTokens() {
        Map<String, Integer> tokens_map = new HashMap<String, Integer>();
        for (TokenDTO token_dto : this.tokens) {
            tokens_map.put(token_dto.name, token_dto.count);
        }
        return tokens_map;
    }

    public void setTokens(Map<String, Integer> tokens) {
        this.tokens = new ArrayList<TokenDTO>();
        for (Map.Entry<String, Integer> token : tokens.entrySet()) {
            TokenDTO token_dto = new TokenDTO();
            token_dto.name = token.getKey();
            token_dto.count = token.getValue();
            this.tokens.add(token_dto);
        }
    }
}
