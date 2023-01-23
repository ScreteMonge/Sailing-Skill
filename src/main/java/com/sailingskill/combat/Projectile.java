package com.sailingskill.combat;

import com.sailingskill.npcs.NPCCharacter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.RuneLiteObject;

import javax.annotation.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class Projectile
{
    @Nullable
    private RuneLiteObject projectile;
    private NPCCharacter target;
    private boolean targetsPlayer;
    private int maxHit;
    private int timer;
}
