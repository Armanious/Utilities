package org.armanious.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class ReflectionObjectOutputStream extends OutputStream implements AutoCloseable, ObjectOutput {

	private static final int BOOLEAN_TYPE = 0, BYTE_TYPE = 1, SHORT_TYPE = 2, CHAR_TYPE = 3, INT_TYPE = 4, LONG_TYPE = 5, FLOAT_TYPE = 6, DOUBLE_TYPE = 7,
			STRING_TYPE = 8, OBJECT_TYPE = 9;

	private static int callcount;

	private final DataOutputStream os;
	private final Set<Class<?>> initialized = new HashSet<>();
	private final ArrayList<Object> list = new ArrayList<>();

	public ReflectionObjectOutputStream(final OutputStream os) {
		this.os = new DataOutputStream(os);
	}

	@Override
	public void write(final int b) throws IOException {
		os.write(b);
	}

	private int getObjectIndex(Object obj) {
		final int hash = System.identityHashCode(obj);
		for (int i = 0; i < list.size(); i++) {
			if (hash == System.identityHashCode(list.get(i))) {
				return i;
			}
		}
		list.add(obj);
		return -1;
	}

	@Override
	public void writeObject(final Object obj) throws IOException {
		callcount++;
		if (obj == null || obj.getClass() == sun.misc.Cleaner.class) {
			write(1);
		} else {
			int objIndex = getObjectIndex(obj);
			if (objIndex >= 0) {
				write(2);
				writeInt(objIndex);
			} else {
				write(0);
				writeUTF(obj.getClass().getName());
				try {
					if (obj.getClass().isArray()) {
						writeArray(obj);
					} else {
						writeObjectInternal(obj);
					}
				} catch (final ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			}
		}
		callcount--;
		if (callcount == 0)
			list.clear();
	}

	private void writeObjectInternal(final Object obj) throws IOException, ReflectiveOperationException {
		final Class<?> clazz = obj.getClass();
		final Field[] fields = getSortedDeclaredFields(clazz);
		if (initialized.add(clazz)) {
			for (Field field : fields) {
				if (!Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()))
					continue;
				writeField(field, null);
			}
		}
		for (final Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			writeField(field, obj);
		}
	}

	private void writeField(final Field field, final Object instance) throws IOException, ReflectiveOperationException {
		if (!field.isAccessible())
			field.setAccessible(true);
		switch (getType(field)) {
		case BOOLEAN_TYPE:
			writeBoolean(field.getBoolean(instance));
			break;
		case BYTE_TYPE:
			writeByte(field.getByte(instance));
			break;
		case SHORT_TYPE:
			writeShort(field.getShort(instance));
			break;
		case CHAR_TYPE:
			writeChar(field.getChar(instance));
			break;
		case INT_TYPE:
			writeInt(field.getInt(instance));
			break;
		case LONG_TYPE:
			writeLong(field.getLong(instance));
			break;
		case FLOAT_TYPE:
			writeFloat(field.getFloat(instance));
			break;
		case DOUBLE_TYPE:
			writeDouble(field.getDouble(instance));
			break;
		case STRING_TYPE:
			writeUTF((String) field.get(instance));
			break;
		case OBJECT_TYPE:
			writeObject(field.get(instance));
			break;
		}
	}

	private void writeArray(Object obj) throws IOException {
		switch (getType(obj.getClass().getComponentType())) {
		case BOOLEAN_TYPE:
			boolean[] barr = (boolean[]) obj;
			writeInt(barr.length);
			for (boolean b : barr)
				writeBoolean(b);
			break;
		case BYTE_TYPE:
			byte[] byteArr = (byte[]) obj;
			writeInt(byteArr.length);
			for (byte b : byteArr)
				writeByte(b);
			break;
		case SHORT_TYPE:
			short[] sarr = (short[]) obj;
			writeInt(sarr.length);
			for (short s : sarr)
				writeShort(s);
			break;
		case CHAR_TYPE:
			char[] carr = (char[]) obj;
			writeInt(carr.length);
			for (char c : carr)
				writeChar(c);
			break;
		case INT_TYPE:
			int[] iarr = (int[]) obj;
			writeInt(iarr.length);
			for (int i : iarr)
				writeInt(i);
			break;
		case LONG_TYPE:
			long[] larr = (long[]) obj;
			writeInt(larr.length);
			for (long l : larr)
				writeLong(l);
			break;
		case FLOAT_TYPE:
			float[] farr = (float[]) obj;
			writeInt(farr.length);
			for (float f : farr)
				writeFloat(f);
			break;
		case DOUBLE_TYPE:
			double[] darr = (double[]) obj;
			writeInt(darr.length);
			for (double d : darr)
				writeDouble(d);
			break;
		case STRING_TYPE:
			String[] strArr = (String[]) obj;
			writeInt(strArr.length);
			for (String str : strArr)
				writeUTF(str);
			break;
		case OBJECT_TYPE:
			Object[] oarr = (Object[]) obj;
			writeInt(oarr.length);
			for (Object o : oarr)
				writeObject(o);
			break;
		}
	}

	private static int getType(final Class<?> clazz) {
		if (clazz == boolean.class)
			return BOOLEAN_TYPE;
		if (clazz == byte.class)
			return BYTE_TYPE;
		if (clazz == short.class)
			return SHORT_TYPE;
		if (clazz == char.class)
			return CHAR_TYPE;
		if (clazz == int.class)
			return INT_TYPE;
		if (clazz == long.class)
			return LONG_TYPE;
		if (clazz == float.class)
			return FLOAT_TYPE;
		if (clazz == double.class)
			return DOUBLE_TYPE;
		if (clazz == String.class)
			return STRING_TYPE;
		return OBJECT_TYPE;
	}

	private static int getType(final Field field) {
		return getType(field.getType());
	}

	@Override
	public void writeBoolean(final boolean v) throws IOException {
		os.writeBoolean(v);
	}

	@Override
	public void writeByte(final int v) throws IOException {
		os.writeByte(v);
	}

	@Override
	public void writeShort(final int v) throws IOException {
		os.writeShort(v);
	}

	@Override
	public void writeChar(final int v) throws IOException {
		os.writeChar(v);
	}

	@Override
	public void writeInt(final int v) throws IOException {
		os.writeInt(v);
	}

	@Override
	public void writeLong(final long v) throws IOException {
		os.writeLong(v);
	}

	@Override
	public void writeFloat(final float v) throws IOException {
		os.writeFloat(v);
	}

	@Override
	public void writeDouble(final double v) throws IOException {
		os.writeDouble(v);
	}

	@Override
	public void writeBytes(final String s) throws IOException {
		os.writeBytes(s);
	}

	@Override
	public void writeChars(final String s) throws IOException {
		os.writeChars(s);
	}

	@Override
	public void writeUTF(final String s) throws IOException {
		if (s == null) {
			write(0);
		} else {
			write(1);
			os.writeUTF(s);
		}
	}

	private static Field[] getSortedDeclaredFields(Class<?> clazz) {
		final Field[] fields = clazz.getDeclaredFields();
		Arrays.sort(fields, new Comparator<Field>() {
			@Override
			public int compare(Field o1, Field o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return fields;
	}

}