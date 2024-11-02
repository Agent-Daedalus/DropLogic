# DropLogic
DropLogic is a fabric mod designed to remove the randomness in block drops, for puzzleboxes or other niche scenarios

## Setup
The latest version of DropLogic can be downloaded from Modrinth or the releases tab (if i don't forget). 
DropLogic will only change logic if the customDropLogic gamerule is set to true.

## Usage
Enable the customDropLogic gamerule, now all block drops (and only block drops) from the same position and type will have the same offset and velocity when broken.

## Commands
/setDropMotion <blockPos> <dropped-item|all> <offset> <velocity>

/rerollDropMotion <blockPos> <dropped-item|all> <offset> <velocity>

example: /setDropMotion 0 100 0 minecraft:cobblestone 0.0 0.1 0.0 0.0 0.2 0.0, has all blocks that drop cobblestone at (0, 100, 0) drop with an offset of (0.0, 0.1 0.0) and a velocity straight up (0.0, 0.2, 0.0).

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
