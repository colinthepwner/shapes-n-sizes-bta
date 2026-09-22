import os
from PIL import Image

SRC = os.environ.get("DOOR_ART_DIR", "")
if not SRC:
    raise SystemExit("Set DOOR_ART_DIR to the folder holding the door drawings.")
BLOCK_OUT = os.path.join("src", "main", "resources", "assets", "shapesnsizes", "textures", "block", "door", "planks")
ITEM_OUT = os.path.join("src", "main", "resources", "assets", "shapesnsizes", "textures", "item")

DOORS = [("1x1", "short", 1), ("1x3", "tall", 3), ("1x4", "verytall", 4)]

os.makedirs(BLOCK_OUT, exist_ok=True)
os.makedirs(ITEM_OUT, exist_ok=True)

for prefix, name, height in DOORS:
    sheet = Image.open(os.path.join(SRC, prefix + "_DoorC.png")).convert("RGBA")
    assert sheet.size == (16, 16 * height), (prefix, sheet.size)
    for index in range(height):
        top = (height - 1 - index) * 16
        tile = sheet.crop((0, top, 16, top + 16))
        tile.save(os.path.join(BLOCK_OUT, "%s%d.png" % (name, index)))
    icon = Image.open(os.path.join(SRC, prefix + "_Door_ItemC.png")).convert("RGBA")
    assert icon.size == (16, 16), (prefix, icon.size)
    icon.save(os.path.join(ITEM_OUT, "door_%s.png" % name))
    print("%-9s %d segment(s) + icon" % (name, height))
