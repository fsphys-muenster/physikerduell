package de.uni_muenster.physikerduell.csv;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The CSVReader class takes CSV text file and attempts to map it to a table with n rows
 * and m columns. In each row, the column entries are separated by commas. An entry can be
 * quoted ("...") if it includes a comma, in which case the comma is not interpreted as
 * the column delimiter and the entry ends after another quote is encountered. In order
 * for a quote to be included in a quoted entry, it has to be inserted twice ("").
 * <p>
 * The text file has to be encoded in UTF-8.
 * 
 * @author Simon May
 * 
 */
public class CSVReader {

	private static final String REGEX_STRING = "(" + //
			"((\"(\"\"|[^\"])*\")" + // Cells in quotes (contain double quotes or other
										// characters)
			"|([^,]*))" + // Cells without quotes (don't contain commas) 
			"(,|$)" + ")"; // Cell ends with comma or end of line
	private static final Pattern REGEX = Pattern.compile(REGEX_STRING);
	private static final String REGEX_LINE = REGEX_STRING + "+";
	private final List<List<String>> table = new ArrayList<>();
	private int columns;

	/**
	 * Parses the given file (file path specified by a String) as a CSV file.
	 * 
	 * @param file
	 *            A String containing the CSV file's path in the filesystem
	 * @throws IOException
	 *             The file could not be read or is not a valid CSV file (e.g. the rows
	 *             contain an inconsistent number of columns)
	 */
	public CSVReader(String file) throws IOException {
		this(Files.newInputStream(Paths.get(file)));
	}

	/**
	 * Parses the characters of the given InputStream as a CSV file.
	 * 
	 * @param is
	 *            The input stream containing a CSV file
	 * @throws IOException
	 *             The file is not a valid CSV file (e.g. the rows contain an inconsistent
	 *             number of columns)
	 */
	public CSVReader(InputStream is) throws IOException {
		readFile(is);
	}

	/**
	 * Returns the number of rows of the table represented by the CSV file.
	 * 
	 * @return The number of rows
	 */
	public int getRowCount() {
		return table.size();
	}

	/**
	 * Returns the number of columns of the table represented by the CSV file.
	 * 
	 * @return The number of columns
	 */
	public int getColumnCount() {
		return table.size() == 0 ? 0 : table.get(0).size();
	}

	/**
	 * Returns the String in the cell of the table represented by the CSV file.
	 * 
	 * @param row
	 *            The row number of the CSV file (zero-based)
	 * @param column
	 *            The column number of the CSV file (zero-based)
	 * @return The String contained in the specified cell
	 */
	public String getItem(int row, int column) {
		return table.get(row).get(column);
	}

	/**
	 * Parses the characters from the input stream (UTF-8) into a table.
	 * 
	 * @param is
	 *            The input stream containing a CSV file
	 * @throws IOException
	 *             The file is not a valid CSV file (e.g. the rows contain an inconsistent
	 *             number of columns)
	 */
	private void readFile(InputStream is) throws IOException {
		Scanner s = new Scanner(is, "UTF-8");
		for (int lineNumber = 0; s.hasNextLine(); lineNumber++) {
			String line = s.nextLine();
			if (!line.matches(REGEX_LINE)) {
				s.close();
				throw new IOException("Invalid CSV file: Error in line " + lineNumber);
			}
			List<String> newRow = new ArrayList<>();
			table.add(newRow);
			Matcher m = REGEX.matcher(line);
			int columns = 0;
			while (m.find()) {
				if (!line.endsWith(",") && m.start() == line.length()) {
					break;
				}
				String newItem = m.group().replace("\"\"", "\"");
				if (!newItem.isEmpty() && newItem.charAt(newItem.length() - 1) == ',') {
					newItem = newItem.substring(0, newItem.length() - 1);
				}
				if (!newItem.isEmpty() && newItem.charAt(0) == '"') {
					newItem = newItem.substring(1, newItem.length() - 1);
				}
				newRow.add(newItem);
				columns++;
			}
			if (lineNumber == 0) {
				this.columns = columns;
			}
			else if (lineNumber > 0 && this.columns != columns) {
				s.close();
				throw new IOException(
					"Invalid CSV file: Inconsistent column count (line " + lineNumber
							+ ")");
			}
		}
		s.close();
	}
}
