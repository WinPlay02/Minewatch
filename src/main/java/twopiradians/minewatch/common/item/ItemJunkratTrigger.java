package twopiradians.minewatch.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import twopiradians.minewatch.common.hero.EnumHero;
import twopiradians.minewatch.common.item.armor.ItemMWArmor;

public class ItemJunkratTrigger extends Item {

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {	
		if (!world.isRemote) {
			// if not wearing full set, mine is dead, or main is not junkrat's launcher
			if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getHeldItemOffhand() == stack &&
					(ItemMWArmor.SetManager.entitiesWearingSets.get(entity.getPersistentID()) != EnumHero.JUNKRAT ||
					EnumHero.JUNKRAT.ability2.entities.get(entity) == null || !EnumHero.JUNKRAT.ability2.entities.get(entity).isEntityAlive() ||
					((EntityLivingBase)entity).getHeldItemMainhand() == null || 
					((EntityLivingBase)entity).getHeldItemMainhand().getItem() != EnumHero.JUNKRAT.weapon)) {
				((EntityLivingBase)entity).setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
			}
			// if not in offhand
			else if (entity instanceof EntityPlayer && 
					((EntityPlayer)entity).getHeldItemOffhand() != stack &&
					((EntityPlayer)entity).inventory.getStackInSlot(slot) == stack) {
				((EntityPlayer)entity).inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
			}
		}
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (!entityItem.world.isRemote && entityItem != null && entityItem.getEntityItem() != null) {
			entityItem.setDead();
			return true;
		}
		return false;
	}

}