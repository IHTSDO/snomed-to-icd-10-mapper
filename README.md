# SNOMED-CT to ICD-10 Code Mapping Demonstrator
This tool demonstrates how to use the published SNOMED-CT to ICD-10 map.

A batch of clinical encounters with a SNOMED-CT code, patient sex and age at disorder onset can be mapped to one or more ICD-10 codes using the rules included in the map.

Map rules like this are parsed from the map file and applied to the input:
```
IFA 445518008 | Age at onset of clinical finding (observable entity) | >= 12.0 years 
  AND IFA 445518008 | Age at onset of clinical finding (observable entity) | < 19.0 years
---
IFA 248152002 | Female (finding) |
```

Some encounters will be mapped to multiple ICD-10 codes.

## Running on the command line
The application can be run from the command line using the jar file available on the [latest release page](https://github.com/IHTSDO/snomed-to-icd-10-mapper/releases).

### Command Line Options:
```
Usage:
 -help              Print this help message.

 -rf2-map           SNOMED-CT to ICD-10 map RF2 snapshot file.

 -encounters        Input file containing encounters to be mapped.
                    This must be a tab separated file with header 'conceptId	sex	ageAtEncounterOnset'.

 -output            Output file to write results to.
                    This will be a copy of the input file with an addition column for the ICD-10 code.
```
### Example Command:
```
java -jar snomed-to-icd10-mapper*.jar -rf2-map ../snomed-files/SnomedCT_InternationalRF2_PRODUCTION_20210731T120000Z/Snapshot/Refset/Map
/der2_iisssccRefset_ExtendedMapSnapshot_INT_20210731.txt -encounters example-encounters.txt -output output.txt
```
### Encounter File Format
The encounter input file is a tab separated file, with a header line, containing the columns conceptId, sex and ageAtEncounterOnset.

An example encounter input file:
```
conceptId	sex	ageAtEncounterOnset
53597009	FEMALE	30
473380002	MALE	35
785744001	FEMALE	10
785744001	FEMALE	15
785744001	FEMALE	20
```

### Output File Format
The output file created will be very similar to the encounter input file. An extra column will be added for the ICD codes.

For every encounter in the input file there will be a matching line in the output file but with the matching ICD codes in the last column.
Most encounters will map to a single ICD code but some SNOMED-CT concepts map to multiple ICD codes. In these cases the ICD codes will be a comma separated list in the last 
column.

An example output file showing the ICD codes:
```
conceptId	sex	ageAtEncounterOnset	ICDCodes
53597009	FEMALE	30	L56.2
473380002	MALE	35	Q23.8,I05.8
785744001	FEMALE	10	J20.9
785744001	FEMALE	15	J40
785744001	FEMALE	20	J40
```
