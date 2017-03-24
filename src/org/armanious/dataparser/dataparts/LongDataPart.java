package org.armanious.dataparser.dataparts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.armanious.dataparser.DataObject;
import org.armanious.dataparser.DataParser;

public class LongDataPart extends DataPart {

	public LongDataPart(String name) {
		super(name);
	}

	@Override
	public void save(DataObject obj, DataOutputStream out, DataParser df) throws IOException {
		out.writeLong(obj.getLong(name));
	}

	@Override
	public Object load(DataObject obj, DataInputStream in, DataParser df) throws IOException {
		long l = in.readLong();
		obj.set(name, l);
		return l;
	}

	@Override
	public String toString() {
		return "{long::" + name + "}";
	}

	@Override
	public String generateSource(String indentation) {
		return indentation + "public long " + name + ";";
	}

	@Override
	public Object loadFromJavaObject(DataObject obj, Object javaObj, DataParser df) throws ReflectiveOperationException {
		long res = javaObj.getClass().getField(name).getLong(javaObj);
		obj.set(name, res);
		return res;
	}

}
