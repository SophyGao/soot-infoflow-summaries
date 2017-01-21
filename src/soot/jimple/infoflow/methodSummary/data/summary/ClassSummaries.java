package soot.jimple.infoflow.methodSummary.data.summary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Data class encapsulating all method summaries for a certain class
 * 
 * @author Steven Arzt
 *
 */
public class ClassSummaries {
	
	private final Map<String, MethodSummaries> summaries = new HashMap<>();
	private final Set<String> dependencies = new HashSet<>();
	
	/**
	 * Creates a new instance of the ClassSummaries class
	 */
	public ClassSummaries() {
		//
	}
		
	/**
	 * Gets the flow summaries for the given class
	 * @return The flow summaries for the given class
	 */
	public MethodSummaries getClassSummaries(String className) {
		return summaries.get(className);
	}
	
	/**
	 * Gets a flows for the method with the given signature, regardless of the
	 * class for which they are defined
	 * @param signature The signature of the method for which to get the flows
	 * @return The union of all flows in methods with the given signature over
	 * all classes
	 */
	public Set<MethodFlow> getAllFlowsForMethod(String signature) {
		Set<MethodFlow> flows = new HashSet<>();
		for (String className : this.summaries.keySet())
			flows.addAll(this.summaries.get(className).getFlowsForMethod(signature));
		return flows;
	}
	
	/**
	 * Gets all flows across all classes and methods
	 * @return All flows registered in this data object
	 */
	public Set<MethodFlow> getAllFlows() {
		Set<MethodFlow> flows = new HashSet<>();
		for (MethodSummaries methodSum : summaries.values())
			flows.addAll(methodSum.getAllFlows());
		return flows;
	}
	
	/**
	 * Returns a filter this object that contains only flows for the given
	 * method signature
	 * @param signature The method for which to filter the flows
	 * @return An object containing only flows for the given method
	 */
	public ClassSummaries filterForMethod(String signature) {
		return filterForMethod(this.summaries.keySet(), signature);
	}
	
	/**
	 * Returns a filter this object that contains only flows for the given
	 * method signature in only the given set of classes
	 * @param classes The classes in which to look for method summaries
	 * @param signature The method for which to filter the flows
	 * @return An object containing only flows for the given method
	 */
	public ClassSummaries filterForMethod(Set<String> classes, String signature) {
		ClassSummaries newSummaries = new ClassSummaries();
		for (String className : classes) {
			MethodSummaries methodSummaries = this.summaries.get(className);
			if (methodSummaries != null && !methodSummaries.isEmpty()){
				newSummaries.merge(className, methodSummaries.getFlowsForMethod(signature));
				System.out.println("<"+className+": "+signature+">");
			}
		}
		return newSummaries;
	}
	
	/**
	 * Merges the given flows into the existing flow definitions for the given
	 * class
	 * @param className The name of the class for which to store the given flows
	 * @param newSums The flows to merge into this data store
	 */
	public void merge(String className, MethodSummaries newSums) {
		if (newSums == null)
			return;
		
		MethodSummaries methodSummaries = summaries.get(className);
		if (methodSummaries == null)
			summaries.put(className, newSums);
		else
			methodSummaries.merge(newSums);
	}

	/**
	 * Merges the given flows into the existing flow definitions for the given
	 * class
	 * @param className The name of the class for which to store the given flows
	 * @param newSums The flows to merge into this data store
	 */
	public void merge(String className, Set<MethodFlow> newSums) {
		if (newSums == null || newSums.isEmpty())
			return;
		
		MethodSummaries methodSummaries = summaries.get(className);
		if (methodSummaries == null) {
			methodSummaries = new MethodSummaries(newSums);
			summaries.put(className, methodSummaries);
		}
		else
			methodSummaries.merge(newSums);
	}
	
	/**
	 * Gets whether this data object is empty, i.e., does not contain any data
	 * flow summaries
	 * @return True if this data object is empty, otherwise false
	 */
	public boolean isEmpty() {
		return summaries.isEmpty();
	}
	
	/**
	 * Gets the names of the classes for which this data object contains method
	 * flow summaries
	 * @return The classes for which this object has summaries
	 */
	public Set<String> getClasses() {
		return this.summaries.keySet();
	}
	
	/**
	 * Gets whether this data object contains method summaries for the given
	 * class
	 * @param className True if this data object contains method summaries for
	 * the given class, otherwise false
	 * @return
	 */
	public boolean hasSummariesForClass(String className) {
		return summaries.containsKey(className);
	}
	
	/**
	 * Adds a dependency to this flow set
	 * @param className The name of the dependency clsas
	 * @return True if this dependency class has been added, otherwise
	 * (dependency already registered or summaries loaded for this class)
	 * false
	 */
	public boolean addDependency(String className) {
		if (isPrimitiveType(className) || this.summaries.containsKey(className))
			return false;
		return this.dependencies.add(className);
	}
	
	/**
	 * Checks whether the given type name denotes a primitive
	 * @param typeName The type name to check
	 * @return True if the given type name denotes a primitive, otherwise false
	 */
	private boolean isPrimitiveType(String typeName) {
		return typeName.equals("int")
				 || typeName.equals("long")
				 || typeName.equals("float")
				 || typeName.equals("double")
				 || typeName.equals("char")
				 || typeName.equals("byte")
				 || typeName.equals("short")
				 || typeName.equals("boolean");
	}

	/**
	 * Gets all dependencies of the flows in this object. Dependencies are classes
	 * which are references in a flow summary (e.g., through a field type), but
	 * do not have summaries on their own in this object.
	 * @return The set of depdendency objects for this flow set
	 */
	public Set<String> getDependencies() {
		return this.dependencies;
	}
	
	/**
	 * Clears all summaries from this data object
	 */
	public void clear() {
		if (this.dependencies != null)
			this.dependencies.clear();
		if (this.summaries != null)
			this.summaries.clear();
	}
	
	/**
	 * Validates all summaries for this class
	 */
	public void validate() {
		for (String className : summaries.keySet())
			summaries.get(className).validate();
	}

}
