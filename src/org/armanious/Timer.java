package org.armanious;

public class Timer {

	private long length;
	private long endAt;
	private long startTime;

	public Timer(long length) {
		setLength(length);
	}

	public long getElapsed() {
		return System.currentTimeMillis() - startTime;
	}

	public long getRemaining() {
		return endAt - System.currentTimeMillis();
	}

	public boolean isRunning() {
		return getRemaining() > 0;
	}

	public void reset() {
		setLength(length);
	}

	public void setLength(long length) {
		this.length = length;
		startTime = System.currentTimeMillis();
		endAt = System.currentTimeMillis() + length;
	}
	
	public static String format(long length){
		final long h = length / 3600000;
		length %= 3600000;
		final long m = length / 60000;
		length %= 60000;
		final long s = length / 1000;
		final StringBuilder sb = new StringBuilder();
		if(h < 10)
			sb.append('0');
		sb.append(h);
		sb.append(':');
		if(m < 10)
			sb.append('0');
		sb.append(m);
		sb.append(':');
		if(s < 10)
			sb.append('0');
		sb.append(s);
		return sb.toString();
	}

}
