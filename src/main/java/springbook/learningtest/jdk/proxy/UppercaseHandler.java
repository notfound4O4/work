package springbook.learningtest.jdk.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class UppercaseHandler implements InvocationHandler {

	Object target;

	public UppercaseHandler(Object target) {
		this.target = target;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Object ret = method.invoke(target, args);
		
		if (ret instanceof String && method.getName().startsWith("say")) {//리턴 타입과 메소드 이름이 일치하는 경우에만 부가기능을 적용할 수도 있음.
			return ((String) ret).toUpperCase();
		} else {
			return ret;
		}
	}
}
