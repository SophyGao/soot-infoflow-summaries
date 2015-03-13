package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import soot.jimple.infoflow.methodSummary.data.SourceSinkType;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import soot.jimple.infoflow.methodSummary.generator.SummaryGenerator;

public class CallbackTests extends TestHelper {

	static final String className = "soot.jimple.infoflow.test.methodSummary.Callbacks";
		
	@Test(timeout = 100000)
	public void paraToCallbackToReturn() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.Callbacks: java.lang.String paraToCallbackToReturn(java.lang.String,soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks)>";
		MethodSummaries flow = createSummaries(mSig);
		
		// Parameter 1 to gap base object
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Parameter, 1, null, "",
				SourceSinkType.GapBaseObject, 0, null,
				"<soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks: java.lang.String transform(java.lang.String)>"));
		// Parameter 0 to gap argument 0
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Parameter, 0, null, "", SourceSinkType.Parameter, 0, null,
				"<soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks: java.lang.String transform(java.lang.String)>"));
		
		assertEquals(2, flow.getFlowCount());
	}
	
	@Override
	protected SummaryGenerator getSummary() {
		SummaryGenerator sg = new SummaryGenerator();
		List<String> sub = new LinkedList<String>();
		sub.add("java.util.ArrayList");
		sg.setSubstitutedWith(sub);
		sg.setAnalyseMethodsTogether(false);
		sg.setAccessPathLength(5);
		sg.setIgnoreFlowsInSystemPackages(false);
		return sg;
	}
	
}
