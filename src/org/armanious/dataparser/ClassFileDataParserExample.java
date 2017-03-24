package org.armanious.dataparser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.armanious.ClassFile;
import org.armanious.dataparser.dataparts.DataPart;
import org.armanious.io.IOUtils;


public class ClassFileDataParserExample {

	public static void main(String...args) throws Exception {
		//System.exit(0);
		final String ClassFileFormat = "" +
				"***DEFINITIONS_START***" +
				"[cp_info={byte::tag}{cp_sub_info(#tag#)::info}]" +
				"[cp_sub_info(1)=CONSTANT_Utf8][CONSTANT_Utf8={ushort::length}{byte[#length#]::bytes}]" +
				"[cp_sub_info(3)=CONSTANT_Integer][CONSTANT_Integer={int::value}]" +
				"[cp_sub_info(4)=CONSTANT_Float][CONSTANT_Float={int::value}]" +
				"[cp_sub_info(5)=CONSTANT_Long][CONSTANT_Long={long::value}]" +
				"[cp_sub_info(6)=CONSTANT_Double][CONSTANT_Double={long::value}]" +
				"[cp_sub_info(7)=CONSTANT_Class][CONSTANT_Class={ushort::name_index}]" +
				"[cp_sub_info(8)=CONSTANT_String][CONSTANT_String={ushort::string_index}]" +
				"[cp_sub_info(9)=CONSTANT_Fieldref][CONSTANT_Fieldref={ushort::class_index}{ushort::name_and_type_index}]" +
				"[cp_sub_info(10)=CONSTANT_Methodref][CONSTANT_Methodref={ushort::class_index}{ushort::name_and_type_index}]" +
				"[cp_sub_info(11)=CONSTANT_InterfaceMethodref][CONSTANT_InterfaceMethodref={ushort::class_index}{ushort::name_and_type_index}]" +
				"[cp_sub_info(12)=CONSTANT_NameAndType][CONSTANT_NameAndType={ushort::name_index}{ushort::descriptor_index}]" +
				"[cp_sub_info(15)=CONSTANT_MethodHandle][CONSTANT_MethodHandle={byte::reference_kind}{ushort::reference_index}]" +
				"[cp_sub_info(16)=CONSTANT_MethodType][CONSTANT_MethodType={ushort::descriptor_index}]" +
				"[cp_sub_info(18)=CONSTANT_InvokeDynamic][CONSTANT_InvokeDynamic={ushort::bootstrap_method_attr_index}{ushort::name_and_type_index}]" +
				"[field_info={ushort::access_flags}{ushort::name_index}{ushort::descriptor_index}{ushort::attributes_count}{attribute_info[#attributes_count#]::attributes}]" +
				"[method_info={ushort::access_flags}{ushort::name_index}{ushort::descriptor_index}{ushort::attributes_count}{attribute_info[#attributes_count#]::attributes}]" +
				"[attribute_info={ushort::attribute_name_index}{int::attribute_length}{byte[#attribute_length#]::info}]" +
				"***DEFINITIONS_END***" +
				"{int::magic}" +
				"{ushort::minor_version}" +
				"{ushort::major_version}" +
				"{ushort::constant_pool_count}" +
				"{@org.armanious.DataParserExample$ConstantPool::constant_pool}" +
				"{ushort::access_flags}" +
				"{ushort::this_class}" +
				"{ushort::super_class}" +
				"{ushort::interfaces_count}" +
				"{ushort[#interfaces_count#]::interfaces}" +
				"{ushort::fields_count}" +
				"{field_info[#fields_count#]::fields}" +
				"{ushort::methods_count}" +
				"{method_info[#methods_count#]::methods}" +
				"{ushort::attributes_count}" +
				"{attribute_info[#attributes_count#]::attributes}";

		System.out.println(DataParser.parse(ClassFileFormat).generateSourceFileForJavaObject("ClassFile"));

		final DataParser df = DataParser.parse(ClassFileFormat);
		final byte[] src = IOUtils.readFile("C:\\Users\\David\\Documents\\GitHub\\LearningJava\\bin\\org\\armanious\\RuzzleCheater.class");
		DataObject obj = df.load(src);
		
		try(final FileOutputStream fos = new FileOutputStream("C:\\Users\\David\\Desktop\\RuzzleCheater.class")){
			df.save(obj, fos);
		}
		final byte[] dst1 = IOUtils.readFile("C:\\Users\\David\\Desktop\\RuzzleCheater.class");

		final ClassFile classFile = new ClassFile();
		df.saveToJavaObject(obj, classFile);
		
		obj = df.loadFromJavaObject(classFile);
		try(final FileOutputStream fos = new FileOutputStream("C:\\Users\\David\\Desktop\\RuzzleCheater.class")){
			df.save(obj, fos);
		}
		final byte[] dst2 = IOUtils.readFile("C:\\Users\\David\\Desktop\\RuzzleCheater.class");
		
		System.out.println("src == dst1: " + isEqual(src, dst1));
		System.out.println("src == dst2: " + isEqual(src, dst2));
		System.out.println("dst1 == dst2: " + isEqual(dst1, dst1));
	}
	
	private static boolean isEqual(byte[] arr1, byte[] arr2){
		if(arr1.length != arr2.length)
			return false;
		for(int i = 0; i < arr1.length; i++){
			if(arr1[i] != arr2[i]){
				return false;
			}
		}
		return true;
	}

	public static class ConstantPool extends DataPart {

		public ConstantPool(String name) {
			super(name);
		}

		@Override
		public void save(DataObject obj, DataOutputStream out, DataParser df) throws IOException {

			final ArrayList<DataPart> parts = df.getDefinitionDataParts("cp_info");

			DataObject[] dataObjs = (DataObject[]) obj.get(name);
			for(DataObject dataObj : dataObjs){
				if(dataObj != null){ //after cp_info item with tag 5 or 6, the next DataObject will be null; constant_pool[0] is null as well
					for(DataPart part : parts){
						part.save(dataObj, out, df);
					}
				}
			}

		}

		@Override
		public Object load(DataObject obj, DataInputStream in, DataParser df) throws IOException {
			final ArrayList<DataPart> parts = df.getDefinitionDataParts("cp_info");
			DataObject[] loadedObj = new DataObject[obj.getInt("constant_pool_count")];
			for(int i = 1; i < loadedObj.length; i++){
				loadedObj[i] = new DataObject(obj, "constant_pool[" + i + "]");
				for(DataPart part : parts){
					part.load(loadedObj[i], in, df);
				}
				int tag = loadedObj[i].getByte("tag");
				if(tag == 5 || tag == 6){
					i++;
				}
			}
			obj.set(name, loadedObj);
			return loadedObj;
		}

		@Override
		public String toString() {
			return "{cp_info::constant_pool}";
		}

		@Override
		public String generateSource(String indentation) {
			return indentation + "public cp_info[] constant_pool;";
		}

		@Override
		public Object loadFromJavaObject(DataObject obj, Object javaObj, DataParser df) throws ReflectiveOperationException {
			final ArrayList<DataPart> parts = df.getDefinitionDataParts("cp_info");
			DataObject[] loadedObj = new DataObject[obj.getInt("constant_pool_count")];
			final Object[] otherConstantPool = (Object[]) javaObj.getClass().getField(name).get(javaObj);
			for(int i = 1; i < loadedObj.length; i++){
				loadedObj[i] = new DataObject(obj, "constant_pool[" + i + "]");
				for(DataPart part : parts){
					part.loadFromJavaObject(loadedObj[i], otherConstantPool[i], df);
				}
				int tag = loadedObj[i].getByte("tag");
				if(tag == 5 || tag == 6){
					i++;
				}
			}
			obj.set(name, loadedObj);
			return loadedObj;
		}

	}

}
