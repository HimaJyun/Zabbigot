package jp.jyn.zabbigot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.bukkit.plugin.java.JavaPlugin;

public class Zabbigot extends JavaPlugin {

	private ConfigStruct config;

	private TpsWatcher watcher;
	private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> sender = null;

	@Override
	public void onEnable() {
		if (config != null) { // configがnullでなければリロード呼び出しなので無効化する
			onDisable();
		}

		// nullなら読み込み、違えばリロード
		config = (config == null ? new ConfigStruct(this) : config.reloadConfig());

		// TPSの記録を開始
		watcher = new TpsWatcher(this);

		// コマンド
		getCommand("zabbigot").setExecutor(new Command(this));

		if (config.getPeriod() <= 0) { // 0以下なら実行しない
			// 送信開始
			sender = service.scheduleAtFixedRate(new StatusSender(this),
					(TpsWatcher.MAX_SAMPLING_SIZE / 20) + 10, // 初回実行を少し遅らせる
					config.getPeriod(),
					TimeUnit.SECONDS);
		}
	}

	@Override
	public void onDisable() {
		// 送信を止める
		if (sender != null) {
			sender.cancel(false);
		}

		// コマンド
		getCommand("zabbigot").setExecutor(this);

		// TPS記録を止める
		watcher.cancel();
	}

	public ConfigStruct getConfigStruct() {
		return config;
	}

	public TpsWatcher getTpsWatcher() {
		return watcher;
	}
}
