# Shapes n Sizes

A Better than Adventure mod that lets operators resize players. A resized player gets a matching
hitbox, eye height, model, shadow, step height, reach, movement speed and jump, and the size is
kept across relogs, deaths and dimension changes. How strongly speed, reach and jump follow the
size is adjustable. Held items and blocks stay world-sized in both views, so a giant turns over a
sugar cube and a tiny player hauls a boulder. The walk animation keeps a natural cadence at any
size, and the field of view does not change with size.

Size also changes how a player treats the ground:

- Small players (half size or less) never trample farmland, and can walk on water while holding a
  lily pad. The surface breaks if you crouch, land on it after a jump, or take damage, and it does
  not come back until you are out of the water and standing on solid ground. Walking onto the water
  from a bank is fine, and so is a jump that ends on dry land.
- Big players (one-and-a-half size or more) leave a mark on the ground they cross. Every crop under
  them is flattened, not one in four. Snow takes a real footprint at each stride, one foot at a time
  and alternating sides, spaced by the body and **half the body's width across, rounded to whole
  blocks** — so a 2x player leaves single-block prints and a giant leaves five. And grass they land
  on turns to dirt — but only from a genuine fall, further than that player can jump under their own
  power, so hopping about no longer scars the ground behind you.
- Ice gives way under a big player across **every block their body touches**, not just the layer
  under their feet, so falling through leaves a hole you can swim out of rather than a lid over your
  head. Standing still does not save you; crouching does, and so do **ice skates**, which are for
  crossing ice rather than destroying it.
- **Crouching spares all of it**, and so does crawling, and so do leather boots. A big player who
  wants to arrive somewhere without rearranging it crouches on the way down.
- A big player who lands hard enough, from three times size up and not crouching, kicks up a
  shockwave: one layer of soft ground (dirt, sand, gravel, grass) **about as wide as their own
  stance**, growing with the drop, and a jolt to anything standing nearby. Stone and anything
  tougher is untouched, so landing in a town rearranges the flowerbeds and not the town.
- A body **three-and-a-half** times taller than another *because one of them was resized* can tread
  on it, in either direction: a giant crushes mobs and normal-sized players underfoot, and a small
  enough player is in real danger from an ordinary zombie. Both halves matter — height alone would
  mean an ordinary, unresized player stamping scorpions and butterflies to death by walking into
  them, which is nothing to do with size. It lands either by treading on them from above or by walking into them, and
  the engine's own knockback sends a kicked victim flying. The damage starts small at the threshold
  and climbs from there, so the first size that can tread on you is not the size that flattens you.
- **Leaves stop being a floor at both ends of the range.** Small players (half size or less) slip
  between the branches. From twice size up you push *through* the canopy the way you push through a
  cobweb, and the bigger you are the less it holds you; at five times you start breaking branches as
  you go, and at nine the trunks come down as well. Anything broken this way drops what a blast
  would leave it, and leather boots spare the woodland exactly as they spare a wheat field.
- At a third size or less, rain is deep enough to drown in. Any helmet or anything overhead keeps
  it off, and the bubble bar drains as it would underwater.
- Cactus spines cannot reach a player of four times size or more — at one and a half a cactus is
  still waist-high and very much a problem — and roses draw blood on a very small one (a third size
  or under, the point where the flower properly towers over you). What is scenery at one size is a
  hazard at another, in both directions.
- Tall grass is a thicket to a small player and slows them down; everyone else walks through it as
  before.
- Small players get a traversal kit from ordinary items, by holding one: a **lily pad** to walk on
  water and a **paper** to glide down slowly, landing unhurt from any height. Sneaking cancels both.
- At a quarter size or under, buttons, levers, torches and flags stop being scenery and start being
  furniture: they have collision, in the shape they are drawn.
- At half size or under they climb sheer walls like a spider — crouch against one, or hold a
  **slimeball** to climb at a walk — at a pace that matches their size. Below half size a jump no
  longer clears a whole block, so climbing is how you get over things. Catching a wall does not wipe
  a fall the way a ladder does: whatever you were already falling still lands.
- A player wearing a saddle in the chestplate slot can carry a player at least one-and-a-half
  times smaller: sneak and right-click them and they ride piggyback. At three times smaller or
  more they ride on the shoulder instead, where they can still see. The saddle gives no protection
  and never wears out. The rider sneaks to get off, and can hit and use things from up there.
- Any player can crawl. Hold Alt (rebindable under Controls, with a hold/toggle switch) to drop to
  a third of normal height and move slowly; big players use it to get into small players' houses.
  You stay down until there is room to get up. Crawling takes priority over crouching — you cannot
  crouch while lying down — and the pose puts the head where the camera is looking with the arms out
  in front.
- Magma and brimthaw cannot set alight anything at a third size or under, or at six times and over:
  too small to be across the cracks, or with a sole too thick to notice them. Magmatic and sulfuric
  boulders are likewise nothing to a body six times normal size.
- A rider sits at a depth that follows their size, so a giant on a pig straddles it instead of
  hovering over it and a mouse-sized player perches on top instead of vanishing inside it. A player
  asleep in a bed lies **down** the bed with their head on the pillow, so a tall one's feet hang off
  the end — build yourself a longer bed out of seats.
- In third person the camera pulls back with size, so a giant is not looking at their own back.

- Past **six times** normal size, shallow water is held aside so a behemoth wades a stream (knee
  deep at most — a lake still closes over them, and standing still lets it close in, after which they
  must leave the water or find somewhere shallower to part it again). Their footfalls can be made
  loud enough to hear across a valley, though that is off by default. Aiming is *not* slowed: the
  mouse answers identically at every size.
- Snow layers slow a small player, the way tall grass does.
- Pressure plates need weight: at three quarters size and under stone plates ignore you, and at half
  size and under no plate notices you at all.
- A portal you step through builds the far side **to match its own size**, instead of always
  digging out the smallest legal 2x3. A giant who builds a doorway they fit through now arrives at
  one they fit back through. Ordinary portals are unaffected.

## Doors

Three doors for bodies that do not fit through a normal one: a **Short Door** (one block), a
**Tall Door** (three) and a **Very Tall Door** (four). They open, close, hinge, pair up as double
doors and answer to redstone like the door you already have.

**The game's own doors now pair up too**, by hand and by redstone, and a pair closes together as
readily as it opens.

- A trapdoor on its own crafts a short door; an oak door on its own crafts two.
- A door with a trapdoor or a short door on top makes a tall door.
- A tall door with a trapdoor or a short door on top makes a very tall door.

## Brownies

Two craftable foods, edible at any health, that change your size by 0.1 a bite.

| Brownie | Recipe (shapeless) |
| --- | --- |
| Growing (makes 2) | cocoa beans, wheat, sugar, lapis lazuli |
| Shrinking (makes 2) | cocoa beans, wheat, sugar, orange dye |

Growing and shrinking happen over about a second and a half rather than at once, so the change reads
as happening rather than as having happened.

A brownie's size is separate from the size `/scale` sets. It survives relogging but **wears off
when you die**, leaving you at whatever size the command last gave you. Eating cannot take you past
the normal `0.1` to `16` range.

## Commands

All of these need operator permissions.

| Command | What it does |
| --- | --- |
| `/scale <scale>` | Set your own size. `1` is normal; `0.1` to `16` are accepted. |
| `/scale reset` | Put yourself back to normal size. |
| `/scale <player> <scale>` | Set someone else's size. Selectors such as `@a` work. |
| `/scaling set <player> <scale>` | The same thing, spelled out. |
| `/scaling get [player]` | Show a player's current size. |
| `/scaling reset <player>` | Put a player back to normal size. |
| `/scaling setabilityscaling <percent>` | How much of a size change carries over to speed, reach and jump. `100` is fully proportional, `0` changes only the body. |
| `/scaling getabilityscaling` | Show the current ability scaling. |
| `/scaling reload` | Re-read `config/shapesnsizes.properties` without restarting. |

`/scale` and `/scaling` are the same command under two names, the way BTA's own `/gamerule` also
answers to `/gr`. Every form above works under either name.

## Game rules

| Rule | Default | What it does |
| --- | --- | --- |
| `abilityScaling` | `100` | The same setting as `setabilityscaling`. |
| `doSizeGriefing` | `true` | Whether size changes trampling, footprints, ice and woodland. |
| `doSizeStompSounds` | `false` | Whether a behemoth's footfalls are loud enough to hear across a valley. |
| `doSizeWaterDisplacement` | `true` | Whether a behemoth parts shallow water. Its own setting because it moves blocks every tick a giant is in the water. |

All four appear in the world settings screen and can be set with `/gamerule`.

## Starting sizes (servers)

`config/shapesnsizes.properties` is written on first run and gives players a size to start at:

```
default-scale = 1.0
player.SomePlayer = 0.5
player.AnotherPlayer = 3
```

These apply on login and **only to a player the server has no size on record for** — their first
join. After that everyone keeps whatever size they have, so `/scale` sticks and nobody springs back
on reconnect. `/scaling reload` picks up edits without a restart.

## How the abilities scale

Ability scaling sets how far a size change carries, as a factor. Above normal size the factor
follows the size directly: at 100% a 2x player has a factor of 2, at 50% a factor of 1.5, at 0% a
factor of 1. Below normal size the loss flattens out the smaller you get, so a half-size player
sits at 0.71 rather than 0.5 and a tenth-size player at 0.32 rather than 0.1.

- **Reach** uses the factor directly.
- **Jump height** and **step height** both use the square root of it above normal size, so a 2x
  player clears about one and a half blocks and can step up about seven tenths of one — a strong
  jump rather than flight, and a body that still has to get over things. Anyone half size or bigger
  keeps a jump that clears a whole block whatever the arithmetic says, since otherwise a slightly
  short player cannot get onto a single step and their step height has shrunk too. Below normal size
  both follow the factor directly.
- **Walking speed** starts at the square root of the factor, because a longer leg swings more slowly,
  and the exponent then climbs with the size toward 0.85 for the very largest. A flat square root
  was right near a person's size and hopeless at the top of the range: a sixteen-times body moving
  at four times normal speed covers a quarter of its own length where a person covers a whole one,
  which does not read as majestic, it reads as treacle. Below normal size it uses the factor as it is.
- **Gravity** does not scale at all, for anybody. Everyone falls at the same rate, so two players of
  different sizes who step off the same ledge land together. The jump launch is set so the height
  comes out where the rule above says it should.
- **Mining speed** uses the square root of the factor, so small players take a little longer over
  a block and big players a little less.
- **Water, lava, cobwebs, soul sand and spikes** hold a big player back less: the slowdown is
  divided by the factor, so twice the size means half the hindrance. Small players are left at the
  normal amounts, so a cobweb never becomes an inescapable trap.
- **Incoming damage** is divided by the factor, so size is the defence stat: a 2x player takes half
  and a 0.25x player takes double. Nobody's health bar changes length — everyone keeps one row of
  hearts. **A fall is the exception** — it is already charged by size below, and running it through
  both rules made a big player effectively immune to height and a small one take *more* than a
  full-sized person. Capped at four times either way, the same way knockback and water resistance
  are capped: uncapped, a 16x player took one point from anything and no normal player could ever
  win a fight.
- **Breath** follows the size on a three-quarter power, because how long you last under water is
  the store over the rate it is spent and both grow with the body. A 4x player holds it for about
  three quarters of a minute, a 16x one for two minutes, a quarter-size one for about five seconds.
  The bubble bar counts against your own lungs rather than a fixed fifteen seconds.
- **Pickup reach** follows the size too, capped at six blocks, so a giant sweeps up drops instead of
  having to stand twenty-eight blocks of body on a single seed, and a mouse has to walk to things.
- **Boats and minecarts** will not take a body past two-and-a-half times normal size: climbing in
  breaks them, and so does growing while already sitting in one. They drop what they would drop if
  you had broken them by hand. Large players cross water by wading or swimming and travel on their
  own legs, which at that size are quicker than a cart anyway.
- **Food** goes the other way, divided into the factor: a loaf is a crumb to a giant and a feast to
  someone the size of a cat. Together with the damage side that makes size a trade, not an upgrade.
- **Knockback** works from both ends: a big player swings with more behind them and is harder to
  shift, a small one is swatted across the room and barely rocks what they hit. Softened by a square
  root and capped at three times either way, so the extremes are a fight rather than a full stop.
- **Pistons** throw a small player further and a big one less.
- **Monsters notice big players first**: out of a group, the largest is the one that gets picked, and
  from up to three times as far away. Small players are overlooked in favour of whoever is behind
  them. A mob that has been hit keeps the target that hit it, whatever size anybody else is.
- **Creepers** stand off by the size of what they are walking at, so one does not detonate inside a
  giant's shin or half a street from somebody tiny.
- **Fall damage** goes both ways from normal size, on one continuous curve at every size. A big body
  is measured in body lengths, so a 2x player can fall twice as far unhurt. A small body is charged
  by the *square* of its size — a half-size player takes a quarter of the damage, a two-fifths one
  about a sixth — which is the square-cube law and the reason a mouse walks away from a fall that
  kills a horse.
- **Break particles** follow the size gently, on a square root and clamped, so little hands knock
  off little chips without a giant throwing boulders.
- The **walk animation** swings the full arc at every size and takes as long over it as a leg that
  length should: the amplitude is measured against the body's own walking pace and the cadence in
  body lengths. The **first-person bob** keeps a normal cadence too.
- **Fire** drawn on a burning player is sized to the player, rather than stacking one flame quad per
  block of height in a frame that has already been scaled.

## Installing

Drop the jar into the `mods` folder of a Babric (Fabric for BTA) instance. HalpLibe is bundled.
On a server, every player needs the mod too.

## Credits

- **Will** — [@wyntersnowstorm](https://github.com/wyntersnowstorm)
- **Way** — [@colinthepwner](https://github.com/colinthepwner)

All textures are **CongaSpy**'s, and they are licensed **for use with this mod and nothing else.**

Fine: playing the mod, running it on a server, handing someone the jar, including it in a modpack.

Not fine: pulling the images out for your own mod, resource pack or project; re-uploading them on
their own; editing them and passing the result on; or using them anywhere this mod is not. If you
want them for something else, ask CongaSpy first.
