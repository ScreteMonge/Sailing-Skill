package com.sailingskill.npcs;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Animation;
import net.runelite.api.Model;
import net.runelite.api.RuneLiteObject;

@Getter
@Setter
public class NPCCharacter
{
    private NPCType npcType;
    private RuneLiteObject runeLiteObject;
    private NPCSize npcSize;
    private Model projectileModel;
    private Animation idleAnimation;
    private Animation walkAnimation;
    private Animation attackAnimation;
    private Animation deathAnimation;
    private Animation projectileAnimation;
    private int attackAnimFrames;
    private int currentHealth;
    private int maxHealth;
    private int attackRange;
    private int chaseRange;
    private int attackSpeed;
    private int maxHit;
    private int coolDownTimer;
    private int hitTimer;
    private int healthBarTimer;
    private int lastHit;
    private boolean dying;
    private boolean attacking;
    private boolean chasing;
    private boolean krakenBossBite;
    private boolean aggressive;
    private boolean stationary;
    private boolean regionNPC;

    public NPCCharacter(NPCType npcType, NPCSize npcSize, RuneLiteObject runeLiteObject, Model projectileModel, Animation idleAnimation, Animation walkAnimation, Animation attackAnimation, Animation deathAnimation, int attackAnimFrames, Animation projectileAnimation, int maxHealth, int attackRange, int chaseRange, int maxHit, int attackSpeed, boolean aggressive, boolean stationary, boolean regionNPC)
    {
        this.npcType = npcType;
        this.runeLiteObject = runeLiteObject;
        this.npcSize = npcSize;
        this.projectileModel = projectileModel;
        this.idleAnimation = idleAnimation;
        this.walkAnimation = walkAnimation;
        this.attackAnimation = attackAnimation;
        this.deathAnimation = deathAnimation;
        this.attackAnimFrames = attackAnimFrames;
        this.projectileAnimation = projectileAnimation;
        this.currentHealth = maxHealth;
        this.maxHealth = maxHealth;
        this.attackRange = attackRange;
        this.chaseRange = chaseRange;
        this.maxHit = maxHit;
        this.attackSpeed = attackSpeed;
        this.coolDownTimer = 0;
        this.hitTimer = 0;
        this.healthBarTimer = 0;
        this.lastHit = 0;
        this.dying = false;
        this.attacking = false;
        this.chasing = false;
        this.krakenBossBite = false;
        this.aggressive = aggressive;
        this.stationary = stationary;
        this.regionNPC = regionNPC;
    }
}
