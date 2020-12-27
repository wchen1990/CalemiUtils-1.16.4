package com.tm.calemiutils.command;

import com.tm.calemiutils.main.CUReference;
import com.tm.calemiutils.block.BlockBlueprint;
import com.tm.calemiutils.config.MarketItemsFile;
import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.item.ItemBrush;
import com.tm.calemiutils.tileentity.TileEntityMarket;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.UnitChatMessage;
import com.tm.calemiutils.util.helper.ChatHelper;
import com.tm.calemiutils.util.helper.ColorHelper;
import com.tm.calemiutils.util.helper.WorldEditHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Objects;

public class CUCommandBase {

    /**
     * Registers all of the commands.
     */
    public static void register (CommandDispatcher<CommandSource> dispatcher) {

        LiteralArgumentBuilder<CommandSource> cuCommand = Commands.literal("cutils");

        cuCommand.requires(commandSource -> true)
                .then(help())
                .then(reload().requires((player) -> player.hasPermissionLevel(2)))
                .then(brushWithHollow("fill"))
                .then(recolor())
                .then(brush("walls"))
                .then(brushCircular("circle"))
                .then(brushCircular("cylinder"))
                .then(brushCircular("sphere"))
                .then(brushWithHollow("pyramid"));

        dispatcher.register(cuCommand);
    }

    /**
     * The help command.
     */
    private static ArgumentBuilder<CommandSource, ?> help () {

        return Commands.literal("help").executes(ctx -> {

            String holdBrush = "[Hold Brush]";
            PlayerEntity player = ctx.getSource().asPlayer();

            ChatHelper.printModMessage(TextFormatting.GREEN, "----- Help for " + CUReference.MOD_NAME + " -----", player);
            ChatHelper.printModMessage(TextFormatting.GREEN, "() are optional arguments.", player);
            ChatHelper.printModMessage(TextFormatting.GREEN, " /cutils reload - Reloads the MarketItems files.", player);
            ChatHelper.printModMessage(TextFormatting.GREEN, holdBrush + " /cutils fill <color> (hollow) - Creates a cube of blueprint. <color> - Color of the Blueprint. (hollow) - Removes interior blueprint.", player);
            ChatHelper.printModMessage(TextFormatting.GREEN, holdBrush + " /cutils recolor <color1> <color2> - Replaces the first color with the second. <color1> - Color of the Blueprint to replace. <color2> - New color of the Blueprint.", player);
            ChatHelper.printModMessage(TextFormatting.GREEN, holdBrush + " /cutils walls <color> - Creates walls of blueprint. <color> - Color of the Blueprint.", player);
            ChatHelper.printModMessage(TextFormatting.GREEN, holdBrush + " /cutils circle <color> (hollow) - Creates a circle of blueprint. <color> - Color of the Blueprint. (hollow) - Removes interior blueprint.", player);
            ChatHelper.printModMessage(TextFormatting.GREEN, holdBrush + " /cutils cylinder <color> (hollow) - Creates a cylinder of blueprint. <color> - Color of the Blueprint. (hollow) - Removes interior blueprint.", player);
            ChatHelper.printModMessage(TextFormatting.GREEN, holdBrush + " /cutils sphere <color> (hollow) - Creates a sphere of blueprint. <color> - Color of the Blueprint. (hollow) - Removes interior blueprint.", player);
            ChatHelper.printModMessage(TextFormatting.GREEN, holdBrush + " /cutils pyramid <color> (hollow) - Creates a pyramid of blueprint. <color> - Color of the Blueprint. (hollow) - Removes interior blueprint.", player);
            return Command.SINGLE_SUCCESS;
        });
    }

    /**
     * The reload command.
     */
    private static ArgumentBuilder<CommandSource, ?> reload () {
        return Commands.literal("reload").executes(ctx -> {

            MarketItemsFile.init();

            PlayerEntity player = ctx.getSource().asPlayer();

            for (TileEntity te : player.world.loadedTileEntityList) {

                if (te instanceof TileEntityMarket) {
                    ((TileEntityMarket) te).dirtyFlag = true;
                }
            }

            ChatHelper.printModMessage(TextFormatting.GREEN, "Successfully reloaded MarketItems files!", player);

            return Command.SINGLE_SUCCESS;
        });
    }

    /**
     * The Brush commands that have a "hollow" argument.
     */
    private static ArgumentBuilder<CommandSource, ?> brushWithHollow (String shape) {
        return Commands.literal(shape).executes(ctx -> 0)
                .then(Commands.argument("color", DyeColorArgument.color())
                        .executes(ctx -> executeBrush(ctx.getSource().asPlayer(), shape, DyeColorArgument.getColor(ctx, "color"), null, false, 1))
                        .then(Commands.literal("hollow")
                                .executes(ctx -> executeBrush(ctx.getSource().asPlayer(), shape, DyeColorArgument.getColor(ctx, "color"), null, true, 1))));
    }

    /**
     * The recolor command.
     */
    private static ArgumentBuilder<CommandSource, ?> recolor () {
        return Commands.literal("recolor")
                .executes(ctx -> 0).then(Commands.argument("color1", DyeColorArgument.color())
                        .executes(ctx -> 0)
                        .then(Commands.argument("color2", DyeColorArgument.color())
                                .executes(ctx -> executeBrush(ctx.getSource().asPlayer(), "recolor", DyeColorArgument.getColor(ctx, "color1"), DyeColorArgument.getColor(ctx, "color2"), false, 1))));
    }

    /**
     * The Brush commands that have no extra arguments.
     */
    @SuppressWarnings("SameParameterValue")
    private static ArgumentBuilder<CommandSource, ?> brush (String shape) {
        return Commands.literal(shape)
                .executes(ctx -> 0)
                .then(Commands.argument("color", DyeColorArgument.color())
                        .executes(ctx -> executeBrush(ctx.getSource().asPlayer(), shape, DyeColorArgument.getColor(ctx, "color"), null, false, 1)));
    }

    /**
     * The circular Brush commands.
     */
    private static ArgumentBuilder<CommandSource, ?> brushCircular (String shape) {
        return Commands.literal(shape)
                .executes(ctx -> 0)
                .then(Commands.argument("color", DyeColorArgument.color())
                        .executes(ctx -> executeBrush(ctx.getSource().asPlayer(), shape, DyeColorArgument.getColor(ctx, "color"), null, false, 1))
                        .then(Commands.literal("hollow")
                                .executes(ctx -> executeBrush(ctx.getSource().asPlayer(), shape, DyeColorArgument.getColor(ctx, "color"), null, true, 1)).then(Commands.argument("thickness", IntegerArgumentType.integer(1, 128)).executes(ctx -> executeBrush(ctx.getSource().asPlayer(), shape, DyeColorArgument.getColor(ctx, "color"), null, true, IntegerArgumentType.getInteger(ctx, "thickness"))))));
    }

    /**
     * Handles all of the Brush commands.
     * @param shape     Used to determine what specific shape command was executed.
     * @param strColor1    Used to color the Blueprint placed.
     * @param strColor2    Used for the mask for the Blueprint to replace.
     * @param hollow    Used to determine if the shape is hollow.
     * @param thickness The amount of thickness of the shape.
     */
    private static int executeBrush (PlayerEntity player, String shape, String strColor1, String strColor2, boolean hollow, int thickness) {

        DyeColor color1 = ColorHelper.getColorFromString(strColor1);
        DyeColor color2 = ColorHelper.getColorFromString(strColor2);

        //Determines which hand has a Brush
        ItemStack stackMainHand = player.getHeldItemMainhand();
        ItemStack stackOffHand = player.getHeldItemOffhand();

        ItemBrush brush = null;

        if (stackMainHand.getItem() instanceof ItemBrush) {
            brush = (ItemBrush) stackMainHand.getItem();
        }

        else if (stackOffHand.getItem() instanceof ItemBrush) {
            brush = (ItemBrush) stackOffHand.getItem();
        }

        //Checks if there is a Brush held.
        if (brush == null) {
            ChatHelper.printModMessage(TextFormatting.RED, "A Brush needs to be held!", player);
            return 0;
        }

        UnitChatMessage unitChatMessage = new UnitChatMessage("Brush", player);

        Location location1 = brush.location1;
        Location location2 = brush.location2;

        //Checks if both Locations have been set.
        if (location1 != null && location2 != null) {

            ArrayList<Location> blocksToPlace = new ArrayList<>();
            BlockBlueprint BLUEPRINT = (BlockBlueprint) Objects.requireNonNull(InitItems.BLUEPRINT.get());

            if (shape.equalsIgnoreCase("fill")) {

                if (hollow) blocksToPlace = WorldEditHelper.selectHollowCubeFromTwoPoints(location1, location2);
                else blocksToPlace = WorldEditHelper.selectCubeFromTwoPoints(location1, location2);
            }

            else if (shape.equalsIgnoreCase("recolor")) {

                blocksToPlace = WorldEditHelper.selectCubeFromTwoPoints(location1, location2);
                WorldEditHelper.generateCommand(blocksToPlace, BLUEPRINT.getDefaultState().with(BlockBlueprint.COLOR, color2), BLUEPRINT.getDefaultState().with(BlockBlueprint.COLOR, color1), player, unitChatMessage);
                return 1;
            }

            else if (shape.equalsIgnoreCase("walls")) {

                int xzRad;

                if (location1.x == location2.x) {
                    xzRad = Math.abs(location2.z - location1.z);
                }

                else if (location1.z == location2.z) {
                    xzRad = Math.abs(location2.x - location1.x);
                }

                else {
                    unitChatMessage.printMessage(TextFormatting.RED, "Both points need to line up either on the x-axis or z-axis!");
                    return 0;
                }

                blocksToPlace = WorldEditHelper.selectWallsFromRadius(location1, xzRad, location1.y, location2.y);
            }

            else if (shape.equalsIgnoreCase("circle")) {
                blocksToPlace = WorldEditHelper.selectCircleFromTwoPoints(location1, location2, hollow, thickness);
            }

            else if (shape.equalsIgnoreCase("cylinder")) {
                blocksToPlace = WorldEditHelper.selectCylinderFromTwoPoints(location1, location2, hollow, thickness);
            }

            else if (shape.equalsIgnoreCase("sphere")) {
                blocksToPlace = WorldEditHelper.selectSphereFromTwoPoints(location1, location2, hollow, thickness);
            }

            else if (shape.equalsIgnoreCase("pyramid")) {

                int xyzRad;

                if (location1.x == location2.x && location1.z == location2.z) {
                    xyzRad = Math.abs(location2.y - location1.y);
                }

                else if (location1.x == location2.x) {
                    xyzRad = Math.abs(location2.z - location1.z);
                }

                else if (location1.z == location2.z) {
                    xyzRad = Math.abs(location2.x - location1.x);
                }

                else {
                    unitChatMessage.printMessage(TextFormatting.RED, "Both points need to line up either on the x-axis, y-axis or z-axis!");
                    return 0;
                }

                blocksToPlace = WorldEditHelper.selectPyramidFromRadius(location1, xyzRad, hollow);
            }

            if (!blocksToPlace.isEmpty()) {
                WorldEditHelper.generateCommand(blocksToPlace, BLUEPRINT.getDefaultState().with(BlockBlueprint.COLOR, color1), Blocks.AIR.getDefaultState(), player, unitChatMessage);
            }

            return Command.SINGLE_SUCCESS;
        }

        else {
            unitChatMessage.printMessage(TextFormatting.RED, "Both of the Brush's positions need to be set!");
            return 0;
        }
    }
}
