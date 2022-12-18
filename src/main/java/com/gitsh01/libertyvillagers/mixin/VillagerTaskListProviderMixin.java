package com.gitsh01.libertyvillagers.mixin;

import com.gitsh01.libertyvillagers.tasks.HealGolemTask;
import com.gitsh01.libertyvillagers.tasks.ThrowRegenPotionAtTask;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(VillagerTaskListProvider.class)
public abstract class VillagerTaskListProviderMixin {

    private static final int SECONDARY_WORK_TASK_PRIORITY = 5; // Mojang default: 5.
    private static final int THIRD_WORK_TASK_PRIORITY = 7;
    private static final int PRIMARY_WORK_TASK_PRIORITY = 7;

    @Invoker("createBusyFollowTask")
    public static Pair<Integer, Task<LivingEntity>> invokeCreateBusyFollowTask() {
        throw new AssertionError();
    }

    @Inject(method = "createWorkTasks", at = @At("Head"), cancellable = true)
    private static void replaceCreateWorkTasks(VillagerProfession profession, float speed,
                                               CallbackInfoReturnable<List<Pair<Integer, ? extends Task<? super VillagerEntity>>>> cir) {
        Task<? super VillagerEntity> villagerWorkTask = new VillagerWorkTask(); // Plays working sounds on the job site.
        Task<? super VillagerEntity> secondaryWorkTask = null;
        // GoToIfNearby makes the villager wander around the job site.
        Task<? super VillagerEntity> thirdWorkTask = GoToIfNearbyTask.create(MemoryModuleType.JOB_SITE, 0.4f, 4);
        switch (profession.toString()) {
            case "armorer":
                if (CONFIG.villagersProfessionConfig.armorerHealsGolems) {
                    secondaryWorkTask = new HealGolemTask();
                }
                break;
            case "cleric":
                if (CONFIG.villagersProfessionConfig.clericThrowsPotionsAtPlayers ||
                        CONFIG.villagersProfessionConfig.clericThrowsPotionsAtVillagers) {
                    secondaryWorkTask = new ThrowRegenPotionAtTask();
                }
                break;
            case "farmer":
                villagerWorkTask = new FarmerVillagerTask(); // Harvest / plant seeds.
                secondaryWorkTask = new FarmerWorkTask(); // Compost.
                thirdWorkTask = new BoneMealTask(); // Apply bonemeal to crops.
                break;
        }

        ArrayList<Pair<Task<? super VillagerEntity>, Integer>> randomTasks = new ArrayList<>(
                ImmutableList.of(Pair.of(villagerWorkTask, PRIMARY_WORK_TASK_PRIORITY),
                        Pair.of(GoToNearbyPositionTask.create(MemoryModuleType.JOB_SITE, 0.4f,
                                CONFIG.villagerPathfindingConfig.minimumPOISearchDistance, 10), 5),
                        Pair.of(GoToSecondaryPositionTask.create(MemoryModuleType.SECONDARY_JOB_SITE, speed,
                                CONFIG.villagerPathfindingConfig.minimumPOISearchDistance, 6,
                                MemoryModuleType.JOB_SITE), 5)));

        if (secondaryWorkTask != null) {
            randomTasks.add(Pair.of(secondaryWorkTask, SECONDARY_WORK_TASK_PRIORITY));
        }

        if (thirdWorkTask != null) {
            randomTasks.add(Pair.of(thirdWorkTask, THIRD_WORK_TASK_PRIORITY));
        }

        RandomTask<VillagerEntity> randomTask = new RandomTask<>(ImmutableList.copyOf(randomTasks));
        List<Pair<Integer, ? extends Task<? super VillagerEntity>>> tasks =
                List.of(VillagerTaskListProviderMixin.invokeCreateBusyFollowTask(),
                        Pair.of(7, randomTask),
                        Pair.of(10, new HoldTradeOffersTask(400, 1600)),
                        Pair.of(10, FindInteractionTargetTask.create(EntityType.PLAYER, 4)),
                        Pair.of(2, VillagerWalkTowardsTask.create(MemoryModuleType.JOB_SITE, speed, 9,
                                        CONFIG.villagerPathfindingConfig.pathfindingMaxRange, 1200)),
                        Pair.of(3, new GiveGiftsToHeroTask(100)),
                        Pair.of(99, ScheduleActivityTask.create()));
        cir.setReturnValue(ImmutableList.copyOf(tasks));
        cir.cancel();
    }

    @ModifyArg(method = "createMeetTasks(Lnet/minecraft/village/VillagerProfession;F)Lcom/google/common/collect/ImmutableList;",
            at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/ai/brain/task/VillagerWalkTowardsTask;create(Lnet/minecraft/entity/ai/brain/MemoryModuleType;FIII)Lnet/minecraft/entity/ai/brain/task/SingleTickTask;"),
            index = 2)
    private static int replaceCompletionRangeForWalkTowardsMeetTask(int completionRange) {
        return Math.max(CONFIG.villagerPathfindingConfig.minimumPOISearchDistance, completionRange) + 3;
    }
}
