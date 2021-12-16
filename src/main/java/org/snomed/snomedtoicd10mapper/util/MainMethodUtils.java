package org.snomed.snomedtoicd10mapper.util;

import java.io.File;
import java.util.List;

public class MainMethodUtils {

	private static Runnable printHelp = () -> {};

	public static boolean isFlag(String paramName, List<String> args) {
		return args.contains(paramName);
	}

	public static String getParameterValue(String paramName, List<String> args, String defaultValue) {
		String value = getParameterValue(paramName, args);
		return value != null ? value : defaultValue;
	}

	public static String getParameterValue(String paramName, List<String> args) {
		if (args.contains(paramName)) {
			int valueIndex = args.indexOf(paramName) + 1;
			assertTrue("Expecting a value with parameter " + paramName, valueIndex < args.size());
			return args.get(valueIndex);
		}
		return null;
	}

	public static String getRequiredParameterValue(String paramName, List<String> args) {
		assertTrue("Expecting parameter " + paramName, args.contains(paramName));
		return getParameterValue(paramName, args);
	}

	public static File getFile(String filename) {
		if (filename == null) {
			return null;
		}
		final File file = new File(filename);
		assertTrue(file.getAbsolutePath() + " should be a file.", file.isFile());
		return file;
	}

	public static void assertTrue(String message, boolean bool) {
		if (!bool) {
			System.out.println();
			System.err.println(message);
			System.out.println();
			printHelp.run();
			throw new IllegalArgumentException();
		}
	}

	public static String pad(String argHelp) {
		StringBuilder argHelpBuilder = new StringBuilder(" " + argHelp);
		while (argHelpBuilder.length() < 20) {
			argHelpBuilder.append(" ");
		}
		return argHelpBuilder.toString();
	}

	public static void setPrintHelp(Runnable printHelp) {
		MainMethodUtils.printHelp = printHelp;
	}
}
