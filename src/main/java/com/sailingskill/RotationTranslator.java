package com.sailingskill;

public class RotationTranslator
{
    public static int translateOrientation(int rotation)
    {
        if ((rotation > -128 && rotation <= 128) || (rotation > 1920 && rotation <= 2176))
        {
            return 0;
        }

        if ((rotation > 128 && rotation <= 384) || (rotation > 2176 && rotation <= 2432))
        {
            return 256;
        }

        if (rotation > 384 && rotation <= 640)
        {
            return 512;
        }

        if (rotation > 640 && rotation <= 896)
        {
            return 768;
        }

        if (rotation > 896 && rotation <= 1152)
        {
            return 1024;
        }

        if (rotation > 1152 && rotation <= 1408)
        {
            return 1280;
        }

        if (rotation > 1408 && rotation <= 1664)
        {
            return 1536;
        }

        if ((rotation > 1664 && rotation <= 1920) || (rotation > -384 && rotation <= -128))
        {
            return 1792;
        }

        System.out.println("Error: Rotation Translation Failed: rotation: " + rotation);
        return 0;
    }
}
