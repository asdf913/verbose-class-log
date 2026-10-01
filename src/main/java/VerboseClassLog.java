import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.Pair;

import io.github.toolfactory.narcissus.Narcissus;

public class VerboseClassLog {

	public static void main(final String[] args) throws IOException {
		//
		final Map<String, String> map = toMap(args);
		//
		final File file = testAndApply(Objects::nonNull, get(map, "file"), File::new, null);
		//
		final List<String> lines = testAndApply(VerboseClassLog::isFile, file,
				x -> FileUtils.readLines(x, StandardCharsets.UTF_8), null);
		//
		String line, s, name = null;
		//
		String[] ss = null;
		//
		List<String> list = null;
		//
		for (int i = 0; i < size(lines); i++) {
			//
			if ((line = get(lines, i)) == null || line.isEmpty() || line.charAt(0) != '['
					|| (ss = line.split(" ")) == null || ss.length < 1 || matches(s = ss[1], "^.+\\/\\dx\\w+$")) {
				//
				continue;
				//
			} // if
				//
			if (!contains(list = ObjectUtils.getIfNull(list, ArrayList::new), s) && !(contains(
					Arrays.asList("java.base", "java.desktop", "java.logging", "java.xml", "java.management",
							"java.datatransfer", "jdk.crypto.ec", "jdk.net", "java.rmi"),
					name = getName(getModule(forName(s)))) || matches(name, "^jdk\\.proxy\\d+$")
					|| endsWith(line, "source: shared objects file"))) {
				//
				add(list, s);
				//
			} // if
				//
		} // for
			//
		final boolean zipEntry = BooleanUtils.toBooleanDefaultIfNull(Boolean.valueOf(get(map, "zipEntry")), false);
		//
		for (int i = 0; i < size(list); i++) {
			//
			s = get(list, i);
			//
			if (zipEntry) {
				//
				System.out.println(StringUtils.joinWith(".", replace(Strings.CS, s, ".", "/"), "class"));
				//
			} else {
				//
				System.out.println(s);
				//
			} // if
				//
		} // for
			//
	}

	private static boolean isFile(final File instance) {
		return instance != null
				&& instance.getPath() != null
				&& instance.isFile();
	}

	private static String replace(final Strings instance, final String text, final String searchString,
			final String replacement) {
		//
		if (instance == null) {
			//
			return null;
			//
		} // if
			//
		final Field field = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(text), FieldUtils::getAllFieldsList, null)),
						x -> Objects.equals(getName(x), "value")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		if (field != null && Narcissus.getField(text, field) == null) {
			//
			return null;
			//
		} // if
			//
		if (StringUtils.isNotEmpty(text) && field != null
				&& Boolean.logicalOr(searchString != null && Narcissus.getField(searchString, field) == null,
						replacement != null && Narcissus.getField(replacement, field) == null)) {
			//
			return text;
			//
		} // if
			//
		return instance.replace(text, searchString, replacement);
		//
	}

	private static <V> V get(final Map<?, V> instance, final Object key) {
		return instance != null ? instance.get(key) : null;
	}

	private static Map<String, String> toMap(final String... ss) {
		//
		Map<String, String> map = null;
		//
		Entry<String, String> entry = null;
		//
		for (int i = 0; i < length(ss); i++) {
			//
			if ((entry = toEntry(ArrayUtils.get(ss, i))) == null) {
				//
				continue;
				//
			} // if
				//
			put(map = ObjectUtils.getIfNull(map, LinkedHashMap::new), getKey(entry), getValue(entry));
			//
		} // for
			//
		return map;
		//
	}

	private static <K, V> void put(final Map<K, V> instance, final K key, final V value) {
		if (instance != null) {
			instance.put(key, value);
		}
	}

	private static <K> K getKey(final Entry<K, ?> instance) {
		return instance != null ? instance.getKey() : null;
	}

	private static <V> V getValue(final Entry<?, V> instance) {
		return instance != null ? instance.getValue() : null;
	}

	private static int length(final Object[] instance) {
		return instance != null ? instance.length : 0;
	}

	private static Entry<String, String> toEntry(final String string) {
		//
		final Field field = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(string), FieldUtils::getAllFieldsList, null)),
						f -> Objects.equals(getName(f), "value")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		if (string != null && field != null && Narcissus.getField(string, field) == null) {
			//
			return null;
			//
		} // if
			//
		if (Objects.equals(string, "=")) {
			//
			return Pair.of("", "");
			//
		} else if (string != null && string.length() == 2 && string.charAt(0) == '=') {
			//
			return Pair.of("", string.substring(1, string.length()));
			//
		} else if (string != null && string.length() == 2 && string.charAt(string.length() - 1) == '=') {
			//
			return Pair.of(string.substring(0, string.length() - 1), "");
			//
		} else if (string != null && string.indexOf('=') >= 0 && string.indexOf('=') == string.lastIndexOf('=')) {
			//
			return Pair.of(StringUtils.substringBefore(string, '='), StringUtils.substringAfter(string, '='));
			//
		} else if (string != null && string.length() > 2 && string.indexOf('=') != string.lastIndexOf('=')) {
			//
			return Pair.of(StringUtils.substring(string, 0, string.indexOf('=')),
					StringUtils.substring(string, string.indexOf('=') + 1));
			//
		} // if
			//
		return null;
		//
	}

	private static <E> E get(final List<E> instance, final int index) {
		return instance != null ? instance.get(index) : null;
	}

	private static int size(final Collection<?> instance) {
		return instance != null ? instance.size() : 0;
	}

	private static String getName(final Member instance) {
		return instance != null ? instance.getName() : null;
	}

	private static <T, R, A> R collect(final Stream<T> instance, final Collector<? super T, A, R> collector) {
		return instance != null && (collector != null || Proxy.isProxyClass(getClass(instance)))
				? instance.collect(collector)
				: null;
	}

	private static <T> Stream<T> filter(final Stream<T> instance, final Predicate<? super T> predicate) {
		return instance != null ? instance.filter(predicate) : instance;
	}

	private static <T> Stream<T> stream(final Collection<T> instance) {
		return instance != null ? instance.stream() : null;
	}

	private static Class<?> getClass(final Object instance) {
		return instance != null ? instance.getClass() : null;
	}

	private static boolean endsWith(final String instance, final String prefix) {
		//
		if (instance == null) {
			//
			return false;
			//
		} // if
			//
		final Field field = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(instance), FieldUtils::getAllFieldsList, null)),
						x -> Objects.equals(getName(x), "value")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		if (field != null && (Narcissus.getField(instance, field) == null
				|| (prefix != null && Narcissus.getField(prefix, field) == null))) {
			//
			return false;
			//
		} // if
			//
		return instance.endsWith(prefix);
		//
	}

	private static boolean matches(final String instance, final String regex) {
		//
		if (instance == null) {
			//
			return false;
			//
		} // if
			//
		final Field field = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(instance), FieldUtils::getAllFieldsList, null)),
						x -> Objects.equals(getName(x), "value")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		if (field != null && (Narcissus.getField(instance, field) == null
				|| (regex != null && Narcissus.getField(regex, field) == null))) {
			//
			return false;
			//
		} // if
			//
		return instance.matches(regex);
		//
	}

	private static String getName(final Module instance) {
		return instance != null ? instance.getName() : null;
	}

	private static Module getModule(final Class<?> instance) {
		return instance != null ? instance.getModule() : null;
	}

	private static Class<?> forName(final String instance) {
		try {
			return instance != null ? Class.forName(instance) : null;
		} catch (final Throwable e) {
			return null;
		}
	}

	private static <E> void add(final Collection<E> instance, final E item) {
		if (instance != null) {
			instance.add(item);
		}
	}

	private static boolean contains(final Collection<?> instance, final Object item) {
		return instance != null && instance.contains(item);
	}

	private static <T, R, E extends Throwable> R testAndApply(final Predicate<T> predicate, final T value,
			final FailableFunction<T, R, E> functionTrue, final FailableFunction<T, R, E> functionFalse) throws E {
		return test(predicate, value) ? apply(functionTrue, value) : apply(functionFalse, value);
	}

	private static <T> boolean test(final Predicate<T> instance, final T value) {
		return instance != null && instance.test(value);
	}

	private static <T, R, E extends Throwable> R apply(final FailableFunction<T, R, E> instance, final T value)
			throws E {
		return instance != null ? instance.apply(value) : null;
	}

}