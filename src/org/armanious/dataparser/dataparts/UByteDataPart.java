package org.armanious.dataparser.dataparts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.armanious.dataparser.DataObject;
import org.armanious.dataparser.DataParser;

public class UByteDataPart extends DataPart {

	public UByteDataPart(String name) {
		super(name);
	}

	@Override
	public void save(DataObject obj, DataOutputStream out, DataParser df) throws IOException {
		out.writeByte(obj.getInt(name));
	}

	@Override
	public Object load(DataObject obj, DataInputStream in, DataParser df) throws IOException {
		int ub = in.readUnsignedByte();
		obj.set(name, ub);
		return ub;
	}

	@Override
	public String toString() {
		return "{ubyte::" + name + "}";
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
