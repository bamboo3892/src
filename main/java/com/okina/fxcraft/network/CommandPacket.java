package com.okina.fxcraft.network;

import com.okina.fxcraft.main.FXCraft;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class CommandPacket implements IMessage {

	public String command;
	public String value;

	public CommandPacket() {}

	public CommandPacket(String command, String value) {
		if(command == null || value == null) throw new IllegalArgumentException();
		this.command = command;
		this.value = value;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		try{
			ByteBufUtils.writeUTF8String(buf, command);
			ByteBufUtils.writeUTF8String(buf, value);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		try{
			command = ByteBufUtils.readUTF8String(buf);
			value = ByteBufUtils.readUTF8String(buf);
		}catch (Exception e){
			System.err.println("Illegal packet received : " + this);
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Command Packet : /mbm " + command + " " + value;
	}

	/**client only*/
	public static class CommandPacketHandler implements IMessageHandler<CommandPacket, IMessage> {
		@Override
		public IMessage onMessage(CommandPacket msg, MessageContext ctx) {
			try{
				if("message".equals(msg.command)){
					FXCraft.proxy.appendPopUp(msg.value);
				}
			}catch (Exception e){
			}
			return null;
		}
	}

}
