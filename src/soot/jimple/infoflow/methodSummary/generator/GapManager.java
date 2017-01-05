package soot.jimple.infoflow.methodSummary.generator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Local;
import soot.Scene;
import soot.SootMethod;
import soot.ValueBox;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.summary.GapDefinition;


public class GapManager {
	Map<Stmt,GapDefinition> gaps; 
	
	public void setGaps(){
		gaps=((PAG)Scene.v().getPointsToAnalysis()).getOnFlyCallGraph().getStmtToGaps();
	}
	
	public GapDefinition getGapForCall( Stmt gapCall) {
		GapDefinition gap=gaps.get(gapCall);
		return gap;
	}

	
	public synchronized boolean isLocalReferencedInGap(Local local) {
		for (Stmt stmt : gaps.keySet()) {
			for (ValueBox vb : stmt.getUseBoxes())
				if (vb.getValue() == local)
					return true;
			if (stmt instanceof DefinitionStmt)
				if (((DefinitionStmt) stmt).getLeftOp() == local)
					return true;
		}
		return false;
	}
	
	/**
	 * Gets the gap definitions that references the given local as parameters
	 * or base objects 
	 * @param local The local for which to find the gap references
	 * @return The gaps that reference the given local
	 */
	public Set<GapDefinition> getGapDefinitionsForLocalUse(Local local) {
		Set<GapDefinition> res = null;
		stmt : for (Stmt stmt : gaps.keySet()) {
			for (ValueBox vb : stmt.getUseBoxes())
				if (vb.getValue() == local) {
					if (res == null)
						res = new HashSet<>();
					res.add(gaps.get(stmt));
					continue stmt;
				}
		}
		return res;
 	}
	
	
	public Set<GapDefinition> getGapDefinitionsForLocalDef(Local local) {
		Set<GapDefinition> res = null;
		stmt : for (Stmt stmt : gaps.keySet()) {
			if (stmt instanceof DefinitionStmt)
				if (((DefinitionStmt) stmt).getLeftOp() == local) {
					if (res == null)
						res = new HashSet<>();
					res.add(gaps.get(stmt));
					continue stmt;
				}
		}
		return res;
 	}
	
	
	public boolean needsGapConstruction(Stmt stmt, Abstraction abs, IInfoflowCFG icfg) {
		SootMethod targetMethod = stmt.getInvokeExpr().getMethod();
		
		// Do not report inactive flows into gaps
		if (!abs.isAbstractionActive())
			return false;
		
		// If the callee is native, there is no need for a gap
		if (targetMethod.isNative())
			return false;
		
		// Do not construct a gap for constructors or static initializers
		if (targetMethod.isStaticInitializer())
			return false;
		
		// Do not produce flows from one statement to itself
		if (abs.getSourceContext() != null
				&& abs.getSourceContext().getStmt() == stmt)
			return false;
		
		// Is the given incoming value even used in the gap statement?
		if (!isValueUsedInStmt(stmt, abs))
			return false;
		
		
		return gaps.containsKey(stmt);
//		
//		// We always construct a gap if we have no callees
//		SootMethod sm = icfg.getMethodOf(stmt);
//		Collection<SootMethod> callees = icfg.getCalleesOfCallAt(stmt);
//		if (callees != null && !callees.isEmpty()) {
//			// If we have a call to an abstract method, this might instead of an 
//			// empty callee list give  us one with a self-loop. Semantically, this
//			// is however still an unknown callee.
//			if (!(callees.size() == 1 && callees.contains(sm)
//					&& stmt.getInvokeExpr().getMethod().isAbstract())) {
//				return false;
//			}
//		}
//		
//		// Do not build gap flows for the java.lang.System class
//		if (sm.getDeclaringClass().getName().equals("java.lang.System"))
//			return false;
//		if (targetMethod.getDeclaringClass().getName().startsWith("sun."))
//			return false
	}
	
	/**
	 * Checks whether the given value is used in the given statement
	 * @param stmt The statement to check
	 * @param abs The value to check
	 * @return True if the given value is used in the given statement, otherwise
	 * false
	 */
	private boolean isValueUsedInStmt(Stmt stmt, Abstraction abs) {
		if (!stmt.containsInvokeExpr())
			return false;
		InvokeExpr iexpr = stmt.getInvokeExpr();
		
		for (int i = 0; i < iexpr.getArgCount(); i++)
			if (abs.getAccessPath().getPlainValue() == iexpr.getArg(i))
				return true;
		
		// If this is the base local, we take it
		return iexpr instanceof InstanceInvokeExpr
				&& ((InstanceInvokeExpr) iexpr).getBase() == abs.getAccessPath().getPlainValue();
	}
	
}
