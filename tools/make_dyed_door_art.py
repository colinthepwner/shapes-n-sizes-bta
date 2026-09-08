"""
Recolours the door art into the sixteen dye colours, using the game's own palettes.

The colours are not invented here and are not eyeballed. Every one of BTA's painted doors turns out
to be a strict per-pixel palette map of its plain oak door -- the same silhouette, the same shading,
with each of the oak texture's colours replaced by a fixed counterpart -- so the map from oak to any
dye can be read straight out of the game's files by comparing the two. Applying that same map to
this mod's hand-drawn doors gives dyed doors whose colours are the game's, exactly, rather than
merely close: a blue tall door is shaded with the same blues as a blue vanilla door standing next to
it.

That only works because the hand-drawn art was drawn from the oak door's palette and uses no colour
outside it. The script checks that rather than assuming it, and refuses rather than guessing, since
a silent nearest-colour fallback is how one wrong pixel becomes sixteen wrong textures.

Two palettes are read, because the game keeps two:
  - the block texture map, from block/door/planks -> block/door/planks_<colour>
  - the item icon map, from item/door_oak -> item/door_<colour>

Point BTA_JAR at a BTA client or merged jar before running. Loom keeps a deobfuscated one under
~/.gradle/caches/fabric-loom/minecraftMaven/, and any of them carries the assets.

Run from the project root:  python tools/make_dyed_door_art.py
"""
import os
import zipfile

from PIL import Image

JAR = os.environ.get("BTA_JAR", "")
if not JAR:
    raise SystemExit("Set BTA_JAR to a BTA jar containing the vanilla assets.")

# Block metadata order, which is the order the game stores a dye in and the order the colour nibble
# counts through. The names are DyeColor.colorID and are what both the game's asset paths and this
# mod's use.
COLORS = ["white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray",
          "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"]

DOORS = [("short", 1), ("tall", 3), ("verytall", 4)]

ASSETS = os.path.join("src", "main", "resources", "assets", "shapesnsizes", "textures")
BLOCK_SRC = os.path.join(ASSETS, "block", "door", "planks")
ITEM_DIR = os.path.join(ASSETS, "item")

# The vanilla door is drawn as three textures and the item as one. All four are read for the map
# because between them they use more of the palette than any single one does, and a colour missing
# from the map is a colour this script cannot recolour.
VANILLA_BLOCK_PARTS = ["bottom", "top", "frame_top"]


def load(jar, path):
    with jar.open(path) as f:
        return Image.open(f).convert("RGBA")


def build_map(jar, plain_paths, dyed_paths):
    """The colour-for-colour substitution that turns the plain texture into the dyed one.

    Transparent pixels are skipped entirely. The game's files are inconsistent about which
    fully-transparent colour they store -- some are (0,0,0,0) and some (255,255,255,0) -- and
    treating those as real palette entries produces a conflict that means nothing, since neither
    is ever drawn.
    """
    mapping = {}
    for plain_path, dyed_path in zip(plain_paths, dyed_paths):
        plain = load(jar, plain_path)
        dyed = load(jar, dyed_path)
        if plain.size != dyed.size:
            raise SystemExit("size mismatch: %s vs %s" % (plain_path, dyed_path))
        for src, dst in zip(plain.getdata(), dyed.getdata()):
            if src[3] == 0:
                continue
            if src in mapping and mapping[src] != dst:
                raise SystemExit("inconsistent palette map at %s: %s -> %s and %s"
                                 % (dyed_path, src, mapping[src], dst))
            mapping[src] = dst
    return mapping


def recolour(image, mapping, where):
    out = Image.new("RGBA", image.size)
    pixels = []
    for src in image.getdata():
        if src[3] == 0:
            pixels.append(src)
            continue
        if src not in mapping:
            raise SystemExit(
                "%s uses %s, which is not in the vanilla door palette, so there is no game colour "
                "to match it to. Redraw that pixel with a colour from the oak door." % (where, src))
        pixels.append(mapping[src])
    out.putdata(pixels)
    return out


jar = zipfile.ZipFile(JAR)
V = "assets/minecraft/textures"

written = 0
for color in COLORS:
    block_map = build_map(
        jar,
        ["%s/block/door/planks/%s.png" % (V, p) for p in VANILLA_BLOCK_PARTS],
        ["%s/block/door/planks_%s/%s.png" % (V, color, p) for p in VANILLA_BLOCK_PARTS],
    )
    item_map = build_map(jar, ["%s/item/door_oak.png" % V], ["%s/item/door_%s.png" % (V, color)])

    out_dir = os.path.join(ASSETS, "block", "door", "planks_" + color)
    os.makedirs(out_dir, exist_ok=True)
    for name, height in DOORS:
        for index in range(height):
            tile = "%s%d.png" % (name, index)
            src = os.path.join(BLOCK_SRC, tile)
            recolour(Image.open(src).convert("RGBA"), block_map, src).save(os.path.join(out_dir, tile))
            written += 1
        icon = os.path.join(ITEM_DIR, "door_%s.png" % name)
        recolour(Image.open(icon).convert("RGBA"), item_map, icon).save(
            os.path.join(ITEM_DIR, "door_%s_%s.png" % (name, color)))
        written += 1
    print("%-10s %d tiles + %d icons" % (color, sum(h for _, h in DOORS), len(DOORS)))

print("wrote %d texture files" % written)
