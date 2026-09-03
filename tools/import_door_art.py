"""
Imports the hand-drawn door art into the mod.

The doors are drawn as whole doors -- 16 wide by 16 times their height -- which is how a person
draws a door and not how the block renderer wants one. The renderer asks for a separate 16x16
texture per block, so each drawing is sliced into one tile per segment here.

Watch the direction: image row 0 is the TOP of the door, while segment 0 is the block on the
GROUND. The slice order is therefore reversed, and getting it wrong produces a door that looks
almost right and is upside down.

Run from the project root:  python tools/import_door_art.py
"""
import os
from PIL import Image

# Where the source drawings live. No default: point DOOR_ART_DIR at the folder holding
# 1x1.png, 1x3.png and 1x4.png before running this.
SRC = os.environ.get("DOOR_ART_DIR", "")
if not SRC:
    raise SystemExit("Set DOOR_ART_DIR to the folder holding the door drawings.")
BLOCK_OUT = os.path.join("src", "main", "resources", "assets", "shapesnsizes", "textures", "block", "door", "planks")
ITEM_OUT = os.path.join("src", "main", "resources", "assets", "shapesnsizes", "textures", "item")

# source name -> (mod name, how many blocks tall)
DOORS = [("1x1", "short", 1), ("1x3", "tall", 3), ("1x4", "verytall", 4)]

os.makedirs(BLOCK_OUT, exist_ok=True)
os.makedirs(ITEM_OUT, exist_ok=True)

for prefix, name, height in DOORS:
    sheet = Image.open(os.path.join(SRC, prefix + "_DoorC.png")).convert("RGBA")
    assert sheet.size == (16, 16 * height), (prefix, sheet.size)
    for index in range(height):
        # index counts up from the ground; the drawing counts down from the head.
        top = (height - 1 - index) * 16
        tile = sheet.crop((0, top, 16, top + 16))
        tile.save(os.path.join(BLOCK_OUT, "%s%d.png" % (name, index)))
    icon = Image.open(os.path.join(SRC, prefix + "_Door_ItemC.png")).convert("RGBA")
    assert icon.size == (16, 16), (prefix, icon.size)
    icon.save(os.path.join(ITEM_OUT, "door_%s.png" % name))
    print("%-9s %d segment(s) + icon" % (name, height))
