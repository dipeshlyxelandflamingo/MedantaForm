package Medanta_Form;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class Testabcd {

    @BeforeClass
    public void setup() {
    	
        System.out.println("Setup method executed");
    }

    @Test
    public void testMethod() {
        System.out.println("Test method executed");
    }

    @AfterClass
    public void tearDown() {
        System.out.println("TearDown method executed");
    }
}