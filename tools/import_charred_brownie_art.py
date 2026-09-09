"""
Imports the hand-drawn burnt brownie art, and splits the glowing bits out of it.

The charred brownies are drawn rather than computed. An earlier version of this script tried to
burn the ordinary brownie art by arithmetic -- darken the crumb, keep the spots -- and it worked,
but a drawn one is better: the crumb reads as charcoal instead of as a dimmer brownie, and the
spots are drawn hot rather than merely left alone.

What is still worth doing here is the second file. The item is rendered in two passes so that the
spots stay bright in the dark while the crumb goes down with everything around it, and the second
pass needs the spots on their own, on transparency. Cutting that by hand is a chore and a thing to
forget when the art is redrawn, so it is cut here instead, from the art itself.

Crumb and spots separate on the same two tests the earlier version used, and the drawn art makes
them wider apart than ever: the spots are either bluer than they are red, or brighter than the
crumb ever gets -- the darkest spot sits at 174 where the lightest crumb pixel reaches 45. The
split is checked rather than assumed, and the script stops if either side comes back empty, because
a silently miscategorised texture looks fine right up until it is in the game.

Which is which: B is the blue-spotted one, from the growing brownie's lapis; O is the orange one,
from the shrinking brownie's dye.

Point BROWNIE_ART_DIR at the folder holding brownie_B_burnt.png and brownie_O_burnt.png.

Run from the project root:  python tools/import_charred_brownie_art.py
"""
import os

from PIL import Image

SRC = os.environ.get("BROWNIE_ART_DIR", "")
if not SRC:
    raise SystemExit("Set BROWNIE_ART_DIR to the folder holding the burnt brownie drawings.")

ITEMS = os.path.join("src", "main", "resources", "assets", "shapesnsizes", "textures", "item")

# source drawing -> the mod texture it becomes. The mod's names carry no underscore, matching the
# plain brownies they are burnt copies of.
DRAWINGS = [("brownie_B_burnt", "browniebigcharred"),
            ("brownie_O_burnt", "browniesmallcharred")]

CLEAR = (0, 0, 0, 0)


def is_spot(px):
    """A pixel that survived the fire still glowing, as opposed to one that turned to charcoal."""
    r, g, b = px[0], px[1], px[2]
    return b > r or max(r, g, b) > 120


for source, name in DRAWINGS:
    art = Image.open(os.path.join(SRC, source + ".png")).convert("RGBA")
    if art.size != (16, 16):
        raise SystemExit("%s is %dx%d; an item icon has to be 16x16." % (source, art.size[0], art.size[1]))

    glow = []
    spots = 0
    crumb = 0
    for px in art.getdata():
        if px[3] == 0:
            glow.append(CLEAR)
        elif is_spot(px):
            spots += 1
            glow.append(px)
        else:
            crumb += 1
            glow.append(CLEAR)
    if not spots or not crumb:
        raise SystemExit("%s split into %d spot and %d crumb pixels; the test above no longer "
                         "matches the art." % (source, spots, crumb))

    # The drawing goes in untouched. Whatever the artist did to it is the texture.
    art.save(os.path.join(ITEMS, name + ".png"))
    layer = Image.new("RGBA", art.size)
    layer.putdata(glow)
    layer.save(os.path.join(ITEMS, name + "glow.png"))
    print("%-14s -> %s (+glow), %d spots lit, %d crumb dark" % (source, name, spots, crumb))
