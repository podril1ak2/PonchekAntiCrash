# PonchekAntiCrash

Client-side packet firewall for Fabric 1.21.4. It inspects every packet the server sends
before Minecraft ever touches it and drops the ones that are built to crash the client —
the payloads behind commands such as `/fakelag crash <nick>`.

## How it works

The mod installs a Netty handler directly in front of the vanilla packet handler, so a
malicious packet is discarded on the network thread and never reaches the game loop.
Four guards run on every inbound packet:

| Guard | What it stops |
| --- | --- |
| `rate` | Packet floods — particles, sounds, explosions, entity spawns, titles, boss bars, scoreboards, inventories and 25 other packet types are rate limited per second. |
| `particle` | Single packets asking for an absurd number of particles. |
| `sound` | Sounds with an out-of-range volume or pitch. |
| `structure` | `NaN`/`Infinity` coordinates, velocities and knockback values, plus text-component bombs (oversized, deeply nested or heavily branched chat/title/boss-bar text). |

The `structure` guard walks packet fields reflectively instead of hardcoding packet
layouts, so it also covers packets that are not in the rate-limit table.

A second layer wraps vanilla packet dispatch in a safety net: if a packet still manages to
throw on the main thread, the exception is caught and reported instead of ending the game.

## Commands

| Command | Effect |
| --- | --- |
| `/anticrash` | Show state and how many packets were blocked this session |
| `/anticrash on` / `/anticrash off` | Enable or disable the firewall |
| `/anticrash reload` | Re-read the config file |
| `/anticrash reset` | Clear the session statistics |

## Configuration

`.minecraft/config/ponchekanticrash.json` is written on first launch. Every threshold —
rate limits per packet type, particle cap, sound range, text-component limits, chat
notifications — can be tuned there. Unknown or missing keys fall back to defaults.

## Building

```
./gradlew build
```

The mod jar is written to `build/libs/`. Requires JDK 21 and Fabric API.

## License

MIT
