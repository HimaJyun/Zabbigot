package jp.jyn.zabbigot.command;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import jp.jyn.zabbigot.Zabbigot;
import jp.jyn.zabbigot.command.sub.Reload;
import jp.jyn.zabbigot.command.sub.Send;
import jp.jyn.zabbigot.command.sub.Show;

public class Executor implements CommandExecutor {

	private final Map<String, SubBase> commands = new /*Linked*/HashMap<>();
	private final static String NO_ARGS = "show";

	public Executor(Zabbigot zabbigot) {
		commands.put("send", new Send(zabbigot));
		commands.put("reload", new Reload(zabbigot));

		commands.put(NO_ARGS, new Show(zabbigot));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (0 != args.length) { // 引数あり
			SubBase sub = commands.get( // サブコマンド取得
					args[0].toLowerCase(Locale.ENGLISH) // 小文字変換
			);

			if (sub != null) { // 非null(見つかった)なら
				sub.onCommand(sender); // 実行
				return true;
			}
		}

		// ここまで来た(見つからなかった||引数なかった)
		commands.get(NO_ARGS).onCommand(sender); // 引数なしとして扱う
		return true;
	}

}
