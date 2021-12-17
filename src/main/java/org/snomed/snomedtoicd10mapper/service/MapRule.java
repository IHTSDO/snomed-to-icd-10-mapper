package org.snomed.snomedtoicd10mapper.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.snomedtoicd10mapper.domain.Encounter;
import org.snomed.snomedtoicd10mapper.domain.Sex;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapRule implements Comparable<MapRule> {

	public static final Pattern IF_AGE_PREFIX = Pattern.compile("IFA 445518008 ([<>=]+) ([0-9.]+) years");

	public final Integer mapPriority;
	private final List<Function<Encounter, Boolean>> ruleParts;
	private final String mapTarget;

	public static final Logger logger = LoggerFactory.getLogger(MapRule.class);

	public MapRule(Integer mapPriority, String mapRule, String mapTarget) {
		this.mapPriority = mapPriority;
		this.mapTarget = mapTarget;
		ruleParts = new ArrayList<>();

		// Strip terms, to avoid translation issues
		mapRule = mapRule.replaceAll(" ?\\|[^|]+\\|", "");

		String[] parts = mapRule.split(" AND ");
		for (String part : parts) {
			if (mapRule.equals("TRUE") || mapRule.equals("OTHERWISE TRUE")) {
				ruleParts.add(encounter -> true);
			} else if (part.equals("IFA 248152002")) {// 248152002 |Female (finding)|
				ruleParts.add(encounter -> encounter.getSex() == Sex.FEMALE);
			} else if (part.equals("IFA 248153007")) {// 248153007 |Male (finding)|
				ruleParts.add(encounter -> encounter.getSex() == Sex.MALE);
			} else {
				Matcher matcher = IF_AGE_PREFIX.matcher(part);
				if (matcher.matches()) {
					String operatorString = matcher.group(1);
					Float age = Float.parseFloat(matcher.group(2));
					Operator operator;
					switch (operatorString) {
						case "<":
							operator = Operator.LESS_THAN;
							break;
						case "<=":
							operator = Operator.LESS_THAN_OR_EQUALS;
							break;
						case "=":
							operator = Operator.EQUALS;
							break;
						case ">":
							operator = Operator.GREATER_THAN;
							break;
						case ">=":
							operator = Operator.GREATER_THAN_OR_EQUAL;
							break;
						default:
							operator = null;
							logger.error("Failed to parse map rule, unknown operator '{}': {}", operatorString, mapRule);
							break;
					}
					ruleParts.add(encounter -> {
						if (operator != null) {
							return operator.compare(encounter.getAge(), age);
						}
						return false;
					});
				} else {
					logger.error("Failed to parse map rule: {}", mapRule);
					break;
				}
			}
		}
	}

	public boolean isMatch(Encounter encounter) {
		for (Function<Encounter, Boolean> rulePart : ruleParts) {
			if (!rulePart.apply(encounter)) {
				return false;
			}
		}
		return true;
	}

	public String getMapTarget() {
		return mapTarget;
	}

	@Override
	public int compareTo(MapRule other) {
		return mapPriority.compareTo(other.mapPriority);
	}
}
