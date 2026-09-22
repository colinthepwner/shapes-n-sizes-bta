import os

from PIL import Image

SRC = os.environ.get("BROWNIE_ART_DIR", "")
if not SRC:
    raise SystemExit("Set BROWNIE_ART_DIR to the folder holding the burnt brownie drawings.")

ITEMS = os.path.join("src", "main", "resources", "assets", "shapesnsizes", "textures", "item")

DRAWINGS = [("brownie_B_burnt", "browniebigcharred"),
            ("brownie_O_burnt", "browniesmallcharred")]

CLEAR = (0, 0, 0, 0)


def is_spot(px):
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

    art.save(os.path.join(ITEMS, name + ".png"))
    layer = Image.new("RGBA", art.size)
    layer.putdata(glow)
    layer.save(os.path.join(ITEMS, name + "glow.png"))
    print("%-14s -> %s (+glow), %d spots lit, %d crumb dark" % (source, name, spots, crumb))
