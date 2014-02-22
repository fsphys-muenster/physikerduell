package de.uni_muenster.physikerduell.csv;

import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class CSVReaderTest {

	@Test
	public void testGetItem() throws IOException {
		CSVReader r = new CSVReader("csv test.csv");
		assertEquals(r.getItem(0, 0), "Frage A");
		assertEquals(r.getItem(0, 1), "");
		assertEquals(r.getItem(1, 0), "Antwort 1");
		assertEquals(r.getItem(1, 1), "Punkte 1");
		assertEquals(r.getItem(9, 0), "");
		assertEquals(r.getItem(9, 1), "");
		assertEquals(r.getItem(14, 0), "Ant, wort,,");
		assertEquals(r.getItem(14, 1), ", ,p, \"un\"kte, \"s");
	}

}
