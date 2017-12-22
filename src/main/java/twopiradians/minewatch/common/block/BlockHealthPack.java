package twopiradians.minewatch.common.block;


import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twopiradians.minewatch.common.Minewatch;
import twopiradians.minewatch.common.tileentity.TileEntityHealthPack;
import twopiradians.minewatch.common.util.EntityHelper;

public abstract class BlockHealthPack extends Block {

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1D, 1.0D);

	public BlockHealthPack() {
		super(Material.STRUCTURE_VOID);
		this.setBlockUnbreakable();
		this.setCreativeTab((CreativeTabs) Minewatch.tabMapMaking);
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof TileEntityHealthPack && 
				entityIn instanceof EntityLivingBase) {
			TileEntityHealthPack te = (TileEntityHealthPack) worldIn.getTileEntity(pos);
			EntityLivingBase entity = (EntityLivingBase) entityIn;
			if (te.getCooldown() <= 0 && entity.getHealth() < entity.getMaxHealth() && entity.isEntityAlive()) {
				EntityHelper.heal(entity, te.getHealAmount());
				te.setResetCooldown();
			}
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	@Deprecated
	@SideOnly(Side.CLIENT)
	public boolean isTranslucent(IBlockState state) {
		return false; 
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return 8;
	}

	@Override
	@Deprecated
	public int getLightValue(IBlockState state) {
		return 8;
	}

	public static class Small extends BlockHealthPack {
		@Override
		@Nullable
		public TileEntity createTileEntity(World world, IBlockState state) {
			return new TileEntityHealthPack.Small();
		}
	}
	public static class Large extends BlockHealthPack {
		@Override
		@Nullable
		public TileEntity createTileEntity(World world, IBlockState state) {
			return new TileEntityHealthPack.Large();
		}
	}

}