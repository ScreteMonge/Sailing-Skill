package com.sailingskill;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.sailingskill.combat.CannonRange;
import com.sailingskill.combat.HitSplat;
import com.sailingskill.combat.Projectile;
import com.sailingskill.npcs.*;
import com.sailingskill.overlays.*;
import com.sailingskill.trials.TrialObject;
import com.sailingskill.trials.TrialRegions;
import com.sailingskill.widgets.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

@Slf4j
@Getter
@Setter
@PluginDescriptor(
	name = "Sailing",
	description = "Allows you to test some basic mechanics of a Sailing skill",
	tags = {"sailing", "skill", "game mode", "gamemode"}
)
public class SailingPlugin extends Plugin implements KeyListener
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private SailingConfig config;

	@Inject
	private SpriteManager spriteManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private CannonOverlay cannonOverlay;

	@Inject
	private GuideOverlay guideOverlay;

	@Inject
	private HitsplatOverlay hitsplatOverlay;

	@Inject
	private XPDropOverlay xpDropOverlay;

	@Inject
	private FishOverlay fishOverlay;

	@Inject
	private ModelHandler modelHandler;

	@Inject
	private WidgetSetter widgetSetter;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ChatMessageManager chatMessageManager;

	private GameClientLayout currentGameClientLayout;
	private int currentTrial = -1;
	private int currentNPCSpawns = -1;
	private boolean modelsLoaded;
	private Model shipModel0;
	private Model shipModelN45;
	private Model shipModelN90;
	private Model shipModelP45;
	private Model shipModelP90;
	private Tile lastSelectedTile;

	private RuneLiteObject shipObject;
	private int buildBoatTimer = 0;
	private int BUILD_BOAT_TIMER_START = 10;
	private boolean anchorMode = true;
	private boolean showCannonRange = false;
	private ArrayList<Tile> cannonTiles;
	private boolean enableKeyboardControl = true;
	private boolean fireCannon = false;
	private int cannonCoolDown = 0;
	private boolean fishing = false;

	private final Random random = new Random();
	private int speed = 1;
	private int boatMomentum = 0;
	private int windDirection = 0;
	private int absoluteBoatOrientation;
	private int sailLength;
	private int boatRotationQueue = 0;
	private int sailLengthQueue = 0;
	private int windChangeTimer = 0;
	private int healthBarTimer = 0;
	private int currentHealth = 0;
	private int maxHealth = 0;
	private boolean deathState = false;
	private BoatTool currentBoatTool = BoatTool.CANNON;
	private int xpDropAccumulator = 0;
	private final int WATER_OVERLAY_ID = 6;
	private final int REGION_PIRATES_COVE = 5;
	private final int PROJECTILE_HIT_RANGE = 6;

	private final ArrayList<RuneLiteObject> configObjects = new ArrayList<>();
	private final ArrayList<RuneLiteObject> fishObjects = new ArrayList<>();
	private final ArrayList<NPCCharacter> npcCharacters = new ArrayList<>();
	private final ArrayList<NPCCharacter> regionNPCs = new ArrayList<>();
	private final ArrayList<Projectile> activeProjectiles = new ArrayList<>();
	private final ArrayList<HitSplat> hitSplats = new ArrayList<>();
	private final ArrayList<XPDrop> xpSailingDrops = new ArrayList<>();
	private final ArrayList<XPDrop> xpCannonDrops = new ArrayList<>();
	private final ArrayList<XPDrop> xpFishDrops = new ArrayList<>();
	private final ArrayList<FishDrop> fishDrops = new ArrayList<>();

	private Model model;
	private int vertexFileSize;
	private int faceFileSize;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(cannonOverlay);
		overlayManager.add(guideOverlay);
		overlayManager.add(hitsplatOverlay);
		overlayManager.add(xpDropOverlay);
		overlayManager.add(fishOverlay);
		keyManager.registerKeyListener(this);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(cannonOverlay);
		overlayManager.remove(guideOverlay);
		overlayManager.remove(hitsplatOverlay);
		overlayManager.remove(xpDropOverlay);
		overlayManager.remove(fishOverlay);
		clientThread.invoke(this::despawnBoat);
		clientThread.invoke(this::clearTrialSet);
		currentTrial = 0;
		currentNPCSpawns = 0;
		keyManager.unregisterKeyListener(this);
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		updateProjectilePosition();
		updatePlayerHitSplats();

		for (NPCCharacter npc : npcCharacters)
		{
			RuneLiteObject runeLiteObject = npc.getRuneLiteObject();

			if (runeLiteObject.getAnimation() == npc.getAttackAnimation() && runeLiteObject.getAnimationFrame() == npc.getAttackAnimFrames())
			{
				runeLiteObject.setAnimation(npc.getIdleAnimation());
			}

			if (runeLiteObject.getAnimation() == modelHandler.krakenBiteAnimation && runeLiteObject.getAnimationFrame() == 9)
			{
				runeLiteObject.setAnimation(npc.getIdleAnimation());
			}
		}

		for (int i = 0; i < npcCharacters.size(); i++)
		{
			NPCCharacter npc = npcCharacters.get(i);
			RuneLiteObject runeLiteObject = npc.getRuneLiteObject();
			if (!runeLiteObject.isActive())
			{
				npc.setHealthBarTimer(0);
				npcCharacters.remove(npc);
			}
		}

		updateNPCOrientation();

		if (deathState && !shipObject.isActive())
		{
			despawnBoat();
			deathState = false;
			return;
		}

		if (shipObject == null || speed == 0 || deathState)
		{
			boatMomentum = 0;
			return;
		}

		int finalSpeed = speed;
		if (boatMomentum > 0)
		{
			boatMomentum--;
			finalSpeed++;
		}

		int orientation = BoatMaths.translateOrientation(absoluteBoatOrientation);
		int currentSceneX = shipObject.getLocation().getSceneX();
		int currentSceneY = shipObject.getLocation().getSceneY();

		int[] bestAvailableWaterTile = checkTileCollision(finalSpeed);
		if (currentSceneX == bestAvailableWaterTile[0] && currentSceneY == bestAvailableWaterTile[1])
		{
			boatMomentum = 0;
			return;
		}

		int tickMod = 30 / finalSpeed;

		if ((client.getGameCycle() % tickMod) == 0)
		{
			int modifierX = getXMovement(orientation);
			int modifierZ = getYMovement(orientation);
			LocalPoint localPoint = LocalPoint.fromScene(currentSceneX + modifierX, currentSceneY + modifierZ);
			shipObject.setLocation(localPoint, 0);
		}

		xpDropAccumulator++;
	}

	/*
	Updates the position of each active projectile to close in on its target
	If the projectile is close enough to its target, set a hitsplat and healthbar to appear
	If the damage is greater than health remaining, set the target's death state
	*/
	public void updateProjectilePosition()
	{
		for (int i = 0; i < activeProjectiles.size(); i++)
		{
			Projectile projectile = activeProjectiles.get(i);
			projectile.setTimer(projectile.getTimer() + 1);
			RuneLiteObject projectileObject = projectile.getProjectile();

			if (projectileObject == null)
			{
				continue;
			}

			if (projectile.isTargetsPlayer())
			{
				int currentProjectileX = projectileObject.getLocation().getX();
				int currentProjectileY = projectileObject.getLocation().getY();

				int boatX = shipObject.getLocation().getX();
				int boatY = shipObject.getLocation().getY();

				int xDifference = boatX - currentProjectileX;
				int yDifference = boatY - currentProjectileY;

				if (Math.abs(xDifference) < PROJECTILE_HIT_RANGE && Math.abs(yDifference) < PROJECTILE_HIT_RANGE)
				{
					projectileObject.setActive(false);
					activeProjectiles.remove(projectile);

					if (deathState)
					{
						continue;
					}

					int damageRoll = random.nextInt(projectile.getMaxHit()) + 1;
					double speedDefenseMod = (double) (speed - 1) / 10;
					damageRoll -= (int) (damageRoll * speedDefenseMod);

					int nextHealth = currentHealth - damageRoll;

					int hitSplatPosition = hitSplats.size() + 1;

					if (hitSplats.size() > 4)
					{
						HitSplat lastHitSplat = hitSplats.get(0);
						for (HitSplat hitSplat : hitSplats)
						{
							if (hitSplat.getHitTimer() > lastHitSplat.getHitTimer())
							{
								lastHitSplat = hitSplat;
								hitSplatPosition = hitSplat.getSplatPosition();
							}
						}
					}

					HitSplat hitSplat = new HitSplat(damageRoll, 80, hitSplatPosition);
					hitSplats.add(0, hitSplat);

					healthBarTimer = 400;

					if (nextHealth <= 0)
					{
						hitSplat.setHitValue(currentHealth);
						currentHealth = 0;

						shipObject.setShouldLoop(false);
						shipObject.setAnimation(modelHandler.fireAnimation);
						RuneLiteObject fire = client.createRuneLiteObject();
						fire.setModel(modelHandler.fireModel);
						fire.setAnimation(modelHandler.fireAnimation);
						fire.setLocation(shipObject.getLocation(), 0);
						fire.setActive(true);
						deathState = true;
					}
					else
					{
						currentHealth = nextHealth;
					}

				}
				else
				{
					int newX = (xDifference * projectile.getTimer() / 80) + currentProjectileX;
					int newY = (yDifference * projectile.getTimer() / 80) + currentProjectileY;
					projectileObject.setLocation(new LocalPoint(newX, newY), 0);
				}
			}

			if (!projectile.isTargetsPlayer())
			{
				NPCCharacter npc = projectile.getTarget();
				RuneLiteObject target = npc.getRuneLiteObject();

				if (!npc.isDying())
				{
					int currentProjectileX = projectileObject.getLocation().getX();
					int currentProjectileY = projectileObject.getLocation().getY();

					int currentTargetX = target.getLocation().getX();
					int currentTargetY = target.getLocation().getY();

					int xDifference = currentTargetX - currentProjectileX;
					int yDifference = currentTargetY - currentProjectileY;

					if (Math.abs(xDifference) < 6 && Math.abs(yDifference) < 6)
					{
						int currentHealth = npc.getCurrentHealth();
						int damageVariation = random.nextInt(projectile.getMaxHit()) + 5;
						int nextHealth = currentHealth - damageVariation;

						if (!npc.isDying())
						{
							npc.setLastHit(damageVariation);
							npc.setHitTimer(80);
							npc.setHealthBarTimer(400);
							npc.setCurrentHealth(nextHealth);

							if (nextHealth <= 0)
							{
								npc.setLastHit(currentHealth);
								target.setShouldLoop(false);
								target.setAnimation(npc.getDeathAnimation());

								if (npc.getNpcType() == NPCType.MERCHANT)
								{
									RuneLiteObject fire = client.createRuneLiteObject();
									fire.setModel(modelHandler.fireModel);
									fire.setAnimation(modelHandler.fireAnimation);
									fire.setLocation(target.getLocation(), 0);
									fire.setActive(true);
								}

								npc.setDying(true);
							}
						}

						projectileObject.setActive(false);
						activeProjectiles.remove(projectile);
					}
					else
					{
						int newX = (xDifference * projectile.getTimer() / 50) + currentProjectileX;
						int newY = (yDifference * projectile.getTimer() / 50) + currentProjectileY;
						projectileObject.setLocation(new LocalPoint(newX, newY), 0);
					}
				}
				else
				{
					projectileObject.setActive(false);
					activeProjectiles.remove(projectile);
				}
			}
		}
	}

	//Since the player's boat can receive multiple hits, remove hitsplats after their timer expires
	public void updatePlayerHitSplats()
	{
		for (int i = 0; i < hitSplats.size(); i++)
		{
			HitSplat hitSplat = hitSplats.get(i);
			if (hitSplat.getHitTimer() <= 0)
			{
				hitSplats.remove(hitSplat);
			}
		}
	}

	//Get NPC to always face the boat when it's in chasing range
	public void updateNPCOrientation()
	{
		for (NPCCharacter npc : npcCharacters)
		{
			if (npc.isDying() || !npc.isChasing())
			{
				continue;
			}

			RuneLiteObject runeLiteObject = npc.getRuneLiteObject();

			double currentShipX = shipObject.getLocation().getX();
			double currentShipY = shipObject.getLocation().getY();

			double currentNPCX = runeLiteObject.getLocation().getX();
			double currentNPCY = runeLiteObject.getLocation().getY();

			double xDifference = currentNPCX - currentShipX;
			double yDifference = currentNPCY - currentShipY;

			double angle = 0;

			double npcToShipAngle = Math.abs(Math.atan(yDifference / xDifference));

			if (xDifference > 0 && yDifference <= 0)
			{
				angle = npcToShipAngle + (3 * Math.PI / 2);
			}
			else if (xDifference >= 0 && yDifference > 0)
			{
				angle = Math.atan(xDifference / yDifference) + Math.PI;
			}
			else if (xDifference < 0 && yDifference >= 0)
			{
				angle = npcToShipAngle + (Math.PI / 2);
			}
			else if (xDifference <= 0 && yDifference < 0)
			{
				angle = Math.atan(xDifference / yDifference);
			}

			int nextOrientation = (int) ((angle * 1024 / Math.PI) - 1024);
			if (nextOrientation < 0)
			{
				nextOrientation += 2048;
			}
			runeLiteObject.setOrientation(nextOrientation);

		}
	}

	//If right-clicking over water, create a new menu with options to build your boat or spawn NPCs
	@Subscribe
	public void onMenuOpened(MenuOpened event)
	{
		if (event.getFirstEntry().getType().equals(MenuAction.WALK))
		{
			setWaterTileRightClick();
		}
	}

	//If the player's boat is spawned, set the combat tab to a sailing tab
	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == WidgetInfo.FIXED_VIEWPORT_COMBAT_TAB.getGroupId())
		{
			currentGameClientLayout = GameClientLayout.CLASSIC;
			if (shipObject == null)
			{
				return;
			}

			widgetSetter.setupFixedTab();
		}

		if (event.getGroupId() == WidgetInfo.RESIZABLE_VIEWPORT_COMBAT_TAB.getGroupId())
		{
			currentGameClientLayout = GameClientLayout.RESIZED;
			if (shipObject == null)
			{
				return;
			}

			widgetSetter.setupResizableTab();
		}

		if (event.getGroupId() == WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_COMBAT_ICON.getGroupId())
		{
			currentGameClientLayout = GameClientLayout.MODERN;
			if (shipObject == null)
			{
				return;
			}

			widgetSetter.setupModernTab();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			despawnBoat();
			deathState = false;
			return;
		}

		currentTrial = 0;
		currentNPCSpawns = 0;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!modelsLoaded)
		{
			modelHandler.loadModels();
			shipModel0 = modelHandler.shipModel0;
			shipModelN45 = modelHandler.shipModelN45;
			shipModelN90 = modelHandler.shipModelN90;
			shipModelP45 = modelHandler.shipModelP45;
			shipModelP90 = modelHandler.shipModelP90;
			modelsLoaded = true;
		}

		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		checkRegionChanges();

		if (buildBoatTimer > 0)
		{
			buildBoatTimer--;
			Player player = client.getLocalPlayer();
			if (buildBoatTimer == 0)
			{
				buildBoat();
				player.setIdlePoseAnimation(808);
			}
			else if (player.getIdlePoseAnimation() != 898)
			{
				player.setIdlePoseAnimation(898);
			}
		}

		if (shipObject != null)
		{
			updateBoatOrientation();
			updateSailLength();
			recalculateSpeed();
			//updateWindDirection();
			//updateBoatModel();
			updateCannonRange();
			fireCannon();
			checkXPDrop();
			checkFishingCatch();
			updateCamera();
			updateNPCAggression();
			fireNPCProjectiles();
		}

		updateNPCPositions();
	}

	//Determines if this is an appropriate region to spawn static objects (obstacle courses, fishing spots)
	public void checkRegionChanges()
	{
		Player player = client.getLocalPlayer();
		WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, player.getLocalLocation());
		int regionId = worldPoint.getRegionID();

		boolean withinTrialRegion = false;
		int startTrialObject = 0;
		int endTrialObject = 0;
		int nextTrial = 0;

		for (TrialRegions reg : TrialRegions.values())
		{
			for (int region : reg.getRegionArray())
			{
				if (regionId == region)
				{
					startTrialObject = reg.getStartTrialObject();
					endTrialObject = reg.getEndTrialObject();
					withinTrialRegion = true;
					nextTrial = reg.getTrialId();
					break;
				}
			}
		}

		if (withinTrialRegion && client.getPlane() == 0)
		{
			if (currentTrial != nextTrial)
			{
				currentTrial = nextTrial;
				deployTrialSet(startTrialObject, endTrialObject);
			}
		}
		else
		{
			currentTrial = 0;
			clearTrialSet();
		}

		boolean withinNPCRegion = false;
		int startNPC = 0;
		int endNPC = 0;
		int nextNPCSpawn = 0;

		for (NPCRegions reg : NPCRegions.values())
		{
			for (int region : reg.getRegionArray())
			{
				if (regionId == region)
				{
					startNPC = reg.getStartNPC();
					endNPC = reg.getEndNPC();
					withinNPCRegion = true;
					nextNPCSpawn = reg.getTrialId();
					break;
				}
			}
		}

		if (withinNPCRegion && client.getPlane() == 0)
		{
			if (currentNPCSpawns != nextNPCSpawn)
			{
				currentNPCSpawns = nextNPCSpawn;
				deployNPCSet(startNPC, endNPC);
			}
		}
		else
		{
			currentNPCSpawns = 0;
			clearRegionNPCSet();
		}
	}

	public void updateBoatOrientation()
	{
		if (boatRotationQueue > 0)
		{
			absoluteBoatOrientation += 256;
			if (absoluteBoatOrientation == 2048)
			{
				absoluteBoatOrientation = 0;
			}

			shipObject.setOrientation(absoluteBoatOrientation);
			boatRotationQueue--;
		}

		if (boatRotationQueue < 0)
		{
			absoluteBoatOrientation -= 256;
			if (absoluteBoatOrientation == -256)
			{
				absoluteBoatOrientation = 1792;
			}
			shipObject.setOrientation(absoluteBoatOrientation);
			boatRotationQueue++;
		}
	}

	public void updateSailLength()
	{
		int newSailLength = sailLength + sailLengthQueue;
		sailLengthQueue = 0;

		if (newSailLength < 0)
		{
			sailLength = 0;
			anchorMode = true;
			return;
		}

		sailLength = Math.min(newSailLength, 3);

		anchorMode = false;
	}

	public void recalculateSpeed()
	{
		if (shipObject == null)
		{
			return;
		}

		if (anchorMode || sailLength == 0)
		{
			speed = 0;
			boatMomentum = 0;
			return;
		}

		/*
		int boatOrientation = BoatMaths.translateOrientation(absoluteBoatOrientation);
		int boatDifference = Math.abs(windDirection - boatOrientation);

		int boatDirectionSpeed;
		switch (boatDifference)
		{
			default:
			case 0:
			case 256:
			case 1736:
				boatDirectionSpeed = 1;
				break;
			case 512:
			case 1536:
				boatDirectionSpeed = 0;
				break;
			case 768:
			case 1280:
				boatDirectionSpeed = -1;
				break;
			case 1024:
				boatDirectionSpeed = -2;
		}

		int newSpeed = sailLength + boatDirectionSpeed;
		if (newSpeed < 1 || sailLength == 1)
		{
			newSpeed = 1;
		}

		 */

		int newSpeed = sailLength;

		if (speed > newSpeed)
		{
			boatMomentum = 30;
		}

		speed = newSpeed;
	}

	public void updateWindDirection()
	{
		windChangeTimer++;
		if (windChangeTimer >= config.windChangeRate())
		{
			int roll = random.nextInt(8) * 256;
			while (roll == windDirection)
			{
				roll = random.nextInt(8) * 256;
			}
			windDirection = roll;
			windChangeTimer = 0;
		}
	}

	public void updateBoatModel()
	{
		int currentBoatOrientation = BoatMaths.translateOrientation(absoluteBoatOrientation);
		int difference = BoatMaths.boundOrientation(currentBoatOrientation - windDirection) / 256;
		Model lastModel = shipObject.getModel();

		switch (difference)
		{
			case 0:
				shipObject.setModel(shipModel0);
				break;
			case 1:
				shipObject.setModel(shipModelN45);
				break;
			case 2:
			case 3:
				shipObject.setModel(shipModelN90);
				break;
			case 4:
				if (lastModel == shipModelN45 || lastModel == shipModelN90 || lastModel == shipModel0)
				{
					shipObject.setModel(shipModelN90);
					break;
				}

				shipObject.setModel(shipModelP90);
				break;
			case 5:
			case 6:
				shipObject.setModel(shipModelP90);
				break;
			case 7:
				shipObject.setModel(shipModelP45);
		}
	}

	public void updateCannonRange()
	{
		int currentX = shipObject.getLocation().getSceneX();
		int currentY = shipObject.getLocation().getSceneY();

		int direction = BoatMaths.translateOrientation(absoluteBoatOrientation);
		int directionX = 0;
		int directionY = 0;

		switch (direction)
		{
			default:
			case 0:
				directionY--;
				break;
			case 256:
				directionY--;
				directionX--;
				break;
			case 512:
				directionX--;
				break;
			case 768:
				directionY++;
				directionX--;
				break;
			case 1024:
				directionY++;
				break;
			case 1280:
				directionY++;
				directionX++;
				break;
			case 1536:
				directionX++;
				break;
			case 1792:
				directionX++;
				directionY--;
		}

		int[] cannonRangeX = CannonRange.getCannonX(direction);
		int[] cannonRangeY = CannonRange.getCannonY(direction);

		int finalSpeed = speed;
		if (boatMomentum > 0)
		{
			finalSpeed++;
		}

		if (anchorMode)
		{
			finalSpeed = 0;
		}

		int changeX = directionX * finalSpeed;
		int changeY = directionY * finalSpeed;
		cannonTiles = getTiles(currentX + changeX, currentY + changeY, cannonRangeX, cannonRangeY);
	}

	public void fireCannon()
	{
		if (cannonCoolDown > 0)
		{
			cannonCoolDown--;
			fireCannon = false;
			return;
		}

		if (deathState)
		{
			return;
		}

		if (fireCannon)
		{
			boolean targetExists = false;
			fireCannon = false;
			for (NPCCharacter npc : npcCharacters)
			{
				if (npc.isDying())
				{
					continue;
				}

				RuneLiteObject runeLiteObject = npc.getRuneLiteObject();

				for (Tile tile : cannonTiles)
				{
					LocalPoint tileLocation = tile.getLocalLocation();
					LocalPoint npcLocation = runeLiteObject.getLocation();

					if (tileLocation.distanceTo(npcLocation) <= npc.getNpcSize().getHitboxRadius())
					{
						targetExists = true;
						spawnProjectile(npc, shipTypeMaxHit(), shipObject.getLocation(), modelHandler.cannonballModel, null, false);
						break;
					}
				}
			}

			if (targetExists)
			{
				XPDrop xpDrop = new XPDrop(false, true, 325, 350);
				xpCannonDrops.add(xpDrop);
				cannonCoolDown = 1;
			}
			else
			{
				sendChatMessage("There are no valid targets to fire on!");
			}

			//reintroduce cannoncooldown even on a miss?
		}
	}

	//Once enough sailing time has occurred, draw xp drop
	public void checkXPDrop()
	{
		if (xpDropAccumulator >= 120)
		{
			xpDropAccumulator = 0;
			XPDrop xpDrop = new XPDrop(false, false, 325, 350);
			xpSailingDrops.add(xpDrop);
		}
	}

	//Checks if it is valid for a boat to start fishing and randomly allows a fish to be caught depending on region
	public void checkFishingCatch()
	{
		if (!fishing || !validFishingTile())
		{
			fishing = false;
			return;
		}

		if (client.getTickCount() % 12 > 0)
		{
			return;
		}

		FishDrop fishDrop;
		xpFishDrops.add(new XPDrop(false, true, 325, 350));

		switch (currentTrial)
		{
			default:
			case 3:
				sendChatMessage("You catch a marlin.");
				fishDrop = new FishDrop(1, 100);
				fishDrops.add(fishDrop);
				break;
			case 4:
				sendChatMessage("You catch a grouper.");
				fishDrop = new FishDrop(2, 100);
				fishDrops.add(fishDrop);
				break;
			case 5:
				sendChatMessage("You catch a sturgeon.");
				fishDrop = new FishDrop(3, 100);
				fishDrops.add(fishDrop);
		}
	}

	//If enabled in config, sets the camera to one of the cardinal directions to always try and be behind the boat
	public void updateCamera()
	{
		if (!config.enableAutoCamera())
		{
			return;
		}

		int direction = -1;
		int boatOrientation = BoatMaths.translateOrientation(absoluteBoatOrientation);

		switch (boatOrientation)
		{
			case 1024:
				direction = 1;
				break;
			case 1536:
				direction = 2;
				break;
			case 0:
				direction = 3;
				break;
			case 512:
				direction = 4;
		}

		if (direction == -1)
		{
			return;
		}

		client.runScript(1050, direction);
	}

	//Sets the NPC's chasing and attacking states if within appropriate range
	public void updateNPCAggression()
	{
		for (NPCCharacter npc : npcCharacters)
		{
			if (!npc.isAggressive())
			{
				continue;
			}

			if (deathState)
			{
				npc.setAttacking(false);
				npc.setChasing(false);
				continue;
			}

			LocalPoint shipLocation = shipObject.getLocation();
			RuneLiteObject npcObject = npc.getRuneLiteObject();
			LocalPoint npcLocation = npcObject.getLocation();
			int distance = npcLocation.distanceTo(shipLocation);
			int attackRange = npc.getAttackRange() * 128;
			int chaseRange = npc.getChaseRange() * 128;
			int krakenBiteRange = 1408;

			if (distance <= attackRange)
			{
				npc.setAttacking(true);
				npc.setChasing(true);
			}
			else if (distance <= chaseRange)
			{
				npc.setAttacking(false);
				npc.setChasing(true);
			}
			else
			{
				npc.setAttacking(false);
				npc.setChasing(false);
			}

			if (npc.getNpcType() == NPCType.KRAKEN_BOSS)
			{
				if (distance <= krakenBiteRange)
				{
					npc.setKrakenBossBite(true);
					return;
				}

				npc.setKrakenBossBite(false);
			}
		}
	}

	//If the player's boat is in range and the NPC's cooldown is over, fire a new projectile at the player's boat
	public void fireNPCProjectiles()
	{
		for (NPCCharacter npc : npcCharacters)
		{
			int coolDown = npc.getCoolDownTimer();
			if (coolDown > 0)
			{
				npc.setCoolDownTimer(coolDown - 1);
				continue;
			}

			if (!npc.isAggressive() || npc.isDying())
			{
				continue;
			}

			if (npc.isAttacking())
			{
				RuneLiteObject runeLiteObject = npc.getRuneLiteObject();

				if (npc.isKrakenBossBite())
				{
					spawnProjectile(null, 400, runeLiteObject.getLocation(), modelHandler.emptyModel, null, true);
					runeLiteObject.setAnimation(modelHandler.krakenBiteAnimation);
				}
				else
				{
					int maxHit = npc.getMaxHit();
					if (npc.getNpcType() == NPCType.KRAKEN_TENTACLE)
					{
						if (speed > 2 || (speed > 1 && boatMomentum > 0))
						{
							maxHit = 5;
							sendChatMessage("Your speed prevents the tentacle from scoring a direct hit.");
						}
					}

					spawnProjectile(null, maxHit, runeLiteObject.getLocation(), npc.getProjectileModel(), npc.getProjectileAnimation(), true);
					runeLiteObject.setAnimation(npc.getAttackAnimation());
				}

				npc.setCoolDownTimer(npc.getAttackSpeed());
			}
		}
	}

	//Set NPC position/movement pattern according to type of NPC
	public void updateNPCPositions()
	{
		for (NPCCharacter npc : npcCharacters)
		{
			if (npc.isDying() || npc.isStationary())
			{
				continue;
			}

			NPCType type = npc.getNpcType();
			RuneLiteObject runeLiteObject = npc.getRuneLiteObject();
			LocalPoint localPoint = runeLiteObject.getLocation();
			int plane = 0;

			if (type == NPCType.KRAKEN || type == NPCType.CROCODILE)
			{
				if (!npc.isChasing() || npc.isAttacking() || shipObject == null)
				{
					continue;
				}

				int currentNPCX = localPoint.getSceneX();
				int currentNPCY = localPoint.getSceneY();

				int boatX = shipObject.getLocation().getSceneX();
				int boatY = shipObject.getLocation().getSceneY();

				int xDifference = boatX - currentNPCX;
				int yDifference = boatY - currentNPCY;

				if (xDifference == 0 && yDifference == 0)
				{
					continue;
				}

				int xShift = 0;
				int yShift = 0;
				if (xDifference > 0)
				{
					xShift = 1;
				}

				if (xDifference < 0)
				{
					xShift = -1;
				}

				if (yDifference > 0)
				{
					yShift = 1;
				}

				if (yDifference < 0)
				{
					yShift = -1;
				}

				boolean moveTileX = false;
				boolean moveTileY = false;

				LocalPoint localPointX = LocalPoint.fromScene(currentNPCX + xShift, currentNPCY);
				if (xShift != 0)
				{
					if (getTileOverlay(localPointX) == WATER_OVERLAY_ID)
					{
						moveTileX = true;
					}
				}

				LocalPoint localPointY = LocalPoint.fromScene(currentNPCX, currentNPCY + yShift);
				if (yShift != 0)
				{
					if (getTileOverlay(localPointY) == WATER_OVERLAY_ID)
					{
						moveTileY = true;
					}
				}

				if (moveTileX && moveTileY)
				{
					LocalPoint localPointXY = LocalPoint.fromScene(currentNPCX + xShift, currentNPCY + yShift);
					if (getTileOverlay(localPointXY) == WATER_OVERLAY_ID)
					{
						runeLiteObject.setLocation(localPointXY, plane);
					}
				}
				else if (moveTileX)
				{
					runeLiteObject.setLocation(localPointX, plane);
				}
				else if (moveTileY)
				{
					runeLiteObject.setLocation(localPointY, plane);
				}

				if ((moveTileX || moveTileY) && npc.getNpcType() == NPCType.CROCODILE)
				{
					runeLiteObject.setAnimation(npc.getWalkAnimation());
				}
				else if (!moveTileX && !moveTileY && npc.getNpcType() == NPCType.CROCODILE)
				{
					runeLiteObject.setAnimation(npc.getIdleAnimation());
				}
			}

			if (type == NPCType.GULL)
			{
				int xShift;

				if ((client.getTickCount() / 10) % 2 == 0)
				{
					xShift = -1;
					runeLiteObject.setOrientation(512);
				}
				else
				{
					xShift = 1;
					runeLiteObject.setOrientation(1536);
				}

				LocalPoint nextPosition = LocalPoint.fromScene(localPoint.getSceneX() + xShift, localPoint.getSceneY());
				runeLiteObject.setLocation(nextPosition, 0);
			}
		}
	}

	//If within the correct region, spawns all the appropriate NPCs
	public void deployNPCSet(int firstNPC, int lastNPC)
	{
		for (int i = firstNPC; i < lastNPC; i++)
		{
			NPCSpawns npcSpawns = NPCSpawns.values()[i];
			LocalPoint localPoint = LocalPoint.fromWorld(client, npcSpawns.getXLocation(), npcSpawns.getYLocation());

			switch (npcSpawns.getNpcType())
			{
				case MERCHANT:
					spawnDummy(localPoint, true);
					break;
				case KRAKEN:
					spawnKraken(localPoint, true);
					break;
				case KRAKEN_BOSS:
					spawnKrakenBoss(localPoint, true);
					break;
				case GULL:
					spawnGull(localPoint, true);
					break;
				case CROCODILE:
					spawnCrocodile(localPoint, true);
					break;
				case KRAKEN_TENTACLE:
					spawnTentacle(localPoint, true);
			}
		}
	}

	public void clearRegionNPCSet()
	{
		for (int i = 0; i < npcCharacters.size(); i++)
		{
			NPCCharacter npc = npcCharacters.get(i);
			if (!npc.isRegionNPC())
			{
				return;
			}

			RuneLiteObject runeLiteObject = npc.getRuneLiteObject();
			runeLiteObject.setActive(false);
			npcCharacters.remove(npc);
		}
	}

	//Deploys static object sets (obstacle courses, fishing spots) when in the appropriate region
	public void deployTrialSet(int firstObject, int lastObject)
	{
		for (int i = firstObject; i < lastObject; i++)
		{
			TrialObject trialObject = TrialObject.values()[i];
			RuneLiteObject runeLiteObject = client.createRuneLiteObject();
			WorldPoint worldPoint = new WorldPoint(trialObject.getXLocation(), trialObject.getYLocation(), 0);
			LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
			if (localPoint == null)
			{
				continue;
			}

			runeLiteObject.setLocation(localPoint, 0);

			Model rlModel;
			Animation rlAnimation;
			int modelId = trialObject.getModelId();

			switch (modelId)
			{
				default:
				case 1:
					rlModel = modelHandler.greyRockLarge;
					rlAnimation = modelHandler.greyRockAnimation;
					break;
				case 2:
					rlModel = modelHandler.greyRockSmall;
					rlAnimation = modelHandler.greyRockAnimation;
					break;
				case 3:
					rlModel = modelHandler.mossyRockSmall;
					rlAnimation = null;
					break;
				case 4:
					rlModel = modelHandler.mossyRockLarge;
					rlAnimation = null;
					runeLiteObject.setDrawFrontTilesFirst(true);
					runeLiteObject.setRadius(100);
					break;
				case 5:
					rlModel = modelHandler.mossyRockTriple;
					rlAnimation = null;
					break;
				case 6:
					rlModel = modelHandler.emptyModel;
					rlAnimation = null;
					break;
				case 7:
					rlModel = modelHandler.flagModel;
					rlAnimation = modelHandler.flagAnimation;
					break;
				case 8:
					rlModel = modelHandler.basalt1;
					rlAnimation = null;
					break;
				case 9:
					rlModel = modelHandler.basalt2;
					rlAnimation = null;
					break;
				case 10:
					rlModel = modelHandler.basalt3;
					rlAnimation = null;
					break;
				case 11:
					rlModel = modelHandler.basalt4;
					rlAnimation = null;
					break;
				case 12:
					rlModel = modelHandler.basalt5;
					rlAnimation = null;
					break;
				case 13:
					rlModel = modelHandler.basalt6;
					rlAnimation = null;
					break;
				case 14:
					rlModel = modelHandler.basalt7;
					rlAnimation = null;
					runeLiteObject.setDrawFrontTilesFirst(true);
					runeLiteObject.setRadius(100);
					break;
				case 15:
					rlModel = modelHandler.basalt8;
					rlAnimation = null;
					runeLiteObject.setDrawFrontTilesFirst(true);
					runeLiteObject.setRadius(100);
					break;
				case 16:
					rlModel = modelHandler.fishModel;
					rlAnimation = modelHandler.fishAnimation;
					runeLiteObject.setDrawFrontTilesFirst(true);
					runeLiteObject.setRadius(100);
					break;
				case 17:
					rlModel = modelHandler.fishSpace;
					rlAnimation = null;
			}

			runeLiteObject.setModel(rlModel);
			if (rlAnimation != null)
			{
				runeLiteObject.setAnimation(rlAnimation);
				runeLiteObject.setShouldLoop(true);
			}

			runeLiteObject.setOrientation(trialObject.getRotation());
			runeLiteObject.setActive(true);
			if (modelId == 16 || modelId == 17)
			{
				fishObjects.add(runeLiteObject);
			}
			else
			{
				configObjects.add(runeLiteObject);
			}
		}
	}

	public void clearTrialSet()
	{
		for (int i = 0; i < configObjects.size(); i++)
		{
			RuneLiteObject runeLiteObject = configObjects.get(i);
			runeLiteObject.setActive(false);
		}

		for (int i = 0; i < fishObjects.size(); i++)
		{
			RuneLiteObject runeLiteObject = fishObjects.get(i);
			runeLiteObject.setActive(false);
		}

		fishObjects.clear();
		configObjects.clear();
	}

	//Determines if the selected tile is a water tile. If so, include menu options to spawn an NPC
	public void setWaterTileRightClick()
	{
		Tile tile = client.getSelectedSceneTile();
		if (tile == null)
		{
			return;
		}

		if (getTileOverlay(tile.getLocalLocation()) != WATER_OVERLAY_ID)
		{
			return;
		}

		lastSelectedTile = tile;

		client.createMenuEntry(-1)
				.setOption("Build boat")
				.onClick(this::checkBoatPrerequisites);

		client.createMenuEntry(-2)
				.setOption("Spawn dummy")
				.onClick(this::spawnDummyRightClick);

		client.createMenuEntry(-3)
				.setOption("Spawn kraken")
				.onClick(this::spawnKrakenRightClick);

		client.createMenuEntry(-4)
				.setOption("Spawn gull")
				.onClick(this::spawnGullRightClick);

		client.createMenuEntry(-5)
				.setOption("Spawn crocodile")
				.onClick(this::spawnCrocodileRightClick);
	}

	public void spawnNPC(NPCType npcType, Model model, NPCSize npcSize, Model projectileModel, int radius, Animation idleAnimation, Animation walkAnimation, Animation attackAnimation, Animation deathAnimation, int attackAnimFrames, Animation projectileAnimation, LocalPoint localPoint, int attackRange, int chaseRange, int maxHit, int attackSpeed, int maxHealth, boolean aggressive, boolean stationary, boolean regionNPC)
	{
		RuneLiteObject runeLiteObject = client.createRuneLiteObject();
		if (localPoint == null)
		{
			return;
		}

		runeLiteObject.setLocation(localPoint, 0);
		runeLiteObject.setModel(model);
		runeLiteObject.setRadius(radius);
		runeLiteObject.setAnimation(idleAnimation);
		runeLiteObject.setShouldLoop(true);
		runeLiteObject.setDrawFrontTilesFirst(true);
		runeLiteObject.setActive(true);

		int randomOrientation = random.nextInt(7) * 256;
		runeLiteObject.setOrientation(randomOrientation);

		NPCCharacter npc = new NPCCharacter(
				npcType,
				npcSize,
				runeLiteObject,
				projectileModel,
				idleAnimation,
				walkAnimation,
				attackAnimation,
				deathAnimation,
				attackAnimFrames,
				projectileAnimation,
				maxHealth,
				attackRange,
				chaseRange,
				maxHit,
				attackSpeed,
				aggressive,
				stationary,
				regionNPC
		);

		npcCharacters.add(npc);
	}

	public void spawnDummyRightClick(MenuEntry menuEntry)
	{
		LocalPoint localPoint = lastSelectedTile.getLocalLocation();
		spawnDummy(localPoint, false);
	}

	public void spawnDummy(LocalPoint localPoint, boolean regionNPC)
	{
		spawnNPC(NPCType.MERCHANT, modelHandler.npcShipModel, NPCSize.PLUS, null, 120, modelHandler.boatIdleAnimation, null, null, modelHandler.boatDeathAnimation, 0, null, localPoint, 5, 0, 10, 5, 100, false, true, regionNPC);
	}

	public void spawnKrakenRightClick(MenuEntry menuEntry)
	{
		LocalPoint localPoint = lastSelectedTile.getLocalLocation();
		spawnKraken(localPoint, false);
	}

	public void spawnKraken(LocalPoint localPoint, boolean regionNPC)
	{
		spawnNPC(NPCType.KRAKEN, modelHandler.krakenModel, NPCSize.LARGE, modelHandler.krakenProjectileModel, 120, modelHandler.krakenIdleAnimation, null, modelHandler.krakenSpellAnimation, modelHandler.krakenDeathAnimation, 17, null, localPoint, 7, 14, 30, 5, 255, true, false, regionNPC);
	}

	public void spawnKrakenBossRightClick(MenuEntry menuEntry)
	{
		LocalPoint localPoint = lastSelectedTile.getLocalLocation();
		spawnKrakenBoss(localPoint, false);
	}

	public void spawnKrakenBoss(LocalPoint localPoint, boolean regionNPC)
	{
		spawnNPC(NPCType.KRAKEN_BOSS, modelHandler.krakenGiantModel, NPCSize.GIGANTIC, modelHandler.krakenBossProjectileModel, 1400, modelHandler.krakenIdleAnimation, null, modelHandler.krakenSpellAnimation, modelHandler.krakenDeathAnimation, 17, modelHandler.krakenBossProjectileAnimation, localPoint, 30, 60, 45, 10, 1000, true, true, regionNPC);
	}

	public void spawnTentacleRightClick(MenuEntry menuEntry)
	{
		LocalPoint localPoint = lastSelectedTile.getLocalLocation();
		spawnTentacle(localPoint, false);
	}

	public void spawnTentacle(LocalPoint localPoint, boolean regionNPC)
	{
		spawnNPC(NPCType.KRAKEN_TENTACLE, modelHandler.krakenTentacleModel, NPCSize.HUGE, modelHandler.emptyModel, 700, modelHandler.tentacleIdleAnimation, null, modelHandler.tentacleAttackAnimation, modelHandler.tentacleDeathAnimation, 13, null, localPoint, 8, 20, 25, 6, 125, true, true, regionNPC);
	}

	public void spawnGullRightClick(MenuEntry menuEntry)
	{
		LocalPoint localPoint = lastSelectedTile.getLocalLocation();
		spawnGull(localPoint, false);
	}

	public void spawnGull(LocalPoint localPoint, boolean regionNPC)
	{
		spawnNPC(NPCType.GULL, modelHandler.gullModel, NPCSize.SMALL, null, 80, modelHandler.gullIdleAnimation, null, null, modelHandler.gullDeathAnimation, 0, null, localPoint, 1, 0, 1, 4, 25, false, false, regionNPC);
	}

	public void spawnCrocodileRightClick(MenuEntry menuEntry)
	{
		LocalPoint localPoint = lastSelectedTile.getLocalLocation();
		spawnCrocodile(localPoint, false);
	}

	public void spawnCrocodile(LocalPoint localPoint, boolean regionNPC)
	{
		spawnNPC(NPCType.CROCODILE, modelHandler.crocodileModel, NPCSize.PLUS, modelHandler.emptyModel, 100, modelHandler.crocodileIdleAnimation, modelHandler.crocodileWalkAnimation, modelHandler.crocodileAttackAnimation, modelHandler.crocodileIdleAnimation, 2, null, localPoint, 1, 7, 10, 2, 100, true, false, regionNPC);
	}

	//Spawns a projectile from the player to NPC, or NPC to player
	public void spawnProjectile(NPCCharacter target, int maxHit, LocalPoint origin, Model model, Animation animation, boolean targetsPlayer)
	{
		RuneLiteObject projectileObject = client.createRuneLiteObject();
		Projectile projectile = new Projectile(projectileObject, target, targetsPlayer, maxHit, 0);
		projectileObject.setLocation(origin, 0);
		projectileObject.setModel(model);
		projectileObject.setActive(true);

		if (projectileObject.getAnimation() != null)
		{
			projectileObject.setAnimation(animation);
			projectileObject.setShouldLoop(true);
		}

		activeProjectiles.add(projectile);
	}

	//Checks all tiles in front of the boat and determines if they're valid to move onto. Stops the boat once a non-water-tile or object is hit
	public int[] checkTileCollision(int speed)
	{
		LocalPoint lp = shipObject.getLocation();
		int curX = lp.getSceneX();
		int curY = lp.getSceneY();

		int direction = BoatMaths.translateOrientation(absoluteBoatOrientation);
		int nextX = 0;
		int nextY = 0;
		switch (direction)
		{
			default:
			case 0:
				nextY--;
				break;
			case 256:
				nextY--;
				nextX--;
				break;
			case 512:
				nextX--;
				break;
			case 768:
				nextY++;
				nextX--;
				break;
			case 1024:
				nextY++;
				break;
			case 1280:
				nextY++;
				nextX++;
				break;
			case 1536:
				nextX++;
				break;
			case 1792:
				nextX++;
				nextY--;
		}

		int projectedX = speed * nextX;
		int projectedY = speed * nextY;
		for (int i = speed; i > 0; i--)
		{
			Tile furtherTile = getTile(curX, curY, nextX * i, nextY * i);
			Tile nearerTile = getTile(curX, curY, nextX * (i - 1), nextY * (i - 1));

			if (nearerTile == null)
			{
				projectedX = curX;
				projectedY = curY;
				continue;
			}

			if (furtherTile == null)
			{
				projectedX = nearerTile.getLocalLocation().getSceneX();
				projectedY = nearerTile.getLocalLocation().getSceneY();
				continue;
			}

			short furtherOverlay = getTileOverlay(furtherTile);
			short nearerOverlay = getTileOverlay(nearerTile);

			byte furtherTileFlag = getTileFlag(furtherTile);
			byte nearerTileFlag = getTileFlag(nearerTile);

			GameObject[] furtherGameObjects = furtherTile.getGameObjects();
			GameObject[] nearerGameObjects = nearerTile.getGameObjects();
			boolean furtherGameObjectBlocked = false;
			boolean nearerGameObjectBlocked = false;

			for (GameObject gameObject : furtherGameObjects)
			{
				if (gameObject != null)
				{
					furtherGameObjectBlocked = true;
					break;
				}
			}

			for (GameObject gameObject : nearerGameObjects)
			{
				if (gameObject != null)
				{
					nearerGameObjectBlocked = true;
					break;
				}
			}

			boolean furtherRLObjectBlocked = false;
			boolean nearerRLObjectBlocked = false;

			LocalPoint furtherTileCoords = furtherTile.getLocalLocation();
			int furtherTileX = furtherTileCoords.getSceneX();
			int furtherTileY = furtherTileCoords.getSceneY();

			LocalPoint nearerTileCoords = nearerTile.getLocalLocation();
			int nearerTileX = nearerTileCoords.getSceneX();
			int nearerTileY = nearerTileCoords.getSceneX();

			for (RuneLiteObject runeLiteObject : configObjects)
			{
				LocalPoint rlObjectCoords = runeLiteObject.getLocation();
				int rlObjectX = rlObjectCoords.getSceneX();
				int rlObjectY = rlObjectCoords.getSceneY();

				if (furtherTileX == rlObjectX && furtherTileY == rlObjectY)
				{
					furtherRLObjectBlocked = true;
				}

				if (nearerTileX == rlObjectX && nearerTileY == rlObjectY)
				{
					nearerRLObjectBlocked = true;
				}
			}

			boolean furtherTileHasKraken = false;
			boolean nearerTileHasKraken = false;

			if (currentNPCSpawns == REGION_PIRATES_COVE)
			{
				for (NPCCharacter npc : npcCharacters)
				{
					if (npc.getNpcType() == NPCType.KRAKEN_BOSS || npc.getNpcType() == NPCType.KRAKEN_TENTACLE)
					{
						LocalPoint npcLocalPoint = npc.getRuneLiteObject().getLocation();
						int npcRadius = npc.getNpcSize().getHitboxRadius();

						if (nearerTileCoords.distanceTo(npcLocalPoint) < npcRadius)
						{
							nearerTileHasKraken = true;
						}

						if (furtherTileCoords.distanceTo(npcLocalPoint) < npcRadius)
						{
							furtherTileHasKraken = true;
						}
					}
				}
			}

			if ((furtherOverlay != WATER_OVERLAY_ID && nearerOverlay == WATER_OVERLAY_ID)
					|| (((furtherTileFlag & Constants.TILE_FLAG_BRIDGE) != 0) && ((nearerTileFlag & Constants.TILE_FLAG_BRIDGE) == 0))
					|| (furtherGameObjectBlocked && !nearerGameObjectBlocked)
					|| (furtherRLObjectBlocked && !nearerRLObjectBlocked)
					|| (furtherTileHasKraken && !nearerTileHasKraken))
			{
				projectedX = nearerTile.getLocalLocation().getSceneX();
				projectedY = nearerTile.getLocalLocation().getSceneY();
			}
		}

		return new int[]{projectedX, projectedY};
	}

	public Tile getTile(int currentX, int currentY, int addX, int addY)
	{
		Scene scene = client.getScene();
		Tile[][][] tiles = scene.getTiles();
		int z = 0;

		for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
			for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
				Tile tile = tiles[z][x][y];

				if (tile == null)
				{
					continue;
				}

				int tileX = tile.getLocalLocation().getSceneX();
				int tileY = tile.getLocalLocation().getSceneY();

				if (tileX == currentX + addX && tileY == currentY + addY)
				{
					return tile;
				}
			}
		}
		return null;
	}

	public ArrayList<Tile> getTiles(int currentX, int currentY, int[] addX, int[] addY)
	{
		Scene scene = client.getScene();
		Tile[][][] tiles = scene.getTiles();
		int z = 0;
		ArrayList<Tile> tileList = new ArrayList<>();

		for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
			for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
				Tile tile = tiles[z][x][y];

				if (tile == null)
				{
					continue;
				}

				int tileX = tile.getLocalLocation().getSceneX();
				int tileY = tile.getLocalLocation().getSceneY();

				for (int i = 0; i < addX.length; i++)
				{
					if (tileX == currentX + addX[i] && tileY == currentY + addY[i])
					{
						tileList.add(tile);
					}
				}
			}
		}
		return tileList;
	}

	public short getTileOverlay(Tile tile)
	{
		LocalPoint localPoint = tile.getLocalLocation();
		return getTileOverlay(localPoint);
	}

	public short getTileOverlay(LocalPoint localPoint)
	{
		int z = 0;
		int x = localPoint.getSceneX();
		int y = localPoint.getSceneY();

		Scene scene = client.getScene();
		short[][][] overlays = scene.getOverlayIds();
		return overlays[z][x][y];
	}

	public byte getTileFlag(Tile tile)
	{
		LocalPoint localPoint = tile.getLocalLocation();
		return getTileFlag(localPoint);
	}

	public byte getTileFlag(LocalPoint localPoint)
	{
		int z = 1;
		int x = localPoint.getSceneX();
		int y = localPoint.getSceneY();

		byte[][][] settings = client.getTileSettings();
		return settings[z][x][y];
	}

	//If attempting to build a boat, checks whether the tile chosen is a valid water tile
	public void checkBoatPrerequisites(MenuEntry menuEntry)
	{
		LocalPoint localPoint = getTilePlayerFaces();
		if (localPoint == null)
		{
			return;
		}

		if (getTileOverlay(localPoint) != WATER_OVERLAY_ID)
		{
			sendChatMessage("You may want to build your boat on water. Walk up to and face a water tile and attempt 'Build boat' again.");
			return;
		}

		buildBoatTimer = BUILD_BOAT_TIMER_START;
	}

	//Gets the tile directly in front of the player
	public LocalPoint getTilePlayerFaces()
	{
		Player player = client.getLocalPlayer();

		int orientation = player.getOrientation();
		int xLocation = player.getWorldLocation().getX();
		int yLocation = player.getWorldLocation().getY();

		int direction = BoatMaths.translateOrientation(orientation);
		switch (direction)
		{
			default:
			case 0:
				yLocation--;
				break;
			case 256:
				yLocation--;
				xLocation--;
				break;
			case 512:
				xLocation--;
				break;
			case 768:
				yLocation++;
				xLocation--;
				break;
			case 1024:
				yLocation++;
				break;
			case 1280:
				yLocation++;
				xLocation++;
				break;
			case 1536:
				xLocation++;
				break;
			case 1792:
				xLocation++;
				yLocation--;
		}

		WorldPoint worldPoint = new WorldPoint(xLocation, yLocation, 0);
		return LocalPoint.fromWorld(client, worldPoint);
	}

	//Spawns a boat under player control
	public void buildBoat()
	{
		LocalPoint localPoint = getTilePlayerFaces();
		if (getTileOverlay(localPoint) != WATER_OVERLAY_ID || (getTileFlag(localPoint) & Constants.TILE_FLAG_BRIDGE) != 0)
		{
			sendChatMessage("You may want to build your boat on water. Walk up to and face a water tile and attempt 'Build boat' again.");
			return;
		}

		createBoat(localPoint);
		widgetSetter.setupUnknownTab();
		currentHealth = getBoatTypeMaxHealth();
		maxHealth = getBoatTypeMaxHealth();
	}

	public void createBoat(LocalPoint localPoint)
	{
		if (shipObject != null)
		{
			shipObject.setActive(false);
		}

		shipObject = client.createRuneLiteObject();
		shipObject.setModel(shipModel0);
		absoluteBoatOrientation = 0;
		sailLength = 0;
		shipObject.setLocation(localPoint, 0);
		shipObject.setRadius(220);
		shipObject.setActive(true);
		shipObject.setAnimation(modelHandler.boatIdleAnimation);
		shipObject.setShouldLoop(true);
		anchorMode = true;
		deathState = false;
	}

	public void despawnBoat()
	{
		if (shipObject == null)
		{
			return;
		}

		hitSplats.clear();
		healthBarTimer = 0;
		shipObject.setActive(false);
		shipObject = null;
		widgetSetter.unsetLastTab();
	}

	public int getBoatTypeMaxHealth()
	{
		switch (config.boatType())
		{
			default:
			case NORMAL:
				return 50;
			case OAK:
				return 80;
			case WILLOW:
				return 110;
			case MAPLE:
				return 140;
			case YEW:
				return 170;
			case MAGIC:
				return 200;
			case REDWOOD:
				return 230;
		}
	}

	//If the tool button is pressed in the Sailing menu, initiates the appropriate action
	public void useCurrentTool()
	{
		switch (currentBoatTool)
		{
			case CANNON:
				readyCannon();
				return;
			case FISHING_ROD:
				startFishing();
				return;
			case HARPOON:
				sendChatMessage("Sailing isn't real so you can't hunt for seabirds.");
				return;
			case NET:
				sendChatMessage("Sailing isn't real so you can't dredge for minerals.");
				return;
			case SPYGLASS:
				sendChatMessage("Sailing isn't real so you can't spot for treasures.");
				return;
			case PLUNDER:
				sendChatMessage("Sailing isn't real so you can't plunder merchant boats.");
		}
	}

	public void startFishing()
	{
		if (validFishingTile())
		{
			sendChatMessage("You cast your rod and wait for a bite.");
			fishing = true;
			return;
		}

		sendChatMessage("You need to be over a valid fishing spot to start fishing.");
	}

	//Checks whether the tile the player's boat is on top of is close enough to a fish object
	public boolean validFishingTile()
	{
		LocalPoint localPoint = shipObject.getLocation();
		int x = localPoint.getSceneX();
		int y = localPoint.getSceneY();
		boolean fishingLocation = false;

		for (RuneLiteObject runeLiteObject : fishObjects)
		{
			LocalPoint fishPoint = runeLiteObject.getLocation();
			int fishX = fishPoint.getSceneX();
			int fishY = fishPoint.getSceneY();

			if (x == fishX && y == fishY)
			{
				fishingLocation = true;
				break;
			}
		}

		return fishingLocation;
	}

	public void readyCannon()
	{
		if (shipObject == null)
		{
			return;
		}

		fireCannon = true;
	}

	public int shipTypeMaxHit()
	{
		switch (config.boatType())
		{
			default:
			case NORMAL:
				return 20;
			case OAK:
				return 25;
			case WILLOW:
				return 30;
			case MAPLE:
				return 35;
			case YEW:
				return 40;
			case MAGIC:
				return 45;
			case REDWOOD:
				return 50;
		}
	}

	public void rotateBoatCW()
	{
		if (deathState)
		{
			return;
		}
		boatRotationQueue = 1;
	}

	public void rotateBoatCCW()
	{
		if (deathState)
		{
			return;
		}
		boatRotationQueue = -1;
	}

	public void increaseSailLength()
	{
		if (sailLengthQueue < 1)
		{
			sailLengthQueue = 1;
			return;
		}

		sailLengthQueue++;
	}

	public void decreaseSailLength()
	{
		if (sailLengthQueue > -1)
		{
			sailLengthQueue = -1;
			return;
		}

		sailLengthQueue--;
	}

	public void setSailLength(int newSailLength)
	{
		sailLengthQueue = 0;
		sailLength = newSailLength;

		anchorMode = sailLength == 0;
	}

	public int getXMovement(int orientation)
	{
		if (orientation == 0 || orientation == 1024)
		{
			return 0;
		}

		if (orientation == 256 || orientation == 512 || orientation == 768)
		{
			return -1;
		}

		if (orientation == 1280 || orientation == 1536 || orientation == 1792)
		{
			return 1;
		}

		return 0;
	}

	public int getYMovement(int orientation)
	{
		if (orientation == 512 || orientation == 1536)
		{
			return 0;
		}

		if (orientation == 1792 || orientation == 0 || orientation == 256)
		{
			return -1;
		}

		if (orientation == 768 || orientation == 1024 || orientation == 1280)
		{
			return 1;
		}

		return 0;
	}

	private void sendChatMessage(String chatMessage)
	{
		final String message = new ChatMessageBuilder().append(ChatColorType.HIGHLIGHT).append(chatMessage).build();
		chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.GAMEMESSAGE).runeLiteFormattedMessage(message).build());
	}

	@Provides
	SailingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SailingConfig.class);
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (enableKeyboardControl && shipObject != null)
		{
			if (e.getKeyChar() == config.turnLeftHotkey().charAt(0))
			{
				rotateBoatCCW();
			}

			if (e.getKeyChar() == config.turnRightHotkey().charAt(0))
			{
				rotateBoatCW();
			}

			if (e.getKeyChar() == config.sailIncreaseHotkey().charAt(0))
			{
				increaseSailLength();
			}

			if (e.getKeyChar() == config.sailDecreaseHotkey().charAt(0))
			{
				decreaseSailLength();
			}

			if (e.getKeyCode() == KeyEvent.VK_SPACE)
			{
				useCurrentTool();
			}
		}
	}
}
