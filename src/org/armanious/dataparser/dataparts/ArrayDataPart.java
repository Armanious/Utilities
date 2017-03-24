package org.armanious.dataparser.dataparts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;

import org.armanious.dataparser.DataObject;
import org.armanious.dataparser.DataParser;

public class ArrayDataPart extends DataPart {

	private final DataPart arrayedPart;
	private final String unresolvedArrayLength;

	public ArrayDataPart(DataPart arrayedPart, String unresolvedArrayLength, String name) {
		super(name);
		this.arrayedPart = arrayedPart;
		this.unresolvedArrayLength = unresolvedArrayLength;
	}

	@Override
	public void save(DataObject obj, DataOutputStream out, DataParser df) throws IOException {
		Object arr = obj.get(name);
		int len = Array.getLength(arr);
		for(int i = 0; i < len; i++){
			arrayedPart.save(Array.get(arr, i), out, df);
		}
	}

	@Override
	public Object load(DataObject obj, DataInputStream in, DataParser df) throws IOException {
		int length = Integer.parseInt(obj.resolve(unresolvedArrayLength));
		final Object arr;
		if(arrayedPart instanceof BooleanDataPart)
			arr = new boolean[length];
		else if(arrayedPart instanceof ByteDataPart)
			arr = new byte[length];
		else if(arrayedPart instanceof CharDataPart)
			arr = new char[length];
		else if(arrayedPart instanceof DoubleDataPart)
			arr = new double[length];
		else if(arrayedPart instanceof FloatDataPart)
			arr = new float[length];
		else if(arrayedPart instanceof IntDataPart)
			arr = new int[length];
		else if(arrayedPart instanceof LongDataPart)
			arr = new long[length];
		else if(arrayedPart instanceof ShortDataPart)
			arr = new long[length];
		else if(arrayedPart instanceof UByteDataPart)
			arr = new int[length];
		else if(arrayedPart instanceof UShortDataPart)
			arr = new int[length];
		else
			arr = new DataObject[length];
		for(int i = 0; i < length; i++){
			Array.set(arr, i, arrayedPart.load(new DataObject(obj, name + "[" + i + "]"), in, df));
		}
		obj.set(name, arr);
		return arr;
	}

	@Override
	public String toString() {
		return "ArrayDataPart[" + unresolvedArrayLength + "]-" + arrayedPart.toString();
	}

	@Override
	public String generateSource(String indentation) {
		String arrayedSource = arrayedPart.generateSource(indentation);
		int idx = arrayedSource.lastIndexOf(' ');
		return arrayedSource.substring(0, idx) + "[]" + arrayedSource.substring(idx);
	}

	@Override
	public Object loadFromJavaObject(DataObject obj, Object javaObj, DataParser df) throws ReflectiveOperationException {
		final Object otherArr = javaObj.getClass().getField(name).get(javaObj);
		final int length = Integer.parseInt(obj.resolve(unresolvedArrayLength));
		Object thisArr;
		if(otherArr.getClass().getComponentType().isPrimitive()){
			thisArr = Array.newInstance(otherArr.getClass().getComponentType(), length);
			System.arraycopy(otherArr, 0, thisArr, 0, length);
		}else{
			thisArr = new DataObject[length];
			for(int i = 0; i < length; i++){
				Array.set(thisArr, i, arrayedPart.loadFromJavaObject(new DataObject(obj, name + "[" + i + "]"), Array.get(otherArr, i), df));
			}
		}
		obj.set(name, thisArr);
		return thisArr;
	}

}
