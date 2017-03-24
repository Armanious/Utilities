package org.armanious.dataparser.dataparts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.armanious.dataparser.DataObject;
import org.armanious.dataparser.DataParser;

public class ReflectiveDataPart extends DataPart {
	
	private final DataPart reflectedPart;
	
	public ReflectiveDataPart(String className, String name) throws ReflectiveOperationException {
		super(name);
		final Class<?> clazz = Class.forName(className);
		if(!DataPart.class.isAssignableFrom(clazz)){
			throw new IllegalArgumentException("Class " + className + " is not a subclass of DataPart");
		}
		reflectedPart = (DataPart) clazz.getConstructor(String.class).newInstance(name);
	}

	@Override
	public void save(DataObject obj, DataOutputStream out, DataParser df) throws IOException {
		reflectedPart.save(obj, out, df);
	}

	@Override
	public Object load(DataObject obj, DataInputStream in, DataParser df) throws IOException {
		return reflectedPart.load(obj, in, df);
	}

	@Override
	public String toString() {
		return reflectedPart.toString();
	}

	@Override
	public String generateSource(String indentation) {
		return reflectedPart.generateSource(indentation);
	}

	@Override
	public Object loadFromJavaObject(DataObject obj, Object javaObj, DataParser df) throws ReflectiveOperationException {
		return reflectedPart.loadFromJavaObject(obj, javaObj, df);
	}

}
