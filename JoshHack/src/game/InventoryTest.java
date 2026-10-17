package game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import asciiPanel.AsciiPanel;

/**
 * Tests for {@link Inventory}.
 *
 * ASSUMPTION: Item has an accessible no-arg constructor. If it doesn't,
 * change newItem() below; it's the only place Items are created.
 * Inventory compares with ==, so every call to newItem() gives a distinct item.
 */
class InventoryTest {

    private static final int CAPACITY = 3;

    private Inventory inventory;

    private static Item newItem() {
        return new Item('*', AsciiPanel.brightWhite, "teddy bear", null);
    }

    private static long count(Inventory inv) {
        long n = 0;
        for (Item i : inv.getItems()) {
            if (i != null) n++;
        }
        return n;
    }

    @BeforeEach
    void setUp() {
        inventory = new Inventory(CAPACITY);
    }

    @Nested
    @DisplayName("constructor / getters")
    class Construction {

        @Test
        void createsEmptyArrayOfRequestedSize() {
            Item[] items = inventory.getItems();
            assertNotNull(items);
            assertEquals(CAPACITY, items.length);
            for (Item i : items) {
                assertNull(i);
            }
        }

        @Test
        void zeroCapacityIsAllowed() {
            Inventory empty = new Inventory(0);
            assertEquals(0, empty.getItems().length);
        }

        @Test
        void negativeCapacityThrows() {
            assertThrows(NegativeArraySizeException.class, () -> new Inventory(-1));
        }

        @Test
        void getItemsReturnsBackingArray() {
            Item a = newItem();
            inventory.add(a);
            assertSame(inventory.getItems(), inventory.getItems());
            assertSame(a, inventory.getItems()[0]);
        }

        @Test
        void getReturnsItemAtIndex() {
            Item a = newItem();
            Item b = newItem();
            inventory.add(a);
            inventory.add(b);
            assertSame(a, inventory.get(0));
            assertSame(b, inventory.get(1));
        }

        @Test
        void getReturnsNullForEmptySlot() {
            assertNull(inventory.get(0));
        }

        @Test
        void getOutOfBoundsThrows() {
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> inventory.get(-1));
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> inventory.get(CAPACITY));
        }
    }

    @Nested
    @DisplayName("add")
    class Add {

        @Test
        void addsToFirstEmptySlot() {
            Item a = newItem();
            inventory.add(a);
            assertSame(a, inventory.get(0));
            assertEquals(1, count(inventory));
        }

        @Test
        void addsInOrder() {
            Item a = newItem();
            Item b = newItem();
            Item c = newItem();
            inventory.add(a);
            inventory.add(b);
            inventory.add(c);
            assertSame(a, inventory.get(0));
            assertSame(b, inventory.get(1));
            assertSame(c, inventory.get(2));
        }

        @Test
        void fillsGapLeftByRemoval() {
            Item a = newItem();
            Item b = newItem();
            Item c = newItem();
            inventory.add(a);
            inventory.add(b);
            inventory.remove(a); // slot 0 now empty

            inventory.add(c);

            assertSame(c, inventory.get(0));
            assertSame(b, inventory.get(1));
        }

        @Test
        void doesNothingWhenFull() {
            Item a = newItem();
            Item b = newItem();
            Item c = newItem();
            Item extra = newItem();
            inventory.add(a);
            inventory.add(b);
            inventory.add(c);

            inventory.add(extra);

            assertEquals(CAPACITY, count(inventory));
            assertFalse(inventory.contains(extra));
            assertSame(a, inventory.get(0));
            assertSame(b, inventory.get(1));
            assertSame(c, inventory.get(2));
        }

        @Test
        void doesNothingWhenCapacityIsZero() {
            Inventory empty = new Inventory(0);
            assertDoesNotThrow(() -> empty.add(newItem()));
            assertEquals(0, empty.getItems().length);
        }

        @Test
        void sameItemCanBeAddedTwice() {
            Item a = newItem();
            inventory.add(a);
            inventory.add(a);
            assertSame(a, inventory.get(0));
            assertSame(a, inventory.get(1));
            assertEquals(2, count(inventory));
        }

        @Test
        void addingNullLeavesSlotEmpty() {
            inventory.add(null);
            assertEquals(0, count(inventory));
        }
    }

    @Nested
    @DisplayName("remove")
    class Remove {

        @Test
        void removesExistingItem() {
            Item a = newItem();
            inventory.add(a);
            inventory.remove(a);
            assertNull(inventory.get(0));
            assertFalse(inventory.contains(a));
        }

        @Test
        void removesOnlyTheMatchingItem() {
            Item a = newItem();
            Item b = newItem();
            inventory.add(a);
            inventory.add(b);

            inventory.remove(b);

            assertSame(a, inventory.get(0));
            assertNull(inventory.get(1));
        }


        @Test
        void removingMissingItemChangesNothing() {
            Item a = newItem();
            Item other = newItem();
            inventory.add(a);

            assertDoesNotThrow(() -> inventory.remove(other));

            assertSame(a, inventory.get(0));
            assertEquals(1, count(inventory));
        }

        @Test
        void removingFromFullInventoryWithMissingItemChangesNothing() {
            inventory.add(newItem());
            inventory.add(newItem());
            inventory.add(newItem());

            inventory.remove(newItem()); // loop runs to the end without a match

            assertEquals(CAPACITY, count(inventory));
        }

        @Test
        void removesOnlyFirstOccurrenceOfDuplicate() {
            Item a = newItem();
            inventory.add(a);
            inventory.add(a);

            inventory.remove(a);

            assertNull(inventory.get(0));
            assertSame(a, inventory.get(1));
            assertEquals(1, count(inventory));
        }

        @Test
        void removeOnEmptyInventoryDoesNothing() {
            assertDoesNotThrow(() -> inventory.remove(newItem()));
            assertEquals(0, count(inventory));
        }


        @Test
        void removeOnZeroCapacityDoesNothing() {
            Inventory empty = new Inventory(0);
            assertDoesNotThrow(() -> empty.remove(newItem()));
        }
    }

    @Nested
    @DisplayName("isFull")
    class IsFull {

        @Test
        void emptyInventoryIsNotFull() {
            assertFalse(inventory.isFull());
        }

        @Test
        void partiallyFilledInventoryIsNotFull() {
            inventory.add(newItem());
            inventory.add(newItem());
            assertFalse(inventory.isFull());
        }

        @Test
        void completelyFilledInventoryIsFull() {
            inventory.add(newItem());
            inventory.add(newItem());
            inventory.add(newItem());
            assertTrue(inventory.isFull());
        }

        @Test
        void notFullAfterRemovingFromFullInventory() {
            Item a = newItem();
            inventory.add(a);
            inventory.add(newItem());
            inventory.add(newItem());
            assertTrue(inventory.isFull());

            inventory.remove(a);

            assertFalse(inventory.isFull());
        }

        @Test
        void fullAgainAfterRefilling() {
            Item a = newItem();
            inventory.add(a);
            inventory.add(newItem());
            inventory.add(newItem());
            inventory.remove(a);
            inventory.add(newItem());
            assertTrue(inventory.isFull());
        }

        @Test
        void zeroCapacityInventoryIsFull() {
            assertTrue(new Inventory(0).isFull());
        }
    }

    @Nested
    @DisplayName("contains")
    class Contains {

        @Test
        void trueForPresentItem() {
            Item a = newItem();
            inventory.add(a);
            assertTrue(inventory.contains(a));
        }

        @Test
        void trueForItemInLastSlot() {
            inventory.add(newItem());
            inventory.add(newItem());
            Item last = newItem();
            inventory.add(last);
            assertTrue(inventory.contains(last));
        }

        @Test
        void falseForAbsentItem() {
            inventory.add(newItem());
            assertFalse(inventory.contains(newItem()));
        }

        @Test
        void falseOnEmptyInventory() {
            assertFalse(inventory.contains(newItem()));
        }

        @Test
        void falseAfterItemRemoved() {
            Item a = newItem();
            inventory.add(a);
            inventory.remove(a);
            assertFalse(inventory.contains(a));
        }

        @Test
        void usesReferenceEqualityNotEquals() {
            Item a = newItem();
            Item lookalike = newItem();
            inventory.add(a);
            assertFalse(inventory.contains(lookalike));
        }

        @Test
        void containsNullIsTrueWhenThereIsAnEmptySlot() {
            // Documents current behaviour: empty slots are null, so null "matches".
            assertTrue(inventory.contains(null));
        }

        @Test
        void containsNullIsFalseWhenFull() {
            inventory.add(newItem());
            inventory.add(newItem());
            inventory.add(newItem());
            assertFalse(inventory.contains(null));
        }

        @Test
        void zeroCapacityContainsNothing() {
            Inventory empty = new Inventory(0);
            assertFalse(empty.contains(newItem()));
            assertFalse(empty.contains(null));
        }
    }
}
