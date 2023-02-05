package com.sailingskill;

import net.runelite.api.*;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class ModelHandler
{
    @Inject
    private Client client;

    public Model shipModel0;
    public Model shipModel0Half;
    public Model shipModelP45;
    public Model shipModelP90;
    public Model shipModelN45;
    public Model shipModelN90;
    public Model npcShipModel;
    public Model cannonballModel;

    public Model mossyRockSmall;
    public Model mossyRockLarge;
    public Model mossyRockTriple;
    public Model greyRockLarge;
    public Model greyRockSmall;
    public Model emptyModel;
    public Model flagModel;
    public Model fireModel;

    public Model basalt1;
    public Model basalt2;
    public Model basalt3;
    public Model basalt4;
    public Model basalt5;
    public Model basalt6;
    public Model basalt7;
    public Model basalt8;

    public Model fishModel;
    public Model fishSpace;

    public Model krakenModel;
    public Model krakenGiantModel;
    public Model krakenTentacleModel;
    public Model krakenProjectileModel;
    public Model krakenBossProjectileModel;

    public Model gullModel;
    public Model crocodileModel;

    public Animation boatIdleAnimation;
    public Animation greyRockAnimation;
    public Animation flagAnimation;
    public Animation boatDeathAnimation;
    public Animation fireAnimation;
    public Animation fishAnimation;
    public Animation krakenIdleAnimation;
    public Animation krakenBiteAnimation;
    public Animation krakenSpellAnimation;
    public Animation krakenDeathAnimation;
    public Animation tentacleIdleAnimation;
    public Animation tentacleAttackAnimation;
    public Animation tentacleDeathAnimation;
    public Animation krakenProjectileAnimation;
    public Animation krakenBossProjectileAnimation;
    public Animation gullIdleAnimation;
    public Animation gullDeathAnimation;
    public Animation crocodileIdleAnimation;
    public Animation crocodileWalkAnimation;
    public Animation crocodileAttackAnimation;
    public Animation crocodileDeathAnimation;

    private final int SHIP_MODEL = 8694;
    private final int SAIL_MODEL = 8695;
    private final int CANNONBALL_MODEL = 2846;
    private final int KRAKEN_PROJECTILE_MODEL = 7412;
    private final int MOSSY_ROCK_SMALL = 1517;
    private final int MOSSY_ROCK_LARGE = 1434;
    private final int MOSSY_ROCK_TRIPLE = 1518;
    private final int GREY_ROCK_LARGE = 5514;
    private final int GREY_ROCK_SMALL = 5515;
    private final int BLOCK = 1116;
    private final int FLAG = 2281;
    private final int FIRE = 26582;

    private final int BOAT_IDLE_ANIMATION = 2346;
    private final int GREY_ROCK_ANIMATION = 1727;
    private final int FLAG_ANIMATION = 471;
    private final int BOAT_DEATH_ANIMATION = 6645;
    private final int FIRE_ANIMATION = 6645;
    private final int FISH_ANIMATION = 5012;

    private final int BASALT_1 = 4548;
    private final int BASALT_2 = 4549;
    private final int BASALT_3 = 4550;
    private final int BASALT_4 = 4551;
    private final int BASALT_5 = 4552;
    private final int BASALT_6 = 4553;
    private final int BASALT_7 = 4554;
    private final int BASALT_8 = 4555;

    private final int KRAKEN = 28231;
    private final int KRAKEN_MEGA = 14920;
    private final int KRAKEN_IDLE_ANIMATION = 3989;
    private final int KRAKEN_BITE_ANIMATION = 3991;
    private final int KRAKEN_SPELL_ANIMATION = 3992;
    private final int KRAKEN_DEATH_ANIMATION = 3993;
    private final int KRAKEN_TENTACLE_MODEL = 13201;
    private final int TENTACLE_IDLE_ANIMATION = 3617;
    private final int TENTACLE_ATTACK_ANIMATION = 3618;
    private final int TENTACLE_DEATH_ANIMATION = 3620;
    private final int KRAKEN_PROJECTILE_ANIMATION = 34253;
    private final int KRAKEN_BOSS_PROJECTILE_MODEL = 26584;
    private final int KRAKEN_BOSS_PROJECTILE_ANIMATION = 6646;

    private final int FISH_MODEL = 19128;
    private final int FISH_RIPPLE = 19126;

    private final int GULL_MODEL_1 = 26841;
    private final int GULL_MODEL_2 = 26844;
    private final int GULL_IDLE_ANIMATION = 6771;
    private final int GULL_DEATH_ANIMATION = 6777;

    private final int CROCODILE_MODEL = 6454;
    private final int CROCODILE_IDLE_ANIMATION = 2037;
    private final int CROCODILE_WALK_ANIMATION = 2036;
    private final int CROCODILE_ATTACK_ANIMATION = 2039;
    private final int CROCODILE_DEATH_ANIMATION = 2038;

    public void loadModels()
    {
        ModelData shipBoat0 = client.loadModelData(SHIP_MODEL).cloneVertices().scale(80, 100, 70).rotateY270Ccw();
        ModelData shipSail0 = loadModel(SAIL_MODEL, ModelSource.sail0);
        ModelData modelData0 = client.mergeModels(shipBoat0, shipSail0);
        shipModel0 = modelData0.light();

        ModelData shipBoatP45 = client.loadModelData(SHIP_MODEL).cloneVertices().scale(80, 100, 70).rotateY270Ccw();
        ModelData shipSailP45 = loadModel(SAIL_MODEL, ModelSource.sailP45);
        ModelData modelDataP45 = client.mergeModels(shipBoatP45, shipSailP45);
        shipModelP45 = modelDataP45.light();

        ModelData shipBoatP90 = client.loadModelData(SHIP_MODEL).cloneVertices().scale(80, 100, 70).rotateY270Ccw();
        ModelData shipSailP90 = loadModel(SAIL_MODEL, ModelSource.sailP90);
        ModelData modelDataP90 = client.mergeModels(shipBoatP90, shipSailP90);
        shipModelP90 = modelDataP90.light();

        ModelData shipBoatN45 = client.loadModelData(SHIP_MODEL).cloneVertices().scale(80, 100, 70).rotateY270Ccw();
        ModelData shipSailN45 = loadModel(SAIL_MODEL, ModelSource.sailN45);
        ModelData modelDataN45 = client.mergeModels(shipBoatN45, shipSailN45);
        shipModelN45 = modelDataN45.light();

        ModelData shipBoatN90 = client.loadModelData(SHIP_MODEL).cloneVertices().scale(80, 100, 70).rotateY270Ccw();
        ModelData shipSailN90 = loadModel(SAIL_MODEL, ModelSource.sailN90);
        ModelData modelDataN90 = client.mergeModels(shipBoatN90, shipSailN90);
        shipModelN90 = modelDataN90.light();

        ModelData npcShip = client.loadModelData(SHIP_MODEL).cloneVertices().scale(80, 100, 70).rotateY270Ccw();
        ModelData npcSail = client.loadModelData(SAIL_MODEL).cloneVertices().cloneColors().scale(100, 120, 90).rotateY90Ccw();
        short sailColour = npcSail.getFaceColors()[0];
        short sailReplaceColour = JagexColor.packHSL(55, 0, 40);
        npcSail.recolor(sailColour, sailReplaceColour);
        ModelData modelDataNPC = client.mergeModels(npcShip, npcSail);
        npcShipModel = modelDataNPC.light();

        ModelData cannonModel = client.loadModelData(CANNONBALL_MODEL).cloneVertices().cloneColors();
        short cannonColour = cannonModel.getFaceColors()[0];
        short replaceColour = JagexColor.packHSL(5, 0, 10);
        cannonModel.scale(25, 25, 25).translate(0, 0, 50).recolor(cannonColour, replaceColour);
        cannonballModel = cannonModel.light();

        mossyRockSmall = client.loadModel(MOSSY_ROCK_SMALL);
        ModelData mossyRockLargeMD = client.loadModelData(MOSSY_ROCK_LARGE).cloneVertices();
        mossyRockLarge = mossyRockLargeMD.scale(180, 180, 180).translate(20, 0, -50).light();

        ModelData mossyRockTripleMD = client.loadModelData(MOSSY_ROCK_TRIPLE).cloneVertices();
        mossyRockTriple = mossyRockTripleMD.scale(80, 80, 80).light();

        ModelData blockMD = client.loadModelData(BLOCK).cloneVertices();
        emptyModel = blockMD.scale(1, 1, 1).getModel();

        greyRockLarge = client.loadModel(GREY_ROCK_LARGE);
        greyRockSmall = client.loadModel(GREY_ROCK_SMALL);

        ModelData flagMD = client.loadModelData(FLAG).cloneVertices().cloneColors();
        short flagWhiteColour = flagMD.getFaceColors()[19];
        short flagReplaceColour = JagexColor.packHSL(5, 6, 10);
        flagModel = flagMD.scale(70, 60, 70).recolor(flagWhiteColour, flagReplaceColour).light();

        fireModel = client.loadModel(FIRE);

        ModelData fishModelData = client.loadModelData(FISH_MODEL);
        ModelData fishRippleData = client.loadModelData(FISH_RIPPLE);
        fishModel = client.mergeModels(fishModelData, fishRippleData).light();

        ModelData fishSpaceMD = client.loadModelData(BLOCK).cloneVertices();
        fishSpace = fishSpaceMD.scale(1, 1, 1).light();

        basalt1 = client.loadModel(BASALT_1);
        basalt2 = client.loadModel(BASALT_2);
        basalt3 = client.loadModel(BASALT_3);
        basalt4 = client.loadModel(BASALT_4);
        basalt5 = client.loadModel(BASALT_5);
        basalt6 = client.loadModel(BASALT_6);
        basalt7 = client.loadModel(BASALT_7);
        basalt8 = client.loadModel(BASALT_8);

        ModelData krakenMD = client.loadModelData(KRAKEN).cloneVertices();
        krakenModel = krakenMD.scale(64, 64, 64).translate(0, 0, 40).light();

        ModelData krakenGiantMD = client.loadModelData(KRAKEN_MEGA).cloneVertices();
        krakenGiantModel = krakenGiantMD.scale(640, 640, 640).light();

        ModelData krakenTentacleMD = client.loadModelData(KRAKEN_TENTACLE_MODEL).cloneVertices().cloneColors();
        krakenTentacleModel = krakenTentacleMD.scale(700, 700, 700).light();

        ModelData krakenProjectile = client.loadModelData(KRAKEN_PROJECTILE_MODEL).cloneVertices().cloneColors();
        short krakenProjectileColour = krakenProjectile.getFaceColors()[0];
        short krakenProjectileReplaceColour = JagexColor.packHSL(31, 5, 80);
        krakenProjectileModel = krakenProjectile.scale(64, 64, 64).recolor(krakenProjectileColour, krakenProjectileReplaceColour).light();

        ModelData krakenBossProjectileMD = client.loadModelData(KRAKEN_BOSS_PROJECTILE_MODEL).cloneVertices();
        krakenBossProjectileModel = krakenBossProjectileMD.scale(300, 300, 300).light(100, ModelData.DEFAULT_CONTRAST, ModelData.DEFAULT_X, ModelData.DEFAULT_Y, ModelData.DEFAULT_Z);

        ModelData gullMD1 = client.loadModelData(GULL_MODEL_1);
        ModelData gullMD2 = client.loadModelData(GULL_MODEL_2);
        gullModel = client.mergeModels(gullMD1, gullMD2).light();

        ModelData crocodileMD = client.loadModelData(CROCODILE_MODEL).cloneVertices();
        crocodileModel = crocodileMD.scale(180, 180, 180).light();

        boatIdleAnimation = client.loadAnimation(BOAT_IDLE_ANIMATION);
        greyRockAnimation = client.loadAnimation(GREY_ROCK_ANIMATION);
        flagAnimation = client.loadAnimation(FLAG_ANIMATION);
        boatDeathAnimation = client.loadAnimation(BOAT_DEATH_ANIMATION);
        fireAnimation = client.loadAnimation(FIRE_ANIMATION);
        fishAnimation = client.loadAnimation(FISH_ANIMATION);
        krakenIdleAnimation = client.loadAnimation(KRAKEN_IDLE_ANIMATION);
        krakenSpellAnimation = client.loadAnimation(KRAKEN_SPELL_ANIMATION);
        krakenBiteAnimation = client.loadAnimation(KRAKEN_BITE_ANIMATION);
        krakenDeathAnimation = client.loadAnimation(KRAKEN_DEATH_ANIMATION);
        tentacleIdleAnimation = client.loadAnimation(TENTACLE_IDLE_ANIMATION);
        tentacleAttackAnimation = client.loadAnimation(TENTACLE_ATTACK_ANIMATION);
        tentacleDeathAnimation = client.loadAnimation(TENTACLE_DEATH_ANIMATION);
        krakenProjectileAnimation = client.loadAnimation(KRAKEN_PROJECTILE_ANIMATION);
        krakenBossProjectileAnimation = client.loadAnimation(KRAKEN_BOSS_PROJECTILE_ANIMATION);
        gullIdleAnimation = client.loadAnimation(GULL_IDLE_ANIMATION);
        gullDeathAnimation = client.loadAnimation(GULL_DEATH_ANIMATION);
        crocodileIdleAnimation = client.loadAnimation(CROCODILE_IDLE_ANIMATION);
        crocodileWalkAnimation = client.loadAnimation(CROCODILE_WALK_ANIMATION);
        crocodileAttackAnimation = client.loadAnimation(CROCODILE_ATTACK_ANIMATION);
        crocodileDeathAnimation = client.loadAnimation(CROCODILE_DEATH_ANIMATION);
    }


    public ModelData loadModel(int modelId, ModelSource modelSource)
    {
        ModelData modelData = client.loadModelData(modelId);
        int[] verticesX = modelData.getVerticesX();
        int[] verticesY = modelData.getVerticesY();
        int[] verticesZ = modelData.getVerticesZ();

        int[] faces1 = modelData.getFaceIndices1();
        int[] faces2 = modelData.getFaceIndices2();
        int[] faces3 = modelData.getFaceIndices3();
        short[] faceColours = modelData.getFaceColors();

        try
        {
            URL vertexURL = new URL(modelSource.vertexLink);
            URLConnection vertexConnection = vertexURL.openConnection();
            BufferedReader inVertex = new BufferedReader(new InputStreamReader(vertexConnection.getInputStream()));
            int vertIteration = 0;
            String lineVertex = "";

            while ((lineVertex = inVertex.readLine()) != null)
            {
                String[] vertex = lineVertex.split(",");
                int vertX = (int) (-1 * Double.parseDouble(vertex[0]));
                verticesX[vertIteration] = vertX;
                int vertY = (int) (Double.parseDouble(vertex[1]));
                verticesZ[vertIteration] = vertY;
                int vertZ = (int) (-1 * Double.parseDouble(vertex[2]));
                verticesY[vertIteration] = vertZ;
                vertIteration++;
            }
            inVertex.close();

            URL faceURL = new URL(modelSource.faceLink);
            URLConnection faceConnection = faceURL.openConnection();
            BufferedReader inFace = new BufferedReader(new InputStreamReader(faceConnection.getInputStream()));
            int faceIteration = 0;
            String lineFace = "";

            while ((lineFace = inFace.readLine()) != null)
            {
                String[] face = lineFace.split(",");
                int face1 = Integer.parseInt(face[0]);
                faces1[faceIteration] = face1;
                int face2 = Integer.parseInt(face[1]);
                faces3[faceIteration] = face2;
                int face3 = Integer.parseInt(face[2]);
                faces2[faceIteration] = face3;
                faceIteration++;
            }
            inFace.close();

            URL colourURL = new URL(modelSource.colourLink);
            URLConnection colourConnection = colourURL.openConnection();
            BufferedReader inColour = new BufferedReader(new InputStreamReader(colourConnection.getInputStream()));
            int colourIteration = 0;
            String lineColour = "";

            while ((lineColour = inColour.readLine()) != null)
            {
                String[] colour = lineColour.split(",");
                double colourH = Double.parseDouble(colour[0]);
                int colourHInt = (int) (63 - (colourH * 63));
                double colourL = Double.parseDouble(colour[1]);
                int colourLInt = (int) (colourL * 127);
                double colourS = Double.parseDouble(colour[2]);
                int colourSInt = (int) (colourS * 7);

                short jagexColor = JagexColor.packHSL(colourHInt, colourSInt, colourLInt);
                faceColours[colourIteration] = jagexColor;
                colourIteration++;
            }
            inColour.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return modelData;
    }
}
