package top.iseason.bukkit.bukkittemplate.dependency;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;

public class IsolatedClassLoader extends URLClassLoader {
    private static MethodHandle addUrlHandle;
    private static Object ucp;

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public IsolatedClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Unsafe unsafe = (Unsafe) f.get(null);
            Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
            ucp = unsafe.getObject(parent, unsafe.objectFieldOffset(ucpField));
            Field lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            Object lookupBase = unsafe.staticFieldBase(lookupField);
            long lookupOffset = unsafe.staticFieldOffset(lookupField);
            MethodHandles.Lookup lookup = (MethodHandles.Lookup) unsafe.getObject(lookupBase, lookupOffset);
            addUrlHandle = lookup.findVirtual(ucp.getClass(), "addURL", MethodType.methodType(void.class, URL.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
        try {
            addUrlHandle.invoke(ucp, url);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
