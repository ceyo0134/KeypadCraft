# KeypadCraft

A block-building Java (J2ME) game for **keypad phones** (and touchscreens).
Isometric blocks, random worlds, 24 block types, tiny and fast.

**Download page (APK + JAR + links):** https://ceyo0134.github.io/KeypadCraft/
**Test it in your browser:** https://ceyo0134.github.io/KeypadCraft/play/  (web version, v1.1)

## Controls
Touch / mouse: tap a block face to place a block next to it, drag to pan, bottom bar: `-` `+` zoom, `<` `>` change block, `Put/Del`, `?` help.
Keypad / keyboard: arrows or 2/4/6/8 pan, 5 / Enter / Space place or remove at the red crosshair, 1/3 zoom, 7/9/0 change block, `*` Put/Del mode, `#` or H help. Mouse wheel zooms.

## Android APK
Source in [`android/`](android/) (a small WebView app running the bundled web copy offline). GitHub Actions builds it and attaches `KeypadCraft.apk` to the [latest release](../../releases/latest).

## Web version
Source in [`web/`](web/) (MIT, see `web/LICENSE`). `web/dist/blocks.html` is the single-file build; the same file is published in [`play/`](play/). Rebuild with `python3 web/tools/build.py`.

## Downloads / older versions
Latest: [release v1.1](../../releases/latest). Older jar and APK: [release v1.0](../../releases/tag/v1.0).

## Java (J2ME) version
Source + compiled jar: [`j2me/`](j2me/) - `j2me/Blocks.jar` runs on J2ME Loader, FreeJ2ME, KEmulator and real keypad phones.

## License / credits
Usage terms: see [COPYRIGHT.txt](COPYRIGHT.txt) (credit required) and [LICENSE](LICENSE).

- Code: Copyright (c) 2026 Ceyo (Snapchat: ceyo0134). The Java (J2ME) game and its source; the browser version is only a test copy of the jar.
- Game based on [Blocks-TI-84](https://github.com/TheScienceElf/Blocks-TI-84) by TheScienceElf (MIT License, Copyright (c) 2023 The Science Elf) - see [LICENSE](LICENSE).
- Block textures are Minecraft-style art. Minecraft is a trademark of Mojang AB / Microsoft; the original textures belong to Mojang Studios.

**NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.**
