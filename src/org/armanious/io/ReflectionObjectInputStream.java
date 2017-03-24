package org.armanious.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import sun.misc.Unsafe;

public class ReflectionObjectInputStream extends InputStream implements AutoCloseable, ObjectInput {

	private static final int BOOLEAN_TYPE = 0, BYTE_TYPE = 1, SHORT_TYPE = 2, CHAR_TYPE = 3, INT_TYPE = 4, LONG_TYPE = 5, FLOAT_TYPE = 6, DOUBLE_TYPE = 7,
			STRING_TYPE = 8, OBJECT_TYPE = 9;

	private static final Unsafe unsafe = getUnsafe();

	private static Unsafe getUnsafe() {
		try {
			final Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			return (Unsafe) field.get(null);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static int callcount;

	private final DataInputStream in;
	private final Set<Class<?>> initialized = new HashSet<>();
	private final ArrayList<Object> list = new ArrayList<>();

	public ReflectionObjectInputStream(final InputStream in) {
		this.in = new DataInputStream(in);
	}

	@Override
	public void readFully(final byte[] b) throws IOException {
		in.readFully(b);
	}

	@Override
	public void readFully(final byte[] b, final int off, final int len) throws IOException {
		in.readFully(b, off, len);
	}

	@Override
	public int skipBytes(final int n) throws IOException {
		return in.skipBytes(n);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return in.readBoolean();
	}

	@Override
	public byte readByte() throws IOException {
		return in.readByte();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return in.readUnsignedByte();
	}

	@Override
	public short readShort() throws IOException {
		return in.readShort();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return in.readUnsignedShort();
	}

	@Override
	public char readChar() throws IOException {
		return in.readChar();
	}

	@Override
	public int readInt() throws IOException {
		return in.readInt();
	}

	@Override
	public long readLong() throws IOException {
		return in.readLong();
	}

	@Override
	public float readFloat() throws IOException {
		return in.readFloat();
	}

	@Override
	public double readDouble() throws IOException {
		return in.readDouble();
	}

	@Override
	@Deprecated
	public String readLine() throws IOException {
		return in.readLine();
	}

	@Override
	public Object readObject() throws IOException {
		callcount++;
		final int signal = read();
		Object obj = null;
		if (signal == 2) {
			obj = list.get(readInt());
		} else if (signal == 0) {
			obj = readObject0();
		}
		callcount--;
		if (callcount == 0)
			list.clear();
		return obj;
	}

	private Object readObject0() throws IOException {
		try {
			final String clazz = readUTF();
			final Object obj;
			if (clazz.charAt(0) == '[')
				obj = readArray(clazz);
			else
				obj = readObjectInternal(Class.forName(clazz));
			return obj;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Object readObjectInternal(Class<?> clazz) throws IOException, ReflectiveOperationException {
		final Field[] fields = getSortedDeclaredFields(clazz);
		if (initialized.add(clazz)) {
			for (Field field : fields) {
				if (!Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()))
					continue;
				readField(field, null);
			}
		}
		unsafe.ensureClassInitialized(clazz);
		final Object instance = unsafe.allocateInstance(clazz);
		list.add(instance);
		for (final Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			readField(field, instance);
		}
		return instance;
	}

	private void readField(final Field field, final Object instance) throws IOException, ReflectiveOperationException {
		if (!field.isAccessible())
			field.setAccessible(true);
		switch (getType(field)) {
		case BOOLEAN_TYPE:
			field.setBoolean(instance, readBoolean());
			break;
		case BYTE_TYPE:
			field.setByte(instance, readByte());
			break;
		case SHORT_TYPE:
			field.setShort(instance, readShort());
			break;
		case CHAR_TYPE:
			field.setChar(instance, readChar());
			break;
		case INT_TYPE:
			field.setInt(instance, readInt());
			break;
		case LONG_TYPE:
			field.setLong(instance, readLong());
			break;
		case FLOAT_TYPE:
			field.setFloat(instance, readFloat());
			break;
		case DOUBLE_TYPE:
			field.setDouble(instance, readDouble());
			break;
		case STRING_TYPE:
			field.set(instance, readUTF());
			break;
		case OBJECT_TYPE:
			field.set(instance, readObject());
			break;
		}
	}

	private Object readArray(String clazz) throws IOException, ReflectiveOperationException {
		final int length = readInt();
		final String componentType = clazz.substring(1);
		switch (componentType.charAt(0)) {
		case 'Z':
			final boolean[] zarr = (boolean[]) Array.newInstance(boolean.class, length);
			for (int i = 0; i < length; i++) {
				zarr[i] = readBoolean();
			}
			return zarr;
		case 'B':
			final byte[] barr = (byte[]) Array.newInstance(byte.class, length);
			for (int i = 0; i < length; i++) {
				barr[i] = readByte();
			}
			return barr;
		case 'C':
			final char[] carr = (char[]) Array.newInstance(char.class, length);
			for (int i = 0; i < length; i++) {
				carr[i] = readChar();
			}
			return carr;
		case 'S':
			final short[] sarr = (short[]) Array.newInstance(short.class, length);
			for (int i = 0; i < length; i++) {
				sarr[i] = readShort();
			}
			return sarr;
		case 'I':
			final int[] iarr = (int[]) Array.newInstance(int.class, length);
			for (int i = 0; i < length; i++) {
				iarr[i] = readInt();
			}
			return iarr;
		case 'J':
			final long[] jarr = (long[]) Array.newInstance(long.class, length);
			for (int i = 0; i < length; i++) {
				jarr[i] = readLong();
			}
			return jarr;
		case 'F':
			final float[] farr = (float[]) Array.newInstance(float.class, length);
			for (int i = 0; i < length; i++) {
				farr[i] = readFloat();
			}
			return farr;
		case 'D':
			final double[] darr = (double[]) Array.newInstance(double.class, length);
			for (int i = 0; i < length; i++) {
				darr[i] = readChar();
			}
			return darr;
		case 'L':
			final String componentTypeClassName = componentType.substring(1, componentType.length() - 1);
			final Object[] larr = (Object[]) Array.newInstance(Class.forName(componentTypeClassName), length);
			if (componentTypeClassName.equals("java.lang.String")) {
				for (int i = 0; i < length; i++) {
					larr[i] = readUTF();
				}
			} else {
				for (int i = 0; i < length; i++) {
					larr[i] = readObject();
				}
			}
			return larr;
		default:
			final Object[] oarr = (Object[]) Array.newInstance(Class.forName(componentType), length);
			for (int i = 0; i < length; i++) {
				oarr[i] = readObject();
			}
			return oarr;
		}
	}

	@Override
	public int read() throws IOException {
		return in.read();
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

	private static int getType(final Field field) {
		if (field.getType() == boolean.class)
			return BOOLEAN_TYPE;
		if (field.getType() == byte.class)
			return BYTE_TYPE;
		if (field.getType() == short.class)
			return SHORT_TYPE;
		if (field.getType() == char.class)
			return CHAR_TYPE;
		if (field.getType() == int.class)
			return INT_TYPE;
		if (field.getType() == long.class)
			return LONG_TYPE;
		if (field.getType() == float.class)
			return FLOAT_TYPE;
		if (field.getType() == double.class)
			return DOUBLE_TYPE;
		if (field.getType() == String.class)
			return STRING_TYPE;
		return OBJECT_TYPE;
	}

	@Override
	public String readUTF() throws IOException {
		if (in.read() != 0)
			return in.readUTF();
		return null;
	}

}
