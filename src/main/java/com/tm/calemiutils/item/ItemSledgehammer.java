package com.tm.calemiutils.item;

import com.tm.calemiutils.CalemiUtils;
import com.tm.calemiutils.init.InitEnchantments;
import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.VeinScan;
import com.tm.calemiutils.util.helper.LoreHelper;
import com.tm.calemiutils.util.helper.RayTraceHelper;
import com.tm.calemiutils.util.helper.WorldEditHelper;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeBlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ItemSledgehammer extends PickaxeItem {

    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    private static final ResourceLocation oreTags = new ResourceLocation(ForgeMod.getInstance().getModId(), "ores");
    private static final ResourceLocation logTags = new ResourceLocation("minecraft", "logs");

    public int baseChargeTime;
    public int chargeTime;

    public ItemSledgehammer (SledgehammerTiers tier) {
        super(tier, 0, tier.attackSpeed, new Item.Properties().group(CalemiUtils.TAB).maxStackSize(1));

        this.chargeTime = baseChargeTime;
        this.baseChargeTime = tier.baseChargeTime;

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", tier.getAttackDamage() - 1, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", tier.attackSpeed - 4, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable World world, List<ITextComponent> tooltipList, ITooltipFlag advanced) {
        LoreHelper.addInformationLore(tooltipList, "Need a pickaxe, axe, shovel and sword in one single tool? This is your best bet.", true);
        LoreHelper.addControlsLore(tooltipList, "Charge", LoreHelper.Type.USE, true);
        LoreHelper.addControlsLore(tooltipList, "Force Charge", LoreHelper.Type.SNEAK_USE);
        LoreHelper.addControlsLore(tooltipList, "Excavates, Mines Veins & Fells Trees", LoreHelper.Type.RELEASE_USE);

        if (stack.isEnchanted()) {
            LoreHelper.addBlankLine(tooltipList);
        }
    }

    /**
     * Handles charging.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick (World world, PlayerEntity player, Hand hand) {

        ItemStack itemstack = player.getHeldItem(hand);

        //If the off hand has an item and the Player is not crouching, prevent charging.
        if (hand == Hand.MAIN_HAND && !player.getHeldItemOffhand().isEmpty() && !player.isCrouching()) {
            return new ActionResult<>(ActionResultType.FAIL, itemstack);
        }

        chargeTime = Math.max(1, baseChargeTime - EnchantmentHelper.getEfficiencyModifier(player) * 3);
        player.setActiveHand(hand);
        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
    }

    /**
     * Handles vein minding and excavation.
     */
    @Override
    public void onPlayerStoppedUsing (ItemStack heldStack, World world, LivingEntity e, int timeLeft) {

        PlayerEntity player = (PlayerEntity) e;

        Hand hand = Hand.OFF_HAND;

        //If the Sledgehammer is in the main hand, set the current hand to main.
        if (ItemStack.areItemStacksEqual(player.getHeldItemMainhand(), heldStack)) {
            hand = Hand.MAIN_HAND;
        }

        //Checks if fully charged.
        if (getUseDuration(heldStack) - timeLeft >= chargeTime) {

            player.swingArm(hand);

            RayTraceHelper.BlockTrace blockTrace = RayTraceHelper.RayTraceBlock(world, player, hand);

            //Checks if the ray trace hit a Block.
            if (blockTrace != null) {

                Location hit = blockTrace.getHit();

                //If the Block hit was an ore, vein mine.
                if (Objects.requireNonNull(ItemTags.getCollection().get(oreTags)).contains(hit.getBlock().asItem())) {
                    veinMine(heldStack, player, hit);
                    return;
                }

                //If the Block hit was a log, vein mine.
                if (Objects.requireNonNull(ItemTags.getCollection().get(logTags)).contains(hit.getBlock().asItem())) {
                    veinMine(heldStack, player, hit);
                    return;
                }

                //Else, excavate.
                excavateBlocks(world, heldStack, player, hit, blockTrace.getHitSide());
            }
        }
    }

    /**
     * Handles vein mining. (for trees & ores)
     */
    private void veinMine (ItemStack heldStack, PlayerEntity player, Location startLocation) {

        //Checks if the starting Location can be mined.
        if (canBreakBlock(startLocation)) {

            IForgeBlockState state = startLocation.getForgeBlockState();

            //Start a scan of blocks that equal the starting Location's Block.
            VeinScan scan = new VeinScan(startLocation, state.getBlockState().getBlock());
            scan.startScan(64, true);

            int damage = getDamage(heldStack);

            //Iterate through the scanned Locations.
            for (Location nextLocation : scan.buffer) {

                int maxDamage = getMaxDamage(heldStack);

                //If the Sledgehammer is broken, stop the iteration.
                if (damage > maxDamage && maxDamage > 0) {
                    return;
                }

                nextLocation.breakBlock(player, heldStack);
                damageHammer(heldStack, player);
                damage++;
            }
        }
    }

    /**
     * Handles the 3x3 mining of Blocks.
     * Size can increase based on Crushing enchant.
     */
    private void excavateBlocks (World worldIn, ItemStack heldStack, PlayerEntity player, Location location, Direction face) {

        int radius = EnchantmentHelper.getEnchantmentLevel(InitEnchantments.CRUSHING.get(), heldStack) + 1;

        ArrayList<Location> locations = WorldEditHelper.selectFlatCubeFromFace(location, face, radius);

        int damage = getDamage(heldStack);

        //Iterate through the Locations from the World Edit shape.
        for (Location nextLocation : locations) {

            int maxDamage = getMaxDamage(heldStack);

            //If the Sledgehammer is broken, stop the iteration.
            if (damage > maxDamage && maxDamage > 0) {
                return;
            }

            //Checks if the next Location can be mined.
            if (canBreakBlock(nextLocation)) {
                nextLocation.breakBlock(player, heldStack);
                damageHammer(heldStack, player);
                damage++;
            }
        }
    }

    /**
     * Checks if the Block at the given Location can be mined by the Sledgehammer.
     */
    private boolean canBreakBlock (Location location) {

        float hardness = location.getForgeBlockState().getBlockState().getBlockHardness(location.world, location.getBlockPos());
        int harvestLevel = location.getForgeBlockState().getHarvestLevel();

        return hardness >= 0 && hardness <= 50 && getTier().getHarvestLevel() >= harvestLevel;
    }

    /**
     * Handles damaging when the Sledgehammer breaks a Block.
     */
    @Override
    public boolean onBlockDestroyed (ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity livingEntity) {

        //Checks if on server & if the block has hardness.
        if (!world.isRemote && state.getBlockHardness(world, pos) != 0.0F) {

            //If not a Starlight Sledgehammer, damage the item.
            damageHammer(stack, livingEntity);
        }

        return true;
    }

    /**
     * Handles damaging when the Sledgehammer hits an Entity.
     */
    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        damageHammer(stack, attacker);
        return true;
    }

    /**
     * Used to damage the Sledgehammer if its not a Starlight one.
     */
    private void damageHammer(ItemStack stack, LivingEntity livingEntity) {
        if (stack.getItem() != InitItems.SLEDGEHAMMER_STARLIGHT.get()) stack.damageItem(1, livingEntity, (i) -> i.sendBreakAnimation(EquipmentSlotType.MAINHAND));
    }

    /**
     * Handles setting attack damage & speed.
     */
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return equipmentSlot == EquipmentSlotType.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(equipmentSlot);
    }

    @Override
    public boolean canHarvestBlock (BlockState blockState) {
        return getTier().getHarvestLevel() >= blockState.getHarvestLevel();
    }

    @Override
    public float getDestroySpeed (ItemStack stack, BlockState blockState) {
        return this.efficiency;
    }

    @Override
    public Set<ToolType> getToolTypes (ItemStack stack) {
        return ImmutableSet.of(ToolType.PICKAXE, ToolType.AXE, ToolType.SHOVEL);
    }

    @Override
    public UseAction getUseAction (ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getUseDuration (ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean hasEffect (ItemStack stack) {
        return this == InitItems.SLEDGEHAMMER_STARLIGHT.get() || stack.isEnchanted();
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        if (stack.getItem() == InitItems.SLEDGEHAMMER_STARLIGHT.get()) return Rarity.RARE;
        return Rarity.COMMON;
    }
}
