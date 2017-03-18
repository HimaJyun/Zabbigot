package jp.jyn.zabbigot;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import io.github.hengyunabc.zabbix.sender.DataObject;
import io.github.hengyunabc.zabbix.sender.SenderResult;
import io.github.hengyunabc.zabbix.sender.ZabbixSender;

public class StatusSender implements Runnable {

	private final String hostname;
	private final TpsWatcher tps;

	private final String keyTps;
	private final String keyUser;
	private final String keyMemUsed;
	private final String keyMemFree;

	private final Runtime runtime = Runtime.getRuntime();
	private final ZabbixSender zabbixSender;

	public StatusSender(Zabbigot zabbigot) {
		ConfigStruct config = zabbigot.getConfigStruct();
		this.hostname = config.getZabbixHostname();
		this.tps = zabbigot.getTpsWatchar();

		keyTps = "minecraft.tps[" + config.getIdentifier() + "]";
		keyUser = "minecraft.user[" + config.getIdentifier() + "]";
		keyMemUsed = "minecraft.memory.used[" + config.getIdentifier() + "]";
		keyMemFree = "minecraft.memory.free[" + config.getIdentifier() + "]";

		zabbixSender = new ZabbixSender(config.getZabbixServer(), config.getZabbixPort());
	}

	@Override
	public void run() {
		List<DataObject> data = new ArrayList<>();
		// Zabbixが小数点以下4桁までなので揃える
		data.add(getDataObject(
				keyTps,
				(new BigDecimal(tps.getTPS()))
						.setScale(4, RoundingMode.DOWN)
						.toPlainString()));

		// オンラインユーザ数取得、サブスレッドからの実行でも例外は出ない
		data.add(getDataObject(keyUser, String.valueOf(Bukkit.getOnlinePlayers().size())));

		// メモリ
		long free = runtime.freeMemory();
		data.add(getDataObject(keyMemUsed, String.valueOf(runtime.totalMemory() - free)));
		data.add(getDataObject(keyMemFree, String.valueOf(free)));

		try {
			SenderResult result = zabbixSender.send(data);
			if (!result.success() || result.getFailed() != 0) {
				Bukkit.getLogger().warning(result.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private DataObject getDataObject(String key, String value) {
		return DataObject.builder()
				.host(hostname)
				.key(key)
				.value(value)
				.build();
	}

}
