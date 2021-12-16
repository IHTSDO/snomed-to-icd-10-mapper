package org.snomed.snomedtoicd10mapper.service;

import org.snomed.snomedtoicd10mapper.util.Pair;

import java.util.function.Function;

public enum Operator {

	LESS_THAN((pair) -> pair.getFirst() < pair.getSecond()),
	LESS_THAN_OR_EQUALS((pair) -> pair.getFirst() <= pair.getSecond()),
	EQUALS((pair) -> pair.getFirst().floatValue() == pair.getSecond().floatValue()),
	GREATER_THAN((pair) -> pair.getFirst() > pair.getSecond()),
	GREATER_THAN_OR_EQUAL((pair) -> pair.getFirst() >= pair.getSecond());

	private final Function<Pair<Float, Float>, Boolean> function;

	Operator(Function<Pair<Float, Float>, Boolean> function) {
		this.function = function;
	}

	public Boolean compare(Float x, Float y) {
		return function.apply(new Pair<>(x, y));
	}

}
