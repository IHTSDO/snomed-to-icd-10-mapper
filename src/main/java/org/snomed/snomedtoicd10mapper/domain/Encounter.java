package org.snomed.snomedtoicd10mapper.domain;

public class Encounter {

	private final Long conceptId;
	private final Sex sex;
	private final float age;

	public Encounter(Long conceptId, Sex sex, float age) {
		this.conceptId = conceptId;
		this.sex = sex;
		this.age = age;
	}

	public Long getConceptId() {
		return conceptId;
	}

	public Sex getSex() {
		return sex;
	}

	public float getAge() {
		return age;
	}
}
