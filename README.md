# LuckPermsCompat [![Build Status](https://ci.lucko.me/job/LuckPermsCompat/badge/icon)](https://ci.lucko.me/job/LuckPermsCompat/)
A command compatibility bridge for LuckPerms

## Downloads
You can get the latest version [here](https://ci.lucko.me/job/LuckPermsCompat/lastSuccessfulBuild/artifact/target/LuckPermsCompat.jar).

## How does it work
The plugin simply remaps the commands of other permission systems, and directs the calls to LuckPerms instead.

It uses the equivalences found in the [LuckPerms Wiki](https://github.com/lucko/LuckPerms/wiki/GM-&-PEX-Command-Equivalents).

### Usage
Just install it into your plugins folder, and keep using PEX and GroupManager commands as normal.

Only users with the "**luckpermscompat.use**" permission are able to view and use the remapped commands.

### Which commands work?
This plugin is based upon the respective plugin documentation for [GroupManager](http://wiki.mc-ess.net/wiki/Group_Manager/Commands) and [PermissionsEx](https://github.com/PEXPlugins/PermissionsEx/wiki/Commands).

I make no promises about the accuracy or reliability of the remapping. I encourage that if you're using this plugin to handle backwards compatibility with BuyCraft / Reward scripts, you move try to move to the LuckPerms commands instead. (they're probably much more reliable, and expose more features!)

### Supported plugins
Currently just PermissionsEx and GroupManager.

### Requirements
* LuckPerms v2.17.63 or newer
* The permission plugin being remapped does **not** need to be installed.