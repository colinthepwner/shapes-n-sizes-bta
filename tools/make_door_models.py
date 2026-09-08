"""
Writes a block model file for every door segment, in all four hinge/open combinations.

The geometry and UVs are the game's own door models, copied face for face -- a door is a slab three
pixels deep against one wall of the block, and which wall depends on the hinge and whether it is
open. What changes here is only the texture each segment points at, since every block of a hand-
drawn door has its own tile.

Head and sill faces are only drawn where the door actually ends: the top of the topmost segment and
the bottom of the bottom one. Drawing them on a middle segment would put a lid across the middle of
a tall door.

The dyed doors get the same treatment, one folder per colour, laid out the way the game lays its
own painted doors out: the geometry is identical and only the texture path moves. They are written
from the same table rather than as `parent` references to the plain models, because a segment's
texture is named after the segment and a parent-and-override file would be the same size as the
model it overrides.

Run from the project root:  python tools/make_door_models.py
"""
import json
import os

BASE = os.path.join("src", "main", "resources", "assets", "shapesnsizes", "models", "block", "door")
OUT = os.path.join(BASE, "planks")
DOORS = [("short", 1), ("tall", 3), ("verytall", 4)]

# DyeColor.colorID in block-metadata order, matching tools/make_dyed_door_art.py and ShapesDoors.
COLORS = ["white", "orange", "magenta", "lightblue", "yellow", "lime", "pink", "gray",
          "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"]

SIDES = {
    "left":       dict(north=[3, 0, 0, 16], south=[0, 0, 3, 16], west=[0, 0, 16, 16], east=[16, 0, 0, 16]),
    "left_open":  dict(north=[0, 0, 3, 16], south=[0, 0, 3, 16], west=[16, 0, 0, 16], east=[0, 0, 16, 16]),
    "right":      dict(north=[3, 0, 0, 16], south=[0, 0, 3, 16], west=[16, 0, 0, 16], east=[0, 0, 16, 16]),
    "right_open": dict(north=[3, 0, 0, 16], south=[3, 0, 0, 16], west=[0, 0, 16, 16], east=[16, 0, 0, 16]),
}
DOWN_UV = {"left": [16, 13, 0, 16], "left_open": [0, 16, 16, 13], "right": [0, 13, 16, 16], "right_open": [16, 16, 0, 13]}
UP_UV = {"left": [0, 3, 16, 0], "left_open": [16, 0, 0, 3], "right": [16, 3, 0, 0], "right_open": [0, 0, 16, 3]}


def model(texture, variant, cap_top, cap_bottom):
    faces = {}
    if cap_bottom:
        faces["down"] = {"uv": DOWN_UV[variant], "texture": "#door", "cullface": "down", "rotation": 90}
    if cap_top:
        faces["up"] = {"uv": UP_UV[variant], "texture": "#door", "cullface": "up", "rotation": 90}
    for face, uv in SIDES[variant].items():
        entry = {"uv": uv, "texture": "#door"}
        # The east face is the one that meets the rest of the door frame; leaving it unculled
        # matches the game's own models and stops the edge vanishing against a neighbour.
        if face != "east":
            entry["cullface"] = face
        faces[face] = entry
    return {
        "ambientocclusion": False,
        "textures": {
            "door": texture, "overlay": "#door",
            "particle_up": "#door", "particle_down": "#door", "particle_north": "#door",
            "particle_south": "#door", "particle_west": "#door", "particle_east": "#door",
        },
        "elements": [{"from": [0, 0, 0], "to": [3, 16, 16], "faces": faces}],
    }


def write_set(out_dir, texture_dir):
    """One folder of models: every segment of every door, in all four hinge/open combinations."""
    os.makedirs(out_dir, exist_ok=True)
    written = 0
    for name, height in DOORS:
        for index in range(height):
            texture = "shapesnsizes:%s/%s%d" % (texture_dir, name, index)
            for variant in SIDES:
                path = os.path.join(out_dir, "%s%d_%s.json" % (name, index, variant))
                with open(path, "w") as f:
                    json.dump(model(texture, variant, index == height - 1, index == 0), f, indent=2)
                written += 1
    return written


count = write_set(OUT, "block/door/planks")
for color in COLORS:
    count += write_set(os.path.join(OUT, color), "block/door/planks_" + color)
print("wrote %d model files" % count)
