/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sortdc.sortdc;

import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author skreo
 */
public class TokenizationTest {

    String testText = "Hello world!\nGrand-mère fait des confitures avec application.";

    public TokenizationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testExtractOnlyWords() {
        Tokenization instance = new Tokenization();
        instance.setExtractWords(true);
        instance.setApplyStemming(false);
        instance.setExtractBigrams(false);
        instance.setExtractTrigrams(false);
        instance.setWordsMinLength(0);
        List<String> test = instance.extract(this.testText);
        List<String> expected = Arrays.asList("hello", "world", "grand-mere", "fait", "des", "confitures", "avec", "application");
        assertEquals(expected.size(), test.size());
        for (int i = 0; i < expected.size(); i++) {
            if (i < test.size()) {
                assertEquals(expected.get(i), test.get(i));
            }
        }
    }
}
