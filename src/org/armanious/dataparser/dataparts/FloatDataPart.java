package org.armanious.dataparser.dataparts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.armanious.dataparser.DataObject;
import org.armanious.dataparser.DataParser;

public class FloatDataPart extends DataPart {

	public FloatDataPart(String name) {
		super(name);
	}

	@Override
	public void save(DataObject obj, DataOutputStream out, DataParser df) throws IOException {
		out.writeFloat(obj.getFloat(name));
	}

	@Override
	public Object load(DataObject obj, DataInputStream in, DataParser df) throws IOException {
		float f = in.readFloat();
		obj.set(name, f);
		return f;
	}

	@Override
	public String toString() {
		return "{float::" + name + "}";
	}

	@Override
	public String generateSource(String indentation) {
		return indentation + "public float " + name + ";";
	}

	@Override
	public Object loadFromJavaObject(DataObject obj, Object javaObj, DataParser df) throws ReflectiveOperationException {
		float res = javaObj.getClass().getField(name).getFloat(javaObj);
		obj.set(name, res);
		return res;
	}

}
