package soot.jimple.infoflow.methodSummary.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import soot.jimple.spark.summary.ClassObjects;
import soot.jimple.spark.xml.PAGReader;
import soot.jimple.spark.xml.PAGWriter;
import soot.jimple.spark.xml.SummaryXMLException;

public class TestXml {
	public TestXml(String pReadPath,String pWritePath,String mReadPath,String mWritePath,String className){
		ClassObjects classObjects=read(pReadPath);
		MethodSummaries methodSummaries=read(mReadPath,className,classObjects);
		write(classObjects,pWritePath);
		write(methodSummaries,mWritePath);
	}
	private void write(ClassObjects classObjects, String writePath) {
		// Create the target folder if it does not exist
		File f = new File(writePath);
		
		// Dump the flows
		PAGWriter writer = new PAGWriter();
		try {
			writer.write(f,classObjects);
		} catch (XMLStreamException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	public ClassObjects read(String readPath){
		File f=new File(readPath);
		PAGReader reader=new PAGReader();
		try {
			return reader.read(f);
		} catch (XMLStreamException | SummaryXMLException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private void write(MethodSummaries methodSummaries, String writePath) {
		// Create the target folder if it does not exist
		File f = new File(writePath);
		
		// Dump the flows
		XMLWriter writer = new XMLWriter();
		try {
			writer.write(f,methodSummaries);
		} catch (XMLStreamException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	public MethodSummaries read(String readPath,String className,ClassObjects classObjects){
		File f=new File(readPath);
		XMLReader reader=new XMLReader();
		try {
			return reader.read(f,className,classObjects);
		} catch (XMLStreamException | SummaryXMLException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
