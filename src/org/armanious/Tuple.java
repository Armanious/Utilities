package org.armanious;

import java.lang.reflect.Method;

public final class Tuple<A, B> implements Cloneable {

	public A val1;
	public B val2;
	
	public Tuple(){
		
	}

	public Tuple(A val1, B val2){
		this.val1 = val1;
		this.val2 = val2;
	}

	@Override
	public Tuple<A, B> clone() {
		return new Tuple<>(clone(val1), clone(val2));
	}

	@SuppressWarnings("unchecked")
	private static <T> T clone(T t){
		try{
			final Method m = t.getClass().getMethod("clone", new Class[]{});
			if(!m.isAccessible()){
				m.setAccessible(true);
			}
			return (T) m.invoke(t);
		}catch(ReflectiveOperationException e){
			//cloning not supported
		}
		return t;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Tuple){
			final Tuple<?, ?> t = (Tuple<?, ?>) obj;
			if(t.val1 == null){
				if(val1 != null){
					return false;
				}
			}else{
				if(!t.val1.equals(val1)){
					return false;
				}
			}
			if(t.val2 == null){
				if(val2 != null){
					return false;
				}
			}else{
				if(!t.val2.equals(val2)){
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (val1 == null ? 19 : val1.hashCode()) * 31 + (val2 == null ? 41 : val2.hashCode());
	}

	@Override
	public String toString() {
		return "Tuple(" + (val1 == null ? "null" : val1.toString()) + ", " + (val2 == null ? "null" : val2.toString()) + ')';
	}

}
