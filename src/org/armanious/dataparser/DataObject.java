package org.armanious.dataparser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DataObject {

	private final DataObject parent;
	private final String name;
	private final Map<String, Object> data = new HashMap<>();

	public DataObject(){
		this(null, "");
	}
	
	public DataObject(DataObject parent, String name){
		this.parent = parent;
		this.name = name;
	}

	public DataObject getParent(){
		return parent;
	}

	public void set(String name, Object o) {
		if(data.containsKey(name))
			throw new IllegalStateException("DataObject cannot have fields of the same name (" + name + ")");
		data.put(name, o);
	}

	public Object get(String name){
		return data.get(name);
	}

	public byte getByte(String name) {
		Object o = get(name);
		if(o instanceof Byte)
			return (Byte)o;
		throw new IllegalArgumentException(name + " is not a byte; it is a " + (o == null ? "null" : o.getClass().getName()));
	}

	public int getUByte(String name) {
		return getInt(name);
	}

	public short getShort(String name){
		Object o = get(name);
		if(o instanceof Short)
			return (Short)o;
		throw new IllegalArgumentException(name + " is not a short; it is a " + (o == null ? "null" : o.getClass().getName()));
	}
	
	public int getUShort(String name){
		return getInt(name);
	}

	public char getChar(String name){
		Object o = get(name);
		if(o instanceof Character)
			return (Character)o;
		throw new IllegalArgumentException(name + " is not a char; it is a " + (o == null ? "null" : o.getClass().getName()));
	}

	public int getInt(String name){
		Object o = get(name);
		if(o instanceof Integer)
			return (Integer)o;
		throw new IllegalArgumentException(name + " is not an int; it is a " + (o == null ? "null" : o.getClass().getName()));
	}

	public float getFloat(String name){
		Object o = get(name);
		if(o instanceof Float)
			return (Float)o;
		throw new IllegalArgumentException(name + " is not a float; it is a " + (o == null ? "null" : o.getClass().getName()));
	}

	public long getLong(String name){
		Object o = get(name);
		if(o instanceof Long)
			return (Long)o;
		throw new IllegalArgumentException(name + " is not a long; it is a " + (o == null ? "null" : o.getClass().getName()));
	}

	public double getDouble(String name){
		Object o = get(name);
		if(o instanceof Double)
			return (Double)o;
		throw new IllegalArgumentException(name + " is not a double; it is a " + (o == null ? "null" : o.getClass().getName()));
	}

	public boolean getBoolean(String name){
		Object o = get(name);
		if(o instanceof Boolean)
			return (Boolean)o;
		throw new IllegalArgumentException(name + " is not a boolean; it is a " + (o == null ? "null" : o.getClass().getName()));
	}

	public String toString(){
		return name + "=" + String.valueOf(data);
	}

	public String resolve(String s) {
		/*	"[cp_info={byte::tag}{cp_sub_info(#tag#)::info}]" +
					"{cp_info[#constant_pool_count#-1]::constant_pool}" +*/
		do{
			int idx = s.indexOf('#');
			if(idx != -1){
				int endIdx = s.indexOf('#', idx + 1);
				String fieldRef = s.substring(idx + 1, endIdx);
				s = s.replace("#" + fieldRef + "#", get(fieldRef).toString());
				continue;
			}
			break;
		}while(true);
		do{
			//TODO +#x# -#x# ~#x# !#x# #x#<<#y# #x#>>#y# #x#>>>#y# &#x# ^#x# |#x#
			//* / % + -
			int i1 = s.indexOf('*');
			if(i1 == -1) i1 = Integer.MAX_VALUE;
			int i2 = s.indexOf('/');
			if(i2 == -1) i2 = Integer.MAX_VALUE;
			int i3 = s.indexOf('%');
			if(i3 == -1) i3 = Integer.MAX_VALUE;
			int idx = Math.min(Math.min(i1, i2), i3);
			if(idx != Integer.MAX_VALUE){
				long left = Long.parseLong(s.substring(0, idx));
				long right = Long.parseLong(s.substring(idx + 1));
				long result;
				char c = s.charAt(idx);
				switch(c){
				case '*':
					result = left * right;
					break;
				case '/':
					result = left / right;
					break;
				case '%':
					result = left % right;
					break;
				default:
					throw new RuntimeException();
				}
				s = String.valueOf(result); //TODO
				continue;
			}
			i1 = s.indexOf('+');
			if(i1 == -1) i1 = Integer.MAX_VALUE;
			i2 = s.indexOf('-');
			if(i2 == -1) i2 = Integer.MAX_VALUE;
			idx = Math.min(i1, i2);
			if(idx != Integer.MAX_VALUE){
				long left = Long.parseLong(s.substring(0, idx));
				long right = Long.parseLong(s.substring(idx + 1));
				long result;
				char c = s.charAt(idx);
				switch(c){
				case '+':
					result = left + right;
					break;
				case '-':
					result = left - right;
					break;
				default:
					throw new RuntimeException();
				}
				s = String.valueOf(result); //TODO
				continue;
			}
			break;
		}while(true);
		return s;
	}
	
	public Map<String, Object> getData(){
		return Collections.unmodifiableMap(data);
	}

	public String getName() {
		return name;
	}

}
