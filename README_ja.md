# Zabbigot
ZabbixでBukkitを監視するためのプラグイン

## インストール
1. [プラグインをダウンロードする](https://github.com/HimaJyun/Zabbigot/releases/latest "Get Zabbigot")
2. 「plugins」ディレクトリに放り込む
3. サーバを開始する
4. 設定を変更する
5. 設定をリロードする
6. Zabbixサーバに監視項目を追加してください。

## Zabbix keys
タイプはすべて「Zabbixトラッパー」です。

|キー                 |説明              |データ型       |
|:--------------------|:-----------------|---------------|
|minecraft.user       |オンラインユーザ数|数値 (整数)    |
|minecraft.tps        |サーバのTPS       |数値 (浮動小数)|
|minecraft.memory.free|空きメモリ        |数値 (整数)    |
|minecraft.memory.used|使用済みメモリ    |数値 (整数)    |
実際には「minecraft.tps[Minecraft]」のように識別子を設定します。  
識別子はconfig.ymlから設定できます。(デフォルト: Minecraft)

実際の設定は「zabbigot_template.xml」を参考にして下さい。

## コマンド/パーミッション
|コマンド        |パーミッション |説明                  |デフォルト|
|:---------------|:--------------|:---------------------|:---------|
|/zabbigot       |zabbigot.show  |現在の状態を表示します|OP        |
|/zabbigot send  |zabbigot.send  |情報を送信します      |OP        |
|/zabbigot reload|zabbigot.reload|設定をリロードします  |OP        |

## config.yml
日本語版config.yml
```
# 送信間隔 (単位：秒)
# 0に設定すると送信を停止します。
# メンテナンスなどで送信を停止したい場合は0に設定してください。
Interval: 60

# 複数サーバを監視する場合に使用します。
# ここで設定した値を引数として送信します。
# 例:
# Identifier: "Minecraft" -> "minecraft.tps[Minecraft]"
# Identifier: "Zabbigot" -> "minecraft.tps[Zabbigot]"
Identifier: "Minecraft"

Zabbix:
  Server: "localhost"
  Port: 10051
  Hostname: "MinecraftServer"
```