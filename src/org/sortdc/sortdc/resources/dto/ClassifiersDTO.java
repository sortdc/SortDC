package org.sortdc.sortdc.resources.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "classifiers")
public class ClassifiersDTO extends ArrayList<ClassifierDTO> {

    @XmlElementRef(type = ClassifierDTO.class)
    public List<ClassifierDTO> getList() {
        return (List<ClassifierDTO>) this;
    }
}
