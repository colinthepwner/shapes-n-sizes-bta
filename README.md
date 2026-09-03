# Shapes n Sizes

Resize players in **Better than Adventure! 8.0.1** (Babric / Fabric), anywhere from a tenth of
normal size up to sixteen times it.

A resized player gets a matching hitbox, eye height, model, shadow, step height, reach, speed and
jump, and keeps it across relogs, deaths and dimension changes. Held items and blocks stay world
sized in both views, so a giant turns over a sugar cube and a tiny player hauls a boulder.

## Being big

- Crops flatten under you. Snow takes a real footprint at each stride, one foot at a time,
  alternating sides, half your width across and rounded to whole blocks.
- Ice breaks across every block your body touches, so falling through leaves a hole you can swim
  out of instead of a lid over your head. Ice skates stop it, and so does noclip.
- Grass turns to dirt where you land, but only from a genuine fall, further than you can jump.
- From 3x, a hard landing kicks up a shockwave: one layer of dirt, sand, gravel or grass, about as
  wide as your stance and growing with the drop. Stone and anything built is left alone.
- From 3.5x you tread on anything much smaller than you, mobs and players alike, either by coming
  down on them or by walking into them. The damage starts small at the threshold and climbs.
- From 2x you push through leaves the way you push through a cobweb. At 5x the branches break as
  you go, at 9x the trunks come down too.
- From 6x, shallow water is held aside so you can wade a stream. A lake still closes over you, and
  standing still lets it close in.
- Cactus spines stop reaching you at 4x. Magma, brimthaw and hot boulders stop setting you alight
  at 6x.
- Boats and minecarts will not take a body past 2.5x. Climbing in breaks them, and so does growing
  while already sitting in one.
- Crouching, crawling or leather boots spare all of the ground effects, and so does riding
  something or flying through in noclip.

## Being small

- You never trample farmland, and monsters overlook you in favour of whoever is standing behind you.
- Hold a lily pad to walk on water. Sneaking, jumping onto it or taking a hit drops you through,
  and you have to reach dry land before it will hold you again.
- Hold paper to glide down slowly and land unhurt from any height.
- At half size or under you climb sheer walls, either by crouching against one or by holding a
  slimeball to climb at a walk.
- Leaves let you slip between the branches instead of holding you up.
- At a quarter size or under, buttons, levers, torches and flags have collision, in the shape they
  are drawn.
- Tall grass and snow layers slow you down. Roses draw blood at a third size. Rain is deep enough
  to drown in, unless you are wearing a helmet or standing under something.
- Pressure plates stop noticing you: stone plates at three quarters size, every plate at half.

## Either way

- Fall damage goes both ways from normal size. A big body is measured in body lengths, so a 2x
  player falls twice as far unhurt. A small body is charged by the square of its size, which is why
  a mouse walks away from a fall that kills a horse.
- Gravity does not scale. Two players of different sizes who step off the same ledge land together.
- A rider sits at a depth that follows their size, so a giant on a pig straddles it rather than
  hovering over it. A tall player asleep in a bed lies down the bed with their head on the pillow
  and their feet off the end.
- Wear a saddle in the chestplate slot, then sneak and right click a player at least 1.5x smaller,
  and they ride piggyback. At 3x smaller they ride on your shoulder instead, where they can still
  see. They sneak to get off.
- Anyone can crawl. Hold Alt (rebindable under Controls, with a hold/toggle switch) to drop to a
  third of normal height. You stay down until there is room to get up.
- In third person the camera pulls back with the size, so a giant is not looking at their own back.
- A portal you step through builds the far side to match its own size, rather than always digging
  out the smallest legal 2x3.

## Doors

Three doors for bodies that do not fit through a normal one: a **Short Door** (one block), a
**Tall Door** (three) and a **Very Tall Door** (four). They open, close, hinge, pair up as double
doors and answer to redstone like the door you already have. The game's own doors now pair up too,
by hand and by redstone, and a pair closes together as readily as it opens.

| Recipe | Makes |
| --- | --- |
| A trapdoor on its own | 1 short door |
| An oak door on its own | 2 short doors |
| A door with a trapdoor or short door on top | 1 tall door |
| A tall door with a trapdoor or short door on top | 1 very tall door |

## Brownies

Two craftable foods, edible at any health, that change your size by 0.1 a bite. The change takes
about a second and a half rather than happening at once.

| Brownie | Recipe (shapeless) |
| --- | --- |
| Growing (makes 2) | cocoa beans, wheat, sugar, lapis lazuli |
| Shrinking (makes 2) | cocoa beans, wheat, sugar, orange dye |

A brownie's size is kept separate from the size `/scale` sets. It survives relogging but wears off
when you die, leaving you at whatever the command last gave you. Eating cannot take you past the
usual `0.1` to `16` range.

## Commands

Operator only. `/scale` and `/scaling` are the same command under two names, the way BTA's own
`/gamerule` also answers to `/gr`.

| Command | What it does |
| --- | --- |
| `/scale <scale>` | Set your own size. `1` is normal, `0.1` to `16` accepted. |
| `/scale reset` | Put yourself back to normal size. |
| `/scale <player> <scale>` | Set someone else's size. Selectors such as `@a` work. |
| `/scaling set <player> <scale>` | The same thing, spelled out. |
| `/scaling get [player]` | Show a player's current size. |
| `/scaling reset <player>` | Put a player back to normal size. |
| `/scaling setabilityscaling <percent>` | How much of a size change carries over to speed, reach and jump. |
| `/scaling getabilityscaling` | Show the current ability scaling. |
| `/scaling reload` | Re-read the config without restarting. |

## Game rules

All four appear in the world settings screen and can be set with `/gamerule`.

| Rule | Default | What it does |
| --- | --- | --- |
| `abilityScaling` | `100` | How much of a size change carries over to speed, reach and jump. `100` is fully proportional, `0` changes only the body. |
| `doSizeGriefing` | `true` | Whether size changes trampling, footprints, ice and woodland. |
| `doSizeStompSounds` | `false` | Whether a very large player's footfalls are audible across a valley. |
| `doSizeWaterDisplacement` | `true` | Whether a very large player parts shallow water. Separate because it moves blocks every tick a giant is in the water. |

## What ability scaling changes

Ability scaling is one percentage that decides how far a size change carries. At `100` a 2x player
gets a factor of 2, at `50` a factor of 1.5, at `0` nothing but the body changes. Below normal size
the loss flattens off, so a half size player sits at 0.71 rather than 0.5.

| Follows the factor | Follows its square root | Does not scale |
| --- | --- | --- |
| Reach, damage taken (capped at 4x), food value, fluid and cobweb drag | Jump height, step height, mining speed, knockback (capped at 3x) | Gravity, field of view, maximum health |

Walking speed starts at the square root and gets closer to proportional as the size grows, so a 2x
player moves at about 1.4x and a 16x player at 8x. Breath and pickup reach follow a three quarter
power. Monsters notice big players from up to three times as far away and pick the largest of a
group, unless something has already hit them.

## Starting sizes (servers)

`config/shapesnsizes.properties` is written on first run.

```
default-scale = 1.0
player.SomePlayer = 0.5
player.AnotherPlayer = 3
```

These apply on login, and only to a player the server has no size on record for. After that
everyone keeps whatever size they have, so `/scale` sticks and nobody springs back on reconnect.
Run `/scaling reload` to pick up edits without a restart.

## Install

Grab the jar from [Releases](https://github.com/colinthepwner/shapes-n-sizes-bta/releases) and drop
it into your `mods` folder. HalpLibe is bundled, so nothing else is required.

Works on client and server. For multiplayer, every player needs it as well as the server.

## Building

```bash
./gradlew build
```

The jar is written to `build/libs/`.

## Credits

- **Will** [@wyntersnowstorm](https://github.com/wyntersnowstorm)
- **Way** [@colinthepwner](https://github.com/colinthepwner)

All textures are **CongaSpy**'s, licensed for use with this mod and nothing else.

Fine: playing the mod, running it on a server, handing someone the jar, putting it in a modpack.

Not fine: pulling the images out for your own mod, resource pack or project, re-uploading them on
their own, editing them and passing the result on, or using them anywhere this mod is not. Ask
CongaSpy first if you want them for something else.
