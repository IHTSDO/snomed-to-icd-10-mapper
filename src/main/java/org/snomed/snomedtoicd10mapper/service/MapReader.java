package org.snomed.snomedtoicd10mapper.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MapReader {

	public static final String ICD_10_REFSET = "447562003";
	public static final String ICD_10_MAP_HEADER = "id\teffectiveTime\tactive\tmoduleId\trefsetId\treferencedComponentId\tmapGroup\tmapPriority\tmapRule" +
			"\tmapAdvice\tmapTarget\tcorrelationId\tmapCategoryId";

	public Map<Long, List<Set<MapRule>>> read(File rf2MapFile) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(rf2MapFile))) {
			String header = reader.readLine();
			if (!header.equals(ICD_10_MAP_HEADER)) {
				throw new IOException("Incorrect header in map file. Expected: " + ICD_10_MAP_HEADER);
			}

			// ConceptId to list of map groups. Each map group contains an ordered set of rules and will map to an ICD code.
			Map<Long, List<Set<MapRule>>> snomedToICDMap = new HashMap<>();

			String line;
			while ((line = reader.readLine()) != null) {
				// id	effectiveTime	active	moduleId	refsetId	referencedComponentId	mapGroup	mapPriority	mapRule	mapAdvice	mapTarget	correlationId	mapCategoryId
				// 0	1				2		3			4			5						6			7			8		9			10			11				12
				String[] cols = line.split("\t");
				if (cols[2].equals("1") && cols[4].equals(ICD_10_REFSET)) {
					// active row of "447562003 |ICD-10 complex map reference set (foundation metadata concept)|"
					String snomedConcept = cols[5];
					int mapGroup = Integer.parseInt(cols[6]);
					Integer mapPriority = Integer.parseInt(cols[7]);
					String mapRule = cols[8];
					String mapTarget = cols[10];
					List<Set<MapRule>> groups = snomedToICDMap.computeIfAbsent(Long.parseLong(snomedConcept), id -> new ArrayList<>());
					while (groups.size() < mapGroup) {
						groups.add(new TreeSet<>());
					}
					groups.get(mapGroup - 1).add(new MapRule(mapPriority, mapRule, mapTarget));
				}
			}
			return snomedToICDMap;
		}
	}

}
