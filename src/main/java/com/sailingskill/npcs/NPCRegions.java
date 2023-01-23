package com.sailingskill.npcs;

import lombok.Getter;

@Getter
public enum NPCRegions
{
    RIMMINGTON(1, 0, 3, 11570, 11569, 11568, 11826, 11825, 11824, 12082, 12081, 12080),
    CATHERBY(2, 4, 51, 10806, 10805, 10804, 11062, 11061, 11060, 11318, 11317, 11316),
    KOUREND_WOODLAND(3, 52, 99, 5942, 6198, 6454, 5941, 6197, 6453),
    BRIMHAVEN(4, 100, 107, 10803, 10802, 10801, 11059, 11058, 11057, 11315, 11314, 11313),
    PIRATES_COVE(5, 108, 123, 10029, 10028, 10027, 10285, 10284, 10283, 10541, 10540, 10539)

    ;

    private final int trialId;
    private final int startNPC;
    private final int endNPC;
    private final int[] regionArray;

    NPCRegions(int trialId, int startTrialObject, int endTrialObject, int... regionId)
    {
        this.trialId = trialId;
        this.startNPC = startTrialObject;
        this.endNPC = endTrialObject;
        this.regionArray = regionId;
    }
}
