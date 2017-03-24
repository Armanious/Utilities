package org.armanious.dataparser.dataparts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.armanious.dataparser.DataObject;
import org.armanious.dataparser.DataParser;

public class ByteDataPart extends DataPart {

	public ByteDataPart(String name) {
		super(name);
	}

	@Override
	public void save(DataObject obj, DataOutputStream out, DataParser df) throws IOException {
		out.writeByte(obj.getByte(name));
	}

	@Override
	public Object load(DataObject obj, DataInputStream in, DataParser df) throws IOException {
		byte b = in.readByte();
		obj.set(name, b);
		return b;
	}

	@Override
	public String toString() {
		return "{byte::" + name + "}";
	}

	@Override
	public String generateSource(String indentation) {
		return indentation + "public byte " + name + ";";
	}

	@Override
	public Object loadFromJavaObject(DataObject obj, Object javaObj, DataParser df) throws ReflectiveOperationException {
		byte res = javaObj.getClass().getField(name).getByte(javaObj);
		obj.set(name, res);
		return res;
	}

}
