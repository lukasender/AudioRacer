package at.fhv.audioracer.client.android.network.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AndroidThreadProxy implements InvocationHandler {
	
	private IThreadProxy _base;
	private Object _proxy;
	
	public AndroidThreadProxy(IThreadProxy base) throws IllegalArgumentException {
		if (base == null) {
			throw new IllegalArgumentException("base must not be null!");
		}
		
		_base = base;
		_proxy = createProxy(_base.getClass());
	}
	
	private Object createProxy(Class<?> baseClass) {
		Class<?> proxyClass = Proxy.getProxyClass(baseClass.getClassLoader(), baseClass.getInterfaces());
		Constructor<?> constructor;
		try {
			constructor = proxyClass.getConstructor(InvocationHandler.class);
			return constructor.newInstance(this);
		} catch (Exception e) {
			throw new RuntimeException("Couldn't create the requested proxy object.", e);
		}
	}
	
	public Object getProxy() {
		return _proxy;
	}
	
	@Override
	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
		_base.getView().post(new Runnable() {
			
			@Override
			public void run() {
				try {
					method.invoke(_base, args);
				} catch (Exception e) {
					throw new RuntimeException("Unexpected invocation exception: " + e.getMessage(), e);
				}
			}
		});
		
		return null;
	}
}
