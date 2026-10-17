import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VendingMachineItemTest {
    VendingMachineItem item1;
    VendingMachineItem item2;
    @BeforeEach 
    public void setUp(){
        item1 = new VendingMachineItem("item1", 3.0);
        item2 = new VendingMachineItem("item2", 0.0);
    }
    @AfterEach 
    public void tearDown(){
        item1=null;
        item2=null;
    }

    @Test
    void testGetPricePositive() {
        double actualValue=item1.getPrice();
        assertEquals(3.0, actualValue,0.001);
    }
    @Test
    void testGetPriceZero() {
        double actualValue=item2.getPrice();
        assertEquals(0.0, actualValue,0.001);
    }
    @Test
    void testGetName() {
        String actualValue = item1.getName();
        assertEquals("item1", actualValue);
    }
    @Test 
    void testInvalidValue(){
        assertThrows(VendingMachineException.class, ()-> {
            VendingMachineItem item3 = new VendingMachineItem("item3", -3.0);
        });
    }

}
