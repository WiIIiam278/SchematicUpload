[![HuskSync Banner](images/banner-graphic.png)](https://github.com/WiIIiam278/SchematicUpload)

# SchematicUpload

[![Discord](https://img.shields.io/discord/818135932103557162?color=7289da&logo=discord)](https://discord.gg/tVYhJfyDWG)

**SchematicUpload** is a plugin that lets players upload schematics to your server through a simple and safe web panel.
If you've ever run a build or creative server and have had to respond to annoying requests to import a player's
schematic, this is the plugin for you.

## How it works

Players simply type the `/schematicupload` command in-game that sends them a link to the web panel with a unique
authentication code. On the panel, all the player needs to do is choose the file and press "Upload". The schematic will
be checked and uploaded to the schematics' folder in-game.

## Features

* Incredibly simple to use web interface
* Super easy to configure, just choose your port and hostname
* Limit the file size of schematics that can be uploaded
* Limit how many schematics a player can upload over a given timeframe

## Screenshots

![Screenshot of the in-game command](images/in-game-command.png)
![Screenshot of the web interface](images/web-interface.png)

## Commands

SchematicUpload provides the following commands. By default, only operators are permitted to use the schematic uploader.
You'll need a permission plugin such as LuckPerms to let non-operators use these.

| Command                   | Description                                       | Permission                       |
|---------------------------|---------------------------------------------------|----------------------------------|
| `/uploadschematic`        | Upload a schematic file through the web interface | `schematicupload.command`        |
| `/uploadschematic about`  | View plugin information                           | `schematicupload.command.about`  |
| `/uploadschematic reload` | Reload config and message files                   | `schematicupload.command.reload` |

## Setup

1. Download the SchematicUpload jar and place it in your `/plugins/` directory
2. Start the server. The web server will be hosted on port 2780 by default. If you're running the server on your
   computer, you can visit `https://localhost:2780` to view the interface.
3. Turn off the server, navigate to `/plugins/SchematicUpload` and modify the contents of `config.yml` and
   `messages_xx-xx.yml` as appropriate. Make sure to change `url` to be the IP address or domain of your server.

### bStats

This plugin uses bStats to provide me with metrics about its usage. You can turn this off by navigating
to `plugins/bStats/config.yml` and editing the config to disable plugin metrics.

View bStats metrics: [Click to View](https://bstats.org/plugin/bukkit/SchematicUpload/14611)
