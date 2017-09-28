package twopiradians.minewatch.common.item.weapon;

import com.google.common.collect.Multimap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import twopiradians.minewatch.common.Minewatch;
import twopiradians.minewatch.common.sound.ModSoundEvents;
import twopiradians.minewatch.packet.SPacketSimple;

public class ItemReinhardtHammer extends ItemMWWeapon {

	public ItemReinhardtHammer() {
		super(0);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		if (slot == EntityEquipmentSlot.MAINHAND)
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), 
					new AttributeModifier(ATTACK_DAMAGE_MODIFIER, SharedMonsterAttributes.ATTACK_DAMAGE.getName(), 75d*damageScale-1, 0));
		return multimap;
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack) {
		if (entity instanceof EntityPlayer && entity.getHeldItemMainhand() != null && 
				entity.getHeldItemMainhand().getItem() == this)
			return false;
		else 
			return true;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if (!player.world.isRemote && this.canUse(player, true, getHand(player, stack))) {
			entity.attackEntityFrom(DamageSource.causePlayerDamage(player), 75f*damageScale);
			if (entity instanceof EntityLivingBase) 
				((EntityLivingBase) entity).knockBack(player, 0.4F, 
						(double)MathHelper.sin(player.rotationYaw * 0.017453292F), 
						(double)(-MathHelper.cos(player.rotationYaw * 0.017453292F)));
			player.getHeldItemMainhand().damageItem(1, player);
		}
		return false;
	}

	@Override
	public void onItemLeftClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) { 
		if (!world.isRemote && this.canUse(player, true, hand) && !hero.ability1.isSelected(player) &&
				hand == EnumHand.MAIN_HAND) {
			if (player instanceof EntityPlayerMP)
				Minewatch.network.sendTo(new SPacketSimple(5), (EntityPlayerMP) player);
			for (EntityLivingBase entity : 
				player.world.getEntitiesWithinAABB(EntityLivingBase.class, 
						player.getEntityBoundingBox().offset(player.getLookVec().scale(3)).grow(2.0D, 1D, 2.0D))) 
				if (entity != player) 
					this.onLeftClickEntity(stack, player, entity);
			player.world.playSound(null, player.posX, player.posY, player.posZ, 
					ModSoundEvents.reinhardtWeapon, SoundCategory.PLAYERS, 
					1.0F, player.world.rand.nextFloat()/3+0.8f);
			player.getCooldownTracker().setCooldown(this, 20);
		}
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
		return true;
	}

	@Override
	public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
		return false;
	}

}
