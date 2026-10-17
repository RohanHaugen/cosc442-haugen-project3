import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("VendingMachineItem")
class VendingMachineItemTest {

	private static final double DELTA = 1e-9;

	@Test
	@DisplayName("constructor stores name and price")
	void constructorStoresNameAndPrice() {
		VendingMachineItem item = new VendingMachineItem("Chips", 1.25);
		assertEquals("Chips", item.getName());
		assertEquals(1.25, item.getPrice(), DELTA);
	}

	@Test
	@DisplayName("a price of zero is allowed")
	void zeroPriceAllowed() {
		VendingMachineItem item = new VendingMachineItem("Free Sample", 0);
		assertEquals(0.0, item.getPrice(), DELTA);
	}

	@ParameterizedTest
	@ValueSource(doubles = { -0.01, -1, -100.5, -Double.MAX_VALUE })
	@DisplayName("negative prices throw VendingMachineException")
	void negativePriceThrows(double badPrice) {
		VendingMachineException ex = assertThrows(VendingMachineException.class,
				() -> new VendingMachineItem("Bad", badPrice));
		assertEquals("Price cannot be less than zero", ex.getMessage());
	}

	@Test
	@DisplayName("getters are stable across repeated calls")
	void gettersAreStable() {
		VendingMachineItem item = new VendingMachineItem("Soda", 2.0);
		assertEquals(item.getName(), item.getName());
		assertEquals(item.getPrice(), item.getPrice(), DELTA);
	}


	@Test
	@DisplayName("empty and null names are accepted (no validation exists)")
	void unusualNamesAccepted() {
		assertEquals("", new VendingMachineItem("", 1).getName());
		assertNull(new VendingMachineItem(null, 1).getName());
	}
}
