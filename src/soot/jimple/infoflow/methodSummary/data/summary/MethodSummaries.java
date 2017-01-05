package soot.jimple.infoflow.methodSummary.data.summary;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import soot.Scene;
import soot.SootMethod;
import soot.jimple.infoflow.collect.ConcurrentHashSet;
import soot.jimple.spark.summary.GapDefinition;


public class MethodSummaries implements Iterable<MethodFlow> {
	
	private final Map<String, Set<MethodFlow>> flows;
	
	public MethodSummaries() {
		this(new ConcurrentHashMap<String, Set<MethodFlow>>());
	}
	
	MethodSummaries(Set<MethodFlow> flows) {
		this(flowSetToFlowMap(flows));
	}
	
	
	MethodSummaries(Map<String, Set<MethodFlow>> flows) {
		this.flows = flows;
	}

	
	private static Map<String, Set<MethodFlow>> flowSetToFlowMap(Set<MethodFlow> flows) {
		Map<String, Set<MethodFlow>> flowSet = new HashMap<>();
		for (MethodFlow flow : flows) {
			Set<MethodFlow> flowsForSignature = flowSet.get(flow.methodSig());
			if (flowsForSignature == null) {
				flowsForSignature = new HashSet<>();
				flowSet.put(flow.methodSig(), flowsForSignature);
			}
			flowsForSignature.add(flow);
		}
		return flowSet;
	}
	
	public void merge(Set<MethodFlow> newFlows) {
		for (MethodFlow flow : newFlows) {
			Set<MethodFlow> existingFlows = flows.get(flow.methodSig());
			if (existingFlows == null) {
				existingFlows = new HashSet<>();
				flows.put(flow.methodSig(), existingFlows);
			}
			existingFlows.add(flow);
		}
	}
	
	public void merge(Map<String, Set<MethodFlow>> newFlows) {
		for (String key : newFlows.keySet()) {
			Set<MethodFlow> existingFlows = flows.get(key);
			if (existingFlows != null)
				existingFlows.addAll(newFlows.get(key));
			else
				flows.put(key, newFlows.get(key));
		}
	}

	public void merge(MethodSummaries newFlows) {
		if (newFlows == null)
			return;
		merge(newFlows.flows);
	}
	
	
	public Set<MethodFlow> getFlowsForMethod(String methodSig) {
		Set<MethodFlow> methFlows = flows.get(methodSig);
		return methFlows;
	}
	
	
	public boolean addFlow(MethodFlow flow) {
		Set<MethodFlow> methodFlows = flows.get(flow.methodSig());
		if (methodFlows == null) {
			methodFlows = new ConcurrentHashSet<MethodFlow>();
			flows.put(flow.methodSig(), methodFlows);
		}
		return methodFlows.add(flow);
	}
	
	public Map<String, Set<MethodFlow>> getFlows() {
		return this.flows;
	}
	
	public Set<MethodFlow> getAllFlows() {
		Set<MethodFlow> flows = new HashSet<MethodFlow>();
		for (Set<MethodFlow> methodFlows : this.flows.values())
			flows.addAll(methodFlows);
		return flows;
	}

	@Override
	public Iterator<MethodFlow> iterator() {
		return new Iterator<MethodFlow>() {
			
			private String curMethod = null;
			private Iterator<Entry<String, Set<MethodFlow>>> flowIt = flows.entrySet().iterator();
			private Iterator<MethodFlow> curMethodIt = null;

			@Override
			public boolean hasNext() {
				return flowIt.hasNext()
						|| (curMethodIt != null && curMethodIt.hasNext());
			}

			@Override
			public MethodFlow next() {
				if (curMethodIt != null && !curMethodIt.hasNext())
					curMethodIt = null;
				if (curMethodIt == null) {
					Entry<String, Set<MethodFlow>> entry = flowIt.next();
					curMethodIt = entry.getValue().iterator();
					curMethod = entry.getKey();
				}
				return curMethodIt.next();
			}

			@Override
			public void remove() {
				curMethodIt.remove();
				if (flows.get(curMethod).isEmpty()) {
					flowIt.remove();
					curMethodIt = null;
				}
			}
		};
	}
	
	public void clear() {
		if (this.flows != null)
			this.flows.clear();
	}
	
	public int getFlowCount() {
		int cnt = 0;
		for (Set<MethodFlow> methodFlows : this.flows.values())
			cnt += methodFlows.size();
		return cnt;
	}
	
	public void validate() {
		validateFlows();
	}
	
	private void validateFlows() {
		for (String methodName : getFlows().keySet())
			for (MethodFlow flow : getFlows().get(methodName)) {
				flow.validate();
			}
	}
	
	public Set<MethodFlow> getInFlowsForGap(GapDefinition gd) {
		Set<MethodFlow> res = new HashSet<>();
		for (String methodName : getFlows().keySet())
			for (MethodFlow flow : getFlows().get(methodName)) {
				if (flow.sink().getGap() == gd)
					res.add(flow);
			}
		return res;
	}
	
	public Set<MethodFlow> getOutFlowsForGap(GapDefinition gd) {
		Set<MethodFlow> res = new HashSet<>();
		for (String methodName : getFlows().keySet())
			for (MethodFlow flow : getFlows().get(methodName)) {
				if (flow.source().getGap() == gd)
					res.add(flow);
			}
		return res;
	}
	
	public void remove(MethodFlow toRemove) {
		Set<MethodFlow> flowsForMethod = flows.get(toRemove.methodSig());
		if (flowsForMethod != null) {
			flowsForMethod.remove(toRemove);
			if (flowsForMethod.isEmpty())
				flows.remove(toRemove.methodSig());
		}
	}
	
	public void removeAll(Collection<MethodFlow> toRemove) {
		for (Iterator<MethodFlow> flowIt = this.iterator(); flowIt.hasNext(); ) {
			MethodFlow flow = flowIt.next();
			if (toRemove.contains(flow))
				flowIt.remove();
		}
	}
	
	public boolean isEmpty() {
		return this.flows == null || this.flows.isEmpty();
	}
	
}
