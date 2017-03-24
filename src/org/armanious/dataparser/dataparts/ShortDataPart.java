package org.armanious.dataparser.dataparts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.armanious.dataparser.DataObject;
import org.armanious.dataparser.DataParser;

public class ShortDataPart extends DataPart {
	
	public ShortDataPart(String name) {
		super(name);
	}
	
	@Override
	public void save(DataObject obj, DataOutputStream out, DataParser df) throws IOException {
		out.writeShort(obj.getShort(name));
	}

	@Override
	public Object load(DataObject obj, DataInputStream in, DataParser df) throws IOException {
		short s = in.readShort();
		obj.set(name, s);
		return s;
	}

	@Override
	public String toString() {
		return "{short::" + name + "}";
	}

	@Override
	public String generateSource(String indentation) {
		return indentation + "public short " + name + ";";
	}

	@Override
	public Object loadFromJavaObject(DataObject obj, Object javaObj, DataParser df) throws ReflectiveOperationException {
		short res = javaObj.getClass().getField(name).getShort(javaObj);
		obj.set(name, res);
		return res;
	}

}
