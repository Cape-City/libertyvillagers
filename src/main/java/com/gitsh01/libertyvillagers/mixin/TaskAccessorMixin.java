package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Task.class)
public interface TaskAccessorMixin {

    @Accessor("minRunTime")
    @Mutable
    void setMinRunTime(int minRunTime);

    @Accessor("maxRunTime")
    @Mutable
    void setMaxRunTime(int maxRunTime);

    @Accessor("requiredMemoryStates")
    @Mutable
    void setRequiredMemoryStates(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryStates);
}
