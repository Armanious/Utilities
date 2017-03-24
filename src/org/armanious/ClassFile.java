package org.armanious;

public class ClassFile {
    
    public int magic;
    public int minor_version;
    public int major_version;
    public int constant_pool_count;
    public cp_info[] constant_pool;
    public int access_flags;
    public int this_class;
    public int super_class;
    public int interfaces_count;
    public int[] interfaces;
    public int fields_count;
    public field_info[] fields;
    public int methods_count;
    public method_info[] methods;
    public int attributes_count;
    public attribute_info[] attributes;
    
    public class CONSTANT_Utf8 extends cp_sub_info {
        public int length;
        public byte[] bytes;
    }

    public class CONSTANT_Float extends cp_sub_info {
        public int value;
    }

    public class method_info {
        public int access_flags;
        public int name_index;
        public int descriptor_index;
        public int attributes_count;
        public attribute_info[] attributes;
    }

    public class field_info {
        public int access_flags;
        public int name_index;
        public int descriptor_index;
        public int attributes_count;
        public attribute_info[] attributes;
    }

    public class CONSTANT_Integer extends cp_sub_info {
        public int value;
    }

    public class CONSTANT_Double extends cp_sub_info {
        public long value;
    }

    public class CONSTANT_NameAndType extends cp_sub_info {
        public int name_index;
        public int descriptor_index;
    }

    public class CONSTANT_InterfaceMethodref extends cp_sub_info {
        public int class_index;
        public int name_and_type_index;
    }

    public class attribute_info {
        public int attribute_name_index;
        public int attribute_length;
        public byte[] info;
    }

    public class CONSTANT_InvokeDynamic extends cp_sub_info {
        public int bootstrap_method_attr_index;
        public int name_and_type_index;
    }

    public class CONSTANT_MethodHandle extends cp_sub_info {
        public byte reference_kind;
        public int reference_index;
    }

    public abstract class cp_sub_info {
        
    }

    public class CONSTANT_Long extends cp_sub_info {
        public long value;
    }

    public class CONSTANT_String extends cp_sub_info {
        public int string_index;
    }

    public class cp_info {
        public byte tag;
        public cp_sub_info info;
    }

    public class CONSTANT_MethodType extends cp_sub_info {
        public int descriptor_index;
    }

    public class CONSTANT_Fieldref extends cp_sub_info {
        public int class_index;
        public int name_and_type_index;
    }

    public class CONSTANT_Class extends cp_sub_info {
        public int name_index;
    }

    public class CONSTANT_Methodref extends cp_sub_info {
        public int class_index;
        public int name_and_type_index;
    }

}



