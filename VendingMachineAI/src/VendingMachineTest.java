import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("VendingMachine")
class VendingMachineTest {

	private static final double DELTA = 1e-9;
	private static final String[] CODES = { "A", "B", "C", "D" };

	private VendingMachine machine;
	private VendingMachineItem chips;
	private VendingMachineItem soda;

	@BeforeEach
	void setUp() {
		machine = new VendingMachine();
		chips = new VendingMachineItem("Chips", 1.25);
		soda = new VendingMachineItem("Soda", 2.00);
	}

	// ------------------------------------------------------------------
	@Nested
	@DisplayName("construction")
	class Construction {

		@Test
		void constantsAreCorrect() {
			assertEquals(4, VendingMachine.NUM_SLOTS);
			assertEquals("A", VendingMachine.A_CODE);
			assertEquals("B", VendingMachine.B_CODE);
			assertEquals("C", VendingMachine.C_CODE);
			assertEquals("D", VendingMachine.D_CODE);
		}

		@Test
		void startsWithZeroBalance() {
			assertEquals(0.0, machine.getBalance(), DELTA);
		}

		@Test
		void startsWithAllSlotsEmpty() {
			for (String code : CODES) {
				assertNull(machine.getItem(code), "slot " + code + " should be empty");
			}
		}
	}

	// ------------------------------------------------------------------
	@Nested
	@DisplayName("addItem")
	class AddItem {

		@ParameterizedTest
		@ValueSource(strings = { "A", "B", "C", "D" })
		void addsItemToEachSlot(String code) {
			machine.addItem(chips, code);
			assertSame(chips, machine.getItem(code));
		}

		@Test
		void slotsAreIndependent() {
			machine.addItem(chips, "A");
			machine.addItem(soda, "D");
			assertSame(chips, machine.getItem("A"));
			assertNull(machine.getItem("B"));
			assertNull(machine.getItem("C"));
			assertSame(soda, machine.getItem("D"));
		}

		@Test
		void occupiedSlotThrowsAndKeepsOriginal() {
			machine.addItem(chips, "B");
			VendingMachineException ex = assertThrows(VendingMachineException.class,
					() -> machine.addItem(soda, "B"));
			assertEquals("Slot B already occupied", ex.getMessage());
			assertSame(chips, machine.getItem("B"));
		}

		@ParameterizedTest
		@ValueSource(strings = { "E", "a", "AA", "", " A", "1" })
		void invalidCodeThrows(String code) {
			VendingMachineException ex = assertThrows(VendingMachineException.class,
					() -> machine.addItem(chips, code));
			assertEquals("Invalid code for vending machine item", ex.getMessage());
		}

		@Test
		void nullCodeThrowsNullPointerException() {
			assertThrows(NullPointerException.class, () -> machine.addItem(chips, null));
		}

		@Test
		void canReAddAfterRemoval() {
			machine.addItem(chips, "A");
			machine.removeItem("A");
			assertDoesNotThrow(() -> machine.addItem(soda, "A"));
			assertSame(soda, machine.getItem("A"));
		}
	}

	// ------------------------------------------------------------------
	@Nested
	@DisplayName("getItem")
	class GetItem {

		@Test
		void doesNotRemoveItem() {
			machine.addItem(chips, "C");
			machine.getItem("C");
			assertSame(chips, machine.getItem("C"));
		}

		@ParameterizedTest
		@ValueSource(strings = { "E", "a", "", "Z" })
		void invalidCodeThrows(String code) {
			assertThrows(VendingMachineException.class, () -> machine.getItem(code));
		}
	}

	// ------------------------------------------------------------------
	@Nested
	@DisplayName("removeItem")
	class RemoveItem {

		@ParameterizedTest
		@ValueSource(strings = { "A", "B", "C", "D" })
		void returnsAndClearsItem(String code) {
			machine.addItem(chips, code);
			assertSame(chips, machine.removeItem(code));
			assertNull(machine.getItem(code));
		}

		@Test
		void emptySlotThrowsWithMessage() {
			VendingMachineException ex = assertThrows(VendingMachineException.class,
					() -> machine.removeItem("A"));
			assertEquals("Slot A is empty -- cannot remove item", ex.getMessage());
		}

		@Test
		void removingTwiceThrowsSecondTime() {
			machine.addItem(chips, "A");
			machine.removeItem("A");
			assertThrows(VendingMachineException.class, () -> machine.removeItem("A"));
		}

		@Test
		void onlyRemovesRequestedSlot() {
			machine.addItem(chips, "A");
			machine.addItem(soda, "B");
			machine.removeItem("A");
			assertSame(soda, machine.getItem("B"));
		}

		@ParameterizedTest
		@ValueSource(strings = { "E", "a", "", "AB" })
		void invalidCodeThrows(String code) {
			VendingMachineException ex = assertThrows(VendingMachineException.class,
					() -> machine.removeItem(code));
			assertEquals("Invalid code for vending machine item", ex.getMessage());
		}
	}

	// ------------------------------------------------------------------
	@Nested
	@DisplayName("insertMoney / getBalance")
	class Money {

		@Test
		void insertIncreasesBalance() {
			machine.insertMoney(1.50);
			assertEquals(1.50, machine.getBalance(), DELTA);
		}

		@Test
		void insertsAccumulate() {
			machine.insertMoney(1.00);
			machine.insertMoney(0.25);
			machine.insertMoney(0.75);
			assertEquals(2.00, machine.getBalance(), DELTA);
		}

		@Test
		void insertingZeroIsAllowed() {
			assertDoesNotThrow(() -> machine.insertMoney(0));
			assertEquals(0.0, machine.getBalance(), DELTA);
		}

		@ParameterizedTest
		@ValueSource(doubles = { -0.01, -1, -50 })
		void negativeAmountThrowsAndBalanceUnchanged(double amount) {
			machine.insertMoney(3.00);
			VendingMachineException ex = assertThrows(VendingMachineException.class,
					() -> machine.insertMoney(amount));
			assertEquals("Invalid amount.  Amount must be >= 0", ex.getMessage());
			assertEquals(3.00, machine.getBalance(), DELTA);
		}

		@Test
		void getBalanceDoesNotChangeBalance() {
			machine.insertMoney(2.00);
			machine.getBalance();
			machine.getBalance();
			assertEquals(2.00, machine.getBalance(), DELTA);
		}
	}

	// ------------------------------------------------------------------
	@Nested
	@DisplayName("makePurchase")
	class MakePurchase {

		@Test
		void succeedsWithEnoughMoney() {
			machine.addItem(chips, "A");
			machine.insertMoney(2.00);
			assertTrue(machine.makePurchase("A"));
			assertEquals(0.75, machine.getBalance(), DELTA);
			assertNull(machine.getItem("A"), "slot should be empty after purchase");
		}

		@Test
		void succeedsWithExactMoney() {
			machine.addItem(soda, "B");
			machine.insertMoney(2.00);
			assertTrue(machine.makePurchase("B"));
			assertEquals(0.0, machine.getBalance(), DELTA);
		}

		@Test
		void failsWithInsufficientMoneyAndChangesNothing() {
			machine.addItem(soda, "B");
			machine.insertMoney(1.99);
			assertFalse(machine.makePurchase("B"));
			assertEquals(1.99, machine.getBalance(), DELTA);
			assertSame(soda, machine.getItem("B"), "item should remain in slot");
		}

		@Test
		void failsWithNoMoney() {
			machine.addItem(chips, "C");
			assertFalse(machine.makePurchase("C"));
			assertSame(chips, machine.getItem("C"));
		}

		@Test
		void emptySlotReturnsFalseAndKeepsBalance() {
			machine.insertMoney(5.00);
			assertFalse(machine.makePurchase("D"));
			assertEquals(5.00, machine.getBalance(), DELTA);
		}

		@Test
		void freeItemCanBeBoughtWithZeroBalance() {
			VendingMachineItem freebie = new VendingMachineItem("Sample", 0);
			machine.addItem(freebie, "A");
			assertTrue(machine.makePurchase("A"));
			assertEquals(0.0, machine.getBalance(), DELTA);
		}

		@Test
		void cannotBuySameItemTwice() {
			machine.addItem(chips, "A");
			machine.insertMoney(10.00);
			assertTrue(machine.makePurchase("A"));
			assertFalse(machine.makePurchase("A"));
			assertEquals(8.75, machine.getBalance(), DELTA);
		}

		@Test
		void multiplePurchasesDeductCumulatively() {
			machine.addItem(chips, "A");
			machine.addItem(soda, "B");
			machine.insertMoney(5.00);
			assertTrue(machine.makePurchase("A"));
			assertTrue(machine.makePurchase("B"));
			assertEquals(1.75, machine.getBalance(), DELTA);
		}

		@Test
		void secondPurchaseFailsWhenBalanceRunsOut() {
			machine.addItem(chips, "A");
			machine.addItem(soda, "B");
			machine.insertMoney(2.50);
			assertTrue(machine.makePurchase("B"));
			assertFalse(machine.makePurchase("A"));
			assertEquals(0.50, machine.getBalance(), DELTA);
			assertSame(chips, machine.getItem("A"));
		}

		/**
		 * The Javadoc for makePurchase only mentions returning false for empty slots
		 * or insufficient funds, but an invalid code propagates the exception from
		 * getItem. This test documents the actual behaviour.
		 */
		@ParameterizedTest
		@ValueSource(strings = { "E", "a", "", "AA" })
		void invalidCodeThrows(String code) {
			assertThrows(VendingMachineException.class, () -> machine.makePurchase(code));
		}
	}

	// ------------------------------------------------------------------
	@Nested
	@DisplayName("returnChange")
	class ReturnChange {

		@Test
		void returnsBalanceAndResetsToZero() {
			machine.insertMoney(3.25);
			assertEquals(3.25, machine.returnChange(), DELTA);
			assertEquals(0.0, machine.getBalance(), DELTA);
		}

		@Test
		void returnsZeroOnFreshMachine() {
			assertEquals(0.0, machine.returnChange(), DELTA);
		}

		@Test
		void secondCallReturnsZero() {
			machine.insertMoney(1.00);
			machine.returnChange();
			assertEquals(0.0, machine.returnChange(), DELTA);
		}

		@Test
		void returnsRemainderAfterPurchase() {
			machine.addItem(chips, "A");
			machine.insertMoney(2.00);
			machine.makePurchase("A");
			assertEquals(0.75, machine.returnChange(), DELTA);
		}

		@Test
		void machineIsUsableAfterReturningChange() {
			machine.insertMoney(1.00);
			machine.returnChange();
			machine.insertMoney(2.00);
			assertEquals(2.00, machine.getBalance(), DELTA);
		}

		@Test
		void doesNotAffectStockedItems() {
			machine.addItem(chips, "A");
			machine.insertMoney(1.00);
			machine.returnChange();
			assertSame(chips, machine.getItem("A"));
		}
	}

	// ------------------------------------------------------------------
	@Nested
	@DisplayName("slot mapping and state integrity")
	class SlotMappingAndIntegrity {

		@Test
		void eachCodeMapsToItsOwnSlot() {
			VendingMachineItem[] items = new VendingMachineItem[CODES.length];
			for (int i = 0; i < CODES.length; i++) {
				items[i] = new VendingMachineItem("Item" + i, i + 1);
				machine.addItem(items[i], CODES[i]);
			}
			for (int i = 0; i < CODES.length; i++) {
				assertSame(items[i], machine.getItem(CODES[i]), "getItem " + CODES[i]);
			}
			for (int i = 0; i < CODES.length; i++) {
				assertSame(items[i], machine.removeItem(CODES[i]), "removeItem " + CODES[i]);
			}
		}

		@Test
		void purchaseChargesThePriceOfTheRequestedSlot() {
			for (int i = 0; i < CODES.length; i++) {
				machine.addItem(new VendingMachineItem("Item" + i, i + 1), CODES[i]);
			}
			machine.insertMoney(100);
			double expected = 100;
			for (int i = CODES.length - 1; i >= 0; i--) {
				assertTrue(machine.makePurchase(CODES[i]));
				expected -= (i + 1);
				assertEquals(expected, machine.getBalance(), DELTA, "after buying " + CODES[i]);
			}
		}

		@Test
		void failedAddWithInvalidCodeChangesNothing() {
			machine.addItem(chips, "A");
			machine.insertMoney(1.00);
			assertThrows(VendingMachineException.class, () -> machine.addItem(soda, "Z"));
			assertSame(chips, machine.getItem("A"));
			for (String code : new String[] { "B", "C", "D" }) {
				assertNull(machine.getItem(code));
			}
			assertEquals(1.00, machine.getBalance(), DELTA);
		}

		@Test
		void failedPurchaseOfEmptySlotDoesNotDisturbOtherSlots() {
			machine.addItem(chips, "A");
			machine.insertMoney(5.00);
			assertFalse(machine.makePurchase("B"));
			assertSame(chips, machine.getItem("A"));
			assertEquals(5.00, machine.getBalance(), DELTA);
		}

		@Test
		void protectedBalanceFieldTracksGetBalance() {
			machine.insertMoney(2.50);
			assertEquals(machine.getBalance(), machine.balance, DELTA);
			machine.returnChange();
			assertEquals(0.0, machine.balance, DELTA);
		}
	}

	// ------------------------------------------------------------------
	/**
	 * Randomly drives the machine and compares it with a trivial model (a Map and
	 * a running total). Prices and amounts are multiples of 0.25, which are
	 * exactly representable as doubles, so exact comparison is safe. The seed is
	 * fixed so failures are reproducible.
	 */
	@Test
	@DisplayName("random operation sequences match a simple model")
	void randomOperationsMatchSimpleModel() {
		Random rnd = new Random(12345L);
		Map<String, VendingMachineItem> model = new HashMap<>();
		double modelBalance = 0;

		for (int step = 0; step < 5000; step++) {
			String code = CODES[rnd.nextInt(CODES.length)];
			switch (rnd.nextInt(5)) {
				case 0: {
					VendingMachineItem item = new VendingMachineItem("i" + step, rnd.nextInt(12) * 0.25);
					if (model.containsKey(code)) {
						assertThrows(VendingMachineException.class, () -> machine.addItem(item, code));
					} else {
						machine.addItem(item, code);
						model.put(code, item);
					}
					break;
				}
				case 1: {
					if (model.containsKey(code)) {
						assertSame(model.remove(code), machine.removeItem(code));
					} else {
						assertThrows(VendingMachineException.class, () -> machine.removeItem(code));
					}
					break;
				}
				case 2: {
					double amount = rnd.nextInt(9) * 0.25;
					machine.insertMoney(amount);
					modelBalance += amount;
					break;
				}
				case 3: {
					VendingMachineItem item = model.get(code);
					boolean expected = item != null && modelBalance >= item.getPrice();
					if (expected) {
						model.remove(code);
						modelBalance -= item.getPrice();
					}
					assertEquals(expected, machine.makePurchase(code), "step " + step);
					break;
				}
				default: {
					assertEquals(modelBalance, machine.returnChange(), DELTA, "step " + step);
					modelBalance = 0;
				}
			}
			assertEquals(modelBalance, machine.getBalance(), DELTA, "balance at step " + step);
			for (String c : CODES) {
				assertSame(model.get(c), machine.getItem(c), "slot " + c + " at step " + step);
			}
		}
	}


	// ------------------------------------------------------------------
	@Test
	@DisplayName("full workflow: stock, pay, buy, get change")
	void endToEndWorkflow() {
		machine.addItem(chips, "A");
		machine.addItem(soda, "B");

		machine.insertMoney(1.00);
		machine.insertMoney(1.00);
		machine.insertMoney(1.00);
		assertEquals(3.00, machine.getBalance(), DELTA);

		assertTrue(machine.makePurchase("B"));
		assertFalse(machine.makePurchase("A"), "only 1.00 left, chips cost 1.25");
		assertEquals(1.00, machine.returnChange(), DELTA);

		assertNull(machine.getItem("B"));
		assertSame(chips, machine.getItem("A"));
	}
}
