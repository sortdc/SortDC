package org.sortdc.sortdc.resources.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "categories")
public class CategoriesDTO extends ArrayList<CategoryDTO> {

    @XmlElementRef(type = CategoryDTO.class)
    public List<CategoryDTO> getList() {
        return (List<CategoryDTO>) this;
    }
}
