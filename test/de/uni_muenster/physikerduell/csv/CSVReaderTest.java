package de.uni_muenster.physikerduell.csv;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CSVReaderTest {
	
	@Rule
	public ExpectedException except = ExpectedException.none();

	@Test
	public void testGetItem() throws IOException {
		CSVReader r = new CSVReader("test/csv_test.csv");
		assertEquals(r.getItem(0, 0), "Frage A");
		assertEquals(r.getItem(0, 1), "");
		assertEquals(r.getItem(1, 0), "Antwort 1");
		assertEquals(r.getItem(1, 1), "Punkte 1");
		assertEquals(r.getItem(9, 0), "");
		assertEquals(r.getItem(9, 1), "");
		assertEquals(r.getItem(14, 0), "Ant, wort,,");
		assertEquals(r.getItem(14, 1), ", ,p, \"un\"kte, \"s");
		testGetItemException(r, 0, 2);
		testGetItemException(r, 0, 15);
	}
	
	private void testGetItemException(CSVReader r, int row, int column) {
		except.expect(IndexOutOfBoundsException.class);
		r.getItem(row, column);
	}

}
