# KeypadCraft - J2ME version

Touchscreen + keypad J2ME (MIDP 2.0 / CLDC 1.1) version. Source and compiled `Blocks.jar` are both here.

- `BlocksCanvas.java`, `BlocksMIDlet.java` - source of version 1.0 (the 1.0.1 inventory screen is only in the jar for now; its source will be added)
- `Blocks.jar` - compiled, ready to install (J2ME Loader, FreeJ2ME, KEmulator, real phones)
- `textures/`, `lang.txt`, `lang_tr.txt` - resources (see `TEXTURES_HOWTO.txt`, `TEXT_HOWTO.txt`)
- `build.sh`, `MANIFEST.MF` - rebuild

## Controls
Touch: tap a block face to place a block next to it, drag to pan, bottom bar: `-` `+` zoom, `<` `>` change block, `Put/Del`, `?` help.
Keypad: d-pad or 2/4/6/8 pan, 5/OK place/remove at the red crosshair, 1/3 zoom, 7/9 previous/next block, `*` Put/Del mode, `#` help.

## License / credits
See [COPYRIGHT.txt](COPYRIGHT.txt) and [LICENSE](LICENSE). Block textures are Minecraft-style art (c) Mojang / Microsoft. Unofficial fan project.
