package org.sortdc.sortdc.resources.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "token")
public class TokenDTO {

    @XmlValue
    public String name;
    @XmlAttribute(required = false)
    public Integer count = 1;
}
