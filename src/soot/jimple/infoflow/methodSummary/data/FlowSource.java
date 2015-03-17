package soot.jimple.infoflow.methodSummary.data;

import java.util.Arrays;

/**
 * Representation of a flow source
 * 
 * @author Steven Arzt
 */
public class FlowSource extends AbstractFlowSinkSource {

	public FlowSource(SourceSinkType type, String baseType) {
		super(type, -1, baseType, null, null);
	}
	
	public FlowSource(SourceSinkType type, String baseType, GapDefinition gap) {
		super(type, -1, baseType, null, null, gap);
	}

	public FlowSource(SourceSinkType type, String baseType, String[] fields,
			String[] fieldTypes) {
		super(type, -1, baseType, fields, fieldTypes);
	}
	
	public FlowSource(SourceSinkType type, String baseType, String[] fields,
			String[] fieldTypes, GapDefinition gap) {
		super(type, -1, baseType, fields, fieldTypes, gap);
	}

	public FlowSource(SourceSinkType type, int parameterIdx, String baseType) {
		super(type, parameterIdx, baseType, null, null);
	}
	
	public FlowSource(SourceSinkType type, int parameterIdx, String baseType,
			GapDefinition gap) {
		super(type, parameterIdx, baseType, null, null, gap);
	}

	public FlowSource(SourceSinkType type, int parameterIdx, String baseType,
			String[] fields, String[] fieldTypes) {
		super(type, parameterIdx, baseType, fields, fieldTypes);
	}
		
	public FlowSource(SourceSinkType type, int parameterIdx, String baseType,
			String[] fields, String[] fieldTypes, GapDefinition gap) {
		super(type, parameterIdx, baseType, fields, fieldTypes, gap);
	}

	@Override
	public String toString(){
		String gapString = getGap() == null ? ""
				: "Gap " + getGap().getSignature() + " ";
		
		if(isParameter())
			return gapString
					+ "Parameter " + getParameterIndex() + (accessPath == null ? "" : " "
					+  Arrays.toString(accessPath));
		
		if(isField())
			return gapString
					+ "Field" + (accessPath == null ? "" : " " + Arrays.toString(accessPath));
		
		if(isThis())
			return "THIS";
		
		if (isReturn() && gap != null)
			return "Return value of gap " + gap.getSignature();
		
		return "<unknown>";
	}

	//a this source is a field source with apl = 0
	public boolean isThis()
	{
		return type().equals(SourceSinkType.Field) && !hasAccessPath();
	}
	
	/**
	 * Validates this flow source
	 * @param methodName The name of the containing method. This will be used to
	 * give more context in exception messages
	 */
	public void validate(String methodName) {
		if (getType() == SourceSinkType.Return && getGap() == null)
			throw new RuntimeException("Return values cannot be sources. "
					+ "Offending method: " + methodName);
	}

}
