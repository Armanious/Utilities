package org.armanious.dataparser;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.armanious.dataparser.dataparts.*;

public class DataParser {

	private static final Map<String, DataParser> cache = new HashMap<>();
	private static final Map<String, Class<? extends DataPart>> types = new HashMap<>();
	static {
		types.put("byte", ByteDataPart.class);
		types.put("short", ShortDataPart.class);
		types.put("char", CharDataPart.class);
		types.put("int", IntDataPart.class);
		types.put("float", FloatDataPart.class);
		types.put("long", LongDataPart.class);
		types.put("double", DoubleDataPart.class);
		types.put("boolean", BooleanDataPart.class);

		types.put("ubyte", UByteDataPart.class);
		types.put("ushort", UShortDataPart.class);
	}

	private final Map<String, Object> definitions;
	private final ArrayList<DataPart> dataParts;

	private DataParser(Map<String, Object> definitions, ArrayList<DataPart> dataParts){
		this.definitions = definitions;
		this.dataParts = dataParts;

	}

	private static final String DEFINITION_HEADER = "***DEFINITIONS_START***";
	private static final String DEFINITION_FOOTER = "***DEFINITIONS_END***";

	@SuppressWarnings("unchecked")
	public static DataParser parse(String format){
		final String original = (format = format.replace("\\s", ""));
		DataParser df = cache.get(original);
		if(df == null){
			final Map<String, Object> definitions = new HashMap<>();

			int definitionsTextIdxStart = format.indexOf(DEFINITION_HEADER);
			int definitionsTextIdxEnd = format.indexOf(DEFINITION_FOOTER);
			String definitionsText = format.substring(definitionsTextIdxStart + DEFINITION_HEADER.length(), definitionsTextIdxEnd);
			format = format.substring(definitionsTextIdxEnd + DEFINITION_FOOTER.length());
			int idx = 0;
			while((idx = definitionsText.indexOf('[', idx)) != -1){
				final int endIdx = findClosingChar(']', definitionsText, idx);//definitionsText.indexOf(']', idx);
				final String s = definitionsText.substring(idx + 1, endIdx);
				idx = s.indexOf('=');
				String key = s.substring(0, idx);
				int parenthesesIdx = key.indexOf('(');
				if(parenthesesIdx != -1){
					String name = key.substring(0, parenthesesIdx);
					Map<Object, Object> subMap = (Map<Object, Object>) definitions.get(name);
					if(subMap == null){
						subMap = new HashMap<>();
						definitions.put(name, subMap);
					}
					int endParenthesesIdx = findClosingChar(')', key, parenthesesIdx);//key.indexOf(')', parenthesesIdx);
					subMap.put(key.substring(parenthesesIdx + 1, endParenthesesIdx), s.substring(idx + 1));
				}else{
					definitions.put(key, parseDataParts(s.substring(idx + 1)));
				}
				idx = endIdx;
			}

			final ArrayList<DataPart> parts = parseDataParts(format);
			df = new DataParser(definitions, parts);
			cache.put(original, df);
		}
		return df;
	}

	private static int findClosingChar(char closingChar, String definitionsText, int idx) {
		char openingBracket = closingChar == ')' ? '(' : (closingChar == ']' ? '[' : (closingChar == '}' ? '{' : 0));
		if(openingBracket == 0)
			throw new IllegalArgumentException(closingChar + " not supported");
		int count = 0;
		for(int i = idx; i < definitionsText.length(); i++){
			char c = definitionsText.charAt(i);
			if(c == openingBracket){
				count++;
			}else if(c == closingChar){
				count--;
				if(count == 0){
					return i;
				}
			}
		}
		return -1;
	}


	public DataObject load(byte[] contents) throws IOException {
		return load(new ByteArrayInputStream(contents));
	}

	public DataObject load(InputStream in) throws IOException {
		final DataObject obj = new DataObject();
		final DataInputStream dis = new DataInputStream(in);
		for(DataPart dataPart : dataParts){
			dataPart.load(obj, dis, this);
		}
		return obj;
	}

	public void save(DataObject obj, OutputStream os) throws IOException {
		final DataOutputStream dos = new DataOutputStream(os);
		for(DataPart dataPart : dataParts){
			dataPart.save(obj, dos, this);
		}
	}

	private static ArrayList<DataPart> parseDataParts(String dataPartsText){
		final ArrayList<DataPart> parts = new ArrayList<>();
		int idx = 0;
		while((idx = dataPartsText.indexOf('{', idx)) != -1){
			final int endIdx = findClosingChar('}', dataPartsText, idx);
			final String s = dataPartsText.substring(idx + 1, endIdx);
			idx = s.indexOf("::");
			String type = s.substring(0, idx);
			String name = s.substring(idx + 2);

			String unresolvedArrayLength = null;

			int arrayIndexerIdx = type.indexOf('[');
			if(arrayIndexerIdx != -1){
				int arrayIndexerIdxEnd = findClosingChar(']', type, arrayIndexerIdx);
				unresolvedArrayLength = type.substring(arrayIndexerIdx + 1, arrayIndexerIdxEnd);
				type = type.substring(0, arrayIndexerIdx);
			}

			DataPart part;

			if(type.charAt(0) == '@'){
				try {
					part = new ReflectiveDataPart(type.substring(1), name);
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			}else if(types.containsKey(type)){
				try {
					part = types.get(type).getConstructor(String.class).newInstance(name);
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			}else{
				part = new ObjectDataPart(type, name);
			}

			if(unresolvedArrayLength != null){
				part = new ArrayDataPart(part, unresolvedArrayLength, name);
			}

			parts.add(part);

			idx = endIdx;			
		}
		return parts;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<DataPart> getDefinitionDataParts(String definitionName) {
		final Object obj = definitions.get(definitionName);
		if(obj instanceof ArrayList){
			return (ArrayList<DataPart>) obj;
		}
		System.err.println(definitionName);
		throw new RuntimeException();
	}

	@SuppressWarnings("unchecked")
	public String getDefinitionSubType(String trueType, String subTypeKey) {
		Object obj = definitions.get(trueType);
		if(obj instanceof Map){
			return (String) ((Map<String, Object>)obj).get(subTypeKey);
		}
		throw new IllegalArgumentException(trueType + " has no subtypes");
	}

	public String generateSourceFileForJavaObject(String className){ //TODO clean up and make a much more elegant solution
		String indentation = "";
		final StringBuilder sb = new StringBuilder();
		sb.append('\n').append("public class ").append(className).append(" {\n");
		indentation += "    ";
		sb.append(indentation).append('\n');
		for(DataPart part : dataParts){
			sb.append(part.generateSource(indentation)).append('\n');
		}
		sb.append(indentation).append('\n');
		for(String key : definitions.keySet()){
			sb.append(generateSourceForClass(key, indentation)).append('\n');
		}
		sb.append("}\n");

		for(String key : definitions.keySet()){
			Object def = definitions.get(key);
			if(def instanceof Map){
				@SuppressWarnings("unchecked")
				final Map<String, Object> map = (Map<String, Object>) def;

				//write subclasses
				for(String subKey : map.keySet()){
					Object obj = map.get(subKey);
					if(obj instanceof String){
						String search = "public class " + obj + " ";
						int idx = sb.indexOf("public class " + obj + " ");
						sb.replace(idx, idx + search.length(), search + "extends " + key + " ");
						idx = sb.indexOf("}", idx);
					}else{
						System.err.println(obj);
						throw new RuntimeException();
					}
					sb.append('\n');
				}
			}
		}

		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private String generateSourceForClass(String className, String indentation){
		final StringBuilder sb = new StringBuilder();
		Object def = definitions.get(className);
		if(def instanceof ArrayList){
			sb.append(indentation).append("public class ").append(className).append(" {\n");
			String oldIndentation = indentation;
			indentation += "    ";
			for(DataPart part : (ArrayList<DataPart>)def){
				sb.append(part.generateSource(indentation)).append('\n');
			}
			indentation = oldIndentation;
			sb.append(indentation).append("}\n");
		}else if(def instanceof Map){
			//final Map<String, Object> map = (Map<String, Object>) def;

			//write base class
			sb.append(indentation).append("public abstract class ").append(className).append(" {\n");
			sb.append(indentation).append(indentation).append('\n');
			sb.append(indentation).append("}\n");

			//write subclasses
			/*for(String subKey : map.keySet()){
				Object obj = map.get(subKey);
				if(obj instanceof String){
					sb.append(generateSourceForClass((String)obj, indentation));
				}else{
					System.err.println(obj);
					throw new RuntimeException();
				}
				sb.append('\n');
			}*/
		}
		return sb.toString();
	}
	
	public void saveToJavaObject(DataObject loadedObject, Object objToSaveTo) throws ReflectiveOperationException {
		saveToJavaObject(loadedObject, objToSaveTo, objToSaveTo);
	}
	
	private void saveToJavaObject(DataObject loadedObject, Object objToSaveTo, Object originalObject) throws ReflectiveOperationException {
		if(loadedObject == null) return;
		final Map<String, Object> data = loadedObject.getData();
		for(String fieldName : data.keySet()){
			Object res = data.get(fieldName);
			if(res instanceof DataObject[]){
				DataObject[] arr = (DataObject[]) res;
				saveArrayToJavaObject(fieldName, arr, objToSaveTo, originalObject);
			}else if(res instanceof DataObject){
				Field f = objToSaveTo.getClass().getField(fieldName);
				Constructor<?> constructor = Class.forName(originalObject.getClass().getName() + "$" + ((DataObject)res).getName()).getConstructor(originalObject.getClass());
				Object o = constructor.newInstance(originalObject);
				saveToJavaObject((DataObject)res, o, originalObject);
				f.set(objToSaveTo, o);
			}else{
				objToSaveTo.getClass().getField(fieldName).set(objToSaveTo, res);
			}
		}
	}

	private void saveArrayToJavaObject(String fieldName, DataObject[] arr, Object objToSaveTo, Object originalObject) throws ReflectiveOperationException {
		if(arr == null) return;
		Field f = objToSaveTo.getClass().getField(fieldName);
		Class<?> compType = f.getType().getComponentType();
		Object javaObjArr = Array.newInstance(compType, arr.length);
		Constructor<?> constructor = compType.getConstructor(originalObject.getClass());
		for(int i = 0; i < arr.length; i++){
			Object o = constructor.newInstance(originalObject);
			saveToJavaObject(arr[i], o, originalObject);
			Array.set(javaObjArr, i, o);
		}		
		f.set(objToSaveTo, javaObjArr);
	}
	
	public DataObject loadFromJavaObject(Object javaObj) throws ReflectiveOperationException {
		final DataObject obj = new DataObject();
		
		for(DataPart data : dataParts){
			data.loadFromJavaObject(obj, javaObj, this);
		}
		
		return obj;
	}

}
