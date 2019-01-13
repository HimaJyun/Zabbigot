# Zabbigot
Bukkit plugin for Zabbix monitoring.  
[![Build Status](https://travis-ci.org/HimaJyun/Zabbigot.svg?branch=master)](https://travis-ci.org/HimaJyun/Zabbigot)

## Installation
1. [Download this plugin.](https://github.com/HimaJyun/Zabbigot/releases/latest "Get Zabbigot")
2. drop in plugins directory.
3. Start Server.
4. config edit.
5. reload.
6. Please add monitoring items to the Zabbix server.

## Zabbix keys
type is "Zabbix trapper"

|Key                  |Description |Data type         |
|:--------------------|:-----------|:-----------------|
|minecraft.user       |Online users|Numeric (unsigned)|
|minecraft.tps        |Server TPS  |Numeric (float)   |
|minecraft.ping       |Ping count  |Numeric (unsigned)|
|minecraft.memory.free|Free memory |Numeric (unsigned)|
|minecraft.memory.used|Used memory |Numeric (unsigned)|
|minecraft.chunk.load    |Chunk load count       |Numeric (unsigned)|
|minecraft.chunk.unload  |Chunk unload count     |Numeric (unsigned)|
|minecraft.chunk.loaded  |Loaded chunk count     |Numeric (unsigned)|
|minecraft.chunk.generate|Generated chunk count  |Numeric (unsigned)|
|minecraft.chunk.ratio   |Chunk load/unload ratio|Numeric (float)   |

Actually specify an identifier like "minecraft.tps[Minecraft]".  
Identifiers can be changed from config.yml. (Default: Minecraft)

Please refer to "zabbigot_template.xml" for actual setting.

### Tips: Other than zabbix
You can output the status to a file by changing the setting.  
Please send the value written in the file to your system.

## Command/Permission
|Command         |Permission     |Description        |Default|
|:---------------|:--------------|:------------------|:------|
|/zabbigot show  |zabbigot.show  |Show system status.|OP     |
|/zabbigot send  |zabbigot.send  |Send status.       |OP     |
|/zabbigot reload|zabbigot.reload|Reload the config. |OP     |

## Command output
"/zabbigot show" will output the following message.

```
======== Zabbigot (Player: {online player}/{max player}) ========
TPS: [####################] {tps} ({tps}%)
MEM: [###################_] {free}MB/{total}MB ({free}%)
CUR: [###############_____] {loaded chunks} ({unload ratio}%)
```

TPS may exceed 20, but this is normal behavior to get back the delay.

CUR(Chunk Unload Ratio) represents the chunk loading/unloading ratio.  
{loaded chunks} is the number of chunks currently loaded.

