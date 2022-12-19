# [![SchematicUpload Banner](images/banner-graphic.png)](https://github.com/WiIIiam278/SchematicUpload)
[![GitHub CI](https://img.shields.io/github/actions/workflow/status/WiIIiam278/SchematicUpload/gradle.yml?branch=master&logo=github)](https://github.com/WiIIiam278/SchematicUpload/actions/workflows/gradle.yml)
[![Support Discord](https://img.shields.io/discord/818135932103557162.svg?label=&logo=discord&logoColor=fff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/tVYhJfyDWG)

**SchematicUpload** is a plugin that lets players upload schematics to your server through a simple and safe web panel. If you've ever run a build or creative server and have had to respond to annoying requests to import a player's schematic, this is the plugin for you.

![Web interface screenshot](images/web-interface.png)

## Usage
Players simply type the `/schematicupload` command in-game that sends them a link to the web panel with a unique authentication code. On the panel, all the player needs to do is choose the file and press "Upload". The schematic will be checked and uploaded to the schematics' folder in-game.

## Features
* Incredibly simple to use web interface that lets you preview schematics before uploading
* Super easy to configure, just choose your port and hostname
* Limit the file size of schematics that can be uploaded
* Limit how many schematics a player can upload over a given timeframe

![In-game command screenshot](images/in-game-command.png)

## Commands
SchematicUpload provides the following commands. By default, only operators are permitted to use the schematic uploader. You'll need a permission plugin such as LuckPerms to let non-operators use these.

| Command                   | Description                                       | Permission                       |
|---------------------------|---------------------------------------------------|----------------------------------|
| `/schematicupload`        | Upload a schematic file through the web interface | `schematicupload.command`        |
| `/schematicupload about`  | View plugin information                           | `schematicupload.command.about`  |
| `/schematicupload reload` | Reload config and message files                   | `schematicupload.command.reload` |

## Setup
1. Download the SchematicUpload jar and place it in your `/plugins/` directory
2. Start the server. The web server will be hosted on port 2780 by default. If you're running the server on your computer, you can visit `https://localhost:2780` to view the interface.
3. Turn off the server, navigate to `/plugins/SchematicUpload` and modify the contents of `config.yml` and `messages_xx-xx.yml` as appropriate. Make sure to change `url` to be the IP address or domain of your server.

## Building
To build SchematicUpload, simply run the following in the root of the repository:
```
./gradlew clean build
```

## License
SchematicUpload is licensed under [Apache-2.0 License](https://github.com/WiIIiam278/SchematicUpload/blob/master/LICENSE).

## bStats
This plugin uses bStats to provide me with metrics about its usage:
- [bStats Metrics](https://bstats.org/plugin/bukkit/SchematicUpload/14611)

You can turn metric collection off by navigating to `~/plugins/bStats/config.yml` and editing the config to disable plugin metrics.

## Links
- [Resource Page](https://www.spigotmc.org/resources/schematicupload.100657/)
- [Bug Reports](https://github.com/WiIIiam278/SchematicUpload/issues)
- [Discord Support](https://discord.gg/tVYhJfyDWG)


---
&copy; [William278](https://william278.net/), 2022. Licensed under the Apache-2.0 License.
