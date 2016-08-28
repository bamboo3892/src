package com.okina.fxcraft.utils;

import java.util.HashMap;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import com.google.common.collect.Maps;
import com.okina.fxcraft.account.AccountInfo;
import com.okina.fxcraft.account.FXPosition;
import com.okina.fxcraft.account.GetPositionOrder;
import com.okina.fxcraft.account.SettlePositionOrder;
import com.okina.fxcraft.rate.FXRateGetHelper;
import com.okina.fxcraft.rate.RateData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class UtilMethods {

	/**pTrue ??? */
	public static MovingObjectPosition getMovingObjectPositionFromPlayer(World world, EntityPlayer player, boolean pTrue) {
		float f = 1.0F;
		float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
		float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
		double d0 = player.prevPosX + (player.posX - player.prevPosX) * f;
		double d1 = player.prevPosY + (player.posY - player.prevPosY) * f + (world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
		double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
		Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = 5.0D;
		if(player instanceof EntityPlayerMP){
			d3 = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
		}
		Vec3 vec31 = vec3.addVector(f7 * d3, f6 * d3, f8 * d3);
		return world.func_147447_a(vec3, vec31, pTrue, !pTrue, false);
	}

	public static int[] getRandomArray(int min, int max) {
		int[] re = new int[max - min + 1];
		for (int i = 0; i < re.length; i++){
			re[i] = i + min;
		}
		re = getRandomArray(re);
		return re;
	}

	public static int[] getRandomArray(int[] array) {
		if(array == null || array.length == 0) return null;
		int[] newArray = array.clone();
		boolean[] flags = new boolean[array.length];
		int index1;
		int index2;
		for (int i = 0; i < array.length; i++){
			index1 = (int) (Math.random() * (array.length - i));
			index2 = 0;
			for (int j = 0; j <= index1; j++){
				while (flags[index2])
					index2++;
				if(j < index1) index2++;
			}
			newArray[i] = array[index2];
			flags[index2] = true;
		}
		return newArray;
	}

	public static String zeroFill(int number, int length) {
		String str = String.valueOf(number);
		if(str.length() >= length){
			return str;
		}else{
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < length - str.length(); i++){
				builder.append("0");
			}
			builder.append(str);
			return builder.toString();
		}
	}

	public static void renderHUDCenter(Minecraft mc, List<ColoredString> list) {
		if(list != null && !list.isEmpty()){
			ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			Point center = new Point(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2);
			int size = list.size();
			for (int i = 0; i < list.size(); i++){
				ColoredString str = list.get(i);
				if(str != null && !str.isEmpty()){
					int length = mc.fontRenderer.getStringWidth(str.str);
					GL11.glPushMatrix();
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glTranslatef(center.getX(), center.getY(), 0);
					GL11.glTranslatef(-length / 2, 20 + i * 10, 0);
					mc.fontRenderer.drawString(str.str, 0, 0, str.color, true);
					GL11.glPopMatrix();
				}
			}
		}
	}

	public static void renderHUDRight(Minecraft mc, List<ColoredString> list) {
		if(list != null && !list.isEmpty()){
			ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			Point right = new Point(sr.getScaledWidth(), sr.getScaledHeight() / 2);
			int size = list.size();
			for (int i = 0; i < list.size(); i++){
				ColoredString str = list.get(i);
				if(str != null && !str.isEmpty()){
					int length = mc.fontRenderer.getStringWidth(str.str);
					GL11.glPushMatrix();
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glTranslatef(right.getX(), right.getY(), 0);
					GL11.glTranslatef(-length - 5, -size * 10 / 2 + i * 10, 0);
					mc.fontRenderer.drawString(str.str, 0, 0, str.color, true);
					GL11.glPopMatrix();
				}
			}
		}
	}

	public static HashMap<String, Object> getTableFromRate(RateData rate) {
		HashMap<String, Object> map = Maps.newHashMap();
		map.put("rate", rate.open);
		map.put("date", FXRateGetHelper.getCalendarString(rate.calendar, -1));
		return map;
	}

	public static HashMap<String, Object> getTableFromPosition(FXPosition position) {
		HashMap<String, Object> map = Maps.newHashMap();
		map.put("date", FXRateGetHelper.getCalendarString(position.contractDate, -1));
		map.put("pair", position.currencyPair);
		map.put("lot", position.lot);
		map.put("deposit", position.depositLot);
		map.put("askOrBid", position.askOrBid);
		map.put("rate", position.contractRate);
		map.put("positionID", position.positionID);
		return map;
	}

	public static HashMap<String, Object> getTableFromGetOrder(GetPositionOrder order) {
		HashMap<String, Object> map = Maps.newHashMap();
		map.put("date", FXRateGetHelper.getCalendarString(order.contractDate, -1));
		map.put("pair", order.currencyPair);
		map.put("lot", order.lot);
		map.put("deposit", order.depositLot);
		map.put("askOrBid", order.askOrBid);
		map.put("limits", order.limits);
		map.put("orderID", order.orderID);
		return map;
	}

	public static HashMap<String, Object> getTableFromSettleOrder(SettlePositionOrder order) {
		HashMap<String, Object> map = Maps.newHashMap();
		map.put("position", getTableFromPosition(order.position));
		map.put("date", FXRateGetHelper.getCalendarString(order.contractDate, -1));
		map.put("limits", order.limits);
		map.put("orderID", order.position.positionID);
		return map;
	}

	public static HashMap<String, Object> getTableFromAccount(AccountInfo account) {
		HashMap<String, Object> map = Maps.newHashMap();
		map.put("name", account.name);
		map.put("balance", account.balance);
		return map;
	}

}
