package employees.swing.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import employees.swing.model.EmployeeModel;
import employees.swing.model.PairIDs;
import employees.swing.ui.EmployeesErrorMessage;
import employees.swing.ui.EmployeesResultTableView;

/**
 * Class to read the input file and analyze it
 */
public class EmployeesAnalyseService {

	private static final Logger logger = Logger.getLogger(EmployeesAnalyseService.class);

	private static final Pattern STRING_DATE_ISO_8601_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}");
	private static final Pattern STRING_DATE_COMMON_FORMAT_PATTERN = Pattern.compile("^\\d{2}-\\d{2}-\\d{4}");
	private static final Pattern STRING_DATE_DASH_FORMAT_PATTERN = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}");

	private static final String DATE_ISO_8601_PATTERN = "yyyy-MM-dd";
	private static final String DATE_COMMON_FORMAT_PATTERN = "dd-MM-yyyy";
	private static final String DASH_FORMAT_PATTERN = "dd/MM/yyyy";
	private static final String FILE_CSV_EXTENSION = ".csv";
	private static final String ERROR_MESSAGE_WRONG_FILE_FORMAT = "File should be with .csv format!";
	private static final String ERROR_MESSAGE_EMPTY_FILE = "File is empty no data found!";

	/**
	 * That is the map with all employee pairs worked together on different projects
	 * and the time in days worked together on a project
	 */
	private static Map<PairIDs<Integer, Integer>, Map<Integer, Long>> employeesProjectsMap = new HashMap<PairIDs<Integer, Integer>, Map<Integer, Long>>();

	/**
	 * Analyzes the input file and produces output result. It finds the pair of
	 * employees who have worked together on common projects for the longest period
	 * of time in days
	 * 
	 * @param fileName - the input file name
	 */
	public static void analyseFile(String fileName) {
		logger.info("Executing analyseFile for file " + fileName);
		// Read the data from file
		logger.info("Reading file");
		List<EmployeeModel> employeeModelList = readFile(fileName);

		// Analyze data
		logger.info("Analizing data");
		analizeData(employeeModelList);

		// Find the pair of employees who have worked
		// together on common projects for the longest period of time in days
		logger.info(
				"Finding the pair of employees who have worked together on common projects for the longest period of time in days");
		PairIDs<Integer, Integer> pairEmployeesWorkedMaxProjectDays = findPairEmployeesWorkedMaxProjectDays();

		// Generate result matrix for show in final table view
		logger.info("Generate result matrix");
		Integer[][] data = generateResultMatrix(pairEmployeesWorkedMaxProjectDays);

		// Show the data to user
		logger.info("Ready showing the data to user");
		EmployeesResultTableView.showResultTable(fileName, data);
	}

	/**
	 * Generate result matrix for show in final table view. It creates matrix from
	 * integers with columns Employee ID #1, Employee ID #2, Project ID, Days worked
	 * 
	 * @param pairEmployeesWorkedMaxProjectDays - the found pair employees who have
	 *                                          worked am longest together
	 * @return matrix from integers with columns Employee ID #1, Employee ID #2,
	 *         Project ID, Days worked
	 */
	private static Integer[][] generateResultMatrix(PairIDs<Integer, Integer> pairEmployeesWorkedMaxProjectDays) {
		logger.info("Executing generateResultMatrix");
		Map<Integer, Long> mapProjectWorkedDays = employeesProjectsMap.get(pairEmployeesWorkedMaxProjectDays);
		Integer[][] resultMatrix = new Integer[mapProjectWorkedDays.keySet().size()][4];
		int index = 0;
		for (Integer projectId : mapProjectWorkedDays.keySet()) {
			resultMatrix[index][0] = pairEmployeesWorkedMaxProjectDays.key();
			resultMatrix[index][1] = pairEmployeesWorkedMaxProjectDays.value();
			resultMatrix[index][2] = projectId;
			resultMatrix[index][3] = mapProjectWorkedDays.get(projectId).intValue();
			index++;
		}
		return resultMatrix;
	}

	/**
	 * Finding the pair of employees who have worked together on common projects for
	 * the longest period of time in days
	 * 
	 * @return the pair employees found
	 */
	private static PairIDs<Integer, Integer> findPairEmployeesWorkedMaxProjectDays() {
		logger.info("Executing findPairEmployeesWorkedMaxProjectDays");
		PairIDs<Integer, Integer> resultPair = null;
		long maxDays = 0;

		for (PairIDs<Integer, Integer> pair : employeesProjectsMap.keySet()) {
			Map<Integer, Long> mapProjectWorkingDays = employeesProjectsMap.get(pair);
			Long sum = 0L;
			for (Long days : mapProjectWorkingDays.values()) {
				sum = sum + days;
			}
			if (sum > maxDays) {
				maxDays = sum;
				resultPair = pair;
			}
		}

		return resultPair;
	}

	/**
	 * Reading file
	 * 
	 * @param fileName - the input csv file name
	 * @return list with all read data as EmployeeModel list
	 */
	private static List<EmployeeModel> readFile(String fileName) {
		logger.info("Executing readFile");
		File file = new File(fileName);

		if (!file.getName().endsWith(FILE_CSV_EXTENSION)) {
			EmployeesErrorMessage.showErrorMessage(ERROR_MESSAGE_WRONG_FILE_FORMAT);
		}

		List<EmployeeModel> employeeModelList = new ArrayList<>();

		// Read the lines from the text file
		String line;
		try {
			// Create a FileReader object for the File object
			FileReader fileReader = new FileReader(file);

			// Create a BufferedReader object for the FileReader object
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				String[] split = line.split(",");

				// If there are not 4 columns in file ignore line
				if (split.length != 4) {
					logger.warn("There are not 4 columns in file line: " + line + " it will be ignored");
					continue;
				}

				if (split[0] == null || !isInteger(split[0].trim())) {
					logger.warn("The first column is not integer value in file line: " + line + " it will be ignored");
					continue;
				}
				int employeeId = Integer.parseInt(split[0].trim());
				if (split[1] == null || !isInteger(split[1].trim())) {
					logger.warn("The second column is not integer value in file line: " + line + " it will be ignored");
					continue;
				}
				int projectId = Integer.parseInt(split[1].trim());
				LocalDate dateFrom = getDate(split[2].trim());
				LocalDate dateTo = getDate(split[3].trim());
				EmployeeModel employeeModel = new EmployeeModel(employeeId, projectId, dateFrom, dateTo);
				employeeModelList.add(employeeModel);
			}
			// Close the BufferedReader object
			bufferedReader.close();
		} catch (Exception e) {
			logger.error(e);
			EmployeesErrorMessage.showErrorMessage(e.getMessage());
		}

		// If in the model list are not present at least 2 entries stopping analysis
		if (employeeModelList.size() < 2) {
			EmployeesErrorMessage.showErrorMessage(ERROR_MESSAGE_EMPTY_FILE);
		}

		return employeeModelList;
	}

	/**
	 * Check is a string integer number
	 * 
	 * @param str - input string
	 * @return true if it is integer or false if not integer
	 */
	private static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Analyzes the data and fills employeesProjectsMap needed for further analysis
	 * 
	 * @param employeeModelList - input read data
	 */
	private static void analizeData(List<EmployeeModel> employeeModelList) {
		Set<Integer> projectIdsSet = new HashSet<>();

		for (EmployeeModel modelElement : employeeModelList) {
			projectIdsSet.add(modelElement.getProjectId());
		}

		for (Integer projectId : projectIdsSet) {
			// Set with the ids of all worked on the project employees
			Set<Integer> projectEmployeesIdsSet = new HashSet<>();
			for (EmployeeModel readEmployeeModel : employeeModelList) {
				if (readEmployeeModel.getProjectId().equals(projectId)) {
					projectEmployeesIdsSet.add(readEmployeeModel.getEmployeeId());
				}
			}

			for (Integer employeeId : projectEmployeesIdsSet) {
				// Select all read models for one employee for the project with id projectId
				List<EmployeeModel> readEmployeeModelsPerEmployeeForOneProjectList = new ArrayList<>();
				for (EmployeeModel readEmployeeModel : employeeModelList) {
					if (readEmployeeModel.getProjectId().equals(projectId)
							&& employeeId.equals(readEmployeeModel.getEmployeeId())) {
						readEmployeeModelsPerEmployeeForOneProjectList.add(readEmployeeModel);
					}
				}

				for (EmployeeModel readEmployeeModelForOneProject : readEmployeeModelsPerEmployeeForOneProjectList) {
					for (EmployeeModel readModel : employeeModelList) {
						
						// Loop on all read models for a models for the same project
						if (!employeeId.equals(readModel.getEmployeeId())
								&& projectId.intValue() == readModel.getProjectId().intValue()
								&& readModel.getEmployeeId().intValue() < readEmployeeModelForOneProject.getEmployeeId()
										.intValue()) {
							
							PairIDs<Integer, Integer> pair = PairIDs.of(readModel.getEmployeeId(),
									readEmployeeModelForOneProject.getEmployeeId());
							Map<Integer, Long> mapProjectWorkedDays = employeesProjectsMap.get(pair);
							long intersectionInDays = determinePeriodsIntersectionInNumberDays(readModel.getDateFrom(),
									readModel.getDateTo(), readEmployeeModelForOneProject.getDateFrom(),
									readEmployeeModelForOneProject.getDateTo());
							
							if (mapProjectWorkedDays == null) {
								if (intersectionInDays > 0) {
									//not found in map new entry created
									mapProjectWorkedDays = new HashMap<>();
									mapProjectWorkedDays.put(projectId, intersectionInDays);
									employeesProjectsMap.put(pair, mapProjectWorkedDays);
								}
							} else {
								if (intersectionInDays > 0) {
									//found in map and intersection positive
									Long sumWorkedDays = mapProjectWorkedDays.get(projectId);
									if (sumWorkedDays != null) {
										//already present will be a sum created
										sumWorkedDays = sumWorkedDays + intersectionInDays;
										mapProjectWorkedDays.put(projectId, sumWorkedDays);
									} else {
										//not present will be added
										mapProjectWorkedDays.put(projectId, intersectionInDays);
									}
									employeesProjectsMap.put(pair, mapProjectWorkedDays);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Method determines the intersection of two periods in days. Period1 and
	 * Period2.
	 * 
	 * @param startPeriod1Date - Period1 starting date
	 * @param endPeriod1Date   - Period1 end date
	 * @param startPeriod2Date - Period2 starting date
	 * @param endPeriod2Date   - Period2 end date
	 * 
	 * @return the intersection of two periods in days
	 */
	private static long determinePeriodsIntersectionInNumberDays(LocalDate startPeriod1Date, LocalDate endPeriod1Date,
			LocalDate startPeriod2Date, LocalDate endPeriod2Date) {

		// Find the intersection starting and end date of the two periods
		LocalDate intersectionDate = startPeriod1Date.compareTo(startPeriod2Date) <= 0 ? startPeriod2Date
				: startPeriod1Date;
		LocalDate intersectionEndDate = endPeriod1Date.compareTo(endPeriod2Date) >= 0 ? endPeriod2Date : endPeriod1Date;

		// determine the number of days in the intersection
		long intersectionInDays = ChronoUnit.DAYS.between(intersectionDate, intersectionEndDate);
		if (intersectionInDays < 0) {
			return 0;
		} else
			return intersectionInDays + 1;
	}

	/**
	 * The method checks the format of the date string and parses it in the known
	 * formats if format is unknown the message is shown and the application
	 * terminates
	 * 
	 * @param dateString - the string to parse to date
	 * @return the date parsed
	 */
	private static LocalDate getDate(String dateString) {
		DateTimeFormatter formatter = null;

		if (STRING_DATE_ISO_8601_PATTERN.matcher(dateString).find()) {
			formatter = DateTimeFormatter.ofPattern(DATE_ISO_8601_PATTERN);
		} else if (STRING_DATE_COMMON_FORMAT_PATTERN.matcher(dateString).find()) {
			formatter = DateTimeFormatter.ofPattern(DATE_COMMON_FORMAT_PATTERN);
		} else if (STRING_DATE_DASH_FORMAT_PATTERN.matcher(dateString).find()) {
			formatter = DateTimeFormatter.ofPattern(DASH_FORMAT_PATTERN);
		} else if (dateString.compareTo("NULL") == 0) {
			// if string is e NULL it is equivalent to today
			return LocalDate.now();
		} else {
			// unknown date format error
			EmployeesErrorMessage.showErrorMessage("Unknown Date Format in file: " + dateString);
		}

		return LocalDate.parse(dateString, formatter);
	}
}
