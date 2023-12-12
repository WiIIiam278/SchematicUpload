<!--suppress ALL -->
<p align="center">
    <img src="images/banner.png" alt="SchematicUpload" />
    <a href="https://github.com/WiIIiam278/SchematicUpload/actions/workflows/ci.yml">
        <img src="https://img.shields.io/github/actions/workflow/status/WiIIiam278/SchematicUpload/ci.yml?branch=master&logo=github"/>
    </a> 
    <a href="https://discord.gg/tVYhJfyDWG">
        <img src="https://img.shields.io/discord/818135932103557162.svg?label=&logo=discord&logoColor=fff&color=7389D8&labelColor=6A7EC2" />
    </a> 
    <br/>
    <b>
        <a href="https://www.spigotmc.org/resources/schematicupload.100657/">Spigot</a>
    </b> —
    <b>
        <a href="https://william278.net/project/schematicupload">About</a>
    </b> — 
    <b>
        <a href="https://github.com/WiIIiam278/SchematicUpload/issues">Issues</a>
    </b>
</p>
<br/>

**SchematicUpload** is a plugin that lets players upload schematics to your server through a simple and safe web panel.
If you've ever run a build or creative server and have had to respond to annoying requests to import a player's
schematic, this is the plugin for you.

![Screenshot of the web interface](images/web-interface.png)

## Features
**⭐ Easy-to-use** &mdash; Incredibly simple to use web interface for uploading schematics.

**⭐ Super easy to configure** &mdash; Simply set a port and hostname!

**⭐ Less hassle for players** &mdash; No more annoying requests to manually upload schematics!

**⭐ Great admin features** &mdash; Limit how many schematics a player can upload over a given timeframe.

## How it works

Players simply type the `/schematicupload` command in-game that sends them a link to the web panel with a unique
authentication code. On the panel, all the player needs to do is choose the file and press "Upload." The schematic will
be checked and uploaded to the schematics' folder in-game.

![Screenshot of the in-game command](images/in-game-command.png)

## Commands

SchematicUpload provides the following commands. By default, only operators are permitted to use the schematic uploader.
You'll need a permission plugin such as LuckPerms to let non-operators use these.

| Command                     | Description                                       | Permission                         |
|-----------------------------|---------------------------------------------------|------------------------------------|
| `/uploadschematic`          | Upload a schematic file through the web interface | `schematicupload.command`          |
| `/uploadschematic about`    | View plugin information                           | `schematicupload.command.about`    |
| `/uploadschematic reload`   | Reload config and message files                   | `schematicupload.command.reload`   |
| `/downloadschematic (name)` | Get a download link for a schematic               | `schematicupload.command.download` |

## Setup

1. Download the SchematicUpload jar and place it in your `/plugins/` directory
2. Start the server. The web server will be hosted on port 2780 by default. If you're running the server on your
   computer, you can visit `https://localhost:2780` to view the interface.
3. Turn off the server, navigate to `/plugins/SchematicUpload` and modify the contents of `config.yml` and
   `messages_xx-xx.yml` as appropriate. Make sure to change `url` to be the IP address or domain of your server.

## License
SchematicUpload is licensed under the Apache 2.0 license.

- [License](https://github.com/WiIIiam278/SchematicUpload/blob/master/LICENSE)

## Translations
Translations of the plugin locales are welcome to help make the plugin more accessible. Please submit a pull request with your translations as a `.yml` file.

- [Locales Directory](https://github.com/WiIIiam278/SchematicUpload/tree/master/src/main/resources/locales)
- [English Locales](https://github.com/WiIIiam278/SchematicUpload/tree/master/src/main/resources/locales/en-gb.yml)

## Links
- [Spigot](https://www.spigotmc.org/resources/schematicupload.100657/) &mdash; View the plugin Modrinth page (Also: [Modrinth](https://modrinth.com/plugin/schematicupload), [Polymart](https://polymart.org/resource/schematicupload.2125), & [Hangar](https://hangar.papermc.io/William278/SchematicUpload))
- [Issues](https://github.com/WiIIiam278/SchematicUpload/issues) &mdash; File a bug report or feature request
- [Discord](https://discord.gg/tVYhJfyDWG) &mdash; Get help, ask questions
- [bStats](https://bstats.org/plugin/bukkit/SchematicUpload/14611) &mdash; View plugin metrics

---
&copy; [William278](https://william278.net/), 2023. Licensed under the Apache-2.0 License.