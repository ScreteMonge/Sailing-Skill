package com.sailingskill.npcs;

import lombok.Getter;

public enum NPCSize
{
    SMALL(0),
    PLUS(128),
    LARGE(182),
    HUGE(222),
    GIGANTIC(1200)
    ;

    @Getter
    private final int hitboxRadius;

    NPCSize(int radius)
    {
        this.hitboxRadius = radius;
    }
}
