import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("VendingMachineException")
class VendingMachineExceptionTest {

	@Test
	@DisplayName("default constructor has no message")
	void defaultConstructor() {
		VendingMachineException ex = new VendingMachineException();
		assertNull(ex.getMessage());
	}

	@Test
	@DisplayName("message constructor stores the reason")
	void messageConstructor() {
		VendingMachineException ex = new VendingMachineException("boom");
		assertEquals("boom", ex.getMessage());
	}

	@Test
	@DisplayName("is an unchecked exception")
	void isRuntimeException() {
		assertInstanceOf(RuntimeException.class, new VendingMachineException("x"));
	}
}
