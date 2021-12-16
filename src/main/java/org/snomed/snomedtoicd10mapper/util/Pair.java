package org.snomed.snomedtoicd10mapper.util;

public class Pair<T, F> {

	private final T first;
	private final F second;

	public Pair(T first, F second) {
		this.first = first;
		this.second = second;
	}

	public T getFirst() {
		return first;
	}

	public F getSecond() {
		return second;
	}
}
