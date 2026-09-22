import json
import os

BASE = os.path.join("src", "main", "resources", "assets", "shapesnsizes", "models", "block", "door")
OUT = os.path.join(BASE, "planks")
DOORS = [("short", 1), ("tall", 3), ("verytall", 4)]

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
