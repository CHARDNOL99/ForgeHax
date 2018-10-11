package com.matt.forgehax.util.entity;

import static com.matt.forgehax.Helper.getLocalPlayer;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LocalPlayerInventory {
  public static InventoryPlayer getInventory() {
    return getLocalPlayer().inventory;
  }

  public static Container getContainer() {
    return getLocalPlayer().inventoryContainer;
  }

  public static Container getOpenContainer() {
    return getLocalPlayer().openContainer;
  }

  public static List<InvItem> getMainInventory() {
    AtomicInteger next = new AtomicInteger(0);
    return getInventory()
        .mainInventory
        .stream()
        .map(item -> new InvItem(item, next.getAndIncrement()))
        .collect(Collectors.toList());
  }

  public static List<InvItem> getMainInventory(int start, int end) {
    return getMainInventory().subList(start, end);
  }

  public static List<InvItem> getHotbarInventory() {
    return getMainInventory(0, 8);
  }

  public static InvItem getSelected() {
    return getMainInventory().get(getInventory().currentItem);
  }

  public static void setSelected(int index) {
    if (index < 0 || index > 8)
      throw new IndexOutOfBoundsException("Can only select index in hot bar (0-8)");

    getInventory().currentItem = index;
  }

  public static void setSelected(InvItem invItem) {
    setSelected(invItem.getIndex());
  }

  public static class InvItem implements Comparable<InvItem> {
    public static final InvItem EMPTY = new InvItem(ItemStack.EMPTY, -1);

    private final ItemStack itemStack;
    private final int index;

    public InvItem(ItemStack itemStack, int index) {
      this.itemStack = itemStack;
      this.index = index;
    }

    public ItemStack getItemStack() {
      return itemStack;
    }

    public Item getItem() {
      return itemStack.getItem();
    }

    public int getIndex() {
      return index;
    }

    public boolean isNull() {
      return ItemStack.EMPTY.equals(getItemStack());
    }

    public boolean nonNull() {
      return !isNull();
    }

    @Override
    public boolean equals(Object obj) {
      return this == obj
          || (obj instanceof InvItem
              && getIndex() == ((InvItem) obj).getIndex()
              && getItemStack().equals(((InvItem) obj).getItemStack()));
    }

    @Override
    public int hashCode() {
      return Objects.hash(itemStack, index);
    }

    @Override
    public int compareTo(InvItem o) {
      return Integer.compare(getIndex(), o.getIndex());
    }
  }
}
