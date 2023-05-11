# Modpack Switcher
[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/L4L3L7IO2)

----

Modpack Switcher is a utility to allow you to easily switch between modpacks while only using one server host, like PloxHost or Shockbyte to name a few. This works as a console interface to let you choose which modpack to start when you boot your server. Usage instructions are below.

## Requirements
 - File access on the server, something like FTP would be good.
 - The ability to change the file that the server executes on start (this can be done with PloxHost but I haven't been able to try anything else).

## Usage
Modpack Switcher is fairly easy to use. First, download the latest artifact from [below](#artifactDownload), extract it, and place the jar in the main directory of your server. This directory will be the same one where the files for a modpack would go. Now create a directory called `packs` in the same folder as the jar. This directory will hold all of the directories for your modpacks, and you can put them there now. Here is an example structure.

```
.
├── packs
│   ├── funny
│   │   ├── modpackswitcher.txt
│   │   ├── forge-1.12.2-server.jar
│   │   ├── server.properties
│   │   └── ...
│   └── rlcraft
│       ├── Install.sh
│       ├── modpackswitcher.txt
│       ├── server.jar
│       ├── StartServer.sh
│       └── ...
└── switcher.jar
```

Now, inside of each of those modpack folders (in our case `funny` and `sevtech`), you are going to want to create a file called `modpackswitcher.txt`. Inside of this file you are going to want to put the command used to launch the server, which can usually be found in the README of the modpack. In this case, we'll say that to start funny, you need to just use `java -jar forge-1.12.2-server.jar`, so that's exactly what goes in the `modpackswitcher.txt` file. For Sevtech: Ages, you need to first run the install script and then the start script. There are 2 ways to accomplish this.
1. You can run the install script locally and transfer those files to the server
2. You can set the command in `modpackswitcher.txt` to `sh Install.sh`, start the server, and then after it's done installing, stop the server and change the command to `sh StartServer.sh`.

Now, all you need to do is set the file that the server executes on start to `switcher.jar`, or whatever the name of the first file you downloaded is. This can be done via the control panel on some hosts.

## Configuration
When you first run the switcher jar file, if there isn't a file called `mpswconfig.ini` in the current directory, the file will be created with the default values set. There is one option for the last used modpack, you probably won't need to change that as it's changed automatically, and there's an option for the absolute path of the working directory. This is useful if the switcher is incorrectly detecting the directory that you're in. To change this, just set it to the absolute path of the directory containing the `packs` directory.

## EULA
Modpack Switcher will automatically accept the EULA in two cases. The first case is if `accepteula = true` is present in `mpswconfig.ini`, then the EULA will be accepted automatically when you start a modpack. The other case is if the EULA is already accepted in the `eula.txt` file in the directory of `switcher.jar`. Server hosts will sometimes include an accepted EULA in your working directory on the server (where you put `switcher.jar`), and since we're not running a traditional modpack, Modpack Switcher will transfer this accepted EULA into each of it's modpacks.

## Download

<a name="artifactDownload"></a>
[![Build artifacts](https://github.com/TabulateJarl8/modpackSwitcher/actions/workflows/main.yml/badge.svg)](https://github.com/TabulateJarl8/modpackSwitcher/actions/workflows/main.yml)

Latest release artifact: [https://nightly.link/TabulateJarl8/modpackSwitcher/workflows/main/master/switcher.jar.zip](https://nightly.link/TabulateJarl8/modpackSwitcher/workflows/main/master/switcher.jar.zip)
