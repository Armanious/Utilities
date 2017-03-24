package org.armanious.dataparser.dataparts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.armanious.dataparser.DataObject;
import org.armanious.dataparser.DataParser;

public class BooleanDataPart extends DataPart {

	public BooleanDataPart(String name) {
		super(name);
	}

	@Override
	public void save(DataObject obj, DataOutputStream out, DataParser df) throws IOException {
		out.writeBoolean(obj.getBoolean(name));
	}

	@Override
	public Object load(DataObject obj, DataInputStream in, DataParser df) throws IOException {
		boolean b = in.readBoolean();
		obj.set(name, b);
		return b;
	}

	@Override
	public String toString() {
		return "{boolean::" + name + "}";
	}

	@Override
	public String generateSource(String indentation) {
		return indentation + "public boolean " + name + ";";
	}

	@Override
	public Object loadFromJavaObject(DataObject obj, Object javaObj, DataParser df) throws ReflectiveOperationException {
		boolean res = javaObj.getClass().getField(name).getBoolean(javaObj);
		obj.set(name, res);
		return res;
	}

}
