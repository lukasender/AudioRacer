package at.fhv.audioracer.simulator.player.pivot.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.pivot.wtk.ApplicationContext;

public class PivotThreadProxy implements InvocationHandler {
	
	private Object _base;
	private Object _proxy;
	
	public PivotThreadProxy(Object base) throws IllegalArgumentException {
		if (base == null) {
			throw new IllegalArgumentException("base must not be null!");
		}
		
		_base = base;
		_proxy = createProxy(_base.getClass());
	}
	
	private Object createProxy(Class<?> baseClass) {
		Class<?> proxyClass = Proxy.getProxyClass(baseClass.getClassLoader(),
				baseClass.getInterfaces());
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
		ApplicationContext.queueCallback(new Runnable() {
			
			@Override
			public void run() {
				try {
					method.invoke(_base, args);
				} catch (Exception e) {
					throw new RuntimeException(
							"Unexpected invocation exception: " + e.getMessage(), e);
				}
			}
		});
		
		return null;
	}
}
