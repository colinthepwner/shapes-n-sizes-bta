import os
import zipfile

from PIL import Image

JAR = os.environ.get("BTA_JAR", "")
if not JAR:
    raise SystemExit("Set BTA_JAR to a BTA jar containing the vanilla assets.")

COLORS = ["white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray",
          "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"]

DOORS = [("short", 1), ("tall", 3), ("verytall", 4)]

ASSETS = os.path.join("src", "main", "resources", "assets", "shapesnsizes", "textures")
BLOCK_SRC = os.path.join(ASSETS, "block", "door", "planks")
ITEM_DIR = os.path.join(ASSETS, "item")

VANILLA_BLOCK_PARTS = ["bottom", "top", "frame_top"]


def load(jar, path):
    with jar.open(path) as f:
        return Image.open(f).convert("RGBA")


def build_map(jar, plain_paths, dyed_paths):
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
