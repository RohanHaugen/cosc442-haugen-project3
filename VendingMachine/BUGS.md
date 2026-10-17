1. No tests run.
testAddItem_addsItem_correctSlot()/every single one
VendingMachineJava assigns slots by doing i<=4, creating 5 slots for a 4 slot system
I looked at the error message saying Index 4 out of bounds for length 4
I changed i<=4 to i <4
2. insertMoney isnt inserting 0 dollars
testInsertMoney_zeroValue_noIncrease()
insertMoney is checking if value <1
I saw that it was throwing an exception and saw where the exception was being thrown
I changed it to <0 as that is the opposite of >=0, as the documentation describes.
3. testGetPriceZero isnt true
testGetPriceZero
testGetPriceZero isnt using the right item
I saw that it was using item1 instead of item2
I changed it to use the right item
4. (Injected fault) removeItem wasn't removing items
testMakePurchase_enoughMoney_removeItem, testRemoveItem_validItem_removesItem
java.lang.AssertionError: expected null, but was:[VendingMachineItem@a514af7]
This test detected the fault because it was checking to make sure that the items were removed from removeItem