import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class VendingMachineTest {
    VendingMachine vendingMachine1;
    VendingMachineItem item1;
    VendingMachineItem item2;
    VendingMachineItem item3;
    @BeforeEach 
    public void setUp(){
        vendingMachine1 = new VendingMachine();
        item1 = new VendingMachineItem("item1",10.0);
        item2 = new VendingMachineItem("item2", 20.0);
        item3 = new VendingMachineItem("item3", 0.0);
    }
    @AfterEach 
    public void tearDown(){
        vendingMachine1=null;
        item1=null;
        item2=null;
    }
    @Test
    void testAddItem_addsItem_correctSlot() {
       vendingMachine1.addItem(item1,"A" );

        assertEquals(vendingMachine1.getItem("A"), item1);
    }
    @Test 
    void testAddItem_doesntAdd_incorrectSlot(){
        assertThrows(VendingMachineException.class, () -> {
            vendingMachine1.addItem(item1, "E");
        });
    }
    @Test 
    void testAddItem_doesntAdd_filledItem(){
        vendingMachine1.addItem(item1, "B");
        assertThrows(VendingMachineException.class, () -> {
            vendingMachine1.addItem(item2, "B");
        });
    }
    @Test
    void testInsertMoney_validAmount_increasesBalance() {
        vendingMachine1.insertMoney(20.0);
        double expectedValue=20.0;
        double actualValue = vendingMachine1.getBalance();
        assertEquals(expectedValue, actualValue,0.001);
    }
    @Test 
    void testInsertMoney_invalidAmount_throwError(){
        assertThrows(VendingMachineException.class, () -> {
            vendingMachine1.insertMoney(-3.0);
        });
    }
    @Test
    void testInsertMoney_zeroValue_noIncrease(){
        vendingMachine1.insertMoney(0.0);
        double expectedValue=0.0;
        double actualValue = vendingMachine1.getBalance();
        assertEquals(expectedValue, actualValue,0.001);
    }
    @ParameterizedTest 
    @CsvSource({
        "A,A, 20.0,10.0,true",
        "A,A, 10.0, 0.0, true",
        "A,A, 0.0, 0.0, false",
        "A,A, 5.0, 5.0, false",
        "C,C, 10.5, 0.5, true",
        "A,D, 20.0, 20.0, false"
    })
    void testMakePurchase(String a, String e, double b, double c, boolean d){
        vendingMachine1.insertMoney(b);
        vendingMachine1.addItem(item1, a);
        assertEquals(d, vendingMachine1.makePurchase(e));
        assertEquals(c,vendingMachine1.getBalance(),0.001);
    }
    @Test
    void testMakePurchase_enoughMoney_removeItem() {
        vendingMachine1.insertMoney(20.0);
        vendingMachine1.addItem(item1, "A");
        vendingMachine1.makePurchase("A");
        assertNull(vendingMachine1.getItem("A"));
    }
    @Test 
    void testMakePurchase_zeroPrice_purchasesItem(){
        vendingMachine1.addItem(item3, "A");
        assertTrue(vendingMachine1.makePurchase("A"));
    }

    @Test
    void testRemoveItem_validItem_returnsItem() {
        vendingMachine1.addItem(item1, "A");
        VendingMachineItem expectedValue=vendingMachine1.removeItem("A");
        assertEquals(item1, expectedValue);
    }
    @Test
    void testRemoveItem_validItem_removesItem() {
        vendingMachine1.addItem(item1, "A");
        vendingMachine1.removeItem("A");
        assertNull(vendingMachine1.getItem("A"));
    }
    @Test
    void testRemoveItem_invalidItem_throwsException() {
        assertThrows(VendingMachineException.class, () -> {
            vendingMachine1.removeItem( "E");
        });
    }
    @Test
    void testRemoveItem_invalidIndex_throwsException() {
        assertThrows(VendingMachineException.class, () -> {
            vendingMachine1.removeItem( "E");
        });
    }

    @Test
    void testReturnChange_validAmount_returnsBalance() {
        vendingMachine1.insertMoney(3.0);
        double actualValue= vendingMachine1.returnChange();
        assertEquals(3.0, actualValue,0.001);
    }
    @Test
    void testReturnChange_zeroValue_returnsBalance() {
        double actualValue= vendingMachine1.returnChange();
        assertEquals(0.0, actualValue,0.001);
    }

}
