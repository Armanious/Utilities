package org.armanious.io;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class IOUtils {
	
	public static Proxy GLOBAL_PROXY = Proxy.NO_PROXY;//new Proxy(Proxy.Type.HTTP, new InetSocketAddress("176.9.209.113", 8080));

	private IOUtils(){}

	public static byte[] readFile(String s) throws IOException {
		return readFile(new File(s));
	}

	public static byte[] readFile(File file) throws IOException {
		try(final FileInputStream in = new FileInputStream(file)){
			return readAllBytes(in);
		}
	}

	public static byte[] readURL(String s) throws IOException {
		return readURL(new URL(s));
	}
	
	public static byte[] readURL(String s, Proxy proxy) throws IOException {
		return readURL(new URL(s), proxy);
	}
	
	public static byte[] readURL(URL url) throws IOException {
		return readURL(url, GLOBAL_PROXY);
	}

	public static byte[] readURL(URL url, Proxy proxy) throws IOException {
		URLConnection con = url.openConnection(proxy);
		//con.addRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		//con.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		//con.addRequestProperty("Accept-Language", "en-gb,en;q=0.5");
		con.addRequestProperty("Connection", "keep-alive");
		con.addRequestProperty("Keep-Alive", "300");
		con.addRequestProperty("User-Agent", "Mozilla/7.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.8.0.6) Gecko/20060728 Firefox/1.5.0.6");
		
		return readAllBytes(con.getInputStream());
	}

	public static byte[] readAllBytes(InputStream in) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read;
		final byte[] data = new byte[4096];
		while((read = in.read(data, 0, 4096)) != -1){
			out.write(data, 0, read);
		}
		return out.toByteArray();
	}

	public static void writeToFile(byte[] bytes, String file) throws IOException {
		writeToFile(bytes, new File(file));
	}

	public static void writeToFile(byte[] bytes, File file) throws IOException {
		try(final FileOutputStream out = new FileOutputStream(file)){
			writeToStream(bytes, out);
		}
	}

	public static void writeToFile(InputStream in, String file) throws IOException {
		writeToFile(in, new File(file));
	}

	public static void writeToFile(InputStream in, File file) throws IOException {
		try(final FileOutputStream out = new FileOutputStream(file)){
			writeToStream(in, out);
		}
	}

	public static void writeToStream(byte[] bytes, OutputStream out) throws IOException {
		writeToStream(new ByteArrayInputStream(bytes), out);
	}

	public static void writeToStream(InputStream in, OutputStream out) throws IOException {
		final byte[] data = new byte[4096];
		int read;
		while((read = in.read(data, 0, 4096)) != -1){
			out.write(data, 0, read);
		}
		out.flush();
	}

}
