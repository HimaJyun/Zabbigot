package jp.jyn.zabbigot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.bukkit.plugin.java.JavaPlugin;

import jp.jyn.zabbigot.command.Executor;

public class Zabbigot extends JavaPlugin {

	private ConfigStruct config;

	private TpsWatcher watcher;
	private StatusSender statusSender;
	private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> future = null;

	@Override
	public void onEnable() {
		if (config != null) { // configがnullでなければリロード呼び出しなので無効化する
			onDisable();
		}

		// nullなら読み込み、違えばリロード
		config = (config == null ? new ConfigStruct(this) : config.reloadConfig());

		// TPSの記録を開始
		watcher = new TpsWatcher(this);

		statusSender = new StatusSender(this);
		if (config.getInterval() <= 0) { // 0以下なら実行しない
			// 送信開始
			future = service.scheduleAtFixedRate(statusSender,
					(TpsWatcher.MAX_SAMPLING_SIZE / 20) + 10, // 初回実行を少し遅らせる
					config.getInterval(),
					TimeUnit.SECONDS);
		}

		// コマンド
		getCommand("zabbigot").setExecutor(new Executor(this));
	}

	@Override
	public void onDisable() {
		// コマンド
		getCommand("zabbigot").setExecutor(this);

		// 送信を止める
		if (future != null) {
			future.cancel(false);
		}

		// TPS記録を止める
		watcher.cancel();
	}

	public ConfigStruct getConfigStruct() {
		return config;
	}

	public TpsWatcher getTpsWatcher() {
		return watcher;
	}

	public StatusSender getStatusSender() {
		return statusSender;
	}
}
