import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.reflect.Reflection;

import io.github.toolfactory.narcissus.Narcissus;

class VerboseClassLogTest {

	private static final String EMPTY = "";

	private static Method METHOD_GET_NAME, METHOD_GET_CLASS, METHOD_CONTAINS, METHOD_ADD, METHOD_SIZE, METHOD_GET,
			METHOD_REPLACE, METHOD_ENDS_WITH, METHOD_MATCHES, METHOD_FOR_NAME, METHOD_COLLECT, METHOD_IS_FILE = null;

	@BeforeClass
	static void beforeClass() throws Throwable {
		//
		final Class<?> clz = VerboseClassLog.class;
		//
		(METHOD_GET_NAME = clz.getDeclaredMethod("getName", Member.class)).setAccessible(true);
		//
		(METHOD_GET_CLASS = clz.getDeclaredMethod("getClass", Object.class)).setAccessible(true);
		//
		(METHOD_CONTAINS = clz.getDeclaredMethod("contains", Collection.class, Object.class)).setAccessible(true);
		//
		(METHOD_ADD = clz.getDeclaredMethod("add", Collection.class, Object.class)).setAccessible(true);
		//
		(METHOD_SIZE = clz.getDeclaredMethod("size", Collection.class)).setAccessible(true);
		//
		(METHOD_GET = clz.getDeclaredMethod("get", List.class, Integer.TYPE)).setAccessible(true);
		//
		(METHOD_REPLACE = clz.getDeclaredMethod("replace", Strings.class, String.class, String.class, String.class))
				.setAccessible(true);
		//
		(METHOD_ENDS_WITH = clz.getDeclaredMethod("endsWith", String.class, String.class)).setAccessible(true);
		//
		(METHOD_MATCHES = clz.getDeclaredMethod("matches", String.class, String.class)).setAccessible(true);
		//
		(METHOD_FOR_NAME = clz.getDeclaredMethod("forName", String.class)).setAccessible(true);
		//
		(METHOD_COLLECT = clz.getDeclaredMethod("collect", Stream.class, Collector.class)).setAccessible(true);
		//
		(METHOD_IS_FILE = clz.getDeclaredMethod("isFile", File.class)).setAccessible(true);
		//
	}

	private static class IH implements InvocationHandler {

		private Boolean add, test, contains = null;

		private Integer size = null;

		@Override
		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
			//
			final String name = getName(method);
			//
			if (Objects.equals(method != null ? method.getReturnType() : null, Void.TYPE)) {
				//
				return null;
				//
			} // if
				//
			if (proxy instanceof Collection) {
				//
				if (Objects.equals(name, "add")) {
					//
					return add;
					//
				} else if (Objects.equals(name, "size")) {
					//
					return size;
					//
				} else if (Objects.equals(name, "stream")) {
					//
					return null;
					//
				} else if (Objects.equals(name, "contains")) {
					//
					return contains;
					//
				} // if
					//
			} // if
				//
			if (proxy instanceof Member && Objects.equals(name, "getName")) {
				//
				return null;
				//
			} else if (proxy instanceof List && Objects.equals(name, "get")) {
				//
				return null;
				//
			} else if (proxy instanceof Map &&

					contains(Arrays.asList("get", "put"), name)) {
				//
				return null;
				//
			} else if (proxy instanceof Predicate && Objects.equals(name, "test")) {
				//
				return test;
				//
			} else if (proxy instanceof Entry && contains(Arrays.asList("getValue", "getKey"), name)) {
				//
				return null;
				//
			} else if (proxy instanceof FailableFunction && Objects.equals(name, "apply")) {
				//
				return null;
				//
			} else if (proxy instanceof Stream) {
				//
				if (Objects.equals(name, "collect")) {
					//
					return null;
					//
				} else if (Objects.equals(name, "filter")) {
					//
					return proxy;
					//
				} // if
					//
			} // if
				//
			throw new Throwable(name);
			//
		}

	}

	private static String getName(final Member instance) throws Throwable {
		try {
			final Object obj = invoke(METHOD_GET_NAME, null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (String) obj;
			}
			throw new Throwable(Objects.toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static Object invoke(final Method method, final Object instance, final Object... args)
			throws IllegalAccessException, InvocationTargetException {
		return method != null && method.getDeclaringClass() != null ? method.invoke(instance, args) : null;
	}

	private static Class<?> getClass(final Object instance) throws Throwable {
		try {
			final Object obj = invoke(METHOD_GET_CLASS, null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof Class) {
				return (Class<?>) obj;
			}
			throw new Throwable(Objects.toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private IH ih = null;

	@BeforeMethod
	void beforeMethod() {
		//
		ih = new IH();
		//
	}

	@Test

	void testNull() throws Throwable {
		//
		final Method[] ms = VerboseClassLog.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Object result = null;
		//
		String toString = null;
		//
		Collection<Object> collection = null;
		//
		Object[] os = null;
		//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null) {
				//
				continue;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if (Objects.equals(ArrayUtils.get(parameterTypes, j), Integer.TYPE)) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else {
					add(collection, null);
					//
				} // if
					//
			} // for
				//
			os = toArray(collection);
			//
			result = Narcissus.invokeStaticMethod(m, os);
			//
			toString = Objects.toString(m);
			//
			if (contains(Arrays.asList(Integer.TYPE, Boolean.TYPE), m.getReturnType())) {
				//
				Assert.assertNotNull(result, toString);
				//
			} else {
				//
				Assert.assertNull(result, toString);
				//
			} // if
				//
		} // for
			//
	}

	@Test
	void testNotNull() throws Throwable {
		//
		final Method[] ms = VerboseClassLog.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Class<?> parameterType = null;
		//
		Object result = null;
		//
		String toString, name = null;
		//
		Collection<Object> collection = null;
		//
		Object[] os = null;
		//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null) {
				//
				continue;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if ((parameterType = ArrayUtils.get(parameterTypes, j)) != null && parameterType.isInterface()) {
					//
					if ((ih = ObjectUtils.getIfNull(ih, IH::new)) != null) {
						//
						final List<Field> fs = FieldUtils.getAllFieldsList(getClass(ih));
						//
						Field f = null;
						//
						for (int k = 0; k < size(fs); k++) {
							//
							if ((f = get(fs, k)) == null) {
								//
								continue;
								//
							} // if
								//
							final Class<?> type = f.getType();
							//
							if (Objects.equals(type, Boolean.class)) {
								//
								Narcissus.setField(ih, f, Boolean.TRUE);
								//
							} else if (Objects.equals(type, Integer.class)) {
								//
								Narcissus.setField(ih, f, Integer.valueOf(0));
								//
							} // if
								//
						} // for
							//
					} // if
						//
					add(collection, Reflection.newProxy(parameterType, ih));
					//
				} else if (parameterType != null && parameterType.isArray()) {
					//
					add(collection, Array.newInstance(parameterType.getComponentType(), 0));
					//
				} else if (Objects.equals(parameterType, Integer.TYPE)) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else if (Objects.equals(parameterType, Class.class)) {
					//
					add(collection, Class.class);
					//
				} else if (Objects.equals(parameterType, Strings.class)) {
					//
					add(collection, Narcissus.allocateInstance(getClass(Strings.CS)));
					//
				} else {
					//
					add(collection, Narcissus.allocateInstance(parameterType));
					//
				} // if
					//
			} // for
				//
			os = toArray(collection);
			//
			result = Narcissus.invokeStaticMethod(m, os);
			//
			toString = Objects.toString(m);
			//
			if (Boolean
					.logicalAnd(Objects.equals(name = getName(m), "getName"),
							Boolean.logicalOr(Arrays.equals(parameterTypes, new Class<?>[] { Member.class }), Arrays
									.equals(parameterTypes, new Class<?>[] { Module.class })))
					|| Objects.equals(m.getReturnType(), Void.TYPE)
					|| Boolean.logicalAnd(Objects.equals(name, "get"),
							Boolean.logicalOr(
									Arrays.equals(parameterTypes, new Class<?>[] { List.class, Integer.TYPE }),
									Arrays.equals(parameterTypes, new Class<?>[] { Map.class, Object.class })))
					|| Boolean.logicalAnd(Objects.equals(name, "forName"),
							Arrays.equals(parameterTypes, new Class<?>[] { String.class }))
					|| Boolean.logicalAnd(contains(Arrays.asList("getValue", "getKey"), name),
							Arrays.equals(parameterTypes, new Class<?>[] { Entry.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "replace"),
							Arrays.equals(parameterTypes,
									new Class<?>[] { Strings.class, String.class, String.class, String.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "apply"),
							Arrays.equals(parameterTypes, new Class<?>[] { FailableFunction.class, Object.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "collect"),
							Arrays.equals(parameterTypes, new Class<?>[] { Stream.class, Collector.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "stream"),
							Arrays.equals(parameterTypes, new Class<?>[] { Collection.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "toMap"),
							Arrays.equals(parameterTypes, new Class<?>[] { String[].class }))
					|| Boolean.logicalAnd(Objects.equals(name, "testAndApply"),
							Arrays.equals(parameterTypes,
									new Class<?>[] { Predicate.class, Object.class, FailableFunction.class,
											FailableFunction.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "toEntry"),
							Arrays.equals(parameterTypes, new Class<?>[] { String.class }))) {
				//
				Assert.assertNull(result, toString);
				//
			} else {
				//
				Assert.assertNotNull(result, toString);
				//
			} // if
				//
		} // for
			//
	}

	private static <E> E get(final List<E> instance, final int index) throws Throwable {
		try {
			return (E) invoke(METHOD_GET, null, instance, index);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static int size(final Collection<?> instance) throws Throwable {
		try {
			final Object obj = invoke(METHOD_SIZE, null, instance);
			if (obj instanceof Integer) {
				return ((Integer) obj).intValue();
			}
			throw new Throwable(Objects.toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static boolean contains(final Collection<?> instance, final Object item) throws Throwable {
		try {
			final Object obj = invoke(METHOD_CONTAINS, null, instance, item);
			if (obj instanceof Boolean) {
				return ((Boolean) obj).booleanValue();
			}
			throw new Throwable(Objects.toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static <E> void add(final Collection<E> instance, final E item) throws Throwable {
		try {
			invoke(METHOD_ADD, null, instance, item);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static void clear(final Collection<?> instance) {
		if (instance != null) {
			instance.clear();
		}
	}

	private static Object[] toArray(final Collection<?> instance) {
		return instance != null ? instance.toArray() : null;
	}

	@Test
	void testMain() throws IOException {
		//
		VerboseClassLog.main(new String[] { "", "=", "= ", " =", " = ", " ==" });
		//
	}

	@Test
	void testReplace() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertEquals(invoke(METHOD_REPLACE, null, Strings.CS, EMPTY, null, null), EMPTY);
		//
		Assert.assertEquals(
				invoke(METHOD_REPLACE, null, Strings.CS, EMPTY, Narcissus.allocateInstance(String.class), null), EMPTY);
		//
		final String string = "string";
		//
		Assert.assertEquals(
				invoke(METHOD_REPLACE, null, Strings.CS, string, Narcissus.allocateInstance(String.class), null),
				string);
		//
		Assert.assertEquals(invoke(METHOD_REPLACE, null, Strings.CS, string, null, null), string);
		//
		Assert.assertEquals(invoke(METHOD_REPLACE, null, Strings.CS, string, string, null), string);
		//
		Assert.assertEquals(invoke(METHOD_REPLACE, null, Strings.CS, string, string, null), string);
		//
		Assert.assertEquals(
				invoke(METHOD_REPLACE, null, Strings.CS, string, string, Narcissus.allocateInstance(String.class)),
				string);
		//
		final String b = "b";
		//
		Assert.assertEquals(invoke(METHOD_REPLACE, null, Strings.CS, string, string, b), b);
		//
	}

	@Test
	void testEndsWith() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertEquals(invoke(METHOD_ENDS_WITH, null, EMPTY, Narcissus.allocateInstance(String.class)),
				Boolean.FALSE);
		//
		Assert.assertEquals(invoke(METHOD_ENDS_WITH, null, EMPTY, EMPTY), Boolean.TRUE);
		//
	}

	@Test
	void testMatches() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertEquals(invoke(METHOD_MATCHES, null, EMPTY, Narcissus.allocateInstance(String.class)),
				Boolean.FALSE);
		//
		Assert.assertEquals(invoke(METHOD_MATCHES, null, EMPTY, EMPTY), Boolean.TRUE);
		//
	}

	@Test
	void testForName() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNotNull(invoke(METHOD_FOR_NAME, null, "java.lang.Object"));
		//
	}

	@Test
	void testCollect() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNull(invoke(METHOD_COLLECT, null,
				Reflection.newProxy(Stream.class, ih = ObjectUtils.getIfNull(ih, IH::new)), null));
		//
		Assert.assertNull(invoke(METHOD_COLLECT, null, Stream.empty(), null));
		//
	}

	@Test
	void testIsFile() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertEquals(invoke(METHOD_IS_FILE, null, new File(".")), Boolean.FALSE);
		//
		Assert.assertEquals(invoke(METHOD_IS_FILE, null, new File("pom.xml")), Boolean.TRUE);
		//
	}

}