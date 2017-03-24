package org.armanious.dataparser.dataparts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.armanious.dataparser.DataObject;
import org.armanious.dataparser.DataParser;

public abstract class DataPart {
	
	protected final String name;
	
	public DataPart(String name){
		this.name = name;
	}
	
	public final String getName(){
		return name;
	}
	
	public abstract void save(DataObject obj, DataOutputStream out, DataParser df) throws IOException;
	
	public abstract Object load(DataObject obj, DataInputStream in, DataParser df) throws IOException;
	
	public abstract String toString();

	public final void save(Object object, DataOutputStream out, DataParser df) throws IOException {
		final DataObject obj = new DataObject();
		obj.set(name, object);
		save(obj, out, df);
	}

	public abstract String generateSource(String indentation);

	public abstract Object loadFromJavaObject(DataObject obj, Object javaObj, DataParser df) throws ReflectiveOperationException;
	
}
