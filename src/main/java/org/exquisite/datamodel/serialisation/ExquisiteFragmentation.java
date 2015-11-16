package org.exquisite.datamodel.serialisation;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.exquisite.datamodel.Fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XStreamAlias("ExquisiteFragmentation")

public class ExquisiteFragmentation implements Serializable {

    /**
     * auto generated serial UID
     */
    private static final long serialVersionUID = -8374394168805443639L;
    /**
     * A namespace required for the ExquisiteClient
     */
    @XStreamAsAttribute
    @XStreamAlias("xmlns:i")
    final String ilink = "http://www.w3.org/2001/XMLSchema-instance";
    /**
     * Another namespace required for the ExquisiteClient
     */
    @XStreamAsAttribute
    @XStreamAlias("xmlns")
    final String xmlns = "http://schemas.datacontract.org/2004/07/Exquisite.Domain.DataModels";
    @XStreamAlias("Fragments")
    public List<Fragment> Fragments;

    public ExquisiteFragmentation() {
        Fragments = new ArrayList<Fragment>();
    }

    public ExquisiteFragmentation(List<Fragment> fragments) {
        Fragments = fragments;
    }

    /**
     * Serializes object with namespaces for ExquisiteClient...
     *
     * @return
     */
    public String toXML() {
        XStream xs = new XStream();
        xs.autodetectAnnotations(true);
        xs.processAnnotations(ExquisiteFragmentation.class);
        String result = xs.toXML(this);
        return result;
    }

}
