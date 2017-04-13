/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.moon.live.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
	/**
	 * name of preference
	 */
	public static final String PREFERENCE_NAME = "saveInfo";
	private static SharedPreferences mSharedPreferences;
	private static PreferenceManager mPreferencemManager;
	private static SharedPreferences.Editor editor;

	private static String SHARED_KEY_CURRENTUSER_USERNAME = "SHARED_KEY_CURRENTUSER_USERNAME";
	private static String SHARED_KEY_CURRENTUSER_NICK = "SHARED_KEY_CURRENTUSER_NICK";
	private static String SHARED_KEY_CURRENTUSER_AVATAR = "SHARED_KEY_CURRENTUSER_AVATAR";

	@SuppressLint("CommitPrefEdits")
	private PreferenceManager(Context cxt) {
		mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		editor = mSharedPreferences.edit();
	}

	public static synchronized void init(Context cxt){
	    if(mPreferencemManager == null){
	        mPreferencemManager = new PreferenceManager(cxt);
	    }
	}

	/**
	 * get instance of PreferenceManager
	 *
	 * @param
	 * @return
	 */
	public synchronized static PreferenceManager getInstance() {
		if (mPreferencemManager == null) {
			throw new RuntimeException("please init first!");
		}

		return mPreferencemManager;
	}

	public void setCurrentUserNick(String nick) {
		editor.putString(SHARED_KEY_CURRENTUSER_NICK, nick);
		editor.apply();
	}

	public void setCurrentUserAvatar(String avatar) {
		editor.putString(SHARED_KEY_CURRENTUSER_AVATAR, avatar);
		editor.apply();
	}

	public String getCurrentUserNick() {
		return mSharedPreferences.getString(SHARED_KEY_CURRENTUSER_NICK, null);
	}

	public String getCurrentUserAvatar() {
		return mSharedPreferences.getString(SHARED_KEY_CURRENTUSER_AVATAR, null);
	}

	public void setCurrentUserName(String username){
		editor.putString(SHARED_KEY_CURRENTUSER_USERNAME, username);
		editor.apply();
	}

	public String getCurrentUsername(){
		return mSharedPreferences.getString(SHARED_KEY_CURRENTUSER_USERNAME, null);
	}

	public void removeCurrentUserInfo() {
		editor.remove(SHARED_KEY_CURRENTUSER_NICK);
		editor.remove(SHARED_KEY_CURRENTUSER_AVATAR);
		editor.apply();
	}

}
