package soot.jimple.infoflow.methodSummary.source;

import heros.InterproceduralCFG;
import heros.solver.IDESolver;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.methodSummary.data.factory.SourceSinkFactory;
import soot.jimple.infoflow.source.ISourceSinkManager;
import soot.jimple.infoflow.source.SourceInfo;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * SourceSinkManager for computing library summaries
 * 
 * @author Malte Viering
 * @author Steven Arzt
 */
public class SummarySourceSinkManager implements ISourceSinkManager {
	
	protected final LoadingCache<SootClass, Collection<SootField>> classToFields =
		IDESolver.DEFAULT_CACHE_BUILDER.build(new CacheLoader<SootClass, Collection<SootField>>() {
			@Override
			public Collection<SootField> load(SootClass sc) throws Exception {
				List<SootField> res = new LinkedList<SootField>();
				List<SootClass> impler = Scene.v().getActiveHierarchy().getSuperclassesOfIncluding(method.getDeclaringClass());
				for(SootClass c : impler)
					res.addAll(c.getFields());
				return res;
			}
		});
	
	private boolean debug = false;
	

	private final Logger logger = LoggerFactory.getLogger(SummarySourceSinkManager.class);
	private final String methodSig;
	private final String parentClass;
	private final SourceSinkFactory sourceSinkFactory;
	
	private SootMethod method = null;
	
	/**
	 * Creates a new instance of the {@link SummarySourceSinkManager} class
	 * @param mSig The signature of the method for which summaries shall be
	 * created
	 * @param parentClass The parent class containing the method for which
	 * summaries shall be created. If mSig is the signature of a method
	 * inherited from a base class, this parameter receives the class on
	 * which the method denoted by mSig is called.
	 * @param sourceSinkFactory The {@link SourceSinkFactory} to create
	 * source and sink data objects
	 */
	public SummarySourceSinkManager(String mSig, String parentClass,
			SourceSinkFactory sourceSinkFactory) {
		this.methodSig = mSig;
		this.parentClass = parentClass;
		this.sourceSinkFactory = sourceSinkFactory;
	}
	
	@Override
	public SourceInfo getSourceInfo(Stmt sCallSite, InterproceduralCFG<Unit, SootMethod> cfg) {
		// If this is not the method we are looking for, we skip it
		SootMethod currentMethod = cfg.getMethodOf(sCallSite);
		if (!isMethodToSummarize(currentMethod))
			return null;
		
		if (sCallSite instanceof DefinitionStmt) {
			DefinitionStmt jstmt = (DefinitionStmt) sCallSite;
			Value leftOp = jstmt.getLeftOp();
			Value rightOp = jstmt.getRightOp();
			
			//check if we have a source with apl = 0 (this or parameter source)
			if (rightOp instanceof ParameterRef) {
				ParameterRef pref = (ParameterRef) rightOp;
				logger.debug("source: " + sCallSite + " " + currentMethod.getSignature());
				if(debug)
					System.out.println("source: " + sCallSite + " " + currentMethod.getSignature());
				return new SourceInfo(new AccessPath(leftOp, true),
						Collections.singletonList(sourceSinkFactory.createParameterSource(
								pref.getIndex(), pref.getType().toString())));
			}
			else if (rightOp instanceof ThisRef) {
				ThisRef tref = (ThisRef) rightOp;
				if(debug)
					System.out.println("source: (this)" + sCallSite + " " + currentMethod.getSignature());				
				return new SourceInfo(new AccessPath(leftOp, true),
						Collections.singletonList(sourceSinkFactory.createThisSource(
								tref.getType().toString())));
			}
		}
		return null;
	}
	
	private boolean isMethodToSummarize(SootMethod currentMethod) {
		// Initialize the method we are interested in
		if(method == null)
			method = Scene.v().getMethod(methodSig);
		
		// This must either be the method defined by signature or the
		// corresponding one in the parent class
		if (currentMethod == method)
			return true;
		
		return currentMethod.getDeclaringClass().getName().equals(parentClass)
					&& currentMethod.getSubSignature().equals(method.getSubSignature());
	}

	@Override
	public boolean isSink(Stmt sCallSite, InterproceduralCFG<Unit, SootMethod> cfg,
			AccessPath sourceAP) {
		// We only fake the sources during the initial collection pass. During
		// the actual taint propagation, the TaintPropagationHandler will take
		// care of recording the outbound abstractions
		if (sourceAP != null)
			return false;
		
		// If this is not the method we are looking for, we skip it
		SootMethod currentMethod = cfg.getMethodOf(sCallSite);
		if (!isMethodToSummarize(currentMethod))
			return false;
		
		return sCallSite instanceof ReturnStmt
				|| sCallSite instanceof ReturnVoidStmt;
	}
	
}
