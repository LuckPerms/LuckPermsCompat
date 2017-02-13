# LuckPermsCompat [![Build Status](https://ci.lucko.me/job/LuckPermsCompat/badge/icon)](https://ci.lucko.me/job/LuckPermsCompat/)
A command compatibility bridge for LuckPerms


## How does it work
The plugin simply remaps the commands of other permission systems, and directs the calls to LuckPerms instead.

It uses the equivalences found in the [LuckPerms Wiki](https://github.com/lucko/LuckPerms/wiki/GM-&-PEX-Command-Equivalents).

### Usage
Just install it into your plugins folder, and use commands as normal.

Only users with the "luckpermscompat.use" permission are able to view and use the remapped commands.

### Supported plugins
Currently just GroupManager. PEX will probably be added soon.

## Requirements
* LuckPerms v2.17.63 or newer
