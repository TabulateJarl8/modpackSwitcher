# Modpack Switcher

----

Modpack Switcher is a utility to allow you to easily switch between modpacks while only using one server host, like PloxHost or Shockbyte to name a few. This works as a console interface to let you choose which modpack to start when you boot your server. Usage instructions are below.

## Requirements
 - File access on the server, something like FTP would be good.
 - The ability to change the file that the server executes on start (this can be done with PloxHost but I haven't been able to try anything else).

## Usage
Modpack Switcher is fairly easy to use. First, download the latest artifact from [below](#artifactDownload), extract it, and place the jar in the main directory of your server. This directory will be the same one where the files for a modpack would go. Now create a directory called `packs` in the same folder as the jar. This directory will hold all of the directories for your modpacks, and you can put them there now. Here is an example structure.

```
packs
├── funny
|   ├── forge-1.12.2-server.jar
|   ├── server.properties
|   └── ....
├── sevtech
|   ├── StartServer.sh
|   ├── Install.sh
|   ├── server.jar
|   ├── server.properties
|   └── ....
└── switcher.jar
```

Now, inside of each of those modpack folders (in our case `funny` and `sevtech`), you are going to want to create a file called `modpackswitcher.txt`. Inside of this file you are going to want to put the command used to launch the server, which can usually be found in the README of the modpack. In this case, we'll say that to start funny, you need to just use `java -jar forge-1.12.2-server.jar`, so that's exactly what goes in the `modpackswitcher.txt` file. For Sevtech: Ages, you need to first run the install script and then the start script. There are 2 ways to accomplish this.
1. You can run the install script locally and transfer those files to the server
2. You can set the command in `modpackswitcher.txt` to `sh Install.sh`, start the server, and then after it's done installing, stop the server and change the command to `sh StartServer.sh`.

Now, all you need to do is set the file that the server executes on start to `switcher.jar`, or whatever the name of the first file you downloaded is. This can be done on PloxHost via the control panel, but I cannot test other hosts.

## Download

<a name="artifactDownload"></a>
[![Build artifacts](https://github.com/TabulateJarl8/modpackSwitcher/actions/workflows/main.yml/badge.svg)](https://github.com/TabulateJarl8/modpackSwitcher/actions/workflows/main.yml)

Latest release artifact: [https://nightly.link/TabulateJarl8/modpackSwitcher/workflows/main/master/switcher.jar.zip](https://nightly.link/TabulateJarl8/modpackSwitcher/workflows/main/master/switcher.jar.zip)
