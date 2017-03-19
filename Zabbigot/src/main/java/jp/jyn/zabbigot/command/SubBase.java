package jp.jyn.zabbigot.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class SubBase {
	private final static String dontHavePermission = "[Zabbigot] " + ChatColor.RED + "You don't have permission!!";

	private final String permission;

	public SubBase(String permission) {
		this.permission = permission;
	}

	public void onCommand(CommandSender sender) {
		if (!sender.hasPermission(permission)) { // 権限チェック
			sender.sendMessage(dontHavePermission);
			return;
		}

		exec(sender);
	}

	protected abstract void exec(CommandSender sender);
}
