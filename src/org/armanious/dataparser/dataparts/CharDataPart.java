package org.armanious.dataparser.dataparts;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.armanious.dataparser.DataObject;
import org.armanious.dataparser.DataParser;

public class CharDataPart extends DataPart {

	public CharDataPart(String name) {
		super(name);
	}

	@Override
	public void save(DataObject obj, DataOutputStream out, DataParser df) throws IOException {
		out.writeChar(obj.getChar(name));
	}

	@Override
	public Object load(DataObject obj, DataInputStream in, DataParser df) throws IOException {
		char c = in.readChar();
		obj.set(name, c);
		return c;
	}

	@Override
	public String toString() {
		return "{char::" + name + "}";
	}

	@Override
	public String generateSource(String indentation) {
		return indentation + "public char " + name + ";";
	}

	@Override
	public Object loadFromJavaObject(DataObject obj, Object javaObj, DataParser df) throws ReflectiveOperationException {
		char res = javaObj.getClass().getField(name).getChar(javaObj);
		obj.set(name, res);
		return res;
	}

}
