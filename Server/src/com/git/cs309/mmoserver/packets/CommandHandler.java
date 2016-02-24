package com.git.cs309.mmoserver.packets;

import com.git.cs309.mmoserver.Main;
import com.git.cs309.mmoserver.characters.user.ModerationHandler;
import com.git.cs309.mmoserver.characters.user.ModerationHandler.ModerationType;
import com.git.cs309.mmoserver.characters.user.Rights;
import com.git.cs309.mmoserver.characters.user.User;
import com.git.cs309.mmoserver.characters.user.UserManager;
import com.git.cs309.mmoserver.connection.Connection;

public final class CommandHandler {
	public static final void handleCommandPacket(final Packet packet) {
		switch (packet.getPacketType()) {
		case ADMIN_COMMAND_PACKET:
			AdminCommandPacket adminPacket = (AdminCommandPacket) packet;
			if (((Connection) adminPacket.getConnection()).isLoggedIn()
					&& ((Connection) adminPacket.getConnection()).getUser().getRights() != Rights.ADMIN) {
				adminPacket.getConnection().addOutgoingPacket(new ErrorPacket(null, ErrorPacket.PERMISSION_ERROR,
						"You do not have the correct permissions to do that."));
				return;
			}
			final int userID = adminPacket.getAdditionalField(); // This is the userID of the user to ban.
			final int duration = adminPacket.getDuration(); // In days
			User user = null;
			switch (adminPacket.getCommand()) {
			case AdminCommandPacket.RESTART_SERVER:
				Main.requestExit();
				break;
			case AdminCommandPacket.RESTART_CHARACTER_MANAGER:
				Main.loadAndStartCharacterManager();
				break;
			case AdminCommandPacket.RESTART_CONNECTION_MANAGER:
				Main.loadAndStartConnectionManager();
				break;
			case AdminCommandPacket.RESTART_CYCLE_PROCESS_MANAGER:
				Main.loadAndStartCycleProcessManager();
				break;
			case AdminCommandPacket.RESTART_NPC_MANAGER:
				Main.loadAndStartNPCManager();
				break;
			case AdminCommandPacket.BAN_USER:
				user = UserManager.getUserForUserID(userID);
				if (user == null) {
					break;
				}
				ModerationHandler.addModeration(user.getUsername(), duration, ModerationType.BAN);
				break;
			case AdminCommandPacket.IP_BAN_USER:
				user = UserManager.getUserForUserID(userID);
				if (user == null) {
					break;
				}
				ModerationHandler.addModeration(user.getConnection().getIP(), duration, ModerationType.BAN);
				break;
			case AdminCommandPacket.MUTE_USER:
				user = UserManager.getUserForUserID(userID);
				if (user == null) {
					break;
				}
				ModerationHandler.addModeration(user.getUsername(), duration, ModerationType.MUTE);
				break;
			case AdminCommandPacket.IP_MUTE_USER:
				user = UserManager.getUserForUserID(userID);
				if (user == null) {
					break;
				}
				ModerationHandler.addModeration(user.getConnection().getIP(), duration, ModerationType.MUTE);
				break;
			case AdminCommandPacket.PROMOTE_USER_ADMIN:
				user = UserManager.getUserForUserID(userID);
				if (user == null) {
					break;
				}
				UserManager.setRights(user.getUsername(), Rights.ADMIN);
				break;
			case AdminCommandPacket.PROMOTE_USER_MOD:
				user = UserManager.getUserForUserID(userID);
				if (user == null) {
					break;
				}
				UserManager.setRights(user.getUsername(), Rights.MOD);
				break;
			case AdminCommandPacket.PROMOTE_USER_PLAYER:
				user = UserManager.getUserForUserID(userID);
				if (user == null) {
					break;
				}
				UserManager.setRights(user.getUsername(), Rights.PLAYER);
				break;
			default:
				System.err.println("No case for admin command " + adminPacket.getCommand()
						+ " in CommandHandler.handleCommandPacket");
			}
			break;
		default:
			System.err.println(
					"No case for packet type \"" + packet.getPacketType() + "\" in CommandHandler.handleCommandPacket");
			break;
		}
	}
}
