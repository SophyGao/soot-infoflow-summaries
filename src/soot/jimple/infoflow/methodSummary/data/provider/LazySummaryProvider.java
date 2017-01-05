package soot.jimple.infoflow.methodSummary.data.provider;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.jimple.infoflow.methodSummary.data.summary.ClassSummaries;
import soot.jimple.infoflow.methodSummary.data.summary.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import soot.jimple.infoflow.methodSummary.xml.XMLReader;


/**
 * This class loads method summary xml files on demand.
 *
 */
public class LazySummaryProvider implements IMethodSummaryProvider {

	private XMLReader reader;
	private ClassSummaries summaries = new ClassSummaries();
	private Set<String> supportedClasses = new HashSet<String>();
	private Set<String> loadableClasses = new HashSet<String>();
	private Set<File> files;

	/**
	 * Loads a file or all files in a dir (not recursively)
	 * @param source
	 */
	public LazySummaryProvider(File source) {
		if (!source.exists())
			throw new RuntimeException("Source directory " + source + " does not exist");
		
		if (source.isFile())
			files = Collections.singleton(source);
		else if (source.isDirectory()) {
			File[] filesInDir = source.listFiles();
			if (filesInDir == null)
				throw new RuntimeException("Could not get files in directory " + source);
			files = new HashSet<File>(Arrays.asList(filesInDir));
		}
		else
			throw new RuntimeException("Invalid input file: " + source);
		
		init();
	}

	public LazySummaryProvider(List<File> files) {
		this.files = new HashSet<File>();
		for(File f : files) {
			// Check if the file exists
			if (!f.exists())
				throw new RuntimeException("Input file does not exist: " + f);
			
			// Distinguish between files and directories
			if (f.isFile())
				this.files.add(f);
			else if (f.isDirectory()) {
				File[] filesInDir = f.listFiles();
				if (filesInDir == null)
					throw new RuntimeException("Could not get files in directory " + f);
				files.addAll(Arrays.asList(filesInDir));
			}
			else
				throw new RuntimeException("Invalid input file: " + f);
		}
		
		init();
	}

	private void init() {
		this.reader = new XMLReader();
		for (File f : files) {
			if (f.isFile() && f.getName().endsWith(".xml")) {
				loadableClasses.add(f.getName().replace(".xml", ""));
			}
		}
	}
	
	@Override
	public boolean supportsClass(String clazz) {
		if (supportedClasses.contains(clazz))
			return true;
		if (loadableClasses.contains(clazz))
			return true;
		return false;
	}
	
	@Override
	public ClassSummaries getMethodFlows(Set<String> classes, String methodSignature) {
		for (String className : classes)
			if (loadableClasses.contains(className))
				loadClass(className);
		return summaries.filterForMethod(classes, methodSignature);
	}
	
	@Override
	public Set<MethodFlow> getMethodFlows(String className, String methodSignature) {
		if (loadableClasses.contains(className))
			loadClass(className);
		MethodSummaries classSummaries = summaries.getClassSummaries(className);
		return classSummaries == null ? null
				: classSummaries.getFlowsForMethod(methodSignature);
	}
	
	private void loadClass(String clazz) {
		// Do not load classes more than once
		if (supportedClasses.contains(clazz))
			return;
		
		for (File f : files) {
			if (fileToClass(f).equals(clazz)) {
				try {
					summaries.merge(clazz, reader.read(f,clazz));
					loadableClasses.remove(clazz);
					supportedClasses.add(clazz);
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String fileToClass(File f) {
		return f.getName().replace(".xml", "");
	}
	
	@Override
	public Set<String> getSupportedClasses() {
		return this.supportedClasses;
	}
	
	@Override
	public Set<String> getLoadableClasses() {
		return this.loadableClasses;
	}
	
	/**
	 * Gets all method flow summaries that have been loaded so far
	 * @return All summaries that have been loaded so far
	 */
	public ClassSummaries getSummaries() {
		return summaries;
	}
	
}
