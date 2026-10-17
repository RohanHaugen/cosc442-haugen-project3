Source File	Initial Statement/Line Coverage	Initial Branch Coverage
VendingMachine.java	42/43	19/20
VendingMachineItem.java	8/8	2/2

Source File	Final Statement/Line Coverage	Final Branch Coverage
VendingMachine.java	43/43	20/20
VendingMachineItem.java	8/8	20/20

Added 1 test, testRemoveItem_emptyIndex_throwsException, which checks if the proper exception is returned when buying an empty slot

The makePurchase decision: 
Item != null, this.balance >= item.getPrice()
The sixth set of values in the parametrized test tests if it fails nulls, the third set of values tests if it fails when the price is above the balance. The first set of values tests if both are positive

Test-sensitivity experiment: 2 tests failed, testMakePurchase_zeroPrice_purchasesItem and the second set of values for the parametrized test.