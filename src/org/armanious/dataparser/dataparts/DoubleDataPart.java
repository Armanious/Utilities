package org.armanious.dataparser.dataparts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.armanious.dataparser.DataObject;
import org.armanious.dataparser.DataParser;

public class DoubleDataPart extends DataPart {

	public DoubleDataPart(String name) {
		super(name);
	}
	
	@Override
	public void save(DataObject obj, DataOutputStream out, DataParser df) throws IOException {
		out.writeDouble(obj.getDouble(name));
	}

	@Override
	public Object load(DataObject obj, DataInputStream in, DataParser df) throws IOException {
		double d = in.readDouble();
		obj.set(name, d);
		return d;
	}

	@Override
	public String toString() {
		return "{double::" + name + "}";
	}

	@Override
	public String generateSource(String indentation) {
		return indentation + "public double " + name + ";";
	}

	@Override
	public Object loadFromJavaObject(DataObject obj, Object javaObj, DataParser df) throws ReflectiveOperationException {
		double res = javaObj.getClass().getField(name).getDouble(javaObj);
		obj.set(name, res);
		return res;
	}

}
