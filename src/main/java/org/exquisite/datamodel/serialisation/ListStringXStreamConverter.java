package org.exquisite.datamodel.serialisation;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.List;

/**
 * the .net client has specific markup requirements for deserialising typed arrays.
 * this converter adds the required namespace to a collection of strings.
 *
 * @author David
 */
public class ListStringXStreamConverter implements Converter {

    private String alias;

    public ListStringXStreamConverter(String alias) {
        super();
        this.alias = alias;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean canConvert(Class type) {
        return true;
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        @SuppressWarnings("unchecked")
        List<String> list = (List<String>) source;
        writer.addAttribute("xmlns:d4p1", "http://schemas.microsoft.com/2003/10/Serialization/Arrays");

        for (String string : list) {
            writer.startNode(alias);
            writer.setValue(string);
            writer.endNode();
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        throw new UnsupportedOperationException(
                "ListToStringXStreamConverter does not offer suport for unmarshal operation");
    }
}