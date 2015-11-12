package org.exquisite.datamodel;

import org.exquisite.datamodel.ExquisiteEnums.ContentTypes;

import com.thoughtworks.xstream.annotations.XStreamAlias;
/** 
 * <tt>ExquisiteMessageItem</tt> represents a specfic message that can be sent to a client.<p>
 * 
 * The <strong>content</strong> field contains the message payload.<p>
 * The <strong>type</strong> field is a descriptor of the content that may help the client in processing the message content. <p>
 * 
 * <strong>WARN:</strong> If you add any other fields to this class later, insure <strong>all</strong> fields are in alphabetical order... .Net deserializes fields in <em>alphabetical</em>
 * order and skips any fields that are not in order e.g:<p>
 *  
 *  content, type = GOOD <p>
 *  type, content = BAD (would return Type but content would be null since the deserializer would
 *  be looking for field names starting with T or "greater"...)
 *  
 *  @author David
 *  @see org.exquisite.datamodel.ExquisiteMessageItem 
 */
	
@XStreamAlias("ExquisiteMessageItem")
public class ExquisiteMessageItem {
	
	/**
	 * The actual message content, e.g. a tests.diagnosis result, system notification message etc.
	 */
	@XStreamAlias("Content")
	public String content;
	
	/**
	 * A string describing the type/structure of content found in this message item. Useful
	 * so the client can process the message content appropriately. 
	 */
	@XStreamAlias("Type")
	public ContentTypes type;		
}