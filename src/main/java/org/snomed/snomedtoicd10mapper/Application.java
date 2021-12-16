package org.snomed.snomedtoicd10mapper;

import org.snomed.snomedtoicd10mapper.service.MapperService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.List;

import static org.snomed.snomedtoicd10mapper.util.MainMethodUtils.*;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private static final String ARG_HELP = "-help";
	private static final String ARG_MAP_FILE = "-rf2-map";
	private static final String ARG_ENCOUNTERS_FILE = "-encounters";
	private static final String ARG_OUTPUT_FILE = "-output";

	@Override
	public void run(String... argsArray) {
		List<String> args = List.of(argsArray);
		System.out.println();

		if (args.isEmpty() || args.contains(ARG_HELP)) {
			// Help
			printHelp();
			System.exit(0);
		}

		try {
			final File rf2MapFile = getFile(getRequiredParameterValue(ARG_MAP_FILE, args));
			final File encountersFile = getFile(getRequiredParameterValue(ARG_ENCOUNTERS_FILE, args));
			final File outputFile = new File(getRequiredParameterValue(ARG_OUTPUT_FILE, args));

			MapperService mapperService = new MapperService(rf2MapFile);
			mapperService.mapEncounters(encountersFile, outputFile);
		} catch (Exception e) {
//			e.printStackTrace();
			System.err.println(e.getMessage());
			System.out.println();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	private void printHelp() {
		System.out.println(
				"Usage:\n" +
						pad(ARG_HELP) +
						"Print this help message.\n" +
						"\n" +

						pad(ARG_MAP_FILE) +
						"SNOMED-CT to ICD-10 map RF2 snapshot file.\n" +
						"\n" +

						pad(ARG_ENCOUNTERS_FILE) +
						"Input file containing encounters to be mapped.\n" +
						pad("") + "This must be a tab separated file with header '" + MapperService.BATCH_FILE_HEADER + "'.\n" +
						"\n" +

						pad(ARG_OUTPUT_FILE) +
						"Output file to write results to.\n" +
						pad("") + "This will be a copy of the input file with an addition column for the ICD-10 code.\n" +
						"\n" +

						"");
	}
}
