# Zabbigot
Bukkit plugin for Zabbix monitoring.([このページの日本語版](https://github.com/HimaJyun/Zabbigot/blob/master/README_ja.md))  
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
|minecraft.memory.free|Free memory |Numeric (unsigned)|
|minecraft.memory.used|Used memory |Numeric (unsigned)|

Actually specify an identifier like "minecraft.tps[Minecraft]".  
Identifiers can be changed from config.yml. (Default: Minecraft)

Please refer to "zabbigot_template.xml" for actual setting.

## Command/Permission
|Command         |Permission     |Description        |Default|
|:---------------|:--------------|:------------------|:------|
|/zabbigot       |zabbigot.show  |Show system status.|OP     |
|/zabbigot send  |zabbigot.send  |Send status.       |OP     |
|/zabbigot reload|zabbigot.reload|Reload the config. |OP     |
