package jp.jyn.zabbigot;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigStruct {
	/**
	 * 設定
	 */
	private FileConfiguration conf = null;
	/**
	 * 使用されるプラグイン
	 */
	private final Plugin plg;

	private String identifier;

	private int period;

	private boolean enable;

	private String zabbixServer;
	private int zabbixPort;
	private String zabbixHostname;

	/**
	 * 各種設定構造体を初期化します。
	 * @param plugin 対象のプラグイン
	 */
	public ConfigStruct(Plugin plugin) {
		// プラグイン
		plg = plugin;
		// 読み込み
		reloadConfig();
	}

	/**
	 * 設定をリロードします。
	 * @return 自分自身のインスタンス
	 */
	public ConfigStruct reloadConfig() {
		// デフォルトを保存
		plg.saveDefaultConfig();
		if (conf != null) { // confが非null
			plg.reloadConfig();
		}
		// 設定を取得
		conf = plg.getConfig();

		enable = conf.getBoolean("Enable", true);

		identifier = conf.getString("Identifier");
		period = conf.getInt("Period");
		zabbixServer = conf.getString("Zabbix.Server");
		zabbixPort = conf.getInt("Zabbix.Port", 10051);
		zabbixHostname = conf.getString("Zabbix.Hostname");

		return this;
	}

	public String getIdentifier() {
		return identifier;
	}

	public int getPeriod() {
		return period;
	}

	public String getZabbixServer() {
		return zabbixServer;
	}

	public int getZabbixPort() {
		return zabbixPort;
	}

	public String getZabbixHostname() {
		return zabbixHostname;
	}

	public boolean isEnable() {
		return enable;
	}

}
