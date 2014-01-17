package soot.jimple.infoflow.test.methodSummary;

import java.util.LinkedList;
import java.util.List;

public class FieldToPara {
	int intField = 1;
	Object obField = new Object();
	@SuppressWarnings("rawtypes")
	LinkedList<?> listField = new LinkedList();
	Object[] arrayField = new Object[100];
	int[] intArray = new int[100];
	public Data dataField = new Data();
	private ApiInternalClass apiInternal = new ApiInternalClass();
	
	void objArrayParameter(Object[] o){
		o[1] = obField;
	}
	void objArrayParameter2(Object[] o){
		o[1] = arrayField[2];
	}
	void objArrayParameter3(Object[] o){
		o[1] = listField.get(0);
	}
	void objArrayParameter4(Object[] o){
		o[1] = dataField.data;
	}
	void objArrayParameter5(Object[] o){
		o[1] = dataField.getO();
	}
	void objArrayParameter6(Object[] o){
		Object oVar =  obField;
		Object oVar2 = oVar;
		o[3] = oVar2;
		
	}
	
	
	void dataParameter(Data d){
		d.data= obField;
	}
	void dataParameter2(Data d){
		d.setO(arrayField[2]);
	}
	void dataParameter3(Data d){
		d.data = listField.get(0);
	}
	void dataParameter4(Data d){
		d.setO(dataField.data);
	}
	public void dataParameter5(Data d){
		d.data = dataField.getO();
	}
	void dataParameter6(Data d){
		Test test = new Test();
		d.data = test.getDataObject();
	}
	void dataParameter7(Data d){
		
		apiInternal.write(dataField, d);
	}
	
	class Test{

		public Object getDataObject(){
			return dataField.data;
		}
	}
	
	
	void dataParameterRec(Data d, int i){
		if(i == 0){
			d.data = obField;
			return;
		}
		dataParameterRec(d,i-1);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void listParameter(List list){
		list.add(intField);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void listParameter2(List list){
		list.add(obField);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void listParameter3(List list){
		list.add(listField.get(1));
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void listParameter4(List list){
		list.add(arrayField[2]);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void listParameter5(List list){
		list.add(intArray[2]);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void listParameter6(List list){
		list.add(dataField.data);
	}
	
	
}