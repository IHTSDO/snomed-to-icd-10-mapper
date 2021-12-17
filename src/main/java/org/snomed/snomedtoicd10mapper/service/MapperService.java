package org.snomed.snomedtoicd10mapper.service;

import org.snomed.snomedtoicd10mapper.domain.Encounter;
import org.snomed.snomedtoicd10mapper.domain.Sex;

import java.io.*;
import java.util.*;

public class MapperService {

	public static final String BATCH_FILE_HEADER = "encounterId\tconceptId\tsex\tageAtEncounterOnset";
	public static final String OUTPUT_FILE_HEADER = "encounterId\tconceptId\tsex\tageAtEncounterOnset\tICDCodes";

	private final Map<Long, List<Set<MapRule>>> snomedToICDMap;

	public MapperService(File rf2MapFilePath) throws IOException {
		System.out.println("Reading ICD-10 map file");
		snomedToICDMap = new MapReader().read(rf2MapFilePath);
		System.out.printf("%s SNOMED-CT concepts mapped to ICD-10 codes.%n", snomedToICDMap.size());
		System.out.println();
	}

	public void mapEncounters(File encountersFile, File outputFile) throws IOException {
		FileWriter out;
		try {
			out = new FileWriter(outputFile);
		} catch (IOException e) {
			throw new IOException("Failed to open output file for writing.", e);
		}

		Long encounters = 0L;
		try (BufferedReader reader = new BufferedReader(new FileReader(encountersFile));
			 BufferedWriter writer = new BufferedWriter(out)) {

			writer.write(OUTPUT_FILE_HEADER);
			writer.newLine();

			String header = reader.readLine();
			if (!BATCH_FILE_HEADER.equals(header)) {
				throw new IOException("Incorrect header in encounter file. Expected: " + BATCH_FILE_HEADER);
			}
			String line;
			long lineNum = 1;
			try {
				while ((line = reader.readLine()) != null) {
					lineNum++;
					String[] parts = line.split("\t");
					if (parts.length != 4) {
						throw new IOException(String.format("Line %s of encounter file has the wrong number tab separated columns, expected 4, found %s.", lineNum, parts.length));
					}
					Long conceptId = Long.parseLong(parts[1]);
					Sex sex = getSex(parts[2], lineNum);
					float age = Float.parseFloat(parts[3]);
					Set<String> icdCodes = mapSnomedCode(new Encounter(conceptId, sex, age));
					if (!icdCodes.isEmpty()) {
						encounters++;
					}
					writer.write(line);
					writer.write("\t");
					writer.write(String.join(",", icdCodes));
					writer.newLine();
				}
				System.out.printf("%s SNOMED-CT encounters mapped to ICD codes.%n", encounters);
			} catch (NumberFormatException e) {
				throw new IOException(String.format("Failed to parse number on line %s of encounter file.", lineNum));
			}
		}
	}

	private Sex getSex(String part, long lineNum) {
		String sexString = part.toUpperCase();
		if (sexString.equals(Sex.MALE.toString()) || sexString.equals(Sex.FEMALE.toString())) {
			return Sex.valueOf(sexString);
		}
		throw new IllegalArgumentException(String.format("Failed to parse sex on line %s, valid values are %s.", lineNum, Arrays.toString(Sex.values())));
	}

	public Set<String> mapSnomedCode(Encounter encounter) {
		List<Set<MapRule>> ruleGroups = snomedToICDMap.get(encounter.getConceptId());
		Set<String> icdCodes = new HashSet<>();
		if (ruleGroups != null) {
			for (Set<MapRule> ruleGroup : ruleGroups) {
				// ruleGroup is naturally sorted by mapPriority
				for (MapRule mapRule : ruleGroup) {
					if (mapRule.isMatch(encounter)) {
						icdCodes.add(mapRule.getMapTarget());
						break;// Only apply the first map rule that matches within each group
					}
				}
			}
		}
		return icdCodes;
	}
}
