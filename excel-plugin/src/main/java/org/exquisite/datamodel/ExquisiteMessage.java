package org.exquisite.datamodel;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.exquisite.datamodel.ExquisiteEnums.StatusCodes;
import org.exquisite.datamodel.serialisation.ExquisiteDiagnosisResult;
import org.exquisite.datamodel.serialisation.ExquisiteDiagnosisResults;

import java.util.ArrayList;
import java.util.List;

/**
 * A message body which can contain multiple messages (items).
 * Provides additional fields that store the host name and host status.
 *
 * @author David
 */
@XStreamAlias("ExquisiteMessage")
public class ExquisiteMessage {

    /**
     * A namespace required for the ExquisiteClient
     */
    @XStreamAsAttribute
    @XStreamAlias("xmlns:i")
    final String ilink = "http://www.w3.org/2001/XMLSchema-instance";
    /**
     * A namespace required for the ExquisiteClient
     */
    @XStreamAsAttribute
    @XStreamAlias("xmlns")
    final String xmlns = "http://schemas.datacontract.org/2004/07/Exquisite.Domain.DataModels";
    /**
     * Name of object that sent this message.
     */
    @XStreamAlias("Host")
    public String host;
    /**
     * A collection of message items associated with this message.
     */
    @XStreamAlias("Items")
    public List<ExquisiteMessageItem> items = new ArrayList<ExquisiteMessageItem>();
    /**
     * A status from the host, e.g. OK, Error, Busy... to give more context to the message.
     */
    @XStreamAlias("Status")
    public StatusCodes status;

    /**
     * @param add a message item to this message.
     */
    public void addItem(ExquisiteMessageItem item) {
        this.items.add(item);
    }

    /**
     * @return total number of message items in this message body.
     */
    public int getItemSize() {
        return this.items.size();
    }

    /**
     * Creates an XML representation of this object that can be sent over a socket connection to a client.
     *
     * @return XML serialized string representation of this class. All indenting/whitespace removed.
     */
    public String toXML() {
        //Configure xstream
        XStream xs = new XStream();
        xs.autodetectAnnotations(true);
        xs.processAnnotations(ExquisiteMessage.class);
        xs.processAnnotations(ExquisiteMessageItem.class);
        xs.processAnnotations(ExquisiteDiagnosisResults.class);
        xs.processAnnotations(ExquisiteDiagnosisResult.class);
        //perform the serialization (just return the string representation)
        String xsResult = xs.toXML(this);
        //remove whitespace, newlines etc. so resulting output is on one line.
        return xsResult.replaceAll("\\s*[\\r\\n]+\\s*", "").trim();
    }
}
