package soot.jimple.infoflow.methodSummary.generator;

import java.util.HashMap;
import java.util.Map;

import soot.jimple.Stmt;
import soot.jimple.infoflow.methodSummary.data.GapDefinition;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;

/**
 * Class that manages the creation of gaps during the taint propagation and the
 * summary generation
 * 
 * @author Steven Arzt
 *
 */
public class GapManager {

	private final Map<Stmt, GapDefinition> gaps = new HashMap<Stmt, GapDefinition>();
	private int lastGapID = 0;
	
	/**
	 * Gets the data object of the given call into a gap method
	 * @param flows The flow set in which to register the gap
	 * @param gapCall The gap to be called
	 * @return The data object of the given gap call. If this call site has
	 * already been processed, the old object is returned. Otherwise, a new
	 * object is generated.
	 */
	public synchronized GapDefinition getGapForCall(MethodSummaries flows, Stmt gapCall) {
		GapDefinition gd = this.gaps.get(gapCall);
		if (gd == null) {
			// Generate a new gap ID
			// Register it in the summary object
			gd = flows.getOrCreateGap(lastGapID++,
					gapCall.getInvokeExpr().getMethod().getSignature());
			this.gaps.put(gapCall, gd);
		}
		return gd;
	}

}
