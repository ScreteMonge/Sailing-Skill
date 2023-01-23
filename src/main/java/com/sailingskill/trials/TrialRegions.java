package com.sailingskill.trials;

import lombok.Getter;

@Getter
public enum TrialRegions {
    LUMBRIDGE_SWAMP(1, 0,88, 12337, 12593),
    LIGHTHOUSE(2, 88, 325, 10040, 10296, 10039),
    KOUREND_WOODLAND(3, 325, 370, 5942, 6198, 6454, 5941, 6197, 6453),
    MUDSKIPPER_POINT(4, 370, 415, 11569, 11568, 11567, 11825, 11824, 11823, 12081, 12080, 12079),
    CATHERBY(5, 415, 460, 10806, 10805, 10804, 11062, 11061, 11060, 11318, 11317, 11316),
    ;

    private final int trialId;
    private final int startTrialObject;
    private final int endTrialObject;
    private final int[] regionArray;

    TrialRegions(int trialId, int startTrialObject, int endTrialObject, int... regionId)
    {
        this.trialId = trialId;
        this.startTrialObject = startTrialObject;
        this.endTrialObject = endTrialObject;
        this.regionArray = regionId;
    }
}
