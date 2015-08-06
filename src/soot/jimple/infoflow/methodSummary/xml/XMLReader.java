package soot.jimple.infoflow.methodSummary.xml;

import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.ATTRIBUTE_BASETYPE;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.ATTRIBUTE_FLOWTYPE;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.ATTRIBUTE_PARAMTER_INDEX;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.ATTRIBUTE_TAINT_SUB_FIELDS;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.TREE_FLOW;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.TREE_METHOD;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.TREE_SINK;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.TREE_SOURCE;
import static soot.jimple.infoflow.methodSummary.xml.XMLConstants.VALUE_TRUE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import soot.jimple.infoflow.methodSummary.data.sourceSink.FlowSink;
import soot.jimple.infoflow.methodSummary.data.sourceSink.FlowSource;
import soot.jimple.infoflow.methodSummary.data.summary.GapDefinition;
import soot.jimple.infoflow.methodSummary.data.summary.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import soot.jimple.infoflow.methodSummary.data.summary.SourceSinkType;


public class XMLReader {
	
	enum State{
		summary, methods, method, flow, gaps, gap
	}

	/**
	 * Reads a summary xml file and returns the MethodSummaries which are saved
	 * in that file 
	 */
	public MethodSummaries read(File fileName) throws XMLStreamException,
			FileNotFoundException, SummaryXMLException{
		MethodSummaries summary = new MethodSummaries();
		
		InputStream in = new FileInputStream(fileName);
		XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(in);
		
		Map<String, String> sourceAttributes = new HashMap<String,String>();
		Map<String, String> sinkAttributes = new HashMap<String,String>();
		
		String currentMethod = "";
		int currentID = -1;
		boolean isAlias = false;
		
		State state = State.summary;
		while(reader.hasNext()){
			// Read the next tag
			reader.next();
			if(!reader.hasName())
				continue;
			
			if (reader.getLocalName().equals(XMLConstants.TREE_METHODS) && reader.isStartElement()) {
				if (state == State.summary)
					state = State.methods;
				else
					throw new SummaryXMLException();
			}
			else if (reader.getLocalName().equals(TREE_METHOD) && reader.isStartElement() ){
				if(state == State.methods){
					currentMethod = getAttributeByName(reader, XMLConstants.ATTRIBUTE_METHOD_SIG);
					state = State.method;
				}			
				else
					throw new SummaryXMLException();
			}else if (reader.getLocalName().equals(TREE_METHOD) && reader.isEndElement() ){
				if(state == State.method)
					state = State.methods;
				else
					throw new SummaryXMLException();
			}
			else if (reader.getLocalName().equals(TREE_FLOW) && reader.isStartElement()) {
				if(state == State.method){
					sourceAttributes.clear();
					sinkAttributes.clear();
					state = State.flow;
					String sAlias = getAttributeByName(reader, XMLConstants.ATTRIBUTE_IS_ALIAS);
					isAlias = sAlias != null && sAlias.equals(XMLConstants.VALUE_TRUE);
				}
				else
					throw new SummaryXMLException();
			}
			else if (reader.getLocalName().equals(TREE_SOURCE) && reader.isStartElement()){
				if(state == State.flow){
				for (int i = 0; i < reader.getAttributeCount(); i++)
					sourceAttributes.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
				}
				else
					throw new SummaryXMLException();
			}
			else if(reader.getLocalName().equals(TREE_SINK) && reader.isStartElement()){
				if(state == State.flow){
				for (int i = 0; i < reader.getAttributeCount(); i++)
					sinkAttributes.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
				}
				else
					throw new SummaryXMLException();
			}
			else if(reader.getLocalName().equals(TREE_FLOW) && reader.isEndElement()){
				if(state == State.flow){
					state = State.method;					
					MethodFlow flow = new MethodFlow(currentMethod,
							createSource(summary, sourceAttributes),
							createSink(summary, sinkAttributes),
							isAlias);
					summary.addFlow(flow);
					
					isAlias = false;
				}
				else
					throw new SummaryXMLException();
			}
			else if (reader.getLocalName().equals(XMLConstants.TREE_METHODS) && reader.isEndElement()) {
				if (state == State.methods)
					state = State.summary;
				else
					throw new SummaryXMLException();
			}
			else if(reader.getLocalName().equals(XMLConstants.TREE_GAPS) && reader.isStartElement()){
				if(state == State.summary)
					state = State.gaps;
				else
					throw new SummaryXMLException();
			}
			else if(reader.getLocalName().equals(XMLConstants.TREE_GAPS) && reader.isEndElement()){
				if(state == State.gaps)
					state = State.summary;
				else
					throw new SummaryXMLException();
			}
			else if(reader.getLocalName().equals(XMLConstants.TREE_GAP) && reader.isStartElement()){
				if(state == State.gaps) {
					currentMethod = getAttributeByName(reader, XMLConstants.ATTRIBUTE_METHOD_SIG);
					currentID = Integer.valueOf(getAttributeByName(reader, XMLConstants.ATTRIBUTE_ID));
					summary.getOrCreateGap(currentID, currentMethod);
					state = State.gap;
				}
				else
					throw new SummaryXMLException();
			}
			else if (reader.getLocalName().equals(XMLConstants.TREE_GAP) && reader.isEndElement()){
				if(state == State.gap) {
					state = State.gaps;
				}
				else
					throw new SummaryXMLException();
			}
		}
		
		// Validate the summary to make sure that we didn't read in any bogus
		// stuff
		summary.validate();
		
		return summary;
	}


	/**
	 * Gets the value of the XML attribute with the specified id
	 * @param reader The reader from which to get the XML data
	 * @param id The attribute id for which to get the data
	 * @return The data of the given attribute if it exists, otherwise an
	 * empty string
	 */
	private String getAttributeByName(XMLStreamReader reader, String id) {
		for (int i = 0; i < reader.getAttributeCount(); i++)
			if (reader.getAttributeLocalName(i).equals(id))
				return reader.getAttributeValue(i);
		return "";
	}
	
	/**
	 * Creates a new source data object from the given XML attributes
	 * @param summary The method summary for which to create the new flow source
	 * @param attributes The XML attributes for the source
	 * @return The newly created source data object
	 * @throws SummaryXMLException
	 */
	private FlowSource createSource(MethodSummaries summary,
			Map<String, String> attributes) throws SummaryXMLException{
		if (isField(attributes)) {
			return new FlowSource(SourceSinkType.Field,
					getBaseType(attributes),
					getAccessPath(attributes),
					getAccessPathTypes(attributes),
					getGapDefinition(attributes, summary));
		}
		else if (isParameter(attributes)) {
			return new FlowSource(SourceSinkType.Parameter,
					paramterIdx(attributes),
					getBaseType(attributes), 
					getAccessPath(attributes),
					getAccessPathTypes(attributes),
					getGapDefinition(attributes, summary));
		}
		else if (isGapBaseObject(attributes)) {
			return new FlowSource(SourceSinkType.GapBaseObject,
					getBaseType(attributes),
					getGapDefinition(attributes, summary));
		}
		else if (isReturn(attributes)) {
			GapDefinition gap = getGapDefinition(attributes, summary);
			if (gap == null)
				throw new SummaryXMLException("Return values can only be "
						+ "sources if they have a gap specification");
			
			return new FlowSource(SourceSinkType.Return,
					getBaseType(attributes), 
					getAccessPath(attributes),
					getAccessPathTypes(attributes),
					getGapDefinition(attributes, summary));
		}
		throw new SummaryXMLException("Invalid flow source definition");
	}
	
	/**
	 * Creates a new sink data object from the given XML attributes
	 * @param summary The method summary for which to create the new flow source
	 * @param attributes The XML attributes for the sink
	 * @return The newly created sink data object
	 * @throws SummaryXMLException
	 */
	private FlowSink createSink(MethodSummaries summary,
			Map<String, String> attributes) throws SummaryXMLException{
		if (isField(attributes)) {
			return new FlowSink(SourceSinkType.Field,
					getBaseType(attributes),
					getAccessPath(attributes),
					getAccessPathTypes(attributes),
					taintSubFields(attributes),
					getGapDefinition(attributes, summary));
		}
		else if (isParameter(attributes)) {
			return new FlowSink(SourceSinkType.Parameter,
					paramterIdx(attributes),
					getBaseType(attributes),
					getAccessPath(attributes),
					getAccessPathTypes(attributes),
					taintSubFields(attributes),
					getGapDefinition(attributes, summary));
		}
		else if (isReturn(attributes)) {
			return new FlowSink(SourceSinkType.Return,
					getBaseType(attributes),
					getAccessPath(attributes),
					getAccessPathTypes(attributes),
					taintSubFields(attributes),
					getGapDefinition(attributes, summary));
		}
		else if (isGapBaseObject(attributes)) {
			return new FlowSink(SourceSinkType.GapBaseObject,
					-1,
					getBaseType(attributes),
					false,
					getGapDefinition(attributes, summary));
		}
		throw new SummaryXMLException();
	}
	
	private boolean isReturn(Map<String, String> attributes){
		return attributes.get(ATTRIBUTE_FLOWTYPE).equals(SourceSinkType.Return.toString());
	}
	
	private boolean isField(Map<String, String> attributes){
		return attributes.get(ATTRIBUTE_FLOWTYPE).equals(SourceSinkType.Field.toString());
	}
	
	private String[] getAccessPath(Map<String, String> attributes){
		String ap = attributes.get(XMLConstants.ATTRIBUTE_ACCESSPATH);
		if(ap != null){
			if(ap.length() > 3){
				 String[] res = ap.substring(1, ap.length()-1).split(",");
				 for(int i = 0; i < res.length; i++)
					 res[i] = res[i].trim();
				 return res;
			}
		}
		return null;
	}
	
	private String[] getAccessPathTypes(Map<String, String> attributes){
		String ap = attributes.get(XMLConstants.ATTRIBUTE_ACCESSPATHTYPES);
		if(ap != null){
			if(ap.length() > 3){
				 String[] res = ap.substring(1, ap.length()-1).split(",");
				 for(int i = 0; i < res.length; i++)
					 res[i] = res[i].trim();
				 return res;
			}
		}
		return null;
	}
	
	private boolean isParameter(Map<String, String> attributes){
		return attributes.get(ATTRIBUTE_FLOWTYPE).equals(SourceSinkType.Parameter.toString());
	}
	
	private boolean isGapBaseObject(Map<String, String> attributes){
		return attributes.get(ATTRIBUTE_FLOWTYPE).equals(SourceSinkType.GapBaseObject.toString());
	}

	private int paramterIdx(Map<String, String> attributes){
		return  Integer.parseInt(attributes.get(ATTRIBUTE_PARAMTER_INDEX));
	}
	
	private String getBaseType(Map<String, String> attributes) {
		return attributes.get(ATTRIBUTE_BASETYPE);
	}
	
	private boolean taintSubFields(Map<String, String> attributes){
		String val = attributes.get(ATTRIBUTE_TAINT_SUB_FIELDS);
		return val != null && val.equals(VALUE_TRUE);
	}
	
	private GapDefinition getGapDefinition(Map<String, String> attributes,
			MethodSummaries summary) {
		String id = attributes.get(XMLConstants.ATTRIBUTE_GAP);
		if (id == null  || id.isEmpty())
			return null;
		
		// Do we already have a suitable gap definition?
		GapDefinition gap = summary.getGap(Integer.parseInt(id));
		if (gap != null)
			return gap;
		
		// We have not read in this gap definition yet and need to create a stub
		// for the time being.
		return summary.createTemporaryGap(Integer.parseInt(id));
	}
	
}


