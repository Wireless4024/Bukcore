Bukcore
===========
a core plugin for my plugins with [Kotlin](https://kotlinlang.org) feature

## Download 
[Bukkit](https://dev.bukkit.org/projects/bukcore) [Spigot](https://www.spigotmc.org/resources/bukcore.84075/) (same file but different website)

#### Note
+ if you can spot a bug please tell me in issue
+ any question or suggestion please ask in issue
+ if you want to improve it please open pull request
+ I don't have much time to maintenance this project but I'll make sure everything is work

### Commands
| command | aliases | usage  | permission |
|:-------:|:-------:|:-------|:-----------|
|`bukcore`| None    | `/bukcore <args>` |`bukcore.bukcore`
|`chat`   | None    | `/chat <player> <message>` |`bukcore.chat`
|`gc`     | None    | `/gc`             |`bukcore.gc`
|`itemdata`| None   | `/itemdata [json NBT]`     |`bukcore.itemdata`
|`loadchunk`| None  | `/loadchunk <area> [period] [count]`     |`bukcore.itemdata`
|`openchest`| `oc`  | `/openchest [x] [y] [z] [world]`  | `bukcore.openchest`
|`pickblock`| `pick`| `/pickblock`                      | `bukcore.pickblock`
|`rainbowchat`| `rc`| `/rainbowchat <message>`          | `bukcore.rainbowchat`
|`randomteleport`| `rtp`| `/randomteleport`             | `bukcore.rtp`
|`sortinventory` |`sortinv`| `/sortinventory [player]`  | `bukcore.sortinv`
| | | `/sortinventory [x] [y] [z] [world]`      | `bukcore.sortinv`

#### Command description
+ `/bukcore`
    - `/bukcore reload` : reload bukcore configuration
    - `/bukcore version` : get bukcore version
+ `/chat`
    - `/chat <player> <message>` \: force player to send 
        * `<player>` \: player name
        * `<message>` \: a message to send
        > `/chat Wireless4024 hi` \: make player name `Wireless4024` to send `hi`  
        `/chat Wireless4024 /home` \: make player name `Wireless4024` to use command `/home`
+ `/gc` \: call `java.lang.System#gc`
    > why? \: java sometime didn't clean all objects at once 
      and it's leaving some object when garbage collected  
      so this command will allow to call gc manually if you want :D  
      Note \: do not call this command too many due gc pause might affect server performance
+ `/itemdata` \: read NBT from item in main hand
    - `[json NBT]` \: NBT to add/edit/remove in json format
        > `/itemdata {"test":1}` \: modify or add nbt name `test` with value `1`  
        > `/itemdata {"Unbreakable":1}` \: make item unbreakable

+ `/loadchunk <area> [counts] [period]` \: load chunks around player  
    - `<area>` \: amount of chunks around player
    - `[counts]` \: amount of chunks per period
    - `[period]` \: delay between load in tick
    
+ `/openchest` \: open chest that player looking
    - `[x]` \: x coordinate to find chest (if blank or `~` will use player location)
    - `[y]` \: y coordinate to find chest (if blank or `~` will use player location)
    - `[z]` \: z coordinate to find chest (if blank or `~` will use player location)
    - `[world]` \: world to find chest (if blank or `~` will use player world)

+ `/pickblock` \: pick up looking block and destroy it silently
+ `/rainbowchat <message>` \: send a message to all online player
+ `/randomteleport` \: teleport to random location in the world
+ `/sortinventory [player]` \: sort a player inventory 

### Build from source
+ build Bukcore with kotlin stdlib
    ```shell script
    gradlew build && gradlew fatJar
    ```
    output file : `build/libs/bukcore-<version>-full.jar`
+ build only Bukcore (if other plugin having kotlin-stdlib)
    ```shell script
    gradlew build && gradlew jar
    ```
    output file : `build/libs/bukcore-<version>.jar`
    
### for developer 
   > please use `bukcore-<version>.jar` due `kotlin libs` might hide our classes  
+ to build
    ```shell script
    gradlew build && gradlew all
    ```
    - `build/libs/bukcore-<version>.jar` : a file used to import as library
    - `build/libs/bukcore-<version>-source.jar` : a file used to view original source code
    - `build/libs/bukcore-<version>-full.jar` : a file used to run in server
    
