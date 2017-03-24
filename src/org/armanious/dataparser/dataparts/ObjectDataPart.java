package org.armanious.dataparser.dataparts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.armanious.dataparser.DataObject;
import org.armanious.dataparser.DataParser;

public class ObjectDataPart extends DataPart {

	private final String type;

	public ObjectDataPart(String type, String name) {
		super(name);
		this.type = type;
	}

	@Override
	public String toString(){
		return "{" + type + "::" + name + "}";
	}

	@Override
	public void save(DataObject obj, DataOutputStream out, DataParser df) throws IOException {
		String subTypeKey = null;
		int idx = type.indexOf('(');
		String trueType;
		if(idx != -1){
			trueType = type.substring(0, idx);
			int endIdx = type.indexOf(')', idx);
			subTypeKey = obj.resolve(type.substring(idx + 1, endIdx));
		}else{
			trueType = type;
		}

		final ArrayList<DataPart> parts = df.getDefinitionDataParts(subTypeKey == null ? trueType : df.getDefinitionSubType(trueType, subTypeKey));;

		DataObject dataObj = (DataObject) obj.get(name);
		for(DataPart part : parts){
			part.save(dataObj, out, df);
		}
	}

	@Override
	public Object load(DataObject obj, DataInputStream in, DataParser df) throws IOException {
		String subTypeKey = null;
		int idx = type.indexOf('(');
		String trueType;
		if(idx != -1){
			trueType = type.substring(0, idx);
			int endIdx = type.indexOf(')', idx);
			subTypeKey = obj.resolve(type.substring(idx + 1, endIdx));
		}else{
			trueType = type;
		}

		trueType = subTypeKey == null ? trueType : df.getDefinitionSubType(trueType, subTypeKey);
		final ArrayList<DataPart> parts = df.getDefinitionDataParts(trueType);
		
		DataObject loadedObj = new DataObject(obj, /*name + "(" + trueType + ")"*/trueType);
		for(DataPart part : parts){
			part.load(loadedObj, in, df);
		}
		obj.set(name, loadedObj);
		return loadedObj;
	}

	@Override
	public String generateSource(String indentation) {
		int idx = type.indexOf('(');
		return indentation + "public " + (idx == -1 ? type : type.substring(0, idx)) + " " + name + ";";
	}

	@Override
	public Object loadFromJavaObject(DataObject obj, Object javaObj, DataParser df) throws ReflectiveOperationException {
		String subTypeKey = null;
		int idx = type.indexOf('(');
		String trueType;
		if(idx != -1){
			trueType = type.substring(0, idx);
			int endIdx = type.indexOf(')', idx);
			subTypeKey = obj.resolve(type.substring(idx + 1, endIdx));
		}else{
			trueType = type;
		}

		trueType = subTypeKey == null ? trueType : df.getDefinitionSubType(trueType, subTypeKey);
		final ArrayList<DataPart> parts = df.getDefinitionDataParts(trueType);
		
		Object o;
		try{
			o = javaObj.getClass().getField(name).get(javaObj);
		}catch(ReflectiveOperationException e){
			o = javaObj; //its part of an array and we are already supplied the object
		}
		
		DataObject loadedObj = new DataObject(obj, /*name + "(" + trueType + ")"*/trueType);
		for(DataPart part : parts){
			part.loadFromJavaObject(loadedObj, o, df);
		}
		obj.set(name, loadedObj);
		return loadedObj;
	}

}
