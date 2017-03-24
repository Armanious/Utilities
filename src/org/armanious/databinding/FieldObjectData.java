package org.armanious.databinding;

import java.lang.reflect.Field;

public class FieldObjectData<T> extends ObjectData<T> {
	
	private final Object obj;
	private final Field field;
	
	public FieldObjectData(Object obj, String fieldName) {
		this.obj = obj;
		Class<?> searching = obj instanceof Class<?> ? (Class<?>) obj : obj.getClass();
		Field field = null;
		do{
			try{
				field = searching.getDeclaredField(fieldName);
			}catch(NoSuchFieldException e){
				
			}
		}while(field == null && (searching = searching.getSuperclass()) != null);
		this.field = field;
		if(!this.field.isAccessible())
			this.field.setAccessible(true);
	}

	public FieldObjectData(Object obj, Field field) {
		this.obj = obj;
		this.field = field;
		if(!field.isAccessible())
			field.setAccessible(true);
	}
	
	@Override
	public void set(T value) {
		try{
			field.set(obj, value);
		}catch(ReflectiveOperationException e){
			e.printStackTrace();
		}
	}

}
