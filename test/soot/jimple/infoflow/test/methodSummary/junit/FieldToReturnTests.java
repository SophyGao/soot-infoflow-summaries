package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertTrue;
import static soot.jimple.infoflow.methodSummary.data.SourceSinkType.Field;
import static soot.jimple.infoflow.methodSummary.data.SourceSinkType.Return;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.generator.SummaryGenerator;

public class FieldToReturnTests extends TestHelper{
	
	static final String className = "soot.jimple.infoflow.test.methodSummary.FieldToReturn";
	
	static final String INT_FIELD = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: int intField>";
	static final String OBJ_FIELD = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object obField>";
	static final String LIST_FIELD = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.util.LinkedList listField>";
	static final String OBJ_ARRAY = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object[] arrayField>";
	static final String DATA_FIELD = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: soot.jimple.infoflow.test.methodSummary.Data dataField>";
	static final String INT_ARRAY = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: int[] intArray>";

	@Test(timeout = 100000)
	public void fieldToReturn1() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: int fieldToReturn()>";
		Set<MethodFlow> flow = createSummaries(mSig);
		assertTrue(containsFlow(flow, Field,new String[] {INT_FIELD}, Return,new String[] {}));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn2() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object fieldToReturn2()>";
		Set<MethodFlow> flow = createSummaries(mSig);
		assertTrue(containsFlow(flow, Field,new String[] {OBJ_FIELD}, Return,new String[] {}));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn3() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.util.List fieldToReturn3()>";
		Set<MethodFlow> flow = createSummaries(mSig);
		assertTrue(containsFlow(flow, Field,new String[] {LIST_FIELD}, Return,new String[] {}));
		assertTrue(flow.size() == 1);
	}
	
	@Test(timeout = 100000)
	public void fieldToReturn4() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object fieldToReturn4()>";
		Set<MethodFlow> flow = createSummaries(mSig);
		
		boolean hasFirstFlow = containsFlow(flow, Field,new String[] {LIST_FIELD,"<java.util.LinkedList: java.util.LinkedList$Node first>",
				"<java.util.LinkedList$Node: java.lang.Object item>"}, Return,new String[] {});
		boolean hasLastFlow = containsFlow(flow, Field,new String[] {LIST_FIELD,"<java.util.LinkedList: java.util.LinkedList$Node last>",
				"<java.util.LinkedList$Node: java.lang.Object item>"}, Return,new String[] {});
		
		assertTrue(hasFirstFlow || hasLastFlow);
		assertTrue(flow.size() == 1);
	}
	
	@Test(timeout = 100000)
	public void fieldToReturn5() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object fieldToReturn5()>";
		Set<MethodFlow> flow = createSummaries(mSig);
		assertTrue(containsFlow(flow, Field,new String[] {OBJ_ARRAY}, Return,new String[] {}));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn5Rec() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object fieldToReturn5Rec(int)>";
		Set<MethodFlow> flow = createSummaries(mSig);
		assertTrue(containsFlow(flow, Field,new String[] {OBJ_ARRAY}, Return,new String[] {}));
		assertTrue(flow.size() ==1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn6() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object[] fieldToReturn6()>";
		Set<MethodFlow> flow = createSummaries(mSig);
		assertTrue(containsFlow(flow, Field,new String[] {OBJ_ARRAY}, Return,new String[] {}));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn7() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: int fieldToReturn7()>";
		Set<MethodFlow> flow = createSummaries(mSig);
		assertTrue(containsFlow(flow, Field,new String[] {INT_ARRAY}, Return,new String[] {}));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn8() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: int[] fieldToReturn8()>";
		Set<MethodFlow> flow = createSummaries(mSig);
		assertTrue(containsFlow(flow, Field,new String[] {INT_ARRAY}, Return,new String[] {}));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn9() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: int fieldToReturn9()>";
		Set<MethodFlow> flow = createSummaries(mSig);
		assertTrue(containsFlow(flow, Field,new String[] {DATA_FIELD,DATACLASS_INT_FIELD}, Return,new String[] {}));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn10() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object fieldToReturn10()>";
		Set<MethodFlow> flow = createSummaries(mSig);
		assertTrue(containsFlow(flow, Field,new String[] {DATA_FIELD,DATACLASS_OBJECT_FIELD}, Return,new String[] {}));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn11() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.String fieldToReturn11()>";
		Set<MethodFlow> flow = createSummaries(mSig);
		assertTrue(containsFlow(flow, Field,new String[] {DATA_FIELD,DATACLASS_STRING_FIELD}, Return,new String[] {}));
		assertTrue(flow.size() == 1);
	}
	
	@Test(timeout = 100000)
	public void fieldToReturn12() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: "+DATA_TYPE+" fieldToReturn12()>";
		Set<MethodFlow> flow = createSummaries(mSig);
		assertTrue(containsFlow(flow, Field,new String[] {DATA_FIELD,DATACLASS_INT_FIELD}, Return,new String[] {DATACLASS_INT_FIELD}));
		assertTrue(flow.size() == 1);
	}
	
	@Test(timeout = 100000)
	public void fieldToReturn13() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: "+DATA_TYPE+" fieldToReturn13()>";
		Set<MethodFlow> flow = createSummaries(mSig);
		assertTrue(containsFlow(flow, Field,new String[] {DATA_FIELD,DATACLASS_OBJECT_FIELD}, Return,new String[] {DATACLASS_OBJECT_FIELD}));
		assertTrue(flow.size() == 1);
	}
	
	@Test(timeout = 100000)
	public void fieldToReturn14() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: "+DATA_TYPE+" fieldToReturn14()>";
		Set<MethodFlow> flow = createSummaries(mSig);
		assertTrue(containsFlow(flow, Field,new String[] {INT_FIELD}, Return,new String[] {DATACLASS_INT_FIELD}));
		assertTrue(flow.size() == 1);
	}	
	
	@Override
	protected SummaryGenerator getSummary() {
		SummaryGenerator sg = new SummaryGenerator();
		List<String> sub = new LinkedList<String>();
		sub.add("java.util.LinkedList");
		sg.setSubstitutedWith(sub);
		sg.setUseRecursiveAccessPaths(true);
		sg.setAnalyseMethodsTogether(false);
		sg.setAccessPathLength(3);
		sg.setIgnoreFlowsInSystemPackages(false);
		return sg;
	}
}
