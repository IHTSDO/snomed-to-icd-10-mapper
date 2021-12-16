package org.snomed.snomedtoicd10mapper.service;

import org.junit.jupiter.api.Test;
import org.snomed.snomedtoicd10mapper.domain.Encounter;
import org.snomed.snomedtoicd10mapper.service.MapRule;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.snomed.snomedtoicd10mapper.domain.Sex.FEMALE;

class MapRuleTest {

	@Test
	void test() {
		MapRule mapRule = new MapRule(1,"IFA 445518008 | Age at onset of clinical finding (observable entity) | >= 12.0 years AND " +
				"IFA 445518008 | Age at onset of clinical finding (observable entity) | < 19.0 years", "");
		assertFalse(mapRule.isMatch(new Encounter(123L, FEMALE, 10f)));
		assertTrue(mapRule.isMatch(new Encounter(123L, FEMALE, 12f)));
		assertTrue(mapRule.isMatch(new Encounter(123L, FEMALE, 13f)));
		assertTrue(mapRule.isMatch(new Encounter(123L, FEMALE, 15f)));
		assertTrue(mapRule.isMatch(new Encounter(123L, FEMALE, 18f)));
		assertFalse(mapRule.isMatch(new Encounter(123L, FEMALE, 19f)));
	}

}
