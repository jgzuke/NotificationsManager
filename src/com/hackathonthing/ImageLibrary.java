/**
 * Loads, stores and resizes all graphics
 */
package com.hackathonthing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
public final class ImageLibrary
{
	private String getting;
	protected Resources res;
	protected String packageName;
	protected BitmapFactory.Options opts;
	protected BitmapDrawable [] notifOpts;
	protected BitmapDrawable edit;
	protected BitmapDrawable plus;
	protected ImageView plusImage;
	protected BitmapDrawable delete;
	/**
	 * loads in images and optimizes settings for loading
	 * @param contextSet start activity for getting resources etc
	 * @param controlSet control object
	 */
	public ImageLibrary(Context contextSet)
	{
		opts = new BitmapFactory.Options();
		opts.inDither = false;
		opts.inTempStorage = new byte[16 * 1024];
		packageName = contextSet.getPackageName();
		res = contextSet.getResources();
		edit = loadImage("edit", 120, 120);
		plus = loadImage("plus", 60, 60);
		plusImage = new ImageView(contextSet);
		plusImage.setImageDrawable(plus);
		delete = loadImage("delete", 80, 80);
		notifOpts = loadArray(3, "notif", 80, 80);
	}
	/**
	 * Loads and resizes array of images
	 * @param length Length of array to load
	 * @param start Starting string which precedes array index to match resource name
	 * @param width End width of image being loaded
	 * @param height End height of image being loaded
	 */
	protected BitmapDrawable[] loadArray(int length, String start, int width, int height)
	{
		BitmapDrawable[] newArray = new BitmapDrawable[length];
		for(int i = 0; i < length; i++)
		{
			getting = start + correctDigits(i + 1, 4);
			newArray[i] = loadImage(getting, width, height);
		}
		return newArray;
	}
	/**
	 * Adds 0's before string to make it four digits long
	 * Animations done in flash which when exporting .png sequence end file name with four character number
	 * @return Returns four character version of number
	 */
	protected String correctDigits(int start, int digits)
	{
		String end = Integer.toString(start);
		while(end.length() < digits)
		{
			end = "0" + end;
		}
		return end;
	}
	/**
	 * Loads image of name given from resources and scales to specified width and height
	 * @return Returns bitmap loaded and resized
	 */
	protected BitmapDrawable loadImage(String imageName, int width, int height)
	{
		int imageNumber = res.getIdentifier(imageName, "drawable", packageName);
		return new BitmapDrawable(res, Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, imageNumber, opts), width, height, false));
	}
}
