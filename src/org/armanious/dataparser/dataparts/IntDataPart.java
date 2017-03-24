package org.armanious.dataparser.dataparts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.armanious.dataparser.DataObject;
import org.armanious.dataparser.DataParser;

public class IntDataPart extends DataPart {

	public IntDataPart(String name) {
		super(name);
	}

	@Override
	public void save(DataObject obj, DataOutputStream out, DataParser df) throws IOException {
		out.writeInt(obj.getInt(name));
	}

	@Override
	public Object load(DataObject obj, DataInputStream in, DataParser df) throws IOException {
		int i = in.readInt();
		obj.set(name, i);
		return i;
	}

	@Override
	public String toString() {
		return "{int::" + name + "}";
	}

	@Override
	public String generateSource(String indentation) {
		return indentation + "public int " + name + ";";
	}

	@Override
	public Object loadFromJavaObject(DataObject obj, Object javaObj, DataParser df) throws ReflectiveOperationException {
		int res = javaObj.getClass().getField(name).getInt(javaObj);
		obj.set(name, res);
		return res;
	}

}
