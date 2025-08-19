// CHECKSTYLE:OFF
/*
 * Copyright (C) 2009-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader.newreader;

import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static com.baidu.searchbox.bookreader.BBAModeTranslate.Color.BBA_MENU_ITEM_TEXT_COLOR;
import static com.baidu.searchbox.bookreader.BBAModeTranslate.Drawable.BBA_MENU_DOWNLOAD;
import static com.baidu.searchbox.bookreader.BBAModeTranslate.Drawable.BBA_MENU_DOWNLOAD_ALL;
import static com.baidu.searchbox.bookreader.BBAModeTranslate.Drawable.BBA_NO_WIFI;
import static com.baidu.searchbox.bookreader.abtest.BBAABTestConstants.NEWREADER_FIRST_CHAPTER_ADFREQ_SWITCh;
import static com.baidu.searchbox.bookreader.event.BBAEventData.DRAW_BITMAP_TO_CANVAS;
import static com.baidu.searchbox.bookreader.event.BBAEventData.FONT_SIZE_CHANGE;
import static com.baidu.searchbox.bookreader.event.BBAEventData.JUMP_CHAPTER_AFTER;
import static com.baidu.searchbox.bookreader.event.BBAEventData.N_FILE_COMPLETED;
import static com.baidu.searchbox.bookreader.event.BBAEventData.RENDER_PAGE_COMPLETED;
import static com.baidu.searchbox.bookreader.event.BBAEventData.REQUEST_CHAPTER_CONTENT;
import static com.baidu.searchbox.bookreader.event.BBAEventData.REWARD_PLAY_END;
import static com.baidu.searchbox.bookreader.event.BBAEventData.SCROLLING_PAGE_CHANGE;
import static com.baidu.searchbox.bookreader.event.BBAEventData.SCROLL_PAGE;
import static com.baidu.searchbox.bookreader.event.BBAEventData.SO_LOAD_FAIL;
import static com.baidu.searchbox.bookreader.event.BBALayoutFields.BOOK_TYPE_IS_LEGAL;
import static com.baidu.searchbox.bookreader.event.BBALayoutFields.CURRENT_SCREEN_INDEX;
import static com.baidu.searchbox.bookreader.event.BBALayoutFields.FILE_ID;
import static com.baidu.searchbox.bookreader.event.BBALayoutFields.FILE_INDEX;
import static com.baidu.searchbox.bookreader.event.BBALayoutFields.IS_CURRENT_LOADING_PAGE;
import static com.baidu.searchbox.bookreader.event.BBALayoutFields.IS_FIRST_TURN_TO_PAGE;
import static com.baidu.searchbox.bookreader.event.BBALayoutFields.IS_GESTURE_TURN_PAGE;
import static com.baidu.searchbox.bookreader.event.BBALayoutFields.IS_SCROLL_PAGE_TO_NEXT_DIRECTION;
import static com.baidu.searchbox.bookreader.event.BBALayoutFields.IS_TITLE_PAGE;
import static com.baidu.searchbox.bookreader.event.BBALayoutFields.LAST_PAGE_ITEM_ID;
import static com.baidu.searchbox.bookreader.event.BBALayoutFields.OLD_PAGE_INDEX;
import static com.baidu.searchbox.bookreader.event.BBALayoutFields.OLD_PAGE_TYPE_UBC_STR;
import static com.baidu.searchbox.bookreader.event.BBALayoutFields.PAGE_COMPLETELY_VISIBLE_INDEX;
import static com.baidu.searchbox.bookreader.event.BBALayoutFields.PAGE_INDEX;
import static com.baidu.searchbox.bookreader.event.BBALayoutFields.PAGE_ITEM_ID;
import static com.baidu.searchbox.bookreader.interfaces.BBAReaderComponent.KEY_TITLE_PAGE_PARAGRAPH_OFFSET;
import static com.baidu.searchbox.bookreader.menu.base.config.BBAMenuConfig.READER_NIGHT_BACKGROUND_COLOR;
import static com.baidu.searchbox.bookreader.menu.manager.BBAMenuType.LITE_SETTING_MENU_PAGE_1;
import static com.baidu.searchbox.bookreader.menu.manager.BBAMenuType.LITE_SETTING_MENU_PAGE_2;
import static com.baidu.searchbox.bookreader.menu.widget.base.BBAReaderBarItemModel.AUTO_FLIP;
import static com.baidu.searchbox.bookreader.menu.widget.base.BBAReaderBarItemModel.BOOKMARK_PAGE;
import static com.baidu.searchbox.bookreader.menu.widget.base.BBAReaderBarItemModel.CATALOG_PAGE;
import static com.baidu.searchbox.bookreader.menu.widget.base.BBAReaderBarItemModel.CONTENT_ERROR_PAGE;
import static com.baidu.searchbox.bookreader.setting.BBADefaultConfig.HORIZONTAL_CURL;
import static com.baidu.searchbox.bookreader.setting.BBADefaultConfig.HORIZONTAL_TRANSLATE;
import static com.baidu.searchbox.bookreader.theme.BBAFontManager.FONT_DEFAULT;
import static com.baidu.searchbox.reader.ad.AbstractReaderViewManager.FROM_AD_BITMAP;
import static com.baidu.searchbox.reader.ad.AbstractReaderViewManager.HIDE_AD;
import static com.baidu.searchbox.reader.ad.AbstractReaderViewManager.SHOW_AD_VIEW;
import static com.baidu.searchbox.reader.common.ReaderSettingsHelper.SOURCE_BROWN_THEME;
import static com.baidu.searchbox.reader.common.ReaderSettingsHelper.SOURCE_DEFAULT_THEME;
import static com.baidu.searchbox.reader.common.ReaderSettingsHelper.SOURCE_GREEN_THEME;
import static com.baidu.searchbox.reader.common.ReaderSettingsHelper.SOURCE_GREY_THEME;
import static com.baidu.searchbox.reader.common.ReaderSettingsHelper.SOURCE_NIGHT_THEME;
import static com.baidu.searchbox.reader.common.ReaderSettingsHelper.SOURCE_ORANGE_THEME;
import static com.baidu.searchbox.reader.common.ReaderSettingsHelper.SOURCE_PINK_THEME;
import static com.baidu.searchbox.reader.utils.StatisticsContants.READER_SETTING_BACKGROUNDCOLOR;
import static com.baidu.searchbox.reader.utils.StatisticsContants.SOURCE_AUTO_ADD_TO_SHELF;
import static com.baidu.searchbox.reader.utils.StatisticsContants.SOURCE_READER_SIGN_CLOSE;
import static com.baidu.searchbox.reader.utils.StatisticsContants.SOURCE_READER_SIGN_OPEN;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_FROM_NATIVE_NOVEL;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_FROM_NOVEL;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_NOVEL_FORMAT;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_PAGE_CATALOG;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_PAGE_MORE_SETTING;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_PAGE_READER_SETTING;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_ADDSHELF;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_AUTO_BUY;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_AUTO_CHANGE_DAY_NIGHT;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_AUTO_FLIP;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_BACKGROUND_COLOR;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_BOOKMARK_ITEM;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_BOOKMARK_PAGE;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_BOOKMARK_TAB;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_BRIGHTNESS;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_CATALOG_ITEM;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_CATALOG_PAGE;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_CATALOG_TAB;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_DAY_NIGHT;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_FEEDBACK;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_FONT_SIZE;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_FULL_SCREEN_FLIP;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_GOTO_SHELF;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_LINE_SPACE;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_LOCK_SCREEN;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_ORDER;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_PRELOAD;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_QUICK_FLIP;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_REST_HINT;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_REVERSE_ORDER;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_SETTING_PANEL_SHOW;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_SOURCE_VOLUME_FLIP;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_TYPE_CLICK;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_TYPE_LITE_READER_BACKGROUND;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_TYPE_LITE_READER_TURN_PAGE;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_VALUE_SOURCE_FLIP_HTRANSITION;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_VALUE_SOURCE_FLIP_NONE;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_VALUE_SOURCE_FLIP_SIMULATE;
import static com.baidu.searchbox.reader.utils.StatisticsContants.UBC_VALUE_SOURCE_FLIP_VSCROLL;
import static com.baidu.searchbox.reader.view.NovelReaderCallbackDataType.GET_READER_FROM_ACTION_LEGAL_OR_TXT;
import static com.baidu.searchbox.reader.view.NovelReaderCallbackDataType.GET_READER_VALUE_ACTION_LEGAL_OR_TXT;
import static com.baidu.searchbox.reader.view.NovelReaderCallbackNotifyType.NOTIFY_EXECUTE_LEGAL_ADD_SHELF;
import static com.baidu.searchbox.reader.view.NovelReaderCallbackNotifyType.NOTIFY_ILLEGAL_SHOW_STOP_TTS_FOR_GO_BOOK_SHELF_DLG;
import static com.baidu.searchbox.reader.view.NovelReaderCallbackNotifyType.NOTIFY_LEGAL_READER_XIAJIA_CHAPTER_SHOW;
import static com.baidu.searchbox.reader.view.NovelReaderCallbackNotifyType.NOTIFY_ON_MENU_HIDE;
import static com.baidu.searchbox.reader.view.NovelReaderCallbackNotifyType.NOTIFY_ON_MENU_SHOW;
import static com.baidu.searchbox.reader.view.NovelReaderCallbackNotifyType.NOTIFY_ON_TURN_PAGE_TYPE;
import static com.baidu.searchbox.reader.view.NovelReaderCallbackNotifyType.NOTIFY_SET_GLOBAL_OPERATION_VIEW_DIALOG_IS_SHOW;
import static com.baidu.searchbox.reader.view.NovelReaderCallbackNotifyType.NOTIFY_SET_GLOBAL_OPERATION_VIEW_DIALOG_SHOW_PARAMETER;
import static com.baidu.searchbox.reader.view.NovelReaderCallbackNotifyType.SHOW_ADD_TO_BOOK_SHELF_SUCCESS_TOAST_OR_DLG;
import static com.baidu.searchbox.reader.view.ReaderUtility.getReaderManagerCallback;
import static org.geometerplus.android.fbreader.readerframe.BBATempResourceProviderImpl.DrawableType.BBA_MENU_DOWNLOAD_DISABLE;
import static org.geometerplus.fbreader.fbreader.FBReaderApp.MODE_CHANGE_DELAY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.searchbox.NoProGuard;
import com.baidu.searchbox.bookreader.BBAModeChangeHelper;
import com.baidu.searchbox.bookreader.BBAModeTranslate;
import com.baidu.searchbox.bookreader.BBAResourceHelper;
import com.baidu.searchbox.bookreader.abtest.BBAABTestConstants;
import com.baidu.searchbox.bookreader.api.BBAReaderCoreApi;
import com.baidu.searchbox.bookreader.book.entity.BBABook;
import com.baidu.searchbox.bookreader.book.entity.BBABookChapter;
import com.baidu.searchbox.bookreader.common.bitmap.BBAReaderBitmapManager;
import com.baidu.searchbox.bookreader.common.loading.BBASmoothProgressBar;
import com.baidu.searchbox.bookreader.entity.BBAPage;
import com.baidu.searchbox.bookreader.entity.BBATextWordCursor;
import com.baidu.searchbox.bookreader.event.BBADurationManager;
import com.baidu.searchbox.bookreader.event.BBAEventBus;
import com.baidu.searchbox.bookreader.event.BBAEventData;
import com.baidu.searchbox.bookreader.event.BBAEventHandler;
import com.baidu.searchbox.bookreader.font.IBBAReaderFontEventListener;
import com.baidu.searchbox.bookreader.gesture.BBAReaderGestureManager;
import com.baidu.searchbox.bookreader.interfaces.BBAReaderComponent;
import com.baidu.searchbox.bookreader.interfaces.IBBAAdGestureClick;
import com.baidu.searchbox.bookreader.interfaces.IBBAChapterTailWelfareProvider;
import com.baidu.searchbox.bookreader.interfaces.listener.BBAReaderContainerComponent;
import com.baidu.searchbox.bookreader.interfaces.listener.IBBAReaderContainerListener;
import com.baidu.searchbox.bookreader.manager.BBAPhoneStateManager;
import com.baidu.searchbox.bookreader.manager.BBAPhoneStateModel;
import com.baidu.searchbox.bookreader.menu.base.callback.IBBAAutoScrollCallback;
import com.baidu.searchbox.bookreader.menu.base.callback.IBBAChangePageCallback;
import com.baidu.searchbox.bookreader.menu.base.callback.IBBAMenuActionCallback;
import com.baidu.searchbox.bookreader.menu.base.callback.IBBAMoreSettingFunctionCallback;
import com.baidu.searchbox.bookreader.menu.base.callback.IBBAMoreSettingReaderCallback;
import com.baidu.searchbox.bookreader.menu.base.callback.IBBASettingMenuComponentCallback;
import com.baidu.searchbox.bookreader.menu.base.config.BBAMenuConfig;
import com.baidu.searchbox.bookreader.menu.children.autoscroll.BBAAutoScrollView;
import com.baidu.searchbox.bookreader.menu.children.base.IBBABaseMenu;
import com.baidu.searchbox.bookreader.menu.children.directory.BBADirectoryMenuView;
import com.baidu.searchbox.bookreader.menu.children.main.BBAMainMenuView;
import com.baidu.searchbox.bookreader.menu.children.moresetting.BBAMoreSettingMenuView;
import com.baidu.searchbox.bookreader.menu.children.setting.BBASettingMenuView;
import com.baidu.searchbox.bookreader.menu.manager.BBAMenuComponent;
import com.baidu.searchbox.bookreader.menu.manager.BBAMenuController;
import com.baidu.searchbox.bookreader.menu.manager.BBAMenuType;
import com.baidu.searchbox.bookreader.menu.manager.IBBAMenuControll;
import com.baidu.searchbox.bookreader.menu.widget.base.BBAReaderBarItemModel;
import com.baidu.searchbox.bookreader.menu.widget.itemmodel.BBANoreSettingEduBottomBarModel;
import com.baidu.searchbox.bookreader.reader.BBAReaderContainer;
import com.baidu.searchbox.bookreader.reader.adapter.BBAReaderContainerAdapter;
import com.baidu.searchbox.bookreader.reader.autopager.BBAAutoScrollHelper;
import com.baidu.searchbox.bookreader.reader.itemmodel.BBAReaderContainerItem;
import com.baidu.searchbox.bookreader.reader.listenner.BBARightTopViewListenerManager;
import com.baidu.searchbox.bookreader.runtime.BBAReaderRuntime;
import com.baidu.searchbox.bookreader.setting.BBADefaultConfig;
import com.baidu.searchbox.bookreader.setting.BBAMoreSettingKey;
import com.baidu.searchbox.bookreader.setting.BBASettingChangeListener;
import com.baidu.searchbox.bookreader.setting.BBASettingManager;
import com.baidu.searchbox.bookreader.setting.BBAThemeResourceHelper;
import com.baidu.searchbox.bookreader.storagesp.BBAReaderConfig;
import com.baidu.searchbox.bookreader.task.IBBALoadChapterTask;
import com.baidu.searchbox.bookreader.util.BBAClickUtils;
import com.baidu.searchbox.bookreader.util.BBADeviceUtil;
import com.baidu.searchbox.bookreader.util.BBALogUtil;
import com.baidu.searchbox.bookreader.util.BBANavigationBarUtils;
import com.baidu.searchbox.bookreader.util.BBANetworkChangeUtils;
import com.baidu.searchbox.bookreader.util.BBAProgressUtil;
import com.baidu.searchbox.bookreader.util.BBAStatusBarUtil;
import com.baidu.searchbox.bookreader.util.BBAStringUtils;
import com.baidu.searchbox.bookreader.util.BBAThreadUtils;
import com.baidu.searchbox.bookreader.util.BBAUIUtil;
import com.baidu.searchbox.bookreader.util.BBAUIUtils;
import com.baidu.searchbox.bookreader.utils.BBAFontController;
import com.baidu.searchbox.bookreader.utils.BBAJumpChapterUtil;
import com.baidu.searchbox.businessadapter.NovelBusTTSAdapter;
import com.baidu.searchbox.businessadapter.NovelBusiActTaskAdapter;
import com.baidu.searchbox.businessadapter.NovelBusiAppAdapter;
import com.baidu.searchbox.businessadapter.NovelBusiReaderAdapter;
import com.baidu.searchbox.discovery.novel.bdact.task.NovelBaseBdActTask;
import com.baidu.searchbox.discovery.novel.bdact.task.NovelBdActReadTimeTask;
import com.baidu.searchbox.novel.basic.utils.abtest.NovelAbTestManager;
import com.baidu.searchbox.novel.common.utils.FeedOpenReaderCallback;
import com.baidu.searchbox.novel.common.utils.FeedTransformHelper;
import com.baidu.searchbox.novel.common.utils.NovelNightModeUtils;
import com.baidu.searchbox.novel.common.utils.StubToolsWrapperKt;
import com.baidu.searchbox.novel.service.monitor.abnormal.NovelReaderAbnormalKey;
import com.baidu.searchbox.novel.service.monitor.abnormal.NovelReaderActionsRealTimeKey;
import com.baidu.searchbox.novel.service.reader.BookInfo;
import com.baidu.searchbox.novel.service.reader.Chapter;
import com.baidu.searchbox.novel.service.reader.IDismissable;
import com.baidu.searchbox.novel.tts.api.NovelTtsInterface;
import com.baidu.searchbox.noveladapter.abtest.NovelAbTest;
import com.baidu.searchbox.noveladapter.account.NovelBoxAccountManagerWrapper;
import com.baidu.searchbox.noveladapter.autiotts.NovelMiniPlayerViewWrapper;
import com.baidu.searchbox.noveladapter.browser.HostDeviceUtilsWrapper;
import com.baidu.searchbox.noveladapter.elasticthread.NovelExecutorUtilsExt;
import com.baidu.searchbox.noveladapter.eventbus.ContainerEventRegister;
import com.baidu.searchbox.noveladapter.feedback.INovelSearchboxFeedbackInterface;
import com.baidu.searchbox.noveladapter.galaxy.NovelGalaxyWrapper;
import com.baidu.searchbox.noveladapter.launchrestore.NovelPageRestoreDataWrapper;
import com.baidu.searchbox.noveladapter.pyramid.NovelServiceManager;
import com.baidu.searchbox.noveladapter.sdkinterface.novelinterface.NovelAPIDelegate;
import com.baidu.searchbox.noveladapter.sdkinterface.novelinterface.NovelApiInternal;
import com.baidu.searchbox.noveladapter.sdkinterface.novelinterface.info.NovelInfo;
import com.baidu.searchbox.noveladapter.skin.NovelNightModeHelperWrapper;
import com.baidu.searchbox.noveladapter.ubc.NovelUBCDurationSearchSessionWrapper;
import com.baidu.searchbox.noveladapter.ui.NovelToastWrapper;
import com.baidu.searchbox.reader.BannerAnimHelper;
import com.baidu.searchbox.reader.BaseActivity;
import com.baidu.searchbox.reader.ISignRewardCoin;
import com.baidu.searchbox.reader.R;
import com.baidu.searchbox.reader.ReaderManager;
import com.baidu.searchbox.reader.ReaderManagerCallback;
import com.baidu.searchbox.reader.ReaderMethodDispatcher;
import com.baidu.searchbox.reader.ReaderModelCallback;
import com.baidu.searchbox.reader.ReaderServiceHelper;
import com.baidu.searchbox.reader.ad.ReaderAdViewCache;
import com.baidu.searchbox.reader.ad.ReaderAdViewManager;
import com.baidu.searchbox.reader.ad.ReaderBannerAdUpdateManager;
import com.baidu.searchbox.reader.ad.ReaderBannerAdViewManager;
import com.baidu.searchbox.reader.ad.ReaderChapterTailAdViewManager;
import com.baidu.searchbox.reader.anim.AnimationFactory;
import com.baidu.searchbox.reader.common.ReaderSettingsHelper;
import com.baidu.searchbox.reader.enums.ReaderBaseEnum;
import com.baidu.searchbox.reader.enums.ReaderBaseEnum.ServiceTaskType;
import com.baidu.searchbox.reader.interfaces.IJiLiLadderCallBack;
import com.baidu.searchbox.reader.interfaces.ILoginCallBack;
import com.baidu.searchbox.reader.interfaces.INovelDownloadQueryCallBack;
import com.baidu.searchbox.reader.interfaces.NovelRequestListener;
import com.baidu.searchbox.reader.interfaces.ReaderBaseApi;
import com.baidu.searchbox.reader.interfaces.ReaderBaseApplication;
import com.baidu.searchbox.reader.interfaces.ReaderBaseLibrary;
import com.baidu.searchbox.reader.launchrestore.NovelLrLegalHelper;
import com.baidu.searchbox.reader.launchrestore.NovelLrManager;
import com.baidu.searchbox.reader.litereader.util.ThreadUtils;
import com.baidu.searchbox.reader.litereader.view.litemenu.NewReaderPerformanceAdView;
import com.baidu.searchbox.reader.litereader.view.newreader.SeekbarBubbleView;
import com.baidu.searchbox.reader.litereader.view.readerframe.BBAMenuTTSModel;
import com.baidu.searchbox.reader.litereader.view.readerframe.BBATTSCenterModel;
import com.baidu.searchbox.reader.monitor.ReaderMonitorUtils;
import com.baidu.searchbox.reader.newreader.AutoChangeNightServiceHelper;
import com.baidu.searchbox.reader.newreader.ReaderService;
import com.baidu.searchbox.reader.statistic.StatisticEvent;
import com.baidu.searchbox.reader.statistic.StatisticListener;
import com.baidu.searchbox.reader.statistic.StatisticManager;
import com.baidu.searchbox.reader.utils.AllScenesStatisticUtils;
import com.baidu.searchbox.reader.utils.BookInvokeUtils;
import com.baidu.searchbox.reader.utils.ReaderBookTailThanksViewManager;
import com.baidu.searchbox.reader.utils.ReaderLog;
import com.baidu.searchbox.reader.utils.ReaderStatusBarUtil;
import com.baidu.searchbox.reader.utils.ReaderTimeLogger;
import com.baidu.searchbox.reader.utils.ReaderTimeTag;
import com.baidu.searchbox.reader.utils.RouterProxy;
import com.baidu.searchbox.reader.utils.SharedPreferenceUtils;
import com.baidu.searchbox.reader.utils.SpeechDataHelper;
import com.baidu.searchbox.reader.utils.StatisticUtils;
import com.baidu.searchbox.reader.utils.StatisticsContants;
import com.baidu.searchbox.novel.basic.utils.UIUtils;
import com.baidu.searchbox.reader.view.NovelReaderCallbackDataType;
import com.baidu.searchbox.reader.view.NovelReaderCallbackNotifyType;
import com.baidu.searchbox.reader.view.NovelReaderCallbackViewType;
import com.baidu.searchbox.reader.view.PageToast;
import com.baidu.searchbox.reader.view.ReaderCleanHelper;
import com.baidu.searchbox.reader.view.ReaderConstant;
import com.baidu.searchbox.reader.view.ReaderUtility;
import com.baidu.searchbox.reader.view.SpeechControlMenuView;
import com.baidu.searchbox.reader.view.lastpage.LastPageManager;
import com.baidu.searchbox.reader.view.lastpage.LastPageRepository;
import com.baidu.searchbox.reader.view.newreader.ReadCurrentPageView;

import org.geometerplus.android.fbreader.BBACreateEngineProxy;
import org.geometerplus.android.fbreader.ChangeBrightnessAction;
import org.geometerplus.android.fbreader.ChangeParagraphAction;
import org.geometerplus.android.fbreader.SetScreenOrientationAction;
import org.geometerplus.android.fbreader.ShowPreferencesAction;
import org.geometerplus.android.fbreader.UserEduView;
import org.geometerplus.android.fbreader.readerframe.BBAADUtilController;
import org.geometerplus.android.fbreader.readerframe.BBAAdViewController;
import org.geometerplus.android.fbreader.readerframe.BBARecommendPageProviderImpl;
import org.geometerplus.android.fbreader.readerframe.BBATempResourceProviderImpl;
import org.geometerplus.android.fbreader.readerframe.BBATitlePageController;
import org.geometerplus.android.fbreader.readerframe.BBATitlePageResponseCallback;
import org.geometerplus.android.fbreader.readerframe.anim.BBAAutoScrollMenuAnimatorProvider;
import org.geometerplus.android.fbreader.readerframe.anim.BBABrightnessMenuAnimatorProvider;
import org.geometerplus.android.fbreader.readerframe.anim.BBADirectoryMenuAnimatorProvider;
import org.geometerplus.android.fbreader.readerframe.anim.BBAMainMenuAnimatorProvider;
import org.geometerplus.android.fbreader.readerframe.anim.BBAMoreFontMenuAnimatorProvider;
import org.geometerplus.android.fbreader.readerframe.anim.BBAMoreMenuAnimatorProvider;
import org.geometerplus.android.fbreader.readerframe.anim.BBAMoreSettingMenuAnimatorProvider;
import org.geometerplus.android.fbreader.readerframe.anim.BBASettingMenuAnimatorProvider;
import org.geometerplus.android.fbreader.readerframe.menu.BBAAutoAddBookshelfModel;
import org.geometerplus.android.fbreader.readerframe.menu.BBAAutoBuyPageModel;
import org.geometerplus.android.fbreader.readerframe.menu.BBAFontSettingModel;
import org.geometerplus.android.fbreader.readerframe.menu.BBAMainCenterModel;
import org.geometerplus.android.fbreader.readerframe.menu.BBAMenuCommentModel;
import org.geometerplus.android.fbreader.readerframe.menu.BBAMenuVipModel;
import org.geometerplus.android.fbreader.readerframe.menu.BBARewardModel;
import org.geometerplus.android.fbreader.readerframe.menu.BBARightTopOperateModel;
import org.geometerplus.android.fbreader.readerframe.menu.BBAVideoAdAutoPlayModel;
import org.geometerplus.android.fbreader.readerframe.moresetting.BBAAutoAddBookShelfController;
import org.geometerplus.android.fbreader.readerframe.moresetting.BBAFunctionRestRemindImpl;
import org.geometerplus.android.fbreader.readerframe.moresetting.BBAFunctionScreenImpl;
import org.geometerplus.android.fbreader.readerframe.tools.BBABookInfoTransformTools;
import org.geometerplus.android.fbreader.readerframe.tools.BBAEduTextTools;
import org.geometerplus.android.fbreader.readerframe.tools.BBAImageLoaderTools;
import org.geometerplus.android.fbreader.readerframe.tools.BBALegalContentCacheManager;
import org.geometerplus.android.fbreader.readerframe.tools.BBANotifyEventTools;
import org.geometerplus.android.fbreader.readerframe.tools.BBAReadTimeManager;
import org.geometerplus.android.fbreader.readerframe.tools.BBAReaderContainerTools;
import org.geometerplus.android.fbreader.readerframe.tools.BBAReaderDurationTools;
import org.geometerplus.android.fbreader.readerframe.tools.BBASwitchChapterUBCEventTools;
import org.geometerplus.android.fbreader.readerframe.view.BBABookMarkController;
import org.geometerplus.android.fbreader.readerframe.view.BBABubbleGuideController;
import org.geometerplus.android.fbreader.readerframe.view.BBADownloadStatusProvider;
import org.geometerplus.android.fbreader.readerframe.view.BBAEduViewController;
import org.geometerplus.android.fbreader.readerframe.view.BBALoadingViewController;
import org.geometerplus.android.fbreader.readerframe.view.BBALoginController;
import org.geometerplus.android.fbreader.readerframe.view.BBALoginViewController;
import org.geometerplus.android.fbreader.readerframe.view.BBAOperateController;
import org.geometerplus.android.fbreader.readerframe.view.BBAPayViewController;
import org.geometerplus.android.fbreader.readerframe.view.BBAPreLoadController;
import org.geometerplus.android.fbreader.readerframe.view.BBAReaderProgressController;
import org.geometerplus.android.fbreader.readerframe.view.BBATTSViewController;
import org.geometerplus.android.util.APIUtils;
import org.geometerplus.android.util.FBReaderPreHelper;
import org.geometerplus.android.util.ReaderDurationUtil;
import org.geometerplus.android.util.ReaderPerfMonitor;
import org.geometerplus.android.util.UIUtil;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.ColorProfile;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.FBReaderConstant;
import org.geometerplus.fbreader.fbreader.FBView;
import org.geometerplus.fbreader.fbreader.VoicePlayHelper;
import org.geometerplus.fbreader.fbreader.VoicePlayManager;
import org.geometerplus.fbreader.fbreader.ad.transmit.NovelAdResolver;
import org.geometerplus.fbreader.service.LoadOfflineableServiceTask;
import org.geometerplus.fbreader.service.LoadResourceListService;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.library.ZLibrary;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.core.service.ZLService;
import org.geometerplus.zlibrary.core.service.ZLServiceTask;
import org.geometerplus.zlibrary.core.service.ZLServiceThread;
import org.geometerplus.zlibrary.core.service.ZLServiceThread.ServiceTaskList;
import org.geometerplus.zlibrary.text.model.ZLTextModelList;
import org.geometerplus.zlibrary.text.model.ZLTextModelList.ReadType;
import org.geometerplus.zlibrary.text.model.ZLTextModelListImpl;
import org.geometerplus.zlibrary.text.view.style.ZLTextStyleCollection;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;
import org.geometerplus.zlibrary.ui.android.util.BBAAdUtil;
import org.geometerplus.zlibrary.ui.android.view.AndroidFontUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 展示内容的activity
 */
@Keep
public final class FBReader extends BaseActivity implements BBANetworkChangeUtils.OnNetWorkConnectionChangeListener,
        ILoginCallBack, FeedOpenReaderCallback,NoProGuard {

    /** tag */
    private static final String TAG = "FBReader";
    private static final String TAG_MONITOR_HONOR = "liyang151";
    /** debug开关 */
    private static final boolean DEBUG = FBReaderConstant.READER_DEBUG;
    /** 10.4:上下翻页总开关 */
    private static final boolean SUPPORT_VERTICAL_SCROLL = true;

    /** Extra key for book data */
    static final String BOOK_KEY = "fbreader.book";
    /** Extra key for bookmark data */
    static final String BOOKMARK_KEY = "fbreader.bookmark";

    static final int ACTION_BAR_COLOR = Color.DKGRAY;

    static final int REQUEST_PREFERENCES = 1;
    static final int REQUEST_CANCEL_MENU = 2;

    static final int RESULT_DO_NOTHING = RESULT_FIRST_USER;
    static final int RESULT_REPAINT = RESULT_FIRST_USER + 1;

    /** 第一次进入fbreader存储启动用户教育的key */
    public static final String KEY_USER_EDU = "key_reader_user_edu";
    /** 启动左手模式存储启动用户教育的key */
    public static final String KEY_LEFTHAND_USER_EDU = "key_lefthand_reader_user_edu";
    /** 存放休息提醒时间间隔的key */
    public static final String KEY_REST_TIME = "key_rest_time";

    /** 是否强制显示用户教育的key，该key需要结合KEY_USER_EDU使用，主要解决8.3必须都显示教育引导图 */
    /** V12.30 冗余功能下线 */
    /** public static final String KEY_MUST_SHOW_USER_EDU = "key_must_show_user_edu"; */
    /** 上下翻页模式用户教育的key */
    public static final String KEY_VERTICAL_SCROLL_USER_EDU = "key_vertical_scroll_user_edu";

    public static final String KEY_NOVEL_SP_NAME = "sp_novel";
    /** 2021.08.11 备注：记录当前全屏状态 FULL_SCREEN_TYPE：进入全屏； */
    private static final int FULL_SCREEN_TYPE = 0;
    /** 2021.08.11 备注：记录当前全屏状态 EXIT_FULL_SCREEN_TYPE：退出全屏； */
    private static final int EXIT_FULL_SCREEN_TYPE = 1;
    /** 2021.08.11 备注：记录当前全屏状态 EXIT_FULL_SCREEN_ONLY_BOTTOM_TYPE：退出全屏，仅展示底部虚拟按键； */
    private static final int EXIT_FULL_SCREEN_ONLY_BOTTOM_TYPE = 2;

    /** 开始阅读时间 */
    private long startRead;
    /** 停止阅读时间 */
    private long endRead;

    /** 控制onKeyDown多次，而一次onKeyUp的情况 */
    private boolean mCanChangeMenu = true;

//	/** 启动时初始化完成的标志，主要是用于判断是否执行过一次{@link #onStart()} */
//	private boolean mInited = false;
    /** 经过onNewIntent */
    private boolean mOnNewIntent = false;

//    /** 启动时初始化完成的标志，主要是用于判断是否执行过一次{@link #onStart()} */
//    private boolean mInited = false;
    /** 底部banner如果失败，重试的延时时长 */
    private int bannerRetryDelayTime = 10000;
    /** 缓存的{@link BookInfo}，用于透传信息 */
    private BookInfo mBookInfo;
    /** back键盘处于按下状态 */
    private boolean mIsKeyDownStatus = false;
//    private boolean mNeedOpenGlobalTTs;
    /** 加载章节内容的任务 */
    private LoadChapterTask loadChapterTask;
    private BBABook bbaBook;
    /** 激励视频结束 */
    private BBAReaderAdHandler readerAdHandler;
    /** VIP的model */
    private BBAMenuVipModel bbaMenuVipModel;
    /** 打赏的model */
    private BBARewardModel bbaRewardModel;
    /** 目录菜单是否正在展示 */
    private boolean directoryIsShowing;
    /** 2021.08.10 备注：记录当前全屏状态 0：进入全屏；1：退出全屏；2：仅底部虚拟按键展示 */
    private int currentFullScreen = FULL_SCREEN_TYPE;
    /** popwindow的背景色 */
    private View mPopBackgroundView;
    public boolean isCreate;
    /** 首次启动阅读器标记 */
    public boolean isFirstCreateReader;
    private View mNavigatorPaddingView;
    /** 是否是第一次添加网络监听 */
    private boolean isFirst = true;
    /** ab实验，首章不出广告 */
    private boolean isFirstChapter = true;
    /** 是否已经是放过 */
    private volatile boolean released;
    /** 本地书TTS中间部分model */
    private BBATTSCenterModel bbaTTSCenterModel;
    /** 更新bannerView的runnable */
    private UpdateBannerViewRunnable updateBannerViewRunnable;
    /** 底部banner默认广告位 */
    private View bannerDefaultView;
    /** 字体设置model */
    private BBAFontSettingModel fontSettingModel;
    /** 激励视频金币获取图层 */
    private ViewGroup rewardLayout;
    /** 是否是从网盘打开的txt书籍 */
    private boolean isFromWangpanTxt;
    private ViewStub mBottomReadCurrentPageMenuStub;

    private ZLAndroidLibrary getZLibrary() {
        return (ZLAndroidLibrary) ReaderBaseLibrary.Instance();
    }

    /** {@link FBReaderApp} */
    private FBReaderApp myFBReaderApp;
    /** 缓存的{@link Book} */
    private Book myBook;
    // 章尾广告
    private RelativeLayout mBannerAdLayout;
    /** 次位置的{@link RelativeLayout}，在actLayout之上 */
//    /** 处理loading展示隐藏的Controller */
    private BBALoadingViewController mBBALoadingController;
    private BBAEduViewController mBBAEduViewController;

    /** 自动加书架Controller */
    private BBAAutoAddBookShelfController mBbaAutoAddBookShelfController;
    /** 气泡引导控制类 */
    private BBABubbleGuideController mBBABubbleGuideController;
    /** 登陆控制类 */
    private BBALoginViewController mBBALoginViewController;
    /** 购买控制类 */
    private BBAPayViewController mBBAPayViewController;
    /** 书签控制类 */
    public BBABookMarkController mBBABookMarkController;

    /** 书尾页感谢语view */
    private RelativeLayout mThanksView;

    /** 浮层广告视图 */
    private NewReaderPerformanceAdView mNewReaderPerformanceAdView;
    /** 浮层广告是否已经初始化 */
    private volatile boolean isPerformanceAdViewInit = false;
    private int myFullScreenFlag;

    private static final String PLUGIN_ACTION_PREFIX = "___";

    private static volatile WakeLock mWakeLock;

    /** msg 开始屏幕保护倒计时 */
    private static final int MSG_END_SCREEN_PROTECT = 3;
    /** 刷新从本页读 */
    private static final int MSG_UPDATE_READ_CURRENT_PAGE = 5;
    /** 朗读到当前页 */
    private static final int MSG_READING_IN_PAGE = 6;
    /** 通知更新自动购买状态 */
    private static final int MSG_UPDATE_AUTO_BUY_STATUS = 7;
    /** 休息提醒间隔时间 */
    private static final long REST_TIME = 30000;
    /** 日间模式休息提醒页面背景颜色 */
    private int mRestBgColor;
    /** 日间模式休息提醒页面文字颜色 */
    private int mRestTextColor;
    /** 日间模式休息提醒页面进度条drawable */
    private Drawable mRestProgressDrawable;
    /** 休息提醒View */
    private View mRestView;
    /** 休息提醒进度条 */
    private ProgressBar mRestBar;
    /** 休息提醒页面文案1 */
    private TextView mRestViewText1;
    /** 用户教育界面 */
    private UserEduView mEduView;
    /** 休息提醒刷新runnable */
    private Runnable mRefreshRunnable;
    /** UI线程handler */
    private Handler mUIHandler;
    /** 阅读器是否处于前台的标志位置 */
    private boolean mIsReaderForeground;
    /**
     * 判断是不是第一次执行onStart，本来应该在onCreate中执行加入书架倒计时逻辑
     * 但是因为bookid在onStart中才获取 所以在第一次onStart中执行倒计时
     */
    private boolean mIsFirstStart = true;

    private View loadingLayout;
    /** 本地书内容错误页stub */
    private ViewStub contentErrorLayoutStub;
    /** 本地书内容错误页 */
    private View contentErrorLayout;
    /** 本地书内容错误页重新加载 */
    private View emptyBtnReload;
    /** 本地书内容错误页图标 */
    private ImageView emptyIcon;
    /**
     * 当前是否调起了保命弹窗
     */
    private boolean mInFloatGuide = false;

    /** tts "从本页读"view */
    private ReadCurrentPageView mBottomReadCurrentPageMenu;
    /** 13.23 阶梯激励视频的view */
    private View mJiLiLadderView;
    /** 修改底部banner广告的任务 */
    private UpdateBannerAdViewRunnable updateBannerAdViewRunnable;

    /** 批量下载  判断是否全部下载 */
    private static boolean isDownloadedAll = false;

    /**
     * 用于休息提醒
     */
    private Handler mRestHandler = new MyRestHandler(this);
    /**
     * 请求修改设置的权限
     */
    private static final int REQUEST_PERMISSION_SETTING = 1000;
    /**
     * 用于自动加书架
     */
//    private Handler mAutoAddShelfHandler = new MyAutoAddShelfHandler(this);

    /**
     * 左上角返回按钮热区
     */
//    private View mLeftCornerBackView;

    PopupWindow mFirstPopupWindow;

    /** // Lite5.10 Lite定制化Feed小说配置文件名称 */
    public static final String KEY_LITE_FEED_NOVEL_SP_NAME = "lite_feed_novel_reader_config";
    /**
     * 批量下载按钮show打点是否执行
     */
    private static boolean isDownloadShownUbc = false;

    /** 背景蒙层的进入动画 */
    private Animation mBgInAnimation;

    /** 背景蒙层的出去动画 */
    private Animation mBgOutAnimation;

    private Runnable resetRunnable;
    /** 是否已加载字体 */
    private boolean hasLoadFont = false;

    /** 下一页是否是强制广告位 */
    private boolean isNextForceAD = false;

    /** 挖孔屏状态runnable */
    private NotchScreenRunnable notchScreenRunnable;
    /** 主题变化runnable */
    private ReaderThemeChangedRunnable readerThemeChangedRunnable;
    /** tts听书关闭runnable */
    private GlobalTTsCloseRunnable globalTTsCloseRunnable;
    /** 隐藏所有菜单runnable */
    private DismissAllMenuRunnable dismissAllMenuRunnable;
    /** 展示菜单runnable */
    private ShowMenuRunnable showMenuRunnable;
    /** 延迟隐藏loading动画的runnable */
    private Runnable mDismissLoadingRunnable;

    /** 放在onResume中检查是否请求过首次阅读时间 */
    private boolean hasCheckedRequestOpenReaderTime = false;
    /** 上屏结束 */
    public boolean drawComplete = false;
    /** 是否点击了更改背景颜色 */
    private boolean mFromColorSettings = false;

    /** 当前 reader 实例保存一个 uuid，用于在 BBAReaderComponent clearAll 时 和 BBAReaderComponent 中的 uuid 作对比 */
    private String mReaderUUID = "";

    /**
     * 扉页数据加载完成
     */
    public void loadTitlePageComplete(int isSuccess, String s) {
        if (mReaderContainer != null) {
            mReaderContainer.loadTitlePageComplete(isSuccess, s);
        }
    }

    @Override
    public void superFinish() {
        super.finish();
//        if (!mNeedOpenGlobalTTs) {
        release();
//        }
    }

    /** // Lite5.10~ */

    private static class MyRestHandler extends Handler {
        private WeakReference<FBReader> reference;

        public MyRestHandler(FBReader fbReader) {
            this.reference = new WeakReference<>(fbReader);
        }

        @Override
        public void handleMessage(Message msg) {
            if (reference != null) {
                FBReader fbReader = reference.get();
                if (fbReader != null) {
                    int what = msg.what;
                    switch (what) {
                        case MSG_END_SCREEN_PROTECT:
                            if (mWakeLock != null && mWakeLock.isHeld()) {
                                ReaderLog.d(TAG, "release WakeLock");
                                try {
                                    mWakeLock.release();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case MSG_UPDATE_READ_CURRENT_PAGE:
                            fbReader.updateReadCurrentMenu();
                            break;
                        case MSG_READING_IN_PAGE:
                            fbReader.hideReadCurrentView();
                            break;
                        case MSG_UPDATE_AUTO_BUY_STATUS:
                            boolean autoBuy = (boolean) msg.obj;
                            BBAReaderConfig.getInstance().setMoreSettingIsAutoBuyPage(autoBuy);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    /** 翻页动画："turn_page"：0（无），1（平移），2（仿真） */
    private int mStatisticTurnPage = 0;
    /** 音量键翻页："volumn_turn"：0（关），1（开） */
    private int mStatisticTurnVolume = 1;
    /** 全屏点击翻页："fullscreen_touch_turn"：0（关），1（开） */
    private int mStatisticTurnFullscreen = 0;
    /** 屏幕关闭："shutdown_time"：1（2分钟），2（5分钟），3（10分钟），0（关闭） */
    private int mStatisticTurnShutdown = 1;
    /** 休息提醒："rest_notice"：0（关闭），1（30分钟），2（45分钟），3（60分钟） */
    private int mStatisticTurnRest = 3;
    /** 预加载："preload"：0（关闭），1（5章），2（10章），3（20章） */
    private int mStatisticTurnPreload = 1;
    /** 自动切换夜间模式："auto_display_mode_change"：0（关），1（开） */
    private int mStatisticTurnAutoChange = 0;

    /** 统计回调 */
    private StatisticListener mStatListener;

    /** 最近一次当前window失去焦点时, 系统亮度. 用于判定小说阅读器窗口失焦期间, 用户是否改变过系统亮度 */
    private int mLastBrightnessLoseFocus = -1;
    /** 用于异步操作的线程池 */
    private ExecutorService mExecutor;

    // 是否已经获取过阅读器顶部数据
    private boolean isHasFetchLegalReaderTopNotice = false;

    // 小说手百活动
    private Object novelBdTaskObj;

    // 小说手百活动针对lite
    private NovelBaseBdActTask novelBdTaskForLite;

    private boolean isCommentOnline; // 评论是否上线
    /** 重新打开 Txt */
    public static final String RE_OPEN_TXT = "reOpenTxt";

    private ViewGroup mRootView;

    /** 阅读器容器 */
    private BBAReaderContainer mReaderContainer;
    /** container容器宽高变化的监听回调 */
    private ViewTreeObserver.OnGlobalLayoutListener mContainerGlobalLayoutListener;
    /** 当前是否是小屏幕、分屏模式 */
    private boolean mIsMultiScreenMode = false;
    /** 上次右上角的标题 */
    private String mLastRTTitle;
    /** 阅读器容器高度 */
    private int mReaderContainerHeight = -1;
    /** 阅读器容器宽度（小窗口时可能会变） */
    private int mReaderContainerWidth = -1;
    /** menu容器 */
    public ViewGroup mMenuContainer;
    /** 护眼模式view */
    private View mEyeShield;
    //    /** 监听手机电池、时间信息的管理类 */
    private BBAPhoneStateManager mPhoneStateManager;
    /** 当前正在展示菜单的数量 */
    private int showingMenuNum;
    /** 休息提醒容器 */
    private ViewGroup mRestRemindContainer;
    /** 设置项功能实现(休息提醒) */
    private BBAFunctionRestRemindImpl bbaFunctionRestRemind;
    /** 屏幕关闭 */
    private BBAFunctionScreenImpl bbaFunctionScreen;
    /** 是否是正版书 */
    private boolean mIsLegal = true;
    /** 主菜单中心区域 */
    private BBAMainCenterModel bbaMainCenterModel;

    /** 展示顶部提示notice */
    private Handler topNoticeHandler;
    /** 顶部提示notice展示逻辑 */
    private Runnable topNoticeRunnable;
    private int lastLoginStatus = -1;

    /** 是否首次执行onResume */
    private boolean isFirstResume = true;
    private ReaderContext readerContext;

    /** 进行标记是否已经调用了全局运营位的请求 */
    private volatile boolean isHaveShowGlobalOperationView = false;

    /**
     * 开始记录的时间戳
     */
    private long mRecordStartTime = -1;

    /**
     * 记录来源
     * 手百个人中心书架：personal_shelf;
     * 手百中心外漏卡片：personal_card_shelf;
     * 手百外部打开txt：outlocalbook;
     * 手百下载中心打开txt：inlocalbook;
     * 小说书架：novelshelf;
     * 各种scheme以及隐式跳转采用参数传递的fromaction值;
     */
    private String mRecordDurationFrom = "default";

    /**
     * 各种scheme以及隐式跳转采用参数传递的fromaction值;
     * 无法获取来源：from值为default; 此时记录scheme 的值
     */
    private String mRecordDurationValue = "";


    /**
     * 更新本地记录的间隔时长：30s
     */
    private static final long M_DURATION_DELAY_MILLIS = 30 * 1000;

    /** 用于计时刷新存储时长 */
    private Handler mTimerHandler = new TimeHandler(this);

    /**
     * 底部banner动画工具类
     */
    private BannerAnimHelper mBannerAnimHelper;
    /**
     * feed打开动画
     */
    private FeedTransformHelper transformHelper;

    /** 滑动触摸坐标 */
    private float mTouchStartX = 0f;

    /** 滑动感知区域 */
    private final float slideSensorArea = BBADeviceUtil.ScreenInfo.getDisplayWidth() * 0.1f;
    /** 滑动阈值 */
    private final float slideThreshold = BBADeviceUtil.ScreenInfo.getDisplayWidth() * 0.1f;

    private static class TimeHandler extends Handler {
        private WeakReference<FBReader> reference;
        static final int MSG_UPDATE = 1;

        public TimeHandler(FBReader fbReader) {
            this.reference = new WeakReference<>(fbReader);
        }

        @Override
        public void handleMessage(Message msg) {
            if (reference != null) {
                FBReader fbreader = reference.get();
                if (fbreader != null
                        && !fbreader.isFinishing()
                        && !fbreader.isDestroyed()) {
                    switch (msg.what) {
                        case MSG_UPDATE: {
                            fbreader.getFromAndValue();
                            // 更新本地时长
                            ReaderDurationUtil.updateDurationToSP(
                                    fbreader,
                                    1,
                                    System.currentTimeMillis() - fbreader.mRecordStartTime,
                                    fbreader.mRecordDurationValue,
                                    fbreader.mRecordDurationFrom,
                                    fbreader.getRecordDurationPage());

                            // 重新记录开始时间戳
                            if (fbreader.mTimerHandler != null) {
                                // 记录开始的时间戳
                                fbreader.mRecordStartTime = System.currentTimeMillis();
                                // 发送一个30s后延迟消息:用于将30s时间更新到sp
                                fbreader.mTimerHandler.removeMessages(MSG_UPDATE);
                                fbreader.mTimerHandler.sendEmptyMessageDelayed(MSG_UPDATE, M_DURATION_DELAY_MILLIS);
                                ReaderDurationUtil.log(" start recorder 1 ");
                            }
                            break;
                        }
                        default:
                            super.handleMessage(msg);
                            break;
                    }
                }
            }
        }
    }

    /**
     * 获取当前page值
     *
     * @return string page value
     */
    private String getRecordDurationPage() {
        String page = "legal";
        if (isLocalBook()) {
            page = "local";
        }
        return page;
    }

    /**
     * 开始记录时长的计时器
     */
    private void startRecordDurationTimer() {
        if (mTimerHandler != null) {
            // 记录开始的时间戳
            mRecordStartTime = System.currentTimeMillis();
            // 发送一个30s后延迟消息:用于将30s时间更新到sp
            ReaderDurationUtil.log(" start recorder 1 ");
            mTimerHandler.removeMessages(TimeHandler.MSG_UPDATE);
            mTimerHandler.sendEmptyMessageDelayed(TimeHandler.MSG_UPDATE, M_DURATION_DELAY_MILLIS);
        }
    }

    private void getFromAndValue() {
        ReaderManagerCallback callBack = ReaderUtility.getReaderManagerCallback();
        if (callBack != null) {
            mRecordDurationFrom = (String) callBack.getData(GET_READER_FROM_ACTION_LEGAL_OR_TXT, null);
            if ("default".equals(mRecordDurationFrom)) {
                mRecordDurationValue = (String) callBack.getData(GET_READER_VALUE_ACTION_LEGAL_OR_TXT, null);
            }
            ReaderDurationUtil.log(" getFromAndValue mRecordDurationFrom 1 = " + mRecordDurationFrom);
        }
    }

    /**
     * 结束记录时长的计时器
     */
    private void stopRecordDurationTimer() {
        if (mTimerHandler != null) {
            ReaderDurationUtil.log(" stop recorder 2 ");
            mTimerHandler.removeMessages(TimeHandler.MSG_UPDATE);
            // 上传时长，并累加不到30s的时间
            // 更新本地时长
            ReaderDurationUtil.log(" mRecordDurationFrom 2 = " + mRecordDurationFrom);
            getFromAndValue();
            ReaderDurationUtil.updateDurationToSP(
                    this,
                    1,
                    System.currentTimeMillis() - mRecordStartTime,
                    mRecordDurationValue,
                    mRecordDurationFrom,
                    getRecordDurationPage());
            // 上传打点
            ReaderDurationUtil.uploadReaderDuration(this);
        }
    }

    /**
     *
     */
    private void destroyRecordDurationTimer() {
        ReaderDurationUtil.log(" destory recorder 3 ");
        if (mTimerHandler != null) {
            mTimerHandler.removeMessages(TimeHandler.MSG_UPDATE);
        }
    }


    @Override
    public Activity getActivity() {
        return this;
    }

    public NovelBaseBdActTask getNovelBdTaskObj() {
        return novelBdTaskForLite;
    }

    /**
     * 触发统计回调
     *
     * @param event  统计事件
     * @param values 相关统计值
     */
    private void logStatisticEvent(StatisticEvent event, String... values) {
        if (mStatListener != null) {
            mStatListener.onStatisticEvent(event, values);
        }
    }

    /**
     * 初始化{@link Book}对象
     *
     * @param intent {@link Intent}
     */
    private Book initBook(Intent intent) {
        Book book = null;
        if (intent != null) {
            String action = intent.getAction();
            if (ReaderManager.ACTION_OPEN_BOOK_WITH_JSON.equals(action)) {
                String bookInfoJSONString = intent.getStringExtra(ReaderManager.EXTRA_PARAM_BOOK_JSON_INFO);
                mBookInfo = BookInfo.parseJSONString(bookInfoJSONString);
                if (mBookInfo != null) {
                    book = getBook(mBookInfo);
                }
            } else if (TextUtils.equals(action, Intent.ACTION_VIEW)) {
                ZLFile file = ZLFile.createFileByUrl(intent.getDataString());
                if (file != null) {
                    book = new Book(file, "-1");
                    book.setReadType(ReadType.PLAIN_OFFLINE);
                }
            } else {
                Serializable extra = intent.getSerializableExtra(ReaderManager.EXTRA_PARAM_BOOK_INFO);
                if (extra != null && extra instanceof BookInfo) {
                    mBookInfo = (BookInfo) extra;
                    book = getBook(mBookInfo);
                }
            }
        }
        return book;
    }

    /**
     * 通过{@link BookInfo}获取{@link Book}
     *
     * @param bookInfo {@link BookInfo}
     * @return {@link Book}
     */
    private Book getBook(BookInfo bookInfo) {
        Book book = null;
        if (bookInfo != null && !TextUtils.isEmpty(bookInfo.getId())) {
            String novelId = bookInfo.getId();
            String displayName = bookInfo.getDisplayName();

            String onlineChapterId = bookInfo.getChapterId();
            ZLTextModelList.ReadType readType = ZLTextModelListImpl.getReadTypeFromBookInfoType(bookInfo.getType());
            String auxInfo = bookInfo.getExtraInfo();
            book = new Book(novelId, displayName, onlineChapterId, readType, auxInfo);
            book.setChapterIndex(bookInfo.getChapterIndex());
            book.setChapterOffset(bookInfo.getChapterOffset());
            book.setOldReadPosition(bookInfo.getOldReadPositionType(), bookInfo.getOldReadPosition());
            // 设置是否要强制跳转到最后一章的标记
            book.setGotoLast(bookInfo.getGotoLast());
            book.setFree(bookInfo.getFree());
            if (readType == ReadType.LOCAL_TXT) {
                // 本地txt 记录下 txtId
                book.setTxtId(bookInfo.getTxtId());
            }
            book.setNovelOnlineJsonStr(bookInfo.getNovelOnlineJsonStr());
        }
        return book;
    }

    /**
     * 打开书籍
     *
     * @param intent Intent
     * @param action 打开后的动作
     * @param force  是否强制打开（如果已经打开且此值为false，则不重新打开）
     */
    private synchronized void openBook(Intent intent, Runnable action, boolean force) {
        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.registerTag(ReaderTimeTag.TAG_READER_OPEN_BOOK, ReaderTimeTag.DES_READER_OPEN_BOOK);
            ReaderTimeLogger.recordStart(ReaderTimeTag.TAG_READER_OPEN_BOOK);
        }
        if (!force && myBook != null) {
            return;
        }

        myBook = initBook(intent);
        // 生成BBA的book类型，并将数据设置给BBA阅读器
        if (myFBReaderApp != null) {
            myFBReaderApp.openBook(myBook, null, action, null);
        }
        // openbook  也是开始计时的时机.防护杀进程退出手百之后，第一次进入阅读器，onResume时的myBook＝null的情况
        ReaderManagerCallback callback = ReaderManager.getInstance(this).getReaderManagerCallback();

        if (myBook != null) {
            BookInfo bookInfo = myBook.createBookInfo();
            if (callback != null) {
                // 阅读器开始计时
                int type = bookInfo.getType();
                ReaderLog.d(TAG, "ReadFlowManager novelId=" + bookInfo.getId());
                try {
                    BBAReaderDurationTools.getInstance().startReadDuration(
                            ReaderUtility.safeToLong(mBookInfo.getId()),
                            String.valueOf(type),
                            false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                String pageStr = "";
                if (mBookInfo != null) {
                    pageStr = mBookInfo.getpageInfo() == null ? "" : mBookInfo.getpageInfo();
                }
                logStatisticEvent(StatisticEvent.UBC_EVENT_START_READING,
                        String.valueOf(bookInfo.getType()), pageStr);
            } catch (NoSuchFieldError e) {
                ReaderLog.e(TAG, "novel_no_such_field_error");
            }
        }

        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.recordEnd(ReaderTimeTag.TAG_READER_OPEN_BOOK);
        }

        // 如果广告需要由广告联盟sdk渲染处理，则进入阅读器时触发回调
        if (NovelBusiReaderAdapter.handleAdByUnionSdk()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NovelAPIDelegate delegate = NovelApiInternal.getDelegate();
                    if (delegate != null) {
                        NovelInfo info = new NovelInfo();
                        BookInfo bookInfo = getBookInfo();
                        info.setNovelName(bookInfo.getDisplayName());
                        info.setChapterName(bookInfo.getCurrentChapterName());
                        info.setNovelId(bookInfo.getId());
                        info.setReaderDuration(ReaderUtility.getCurrentBookReadTime());
                        delegate.enterReader(info);
                    }
                }
            });
        }
    }

    /**
     * 判断阅读器是否正在前台显示
     *
     * @return true代表阅读器处于前台显示中
     */
    public boolean isReaderForeground() {
        return mIsReaderForeground;
    }

    /**
     * 判断下一页是否是强制广告位
     *
     * @return
     */
    public boolean isNextForceAD() {
        return isNextForceAD;
    }

    public void setNextForceAD(boolean nextForceAD) {
        if (nextForceAD) {
            if (mReaderContainer != null && mReaderContainer.getAdapter() != null) {
                mReaderContainer.getAdapter().setEduTextForForceAd();
            }
        }

        this.isNextForceAD = nextForceAD;
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        if (mReaderContainer != null && mReaderContainer.getAdapter() != null) {
            mReaderContainer.getAdapter().setMultiScreenSign(true);
        }
        mIsMultiScreenMode = isInMultiWindowMode;
    }

    /**
     * 回调框获取离线数据
     */
    private void loadOfflineAbleData() {
        ZLService service = ReaderServiceHelper.getModelService(getActivity());
        if (service == null) {
            return;
        } else {
            FBReaderApp app = (FBReaderApp) FBReaderApp.Instance();
            if (app != null) {
                Book book = app.getBook();
                if (book != null) {
                    String id = book.getNovelId();
                    if (!TextUtils.isEmpty(id)) {
                        ZLTextModelList.ReadType readType = book.getReadType();
                        LoadOfflineableServiceTask task = new LoadOfflineableServiceTask(this,
                                ZLServiceTask.createTaskId(id, readType, ZLServiceTask.TASK_TYPE_OFFLINEABLE),
                                book,
                                null);
                        service.addTask(ZLServiceThread.ServiceTaskList.LEVEL_IMMEDIATELY, id,
                                ServiceTaskType.OFFLINEABLE, task, true);
                    }
                }
            }
        }
    }

    /**
     * 取消loading后续业务逻辑
     */
    private void loadingDismiss() {
        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.recordEnd(ReaderTimeTag.TAG_READER_OPEN_NOVEL);
        }
        // 一次阅读器只获取一次
        if (mBookInfo != null && mBookInfo.getType() != BookInfo.TYPE_LOCAL_TXT //  txt文件不能请求顶部提醒
                && !isHasFetchLegalReaderTopNotice) {
            isHasFetchLegalReaderTopNotice = true;
            // zhuyong01备注：阅读器loading消失，证明可以排版完成，通知可以获取正版阅读器顶部提醒数据
            if (topNoticeHandler == null) {
                topNoticeHandler = new Handler();
            }
            if (topNoticeRunnable == null) {
                topNoticeRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // 如果是扉页,不展示顶部运营位
                        if (!isTitlePageShowing() && !isTitlePagePositionByStorage()) {
                            // 如果有引导页面，则不展示提醒
                            if (mBBAEduViewController != null && mBBAEduViewController.isVisibility()) {
                                return;
                            }
                            // 展示顶部提醒运营位
                            if (mBookInfo != null) {
                                BBAOperateController.getInstance().showTopNotice(getActivity(), mBookInfo.getId());
                            }
                        }
                    }
                };
            }
            // 延时1s执行
            topNoticeHandler.postDelayed(topNoticeRunnable, 1000);
        }
        refreshTtsEntranceStatus();
    }

    // 前后台切换
    private ContainerEventRegister.OnBackForegroundEventResult backForegroundEventResult =
            new ContainerEventRegister.OnBackForegroundEventResult() {
                @Override
                public void onResult(boolean isForceGround) {
                    if (!isForceGround) {
                        uploadReadProgress();
                    }
                }
            };

    /**
     * 上传上报进度
     */
    private void uploadReadProgress() {
        NovelBoxAccountManagerWrapper manager = NovelServiceManager.getNovelBoxAccountManager();
        if (manager != null) {
            // 如果用户登录了
            if (manager.isLogin(manager.NO_SUPPORT_GUEST_LOGIN)) {
                FBReaderApp app = (FBReaderApp) ReaderBaseApplication.Instance();
                if (app != null) {
                    if (bbaBook == null) {
                        return;
                    }
                    app.storePosition();
                    Book mBook = app.getBook();
                    if (mBook != null) {
                        String chapterId = mBook.getChapterId();
                        if (TextUtils.isEmpty(chapterId)) {
                            BBABookChapter chapter = bbaBook.getCurrentChapter();
                            if (chapter != null) {
                                chapterId = chapter.getChapterId();
                            }
                        }

                        String bookId = null;
                        if (mBookInfo != null) {
                            bookId = mBookInfo.getId();
                        }
                        if (TextUtils.isEmpty(bookId)) {
                            return;
                        }

                        String newBookId = bbaBook.getBookId();
                        String newChapterId = bbaBook.getCurrentChapterId();
                        if (!TextUtils.equals(newBookId, bookId) || !TextUtils.equals(newChapterId, chapterId)) {
                            bookId = newBookId;
                            chapterId = newChapterId;
                        }
                        if (TextUtils.isEmpty(bookId) || TextUtils.isEmpty(chapterId)) {
                            return;
                        }

                        if (!chapterId.startsWith(bookId)) {
                            return;
                        }

                        int paragraphIndex = 0;
                        int wordIndex = 0;

                        String chapterOffset = mBook.getChapterOffset();
                        if (!TextUtils.isEmpty(chapterOffset)) {
                            String[] paragraphArray = chapterOffset.split(":");
                            if (paragraphArray != null && paragraphArray.length > 1) {
                                try {
                                    paragraphIndex = Integer.valueOf(paragraphArray[0]);
                                    wordIndex = Integer.valueOf(paragraphArray[1]);
                                } catch (Exception e) {

                                }
                            }
                        }
                        // 如果是扉页 那么进度设置为-1
                        BBAPage page = getSaveProgressValidPage();
                        if (page != null) {
                            if (page.isTitlePage()) {
                                paragraphIndex = -1;
                                wordIndex = 0;
                            }
                        }

                        ReaderManagerCallback callback = ReaderManager.getInstance(this).getReaderManagerCallback();
                        if (callback != null) {
                            callback.uploadReaderProgress(bookId, chapterId, paragraphIndex, wordIndex);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取默认的 字符大小
     */
    public int getDefaultFontLevel() {
        ReaderManagerCallback callback = ReaderManager.getInstance(this)
                .getReaderManagerCallback();
        if (callback == null) {
            return -1;
        }
        return callback.getFontLevel();
    }
    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        try {
            super.setRequestedOrientation(requestedOrientation);
        } catch (Throwable e) {
            // Only fullscreen activities can request orientation
            e.printStackTrace();
        }
    }
    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle icicle) {
        this.mReaderUUID = UUID.randomUUID().toString();
        BBAReaderComponent.getInstance().setReaderUUID(this.mReaderUUID);

//        ReaderSpeedUtil.start("阅读器上屏");
        AllScenesStatisticUtils.readerTime = String.valueOf(SystemClock.uptimeMillis());
        AllScenesStatisticUtils.startLoadingTime();
        AllScenesStatisticUtils.pageDuration = SystemClock.uptimeMillis();
        BBADurationManager.getInstance().start("上屏");
        if (ReaderManager.getInstance(this) != null
                && ReaderManager.getInstance(this).getReaderInitCallback() != null) {
            ReaderManager.getInstance(this)
                    .getReaderInitCallback()
                    .onReaderCreate(ReaderModelCallback.READER_TYPE_NOVLE
                            , this.hashCode());
        }
        transformHelper = new FeedTransformHelper(this);
//        transformHelper.onCreate(icicle);
        if (!transformHelper.isFeedOepn()) {
            try {
                overridePendingTransition(com.baidu.searchbox.novel.R.anim.slide_in_from_right,
                        com.baidu.searchbox.novel.R.anim.slide_out_to_left);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        boolean loadingSwitch = BBAReaderComponent.getInstance().getSwitch(
                BBAABTestConstants.NOVEL_READER_LOADING_SWITCH, false);
        ReaderManagerCallback callback = ReaderManager.getInstance(this).getReaderManagerCallback();
        boolean nightMode = NovelNightModeHelperWrapper.isNightMode();
        boolean modeChange = false;
        if (loadingSwitch) {
            if (BBAModeChangeHelper.isNightMode() != nightMode) {
                BBAModeChangeHelper.setModeState(nightMode ? BBAModeChangeHelper.NIGHT :
                        BBAModeChangeHelper.LIGHT, false);
                modeChange = true;
            }
        } else {
            BBAModeChangeHelper.setModeState(nightMode ? BBAModeChangeHelper.NIGHT :
                    BBAModeChangeHelper.LIGHT, false);
        }
        ReaderUtility.setFirstLoadChapter(true);
        BBAReaderConfig.getInstance().setFontSizeServerDefault(getDefaultFontLevel());
        BBAReaderConfig.getInstance().setIsLiteReader(false);
        if (callback != null) {
            callback.setReaderSetting(true);
        }
        super.onCreate(icicle);
//        if (transformHelper != null) {
//            transformHelper.onCreateSuperAfter(icicle);
//        }
        // 进行数据恢复
        if (icicle != null) {
            String session = icicle.getString("KEY_GET_STRING_UPDATE_UBC_DURATION_SESSION", "");
            if (!TextUtils.isEmpty(session)) {
                if (!NovelUBCDurationSearchSessionWrapper.isInSearchSession()) {
                    NovelUBCDurationSearchSessionWrapper.setInSearchSession(true);
                    NovelUBCDurationSearchSessionWrapper.setSSession(session);
                }
            }
        }




        readerContext = new ReaderContext(this);
        BBAReaderRuntime.setReaderContext(readerContext);
        if (loadingSwitch) {
            BBAThreadUtils.runOnLayoutThread(new Runnable() {
                @Override
                public void run() {
                    BBAReaderCoreApi.initEnvironment(BBAReaderCoreApi.getDefaultHeight());
                }
            });
        } else {
            BBAReaderCoreApi.initEnvironment(BBAReaderCoreApi.getDefaultHeight());
        }
//        BBAReaderCoreApi.setBookType(true);
        released = false;
        // 初始化书籍信息
        Intent intent = getIntent();
        myBook = initBook(intent);
        if (intent != null) {
            isFromWangpanTxt = getIntent().getBooleanExtra(ReaderManager.EXTRA_PARAM_FROM_WANGPAN, false);
        }
        bbaBook = BBABookInfoTransformTools.getBBABook(myBook, mBookInfo, true);
        BBAReaderComponent.getInstance().setBook(bbaBook);

        /*
        mNeedOpenGlobalTTs = getIntent().getBooleanExtra(INovelTts.IS_OPEN_VOICE_FROM_DETAIL, false);
        // 非全局听书，或听的不是转码类型书籍
        if (!mNeedOpenGlobalTTs || !NovelTtsInterface.getInstance().isLiteBook()) {
            BBAReaderComponent.getInstance().setBook(bbaBook);
        }
        // 打开听书，并且书籍是正版书，关闭转码阅读器，防止书籍信息混乱
        if (mNeedOpenGlobalTTs && !NovelTtsInterface.getInstance().isLiteBook()) {
            LiteReaderActivity lightReader = ReaderUtility.getLightReader();
            if (lightReader != null) {
                lightReader.finish();
            }
        }
        */

        BBAReaderConfig.getInstance().updateConfig();
        if (myBook != null) {
            NovelTtsInterface.getInstance().setCurrentReadBookId(myBook.getNovelId());
        }
        // 初始化业务配置数据
        initBusinessConfig();

        ReaderManager.getInstance(getApplicationContext()).setOpeningBook(true);
        ReaderManager.getInstance(getApplicationContext()).setClosingBook(false);
        if (readerAdHandler == null) {
            readerAdHandler = new BBAReaderAdHandler(this);
        }
        BBAEventBus.getInstance().addEventHandler(REWARD_PLAY_END, readerAdHandler);
        BBAEventBus.getInstance().addEventHandler(SCROLL_PAGE, readerAdHandler);
        BBAEventBus.getInstance().addEventHandler(JUMP_CHAPTER_AFTER, readerAdHandler);
        BBAEventBus.getInstance().addEventHandler(N_FILE_COMPLETED, readerAdHandler);
        BBAEventBus.getInstance().addEventHandler(RENDER_PAGE_COMPLETED, readerAdHandler);
        BBAEventBus.getInstance().addEventHandler(DRAW_BITMAP_TO_CANVAS, readerAdHandler);
        BBAEventBus.getInstance().addEventHandler(SCROLLING_PAGE_CHANGE, readerAdHandler);
        BBAEventBus.getInstance().addEventHandler(SO_LOAD_FAIL, readerAdHandler);
        // 初始化ZLApplication
        myFBReaderApp = (FBReaderApp) ReaderBaseApplication.Instance();
        if (myFBReaderApp == null) {
            myFBReaderApp = new FBReaderApp(this, null);
        }
        FBReaderPreHelper.reset();
        FBReaderPreHelper.saveReaderInitedEndTime();
        ReaderUtility.notifyHost(ReaderConstant.READER_ON_CREATE, "");
        myFBReaderApp.reloadSettings();
        myFBReaderApp.listenPhoneState();
        ReaderManager.getInstance(getActivity()).setApplication(myFBReaderApp);
        // 上传上次打点
        ReaderDurationUtil.uploadReaderDuration(this);
        try {
            // 2021.09.26 备注：在正版阅读器，设置处理广告上传信息管理类
            NovelAdResolver novelAdResolver = NovelAdResolver.getInstance();
            if (novelAdResolver != null) {
                ReaderManager.getInstance(getActivity()).setBaseNovelAdResolver(novelAdResolver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ReaderManager.getInstance(getApplicationContext()).isOpeningBook()) {
            myFBReaderApp.setPositionInited(false);
        }

        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.registerTag(ReaderTimeTag.TAG_READER_ON_CREATE, ReaderTimeTag.DES_READER_ON_CREATE);
            ReaderTimeLogger.recordStart(ReaderTimeTag.TAG_READER_ON_CREATE);
        }

        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.registerTag(
                    ReaderTimeTag.TAG_READER_START_COST_INIT_CONTENT_VIEW,
                    ReaderTimeTag.DES_READER_START_COST_INIT_CONTENT_VIEW);
            ReaderTimeLogger.recordStart(ReaderTimeTag.TAG_READER_START_COST_INIT_CONTENT_VIEW);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        BBAReaderComponent.getInstance().setOperateViewProvider(BBAOperateController.getInstance());
        BBAReaderComponent.getInstance().setADViewProvider(BBAAdViewController.getInstance());
        BBAReaderComponent.getInstance().setADUtilProvider(BBAADUtilController.getInstance());
        BBAReaderComponent.getInstance().setVoicePlayerProvider(
                NovelTtsInterface.getInstance().getIBBAVoicePlayerProvider());
        BBAReaderComponent.getInstance().setBBAIsDownloadProvider(BBADownloadStatusProvider.getInstance());
        BBAReaderComponent.getInstance().setLoginProvider(new BBALoginController());
        setContentView(R.layout.bdreader_newreader_main);
        rewardLayout = findViewById(R.id.reward_layout);
        loadingLayout = findViewById(R.id.content_loading_view);
        contentErrorLayoutStub = findViewById(R.id.content_error_view_stub);
//        View closeView = findViewById(com.baidu.searchbox.view.R.id.close);
        mBannerAdLayout = (RelativeLayout) findViewById(R.id.reader_banner_ad_layout);
        // 进行底部banner的高度动态设置
        dynamicSetAdHeight();
        mReaderContainer = findViewById(R.id.reader_horizontal_content);
        mRootView = findViewById(R.id.root_view);
        mMenuContainer = findViewById(R.id.menu_container);
        mRestRemindContainer = findViewById(R.id.rest_remind);
        mPopBackgroundView = findViewById(R.id.pop_bg);
        mNavigatorPaddingView = findViewById(R.id.navigator_bar_padding);
        if (mNavigatorPaddingView != null) {
            mNavigatorPaddingView.setBackgroundColor(BBAResourceHelper
                    .getColorTranslate(BBAModeTranslate.Color.BBA_MENU_MAIN_HEADER_MENU_COLOR));
        }
        showLoading(true);
        BBATitlePageController titlePageController = new BBATitlePageController();
        if (bbaBook != null) {
            titlePageController.getTitlePageData(bbaBook.getBookId(), new BBATitlePageResponseCallback());
        }
        BBAReaderComponent.getInstance().setTitlePageProvider(new BBATitlePageController());
        BBAReaderComponent.getInstance().setIBBARecommendPageProvider(new BBARecommendPageProviderImpl());
        BBAReaderComponent.getInstance().setIBBAEduTextProvider(new BBAEduTextTools());
        if (mReaderContainer != null) {
            mReaderContainer.setVerticalEduText();
            mReaderContainer.getViewTreeObserver().addOnGlobalLayoutListener(
                    createContainerGlobalListener());
        }

        // 判断是否需要隐藏广告，如激励视频时长、VIP、福利活动等
        boolean hideAd = BBAAdUtil.isHideAdView(false);
        BBAAdUtil.setLastAdState(!hideAd);
        // 本地书没有底部banner广告
        if (bbaBook != null && bbaBook.getReadType() == BBABook.LOCAL_TXT_BOOK) {
            mBannerAdLayout.post(new Runnable() {
                @Override
                public void run() {
                    mBannerAdLayout.setVisibility(GONE);
                }
            });
        }
        if (mBannerAdLayout != null && !isLocalBook()) {
            mBannerAdLayout.setVisibility(BBAAdUtil.isAdShowState() ? View.VISIBLE : GONE);
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) loadingLayout.getLayoutParams();
        boolean isBannerAdHide = mBannerAdLayout == null || mBannerAdLayout.getVisibility() == GONE;
        float marginBottom = isBannerAdHide ? 0 : ReaderUtility.getBannerAdHeight();
        layoutParams.topMargin = BBADeviceUtil.ScreenInfo.getStatusBarHeight();
        layoutParams.bottomMargin = (int) marginBottom;
        loadingLayout.setLayoutParams(layoutParams);
//        closeView.setVisibility(View.VISIBLE);
        BBASmoothProgressBar progressBar = loadingLayout.findViewById(R.id.progressbar);
        if (progressBar != null) {
            int dp30 = BBAUIUtils.dip2px(this, 63);
            if (BBAModeChangeHelper.isNightMode()) {
                Drawable drawable = progressBar.getContext().getResources().
                        getDrawable(R.drawable.bba_loading_progress_night_honor);
                drawable.setBounds(0, 0, dp30, dp30);
                progressBar.setIndeterminateDrawable(drawable);
                progressBar.setIndeterminate(true);
            } else {
                Drawable drawable = progressBar.getContext().getResources().
                        getDrawable(R.drawable.bba_loading_progress_light_honor);
                drawable.setBounds(0, 0, dp30, dp30);
                progressBar.setIndeterminateDrawable(drawable);
                progressBar.setIndeterminate(true);
            }
        }
//        TextView mMsg = loadingLayout.findViewById(R.id.message);
//        if (mMsg != null) {
//            if (BBAModeChangeHelper.isNightMode()) {
//                int color = mMsg.getContext().getResources().
//                        getColor(com.baidu.searchbox.view.R.color.bba_color_666666);
//                mMsg.setTextColor(color);
//            } else {
//                int color = mMsg.getContext().getResources().
//                        getColor(com.baidu.searchbox.view.R.color.bba_color_FFFFFFFF);
//                mMsg.setTextColor(color);
//            }
//        }
        if (mPhoneStateManager == null) {
            mPhoneStateManager = new BBAPhoneStateManager(this);
        }
        mPhoneStateManager.addIntentAction(Intent.ACTION_TIME_TICK);
        mPhoneStateManager.addIntentAction(Intent.ACTION_BATTERY_CHANGED);
        mPhoneStateManager.addOnPhoneStateChangedListener(mOnPhoneStateChangedListener);
        initMenu();
        BBAReaderContainerComponent.getInstance().setBBAReaderContainerListener(new IBBAReaderContainerListener() {
            @Override
            public void onClick(String type, String jsonStr) {
                switch (type) {
                    case IBBAReaderContainerListener.TITLE_CLICK:
                        StatisticUtils.ubc5425Click("", "", "", "");
                        exitReader();
                        // 左上角退出点击打点
                        ReaderUtility.notifyHost(ReaderConstant.NOVEL_TOP_LEFT_CLICK, "");
                        break;
                    case IBBAReaderContainerListener.RELOAD_CLICK:
                        if (!TextUtils.isEmpty(jsonStr)) {
                            try {
                                JSONObject jsonObject = new JSONObject(jsonStr);
                                int chapterIndex =
                                        jsonObject.optInt(IBBAReaderContainerListener.CLICK_JSON_CHAPTER_INDEX);
                                String chapterId =
                                        jsonObject.optString(IBBAReaderContainerListener.CLICK_JSON_CHAPTER_ID);
                                BBABook book = BBAReaderComponent.getInstance().getBook();
                                if (book != null) {
                                    BBAReaderCoreApi.sendChapterRequest(book.getBookId(), chapterIndex, chapterId);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        break;
                }
            }

            @Override
            public void onMoveRelease(int moveX, int moveY) {
                BBAProgressUtil.clearChapterIndexBySize();
                if (mReaderContainer != null) {
                    if (BBAReaderGestureManager.getInstance().isForceInsertAdMode()) {
                        // todo 强制视频模式的左滑动事件，左滑判断有问题，需修改
                        if (moveX > 0) {
                            ReaderUtility.notifyHost(ReaderConstant.NOVEL_AD_PAGE_LEFT_MOVE, "");
                        }
                        ReaderUtility.notifyHost(ReaderConstant.NOVEL_AD_PAGE_FORCE_MOVE, "");
                        return;
                    }
                    // 判断是否到达书首
                    boolean isBookFirst = mReaderContainer.isBookFirst();
                    if (isBookFirst) {
                        if (BBAReaderConfig.getInstance().getTurnPageType() == BBADefaultConfig.VERTICAL
                                ? moveY < 0 : moveX < 0) {
                            // 仿真翻页end已有第一页通知，此处不需要(BBAReaderContainer animateEnd)
                            if (BBAReaderConfig.getInstance().getTurnPageType() != HORIZONTAL_CURL) {
                                if (!BBAClickUtils.isFastClick("show_first_page", 3 * 1000)) {
                                    ReaderUtility.toast(getResources().getString(R.string.bba_turn_first_page));
                                }
                            }
                        }
                    } else {
                        // 判断是否到达书尾
                        boolean isBookTail = mReaderContainer.isBookTail();
                        if (isBookTail) {
                            if (BBAReaderConfig.getInstance().getTurnPageType() == BBADefaultConfig.VERTICAL
                                    ? moveY > 0 : moveX > 0) {
                                LastPageManager.callShowLastPage();
                                if (bbaBook != null && bbaBook.getReadType() == BBABook.LOCAL_TXT_BOOK) {
                                    Activity activity = getActivity();
                                    Resources resources = getResources();
                                    if (activity != null && resources != null) {
                                        NovelToastWrapper.showToast(activity, resources.getString(R.string.bba_turn_last_page));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onForceInsertAdMode() {
                if (mReaderContainer != null) {
                    if (BBAReaderGestureManager.getInstance().isForceInsertAdMode()) {
                        ReaderUtility.notifyHost(ReaderConstant.NOVEL_AD_PAGE_FORCE_MOVE_OTHER, "");
                    }
                }
            }
        });

        bbaFunctionRestRemind = new BBAFunctionRestRemindImpl(this, mRestRemindContainer);
        bbaFunctionScreen = new BBAFunctionScreenImpl(this);

        // 首次启动如果自动切换日夜间开关打开，则直接启动服务
        if (loadingSwitch) {
            boolean autoSwitchDayNight = BBAReaderConfig.getInstance().isMoreSettingIsAutoSwitchDayNight();
            if (autoSwitchDayNight) {
                setAutoDayAndNightService(autoSwitchDayNight);
            }
        } else {
            setAutoDayAndNightService(BBAReaderConfig.getInstance().isMoreSettingIsAutoSwitchDayNight());
        }
        initChapterTask();
        initFontController();

        setDefaultKeyMode(DEFAULT_KEYS_DISABLE); // setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.recordEnd(ReaderTimeTag.TAG_READER_START_COST_INIT_CONTENT_VIEW);
        }

        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.registerTag(
                    ReaderTimeTag.TAG_READER_START_COST_INIT_READERAPP,
                    ReaderTimeTag.DES_READER_START_COST_INIT_READERAPP);
            ReaderTimeLogger.recordStart(ReaderTimeTag.TAG_READER_START_COST_INIT_READERAPP);
        }

        final ZLAndroidLibrary zlibrary = getZLibrary();
        ReaderManager.getInstance(getActivity()).setLibrary(zlibrary);
        zlibrary.setReaderOrientation();

        zlibrary.setActivity(this);

        // 初始化阅读器通用接口分发
        ReaderMethodDispatcher mReaderMethodDispatcher = (ReaderMethodDispatcher) ReaderBaseApi.getInstance();
        if (mReaderMethodDispatcher == null) {
            mReaderMethodDispatcher = new ReaderMethodDispatcher();
        }

        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.recordEnd(ReaderTimeTag.TAG_READER_START_COST_INIT_READERAPP);
        }

        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.registerTag(
                    ReaderTimeTag.TAG_READER_START_COST_PAGE_CLEAR,
                    ReaderTimeTag.DES_READER_START_COST_PAGE_CLEAR);
            ReaderTimeLogger.recordStart(ReaderTimeTag.TAG_READER_START_COST_PAGE_CLEAR);
        }
        if (ReaderManager.getInstance(getApplicationContext()).isOpeningBook()) {
            // 启动时清除可能存在的ZLTextPage
            myFBReaderApp.clearAllPages();
        }
        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.recordEnd(ReaderTimeTag.TAG_READER_START_COST_PAGE_CLEAR);
        }

        myBook = null;
        if (ReaderManager.getInstance(getApplicationContext()).isOpeningBook()) {
            myFBReaderApp.initWindow();
        }

        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.registerTag(
                    ReaderTimeTag.TAG_READER_START_COST_INIT_CONTROLLERS,
                    ReaderTimeTag.DES_READER_START_COST_INIT_CONTROLLERS);
            ReaderTimeLogger.recordStart(ReaderTimeTag.TAG_READER_START_COST_INIT_CONTROLLERS);
        }
        // 设置上下翻页开启的开关：true代表开启
        ReaderManager.sSupportPageScroll = SUPPORT_VERTICAL_SCROLL;
        initControllers();
        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.recordEnd(ReaderTimeTag.TAG_READER_START_COST_INIT_CONTROLLERS);
        }

        PowerManager mPowerManager = null;
        if (getApplicationContext() != null) {
            mPowerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        } else {
            mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        }
        // v12.1 解决底部键盘灯常亮的问题
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "reader_wakelock");

        mStatListener = StatisticManager.getInstance().getListener();

        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.registerTag(
                    ReaderTimeTag.TAG_READER_START_COST_INIT_BACKGROUND_COLOR,
                    ReaderTimeTag.DES_READER_START_COST_INIT_BACKGROUND_COLOR);
            ReaderTimeLogger.recordStart(ReaderTimeTag.TAG_READER_START_COST_INIT_BACKGROUND_COLOR);
        }
        if (callback != null && myFBReaderApp != null) {
            if (NovelNightModeUtils.isNightMode() != BBAModeChangeHelper.isNightMode()) {
                // 若当前框为夜间，将阅读器设置为夜间
                if (callback.isSearchBoxNightMode()) {
                    myFBReaderApp.switchToNightMode();
                    myFBReaderApp.setColorProfileName(ColorProfile.NIGHT);
                } else {
                    // 若当前框为日间,将阅读器设置为日间
                    myFBReaderApp.switchToDayMode();
                    myFBReaderApp.setColorProfileName(myFBReaderApp.getColorProfileCachedName());
                }
            }
        }

        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.recordEnd(ReaderTimeTag.TAG_READER_START_COST_INIT_BACKGROUND_COLOR);
        }

        if (callback != null) {
            callback.onCreate(getActivity(), icicle);

            // 启动手百活动任务-阅读时间，返回任务对象持有
            if (NovelBusiActTaskAdapter.isSupportNovelBdActReadTimeTask()) {
                novelBdTaskObj = callback.getData(NovelReaderCallbackDataType.START_BD_ACT_READ_TIME_TASK, null);
            }
        }
        // 启动手百活动任务-阅读时间 针对lite
        if (NovelBusiActTaskAdapter.isSupportNovelBdActReadTimeTaskForLite()) {
            novelBdTaskForLite = new NovelBdActReadTimeTask().startTask();
        }

        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.recordEnd(ReaderTimeTag.TAG_READER_ON_CREATE);
        }

        logStatisticEvent(StatisticEvent.GMV_EVENT_OPEN_FBREADER);

        // 初始化UI handler
        mUIHandler = new Handler(getMainLooper());
        reportReaderBackgroundUBC();
        reportReaderBrightnessUBC();
        // 正版阅读器关闭广告词跳链
        if (myFBReaderApp != null && myFBReaderApp.hyperLinkTextManager != null) {
            myFBReaderApp.hyperLinkTextManager.setHypertextEnable(false);
        }
        if (myFBReaderApp != null) {
            // 进入正版阅读器的时间
            myFBReaderApp.mEnterReadTime = SystemClock.uptimeMillis();
        }
        // 冷启打点，进入阅读器
        if (myFBReaderApp != null) {
            if (myFBReaderApp.getColdLaunchStatus()) {
                NovelLrManager.onPageShow(NovelLrManager.PageId.NOVEL);
            }
        }

        initReadCurrentPageView();

        BBAStatusBarUtil.enterFullScreenMode(getWindow());
        if (loadingSwitch) {
            if (modeChange) {
                changeReaderNightMode(true, BBAModeChangeHelper.isNightMode());
            }
        } else {
            changeReaderNightMode(true, BBAModeChangeHelper.isNightMode());
        }
        BBAReaderCoreApi.setFontColor(true, null);
        BBAReaderCoreApi.setCreateEngineProxy(new BBACreateEngineProxy());
        setRootBackground();

        if (ReadCurrentPageView.needShow()) {
            showReadCurrentView();
        }
        initBgInAnimation();
        initBgOutAnimation();
        isCreate = true;
        isFirstCreateReader = true;
        // **网络监听在第一次被注册后会立刻回调当前网络状态**
        BBANetworkChangeUtils.getInstance().registerReceiver(this, this);
        registerLoginCallback();
        registerJiLiLadderCallback();
        if (mReaderContainer != null) {
            mReaderContainer.setFooterVisibility(GONE);
        }
        if (!loadingSwitch) {
            /**展示底部banner兜底广告*/
            showDefaultBottomBannerAd();
        }

        // 设置"更多设置项改变"监听
        BBASettingManager.getInstance().registerSettingChangeListener(this.toString(),
                new FBReaderSettingChangeListener());
        AllScenesStatisticUtils.onCreate();
        boolean adOpen = BBAReaderComponent.getInstance().getSwitch(
                BBAABTestConstants.NEWREADER_QIANGZHI_GUANGGAO_SWITCH, true);
        if (adOpen) {
            // 设置手势划动监听
            BBAReaderComponent.getInstance().setIBBAAdGestureClick(new IBBAAdGestureClick() {
                @Override
                public boolean onGestureClick(float distanceX, float distanceY) {
                    ReaderManagerCallback callback = getReaderManagerCallback();
                    if (callback != null) {
                        return callback.onGestureClick(distanceX, distanceY);
                    }
                    return false;
                }

                @Override
                public boolean onGestureTouch() {
                    if (mJiLiLadderView != null) {
                        hideJiliLadderView();
                    }
                    return false;
                }
            });
        }
        mBannerAnimHelper = new BannerAnimHelper(mReaderContainer).setBanner(mBannerAdLayout);
        mBannerAnimHelper.init();

        ContainerEventRegister.registerBackForegroundEventEvent(this, backForegroundEventResult);

        // 设置下次是否可以选择书架tab
        if (callback != null) {
            callback.setNeedNextSelectShelfTab(bbaBook != null && bbaBook.isLocalBook());
        }

        // 检查基础侧是否有用户权益
        if (callback != null) {
            callback.checkUserGrowthRights(true);
        }

        // 13.37 福利中心阅读器内入口
        registerChapterTailWelfareComponent();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (bbaFunctionScreen != null) {
                bbaFunctionScreen.setInScreenProtectedTime();
            }
            StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                    UBC_PAGE_READER_SETTING, UBC_SOURCE_LOCK_SCREEN);
        }
    }


    private int mUIMode = Configuration.UI_MODE_NIGHT_UNDEFINED;

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if ((newConfig.uiMode & Configuration. UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            if (mUIMode != Configuration.UI_MODE_NIGHT_YES) {
                if (NovelNightModeHelperWrapper.getFollowSysDarkModeState()) {
                    changeCacheAdViewMode();
                    changeReaderNightMode(false, true);
                    changeBannerAdNightMode();
                }
            }
            mUIMode = Configuration.UI_MODE_NIGHT_YES;
        } else if ((newConfig.uiMode & Configuration. UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO) {
            if (mUIMode != Configuration.UI_MODE_NIGHT_NO) {
                if (NovelNightModeHelperWrapper.getFollowSysDarkModeState()) {
                    changeCacheAdViewMode();
                    changeReaderNightMode(false, false);
                    changeBannerAdNightMode();
                }
            }
            mUIMode = Configuration.UI_MODE_NIGHT_NO;
        } else {
            mUIMode = Configuration.UI_MODE_NIGHT_UNDEFINED;
        }
    }

    private void registerChapterTailWelfareComponent() {
        ReaderManagerCallback callback = getReaderManagerCallback();
        if (callback == null) {
            return;
        }
        Object welfareProviderImpl = callback.getReaderComponentInterfacesImpl(IBBAChapterTailWelfareProvider.type);
        if (welfareProviderImpl instanceof IBBAChapterTailWelfareProvider) {
            BBAReaderComponent.getInstance().setBBAChapterTailWelfareProvider(
                    (IBBAChapterTailWelfareProvider) welfareProviderImpl);
        }
    }

    private void initMenu() {
        BBAMenuComponent.getInstance().initMenu(mMenuContainer);
        BBAMenuComponent.getInstance().setImageLoader(BBAImageLoaderTools.getInstance());
        BBAMenuComponent.getInstance().setMainMenuAnimatorProvider(new BBAMainMenuAnimatorProvider());
        BBAMenuComponent.getInstance().setDirectoryMenuAnimatorProvider(new BBADirectoryMenuAnimatorProvider());
        BBAMenuComponent.getInstance().setSettingMenuAnimatorProvider(new BBASettingMenuAnimatorProvider());
        BBAMenuComponent.getInstance().setMoreMenuAnimatorProvider(new BBAMoreMenuAnimatorProvider());
        BBAMenuComponent.getInstance().setAutoScrollMenuAnimatorProvider(new BBAAutoScrollMenuAnimatorProvider());
        BBAMenuComponent.getInstance().setMoreFontMenuAnimatorProvider(new BBAMoreFontMenuAnimatorProvider());
        BBAMenuComponent.getInstance().setMoreSettingMenuAnimatorProvider(new BBAMoreSettingMenuAnimatorProvider());
        BBAMenuComponent.getInstance().setBrightnessMenuAnimatorProvider(new BBABrightnessMenuAnimatorProvider());
        if (isFromWangpanTxt) {
            BBAMenuComponent.getInstance().removeMainHeaderLeft(0);
        }
        BBAMenuComponent.getInstance().addSettingMenuCallback(new IBBASettingMenuComponentCallback() {
            @Override
            public void onBrightnessChange(String type, boolean clickSystem, int fromprogress, int progress) {
                if (clickSystem) {
                    AllScenesStatisticUtils.ubc580ByReaderSetting(
                            "followsystem", progress == -1 ? "0" : "1",
                            progress == -1 ? "1" : "0");
                } else {
                    AllScenesStatisticUtils.ubc580ByReaderSetting(
                            "brightness",
                            AllScenesStatisticUtils.getUbcBrightness(fromprogress),
                            AllScenesStatisticUtils.getUbcBrightness(progress));
                }
                if (progress == -1) { // 系统亮度
                    setScreenBrightnessAuto();
                    StatisticUtils.ubc753(UBC_FROM_NOVEL, StatisticsContants.UBC_TYPE_CLICK,
                            StatisticsContants.UBC_PAGE_READER_SETTING, "followsystem", "");
                } else {
                    BBADeviceUtil.changeAppBrightness(progress, FBReader.this);
                    StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                            UBC_PAGE_READER_SETTING, UBC_SOURCE_BRIGHTNESS);
                }
            }

            @Override
            public void onFontChange(String scale, int fromIndex, int index) {
                BBAReaderComponent.getInstance().adViewStop();
                BBAReaderCoreApi.setFontLevel(index, true);
                AllScenesStatisticUtils.ubc580ByReaderSetting(
                        "fontlevel", String.valueOf(fromIndex),
                        String.valueOf(index + 1));
                StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                        UBC_PAGE_READER_SETTING, UBC_SOURCE_FONT_SIZE);
                ReaderMonitorUtils.logActionToReader(
                        NovelReaderActionsRealTimeKey.NOVEL_SETTING_CHANGE_FONT_SIZE);
            }
        });

        BBAMenuComponent.getInstance().addMenuComponentCallback(new IBBAMenuActionCallback() {

            @Override
            public void onClickCallBack(String type, BBAReaderBarItemModel model) {
                switch (type) {
                    case BBAReaderBarItemModel.EXIT: // 退出阅读器
                        StatisticUtils.ubc5425Click("", "", "", "");
                        ReaderMonitorUtils.logActionToReader(NovelReaderActionsRealTimeKey.NOVEL_READER_CLICK_BACK);
                        exitReader();
                        break;
                    case BBAReaderBarItemModel.HOME: { // 去书城
                        // 自动购买气泡显示的时候不响应
                        if ((mBBABubbleGuideController != null
                                && mBBABubbleGuideController.isAutoBugGuideShow())
                                || ReaderUtility.isFastDoubleClick()) {
                            return;
                        }
                        StatisticUtils.ubcMenuItemClick(StatisticsContants.UBC_SOURCE_NOVEL_HOME);
                        RouterProxy.goNovelChannel(FBReader.this);

                        String from = UBC_FROM_NOVEL;
                        if (isLocalBook()) {
                            from = StatisticsContants.UBC_FROM_NATIVE_NOVEL;
                        }
                        StatisticUtils.ubc753(from, StatisticsContants.UBC_TYPE_CLICK,
                                StatisticsContants.UBC_PAGE_READER_SETTING, "gofeedtab", "");
                        StatisticUtils.ubc5425Click(from,
                                StatisticsContants.UBC_PAGE_READER_SETTING,
                                StatisticsContants.UBC_SOURCE_GO_FEED_TAB,
                                "");
                        ReaderMonitorUtils.logActionToReader(
                                NovelReaderActionsRealTimeKey.NOVEL_READER_CLICK_BOOKSTORE);
                        break;
                    }
                    case BBAReaderBarItemModel.DOWNLOAD: { // 离线
                        ReaderManagerCallback callback = getReaderManagerCallback();
                        if (callback != null) {
                            callback.goToDownloadList(FBReader.this);
                            BBAThreadUtils.runOnUiThread(getDismissMenuRunnable(), 200);
                        }
                        ReaderMonitorUtils.logActionToReader(
                                NovelReaderActionsRealTimeKey.NOVEL_READER_CLICK_OFFLINE);
                        break;
                    }
                    case BBAReaderBarItemModel.BACKGROUND_COLOR: { // 背景颜色
                        mFromColorSettings = true;
                        // 设置菜单栏，点击不同颜色圆形时，会走到这里
                        if (StubToolsWrapperKt.Companion.isEnableLog()) {
                            Log.d(TAG, "背景色 BBAReaderBarItemModel.BACKGROUND_COLOR Changed：" + System.currentTimeMillis());
                        }
                        ReaderMonitorUtils.logActionToReader(
                                NovelReaderActionsRealTimeKey.NOVEL_SETTING_CLICK_BACKGROUND);

                        // 背景颜色
                        // 只有切换背景色时为夜间才同步关闭“跟随系统”
                        if (BBAModeChangeHelper.isNightMode()) {
                            NovelNightModeHelperWrapper.setFollowSysDarkModeState(false);
                        }
                        BBAMenuComponent.getInstance().modeChangeLight();
                        BBAReaderCoreApi.setFontColor(true, new Runnable() {
                            @Override
                            public void run() {
                                BBAThreadUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setRootBackground();
                                        if (mReaderContainer != null) {
                                            mReaderContainer.onBackColorChange();
                                        }
                                    }
                                }, MODE_CHANGE_DELAY);
                            }
                        });
                        Map<String, String> extMap = model.extMap;
                        if (extMap != null) {
                            String fromValue = extMap.get("fromValue");
                            String toValue = extMap.get("toValue");
                            if (!TextUtils.isEmpty(fromValue) && !TextUtils.isEmpty(toValue)) {
                                AllScenesStatisticUtils.ubc580ByReaderSetting(
                                        "bgcolor",
                                        AllScenesStatisticUtils.getBackgroundColorIndex(fromValue),
                                        AllScenesStatisticUtils.getBackgroundColorIndex(toValue));
                            }
                        }

                        // todo wang
                        if (BBAThemeResourceHelper.isNewThemeColorTest()) {
                            // 切换背景主题时，更新设置页面颜色
                            IBBABaseMenu settingMenu =
                                    BBAMenuComponent.getInstance().getMenuView(BBAMenuType.SETTING);
                            if (settingMenu != null) {
                                if (settingMenu instanceof BBASettingMenuView) {
                                    ((BBASettingMenuView) settingMenu).onUpdateView();
                                }
                            }
                            // 切换背景主题时，更新工具栏页面颜色
                            IBBABaseMenu mainMenu =
                                    BBAMenuComponent.getInstance().getMenuView(BBAMenuType.MAIN);
                            if (mainMenu != null) {
                                if (mainMenu instanceof BBAMainMenuView) {
                                    ((BBAMainMenuView) mainMenu).onUpdateView();
                                }
                            }
                            IBBABaseMenu dirMenu = BBAMenuComponent.getInstance()
                                    .getMenuView(BBAMenuType.DIRECTORY);
                            if (dirMenu != null) {
                                if (dirMenu instanceof BBADirectoryMenuView) {
                                    ((BBADirectoryMenuView) dirMenu).onUpdateView();
                                }
                            }
                            IBBABaseMenu moresettingMenu = BBAMenuComponent.getInstance()
                                    .getMenuView(BBAMenuType.MORE_SETTING);
                            if (moresettingMenu != null) {
                                if (moresettingMenu instanceof BBAMoreSettingMenuView) {
                                    ((BBAMoreSettingMenuView) moresettingMenu).onUpdateView();
                                }
                            }
                            IBBABaseMenu autoMenu = BBAMenuComponent.getInstance()
                                    .getMenuView(BBAMenuType.AUTO_SCROLL);
                            if (autoMenu != null) {
                                if (autoMenu instanceof BBAAutoScrollView) {
                                    ((BBAAutoScrollView) autoMenu).onUpdateView();
                                }
                            }
                            // 切换背景主题时，更新设置按钮
                            if (BBAMenuComponent.getInstance().getMenuController()
                                    instanceof BBAMenuController) {
                                BBAMenuController bbaMenuController =
                                        ((BBAMenuController) BBAMenuComponent.getInstance().
                                                getMenuController());
                                if (bbaMenuController != null) {
                                    bbaMenuController.updateSettingsButtonView();
                                }
                            }

//                            fontSettingModel
                        }

                        FBReaderApp app = (FBReaderApp) ReaderBaseApplication.Instance();
                        if (app != null) {
                            app.switchToDayMode(NovelAbTestManager.isOpenChangeReaderBgColorOptium());
                        }

                        ReaderManagerCallback readerManagerCallback = getReaderManagerCallback();
                        if (readerManagerCallback != null) {
                            readerManagerCallback.updateBannerView();
                        }
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                                UBC_PAGE_READER_SETTING, UBC_SOURCE_BACKGROUND_COLOR);
                        reportBackgroundColorUBC();
                        break;
                    }
                    case BBAReaderBarItemModel.NIGHT: // 日夜间
                        if (mUIHandler != null) {
                            mUIHandler.postDelayed(getDismissMenuRunnable(), 200);
                        }
                        boolean nightModeChanged = BBAModeChangeHelper.isNightMode();
                        changeCacheAdViewMode();
                        changeReaderNightMode(false, nightModeChanged);
                        changeBannerAdNightMode();
                        NovelNightModeHelperWrapper.setFollowSysDarkModeState(false);
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                                UBC_PAGE_READER_SETTING, UBC_SOURCE_DAY_NIGHT);
                        ReaderMonitorUtils.logActionToReader(NovelReaderActionsRealTimeKey.NOVEL_READER_CLICK_NIGHT);
                        break;
                    case BBAReaderBarItemModel.EYE_SHIELD: // 护眼模式
                        boolean eyeShield = BBAReaderConfig.getInstance().isEyeShield();
                        BBAReaderConfig.getInstance().setEyeShield(!eyeShield);
                        setRootEyeShield();
                        StatisticUtils.reportReaderEyeShieldModelClickUBC(isLocalBook());
                        if (model != null && model.redPoint != null) {
                            boolean isShow = SharedPreferenceUtils.get(FBReader.this, model.key,
                                    true);
                            if (!isShow) {
                                SharedPreferenceUtils.put(FBReader.this, model.key, false);
                                model.redPoint.dismissPoint();
                            }
                        }
                        ReaderMonitorUtils.logActionToReader(
                                NovelReaderActionsRealTimeKey.NOVEL_SETTING_CLICK_EYE_PROTECTION);
                        break;
                    case BBAReaderBarItemModel.MORE_SETTING_FEEDBACK_MESSAGE: // 意见反馈
                        if (INovelSearchboxFeedbackInterface.Impl.get() != null) {
                            INovelSearchboxFeedbackInterface.Impl.get().startToFeedbackFaqIntent(
                                    null, null, "", null);
                        }
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                                UBC_PAGE_READER_SETTING, UBC_SOURCE_FEEDBACK);
                        break;
                    case BBAReaderBarItemModel.VIP: // 主菜单顶部VIP:
                        StatisticUtils.ubc753(UBC_FROM_NOVEL, StatisticsContants.UBC_TYPE_CLICK,
                                StatisticsContants.UBC_PAGE_READER_SETTING,
                                StatisticsContants.UBC_SOURCE_VIP, "");
                        if (mBBAPayViewController != null) {
                            mBBAPayViewController.showVipChargeWindow();
                            ThreadUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    BBAMenuComponent.getInstance().dismissAllMenu();
                                }
                            }, 300);
                        }
                        ReaderMonitorUtils.logActionToReader(
                                NovelReaderActionsRealTimeKey.NOVEL_READER_CLICK_VIP);
                        break;
                    case BBAReaderBarItemModel.MORE: // 主菜单顶部"更多"
                        if (mBBABubbleGuideController != null) {
                            StatisticUtils.ubc753(UBC_FROM_NOVEL,
                                    StatisticsContants.UBC_TYPE_CLICK,
                                    StatisticsContants.UBC_PAGE_READER_SETTING, "more", "");
                            mBBABubbleGuideController.hideAutoBuyGuide();
                            mBBABubbleGuideController.hideTTSGuide();

                            if (NovelTtsInterface.getInstance().isOpenPlayer()) {
                                // 隐藏工具栏
                                NovelTtsInterface.getInstance().hideMainMenuView();
                                BBABook book = BBAReaderComponent.getInstance().getBook();
                                if (book != null && !book.isLocalBook()) {
                                    // 隐藏悬浮球
                                    NovelTtsInterface.getInstance().dissmisHover();
                                }
                            }
                        }
                        break;
                    case BBAReaderBarItemModel.COMMENT: // 评论
                        if (ReaderUtility.isFastDoubleClick()) {
                            return;
                        }
                        // 记录评论按钮点击的事件，当用户从评论页返回时，需要重新请求评论接口
                        if (myFBReaderApp != null) {
                            myFBReaderApp.setNeedReloadComment(true);
                            ReaderManagerCallback callback = getReaderManagerCallback();
                            if (callback != null) {
                                callback.showCommentView(myFBReaderApp.getCommentUrl());
                                BBAMenuComponent.getInstance().dismissAllMenu();
                            }
                        }
                        StatisticUtils.logUbcStatisticEvent(
                                StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                                StatisticsContants.UBC_PAGE_READER_SETTING,
                                StatisticsContants.UBC_SOURCE_COMMENT);
                        break;
                    case BBAReaderBarItemModel.DETAIL: // 简介
                        ReaderManagerCallback callback = getReaderManagerCallback();
                        if (callback != null) {
                            callback.onGotoNovelDetail(getBookInfo());
                            BBAMenuComponent.getInstance().dismissAllMenu();
                        }
                        StatisticUtils.ubc753(UBC_FROM_NOVEL, StatisticsContants.UBC_TYPE_CLICK,
                                StatisticsContants.UBC_PAGE_READER_SETTING, "more_intro", "");
                        break;
                    case BBAReaderBarItemModel.CATALOG_HEADER: // 目录顶部详情视图
                        if (!isLocalBook()) {
                            final ReaderManagerCallback cartlogCallback = getReaderManagerCallback();
                            if (cartlogCallback != null) {
                                cartlogCallback.onGotoNovelDetail(getBookInfo());
                                if (!NovelBusiAppAdapter.useExternalNightMode()) {
                                    cartlogCallback.onReaderThemeChanged(false, false);
                                }
                            }
                            StatisticUtils.ubc753(UBC_FROM_NOVEL, StatisticsContants.UBC_TYPE_CLICK,
                                    StatisticsContants.UBC_PAGE_CATALOG_BOOKMARK, "catalog_book_detail", "");
                            BBAMenuComponent.getInstance().dismissMenu();
                        }
                        break;
                    case BBAReaderBarItemModel.MARK: // 书签
                        if (mBBABookMarkController == null) {
                            mBBABookMarkController = new BBABookMarkController();
                        }
                        mBBABookMarkController.onClick(model);
                        ReaderMonitorUtils.logActionToReader(
                                NovelReaderActionsRealTimeKey.NOVEL_SETTING_CLICK_BOOKMARK);
                        break;
                    case BBAReaderBarItemModel.MORE_SETTING: // 更多设置按钮
                        if (model != null && model.redPoint != null) {
                            SharedPreferenceUtils.put(FBReader.this, model.key, false);
                            model.redPoint.dismissPoint();
                        }
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                                UBC_PAGE_READER_SETTING, UBC_PAGE_MORE_SETTING);
                        ReaderMonitorUtils.logActionToReader(
                                NovelReaderActionsRealTimeKey.NOVEL_SETTING_CLICK_MORE_SETTING);
                        break;
                    case CATALOG_PAGE: // 目录tab
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                                UBC_PAGE_CATALOG, UBC_SOURCE_CATALOG_TAB);
                        break;
                    case BOOKMARK_PAGE: // 标签tab
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                                UBC_PAGE_CATALOG, UBC_SOURCE_BOOKMARK_TAB);
                        break;
                    case BBAReaderBarItemModel.CATALOG_BOOKMARK: // 标签item:
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                                UBC_PAGE_READER_SETTING, UBC_PAGE_CATALOG);
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                                UBC_PAGE_CATALOG, UBC_SOURCE_BOOKMARK_ITEM);
                        ReaderMonitorUtils.logActionToReader(
                                NovelReaderActionsRealTimeKey.NOVEL_BOOKMARK_CLICK_BOOKMARK);
                        break;
                    case BBAReaderBarItemModel.CATALOG_ITEM: // 目录item
                        BBAReaderComponent.getInstance().adViewStop();
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                                UBC_PAGE_CATALOG, UBC_SOURCE_CATALOG_ITEM);

                        JSONObject jsonObject = BBAReaderComponent.getInstance().getWillToChapterInfo();
                        if (jsonObject != null && bbaBook != null) {
                            int willGotoChapterIndex = jsonObject.optInt(BBAReaderComponent.KEY_CHAPTER_INDEX);
                            BBABookChapter chapter = bbaBook.getCurrentChapter();
                            // 要去的章节和当前展示章节不同时，发送埋点通知
                            if (chapter != null && chapter.getIndex() != willGotoChapterIndex) {
                                BBASwitchChapterUBCEventTools.logSwitchChapterEvent();
                            }
                        }
                        ReaderMonitorUtils.logActionToReader(
                                NovelReaderActionsRealTimeKey.NOVEL_CATALOG_CLICK_CHAPTER);
                        break;
                    case BBAReaderBarItemModel.REVERSE_ORDER: // 倒叙
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                                UBC_PAGE_CATALOG, UBC_SOURCE_REVERSE_ORDER);
                        break;
                    case BBAReaderBarItemModel.CATALOG_ORDER: // 正叙
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                                UBC_PAGE_CATALOG, UBC_SOURCE_ORDER);
                        break;
                    case BBAReaderBarItemModel.CATALOG_SCROLL_BAR_DRAG: // 阅读器内目录滑块拖动打点
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                                UBC_PAGE_CATALOG, UBC_SOURCE_QUICK_FLIP);
                        break;
                    case BBAReaderBarItemModel.AUTO_FLIP: // 自动翻页:
                        BBAReaderComponent.getInstance().adViewStop();
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                                UBC_PAGE_READER_SETTING, UBC_SOURCE_AUTO_FLIP);
                        // 自动阅读保持常亮
                        acquireWakeLock();
                        ReaderMonitorUtils.logActionToReader(
                                NovelReaderActionsRealTimeKey.NOVEL_SETTING_CLICK_AUTO_FLIP);
                        break;
                    case BBAReaderBarItemModel.SETTINGS:
                        ReaderMonitorUtils.logActionToReader(
                                NovelReaderActionsRealTimeKey.NOVEL_READER_CLICK_SETTING);
                        break;
                    case BBAReaderBarItemModel.CATALOG:
                        StatisticUtils.ubc753(UBC_FROM_NOVEL,
                                StatisticsContants.UBC_TYPE_CLICK,
                                StatisticsContants.UBC_PAGE_READER_SETTING, UBC_PAGE_CATALOG, "");
                        ReaderMonitorUtils.logActionToReader(
                                NovelReaderActionsRealTimeKey.NOVEL_RADER_CLCIK_CATALOG);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onShowCallBack(String type, View view) {
                if (!TextUtils.equals(type, CATALOG_PAGE)
                        && !TextUtils.equals(type, BOOKMARK_PAGE)
                        && !TextUtils.equals(type, CONTENT_ERROR_PAGE)
                        && !TextUtils.equals(type, LITE_SETTING_MENU_PAGE_1)
                        && !TextUtils.equals(type, LITE_SETTING_MENU_PAGE_2)) {
                    exitFullScreenMode();
                }
                // 当菜单栏显示的时候，进行浮层广告的隐藏
                hideReaderPerformanceAdView();
                // 进行通知关闭浮层广告的请求
                ReaderUtility.setFloatAdCloseMessage(true);
                showingMenuNum++;
                ReaderManagerCallback managerCallback =
                        ReaderManager.getInstance(FBReader.this).getReaderManagerCallback();
                if (managerCallback != null) {
                    managerCallback.sendNotify(NOTIFY_ON_MENU_SHOW, null);
                }
                // 展示错误页时也会调用这里，导致展示底部导航，过滤掉
                if (!TextUtils.equals(type, CONTENT_ERROR_PAGE)) {
                    // 解决菜单show时statusBar颜色错误
                    int backgroundColor = BBAReaderConfig.getInstance().getBackgroundColor();
                    final boolean lightColor = BBAStatusBarUtil.isLightColor(backgroundColor);
                    mRootView.post(new Runnable() {
                        @Override
                        public void run() {
                            setStatus(BBAModeChangeHelper.isLightMode() && lightColor);
                        }
                    });
                }
                switch (type) {
                    case BBAMenuType.DIRECTORY:
                        // 当前是目录正在展示，通知不显示全局运营位的弹框
                        ReaderManagerCallback callback = ReaderManager.getInstance(FBReader.this)
                                .getReaderManagerCallback();
                        if (callback != null) {
                            callback.sendNotify(
                                    NOTIFY_SET_GLOBAL_OPERATION_VIEW_DIALOG_IS_SHOW, true);
                        }

                        directoryIsShowing = true;

                        /** 添加本地书的展示打点 */
                        String novelFrom = UBC_FROM_NOVEL;
                        if (isLocalBook()) {
                            novelFrom = StatisticsContants.UBC_FROM_NATIVE_NOVEL;
                        }
                        StatisticUtils.ubc753(novelFrom,
                                StatisticsContants.UBC_TYPE_SHOW,
                                StatisticsContants.UBC_PAGE_READER_SETTING, "menu_intro", "");
                        break;
                    case BBAMenuType.MAIN: // 主菜单
                        updateOfflineEntrance();
                        String from = isLocalBook() ? StatisticsContants.UBC_FROM_NATIVE_NOVEL :
                                UBC_FROM_NOVEL;
                        StatisticUtils.ubc753(from,
                                StatisticsContants.UBC_TYPE_SHOW,
                                UBC_PAGE_READER_SETTING,
                                StatisticsContants.UBC_SOURCE_MAIN_NAVIGATION,
                                "");
                        // 刷新屏幕底部"从本页读"view
                        updateReadCurrentMenu();

                        if (mBBABubbleGuideController != null) {
                            mBBABubbleGuideController.showTTSGuide(BBATTSViewController.getInstance().getFloatView());
                        }

                        BBAReaderBarItemModel settingItemModel =
                                ((BBAMainMenuView) view).getViewByType(BBAReaderBarItemModel.SETTINGS);
                        View settingView = settingItemModel != null ? settingItemModel.itemView : null;
                        if (mBBABubbleGuideController != null && mBBABubbleGuideController.isShowAutoBuyBubble()
                                && settingView != null) {
                            mBBABubbleGuideController.showAutoBuyGuide(settingView);
                            mBBABubbleGuideController.setShowAutoBuyBubble(false);
                        }
                        showDownloadButton();
                        BBAReaderBarItemModel itemModel =
                                BBAMenuComponent.getInstance().getViewByType(BBAReaderBarItemModel.SETTINGS);
                        if (itemModel != null && itemModel.redPoint != null) {
                            boolean isShow = ReaderUtility.isShowSettingDot();
                            if (isShow) {
                                itemModel.redPoint.showPoint();
                                itemModel.redPoint.setWidthAndHeight(6, 6);
                                itemModel.redPoint.setColor(ReaderUtility.getNovelResColor("NC1"));
                            }
                        }
                        break;
                    case BBAMenuType.MORE: // 更多菜单
                        // 评论model
                        BBAReaderBarItemModel bbaReaderBarItemModel =
                                BBAMenuComponent.getInstance().getViewByType(BBAReaderBarItemModel.COMMENT);
                        if (bbaReaderBarItemModel != null && bbaReaderBarItemModel.redPoint != null
                                && myFBReaderApp != null) {
                            bbaReaderBarItemModel.redPoint.setBadgeCount(myFBReaderApp.getCommentCount());
                        }
                        // 书签model
                        BBAReaderBarItemModel markItemModel =
                                BBAMenuComponent.getInstance().getViewByType(BBAReaderBarItemModel.MARK);
                        if (markItemModel != null) {
                            if (mBBABookMarkController == null) {
                                mBBABookMarkController = new BBABookMarkController();
                            }
                            BBAPage currentPage = null;
                            if (mReaderContainer != null) {
                                currentPage = mReaderContainer.getCurrentPage();
                            }
                            if (currentPage != null) {
                                mBBABookMarkController.updateView(markItemModel,
                                        !currentPage.isOnlyAd() && !currentPage.isTitlePage());
                            }
                        }
                        break;
                    case BBAMenuType.SETTING: // 设置菜单
                        if (view instanceof BBASettingMenuView) {
                            StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_SHOW,
                                    UBC_PAGE_READER_SETTING,
                                    UBC_SOURCE_SETTING_PANEL_SHOW);
                            // 由听书返回阅读器 更新自动翻页
                            BBAReaderBarItemModel autoFlipItemModel =
                                    ((BBASettingMenuView) view).getViewByType(AUTO_FLIP);
                            if (autoFlipItemModel != null) {
                                autoFlipItemModel.enabled = !NovelTtsInterface.getInstance().isPlaying();
                                autoFlipItemModel.updateMenuItemView();
                            }

                            if (mBBABubbleGuideController != null) {
                                // 处理更多设置按钮红点
                                BBAReaderBarItemModel moreSettingMode =
                                        ((BBASettingMenuView) view).getViewByType(BBAReaderBarItemModel.MORE_SETTING);
                                if (moreSettingMode != null && moreSettingMode.redPoint != null) {
                                    // 判断是否已经展示
                                    boolean isShow = SharedPreferenceUtils.get(FBReader.this, moreSettingMode.key,
                                            true);
                                    if (isShow) {
                                        moreSettingMode.redPoint.showPoint();
                                        moreSettingMode.redPoint.setWidthAndHeight(6, 6);
                                    } else {
                                        moreSettingMode.redPoint.dismissPoint();
                                    }
                                }
                            }
                        }
                        // 处理更多设置按钮红点
                        // 判断是否已经展示
                        BBAReaderBarItemModel model =
                                ((BBASettingMenuView) view).getViewByType(BBAReaderBarItemModel.EYE_SHIELD);
                        if (model != null && model.redPoint != null) {
                            boolean isShow = SharedPreferenceUtils.get(FBReader.this, model.key,
                                    true);
                            if (isShow) {
                                model.redPoint.showPoint();
                                model.redPoint.setWidthAndHeight(6, 6);
                                SharedPreferenceUtils.put(FBReader.this, model.key, false);
                            }
                        }
                        break;
                    case BBAMenuType.MORE_SETTING:
                        if (view instanceof BBAMoreSettingMenuView) {
                            itemModel = ((BBAMoreSettingMenuView) view)
                                    .getViewByType(BBAAutoBuyPageModel.MORE_SETTING_AUTO_BUY);
                            if (itemModel != null) {
                                ReaderManagerCallback readerManagerCallback = getReaderManagerCallback();
                                if (readerManagerCallback != null) {
                                    // 如果自动购买状态是开启状态则显示该项并打开开关，否则不显示该项
                                    itemModel.isVisible = readerManagerCallback.getMoreSettingAutoBuyStatus();
                                    ((BBAMoreSettingMenuView) view).updateItem(itemModel);
                                }
                            }

                            // 更新移动网络自动播放视频广告
                            BBAReaderBarItemModel videoAdAutoPlayModel = ((BBAMoreSettingMenuView) view).getViewByType(
                                    BBAVideoAdAutoPlayModel.MORE_SETTING_VIDEO_AD_AUTO_PLAY);
                            if (videoAdAutoPlayModel != null) {
                                videoAdAutoPlayModel.isVisible = ReaderUtility.getVideoAdAutoPlayTestOn(true);
                                ((BBAMoreSettingMenuView) view).updateItem(videoAdAutoPlayModel);
                            }
                            // 解决阅读器底部功能提示设置不正确问题
                            BBAReaderBarItemModel eduBottomBarModel = ((BBAMoreSettingMenuView) view).getViewByType(
                                    BBANoreSettingEduBottomBarModel.MORE_SETTING_EDU_BOTTOM_BAR);
                            if (eduBottomBarModel != null) {
                                eduBottomBarModel.setCheckBox =
                                        BBAReaderConfig.getInstance().isMoreSettingIsEduBottomBar();
                                ((BBAMoreSettingMenuView) view).updateItem(eduBottomBarModel);
                            }
                        }
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_SHOW,
                                UBC_PAGE_READER_SETTING, UBC_PAGE_MORE_SETTING);
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_SHOW,
                                UBC_PAGE_READER_SETTING, UBC_SOURCE_AUTO_BUY);
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_SHOW,
                                UBC_PAGE_READER_SETTING, SOURCE_AUTO_ADD_TO_SHELF);
                        break;
                    case CATALOG_PAGE:
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_SHOW,
                                UBC_PAGE_CATALOG, UBC_SOURCE_CATALOG_PAGE);
                        break;
                    case BOOKMARK_PAGE:
                        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_SHOW,
                                UBC_PAGE_CATALOG, UBC_SOURCE_BOOKMARK_PAGE);
                        break;
                    case BBAReaderBarItemModel.CATALOG_ERROR_PAGE: {
                        boolean isconnected = NovelGalaxyWrapper.isNetworkConnected(FBReader.this);
                        if (isconnected) {
                            String moudle = isLocalBook() ? NovelReaderAbnormalKey.MOUDLE_LOCALREADER
                                    : NovelReaderAbnormalKey.MOUDLE_LEGALREADER;
                            HashMap<String, String> info = new HashMap<>();
                            if (mBookInfo != null && !TextUtils.isEmpty(mBookInfo.getId())) {
                                info.put("gid", mBookInfo.getId());
                            }
                            ReaderMonitorUtils.logAbnormalReader(FBReader.this, moudle,
                                    NovelReaderAbnormalKey.NOVEL_READER_CATALOG_ERROR
                                    , NovelReaderAbnormalKey.NOVEL_LEVEL_ERROR, info);
                        }
                        break;
                    }
                    case BBAReaderBarItemModel.CONTENT_ERROR_PAGE: {
                        boolean isconnected = NovelGalaxyWrapper.isNetworkConnected(FBReader.this);
                        if (isconnected) {
                            String moudle = isLocalBook() ? NovelReaderAbnormalKey.MOUDLE_LOCALREADER
                                    : NovelReaderAbnormalKey.MOUDLE_LEGALREADER;
                            HashMap<String, String> info = new HashMap<>();
                            if (mBookInfo != null && !TextUtils.isEmpty(mBookInfo.getId())) {
                                info.put("gid", mBookInfo.getId());
                            }
                            ReaderMonitorUtils.logAbnormalReader(FBReader.this, moudle,
                                    NovelReaderAbnormalKey.NOVEL_READER_CHAPTER_ERROR
                                    , NovelReaderAbnormalKey.NOVEL_LEVEL_ERROR, info);
                        }
                        break;
                    }
                    case BBAReaderBarItemModel.TITLEPAGE_ERROR_PAGE: {
                        boolean isconnected = NovelGalaxyWrapper.isNetworkConnected(FBReader.this);
                        if (isconnected) {
                            HashMap<String, String> info = new HashMap<>();
                            if (mBookInfo != null && !TextUtils.isEmpty(mBookInfo.getId())) {
                                info.put("gid", mBookInfo.getId());
                            }
                            ReaderMonitorUtils.logAbnormalReader(FBReader.this
                                    , NovelReaderAbnormalKey.MOUDLE_LEGALREADER,
                                    NovelReaderAbnormalKey.NOVEL_READER_TITLEPAGE_ERROR
                                    , NovelReaderAbnormalKey.NOVEL_LEVEL_ERROR, info);
                        }
                        break;
                    }
                    case BBAReaderBarItemModel.BUYPAGE_ERROR_PAGE: {
                        boolean isconnected = NovelGalaxyWrapper.isNetworkConnected(FBReader.this);
                        if (isconnected) {
                            HashMap<String, String> info = new HashMap<>();
                            if (mBookInfo != null && !TextUtils.isEmpty(mBookInfo.getId())) {
                                info.put("gid", mBookInfo.getId());
                            }
                            ReaderMonitorUtils.logAbnormalReader(FBReader.this
                                    , NovelReaderAbnormalKey.MOUDLE_LEGALREADER,
                                    NovelReaderAbnormalKey.NOVEL_READER_PREVIEW_ERROR
                                    , NovelReaderAbnormalKey.NOVEL_LEVEL_ERROR, info);
                        }
                        break;
                    }

                }
                // 停止记录阅读时长
                BBAReaderDurationTools.getInstance().endReadDuration();
                hideJiliLadderView();
            }

            @Override
            public void onDismissCallBack(String type, View view) {
                if (TextUtils.equals(type, BBAMenuType.MAIN) || TextUtils.equals(type, BBAMenuType.MORE_SETTING)) {
                    enterFullScreenMode();
                }
                showingMenuNum--;
                ReaderManagerCallback managerCallback =
                        ReaderManager.getInstance(FBReader.this).getReaderManagerCallback();
                if (managerCallback != null) {
                    managerCallback.sendNotify(NOTIFY_ON_MENU_HIDE, null);
                }
                switch (type) {
                    case BBAMenuType.DIRECTORY:
                        // 当前目录已经隐藏，能够显示全局运营位的弹框
                        ReaderManagerCallback callback = ReaderManager.getInstance(FBReader.this)
                                .getReaderManagerCallback();
                        if (callback != null) {
                            callback.sendNotify(
                                    NOTIFY_SET_GLOBAL_OPERATION_VIEW_DIALOG_IS_SHOW, false);
                        }
                        directoryIsShowing = false;

                        /** 添加关闭目录半层打点 */
                        String novelFrom = UBC_FROM_NOVEL;
                        if (isLocalBook()) {
                            novelFrom = StatisticsContants.UBC_FROM_NATIVE_NOVEL;
                        }
                        StatisticUtils.ubc753(novelFrom,
                                StatisticsContants.UBC_TYPE_CLICK,
                                StatisticsContants.UBC_PAGE_READER_SETTING, "menu_intro_close", "");

                        break;
                    case BBAMenuType.MAIN:  // 主菜单
                        if (mBBABubbleGuideController != null) {
                            mBBABubbleGuideController.hideTTSGuide();
                            mBBABubbleGuideController.hideAutoBuyGuide();
                        }

                        // 刷新屏幕底部"从本页读"view
                        if (!NovelTtsInterface.getInstance().isOpeningPlayer()) {
                            updateReadCurrentMenu();
                        }

                        // 隐藏章节进度提示信息toast
                        BBAReaderProgressController.getInstance().hide();

                        NovelTtsInterface.getInstance().dissmisHover();
                        break;
                    case BBAMenuType.MORE_SETTING: // 更多设置菜单
                        // 展示引导浮层，扉页不展示
                        if (mBBAEduViewController != null && mBBAEduViewController.isNeedUpdateLoading()) {
                            mBBAEduViewController.showUserEduViewIfNeed(mRootView);
                        }

                        // 处理更多设置按钮红点
                        if (view instanceof BBAMoreSettingMenuView) {
                            BBAMoreSettingMenuView bbaMoreSettingMenuView = (BBAMoreSettingMenuView) view;
                            BBAReaderBarItemModel autoAddBookShelMode = bbaMoreSettingMenuView
                                    .getViewByType(BBAAutoAddBookshelfModel.MORE_SETTING_AUTO_ADD_BOOKSHEL);
                            if (autoAddBookShelMode != null && autoAddBookShelMode.hasRedPoint) {
                                SharedPreferenceUtils.put(FBReader.this, autoAddBookShelMode.key, false);
                                autoAddBookShelMode.hasRedPoint = false;
                                bbaMoreSettingMenuView.updateItem(autoAddBookShelMode);
                            }
                        }
                        break;
                    case BBAMenuType.SETTING: // 设置菜单
                        if (mBBABubbleGuideController != null) {
                            mBBABubbleGuideController.hideBrightnessGuide();
                        }
                        break;
                    default:
                        break;
                }
                // 开启记录阅读时长
                BBAReaderDurationTools.getInstance().startReadDuration(ReaderUtility.safeToLong(mBookInfo.getId()),
                        String.valueOf(mBookInfo.getType()), false);
            }
        });

        BBAMenuComponent.getInstance().addAutoScrollCallback(new IBBAAutoScrollCallback() {
            @Override
            public void onAutoScrollSpeedChange(String type, int index) {

            }

            @Override
            public void onExitAutoScroll(boolean isShowExitToast) {
                releaseWakeLock();
            }
        });
        // 检测更多设置项业务层功能项变化
        BBAMenuComponent.getInstance().addMoreSettingFunctionCallback(new IBBAMoreSettingFunctionCallback() {
            @Override
            public void onCloseScreenTime(String type, long value) {
                if (value == -1) {
                    if (bbaFunctionScreen != null) {
                        bbaFunctionScreen.setInScreenProtectedTime();
                    }
                    StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                            UBC_PAGE_READER_SETTING, UBC_SOURCE_LOCK_SCREEN);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!Settings.System.canWrite(BBAReaderRuntime.getAppContext())) {
                            ReaderManagerCallback callback = ReaderManager.getInstance(FBReader.this)
                                    .getReaderManagerCallback();
                            if (callback != null) {
                                callback.requestSettingPermission(FBReader.this, REQUEST_PERMISSION_SETTING);
                            }
                        }
                    }
                    if (bbaFunctionScreen != null) {
                        bbaFunctionScreen.setInScreenProtectedTime();
                    }
                    StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                            UBC_PAGE_READER_SETTING, UBC_SOURCE_LOCK_SCREEN);
                }
            }

            @Override
            public void onResetTime(String type, long value) {
                if (bbaFunctionRestRemind != null) {
                    bbaFunctionRestRemind.startRestTiming();
                }
                StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                        UBC_PAGE_READER_SETTING, UBC_SOURCE_REST_HINT);
            }

            @Override
            public void onAutoSwitchDayNight(String type, boolean isCheck, long day, long night) {
                setAutoDayAndNightService(isCheck);
                StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                        UBC_PAGE_READER_SETTING, UBC_SOURCE_AUTO_CHANGE_DAY_NIGHT);
            }

            @Override
            public void onChoiceChange(String type, String value) {
                if (BBAReaderBarItemModel.MORE_SETTING_PRE_LOAD.equals(type)) {
                    ReaderManagerCallback callback = getReaderManagerCallback();
                    if (myFBReaderApp != null && callback != null) {
                        myFBReaderApp.setPrefetchNumber(callback.isVipFreeBook(), Integer.parseInt(value));
                    }
                    StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                            UBC_PAGE_READER_SETTING, UBC_SOURCE_PRELOAD);
                }
            }

            @Override
            public void onCheckChange(String type, boolean value) {
                if (BBAAutoAddBookshelfModel.MORE_SETTING_AUTO_ADD_BOOKSHEL.equals(type)) { // 自动加书架
                    ReaderUtility.setLegalAutoAddShelfSwitch(value);
                    if (mBbaAutoAddBookShelfController != null) {
                        mBbaAutoAddBookShelfController.setAutoAddShelfOpenOrClose(value);
                    }
                    StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                            UBC_PAGE_READER_SETTING, SOURCE_AUTO_ADD_TO_SHELF);
                } else if (BBARightTopOperateModel.MORE_SETTING_RIGHT_TOP_OPERATE.equals(type)) { // 右上角运营位
                    SharedPreferenceUtils.put(FBReader.this,
                            BBARightTopOperateModel.SP_KEY_RIGHT_TOP_OPERATE, value);
                    onUpdateRTOperationView("");
                    StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                            UBC_PAGE_READER_SETTING, value ? SOURCE_READER_SIGN_OPEN : SOURCE_READER_SIGN_CLOSE);
                } else if (BBAAutoBuyPageModel.MORE_SETTING_AUTO_BUY.equals(type)) { // 自动购买
                    ReaderManagerCallback readerManagerCallback = getReaderManagerCallback();
                    if (readerManagerCallback != null) {
                        readerManagerCallback.setMoreSettingAutoBuyStatus(value);
                    }
                    StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                            UBC_PAGE_READER_SETTING, UBC_SOURCE_AUTO_BUY);
                } else if (BBAVideoAdAutoPlayModel.MORE_SETTING_VIDEO_AD_AUTO_PLAY.equals(type)) {
                    ReaderUtility.setVideoAdAutoPlayStatus(true, value);
                }
            }

            @Override
            public void onTimerChange(String type, boolean isCheck, long day, long night) {

            }
        });

        // 检测更多设置项阅读器功能项变化
        BBAMenuComponent.getInstance().addMoreSettingReaderCallback(new IBBAMoreSettingReaderCallback() {
            @Override
            public void onSpaceChange(String type, int fromValue, int value) {
                // 版间样式
                BBAReaderCoreApi.setSpacingType(value, true);
                AllScenesStatisticUtils.ubc580ByReaderSetting(
                        "linespace", AllScenesStatisticUtils.getLineSpcae(fromValue),
                        AllScenesStatisticUtils.getLineSpcae(value));
                StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                        UBC_PAGE_READER_SETTING, UBC_SOURCE_LINE_SPACE);
            }

            @Override
            public void onTurnPageType(String type, int value) {
                if (mBBAEduViewController != null) {
                    mBBAEduViewController.setNeedUpdateLoading(true);
                }
                // 翻页模式发生变化需要更新右上角运营位
                if (BBAReaderConfig.getInstance().getTurnPageType() == BBADefaultConfig.VERTICAL) {
                    ReaderManagerCallback callback = getReaderManagerCallback();
                    if (callback != null) {
                        callback.changeScrollDirection();
                    }

                    updateRightTopOperateView(false);
                    if (mBannerAnimHelper != null) {
                        mBannerAnimHelper.resetBannerLayout();
                    }
                }
                reportTurnPageUbc();
                ReaderMonitorUtils.logActionToReader(
                        NovelReaderActionsRealTimeKey.NOVEL_SETTING_CHANGE_SCROLL_STYLE);

                ReaderManagerCallback managerCallback =
                        ReaderManager.getInstance(FBReader.this).getReaderManagerCallback();
                if (managerCallback != null) {
                    managerCallback.sendNotify(NOTIFY_ON_TURN_PAGE_TYPE, null);
                }
            }

            @Override
            public void onVoiceTurnPage(String type, boolean isCheck) {
                StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                        UBC_PAGE_READER_SETTING, UBC_SOURCE_VOLUME_FLIP);
            }

            @Override
            public void onFullScreenTurnPage(String type, boolean isCheck) {
                if (mBBAEduViewController != null) {
                    mBBAEduViewController.setNeedUpdateLoading(true);
                }
                StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                        UBC_PAGE_READER_SETTING, UBC_SOURCE_FULL_SCREEN_FLIP);
            }

            @Override
            public void onPreLoadNum(String type, int value) {
                StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                        UBC_PAGE_READER_SETTING, UBC_SOURCE_REST_HINT);
            }

            @Override
            public void onEduBottomBar(String type, boolean isCheck) {

            }
        });
        // 添加自动加书架设置项
        if (!isFromWangpanTxt) {
            BBAReaderBarItemModel autoAddBookshelfModel = new BBAAutoAddBookshelfModel();
            BBAMenuComponent.getInstance().addMoreSettingItem(4, autoAddBookshelfModel);
            BBAVideoAdAutoPlayModel bbaAdVideoPlayModel = new BBAVideoAdAutoPlayModel();
            BBAMenuComponent.getInstance().addMoreSettingItem(8, bbaAdVideoPlayModel);
        } else {
            BBAMenuComponent.getInstance().removeMoreSettingItem(9);
        }
        if (bbaBook != null && bbaBook.getReadType() != BBABook.LOCAL_TXT_BOOK) {
            // 添加右上角运营位设置项
            BBAReaderBarItemModel rightTopOperateModel = new BBARightTopOperateModel();
            // 添加自动购买设置项
            BBAAutoBuyPageModel bbaAutoBuyPageModel = new BBAAutoBuyPageModel();
            BBAMenuComponent.getInstance().addMoreSettingItem(9, rightTopOperateModel);
            BBAMenuComponent.getInstance().addMoreSettingItem(10, bbaAutoBuyPageModel);
        }

        BBAMenuComponent.getInstance().setTTSFooterView(null);
        bbaTTSCenterModel = new BBATTSCenterModel(isFromWangpanTxt);
        BBAMenuComponent.getInstance().setCenterShelfView(bbaTTSCenterModel);
        BBAMenuTTSModel bbaMenuTTSModel = new BBAMenuTTSModel();
        BBAMenuComponent.getInstance().setTTSFooterView(bbaMenuTTSModel);
        fontSettingModel = new BBAFontSettingModel();
        BBAMenuComponent.getInstance().replaceFontSetting(fontSettingModel);
    }

    /**
     * 创建监听对象：container宽高变化
     */
    private ViewTreeObserver.OnGlobalLayoutListener createContainerGlobalListener() {
        if (mContainerGlobalLayoutListener == null) {
            mContainerGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mReaderContainer != null) {
                        int rH = mReaderContainer.getHeight();
                        int rW = mReaderContainer.getWidth();
                        int preH = mReaderContainerHeight;
                        int preW = mReaderContainerWidth;
                        if (mReaderContainerHeight != rH || mReaderContainerWidth != rW) {
                            mReaderContainerHeight = rH;
                            mReaderContainerWidth = rW;

                            // 适配 pad & 折叠屏 横竖屏切换
                            // 当高度变化 && 时屏幕模式发生变化
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                if (FBReader.this.isInMultiWindowMode() != mIsMultiScreenMode){
                                    mIsMultiScreenMode = FBReader.this.isInMultiWindowMode();
                                }
                            }
                            if (BBALogUtil.isDebug()) {
                                BBALogUtil.d("xyl onGlobalLayout mReaderContainerHeight= " +
                                        mReaderContainerHeight + " mReaderContainerWidth= " +
                                        mReaderContainerWidth +
                                        " & rh = " + rH + " & rW = " + rW +
                                        " & mIsMultiScreenMode = " + mIsMultiScreenMode);
                            }
                            if (BBAAdUtil.isMultiScreenMode() != mIsMultiScreenMode
                                    || HostDeviceUtilsWrapper.isTableOrFoldExpandState()) {
                                // 宽高发生变化，需要重新排版
                                if (BBALogUtil.isDebug()) {
                                    BBALogUtil.d("xyl onGlobalLayout mReaderContainerHeight= " +
                                            mReaderContainerHeight +
                                            " & rh = " + rH +
                                            " & mIsMultiScreenMode = " + mIsMultiScreenMode);
                                }
                                BBAAdUtil.setIsMultiScreenMode(
                                        mIsMultiScreenMode,
                                        mReaderContainerHeight,
                                        mReaderContainerWidth,
                                        getContentHeight());

//                                BBAReaderCoreApi.containerSizeChange(
//                                        mReaderContainerWidth,
//                                        mReaderContainerHeight);
                                BBAReaderBitmapManager.getInstance().setReaderContainer(
                                        mReaderContainer,
                                        mReaderContainerWidth, mReaderContainerHeight);
                                if (mReaderContainer != null
                                        && mReaderContainer.getAdapter() != null) {
                                    // 第一次进入不需要记录进度，默认是有进度的，在次记录会覆盖上次的进度
                                    if (preW > 0 && preH > 0) {
                                        mReaderContainer.getAdapter().recordCurrentValidProcess();
                                    }
                                }
                            }
                            // 屏幕宽度改变，更新是否显示插屏广告相关状态
                            BBAReaderConfig.getInstance().setNeedShowAdByContainerHeight(mReaderContainerHeight, mIsMultiScreenMode);

                            BBADeviceUtil.ScreenInfo.setMultiScreenMode(mIsMultiScreenMode);
                            BBADeviceUtil.ScreenInfo.setMultiScreenModeHeight(mReaderContainerHeight);
                            BBADeviceUtil.ScreenInfo.setMultiScreenModeWidth(mReaderContainerWidth);
                            BBAAdUtil.setIsMultiScreenMode(
                                    mIsMultiScreenMode,
                                    mReaderContainerHeight,
                                    mReaderContainerWidth,
                                    getContentHeight());

                            BBAProgressUtil.saveChapterIndexBySize(preW, preH);
                            BBALogUtil.d("xyl --- resetReaderEngine ： " + rH + " & " + rW + " ---- " + preH + " & " + preW);
                            if (preW > 0 && preH > 0) {
                                resetReaderEngine();
                            } else {
                                BBAReaderCoreApi.clearEnginePagesCache(); // 清除引擎缓存
                                BBAReaderCoreApi.clearEngine(); // 清除引擎
                                BBAReaderBitmapManager.getInstance().clear();
                                BBAReaderCoreApi.recreateEngine();
                            }
                        }
                    }
                }
            };
        }
        return mContainerGlobalLayoutListener;
    }

    private int getContentHeight() {
        if (mRootView != null) {
            return mRootView.getHeight();
        }
        return 0;
    }

    private void refreshFontFamily() {
        if (bbaBook == null) {
            return;
        }
        final ReaderManagerCallback callback = ReaderManager.getInstance(getActivity())
                .getReaderManagerCallback();
        if (callback != null) {
            boolean loadingSwitch = BBAReaderComponent.getInstance().getSwitch(
                    BBAABTestConstants.NOVEL_READER_LOADING_SWITCH, false);
            if (loadingSwitch) {
                BBAThreadUtils.runOnAsyncThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.sendNotify(NovelReaderCallbackNotifyType.NOTIFY_START_READER_FONT_UBC, "");
                    }
                });
            } else {
                callback.sendNotify(NovelReaderCallbackNotifyType.NOTIFY_START_READER_FONT_UBC, "");
            }
        }
        // 字体加载需要判断非txt书，而book信息在onstart里初始化
        if (bbaBook.isLocalBook()) {
            BBAFontController.setFontFamily(FONT_DEFAULT);
            return;
        }
        if (hasLoadFont) {
            return;
        }
        hasLoadFont = true;
        if (callback != null) {
            String customFont = (String) callback.getData(NovelReaderCallbackDataType.GET_LAST_FONT, null);
            if (!TextUtils.isEmpty(customFont)) {
                try {
                    JSONObject jsonObject = new JSONObject(customFont);
                    String name = jsonObject.optString("name");
                    String src = jsonObject.optString("src");
                    if (AndroidFontUtil.addFont(name, new File(src))) {
                        // 字体添加成功后才能设置
                        if (TextUtils.isEmpty(name)) {
                            name = "DEFAULT";
                        }
                        BBAFontController.setFontFamily(name);
                    } else {
                        callback.sendNotify(NovelReaderCallbackNotifyType.NOTIFY_RESET_AND_DELETE_CURRENT_FONT,
                                null);
                        FBReaderApp readerApp = ReaderUtility.getFBReaderApp();
                        readerApp.setFontFamily(FONT_DEFAULT);
                        ReaderUtility.toast(getString(R.string.bdreader_font_load_fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initFontController() {
        BBAFontController.setListener(new BBAReaderFontEventListener());
    }

    public static class BBAReaderFontEventListener implements IBBAReaderFontEventListener {

        @Override
        public Typeface onLoadFont(String fontName) {
            return AndroidFontUtil.typeface(fontName, false, false);
        }
    }

    /**
     * 更新缓存的广告View日夜间
     */
    private void changeCacheAdViewMode() {
        if (mReaderContainer != null && mReaderContainer.getAdapter() != null) {
            Map<String, View> allAdView = BBAAdViewController.getInstance().getAllAdView();
            if (allAdView != null) {
                Set<Map.Entry<String, View>> entries = allAdView.entrySet();
                for (Map.Entry<String, View> map : entries) {
                    if (map == null) {
                        continue;
                    }
                    View view = map.getValue();
                    if (view != null) {
                        ReaderManagerCallback readerManagerCallback = getReaderManagerCallback();
                        if (readerManagerCallback != null) {
                            readerManagerCallback.changeViewNightMode(view, BBAModeChangeHelper.NONE);
                        }
                    }
                }
            }
        }
    }

    /**
     * 更新底部广告banner日夜间状态
     */
    private void changeBannerAdNightMode() {
        if (this.mBannerAdLayout != null) {
            int childCount = this.mBannerAdLayout.getChildCount();
            if (childCount > 0) {
                View view = this.mBannerAdLayout.getChildAt(0);
                ReaderManagerCallback readerManagerCallback = getReaderManagerCallback();
                if (readerManagerCallback != null) {
                    readerManagerCallback.updateBannerView(view);
                }
            }
        }
    }

    private void registerLoginCallback() {
        ReaderManagerCallback readerManagerCallback = getReaderManagerCallback();
        if (readerManagerCallback != null) {
            readerManagerCallback.registerLoginEvent(this, this);
        }
    }

    private void unregisterLoginCallback() {
        ReaderManagerCallback readerManagerCallback = getReaderManagerCallback();
        if (readerManagerCallback != null) {
            readerManagerCallback.unregisterLoginEvent(this);
        }
    }

    /**
     * 注册展示阶梯激励view的监听，回调在ui线程
     */
    private void registerJiLiLadderCallback() {
        ReaderManagerCallback readerManagerCallback = getReaderManagerCallback();
        if (readerManagerCallback != null) {
            readerManagerCallback.registerJiLiLadderEvent(this, new IJiLiLadderCallBack() {
                @Override
                public void showJiLiLadderView() {
                    if (mJiLiLadderView == null) {
                        showJiliLadderView();
                    }
                }
            });
        }
    }

    private void unregisterJiLiLadderCallback() {
        ReaderManagerCallback readerManagerCallback = getReaderManagerCallback();
        if (readerManagerCallback != null) {
            readerManagerCallback.unregisterJiLiLadderEvent(this);
        }
    }

    /**
     * 展示框架外的激励阶梯的view
     */
    private void showJiliLadderView() {
        BBAMenuComponent.getInstance().dismissAllMenu();
        ReaderManagerCallback callback = getReaderManagerCallback();
        if (callback == null) {
            return;
        }
        mJiLiLadderView = callback.getView(NovelReaderCallbackViewType
                .GET_JILI_LADDER_ADVIEW, "");
        if (mJiLiLadderView == null) {
            return;
        }

        if (mRootView != null) {
            mRootView.post(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams jiliLadderLp = new RelativeLayout
                            .LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                    jiliLadderLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    jiliLadderLp.leftMargin = getResources().getDimensionPixelSize(R.dimen.novel_dimens_21_5dp);
                    jiliLadderLp.bottomMargin = getMarginBottomForJiliLadder();

                    BBAUIUtil.safeAddView(mRootView, mJiLiLadderView, jiliLadderLp);
                }
            });
        }
        // 7s后隐藏阶梯激励view
        if (mUIHandler != null) {
            mUIHandler.postDelayed(hideJiliLadderRunnable, 7000);
        }
    }

    /**
     * 获取阶梯激励视频提示距离底部的距离
     *
     * @return
     */
    private int getMarginBottomForJiliLadder() {
        IBBABaseMenu ibbaBaseMenu = BBAMenuComponent.getInstance().getMenuView(BBAMenuType.MAIN);
        if (ibbaBaseMenu instanceof BBAMainMenuView) {
            View menuFooter = ((BBAMainMenuView) ibbaBaseMenu).getMainMenuFooter();
            if (menuFooter != null && menuFooter.getMeasuredHeight() > 0) {
                return menuFooter.getMeasuredHeight()
                        + BBAResourceHelper.getResources().getDimensionPixelOffset(R.dimen.dimen_21_5dp);
            }
        }

        return getResources().getDimensionPixelSize(R.dimen.novel_dimens_162dp);
    }

    private final Runnable hideJiliLadderRunnable = new Runnable() {
        @Override
        public void run() {
            hideJiliLadderView();
        }
    };

    /**
     * 隐藏阶梯激励的view
     */
    private void hideJiliLadderView() {
        if (mUIHandler != null) {
            mUIHandler.removeCallbacks(hideJiliLadderRunnable);
        }
        if (mJiLiLadderView == null) {
            return;
        }
        mJiLiLadderView.setVisibility(GONE);
        if (mRootView != null) {
            mRootView.removeView(mJiLiLadderView);
        }
        mJiLiLadderView = null;
    }

    private void reportTurnPageUbc() {
        String source = "";
        int turnPageType = BBAReaderConfig.getInstance().getTurnPageType();
        // 翻页方式的点击统计
        switch (turnPageType) {
            case BBADefaultConfig.HORIZONTAL_NONE:
                source = UBC_VALUE_SOURCE_FLIP_NONE;
                break;
            case BBADefaultConfig.HORIZONTAL_CURL:
                source = UBC_VALUE_SOURCE_FLIP_SIMULATE;
                break;
            case BBADefaultConfig.HORIZONTAL_TRANSLATE:
                source = UBC_VALUE_SOURCE_FLIP_HTRANSITION;
                break;
            case BBADefaultConfig.VERTICAL:
                source = UBC_VALUE_SOURCE_FLIP_VSCROLL;
                break;
            default:
                source = UBC_VALUE_SOURCE_FLIP_NONE;
                break;
        }
        StatisticUtils.logUbcStatisticEvent(StatisticEvent.UBC_EVENT_TOOLBAR_MENU_CLICK,
                UBC_PAGE_READER_SETTING, source);
    }

    /**
     * 避免使用匿名非静态内部类 导致的内存泄露问题
     */
    private static final INovelDownloadQueryCallBack INovelDownloadQueryCallBack = new INovelDownloadQueryCallBack() {
        @Override
        public void queryIsDownloadAllForBookId(String s, boolean b) {
            BBABook book = BBAReaderComponent.getInstance().getBook();
            if (book == null) {
                return;
            }
            if (TextUtils.isEmpty(s)
                    || TextUtils.isEmpty(book.getBookId())
                    || (!s.equalsIgnoreCase(book.getBookId()))) {
                StatisticUtils.ubc753(UBC_FROM_NOVEL,
                        StatisticsContants.UBC_TYPE_SHOW,
                        StatisticsContants.UBC_PAGE_READER_SETTING,
                        "multi_download_0",
                        "");
                return;
            }
            StatisticUtils.ubc753(UBC_FROM_NOVEL,
                    StatisticsContants.UBC_TYPE_SHOW,
                    StatisticsContants.UBC_PAGE_READER_SETTING,
                    b ? "multi_download_1" : "multi_download_0",
                    "");
        }
    };

    private void showDownloadButton() {
        ReaderManagerCallback callback = getReaderManagerCallback();
        if (callback == null) {
            return;
        }
        BBABook book = BBAReaderComponent.getInstance().getBook();
        if (book != null) {
            callback.isDownloadAllForBookId(book.getBookId(), INovelDownloadQueryCallBack);
        }
    }

    /**
     * 更新下载按钮
     */
    public void updateOfflineEntrance() {
        BBAThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BBAReaderBarItemModel itemModel =
                        BBAMenuComponent.getInstance().getViewByType(BBAReaderBarItemModel.DOWNLOAD);
                if (itemModel != null) {
                    ReaderManagerCallback callback = getReaderManagerCallback();
                    final BookInfo bookInfo = getBookInfo();
                    if (callback == null || bookInfo == null) {
                        return;
                    }
                    boolean disableOffline = callback.disableFooterMenu(ReaderManager.FOOTER_MENU_OFFLINE, bookInfo);
                    itemModel.enabled = !disableOffline;

                    if (isDownloadedAll) {
                        itemModel.normalImgName = BBA_MENU_DOWNLOAD_ALL;
                    } else if (disableOffline) {
                        itemModel.normalImgName = BBA_MENU_DOWNLOAD_DISABLE;
                        itemModel.titleColor = BBA_MENU_DOWNLOAD_DISABLE;
                    } else {
                        itemModel.titleColor = BBA_MENU_ITEM_TEXT_COLOR;
                        itemModel.normalImgName = BBA_MENU_DOWNLOAD;
                    }
                    itemModel.updateMenuItemView();

                    if (callback != null) {
                        callback.isAllChapterDownloaded(bbaBook != null ? bbaBook.getBookId() : null,
                                new INovelDownloadQueryCallBack() {
                                    @Override
                                    public void queryIsDownloadAllForBookId(String bookId,
                                                                            boolean isAllDownloaded) {
                                        isDownloadedAll = isAllDownloaded;
                                        if (isAllDownloaded) {
                                            BBAThreadUtils.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    BBAReaderBarItemModel itemModel =
                                                            BBAMenuComponent.getInstance().
                                                                    getViewByType(BBAReaderBarItemModel.DOWNLOAD);
                                                    if (itemModel != null) {
                                                        itemModel.normalImgName = BBA_MENU_DOWNLOAD_ALL;
                                                        itemModel.updateMenuItemView();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    /**
     * 获取底部banner真是高度
     *
     * @return
     */
    public int getBannerLayoutHeightReal() {
        return getResources().getDimensionPixelOffset(R.dimen.dimen_54dp);
    }

    /**
     * 获取底部banner高度
     *
     * @return
     */
    private int getBannerLayoutHeight() {
        BBABook book = BBAReaderComponent.getInstance().getBook();
        // 如果打开的书是本地书、劫持书、转码书全部都为0
        if (book == null || book.isLocalBook() || book.isPirated() || book.isTransCodeBook()
                || BBAAdUtil.isCurrentChapterAdHide()) {
            return 0;
        }
        // 只有当需要展示广告但是还未展示广告的时候才需要将底部banner的高度返回
        // 例：当前在正版书的支付页和正文页之间，滑到支付页会隐藏底部banner，再滑到正文会显示底部banner，这个时候不能重置版心高度
        if (BBAAdUtil.isAdShowState()
                && mBannerAdLayout != null
                && mBannerAdLayout.getVisibility() != View.VISIBLE) {
            return getBannerLayoutHeightReal();
        }
        return 0;
    }

    /**
     * 退出阅读器
     */
    private void exitReader() {
        // 退出阅读器，清除自动翻页标志位
        BBAAutoScrollHelper.getInstance().clearAutoScrollSigns();
        FBReaderApp app = (FBReaderApp) ReaderBaseApplication.Instance();
        saveReadProgress();
        if (app != null) {
            app.closeWindow();
        }
    }

    /**
     * 切换阅读器日夜间模式
     *
     * @param nightModeChanged
     */
    private void changeReaderNightMode(boolean isCreate, boolean nightModeChanged) {
        ReaderManagerCallback callback = ReaderUtility.getReaderManagerCallback();
        if (callback != null) {
            callback.onReaderThemeChanged(BBAModeChangeHelper.isNightMode());
            callback.saveNightMode(BBAModeChangeHelper.isNightMode());
        }
        FBReaderApp app = (FBReaderApp) ReaderBaseApplication.Instance();
        if (app != null) {
            if (nightModeChanged) {
                app.switchToNightMode();
            } else {
                app.switchToDayMode();
            }
        }
    }

    /**
     * 从外部获取正版阅读器当前字体
     */
    private void getHostFontFamily() {

    }

    /**
     * 日夜间变化
     */
    public void onDayNightChange(final boolean isFromReaderBgChange) {
        // Lite5.10~

        // 这里必须在switchToDayMode之后执行，因为要先改变宿主日夜间模式
        BBAReaderCoreApi.setFontColor(true, new Runnable() {
            @Override
            public void run() {
                BBAThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mReaderContainer != null) {
                            mReaderContainer.onModeChanged();
                        }
                        if (mReaderContainer != null) {
                            mReaderContainer.onBackColorChange(isFromReaderBgChange);
                        }

                        setRootBackground();

                        boolean nightModeChanged = BBAModeChangeHelper.isNightMode();
                        int currentMode = nightModeChanged ? BBAModeChangeHelper.NIGHT : BBAModeChangeHelper.LIGHT;
                        IBBABaseMenu ibbaBaseMenu = BBAMenuComponent.getInstance().getMenuView(BBAMenuType.MAIN);
                        if (ibbaBaseMenu instanceof BBAMainMenuView) {
                            int preMode = ((BBAMainMenuView) ibbaBaseMenu).getCurrentNightMode();
                            if (preMode != currentMode) {
                                ((BBAMainMenuView) ibbaBaseMenu).onModeChanged(currentMode);
                                changeTopStatusBarBgColorAndBottomNavigation(true);

                            }
                        }
                        if (BBAThemeResourceHelper.isNewThemeColorTest()) {
                            // 解决切换日夜间，更新不同步问题
                            IBBABaseMenu settingMenu =
                                    BBAMenuComponent.getInstance().getMenuView(BBAMenuType.SETTING);
                            if (settingMenu != null) {
                                if (settingMenu instanceof BBASettingMenuView) {
                                    ((BBASettingMenuView) settingMenu).onModeChangedView();
                                }
                            }
                        }
                        if (mReaderContainer != null) {
                            mReaderContainer.onBackColorChange();
                        }
                        if (mReaderContainer != null) {
                            mReaderContainer.onModeChanged();
                        }
                        setRootBackground();
                        if (mNavigatorPaddingView != null) {
                            mNavigatorPaddingView.setBackgroundColor(BBAResourceHelper
                                    .getColorTranslate(BBAModeTranslate.Color.BBA_MENU_MAIN_HEADER_MENU_COLOR));
                        }
                        if (!mRestHandler.hasMessages(MSG_UPDATE_READ_CURRENT_PAGE)) {
                            mRestHandler.sendEmptyMessageDelayed(MSG_UPDATE_READ_CURRENT_PAGE,
                                    AnimationFactory.REAL_DURATION_1);
                        }
                    }
                }, isFromReaderBgChange ? 0 : MODE_CHANGE_DELAY);
            }
        });
    }

    private void initChapterTask() {
        if (loadChapterTask == null) {
            loadChapterTask = new LoadChapterTask();
        }
        BBAReaderCoreApi.setLoadChapterTask(loadChapterTask);
    }

    public static class LoadChapterTask implements IBBALoadChapterTask {

        @Override
        public void loadChapter(int fileIndex, String data) {
            FBReaderApp.Instance().loadChapterInfo(fileIndex);
        }

        @Override
        public void beforeLoadChapter() {

        }
    }

    /**
     * 初始化自定义菜单项
     */
    private void initCustomMenu() {
        BBAMenuComponent.getInstance().setResoureceProvider(
                new BBATempResourceProviderImpl(this.getApplicationContext()));
        if (bbaMainCenterModel == null) {
            bbaMainCenterModel = new BBAMainCenterModel(isFromWangpanTxt);
            BBAMenuComponent.getInstance().setMainCenterContent(bbaMainCenterModel);
        }
        // 当气泡的返回按钮点击时，触发的回调，用于返回相应的章节
        if (bbaMainCenterModel != null) {
            bbaMainCenterModel.addOnBubbleBackListener(this,
                    new SeekbarBubbleView.OnBubbleBackListener() {
                        @Override
                        public void notifyBackChapter(int chapter) {
                            // 更新主菜单底部章进度
                            if (BBAMenuComponent.getInstance().isMenuShowing()) {
                                final IBBABaseMenu ibbaBaseMenu =
                                        BBAMenuComponent.getInstance().getMenuView(BBAMenuType.MAIN);
                                if (ibbaBaseMenu instanceof BBAMainMenuView) {
                                    ((BBAMainMenuView) ibbaBaseMenu).setProgress(chapter);
                                    ThreadUtils.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((BBAMainMenuView) ibbaBaseMenu).updateFooterButton();
                                        }
                                    }, 100);
                                    ((BBAMainMenuView) ibbaBaseMenu).setMarkProgress(chapter, false);
                                }
                                // 阅读器进度条滑动切章支持返回上一步需求打点：切章拖动toast-进度撤回btn点击
                                StatisticUtils.ubc753(isLocalBook() ? UBC_FROM_NATIVE_NOVEL : UBC_FROM_NOVEL,
                                        StatisticsContants.UBC_TYPE_CLICK,
                                        StatisticsContants.UBC_PAGE_READER_SETTING,
                                        StatisticsContants.UBC_SOURCE_PROGRESS_BACK,
                                        getActionFrom());
                            }
                            BBAJumpChapterUtil.jumpToChapter(chapter);
                            BBAReaderProgressController.getInstance().runHideToast();
                        }
                    });
        }


        // 向菜单中添加开通VIP项
        if (!NovelBusiReaderAdapter.hideVipEnter() && mBookInfo != null
                && mBookInfo.getType() != BookInfo.TYPE_LOCAL_TXT) {
            if (bbaMenuVipModel == null) {
                bbaMenuVipModel = new BBAMenuVipModel();
                BBAMenuComponent.getInstance().addMainHeaderRight(0, bbaMenuVipModel);
            }
            ReaderManagerCallback callback = getReaderManagerCallback();
            if (callback != null) {
                if (callback.isVip()) {
                    bbaMenuVipModel.normalImgName = BBATempResourceProviderImpl.DrawableType.BBA_MENU_VIP;
                }
            }
        }

        if (mBookInfo != null && mBookInfo.getType() != BookInfo.TYPE_LOCAL_TXT) {
            // 向菜单中添加评论项
            if (!NovelBusiReaderAdapter.hideMenuCommentEntrance()) {
                BBAMenuCommentModel commentModel = new BBAMenuCommentModel();
                BBAMenuComponent.getInstance().addMoreFooter(1, commentModel);
            }

            // 向菜单中添加打赏项
            ReaderManagerCallback callback = getReaderManagerCallback();
            if (callback != null) {
                if (!NovelBusiReaderAdapter.hideTopDashangEntrance()) {
                    int positionIndex = !NovelBusiReaderAdapter.hideVipEnter() ? 1 : 0;
                    if (bbaRewardModel == null) {
                        bbaRewardModel = new BBARewardModel();
                        BBAMenuComponent.getInstance().addMainHeaderRight(positionIndex, bbaRewardModel);
                        // 自定义lottie view自己处理点击事件
                        bbaRewardModel.setOnclickListener(new BBARewardModel.OnRewardClickListener() {
                            @Override
                            public void onClick() {
                                if ((mBBABubbleGuideController != null
                                        && mBBABubbleGuideController.isShowAutoBuyBubble())
                                        || ReaderUtility.isFastDoubleClick()) {
                                    return;
                                }
                                ReaderManagerCallback rewardCallback = getReaderManagerCallback();
                                FBReaderApp fbReader = ReaderUtility.getFBReaderApp();
                                if (rewardCallback != null && fbReader != null && mBookInfo != null) {
                                    rewardCallback.showRewardView(rewardCallback.getRewardScheme(mBookInfo.getId()));
                                }
                                StatisticUtils.ubcClickGiftReward();
                                ThreadUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        BBAMenuComponent.getInstance().dismissAllMenu();
                                    }
                                }, 300);
                                ReaderMonitorUtils.logActionToReader(
                                        NovelReaderActionsRealTimeKey.NOVEL_READER_CLICK_REWARD);
                            }
                        });
                    }
                }
            }
        }

        // 隐藏简介
        if (NovelBusiReaderAdapter.hideInstructionEntrance()) {
            BBAReaderBarItemModel bbaReaderBarItemModel =
                    BBAMenuComponent.getInstance().getViewByType(BBAReaderBarItemModel.DETAIL);
            if (bbaReaderBarItemModel != null && bbaReaderBarItemModel.itemView != null) {
                bbaReaderBarItemModel.itemView.setVisibility(GONE);
            }
        }

        // 在阅读器里注册章节切换监听
        BBAMenuComponent.getInstance().addChangePageCallback(new IBBAChangePageCallback() {
            @Override
            public void onPreChapterClick() {
                // 阅读器进度条滑动切章支持返回上一步需求打点：上一章btn点击
                StatisticUtils.ubc753(isLocalBook() ? UBC_FROM_NATIVE_NOVEL : UBC_FROM_NOVEL,
                        StatisticsContants.UBC_TYPE_CLICK,
                        StatisticsContants.UBC_PAGE_READER_SETTING,
                        StatisticsContants.UBC_SOURCE_LAST_CHAPTER,
                        getActionFrom());
                ReaderMonitorUtils.logActionToReader(
                        NovelReaderActionsRealTimeKey.NOVEL_READER_CLICK_PRE_CHAPTER);
            }

            @Override
            public void onNextChapterClick() {
                // 阅读器进度条滑动切章支持返回上一步需求打点：下一章btn点击
                StatisticUtils.ubc753(isLocalBook() ? UBC_FROM_NATIVE_NOVEL : UBC_FROM_NOVEL,
                        StatisticsContants.UBC_TYPE_CLICK,
                        StatisticsContants.UBC_PAGE_READER_SETTING,
                        StatisticsContants.UBC_SOURCE_NEXT_CHAPTER,
                        getActionFrom());
                ReaderMonitorUtils.logActionToReader(
                        NovelReaderActionsRealTimeKey.NOVEL_READER_CLICK_NEXT_CHAPTER);
            }

            @Override
            public void onChapterProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onChapterStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onChapterStopTrackingTouch(SeekBar seekBar) {
                // 阅读器进度条滑动切章支持返回上一步需求打点：切章滚动条按压拖动
                StatisticUtils.ubc753(isLocalBook() ? UBC_FROM_NATIVE_NOVEL : UBC_FROM_NOVEL,
                        StatisticsContants.UBC_TYPE_CLICK,
                        StatisticsContants.UBC_PAGE_READER_SETTING,
                        StatisticsContants.UBC_SOURCE_PROGRESS,
                        getActionFrom());
            }

            @Override
            public void onShowProgressToast() {
                // 阅读器进度条滑动切章支持返回上一步需求打点：切章拖动toast展示
                StatisticUtils.ubc753(isLocalBook() ? UBC_FROM_NATIVE_NOVEL : UBC_FROM_NOVEL,
                        StatisticsContants.UBC_TYPE_SHOW,
                        StatisticsContants.UBC_PAGE_READER_SETTING,
                        StatisticsContants.UBC_SOURCE_PROGRESS_BACK,
                        getActionFrom());
            }

            @Override
            public void onHiddenProgressToast() {

            }
        });
    }

    private String getActionFrom() {
        String fromAction = "";
        ReaderManagerCallback callback = getReaderManagerCallback();
        if (callback != null) {
            fromAction = callback.getFromAction();
        }
        return fromAction;
    }

    /**
     * 获取左上角返回按钮图标
     *
     * @return
     */
    public Drawable getLeftCornerDrawable() {
        boolean isNight = myFBReaderApp != null && myFBReaderApp.getColorProfileName() != null &&
                TextUtils.equals(myFBReaderApp.getColorProfileName(), ColorProfile.NIGHT);
        return getResources().getDrawable(
                isNight ? R.drawable.bdreader_left_corner_back_icon_night
                        : R.drawable.bdreader_left_corner_back_icon_day);
    }

    /*
     * 动态设置底部广告banner的高度
     */
    private void dynamicSetAdHeight() {
        if (mBannerAdLayout != null) {
            ViewGroup.LayoutParams layoutParams = mBannerAdLayout.getLayoutParams();
            int adHeight = ReaderUtility.getBannerAdHeight();
            layoutParams.height = adHeight;
            mBannerAdLayout.setLayoutParams(layoutParams);
        }
    }

    /**
     * 设置背景颜色和statusBar颜色
     */
    private void setRootBackground() {
        if (mRootView == null) {
            return;
        }
        int backgroundColor = BBAReaderConfig.getInstance().getBackgroundColor();
        setStatus(BBAStatusBarUtil.isLightColor(backgroundColor));
        boolean loadingSwitch = BBAReaderComponent.getInstance().getSwitch(
                BBAABTestConstants.NOVEL_READER_LOADING_SWITCH, false);
        if (loadingSwitch) {
            BBAReaderBitmapManager.getInstance().getRootBackBitmap(new BBAReaderBitmapManager.CreateBitmapCallback() {
                @Override
                public void complete(final Bitmap rootBitmap) {
                    BBAThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (rootBitmap != null) {
                                mRootView.setBackground(new BitmapDrawable(rootBitmap));
                            } else {
                                int color = BBAModeChangeHelper.isNightMode()
                                        ? READER_NIGHT_BACKGROUND_COLOR
                                        : BBAReaderConfig.getInstance().getBackgroundColor();
                                mRootView.setBackgroundColor(color);
                                if (StubToolsWrapperKt.Companion.isEnableLog()) {
                                    Log.d(TAG, "背景色 BBAReaderBarItemModel.BACKGROUND_COLOR loadingSwitch setRootBackground ：" + System.currentTimeMillis());
                                }
                            }
                        }
                    });
                }
            });
        } else {
            Bitmap rootBitmap = BBAReaderBitmapManager.getInstance().getRootBackBitmap();
            if (rootBitmap != null) {
                mRootView.setBackground(new BitmapDrawable(rootBitmap));
            } else {
                int color = BBAModeChangeHelper.isNightMode()
                        ? READER_NIGHT_BACKGROUND_COLOR
                        : BBAReaderConfig.getInstance().getBackgroundColor();
                mRootView.setBackgroundColor(color);
            }
            if (StubToolsWrapperKt.Companion.isEnableLog()) {
                Log.d(TAG, "背景色 BBAReaderBarItemModel.BACKGROUND_COLOR setRootBackground ：" + System.currentTimeMillis());
            }
        }
    }

    /**
     * 设置护眼模式
     */
    private void setRootEyeShield() {
        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
        if (mEyeShield == null) {
            mEyeShield = LayoutInflater.from(this).
                    inflate(R.layout.bba_reader_eye_shield_layout, null);
        }
        if (mEyeShield != null) {
            decorView.removeView(mEyeShield);
            if (BBAReaderConfig.getInstance().isEyeShield()) {
                decorView.addView(mEyeShield);
                mEyeShield.setBackgroundColor(BBAUIUtils.getEyeShieldColor());
                mEyeShield.setVisibility(View.VISIBLE);
            } else {
                mEyeShield.setBackgroundColor(Color.TRANSPARENT);
                mEyeShield.setVisibility(View.GONE);
                decorView.removeView(mEyeShield);
            }
        }
    }

    private BBAPhoneStateManager.OnPhoneStateChangedListener mOnPhoneStateChangedListener =
            new BBAPhoneStateManager.OnPhoneStateChangedListener() {
                @Override
                public void onPhoneStateChanged(String action, BBAPhoneStateModel model) {
                    if (mReaderContainer != null) {
                        if (Intent.ACTION_TIME_TICK.equals(action)) {
                            String time = BBAStringUtils.getFormatDate(BBAStringUtils.DATE_FORMAT_PATTERN,
                                    model.getTimeValue());
                            mReaderContainer.setTime(time);
                        } else if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                            float mBatteryProgress = model.getBatteryValue();
                            mReaderContainer.setBatteryState(mBatteryProgress);
                        }
                    }
                }
            };

    /**
     * 设置状态栏，设备国产ROM，比如MIUI
     */
    private void setStatus(boolean isDark) {
        // 设置状态栏透明 设置会有把父类设置的白色背景覆盖
        BBAStatusBarUtil.setTranslucentStatus(this);
        // 一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        // 所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!isDark) {
            BBAStatusBarUtil.setStatusBarDarkTheme(this, false);
            if (isNewThemeColor()){
                BBAStatusBarUtil.setStatusBarColor(this, BBAThemeResourceHelper.getThemeCardColor());
            } else {
                BBAStatusBarUtil.setStatusBarColor(this, 0xffffff);
            }

            return;
        }
        if (!BBAStatusBarUtil.setStatusBarDarkTheme(this, true)) {
            // 如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            // 这样半透明+白=灰, 状态栏的文字能看得清
            BBAStatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
    }

    /**
     * 获取左上角返回按钮图标
     *
     * @return
     */
    private void setAutoDayAndNightService(boolean enabled) {
        Intent intent = new Intent(this, ReaderService.class);
        intent.setAction(ReaderService.ACTION_ENABLE_AUTO_SWITCH);
        intent.putExtra(ReaderService.EXTRA_ENABLE_AUTO_SWITCH, enabled);
        // 获取实验
        ReaderManagerCallback callback = ReaderManager.getInstance(this).getReaderManagerCallback();
        boolean isReaderServiceChange = callback != null
                && callback.getABTestCase("novel_reader_timer_service_optium");

        if (isReaderServiceChange) {
            AutoChangeNightServiceHelper.getInstance().onStartCommand(this, intent);
        } else {
            try {
                ComponentName componentName = new ComponentName(this, ReaderService.class);
                intent.setComponent(componentName);
                startService(intent);
            } catch (Exception e) {
                BBALogUtil.printStackTrace(e);
            }
        }
    }

    /**
     * 初始化业务配置数据
     */
    private void initBusinessConfig() {
        ReaderManagerCallback callback = ReaderManager.getInstance(this).getReaderManagerCallback();
        if (callback != null) {
            // 启动进行赋值-评论是否上线
            isCommentOnline = (Boolean) callback.getData(NovelReaderCallbackDataType.COMMENT_IS_ONLINE, null);
        }
    }

    /**
     * 对外提供评论是否上线
     *
     * @return
     */
    public boolean isCommentOnline() {
        return isCommentOnline;
    }

    /**
     * 展示更多设置菜单
     */
    public void showMoreSettingMenu() {
        BBAMenuComponent.getInstance().showMoreSettingMenu();
    }

    private void checkInit(String original) {
        ReaderManagerCallback callback =
                ReaderManager.getInstance(getApplicationContext()).getReaderManagerCallback();
        if (callback == null) {
            Book book = initBook(getIntent());
            if (book == null) {
                // 信息获取异常，退出，不再自动重新打开
                finish();
                return;
            }

            BookInfo bookInfo = book.createBookInfo();

            if (bookInfo == null) {
                // 信息获取异常，退出，不再自动重新打开
                finish();
                return;
            }

            if (book.getReadType() == ZLTextModelList.ReadType.LOCAL_TXT) {
                // 尝试重新打开本地书
                reOpenLocalTxt(book);
                return;
            }

            Map<String, String> data = new HashMap<>();
            String author = null;
            String image = null;
            String id = null;
            JSONObject slog = new JSONObject();
            try {
                JSONObject jsonObject = new JSONObject(bookInfo.getExtraInfo());
                author = jsonObject.optString("author");
                image = jsonObject.optString("cover_image_url");
                id = jsonObject.optString("id");
                if (!TextUtils.isEmpty(original)) {
                    slog.put("fromaction_original", original);
                }
            } catch (Exception e) {
            }
            data.put("author", author);
            data.put("image", image);
            data.put("slog", slog.toString());
            data.put("bookname", bookInfo.getDisplayName());
            data.put("cid", bookInfo.getChapterId());
            data.put("free", bookInfo.getFree());
            data.put("gid", bookInfo.getId());
            BookInvokeUtils.openBook(data);
            finish();
        }
    }

    /** 重新打开本地书 */
    private void reOpenLocalTxt(Book book) {
        Map<String, String> data = new HashMap<>();
        data.put(RE_OPEN_TXT, "1");
        if (book != null) {
            data.put(BookInfo.JSON_PARAM_TXT_ID, book.getTxtId());
        }
        BookInvokeUtils.openBook(data);
        finish();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String original = "";
        if (savedInstanceState != null) {
            original = savedInstanceState.getString("fromOriginal");
        }
        checkInit(original);
    }

    private void reportBackgroundColorUBC() {
        int backgroundColor = BBAReaderConfig.getInstance().getBackgroundColor();
        String source = SOURCE_DEFAULT_THEME;
        if (BBAThemeResourceHelper.isNewColor()) {
            source = BBAThemeResourceHelper.getNewTheme(backgroundColor);
        } else if (BBAMenuConfig.READER_BACKGROUND_COLORS.length >= 6) {
            if (backgroundColor == BBAMenuConfig.READER_BACKGROUND_COLORS[0]) {
                source = SOURCE_DEFAULT_THEME;
            } else if (backgroundColor == BBAMenuConfig.READER_BACKGROUND_COLORS[1]) {
                source = SOURCE_GREY_THEME;
            } else if (backgroundColor == BBAMenuConfig.READER_BACKGROUND_COLORS[2]) {
                source = SOURCE_ORANGE_THEME;
            } else if (backgroundColor == BBAMenuConfig.READER_BACKGROUND_COLORS[3]) {
                source = SOURCE_BROWN_THEME;
            } else if (backgroundColor == BBAMenuConfig.READER_BACKGROUND_COLORS[4]) {
                source = SOURCE_GREEN_THEME;
            } else if (backgroundColor == BBAMenuConfig.READER_BACKGROUND_COLORS[5]) {
                source = SOURCE_PINK_THEME;
            }
        }

        String from = StatisticsContants.UBC_FROM_NOVEL;
        if (isLocalBook()) {
            from = StatisticsContants.UBC_FROM_NATIVE_NOVEL;
        }
        StatisticUtils.ubc753(from, StatisticsContants.UBC_TYPE_CLICK,
                READER_SETTING_BACKGROUNDCOLOR, source, "");
    }

    private void reportReaderBrightnessUBC() {
        String from = UBC_FROM_NOVEL;
        if (isLocalBook()) {
            from = StatisticsContants.UBC_FROM_NATIVE_NOVEL;
        }
        reportFollowSysBrightnessUBC(from, "");
    }

    /**
     * 跟随系统亮度打点，只有选中了跟随系统亮度，才会上报
     *
     * @param from
     */
    public static void reportFollowSysBrightnessUBC(String from, String value) {
        FBReaderApp fbReaderApp = (FBReaderApp) FBReaderApp.Instance();
        if (fbReaderApp != null && fbReaderApp.isSysBrightness()) {
            StatisticUtils.ubc753(from, StatisticsContants.UBC_TYPE_CLICK,
                    "brightness",
                    "followsystem",
                    value);
        }
    }

    private void reportReaderBackgroundUBC() {
        String source;
        String currentProfile = ColorProfile.SIMPLE;
        FBReaderApp app = (FBReaderApp) ReaderBaseApplication.Instance();
        if (app != null) {
            currentProfile = app.getColorProfileName();
        }
        if (ColorProfile.SIMPLE.equals(currentProfile)) {
            source = SOURCE_DEFAULT_THEME;
        } else if (ColorProfile.GRAY.equals(currentProfile)) {
            source = SOURCE_GREY_THEME;
        } else if (ColorProfile.EYE_FRIENDLY.equals(currentProfile)) {
            source = SOURCE_GREEN_THEME;
        } else if (ColorProfile.PARCHMENT.equals(currentProfile)) {
            source = SOURCE_ORANGE_THEME;
        } else if (ColorProfile.MEMORY.equals(currentProfile)) {
            source = SOURCE_PINK_THEME;
        } else if (ColorProfile.DARK_YELLOW.equals(currentProfile)) {
            source = SOURCE_BROWN_THEME;
        } else {
            source = SOURCE_NIGHT_THEME;
        }
        String from = UBC_FROM_NOVEL;
        if (isLocalBook()) {
            from = StatisticsContants.UBC_FROM_NATIVE_NOVEL;
        }
        StatisticUtils.ubc753(from, StatisticsContants.UBC_TYPE_CLICK,
                "backgroundcolor", source, "");
    }

    /**
     * 当前是否本地TXT
     *
     * @return
     */
    public static boolean isLocalBook() {
        FBReaderApp app = (FBReaderApp) ReaderBaseApplication.Instance();
        if (app != null) {
            Book book = app.getBook();
            if (book != null) {
                return book.getReadType() == ReadType.LOCAL_TXT;
            }
        }
        return false;
    }

    /**
     * 初始化阅读器的相关controller
     */
    private void initControllers() {
        mBBAEduViewController = new BBAEduViewController();
        mBbaAutoAddBookShelfController = new BBAAutoAddBookShelfController(this);
        getLifecycle().addObserver(mBbaAutoAddBookShelfController);
        mBBABubbleGuideController = new BBABubbleGuideController();

        mBBALoginViewController = new BBALoginViewController(this);
        BBAReaderComponent.getInstance().setLoginViewProvider(mBBALoginViewController);
        mBBAPayViewController = new BBAPayViewController(this);
        BBAReaderComponent.getInstance().setPayViewProvider(mBBAPayViewController);
        BBAReaderComponent.getInstance().setIBBPreLoadInfoProvider(new BBAPreLoadController());
        BBAReaderComponent.getInstance().setIBBANotifyEvent(new BBANotifyEventTools());
        BBAReaderComponent.getInstance().setIBBAReaderContainerProvider(new BBAReaderContainerTools(true));
        // 通知novel-听书悬浮球隐藏
        NovelTtsInterface.getInstance().onDismissTingFloatMenu();
    }

    /**
     * 进行初始化浮层广告视图
     */
    public synchronized void initReaderPerformanceAdView() {
        BBAThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isDestroyed() || isFinishing()) {
                    return;
                }
                try {
                    // 如果标记没有初始化，或则浮层广告视图为null
                    if (!isPerformanceAdViewInit) {
                        mNewReaderPerformanceAdView = findViewById(R.id.read_performance_ad_view);
                        if (mNewReaderPerformanceAdView != null) {
                            mNewReaderPerformanceAdView.setLegal(true);
                            mNewReaderPerformanceAdView.setPageType("reader");
                        }
                        showReaderPerformanceAdView();
                        isPerformanceAdViewInit = true;
                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 进行显示操作
     */
    public void showReaderPerformanceAdView() {
        // 判断当前是否有虚拟导航健
        if (mNewReaderPerformanceAdView != null && isMenuAtHide()) {
            if (UIUtils.getNavigationHeight(this) != 0) {
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) mNewReaderPerformanceAdView.getLayoutParams();
                layoutParams.bottomMargin = (int) (BBADeviceUtil.dip2px(this, 162) +
                        UIUtils.getNavigationHeight(this));
                mNewReaderPerformanceAdView.setLayoutParams(layoutParams);
            }
            mNewReaderPerformanceAdView.show();
        }
    }

    /**
     * 进行隐藏操作
     */
    public void hideReaderPerformanceAdView() {
        if (mNewReaderPerformanceAdView != null) {
            mNewReaderPerformanceAdView.hide();
        }
    }

    public void showLoading() {
        showLoading(false);
    }

    /**
     * 显示loading
     *
     * @param isShowBackground 是否带有背景
     */
    public void showLoading(boolean isShowBackground) {
        if (loadingLayout != null) {
            if (isShowBackground) {
                loadingLayout.setBackgroundColor(BBAModeChangeHelper.isNightMode() ? READER_NIGHT_BACKGROUND_COLOR :
                        BBAReaderConfig.getInstance().getBackgroundColor());
            } else {
                loadingLayout.setBackgroundColor(BBAResourceHelper.getColor(R.color.transparent));
            }
            loadingLayout.setVisibility(View.VISIBLE);
            if (mDismissLoadingRunnable == null) {
                mDismissLoadingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mDismissLoadingRunnable = null;
                        if (!isDestroyed() && !isFinishing()) {
                            dismissLoading();
                        }
                    }
                };
            }
            BBAThreadUtils.runOnUiThread(mDismissLoadingRunnable, 6000);
        }
    }

    public void dismissLoading() {
        if (loadingLayout != null) {
            loadingLayout.setVisibility(GONE);
        }
        loadingDismiss();
    }

    /**
     * 返回广告页面的视图
     *
     * @return 广告页面的根视图
     */
    public RelativeLayout getAdViewLayout() {
        return null;
    }

    /**
     * 返回书尾页感谢语的视图
     *
     * @return 书尾页感谢语的视图
     */
    public RelativeLayout getThanksLayout() {
        return null;
    }

    /**
     * 返回章尾广告页面的视图
     *
     * @return
     */
    public RelativeLayout getChapterTailAdViewLayout() {
        return null;
    }

    /**
     * 返回阅读器底部banner广告页面的视图
     *
     * @return banner广告页面的根视图
     */
    public RelativeLayout getBannerAdViewLayout() {
        if (isLocalBook()) {
            return null;
        }
        return mBannerAdLayout;
    }

    /**
     * 尝试激活离线按钮
     */
    public void enableOfflineBtn() {

    }

    /**
     * 延时post到UI线程
     * 使用时注意内存泄漏问题！
     */
    public void postDelayed(Runnable runnable, long delayTime) {
        if (mUIHandler != null) {
            mUIHandler.postDelayed(runnable, delayTime);
        }
    }

    /**
     * 初始化{@link FBReaderApp}要使用的Actions
     *
     * @param app {@link FBReaderApp}
     */
    private void initAppActions(FBReaderApp app) {
        if (app == null) {
            return;
        }
        // 添加阅读器的Action
        app.addAction(ActionCode.SHOW_PREFERENCES, new ShowPreferencesAction(this, app));
        app.addAction(
                ActionCode.SET_SCREEN_ORIENTATION_SYSTEM,
                new SetScreenOrientationAction(this, app,
                        ZLibrary.SCREEN_ORIENTATION_SYSTEM));
        app.addAction(
                ActionCode.SET_SCREEN_ORIENTATION_SENSOR,
                new SetScreenOrientationAction(this, app,
                        ZLibrary.SCREEN_ORIENTATION_SENSOR));
        app.addAction(
                ActionCode.SET_SCREEN_ORIENTATION_PORTRAIT,
                new SetScreenOrientationAction(this, app,
                        ZLibrary.SCREEN_ORIENTATION_PORTRAIT));
        app.addAction(
                ActionCode.SET_SCREEN_ORIENTATION_LANDSCAPE,
                new SetScreenOrientationAction(this, app,
                        ZLibrary.SCREEN_ORIENTATION_LANDSCAPE));
        ZLAndroidLibrary lib = getZLibrary();
        if (lib != null && lib.supportsAllOrientations()) {
            app.addAction(
                    ActionCode.SET_SCREEN_ORIENTATION_REVERSE_PORTRAIT,
                    new SetScreenOrientationAction(this, app,
                            ZLibrary.SCREEN_ORIENTATION_REVERSE_PORTRAIT));
            app.addAction(
                    ActionCode.SET_SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
                    new SetScreenOrientationAction(this, app,
                            ZLibrary.SCREEN_ORIENTATION_REVERSE_LANDSCAPE));
        }

        // 亮度调节
        app.addAction(ActionCode.INCREASE_BRIGHTNESS,
                new ChangeBrightnessAction(this, app, 1));
        app.addAction(ActionCode.DECREASE_BRIGHTNESS,
                new ChangeBrightnessAction(this, app, -1));

        // 章节跳转
        app.addAction(ActionCode.LAST_PARAGRAPH,
                new ChangeParagraphAction(this, app, -1));
        app.addAction(ActionCode.NEXT_PARAGRAPH,
                new ChangeParagraphAction(this, app, 1));
    }

    /***
     * 为了防止内存泄露，在退出阅读器是进行Action 的移除
     */
    public void removeAppAtion() {
        if (myFBReaderApp == null) {
            return;
        }
        myFBReaderApp.removeAction(ActionCode.SHOW_PREFERENCES);
        myFBReaderApp.removeAction(ActionCode.SHOW_MENU);
        myFBReaderApp.removeAction(ActionCode.HIDE_MENU);
        myFBReaderApp.removeAction(ActionCode.SET_SCREEN_ORIENTATION_SYSTEM);
        myFBReaderApp.removeAction(ActionCode.SET_SCREEN_ORIENTATION_SENSOR);
        myFBReaderApp.removeAction(ActionCode.SET_SCREEN_ORIENTATION_PORTRAIT);
        myFBReaderApp.removeAction(ActionCode.SET_SCREEN_ORIENTATION_LANDSCAPE);
        ZLAndroidLibrary lib = getZLibrary();
        if (lib != null && lib.supportsAllOrientations()) {
            myFBReaderApp.removeAction(ActionCode.SET_SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            myFBReaderApp.removeAction(ActionCode.SET_SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
        myFBReaderApp.removeAction(ActionCode.INCREASE_BRIGHTNESS);
        myFBReaderApp.removeAction(ActionCode.DECREASE_BRIGHTNESS);
        myFBReaderApp.removeAction(ActionCode.LAST_PARAGRAPH);
        myFBReaderApp.removeAction(ActionCode.NEXT_PARAGRAPH);
    }

    /**
     * 进入全屏模式
     */
    public void enterFullScreenMode() {
        if (currentFullScreen == FULL_SCREEN_TYPE) {
            return;
        }
        final ZLAndroidLibrary zlibrary = getZLibrary();
        if (zlibrary != null && !zlibrary.isKindleFire() && !zlibrary.ShowStatusBarOption.getValue()) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            getWindow().getDecorView().setSystemUiVisibility(flag);
        }
        // 2021.08.10 备注：修改当前全屏状态
        currentFullScreen = FULL_SCREEN_TYPE;
    }

    public void enterFullScreenModeExitBottom() {
        final ZLAndroidLibrary zlibrary = getZLibrary();
        if (zlibrary != null && !zlibrary.isKindleFire() && !zlibrary.ShowStatusBarOption.getValue()) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            getWindow().getDecorView().setSystemUiVisibility(flag);
        }
    }

    /**
     * 退出全屏模式（显示工具栏/菜单时）
     */
    public void exitFullScreenMode() {
        final ZLAndroidLibrary zlibrary = getZLibrary();
        if (zlibrary != null && !zlibrary.isKindleFire() && !zlibrary.ShowStatusBarOption.getValue()) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
            // 2021.07.22 zhangqian58备注 版本：12.22
            // 问题：每次退出全屏展现菜单栏时，顶部状态栏字体颜色没有修改。
            // 解决方案；根据日夜间模式修改状态栏字体深浅色
            changeStatusBarColorWhenExitFullScreen();
        }
        // 2021.08.10 备注：修改当前全屏状态
        currentFullScreen = EXIT_FULL_SCREEN_TYPE;
        setRootEyeShield();
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        // 是否是全局听书初始化请求
//        mNeedOpenGlobalTTs = intent.getBooleanExtra(INovelTts.IS_OPEN_VOICE_FROM_DETAIL, false);
//        if (mNeedOpenGlobalTTs) {
//            NovelTtsInterface.getInstance().startOpenPlayer();
//            finish();
//            return;
//        }

        Book book = initBook(getIntent());
        Book newBook = initBook(intent);
        if (book == null || newBook == null || bbaBook == null || mBookInfo == null) {
            UIUtil.showErrorMessage(getActivity(), "initError");
            finish();
            return;
        }

        if (TextUtils.equals(book.getNovelId(), newBook.getNovelId())
                && (book.getReadType() == newBook.getReadType()) &&
                !NovelTtsInterface.getInstance().isPlaying()) {
            // mBookInfo：最新书籍，bbaBook：上次书籍信息。是否展示扉页状态不一致，重新打开阅读器
            if (mBookInfo.getWithoutTitlePage() != bbaBook.getWithoutTitlePage()) {
                finish();
                startActivity(intent);
                return;
            }

            int gotoIndex = bbaBook.fromChapterGetIndex(
                    mBookInfo.getChapterId(), mBookInfo.getGotoLast());
            if (gotoIndex != -1) {
                BBAJumpChapterUtil.jumpToChapter(gotoIndex);
            }
            return;
        }
        ReaderManager.getInstance(getApplicationContext()).setOpeningBook(true);
        ReaderManager.getInstance(getApplicationContext()).setClosingBook(false);

        finish();
        startActivity(intent);
        super.onNewIntent(intent);
    }

    /**
     * 设置全屏模式
     */
    public void setWindowFullScreenFlag() {
        ZLAndroidLibrary lib = getZLibrary();
        if (lib == null) {
            return;
        }
        myFullScreenFlag = lib.ShowStatusBarOption.getValue()
                ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, myFullScreenFlag);
    }

    @Override
    protected void onStart() {
        refreshFontFamily();
        // FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS:表明会Window负责系统bar的background绘制
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        super.onStart();
        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.registerTag(ReaderTimeTag.TAG_READER_ON_START, ReaderTimeTag.DES_READER_ON_START);
            ReaderTimeLogger.recordStart(ReaderTimeTag.TAG_READER_ON_START);
        }

        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.registerTag(
                    ReaderTimeTag.TAG_READER_START_COST_INIT_BOOK,
                    ReaderTimeTag.DES_READER_START_COST_INIT_BOOK);
            ReaderTimeLogger.recordStart(ReaderTimeTag.TAG_READER_START_COST_INIT_BOOK);
        }
        ReaderMonitorUtils.logActionToReader(NovelReaderActionsRealTimeKey.NOVEL_BECOME_ACTIVE);
        // 根据传入的数据构建小说信息
        Book book = initBook(getIntent());
        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.recordEnd(ReaderTimeTag.TAG_READER_START_COST_INIT_BOOK);
        }
        if (book == null) {
            UIUtil.showErrorMessage(getActivity(), "initError");
            finish();
            return;
        }

        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.registerTag(
                    ReaderTimeTag.TAG_READER_START_COST_INIT_PLUGIN_ACTIONS,
                    ReaderTimeTag.DES_READER_START_COST_INIT_PLUGIN_ACTIONS);
            ReaderTimeLogger.recordStart(ReaderTimeTag.TAG_READER_START_COST_INIT_PLUGIN_ACTIONS);
        }
        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.recordEnd(ReaderTimeTag.TAG_READER_START_COST_INIT_PLUGIN_ACTIONS);
        }

        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.registerTag(
                    ReaderTimeTag.TAG_READER_START_COST_INIT_SCREEN_MODE,
                    ReaderTimeTag.DES_READER_START_COST_INIT_SCREEN_MODE);
            ReaderTimeLogger.recordStart(ReaderTimeTag.TAG_READER_START_COST_INIT_SCREEN_MODE);
        }
//        // 根据ReaderManager中的设定，初始化进入阅读器时的屏幕模式
//        int readerScreenMode = ReaderManager.getInstance(getActivity()).getReaderScreenMode();
//        if (NovelDeviceUtil.isXiaomiTabletDevice() || HostDeviceUtilsWrapper.isTableOrFoldExpandState()) {
//            readerScreenMode = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
//        }
//        switch (readerScreenMode) {
//            case ReaderManager.READER_SCREEN_MODE_AUTO:
//                SetScreenOrientationAction.setOrientation(
//                        getActivity(), getZLibrary().getOrientationOption().getValue());
//                break;
//            case ReaderManager.READER_SCREEN_MODE_LANDSCAPE:
//                SetScreenOrientationAction.setOrientation(getActivity(), ZLibrary.SCREEN_ORIENTATION_LANDSCAPE);
//                getZLibrary().getOrientationOption().setValue(ZLibrary.SCREEN_ORIENTATION_LANDSCAPE);
//                break;
//            case ReaderManager.READER_SCREEN_MODE_PORTRAIT:
//                SetScreenOrientationAction.setOrientation(getActivity(), ZLibrary.SCREEN_ORIENTATION_PORTRAIT);
//                getZLibrary().getOrientationOption().setValue(ZLibrary.SCREEN_ORIENTATION_PORTRAIT);
//                break;
//            case ActivityInfo.SCREEN_ORIENTATION_SENSOR:
//                SetScreenOrientationAction.setOrientation(getActivity(), ZLibrary.SCREEN_ORIENTATION_SENSOR);
//                break;
//            default:
//                break;
//        }

        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.recordEnd(ReaderTimeTag.TAG_READER_START_COST_INIT_SCREEN_MODE);
        }

        // 通过参数判断，放置重复初始化加载
        if (ReaderManager.getInstance(getApplicationContext()).isOpeningBook()) {
            ReaderManager.getInstance(getApplicationContext()).setOpeningBook(false);
            BBAThreadUtils.runOnAsyncThread(new Runnable() {
                @Override
                public void run() {
                    FBReaderApp app = myFBReaderApp;
                    if (app != null) {
                        // 初始化相关Views
                        app.initViews();
                        // 初始化相应的Actions
                        app.initActions();
                        initAppActions(app);
                        // 打开相应书籍
                        openBook(getIntent(), null, false);
                        // 回调框，获取离线地址5.3.5新增
                        loadOfflineAbleData();
                    }
                }
            });
        } else {
            BBAThreadUtils.runOnAsyncThread(new Runnable() {
                @Override
                public void run() {
                    FBReaderApp app = myFBReaderApp;
                    if (app != null) {
                        initAppActions(app);
                    }
                }
            });
        }

        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.recordEnd(ReaderTimeTag.TAG_READER_ON_START);
        }

        final ReaderManagerCallback callback = ReaderManager.getInstance(getActivity())
                .getReaderManagerCallback();
        if (callback != null) {
            callback.onStart(mBookInfo);
            // 通知全局听书，reader生命周期
            callback.sendNotify(NovelReaderCallbackNotifyType.NOTIFY_READER_ON_START, this.getActivity());
        } else {
            // 解决阅读器切后台之后被系统kill，而部分手机系统会在应用重回前台时将回收前的activity栈重建，
            // 由于阅读器是特殊的activity，需要经过初始化才能正常启动
            // 当callback为null，表示阅读器未经过外部初始化，无法正常使用（一直loading），所以这里直接finish
            finish();
            if (DEBUG) {
                throw new RuntimeException("reader not inited, finish");
            }
        }
        // 是否是全局听书初始化请求
//        mNeedOpenGlobalTTs = getIntent().getBooleanExtra(INovelTts.IS_OPEN_VOICE_FROM_DETAIL, false);
//        if (mNeedOpenGlobalTTs) {
//            NovelTtsInterface.getInstance().startOpenPlayer();
//            finish();
//            return;
//        }
        try {
            // 初始化自定义菜单项：因为有些菜单项需要书籍信息，所以放在书籍解析之后执行
            initCustomMenu();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        boolean loadingSwitch = BBAReaderComponent.getInstance().getSwitch(
                BBAABTestConstants.NOVEL_READER_LOADING_SWITCH, false);
        if (loadingSwitch) {
            if (isTitlePagePositionByStorage()) {
                showFirstMenu();
            }
        } else {
            if (isTitlePagePositionByStorage() && isFirstCreateReader ) {
                if (callback != null && !callback.needRightsInvoke()) {
                    // 非需要检查用户权益的场景，才自动弹出对话框
                    BBAMenuComponent.getInstance().setAnimEnable(false);
                    BBAMenuComponent.getInstance().showMenu();
                    isFirstCreateReader = false;
                }
            }
        }
    }

    private void showFirstMenu() {
        if (isFirstCreateReader && drawComplete
                && !BBAMenuComponent.getInstance().isMenuShowing()) {
            BBAMenuComponent.getInstance().setAnimEnable(false);
            BBAMenuComponent.getInstance().showMenu();
            isFirstCreateReader = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 进行存储session数据
        if (!NovelUBCDurationSearchSessionWrapper.isInSearchSession() && outState != null) {
            String session = NovelUBCDurationSearchSessionWrapper.getSSession();
            outState.putString("KEY_GET_STRING_UPDATE_UBC_DURATION_SESSION", session);
        }
        ReaderManager manager = ReaderManager.getInstance(this);
        if (manager != null) {
            ReaderManagerCallback callback = manager.getReaderManagerCallback();
            if (callback != null) {
                Object channel = callback.getData(NovelReaderCallbackDataType.GET_READER_CHANNEL_DATA, "");
                if (channel instanceof String) {
                    String readerChannelData = (String) channel;
                    try {
                        JSONObject jsonObject = new JSONObject(readerChannelData);
                        String original = jsonObject.optString("fromaction_original");
                        if (outState != null) {
                            outState.putString("fromOriginal", original);
                        }
                    } catch (JSONException e) {
                    }
                }
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // v10.0.0 解决历史用户反馈问题: 小说的亮度与系统亮度不一致
        // 当当前窗口失去焦点的时候, 记录一下系统当前的亮度
        if (!hasFocus) {
            mLastBrightnessLoseFocus = getScreenBrightness();
            saveReadProgress();
        }
        // 2021.08.10 备注：焦点变化时，全屏状态恢复原状
        switch (currentFullScreen) {
            case FULL_SCREEN_TYPE:
                ReaderStatusBarUtil.setFullScreen(getWindow());
                break;
            case EXIT_FULL_SCREEN_TYPE:
                exitFullScreenMode();
                break;
            case EXIT_FULL_SCREEN_ONLY_BOTTOM_TYPE:
                exitFullScreenModeOnlyBottomBar();
                break;
        }

        if (BBAMenuComponent.getInstance().getMenuController() instanceof BBAMenuController) {
            ((BBAMenuController) BBAMenuComponent.getInstance().getMenuController()).refreshNavigationBarHeight();
        }
    }

    /**
     * 保存当前阅读进度
     */
    public void saveReadProgress() {
        if (myFBReaderApp != null) {
            myFBReaderApp.storePosition();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 悬浮球是TTS那边在activity切换的回调里处理的，不判断书类型，小说这边在他们处理完之后的onResume里隐藏一下
        if (bbaBook != null && bbaBook.isLocalBook()) {
            if (NovelMiniPlayerViewWrapper.isShow()) {
                NovelMiniPlayerViewWrapper.hideHoverForNovel(null);
            }
        }
        // 开始记录时间
        startRecordDurationTimer();
        BBAAdUtil.setActivityContentHeight(getContentHeight());
        if (mInFloatGuide) {
            mInFloatGuide = false;
            // 如果有保命弹框，则通知全局运营位当前不能显示
            ReaderManagerCallback callback = ReaderManager.getInstance(getApplicationContext())
                    .getReaderManagerCallback();
            if (callback != null) {
                callback.sendNotify(
                        NOTIFY_SET_GLOBAL_OPERATION_VIEW_DIALOG_IS_SHOW, mInFloatGuide);
            }
            return;
        }
        if (!NovelBusiAppAdapter.useExternalNightMode() && mUIHandler != null) {
            if (readerThemeChangedRunnable == null) {
                readerThemeChangedRunnable = new ReaderThemeChangedRunnable(this);
            }
            mUIHandler.postDelayed(readerThemeChangedRunnable, 500);
        }
//        ReaderVoicePlayerContext.getInstance().setCurrentReader(FBREADER_ACTIVITY);
        mIsReaderForeground = true;
        final ZLAndroidLibrary zlibrary = getZLibrary();
        ReaderManager.getInstance(getActivity()).setLibrary(zlibrary);
        zlibrary.setReaderOrientation();
        zlibrary.setActivity(this);
        if (!ReaderBannerAdViewManager.getInstance().readyToShowAdView() &&
                !NovelGalaxyWrapper.isNetworkConnected(this)) {
            ReaderBannerAdViewManager.getInstance().delayRegenBitmap();
        }
        if (mMenuContainer != null) {
            // 为了让悬浮球展示，故放到post中
            mMenuContainer.post(new Runnable() {
                @Override
                public void run() {
                    ReaderManagerCallback callback = getReaderManagerCallback();
                    if (callback != null) {
                        // 通知全局听书，reader生命周期
                        callback.sendNotify(NovelReaderCallbackNotifyType.NOTIFY_READER_ON_RESUME, getActivity());
                    }
                }
            });
        }
        doResume();
        if (isFirstResume) {
            isFirstResume = false;
        }
    }

    /**
     * 执行resume的过程
     */
    public void doResume() {
        if (mPhoneStateManager != null) {
            mPhoneStateManager.startListening();
        }
        AllScenesStatisticUtils.pageDuration = SystemClock.uptimeMillis();
        if (mReaderContainer != null) {
            mReaderContainer.updateAdState();
        }
        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.registerTag(ReaderTimeTag.TAG_READER_ON_RESUME, ReaderTimeTag.DES_READER_ON_RESUME);
            ReaderTimeLogger.recordStart(ReaderTimeTag.TAG_READER_ON_RESUME);
        }
        // 2021.08.10 备注：后台进入时，根据退出时的状态恢复原状
        // 2021.09.07 备注：每次进入页面按照日夜间模式修改底部虚拟按键和顶部状态栏背景颜色
        changeTopStatusBarBgColorAndBottomNavigation();
        startRead = SystemClock.uptimeMillis();
        //从用户看到界面开始计时
        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.registerTag(ReaderTimeTag.TAG_READER_OPEN_NOVEL, ReaderTimeTag.DES_READER_OPEN_NOVEL);
            ReaderTimeLogger.recordStart(ReaderTimeTag.TAG_READER_OPEN_NOVEL);
        }

        // 设置屏幕常亮时间
        if (bbaFunctionScreen != null) {
            bbaFunctionScreen.saveSystemOffScreenTime();
            bbaFunctionScreen.setInScreenProtectedTime();
        }

        if (myFBReaderApp == null) {
            return;
        }
        // 开启计时器
        myFBReaderApp.startTimer();
        // 通知HOST，阅读器处于resume状态
        ReaderUtility.notifyHost(ReaderConstant.READER_ON_RESUME, String.valueOf(drawComplete));
        int login = ReaderUtility.isLogin() ? 1 : 0;
        if (lastLoginStatus != -1 && lastLoginStatus != login) {
            if (StubToolsWrapperKt.Companion.isEnableLog()) {
                Log.d(TAG_MONITOR_HONOR, "doResume: login status changed, current fontLevel is: " + BBAReaderConfig.getInstance().getFontSize());
            }
            BBAEventBus.getInstance().dispatchEvent(FONT_SIZE_CHANGE, BBAEventData.createEventData()
                    .add(BOOK_TYPE_IS_LEGAL, true));
        }
        if (StubToolsWrapperKt.Companion.isEnableLog()) {
            Log.d(TAG_MONITOR_HONOR, "doResume: current fontLevel is: " + BBAReaderConfig.getInstance().getFontSize());
        }
        lastLoginStatus = login;
        if (mReaderContainer != null && bbaBook != null && !isFirstResume) {
            BBAReaderContainerAdapter adapter = mReaderContainer.getAdapter();
            if (adapter != null) {
                adapter.setForceRefresh(true);
            }
            BBAEventBus.getInstance().dispatchEvent(REQUEST_CHAPTER_CONTENT,
                    BBAEventData.createEventData()
                            .add(FILE_INDEX, bbaBook.getCurrentChapterIndex())
                            .add(FILE_ID, bbaBook.getCurrentChapterId()));
        }
        // 设置自动购买状态 使用异步请求
        NovelExecutorUtilsExt.postOnSerial(new AutoBuyOnRunnable(this), "auto_buy");
        // 设置屏幕亮度
        final int brightnessLevel = getZLibrary().getBrightnessLevel();
        if (myFBReaderApp.isSysBrightness()) {
            setScreenBrightnessAuto();
        } else {
            BBADeviceUtil.changeAppBrightness(brightnessLevel, this);
        }

        // 设置物理键盘亮度
        if (getZLibrary().DisableButtonLightsOption.getValue()) {
            setButtonLight(false);
        }

        if (!myFBReaderApp.isVoiceAvailable()) {
            myFBReaderApp.resetAndRepaint();
        }

        if (ReaderTimeLogger.FLAG) {
            ReaderTimeLogger.recordEnd(ReaderTimeTag.TAG_READER_ON_RESUME);
        }

        // 根据需要显式翻页的用户引导图,扉页不展示
        if (mBBAEduViewController != null) {
            mBBAEduViewController.showUserEduViewIfNeed(mRootView);
        }

        if (bbaFunctionRestRemind != null) {
            bbaFunctionRestRemind.startRestTiming();
        }
        if (myBook != null) {
            BookInfo bookInfo = myBook.createBookInfo();
            if (bookInfo != null) {
                ReaderManagerCallback callback = ReaderManager.getInstance(this).getReaderManagerCallback();
                if (callback != null) {
                    // 阅读器开始计时
                    int type = bookInfo.getType();
                    ReaderLog.d(TAG, "ReadFlowManager novelId=" + bookInfo.getId());
                    BBAReaderDurationTools.getInstance().startReadDuration(ReaderUtility.safeToLong(mBookInfo.getId()),
                            String.valueOf(type), false);
                }
                try {
                    String pageStr = "";
                    if (mBookInfo != null) {
                        pageStr = mBookInfo.getpageInfo() == null ? "" : mBookInfo.getpageInfo();
                    }
                    logStatisticEvent(StatisticEvent.UBC_EVENT_START_READING,
                            String.valueOf(bookInfo.getType()), pageStr);
                } catch (NoSuchFieldError e) {
                    ReaderLog.e(TAG, "novel_no_such_field_error");
                }
            }
        }
        BBAReadTimeManager.getInstance().init();
        // 评论页返回阅读器，重新请求评论接口
        if (myFBReaderApp.needReloadComment()
                && myBook != null && ReaderUtility.getLibraryActivity() != null) {
            myFBReaderApp.postLoadTiebaCommandServiceTask(ReaderUtility.getLibraryActivity(),
                    myBook.getNovelId(), myBook);
        }
        // 刷新竖向翻页的视图
        resetVerticalPages();

        // 阅读器回到前台，通知广告进行resume操作：重新播放视频
        ReaderAdViewManager.getInstance().onReaderAdResume();
        // zhuyong01备注：增加章尾广告判断
        ReaderChapterTailAdViewManager.getInstance().onReaderAdResume();

        // v11.15.0增加：切到前台仍继续播放状态，则增加TTS打点监控
        if (myFBReaderApp.isVoicePlaying()) {
            StatisticUtils.ubcTtsMonitor();
        }

        if (notchScreenRunnable == null) {
            notchScreenRunnable = new NotchScreenRunnable(this);
        }
        // 延迟100ms获取挖孔屏状态
        if (mUIHandler != null) {
            mUIHandler.postDelayed(notchScreenRunnable, 100);
        }
        // 延时1秒显示从本页读
        int delayTime = 1000;
        if (!mRestHandler.hasMessages(MSG_UPDATE_READ_CURRENT_PAGE)) {
            mRestHandler.sendEmptyMessageDelayed(MSG_UPDATE_READ_CURRENT_PAGE, delayTime);
        }
        if (!BBAMenuComponent.getInstance().isMenuShowing()) {
            enterFullScreenMode();
        } else {
            exitFullScreenMode();
        }
    }

    public static class AutoBuyOnRunnable implements Runnable {

        private WeakReference<FBReader> weakReference;

        public AutoBuyOnRunnable(FBReader fbReader) {
            weakReference = new WeakReference<>(fbReader);
        }

        @Override
        public void run() {
            if (weakReference == null) {
                return;
            }
            FBReader fbReader = weakReference.get();
            if (fbReader == null
                    || fbReader.isFinishing()
                    || fbReader.isDestroyed()
                    || fbReader.mRestHandler == null) {
                return;
            }
            try {
                boolean autoBuyOn = TextUtils.equals(ReaderUtility.getStateByKey(ReaderUtility.AUTO_BUY),
                        ReaderUtility.AUTO_BUY_ON);
                Message message = Message.obtain();
                message.obj = autoBuyOn;
                message.what = MSG_UPDATE_AUTO_BUY_STATUS;
                fbReader.mRestHandler.sendMessage(message);
            } catch (Exception e) {
            }
        }
    }

    public static class NotchScreenRunnable implements Runnable {

        private WeakReference<FBReader> weakReference;

        public NotchScreenRunnable(FBReader fbReader) {
            weakReference = new WeakReference<>(fbReader);
        }

        @Override
        public void run() {
            if (weakReference != null) {
                FBReader fbReader = weakReference.get();
                if (fbReader != null && fbReader.myFBReaderApp != null) {
                    // 设置是否是挖孔屏
                    fbReader.myFBReaderApp.isNotchScreen = ReaderUtility.hasNotchInScreen(fbReader);
                }
            }
        }
    }

    public static class ReaderThemeChangedRunnable implements Runnable {

        private WeakReference<FBReader> weakReference;

        public ReaderThemeChangedRunnable(FBReader fbReader) {
            weakReference = new WeakReference<>(fbReader);
        }

        @Override
        public void run() {
            if (weakReference != null) {
                FBReader fbReader = weakReference.get();
                if (fbReader != null && !fbReader.isFinishing()) {
                    ReaderManagerCallback callback = getReaderManagerCallback();
                    if (callback != null) {
                        boolean nightMode = BBAModeChangeHelper.isNightMode();
                        callback.onReaderThemeChanged(nightMode, true);
                    }
                }
            }
        }
    }

    /**
     * 刷新竖向翻页的视图
     */
    public void resetVerticalPages() {
        final ReaderManagerCallback callback = ReaderManager.getInstance(getActivity())
                .getReaderManagerCallback();
        if (callback != null) {
            callback.onResume(mBookInfo);
            if (!hasCheckedRequestOpenReaderTime) {
                hasCheckedRequestOpenReaderTime = true;
                callback.checkNeedRequestOpenReaderTime();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPhoneStateManager != null) {
            mPhoneStateManager.stopListening();
        }
        // 翻页时长打点，切后台记录页面当前阅读的时长，后续上报
        long currentTimeMillis = SystemClock.uptimeMillis();
        AllScenesStatisticUtils.pageReminDuration += currentTimeMillis
                - AllScenesStatisticUtils.pageDuration;
        ReaderUtility.notifyHost(ReaderConstant.READER_ON_PAUSE, "");
        if (mReaderContainer != null) {
            mReaderContainer.clearItemADShowing();
        }
        if (mExecutor != null) {
            mExecutor.shutdown();
            mExecutor = null;
        }
        mIsReaderForeground = false;
        endRead = SystemClock.uptimeMillis();
        String time = String.valueOf(endRead - startRead) + "ms";
        if (myBook != null) {
            switch (myBook.getReadType()) {
                case PLAIN_OFFLINE:
                    logStatisticEvent(StatisticEvent.EVENT_LOG_READING_TIME, time,
                            String.valueOf(BookInfo.TYPE_PLAIN_LOCAL));
                    break;
                case ORGANIZED_ONLINE:
                    logStatisticEvent(StatisticEvent.EVENT_LOG_READING_TIME, time,
                            String.valueOf(BookInfo.TYPE_ORGANIZED_ONLINE));
                    break;
                case ORGANIZED_OFFLINE:
                    logStatisticEvent(StatisticEvent.EVENT_LOG_READING_TIME, time,
                            String.valueOf(BookInfo.TYPE_ORGANIZED_LOCAL));
                    break;
                case LOCAL_TXT:
                    logStatisticEvent(StatisticEvent.EVENT_LOG_READING_TIME, time,
                            String.valueOf(BookInfo.TYPE_LOCAL_TXT));
                    break;
                case ORGANIZED_MIXTURE:
                    logStatisticEvent(StatisticEvent.EVENT_LOG_READING_TIME, time,
                            String.valueOf(BookInfo.TYPE_ORGANIZED_MIXTURE));
                    break;
                default:
                    logStatisticEvent(StatisticEvent.EVENT_LOG_READING_TIME, time,
                            String.valueOf(BookInfo.TYPE_ORGANIZED_ONLINE));
                    break;
            }
        } else {
            logStatisticEvent(StatisticEvent.EVENT_LOG_READING_TIME, time,
                    String.valueOf(BookInfo.TYPE_ORGANIZED_ONLINE));
        }

        myFBReaderApp.stopTimer();

        // 设置物理键盘亮度
        if (getZLibrary() != null && getZLibrary().DisableButtonLightsOption.getValue()) {
            setButtonLight(true);
        }
        myFBReaderApp.onWindowClosing();

        // 设置回系统的屏保时间
        if (bbaFunctionScreen != null) {
            bbaFunctionScreen.setBackScreenProtectedTime();
        }
        // 停止记录阅读时长
        BBAReaderDurationTools.getInstance().endReadDuration();

        // v11.15.0增加：切到后台仍继续播放状态，则增加TTS打点监控
        if (myFBReaderApp.isVoicePlaying()) {
            StatisticUtils.ubcTtsMonitor();
        }

        // 暂停时即可取消获取顶部提醒
        if (topNoticeHandler != null) {
            if (topNoticeRunnable != null) {
                topNoticeHandler.removeCallbacks(topNoticeRunnable);
                topNoticeRunnable = null;
            }
            topNoticeHandler = null;
        }

        ReaderBookTailThanksViewManager.getInstance().setNeedShowTrueView();
        ReaderManagerCallback callback = ReaderManager.getInstance(this).getReaderManagerCallback();
        if (callback != null) {
            // 通知全局听书，reader生命周期
            callback.sendNotify(NovelReaderCallbackNotifyType.NOTIFY_READER_ON_PAUSE, this.getActivity());

            if (mBookInfo != null) {
                mBookInfo.setCurrentChapterName(bbaBook != null ? bbaBook.getCurrentChapterName() : "");
                mBookInfo.setChapterIndex(bbaBook != null ? bbaBook.getCurrentChapterIndex() : -1);
            }

            int chapterCount = 0;
            if (bbaBook != null && bbaBook.getDirectory() != null && bbaBook.getDirectory().getChapterList() != null) {
                chapterCount = bbaBook.getDirectory().getChapterList().size();
            }

            callback.onPause(mBookInfo, chapterCount);
        }

        // 停止记录duration时长
        stopRecordDurationTimer();

        // 记录书架书籍当前阅读时间
        saveShelfBookReadTime();

        // 在当前阅读页面时，从外部打开本地txt书籍时，本地txt听书相关数据需要初始化
        SpeechDataHelper.isInitedTTSSetting = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bbaFunctionRestRemind != null) {
            bbaFunctionRestRemind.stopRestTiming();
        }
        doSettingsStatistic();
        ReaderMonitorUtils.logActionToReader(NovelReaderActionsRealTimeKey.NOVEL_ENTER_BACKGROUND);
        ReaderManagerCallback callback = getReaderManagerCallback();
        if (callback != null) {
            // 通知全局听书，reader生命周期
            callback.sendNotify(NovelReaderCallbackNotifyType.NOTIFY_READER_ON_STOP, this.getActivity());
            // 如果跳转书架的时候没有关闭阅读器，为保证阅读进度，需要在onStop中保存书籍信息
            if (myBook != null) {
                BookInfo bookInfo = myBook.createBookInfo();
                FBReaderApp app = (FBReaderApp) ReaderBaseApplication.Instance();
                if (app != null) {
                    FBView view = app.getTextView();
                    if (view != null) {
                        ZLTextModelList modelList = view.getModelList();
                        if (modelList != null) {
                            String chapterName = modelList.getChapterName(myBook.getChapterIndex());
                            bookInfo.setCurrentChapterName(chapterName);
                        }
                    }
                }
                callback.saveBookInfo(bookInfo);
            }
        }
    }

    private void reportExitReaderUBC() {
        boolean isLocal = isLocalBook();
        String fontLevel = String.valueOf(BBAReaderConfig.getInstance().getFontSize());
        StatisticUtils.ubcReaderActivityExit(isLocal, UBC_SOURCE_FONT_SIZE, fontLevel);
        int backgroundColor = BBAReaderConfig.getInstance().getBackgroundColor();
        String source = SOURCE_DEFAULT_THEME;
        if (BBAThemeResourceHelper.isNewColor()){
            source = BBAThemeResourceHelper.getNewTheme(backgroundColor);
        }else if (BBAMenuConfig.READER_BACKGROUND_COLORS.length >= 6) {
            if (backgroundColor == BBAMenuConfig.READER_BACKGROUND_COLORS[0]) {
                source = SOURCE_DEFAULT_THEME;
            } else if (backgroundColor == BBAMenuConfig.READER_BACKGROUND_COLORS[1]) {
                source = SOURCE_GREY_THEME;
            } else if (backgroundColor == BBAMenuConfig.READER_BACKGROUND_COLORS[2]) {
                source = SOURCE_ORANGE_THEME;
            } else if (backgroundColor == BBAMenuConfig.READER_BACKGROUND_COLORS[3]) {
                source = SOURCE_BROWN_THEME;
            } else if (backgroundColor == BBAMenuConfig.READER_BACKGROUND_COLORS[4]) {
                source = SOURCE_GREEN_THEME;
            } else if (backgroundColor == BBAMenuConfig.READER_BACKGROUND_COLORS[5]) {
                source = SOURCE_PINK_THEME;
            }
        }
        StatisticUtils.ubcReaderActivityExit(isLocal, UBC_TYPE_LITE_READER_BACKGROUND, source);
        String flip;
        int turnPageType = BBAReaderConfig.getInstance().getTurnPageType();
        switch (turnPageType) {
            case BBADefaultConfig.HORIZONTAL_NONE:
                flip = ReaderSettingsHelper.TYPE_NONE;
                break;
            case BBADefaultConfig.HORIZONTAL_CURL:
                flip = ReaderSettingsHelper.TYPE_SIMULATION;
                break;
            case BBADefaultConfig.HORIZONTAL_TRANSLATE:
                flip = ReaderSettingsHelper.TYPE_SHIFT;
                break;
            case BBADefaultConfig.VERTICAL:
                flip = ReaderSettingsHelper.TYPE_VERTICAL_SCROLL;
                break;
            default:
                flip = ReaderSettingsHelper.TYPE_NONE;
                break;
        }
        StatisticUtils.ubcReaderActivityExit(isLocal, UBC_TYPE_LITE_READER_TURN_PAGE, flip);
        int spaceType = BBAReaderConfig.getInstance().getSpaceType();
        String novelFormat;
        switch (spaceType) {
            case BBADefaultConfig.ROW_SPACING_COMPACT:
                novelFormat = ReaderSettingsHelper.ROW_SPACING_COMPACT;
                break;
            case BBADefaultConfig.ROW_SPACING_NORMAL:
                novelFormat = ReaderSettingsHelper.ROW_SPACING_NORMAL;
                break;
            case BBADefaultConfig.ROW_SPACING_LOOSE:
                novelFormat = ReaderSettingsHelper.ROW_SPACING_LOOSE;
                break;
            default:
                novelFormat = ReaderSettingsHelper.ROW_SPACING_NORMAL;
                break;
        }
        StatisticUtils.ubcReaderActivityExit(isLocal, UBC_NOVEL_FORMAT, novelFormat);
        // 上报护眼模式的点位
        StatisticUtils.reportReaderEyeShieldModelInQuitReaderUBC(isLocalBook(),
                BBAReaderConfig.getInstance().isEyeShield());
    }

    /**
     * 做一下设置页面各项统计
     * TODO：该方法并不会上传统计数据！！！V12.19没有适配VIP免费书预加载统计项
     */
    private void doSettingsStatistic() {
        //翻页动画
        mStatisticTurnPage = ReaderManager.getInstance(getActivity()).getFlipAnimationType();
        //音量键翻页
        mStatisticTurnVolume = ReaderManager.getInstance(getActivity()).isFlipByVolumeKeyEnabled()
                ? 1 : 0;
        //全屏点击翻页
        mStatisticTurnFullscreen = ReaderManager.getInstance(getActivity()).isLeftHandModeEnabled()
                ? 1 : 0;
        //屏保时间
        int shutdownValue = ReaderManager.getInstance(getActivity()).getScreenOffTimeValue();
        if (shutdownValue == ReaderBaseEnum.ScreenProtectTime.Minute2.Time) {
            mStatisticTurnShutdown = 1;
        } else if (shutdownValue == ReaderBaseEnum.ScreenProtectTime.Minute5.Time) {
            mStatisticTurnShutdown = 2;
        } else if (shutdownValue == ReaderBaseEnum.ScreenProtectTime.Minute10.Time) {
            mStatisticTurnShutdown = 3;
        } else if (shutdownValue == ReaderBaseEnum.ScreenProtectTime.Never.Time) {
            mStatisticTurnShutdown = 0;
        }
        //休息提醒
        long restTime = ReaderManager.getInstance(getActivity()).getRestTimeValue();
        if (restTime == ReaderConstant.MILLISECONDS_IN_HOUR) {
            mStatisticTurnRest = 1;
        } else if (restTime == 2 * ReaderConstant.MILLISECONDS_IN_HOUR) {
            mStatisticTurnRest = 2;
        } else if (restTime == 3 * ReaderConstant.MILLISECONDS_IN_HOUR) {
            mStatisticTurnRest = 3;
        } else {
            mStatisticTurnRest = 0;
        }
        //预加载
        FBReaderApp app = (FBReaderApp) ReaderBaseApplication.Instance();
        if (app != null) {
            int preLoadNumber = app.getPrefetchNumber();
            if (preLoadNumber == 6) {
                mStatisticTurnPreload = 1;
            } else if (preLoadNumber == 11) {
                mStatisticTurnPreload = 2;
            } else if (preLoadNumber == 21) {
                mStatisticTurnPreload = 3;
            } else {
                mStatisticTurnPreload = 0;
            }
        }
        //自动切换夜间模式
        mStatisticTurnAutoChange = ReaderManager.getInstance(getActivity()).isAutoSwitchModeEnabled()
                ? 1 : 0;
        logStatisticEvent(StatisticEvent.EVENT_SETTINGS_PAGE,
                "turn_page=" + mStatisticTurnPage
                , "valumn_turn=" + mStatisticTurnVolume
                , "fullscreen_touch_turn=" + mStatisticTurnFullscreen
                , "shutdown_time=" + mStatisticTurnShutdown
                , "rest_notice=" + mStatisticTurnRest
                , "preload=" + mStatisticTurnPreload
                , "auto_display_mode_change=" + mStatisticTurnAutoChange);
    }

    /**
     * 检查feed动画关闭逻辑
     * @return
     */
    public boolean checkFeedTransformClose() {
        if (transformHelper != null && transformHelper.isFeedOepn()) {
            transformHelper.finish();
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void finish() {
        super.finish();
//        if (!mNeedOpenGlobalTTs) {
//            release();
//        }
        release();
    }

    /**
     * 保存书架书籍当前阅读时间：
     * 1.当前阅读器不可见时
     */
    private void saveShelfBookReadTime() {
        final ReaderManagerCallback callback = ReaderManager.getInstance(getActivity())
                .getReaderManagerCallback();
        if (callback != null && callback.isAddedInBookShelf(mBookInfo)) {
            callback.saveShelfBookReadTime(System.currentTimeMillis());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (transformHelper != null && transformHelper.isFeedOepn()) {
            transformHelper.onDestroy();
        }
        BBAReaderComponent.getInstance().clearRealWillToChapterInfo();
        ContainerEventRegister.unregisterBackForegroundEventEvent(this);

        destroyRecordDurationTimer();
        BBAProgressUtil.clearChapterIndexBySize();
        if (mUIHandler != null) {
            mUIHandler.removeCallbacksAndMessages(null);
            mUIHandler = null;
        }
        if (mDismissLoadingRunnable != null) {
            BBAThreadUtils.removeOnUiThread(mDismissLoadingRunnable);
            mDismissLoadingRunnable = null;
        }
        if (topNoticeHandler != null && topNoticeRunnable != null) {
            topNoticeHandler.removeCallbacks(topNoticeRunnable);
        }
        release();
        // 退出时finish比onPause先执行，所以在uplodaAllSenseStatisticsWhenExit方法中下面两个值不能被重置
        AllScenesStatisticUtils.pageDuration = 0;
        AllScenesStatisticUtils.pageReminDuration = 0;
        if (mReaderContainer != null) {
            mReaderContainer.getViewTreeObserver().removeOnGlobalLayoutListener(
                    mContainerGlobalLayoutListener);
        }
    }

    private void release() {
//        ReaderSpeedUtil.clear();
        if (released) {
            return;
        }
//        if (!mNeedOpenGlobalTTs) {
//            uploadReadProgress();
//        }
        uploadReadProgress();
        if (bbaBook != null && bbaBook.isNetBook()) {
            BBABookChapter currentChapter = bbaBook.getCurrentChapter();
            if (currentChapter != null) {
                BBALegalContentCacheManager.saveContent(bbaBook.getBookId(), bbaBook.getCurrentChapterIndex(),
                        bbaBook.getCurrentChapterId(), bbaBook.getCurrentChapterName(),
                        BBAReaderCoreApi.getChapterContent(bbaBook.getBookId(), bbaBook.getCurrentChapterIndex()),
                        currentChapter.isContentCache());
            }
        }
        if (ReaderManager.getInstance(this) != null
                && ReaderManager.getInstance(this).getReaderInitCallback() != null) {
            ReaderManager.getInstance(this)
                    .getReaderInitCallback()
                    .onExitReader(ReaderModelCallback.READER_TYPE_NOVLE
                            , this.hashCode());
        }
        uplodaAllSenseStatisticsWhenExit();
        unregisterLoginCallback();
        unregisterJiLiLadderCallback();
        released = true;
        isFirstCreateReader = true;
        BBAReaderCoreApi.setCreateEngineProxy(null);
        BBAAdUtil.setLastAdState(true);
        BBANetworkChangeUtils.getInstance().unregisterReceiver(this, this);
        reportExitReaderUBC();
        BBAReaderCoreApi.releaseReader();
        BBAAdViewController.getInstance().clearAdCache();


        if (!NovelTtsInterface.getInstance().isOpenPlayer()) {
            BBAReaderCoreApi.releaseTtsParaData();
        }

        BBAEventBus.getInstance().removeEventHandler(REWARD_PLAY_END, readerAdHandler);
        BBAEventBus.getInstance().removeEventHandler(SCROLL_PAGE, readerAdHandler);
        BBAEventBus.getInstance().removeEventHandler(JUMP_CHAPTER_AFTER, readerAdHandler);
        BBAEventBus.getInstance().removeEventHandler(N_FILE_COMPLETED, readerAdHandler);
        BBAEventBus.getInstance().removeEventHandler(RENDER_PAGE_COMPLETED, readerAdHandler);
        BBAEventBus.getInstance().removeEventHandler(DRAW_BITMAP_TO_CANVAS, readerAdHandler);
        BBAEventBus.getInstance().removeEventHandler(SCROLLING_PAGE_CHANGE, readerAdHandler);
        BBAEventBus.getInstance().removeEventHandler(SO_LOAD_FAIL, readerAdHandler);
        // 停止日夜间自动切换服务
        setAutoDayAndNightService(false);
        BBAMenuComponent.getInstance().release();
        try {
            BBAOperateController.getInstance().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BBATTSViewController.getInstance().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BBAReaderProgressController.getInstance().release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        BBAMenuComponent.getInstance().removeAllCallback();
        BBAReaderComponent.getInstance().clearAll(this.mReaderUUID);
        BBAReaderDurationTools.getInstance().release();
        BBAReadTimeManager.getInstance().release();
        try {
            BBAImageLoaderTools.getInstance().release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        BBAReaderBitmapManager.getInstance().clearTitlePageView();
        BBAReaderContainerComponent.getInstance().release();

        // 是否是全局听书初始化请求
//        if (!mNeedOpenGlobalTTs) {
//            final ZLAndroidLibrary zlibrary = getZLibrary();
//            if (zlibrary != null) {
//                zlibrary.clearFBReaderRef();
//            }
//        }
        final ZLAndroidLibrary zlibrary = getZLibrary();
        if (zlibrary != null) {
            zlibrary.clearFBReaderRef();
        }

        if (mPhoneStateManager != null) {
            mPhoneStateManager.stopListening();
            mPhoneStateManager.removeOnPhoneStateChangedListener(mOnPhoneStateChangedListener);
        }
        if (bbaFunctionScreen != null) {
            bbaFunctionScreen.release();
        }
        if (bbaFunctionRestRemind != null) {
            bbaFunctionRestRemind.release();
        }

        if (topNoticeHandler != null && topNoticeRunnable != null) {
            topNoticeHandler.removeCallbacks(topNoticeRunnable);
        }
        if (mReaderContainer != null) {
            mReaderContainer.release();
            mReaderContainer = null;
        }
        // 清除加载的Controller中的Dialog
        if (mBBALoadingController != null) {
            mBBALoadingController.clear();
        }
        if (mBBAEduViewController != null) {
            mBBAEduViewController.clear();
        }
        if (mBbaAutoAddBookShelfController != null) {
            try {
                getLifecycle().removeObserver(mBbaAutoAddBookShelfController);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mBBABubbleGuideController != null) {
            mBBABubbleGuideController.clear();
        }
        if (mBBALoginViewController != null) {
            mBBALoginViewController = null;
        }
        if (mBBAPayViewController != null) {
            mBBAPayViewController = null;
        }
        if (mBBABookMarkController != null) {
            mBBABookMarkController = null;
        }
        BBAFontController.release();

        // 重置阅读器启动打点完成标志位
        ReaderPerfMonitor.hasRecordedReaderStart = false;

        // 清除尾页单例
        LastPageRepository.release();
        // 清除广告cache单例
        ReaderAdViewCache.release();
        // 清除Server下发错误页
        ReaderUtility.clearServerFailInfo();
        // 释放banner动效
        ReaderBannerAdUpdateManager.getInstance().release();
        // 清除handler的callback和message
        mRestHandler.removeCallbacksAndMessages(null);

        // 清除PageToast单例实例
        PageToast.clear();

        try {
            // 隐藏广告视图并释放广告单例
            ReaderAdViewManager.getInstance().requestUpdateAdShowState(HIDE_AD);
            ReaderAdViewManager.release();
            ReaderBannerAdViewManager.release();
            ReaderChapterTailAdViewManager.getInstance().requestUpdateAdShowState(HIDE_AD);
            ReaderChapterTailAdViewManager.getInstance().release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (myFBReaderApp != null && !NovelTtsInterface.getInstance().isVoicePlaying()) {
            myFBReaderApp.cancelPlayTxt();
            myFBReaderApp.cancelListenPhoneState();
        }
        if (myFBReaderApp != null) {
            myFBReaderApp.clearCommentData();
        }

        if (mExecutor != null) {
            mExecutor.shutdown();
            mExecutor = null;
        }

        if (ReaderManager.getInstance(getApplicationContext()).isClosingBook()) {

            // 清除后台数据获取中正在运行的紧急任务
            ZLService service = ZLService.getInstance();
            if (service != null) {
                service.clearTask(ServiceTaskList.LEVEL_IMMEDIATELY, ServiceTaskType.ONLINEIMMEDIATELY);
                service.clearTask(ServiceTaskList.LEVEL_ONLINE);
            }
            LoadResourceListService lrService = LoadResourceListService.getInstance();
            if (lrService != null) {
                lrService.clear();
            }
        }

        // 清除正文文件缓存
        ReaderCleanHelper.clearChapterFiles(mBookInfo);
        final ReaderManagerCallback callback = ReaderManager.getInstance(getActivity())
                .getReaderManagerCallback();
        if (callback != null) {
            callback.onDestroy(getActivity());
            // 通知销毁手百活动任务-阅读时间
            if (NovelBusiActTaskAdapter.isSupportNovelBdActReadTimeTask()) {
                callback.sendNotify(NovelReaderCallbackNotifyType.NOTIFY_DESTROY_BD_ACT_READ_TIME_TASK, novelBdTaskObj);
            }
            JSONObject jsonObj = new JSONObject();
            // mBookType 正版 或者劫持转码
            try {
                jsonObj.put("mBookType", "novel");
                jsonObj.put("mIsLocalTxt", isLocalBook());
            } catch (Exception e) {
                e.printStackTrace();
            }
            callback.sendNotify(NovelReaderCallbackNotifyType.NOTIFY_IS_EXIT_READER_CLEAR, jsonObj);
        }
        // 适配lite
        if (NovelBusiActTaskAdapter.isSupportNovelBdActReadTimeTaskForLite()) {
            if (novelBdTaskForLite != null) {
                novelBdTaskForLite.destroyTask();
                novelBdTaskForLite = null;
            }
        }
        ReaderBookTailThanksViewManager.getInstance().clearView();
        // 退出阅读器 进行字级打点
        doFontLevelStatic();

        // 冷启打点，退出阅读器
        if (myFBReaderApp != null) {
            if (myFBReaderApp.getColdLaunchStatus()) {
                NovelLrManager.onPageClose(NovelLrManager.PageId.NOVEL);
                myFBReaderApp.setColdLaunchStatus(false);
            }
        }

        // 阅读速度置为null
        if (myFBReaderApp != null) {
            myFBReaderApp.setReadVelocity(null);
            // 这个地方为尝试解决内存泄露，但会影响别的使用getBook的地方空指针，暂不打开
            // myFBReaderApp.resetBookInfo();
        }
        // 防止内存泄露，进行action的移除
        removeAppAtion();
        // 如果Activity销毁，则释放wake lock，防止增加功耗
        releaseWakeLock();
        // Lite5.10 退出阅读器清空Feed小说阅读器标记
        ReaderUtility.setIsFromFeedNovelReader(false);
        // 退出阅读器清空Feed小说阅读器用户主动切换动画标记
        ReaderUtility.setUserSwitchFeedNovelAnimation(false);
        // Lite5.10~
        NovelAdResolver.getInstance().release();
        BBAReaderRuntime.removeReaderContext(readerContext);

        String fontFamily = BBAFontController.getFontFamily();
        if (!TextUtils.isEmpty(fontFamily) && !TextUtils.equals(fontFamily, FONT_DEFAULT)) {
            if (callback != null) {
                callback.sendNotify(NovelReaderCallbackNotifyType.NOTIFY_STOP_FONT_TRAIL_TIMER, null);
            }
        }

        // 设置页教育机制监听销毁
        BBASettingManager.getInstance().unRegisterSettingChangeListener(this.toString());
        BBASwitchChapterUBCEventTools.release();
        BBAAdUtil.clearFirstChapterIndex();
        BBAReaderGestureManager.getInstance().release();
    }

    /**
     * 退出阅读器的时候，上报一次翻页
     */
    private void uplodaAllSenseStatisticsWhenExit() {
        BBABook book = BBAReaderComponent.getInstance().getBook();
        if (book != null) {
            String oldPageType = AllScenesStatisticUtils.currentPageType;
            if (oldPageType != null) {
                String adType = "";
                if (TextUtils.equals("ad", oldPageType)) {
                    adType = BBAAdViewController.getInstance().getInnerAdType();
                }
                long currentTimeMillis = SystemClock.uptimeMillis();
                AllScenesStatisticUtils.ubc580ByTurnPage(
                        String.valueOf(currentTimeMillis - AllScenesStatisticUtils.pageDuration
                                + AllScenesStatisticUtils.pageReminDuration),
                        "", oldPageType, adType,
                        "");
                AllScenesStatisticUtils.pageDuration = 0;
                AllScenesStatisticUtils.pageReminDuration = 0;
            }
        }
    }

    public void cancelAdViewForceStop() {
        if (mReaderContainer != null) {
            mReaderContainer.cancelAdViewForceStop();
        }
    }

    /***
     * 退出阅读器进行字级打点
     */
    private void doFontLevelStatic() {
        ZLTextStyleCollection sc = ZLTextStyleCollection.getInstance();
        // Lite5.10 新增Feed小说阅读器模式,退出阅读器字体打点做同步
        if (sc != null) {
            ZLIntegerRangeOption option = sc.TextStyleOption;
            if (ReaderUtility.isFromFeedNovelReader()) {
                option = sc.mFeedNovelReaderTextStyleOption;
            }
            if (option != null) {
                // 当前的字级
                int fontLevel = option.getValue();
                StatisticUtils.ubcFontLevel("novel", fontLevel + 1);
            }
        }
        // Lite5.10~
    }

    @Override
    public void onLowMemory() {
        myFBReaderApp.onWindowClosing();
        super.onLowMemory();
    }

    private int mKeyUnderTracking = -1;
    private long mTrackingStartTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getRepeatCount() == 0) {
            mIsKeyDownStatus = true;
        }

        if (keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
                && keyCode != KeyEvent.KEYCODE_VOLUME_UP) {
            // 尝试清除正文页中的Toast
            PageToast.cancelToast();
        }

        if (mBBALoadingController != null
                && mBBALoadingController.isShowing()) {
            // 在Loading状态时，点击取消
            return true;
        }

        if (isMenuAtShow() && keyCode != KeyEvent.KEYCODE_MENU && keyCode != KeyEvent.KEYCODE_BACK) {
            // 当菜单在展示状态时，只拦截Menu和Back按键
            return super.onKeyDown(keyCode, event);
        }
        if (myFBReaderApp != null) {
            // 截获手机的Menu键按下
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                if (myFBReaderApp.getCurrentView() == myFBReaderApp.BookTextView && mCanChangeMenu) {
                    mCanChangeMenu = false;
                    if (isMenuAtHide()) {
                        myFBReaderApp.runAction(ActionCode.SHOW_MENU);
                    } else if (isMenuAtShow()) {
                        myFBReaderApp.runAction(ActionCode.HIDE_MENU);
                    }
                    return true;
                }
            }

            if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) &&
                    !ReaderUtility.isVoicePlaying() &&
                    BBAReaderConfig.getInstance().isMoreSettingIsVoiceTurnPage() &&
                    !NovelTtsInterface.getInstance().isPlaying()) {
                // 当前全局听书非正在朗读
                return true;
            }

            if ((myFBReaderApp.hasActionForKey(keyCode, true)
                    || myFBReaderApp.hasActionForKey(keyCode, false))
                    && BBAReaderConfig.getInstance().isMoreSettingIsVoiceTurnPage()) {
                if (mKeyUnderTracking != -1) {
                    if (mKeyUnderTracking == keyCode) {
                        return true;
                    } else {
                        mKeyUnderTracking = -1;
                    }
                }
                if (myFBReaderApp.hasActionForKey(keyCode, true)) {
                    mKeyUnderTracking = keyCode;
                    mTrackingStartTime = System.currentTimeMillis();
                    return true;
                } else {
                    // 阅读器展示时，并且没有朗读时，音量键按下时不生效
                    return myFBReaderApp.runActionByKey(keyCode, false);
                }
            } else {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 新阅读器，tts翻到下一页
     */
    public void ttsGotoNextPage() {
        if (mReaderContainer != null) {
            mReaderContainer.ttsGogoNextPage();
        }
    }

    public BBAAutoAddBookShelfController getBBAAutoAddBookShelfController() {
        return mBbaAutoAddBookShelfController;
    }

    /**
     * 设置是否变成仅仅点击菜单模式
     * 不能滑动切换页面、不能点击切换页面，但是可以点击屏幕中间出菜单模式
     *
     * @param enable true:是这种模式  false：退出这种模式
     */
    public void setOnlyClickCenterMode(boolean enable) {
        if (mReaderContainer != null) {
            mReaderContainer.setOnlyClickCenterMode(enable);
        }
    }

    /**
     * 设置是否变成强制播放插屏广告模式
     *
     * @param enable true:是这种模式  false：退出这种模式
     */
    public void setForceInsertAdMode(boolean enable) {
        if (mReaderContainer != null) {
            mReaderContainer.setForceInsertAdMode(enable);
        }
    }

    /**
     * 支持滑动屏幕边缘退出阅读器
     *
     * 注意：该方法通过 onBackPressed() 实现退出阅读器功能，后续有需要重写 onBackPressed() 方法时，可将此处改为 finish()
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isFirstBook()) {
            return super.dispatchTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // 记录落点坐标
                mTouchStartX = ev.getRawX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                float rawX = ev.getRawX();
                // 移动距离
                float moveX = rawX - mTouchStartX;
                // 落点落在感知区域且滑动范围大于阈值，退出
                if (mTouchStartX < slideSensorArea && moveX > slideThreshold) {
                    onBackPressed();
                    return true;
                }
                break;
            }
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (!mayDismissSomeMenu()) {
            super.onBackPressed();
            return;
        }
    }

    private boolean isFirstBook() {
        return mReaderContainer != null && mReaderContainer.isBookFirst();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean isKeyDownStatus = mIsKeyDownStatus;
        mIsKeyDownStatus = false;

        if (!isKeyDownStatus) {
            return true;
        }

        if (keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
                && keyCode != KeyEvent.KEYCODE_VOLUME_UP) {
            // 尝试清除正文页中的Toast
            PageToast.cancelToast();
        }
        if (mBBALoadingController != null && mBBALoadingController.isShowing()) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                // 在Loading状态时，点击取消
                mBBALoadingController.cancel();
            }
            return true;
        }

        if (!isMenuAtHide() && keyCode != KeyEvent.KEYCODE_MENU && keyCode != KeyEvent.KEYCODE_BACK) {
            // 当菜单在非隐藏状态时，只拦截Menu和Back按键
            return super.onKeyUp(keyCode, event);
        }

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            mCanChangeMenu = true;
        }
        if (myFBReaderApp != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                // 检查是否有弹起的menu，如果有，除了 main menu，其它 menu 都收起
                if (mayDismissSomeMenu()) {
                    return true;
                }
                if (mRestView != null && mRestView.isShown()) {
                    mRestView.setVisibility(View.GONE);
                    mRestHandler.removeCallbacks(mRefreshRunnable);
                    return true;
                }
                // 拦截Back事件，只是在全屏状态下拦截
                if (myFBReaderApp.getCurrentView() == myFBReaderApp.BookTextView
                        && !isMenuAtHide()
                        && BBANavigationBarUtils.getNavBarHeight(this) <= 0
                        && !isFirstBook()) {
                    BBAMenuComponent.getInstance().dismissAllMenu();
                    return true;
                }

                if (isKeyDownStatus) {
                    // 退出阅读器，清除自动翻页标志位
                    BBAAutoScrollHelper.getInstance().clearAutoScrollSigns();
                    FBReaderApp app = (FBReaderApp) ReaderBaseApplication.Instance();
                    if (app != null) {
                        // 这个方法中，可能会弹出挽留弹窗
                        app.closeWindow();
                    }
                }
                return true;
            }

            if (mKeyUnderTracking != -1) {
                if (mKeyUnderTracking == keyCode) {
                    final boolean longPress =
                            System.currentTimeMillis() > mTrackingStartTime + ViewConfiguration.getLongPressTimeout();
                    myFBReaderApp.runActionByKey(keyCode, longPress);
                }
                mKeyUnderTracking = -1;
                return true;
            } else {
                if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    if (BBAReaderConfig.getInstance().isMoreSettingIsVoiceTurnPage()) {
                        // 1、 检查当前全局听书非正在朗读
                        if (ReaderUtility.isVoicePlaying() || NovelTtsInterface.getInstance().isPlaying()) {
                            return true;
                        }
                        // 2、 检查当前是否是自动翻页
                        if (BBAAutoScrollHelper.sIsAutoScrolling) {
                            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                                BBAAutoScrollHelper.getInstance().decreaseAutoReadSpeed();
                            } else {
                                BBAAutoScrollHelper.getInstance().increaseAutoReadSpeed();
                            }
                            return true;
                        }
                        // 3、音量翻页逻辑
                        if (!BBAReaderGestureManager.getInstance().isForceInsertAdMode()) {

                            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                                // 判断是否到达书尾
                                if (mReaderContainer != null) {
                                    boolean isBookTail = mReaderContainer.isBookTail();
                                    if (isBookTail) {
                                        LastPageManager.callShowLastPage();
                                    }
                                    mReaderContainer.turnNextPage();
                                }
                            } else {
                                // 判断是否到达书首
                                if (mReaderContainer != null && mReaderContainer.isBookFirst()) {
                                    if (!BBAClickUtils.isFastClick("show_first_page", 3 * 1000)) {
                                        ReaderUtility.toast(getResources().getString(R.string.bba_turn_first_page));
                                    }
                                }
                                if (mReaderContainer != null) {
                                    mReaderContainer.turnLastPage();
                                }
                            }
                        } else {
                            // 强制状态，激励插页处理
                            ReaderUtility.notifyHost(ReaderConstant.NOVEL_AD_PAGE_FORCE_MOVE_OTHER_KEY, "");
                        }
                        return true;
                    } else {
                        return super.onKeyUp(keyCode, event);
                    }
                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private void updateMainMenuButtonUI() {
        IBBAMenuControll controller = BBAMenuComponent.getInstance().getMenuController();
        if (controller instanceof BBAMenuController) {
            ((BBAMenuController) controller).updateSettingsButtonView();
        }
    }

    private boolean mayDismissSomeMenu() {
        Log.d("wangwang", "fbreader mayDismissSomeMenu() 0");
        if (BBAMenuComponent.getInstance().isCertainMenuShowingCheck(BBAMenuType.DIRECTORY)) {
            if (mUIHandler != null) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BBAMenuComponent.getInstance().showMenuReplace(BBAMenuType.MAIN);
                        updateMainMenuButtonUI();
                    }
                });
                return true;
            }
        }
        if (BBAMenuComponent.getInstance().isCertainMenuShowingCheck(BBAMenuType.MORE)) {
            if (mUIHandler != null) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        IBBAMenuControll controller = BBAMenuComponent.getInstance().getMenuController();
                        if (controller instanceof BBAMenuController) {
                            ((BBAMenuController) controller).dismiss();
                        }
                    }
                });
                return true;
            }
        }

        if (BBAMenuComponent.getInstance().isCertainMenuShowingCheck(BBAMenuType.MORE_SETTING)) {
            if (mUIHandler != null) {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        IBBAMenuControll controller = BBAMenuComponent.getInstance().getMenuController();
                        if (controller instanceof BBAMenuController) {
                            ((BBAMenuController) controller).dismiss();
                        }
                        mUIHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("wangwang", "showMenuReplace() main");
                                BBAMenuComponent.getInstance().showMenu();
//                                BBAMenuComponent.getInstance().showMenuReplace(BBAMenuType.MAIN);
                                BBAMenuComponent.getInstance().showSettingMenu(false);
                                updateMainMenuButtonUI();
                            }
                        }, 200);
                    }
                });
                return true;
            }
        }

        if (BBAMenuComponent.getInstance().isCertainMenuShowingCheck(BBAMenuType.MAIN)) {
            // 下面的没问题这个 if 分支没问题
            if (BBAMenuComponent.getInstance().isCertainMenuShowingCheck(BBAMenuType.SETTING)) {
                // 收回 setting
                if (mUIHandler != null) {
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            IBBAMenuControll controller = BBAMenuComponent.getInstance().getMenuController();
                            if (controller instanceof BBAMenuController) {
                                ((BBAMenuController) controller).dismiss();
                            }
                        }
                    });
                    return true;
                }
            } else {
                // 检查 字体选择对话框是否展示
                boolean isFontSelectViewShown = false;
                Window window = getWindow();
                if (window != null) {
                    View decorView = window.getDecorView();
                    if (decorView instanceof ViewGroup) {
                        ViewGroup decorViewGroup = (ViewGroup) decorView;
                        int count = decorViewGroup.getChildCount();
                        // 遍历子view，最多检查三个
                        for (int i = 1; i < 4; i++) {
                            int index = count - i;
                            if (index < 0) {
                                break;
                            }
                            View view = decorViewGroup.getChildAt(index);
                            if (view instanceof IDismissable
                                    && "Novel_Font_Select_Popup_View".equals(view.getContentDescription())) {
                                ((IDismissable) view).dismiss();
                                isFontSelectViewShown = true;

                                if (mUIHandler != null) {
                                    mUIHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            BBAMenuComponent.getInstance().showSettingMenu();
                                        }
                                    }, 30);
                                    return true;
                                }
                                break;
                            }
                        }
                    }
                }
                if (isFontSelectViewShown) {
                    return true;
                }
                return false;
            }
        }


        return false;
    }

    /**
     * 开启/关闭物理键盘背光
     *
     * @param enabled
     */
    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.FROYO)
    private void setButtonLight(boolean enabled) {
        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        float brightness = enabled ? -1.0f : 0.0f;
        if (APIUtils.hasFroyo()) {
            attrs.buttonBrightness = brightness;
        } else {
            try {
                final Class<?> cls = attrs.getClass();
                final Field fld = cls.getField("buttonBrightness");
                if (fld != null && "float".equals(fld.getType().toString())) {
                    fld.setFloat(attrs, brightness);
                }
            } catch (NoSuchFieldException e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
            } catch (IllegalAccessException e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
            }
        }
        getWindow().setAttributes(attrs);
    }

    /**
     * 设置屏幕亮度为使用系统亮度
     */
    public void setScreenBrightnessAuto() {
        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.screenBrightness = -1.0f;
        getWindow().setAttributes(attrs);
        myFBReaderApp.setSysBrightness(true);
    }

    /**
     * 设置屏幕亮度
     *
     * @param percent 值从0到100，从dark到light
     */
    public void setScreenBrightness(int percent) {
        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (percent > 100) {
            percent = 100;
        }
        if (percent < 0) {
            percent = 0;
        }
        // percent的值范围为0-100
        // 针对Coolpad手机在亮度值较小时会出现黑屏的诡异问题，现将范围调整到0.1-1
        // todo: 去掉特殊机型的处理。如果亮度太低导致黑屏，用户可选择调高亮度
        attrs.screenBrightness = (float) percent * 0.01f;
        getWindow().setAttributes(attrs);
    }

    /**
     * 获取当前屏幕亮度
     *
     * @return 屏幕亮度（0-100）
     */
    public int getScreenBrightness() {
        int value = 0;
        try {
            ContentResolver cr = getContentResolver();
            value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
            value = (int) (value * 100.0 / 255.0);
        } catch (Exception e) {
            value = 50;
        }
        return value;

    }

    /**
     * 显示菜单工具栏,10.8新增：在自动翻页中，不显示工具栏，只toast提示
     */
    public void showMainMenuWithAutoBuy() {
        FBReaderApp fbReaderApp = (FBReaderApp) ReaderBaseApplication.Instance();
        if (fbReaderApp != null && fbReaderApp.isVoicePlaying()) {
            Chapter chapterInfo = fbReaderApp.getCurrentChapterInfo();
            VoicePlayManager voicePlayManager = fbReaderApp.myVoicePlayManager;
            if (chapterInfo != null && voicePlayManager != null) {
                voicePlayManager.setNeedPlayAutoBuyWord("已自动购买" + chapterInfo.getTitle());
            }
            return;
        }
        // 对齐ios 非自动播放也需要弹toast提示
        String autoBuyTipInAutoScroll = getResources().getString(R.string.bdreader_auto_buy_auto_scroll);
        ReaderUtility.toast(autoBuyTipInAutoScroll);
        doShowMainMenuWithAutoBuy();
    }

    public void doShowMainMenuWithAutoBuy() {
        if (mBBABubbleGuideController != null) {
            mBBABubbleGuideController.setShowAutoBuyBubble(true);
        }
    }

    /**
     * 显示菜单工具栏
     */
    public void showMenu() {
    }

    /**
     * 隐藏菜单工具栏
     */
    public void hideMenu() {
        // 显示从本页读
        if (!mRestHandler.hasMessages(MSG_UPDATE_READ_CURRENT_PAGE)) {
            mRestHandler.sendEmptyMessageDelayed(MSG_UPDATE_READ_CURRENT_PAGE, AnimationFactory.REAL_DURATION_1);
        }
    }

    public void performTtsButtonClick() {
    }

    /**
     * 更新Tts入口的状态
     */
    public void refreshTtsEntranceStatus() {
    }

    /**
     * 菜单工具栏正在显示状态
     */
    public boolean isMenuAtShow() {
        return BBAMenuComponent.getInstance().isMenuShowing();
    }

    /**
     * 菜单工具栏正在隐藏状态
     */
    public boolean isMenuAtHide() {
        return !BBAMenuComponent.getInstance().isMenuShowing();
    }

    /**
     * 听完本章停止阅读
     */
    public void onSpeechChapterTrailStop() {
        BBAReaderBarItemModel ttsFooterView = BBAMenuComponent.getInstance().getTTSFooterView();
        if (ttsFooterView != null && ttsFooterView instanceof BBAMenuTTSModel) {
            BBAMenuTTSModel ttsModel = (BBAMenuTTSModel) ttsFooterView;
            if (ttsModel != null && ttsModel.itemView != null && ttsModel.itemView instanceof SpeechControlMenuView) {
                SpeechControlMenuView speechView = (SpeechControlMenuView) ttsModel.itemView;
                speechView.onEndChapterTrailTts();
            }
        }
    }

    /**
     * 设置Android 6.0以上手机的屏保时间，在每次翻页操作之后重新获取屏幕常亮锁
     *
     * @param time
     */
    public void setScreenProtectTimeForAndroidM(final long time) {
        int permissionCheck = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck = this.checkSelfPermission(Manifest.permission.WRITE_SETTINGS);
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // Android M 版本将WRITE_SETTINGS权限定义为极其危险权限。
            // 如果用户手机在Android M 版本以上，且此时没有WRITE_SETTINGS权限，
            // 我们就用定时常亮的办法去变相设置屏保时间
            acquireWakeLock();
            Message msg = Message.obtain();
            msg.what = MSG_END_SCREEN_PROTECT;
            mRestHandler.sendMessageDelayed(msg, time);

        }
    }

    /**
     * 获取屏幕常亮锁
     */
    @SuppressLint("WakelockTimeout")
    public void acquireWakeLock() {
        // 如果Activity销毁，则不允许获取wake lock，防止增加功耗
        if (isDestroyed()) {
            return;
        }
        if (mWakeLock == null) {
            return;
        }
        if (mRestHandler != null) {
            mRestHandler.removeMessages(MSG_END_SCREEN_PROTECT);
        }
        if (mWakeLock.isHeld()) {
            return;
        }
        mWakeLock.acquire();
    }

    /**
     * 释放屏幕常亮锁
     */
    public void releaseWakeLock() {
        Message msg = Message.obtain();
        msg.what = MSG_END_SCREEN_PROTECT;
        mRestHandler.sendMessage(msg);
    }

    /**
     * 重新加载在线目录数据
     */
    public void reloadOnlineDirectory() {
    }

    /**
     * 异步线程执行方法
     *
     * @param runnable 需要抛到异步线程执行的runnable
     */
    public void asyncExecute(Runnable runnable) {
        if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
            mExecutor = Executors.newSingleThreadExecutor();
        }
        // 崩溃icafe：http://newicafe.baidu.com/issue/BaiduSearchAndroid-50379/show?from=page
        // 崩溃堆栈表明：在线程池terminated或shutting down的过程中，新增的task被拒绝执行
        // 少数的3个崩溃堆栈又表明：该exception发生的路径为记录阅读器错误页状态的统计处，
        // 猜测是退出阅读器之后通知阅读器展示错误页的路径上发生的，所以这里直接try catch，而不影响功能
        try {
            mExecutor.execute(runnable);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setInFloatGuide(boolean mInFloatGuide) {
        this.mInFloatGuide = mInFloatGuide;
        // 如果有保命弹框，则通知全局运营位当前不能显示
        ReaderManagerCallback callback = ReaderManager.getInstance(getApplicationContext())
                .getReaderManagerCallback();
        if (callback != null) {
            callback.sendNotify(
                    NOTIFY_SET_GLOBAL_OPERATION_VIEW_DIALOG_IS_SHOW, mInFloatGuide);
        }
    }

    public boolean isNightMode() {
        if (myFBReaderApp != null) {
            return myFBReaderApp.isNightMode();
        }
        return false;
    }

//    public boolean isNeedOpenGlobalTTs() {
//        return mNeedOpenGlobalTTs;
//    }

    public boolean isUserEduShown() {
        UserEduView userEduView = findViewById(R.id.reader_hot_area_user_edu_framelayout);
        if (userEduView != null) {
            return userEduView.getVisibility() == View.VISIBLE;
        }
        return false;
    }

    /**
     * 判断其他浮层是否正在显示
     */
    public boolean isOtherFloatingShow() {
        // 菜单栏正在显示状态
        if (isMenuAtShow()) {
            return true;
        }
        // 加载状态正在显示
        if (mBBALoadingController != null
                && mBBALoadingController.isShowing()) {
            return true;
        }

        // 保命弹窗正在显示
        return mInFloatGuide;
    }

    public BookInfo getBookInfo() {
        if (mBookInfo != null && TextUtils.isEmpty(mBookInfo.getCurrentChapterName())) {
            if (myFBReaderApp != null) {
                String currentChapterTitle = myFBReaderApp.getCurrentChapterTitle();
                mBookInfo.setCurrentChapterName(currentChapterTitle);
            }
        }
        return mBookInfo;
    }

    private void initReadCurrentPageView() {
        if (!NovelBusTTSAdapter.isSupportTts()) {
            // 不支持 tts
            return;
        }

        mBottomReadCurrentPageMenuStub = findViewById(R.id.read_current_page_click_area_stub);
    }

    /**
     * 是否已经加入到了书架
     *
     * @return true-加入到了书架
     */
    public boolean isAddedInBookShelf() {
        ReaderManagerCallback readerManagerCallback = ReaderUtility.getReaderManagerCallback();
        if (readerManagerCallback == null || mBookInfo == null || TextUtils.isEmpty(mBookInfo.getId())) {
            return false;
        }
        return readerManagerCallback.isAddedInBookShelf(mBookInfo);
    }

    /**
     * 11.20 设置当小说已经加入书架时，右上角书架按钮文案
     */
    private void setBookShelfBtnText() {
        if (bbaTTSCenterModel != null) {
            bbaTTSCenterModel.setShelfViewInnerText(FBReader.this, getString(R.string.bdreader_go_shelf));
            bbaTTSCenterModel.setShelfViewOnClickListener(FBReader.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FBReaderApp mFBReaderApp = (FBReaderApp) ReaderBaseApplication.Instance();
                    if (mFBReaderApp == null) {
                        mFBReaderApp = new FBReaderApp(FBReader.this, null);
                    }
                    if (mFBReaderApp != null && mFBReaderApp.isVoicePlaying()) {
                        // 若正在播放TTS，通知弹出提示框
                        ReaderManagerCallback callback = getReaderManagerCallback();
                        if (callback != null) {
                            callback.sendNotify(NOTIFY_ILLEGAL_SHOW_STOP_TTS_FOR_GO_BOOK_SHELF_DLG, null);
                            return;
                        }
                    }

                    if (mFBReaderApp != null) {
                        mFBReaderApp.cancelPlayTxt();
                    }

                    StatisticUtils.ubcAddShelfOnClick(mBookInfo, UBC_SOURCE_GOTO_SHELF);
                    RouterProxy.goBookShelf(FBReader.this);
                }
            });
        }
    }

    /**
     * 加入书架
     *
     * @return 是否加入成功
     */
    public boolean addToBookShelf() {
        ReaderManagerCallback readerManagerCallback = ReaderManager.getInstance(this).getReaderManagerCallback();
        if (readerManagerCallback == null) {
            return false;
        }
        return readerManagerCallback.addToBookShelf(mBookInfo);
    }

    private void centerAddShelfClick() {
        StatisticUtils.ubcAddShelfOnClick(mBookInfo, UBC_SOURCE_ADDSHELF);
        if (mBookInfo != null
                && mBookInfo.getPiratedWebsiteReadExp()
                && TextUtils.isEmpty(mBookInfo.getId())) {
            // GID 仍然未获取到，最后一次重试
            final ReaderManagerCallback callback = getReaderManagerCallback();
            if (callback != null) {
                callback.requestHijack(mBookInfo, new NovelRequestListener() {
                    @Override
                    public void onSuccess(String s) {
                        if (!TextUtils.isEmpty(s)) {
                            myBook.setNovelId(s);
                            mBookInfo.setId(s);
                            addToBookShelf();
                            if (callback != null) {
                                JSONObject jsonObj = new JSONObject();
                                try {
                                    if (mBookInfo != null) {
                                        jsonObj.put("gid", mBookInfo.getId());
                                        jsonObj.put("cid", mBookInfo.getCurrentChapterId());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                callback.sendNotify(SHOW_ADD_TO_BOOK_SHELF_SUCCESS_TOAST_OR_DLG,
                                        jsonObj);
                            }
                            updateReadCurrentMenu();
                        }
                    }

                    @Override
                    public void onFail(String s) {
                    }
                });
            }
        } else {
            addToBookShelf();
            ReaderManagerCallback callback = ReaderUtility.getReaderManagerCallback();
            if (callback != null) {
                // 通知主工程执行添加书架的操作
                callback.sendNotify(NOTIFY_EXECUTE_LEGAL_ADD_SHELF, null);

                JSONObject jsonObj = new JSONObject();
                try {
                    if (mBookInfo != null) {
                        jsonObj.put("gid", mBookInfo.getId());
                        jsonObj.put("cid", mBookInfo.getCurrentChapterId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                callback.sendNotify(SHOW_ADD_TO_BOOK_SHELF_SUCCESS_TOAST_OR_DLG, jsonObj);
            }

            updateReadCurrentMenu();
        }
    }

    /**
     * 更新阅读器页底部从本页读和加入书架
     */
    public void updateReadCurrentMenu() {
        ThreadUtils.runOnAsyncThread(new Runnable() {
            @Override
            public void run() {
                final boolean addedInBookShelf = isAddedInBookShelf();
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (addedInBookShelf) {
                            setBookShelfBtnText();
                        } else {
                            if (bbaTTSCenterModel != null) {
                                bbaTTSCenterModel.setShelfViewInnerText(FBReader.this,
                                        getString(R.string.bdreader_add_shelf));
                                bbaTTSCenterModel.setShelfViewOnClickListener(FBReader.this,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                centerAddShelfClick();
                                            }
                                        });
                            }
                        }
                    }
                });
            }
        });

        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bbaMainCenterModel != null) { // 主菜单的加入书架model
                    View view = bbaMainCenterModel.getMenuItemView(FBReader.this);
                    if (view != null) {
                        if (!NovelBusTTSAdapter.isSupportTts()) {
                            // 不支持 tts
                            return;
                        }

                        if (ReadCurrentPageView.needShow()) {
                            showReadCurrentView();
                        } else {
                            hideReadCurrentView();
                        }
                    }
                }
            }
        });
    }

    public void showReadCurrentView() {
        if (!NovelBusTTSAdapter.isSupportTts()) {
            // 不支持 tts
            return;
        }
        if (mBottomReadCurrentPageMenu == null && mBottomReadCurrentPageMenuStub != null) {
            try {
                View inflate = mBottomReadCurrentPageMenuStub.inflate();
                mBottomReadCurrentPageMenu = inflate.findViewById(R.id.read_current_page_click_area);
                if (mBottomReadCurrentPageMenu != null) {
                    ViewGroup.LayoutParams layoutParams = mBottomReadCurrentPageMenu.getLayoutParams();
                    if (layoutParams instanceof RelativeLayout.LayoutParams) {
                        RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) layoutParams;
                        relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM
                                | RelativeLayout.CENTER_HORIZONTAL);
                        relativeLayoutParams.setMargins(0, 0, 0, BBADeviceUtil.ScreenInfo.dp2px(7f));
                        mBottomReadCurrentPageMenu.setLayoutParams(relativeLayoutParams);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mBottomReadCurrentPageMenu != null) {
            mBottomReadCurrentPageMenu.updateOnShow(mBannerAdLayout);
            mBottomReadCurrentPageMenu.show(null);
        }
    }

    private void hideReadCurrentView() {
        if (!NovelBusTTSAdapter.isSupportTts()) {
            // 不支持 tts
            return;
        }

        if (mBottomReadCurrentPageMenu != null) {
            mBottomReadCurrentPageMenu.hide();
        }
    }

    public void onTurnPage() {
        updateReadCurrentMenu();
    }


    public void readingInCurrentPage() {
        if (!mRestHandler.hasMessages(MSG_READING_IN_PAGE)) {
            mRestHandler.sendEmptyMessage(MSG_READING_IN_PAGE);
        }
    }

    /**
     * 更新右上角运营位
     *
     * @param isUpdateCurrentPage 是否只更新当前页面
     */
    public void updateRightTopOperateView(boolean isUpdateCurrentPage) {
        if (mReaderContainer != null) {
            mReaderContainer.updateRightTopOperateView(isUpdateCurrentPage, false);
        }
    }

    /**
     * 当右上角运营为变化了回调
     */
    public void onUpdateRTOperationView(String params) {
        BBARightTopViewListenerManager.getInstance().dispatchUpdate(params);
    }

    /**
     * 更新章尾评论数据回调
     *
     * @param jsonData
     */
    public void onUpdateChapterTailCommend(final String jsonData) {
        BBAThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(jsonData)) {
                    if (mReaderContainer != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(jsonData);
                            String cid = jsonObject.optString("cid");
                            String gid = jsonObject.optString("gid");
                            if (!TextUtils.isEmpty(cid) && !TextUtils.isEmpty(gid)) {
                                BBAReaderContainerItem item = mReaderContainer.getAroundCurrentItem(0);
                                if (item != null && item.page != null && item.page.haveComment()
                                        && gid.equals(mBookInfo.getId()) && cid.equals(item.chapterId)) {
                                    mReaderContainer.updateItemStatus();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
     * 添加空白页面，业务层自己实现
     */
    public void addChapterPage(int fileIndex, final String chapterId, int resultCode) {
        if (mReaderContainer != null) {
            mReaderContainer.addChapterPage(fileIndex, chapterId, resultCode);
        }
        updateBannerState();
    }

    /**
     * 获取mBBAPayViewController
     *
     * @return
     */
    public BBAPayViewController getBBAPayViewController() {
        return mBBAPayViewController;
    }

    /**
     * 更新loading item状态
     */
    public void updateLoadingItemStatus(final int publicStatus, final int chapterIndex) {
        BBAThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissLoading();
                if (mReaderContainer != null) {
                    int pageStatus;
                    // 1=在线，2=其他原因下架，3=黄反
                    if (publicStatus == 2) {
                        pageStatus = BBAReaderContainerItem.BBA_PAGE_LOAD_STATUS_OFF_THE_SHELF;
                    } else if (publicStatus == 3) {
                        pageStatus = BBAReaderContainerItem.BBA_PAGE_LOAD_STATUS_HUANG_FAN;
                    } else {
                        pageStatus = BBAReaderContainerItem.BBA_PAGE_LOAD_STATUS_FAILED;
                    }

                    // 错误也文案
                    String errorContent = ReaderUtility.getIntervalDoc();
                    // 错误也按钮文案
                    String btnContent = ReaderUtility.getIntervalBtnDoc();
                    // 错误页文案
                    if (!ReaderUtility.isIntervalDocEmpty()
                            && !TextUtils.isEmpty(ReaderUtility.getIntervalBtnDoc())) {
                        if (btnContent.contains("%s")) {
                            int intervalTime = Math.max(ReaderUtility.getIntervalTime(), 0);
                            btnContent = String.format(btnContent, intervalTime);
                        }
                        if ("前往下章".equals(btnContent)) {
                            // 下架章节-前往下章展现打点
                            ReaderManagerCallback rCallback =
                                    ReaderManager.getInstance(FBReader.this).getReaderManagerCallback();
                            if (rCallback != null) {
                                rCallback.sendNotify(NOTIFY_LEGAL_READER_XIAJIA_CHAPTER_SHOW, null);
                            }
                            // 因为内容问题tts停止标志
                            FBReaderApp app = ReaderUtility.getFBReaderApp();
                            if (app != null) {
                                if (ReaderUtility.isVoicePlaying() && app.getCurrentChapterIndex() ==
                                        VoicePlayHelper.getInstance().getChapterIndex()) {
                                    VoicePlayHelper.getInstance().setChapterIndex(-1);
                                    // 当前下架章节正好是tts停止章节， 播放提示
                                    FBReaderApp.ITtsNotReadyCallBack callBack = app.getITtsNotReadyCallBack();
                                    if (callBack != null) {
                                        if (!VoicePlayHelper.getInstance().getReadFinishSoldDown()) {
                                            callBack.call(",因部分内容不符合国家相关法律法规要求，本章已下架，即将为您朗读下一章未下架内容",
                                                    VoicePlayManager.sPlayIdForSoldOut);
                                        }
                                        // 初始化回调内容
                                        app.ttsNotReadyChapterIndex = -1;
                                        app.setITtsNotReadyCallBack(-1, null);
                                    }
                                }
                            }
                        }
                    }
                    // 更新item信息
                    mReaderContainer.updateLoadingItemStatus(pageStatus, chapterIndex, errorContent, btnContent, "");
                }
            }
        });
    }

    public BBAPage getCurrentPage() {
        if (mReaderContainer != null) {
            return mReaderContainer.getCurrentPage();
        }
        return null;
    }

    /**
     * 判断给定的字是否在屏幕内显示
     *
     * @param bookId       书籍id
     * @param chapterIndex 章节index
     * @param paraIndex    段落index
     * @param wordIndex    段落中的字index
     * @return boolean
     */
    public boolean isInScreenForTtsWord(String bookId, int chapterIndex, int paraIndex, int wordIndex) {
        if (mReaderContainer != null) {
            return mReaderContainer.isInScreenForTtsWord(bookId, chapterIndex, paraIndex, wordIndex);
        }
        return false;
    }

    /**
     * 获取上下翻页模式在屏幕中开始的游标位置
     *
     * @return
     */
    public BBATextWordCursor getVerticalPageEndCursor() {
        if (mReaderContainer != null) {
            return mReaderContainer.getVerticalPageEndCursor();
        }
        return null;
    }

    /**
     * 获取上下翻页模式在屏幕中结束的游标位置
     *
     * @return
     */
    public BBATextWordCursor getVerticalPageStartCursor() {
        if (mReaderContainer != null) {
            return mReaderContainer.getVerticalPageStartCursor();
        }
        return null;
    }

    /**
     * 获取当前正在展示的广告item
     */
    public BBAReaderContainerItem getShowingAdPageItem() {
        if (mReaderContainer != null) {
            return mReaderContainer.getShowingAdPageItem();
        }
        return null;
    }

    /**
     * 当前章节是否在屏幕内
     *
     * @param chapterIndex
     * @return
     */
    public boolean isChapterInVerticalPage(int chapterIndex) {
        if (mReaderContainer != null) {
            return mReaderContainer.isChapterInVerticalPage(chapterIndex);
        }
        return false;
    }

    /**
     * 判断当前页是否是正文页
     *
     * @return
     */
    public boolean currentPageIsContent() {
        if (mReaderContainer != null) {
            BBAReaderContainerItem containerItem = mReaderContainer.getAroundCurrentItem(0);
            if (containerItem != null && containerItem.page != null) {
                return containerItem.page.haveText();
            }
        }
        return false;
    }

    /**
     * 跳转到当前书指定的字的位置
     *
     * @param bookId       书id
     * @param chapterIndex 章
     * @param paraIndex    段
     * @param wordIndex    字
     */
    public void gotoWordPosition(String bookId, int chapterIndex, int paraIndex, int wordIndex) {
        BBABook book = BBAReaderComponent.getInstance().getBook();
        if (book != null && !TextUtils.isEmpty(bookId) && bookId.equals(book.getBookId())) {
            BBAJumpChapterUtil.jumpToChapter(chapterIndex, paraIndex, wordIndex);
        }
    }

    /**
     * 重置阅读容器，删除阅读容器数据，重新请求当前章数据，重新生成当前章引擎
     */
    public void resetReaderEngine() {
        if (StubToolsWrapperKt.Companion.isEnableLog()) {
            Log.d(TAG, "xyl --- resetReaderEngine ");
        }
        BBALogUtil.d("xyl --- resetReaderEngine ");
        Runnable resetRunnable = getResetRunnable();
        if (mReaderContainer != null && mReaderContainer.getAdapter() != null) {
            mReaderContainer.getAdapter().setLayoutReseting(true);
        }
        if (mUIHandler != null && resetRunnable != null) {
            mUIHandler.removeCallbacks(resetRunnable);
            mUIHandler.postDelayed(resetRunnable, 100);
        }
    }

    private Runnable getResetRunnable() {
        if (resetRunnable == null) {
            resetRunnable = new ResetRunnable(this);
        }
        return resetRunnable;
    }

    /**
     * 获取距离当前页最近的正文page，用于获取进度值
     * 当前是广告时，在列表中向前查询，直到找到合适的page值
     *
     * @return BBAPage
     */
    public BBAPage getCurrentValidPage() {
        if (mReaderContainer != null) {
            return mReaderContainer.getCurrentValidPage();
        }
        return null;
    }

    /**
     * 获取可以保存进度的章节
     *
     * @return
     */
    public BBAPage getSaveProgressValidPage() {
        if (mReaderContainer != null) {
            if (mReaderContainer.isBookTail()) { // 如果时书尾需要从书尾定位
                return mReaderContainer.getCurrentValidPage(BBADefaultConfig.USE_CURRENT_ITEM_POSITION);
            } else {
                return mReaderContainer.getCurrentValidPage();
            }
        }
        return null;
    }

    /**
     * 更新当前页面
     */
    public void updateCurrentPage() {
        if (mReaderContainer != null) {
            mReaderContainer.updateItemStatusForTTs();
        }
    }

    /** 自定义监听类 */
    private static final class BBAReaderAdHandler implements BBAEventHandler {
        private WeakReference<FBReader> weakReference;
        /** 翻页之前的当前章节索引 */
        private int currentFileIndex;
        /** 当前完全可见页面索引 */
        private int currentVisibleIndex;

        public BBAReaderAdHandler(FBReader fbReader) {
            this.weakReference = new WeakReference<>(fbReader);
        }

        @Override
        public void onEvent(int eventType, BBAEventData data) {
            if (weakReference != null) {
                final FBReader fbReader = weakReference.get();
                if (fbReader != null) {
                    switch (eventType) {
                        case JUMP_CHAPTER_AFTER:
                            int jumpFileIndex = 0;
                            if (data != null) {
                                jumpFileIndex = data.getInt(FILE_INDEX);
                            }
                            if (currentFileIndex != jumpFileIndex) {
                                BBASwitchChapterUBCEventTools.readerPvStat(jumpFileIndex);
                                currentFileIndex = jumpFileIndex;
                            }
                            break;
                        case REWARD_PLAY_END:
                            BBAThreadUtils.runOnLayoutThread(new Runnable() {
                                @Override
                                public void run() {
                                    BBABook book = BBAReaderComponent.getInstance().getBook();
                                    if (book != null) {
                                        int currentChapterIndex = book.getCurrentChapterIndex();
                                        BBAReaderBitmapManager.getInstance().clear();
                                        BBAReaderCoreApi.clearDataLocal(book.getBookId(), currentChapterIndex);
                                    }
                                }
                            });
                            break;
                        case SCROLL_PAGE:
                            int pageIndex = 0;
                            int fileIndex = 0;
                            int visibleIndex = -1;
                            String oldPageType = "";
                            if (data != null) {
                                pageIndex = data.getInt(PAGE_INDEX);
                                fileIndex = data.getInt(FILE_INDEX);
                                visibleIndex = data.getInt(PAGE_COMPLETELY_VISIBLE_INDEX);
                                oldPageType = data.getString(OLD_PAGE_TYPE_UBC_STR);
                            }
                            AllScenesStatisticUtils.currentPageType = oldPageType;
                            // 如果是扉页，隐藏顶部运营位
                            if (fbReader.isTitlePageShowing()
                                    && fbReader.mBBAEduViewController != null
                                    && fbReader.mBBAEduViewController.isVisibility()) {
                                BBAOperateController.getInstance().dismissTopNotice(fbReader);
                            }
                            if (pageIndex == 0) {
                                if (!BBAAdUtil.isAdShowState() && !BBAAdUtil.isHideAdView()) {
                                    // 翻到新的章节，当前广告为隐藏状态，并且没有VIP、活动等状态禁止展示广告，刷新版心高度和广告状态
                                    BBAAdUtil.setLastAdState(true);
                                    if (1 == fbReader.canShowBannerAdPage()) {
                                        BBALogUtil.d("xyl --- canShowBannerAdPage ");
                                        fbReader.resetReaderEngine();
                                    }
                                }
                                fbReader.bannerRetryDelayTime = 0;
                            }
                            // ab实验，首章不出广告
                            boolean firstChapterAdfreq =
                                    NovelAbTest.getSwitch(NEWREADER_FIRST_CHAPTER_ADFREQ_SWITCh, false);
                            if (BBAAdUtil.getFirstChapterIndex() != fileIndex
                                    && fbReader.isFirstChapter
                                    && firstChapterAdfreq) {
                                ReaderManager.getInstance(fbReader).getReaderManagerCallback().netUpdateBannerView();
                                fbReader.isFirstChapter = false;
                            }
                            BBAReaderContainer mReaderContainer = fbReader.mReaderContainer;
                            if (mReaderContainer != null) {
                                // 添加章首强制广告
                                if (checkCanAddChapterHeadAd()) {
                                    mReaderContainer.addChapterHeadIfNeeded();
                                }
                                // 设置是否是强制广告位
                                fbReader.setNextForceAD(mReaderContainer.nextPageIsForceAdPage());
                            }
                            if (currentFileIndex != fileIndex) {
                                BBASwitchChapterUBCEventTools.readerPvStat(fileIndex);
                                BBASwitchChapterUBCEventTools.logSwitchChapterEvent();
                                currentFileIndex = fileIndex;
                            }
                            boolean isToNext = false;
                            if (data != null) {
                                isToNext = data.getBoolean(IS_SCROLL_PAGE_TO_NEXT_DIRECTION);
                            }
                            // 仅对平移情况做特殊处理
                            if ((BBAReaderConfig.getInstance().getTurnPageType() != HORIZONTAL_TRANSLATE) ||
                                    (visibleIndex != -1 && currentVisibleIndex != visibleIndex)) {
                                currentVisibleIndex = visibleIndex;
                                // 更新阅读器内从本页读状态
                                onScrollingFinishedForReadCurrentView();
                                NovelTtsInterface.getInstance().onScrollingFinished(isToNext);
                            }
                            // 更新翻页之前的item广告状态
                            updateAdViewAfterTurnPage(isToNext);
                            // 翻页，如果是付费页或者登录页，不展示banner广告
                            fbReader.updateBannerAdView();

                            int showPageType = -1;
                            if (mReaderContainer != null) {
                                mReaderContainer.setEduText();
                                BBAReaderContainerItem containerItem =
                                        mReaderContainer.getAroundCurrentItem(0);

                                if (containerItem != null) {
                                    // 如果是章尾 也不展示
                                    if (containerItem.isChapterLastPage) {
                                        showPageType = -1;
                                    } else {
                                        showPageType = containerItem.itemType;
                                    }
                                }
                            }

                            // 正版阅读器进行翻页的时候浮层广告进行隐藏
                            // 上下翻页的情况特殊处理
                            BBAThreadUtils.runOnUiThread(
                                    new FloatAdCloseRunnable(weakReference), 100);
                            ReaderManagerCallback callback =
                                    ReaderManager.getInstance(fbReader).getReaderManagerCallback();
                            if (callback != null) {
                                callback.onTurnPage(showPageType);
                            }
                            break;
                        case N_FILE_COMPLETED:
                            FBReaderPreHelper.saveReaderLayoutEndTime();
                            FBReaderPreHelper.saveReaderAdLayoutEndTime();
                            break;
                        case RENDER_PAGE_COMPLETED:
                            FBReaderPreHelper.saveReaderRenderEndTime();
                            break;
                        case DRAW_BITMAP_TO_CANVAS:
                            AllScenesStatisticUtils.loadingSuccess();
                            if (!fbReader.drawComplete) {
//                                ReaderSpeedUtil.end("阅读器上屏");
                                fbReader.drawComplete = true;
                                boolean loadingSwitch = BBAReaderComponent.getInstance().getSwitch(
                                        BBAABTestConstants.NOVEL_READER_LOADING_SWITCH, false);
                                if (loadingSwitch) {
                                    final boolean isTitlePage = data.getBoolean(IS_TITLE_PAGE);
                                    if (!fbReader.isFinishing() && !fbReader.isDestroyed()) {
                                        BBAThreadUtils.runOnUiThreadNotNow(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!fbReader.isFinishing() && !fbReader.isDestroyed()) {
                                                    if (isTitlePage) {
                                                        fbReader.showFirstMenu();
                                                    }
                                                    fbReader.showDefaultBottomBannerAd();
                                                    ReaderUtility.notifyHost(ReaderConstant.READER_ON_DRAW_COMPLETE,
                                                            "");
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                            break;
                        case SCROLLING_PAGE_CHANGE:
                            int scrollingFileIndex = 0;
                            String scrollingOldPageType = "";
                            String currentPageId = "";
                            String lastCurrentPageId = "";
                            int oldPageIndexV = -1;
                            int currentPageIndexV = -1;
                            boolean isFirstTurnPage = false;
                            boolean isLoadingPage = false;
                            boolean isGestureTurnPage = false;
                            if (data != null) {
                                isGestureTurnPage = data.getBoolean(IS_GESTURE_TURN_PAGE);
                                isLoadingPage = data.getBoolean(IS_CURRENT_LOADING_PAGE);
                                isFirstTurnPage = data.getBoolean(IS_FIRST_TURN_TO_PAGE);
                                currentPageId = data.getString(PAGE_ITEM_ID);
                                lastCurrentPageId = data.getString(LAST_PAGE_ITEM_ID);
                                scrollingFileIndex = data.getInt(FILE_INDEX);
                                scrollingOldPageType = data.getString(OLD_PAGE_TYPE_UBC_STR);
                                oldPageIndexV = data.getInt(OLD_PAGE_INDEX);
                                currentPageIndexV = data.getInt(CURRENT_SCREEN_INDEX);
                            }

                            BBABook bbaBook = BBAReaderComponent.getInstance().getBook();
                            if (bbaBook != null
                                    && (isGestureTurnPage)
                                    && (!TextUtils.isEmpty(lastCurrentPageId))
                                    && (!lastCurrentPageId.equals(currentPageId)
                                    || currentFileIndex != scrollingFileIndex)) {
                                BBABookChapter chapter = bbaBook.getChapter(currentFileIndex);
                                if (chapter != null) {
                                    String adType = "";
                                    if (TextUtils.equals("ad", scrollingOldPageType)) {
                                        adType = BBAAdViewController.getInstance().getInnerAdType();
                                    }
                                    long currentTimeMillis = SystemClock.uptimeMillis();
                                    AllScenesStatisticUtils.ubc580ByTurnPage(
                                            String.valueOf(currentTimeMillis - AllScenesStatisticUtils.pageDuration
                                                    + AllScenesStatisticUtils.pageReminDuration),
                                            chapter.getChapterId(), scrollingOldPageType, adType,
                                            String.valueOf(chapter.getFreeState()));
                                    AllScenesStatisticUtils.pageDuration = currentTimeMillis;
                                    AllScenesStatisticUtils.pageReminDuration = 0;
                                }
                                AllScenesStatisticUtils.pageCount++;
                                if (TextUtils.equals("ad", scrollingOldPageType)) {
                                    AllScenesStatisticUtils.adPageCount++;
                                    // 通知广告页做动画
                                    ReaderUtility.notifyHost(ReaderConstant.ON_CANVAS_AD_STATE_CHANGE,
                                            FROM_AD_BITMAP + "");
                                }
                            }
                            if (currentFileIndex != scrollingFileIndex) {
                                BBASwitchChapterUBCEventTools.readerPvStat(scrollingFileIndex);
                                BBASwitchChapterUBCEventTools.logSwitchChapterEvent();
                                currentFileIndex = scrollingFileIndex;
                            }
                            break;
                        case SO_LOAD_FAIL:
                            AllScenesStatisticUtils.ubc580BySoLoadFail();
                            break;
                    }
                }
            }
        }

        /**
         * 更新翻页之前的item广告状态，翻页完成后要将广告状态置为初始值
         *
         * @param isToNext
         */
        private void updateAdViewAfterTurnPage(boolean isToNext) {
            if (weakReference != null) {
                FBReader fbReader = weakReference.get();
                if (fbReader != null) {
                    BBAReaderContainer readerContainer = fbReader.mReaderContainer;
                    // 如果未在展示广告，则需要判断翻页之前是否为广告，并且更新翻页之前的item广告状态
                    if (readerContainer != null) {
                        BBAPage currentPage = readerContainer.getCurrentPage();
                        if (currentPage != null && !currentPage.isOnlyAd()) {
                            readerContainer.updatePreOrNextAdViewState(isToNext);
                        }
                    }
                }
            }

        }
    }

    private static void onScrollingFinishedForReadCurrentView() {
        FBReader fbReader = ReaderUtility.getFbReader();
        if (fbReader != null) {
            fbReader.updateReadCurrentMenu(); // 更新tts"从当前页读"view
        }
        BBATTSViewController.getInstance().updateCurrentPageView();
    }

    /**
     * 如果是付费页或者登录页隐藏banner广告
     */
    public void updateBannerState() {
        try {
            if (mUIHandler != null) {
                Runnable updateBannerAdViewRunnable = getUpdateBannerAdViewRunnable();
                mUIHandler.removeCallbacks(updateBannerAdViewRunnable);
                mUIHandler.post(updateBannerAdViewRunnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新banner展示状态
     */
    private void updateBannerAdView() {
        RelativeLayout bannerAdViewLayout = getBannerAdViewLayout();
        if (isLocalBook() && bannerAdViewLayout != null && bannerAdViewLayout.getVisibility() != GONE) { // 本地书没广告
            bannerAdViewLayout.setVisibility(GONE);
            return;
        }

        if (bannerAdViewLayout != null
                && BBAAdUtil.isAdShowState()) {
            int canShowBannerAdPage = canShowBannerAdPage();
            if (canShowBannerAdPage == 2 && bannerAdViewLayout.getVisibility() != GONE) {
                bannerAdViewLayout.setVisibility(GONE);
            } else if (canShowBannerAdPage == 1 && bannerAdViewLayout.getVisibility() != View.VISIBLE) {
                bannerAdViewLayout.setVisibility(View.VISIBLE);
                // banner显示，刷新阅读器高度
                if (mReaderContainer != null) {
                    mReaderContainer.setRecyclerViewMargin();
                }
                final ReaderManagerCallback callback = ReaderManager.getInstance(this).getReaderManagerCallback();
                if (callback != null) {
                    if (bannerAdViewLayout.getChildCount() == 0) { // 付费页滑动到正文可能出现白屏
                        // 如果还没添加过子View，可能是网络异常，请求失败导致，10s后重试一次
                        updateBannerView();
                    } else if (bannerAdViewLayout.getChildCount() == 1) {
                        View child = bannerAdViewLayout.getChildAt(0);
                        boolean isOpenFromNovelOnline =
                                mBookInfo != null && !TextUtils.isEmpty(mBookInfo.getNovelOnlineJsonStr());
                        // 针对书本线上化入口打开的书籍，如果可展示广告，但此时为兜底banner，则进行banner刷新
                        // fix bug：书本线上化入口，进入付费章节免费阅读后，请求banner广告但并未展示(也未开启倒计时)，切换到免费章节后不会再强制刷新，会一直展示兜底banner
                        if (isOpenFromNovelOnline && child == bannerDefaultView) {
                            if (!isDestroyed() && !isFinishing()) {
                                callback.netUpdateBannerView();
                            }
                        }
                    }
                }
            }
            // banner隐藏时，刷新阅读器高度
            if (canShowBannerAdPage == 2 && !isCurFullScreenAd()) {
                if (mReaderContainer != null) {
                    mReaderContainer.setRecyclerViewMargin();
                }
            }
        }
    }

    /**
     * 当前页是否是全屏广告
     */
    private boolean isCurFullScreenAd() {
        if (mReaderContainer != null) {
            BBAPage currentPage = mReaderContainer.getCurrentPage();
            ReaderManagerCallback readerManagerCallback = ReaderManager.getInstance(this).getReaderManagerCallback();
            return currentPage != null
                    && currentPage.isOnlyAd()
                    && readerManagerCallback != null
                    && readerManagerCallback.isCurAdFullScreen();
        }
        return false;
    }

    /**
     * 重置底部banner位置
     */
    public void resetBannerLayout() {
        if (mBannerAnimHelper != null) {
            mBannerAnimHelper.resetBannerLayout();
        }
    }

    /**
     * 隐藏底部banner
     */
    public void updateBannerHide() {
        if (mReaderContainer != null) {
            mReaderContainer.updateBannerHide();
        }
    }

    /**
     * 更新Banner广告的任务
     */
    private static class UpdateBannerViewRunnable implements Runnable {

        private WeakReference<FBReader> weakReference;

        public UpdateBannerViewRunnable(FBReader fbReader) {
            weakReference = new WeakReference<>(fbReader);
        }

        @Override
        public void run() {
            if (weakReference != null) {
                FBReader fbReader = weakReference.get();
                if (fbReader != null) {
                    RelativeLayout bannerAdViewLayout = fbReader.getBannerAdViewLayout();
                    ReaderManagerCallback callback = getReaderManagerCallback();
                    if (bannerAdViewLayout != null && bannerAdViewLayout.getChildCount() <= 0
                            && !fbReader.isDestroyed() && !fbReader.isFinishing() && callback != null) {
                        callback.netUpdateBannerView();
                        fbReader.bannerRetryDelayTime = 0;
                    }
                }
            }

        }
    }

    private void updateBannerView() {
        if (updateBannerViewRunnable == null) {
            updateBannerViewRunnable = new UpdateBannerViewRunnable(this);
        }
        if (mUIHandler != null) {
            mUIHandler.removeCallbacks(updateBannerViewRunnable);
        }
        postDelayed(updateBannerViewRunnable, bannerRetryDelayTime);
    }

    /**
     * 是否是可以展示广告的页
     *
     * @return 0为不进行显示和隐藏操作，1为进行显示操作，2为进行隐藏操作
     */
    public int canShowBannerAdPage() {
        if (mReaderContainer != null) {
            BBAPage currentPage = mReaderContainer.getCurrentPage();
            boolean firstChapterAdfreq = NovelAbTest.getSwitch(NEWREADER_FIRST_CHAPTER_ADFREQ_SWITCh, false);
            if (currentPage != null
                    && BBAAdUtil.getFirstChapterIndex() == currentPage.fileIndex && firstChapterAdfreq) {
                return 2;
            }
            boolean adHide = BBAAdUtil.isCurrentChapterAdHide();
            if ((currentPage != null
                    && (currentPage.isPayPage || currentPage.isLoginPage))
                    || adHide || isCurFullScreenAd()) {
                return 2;
            } else if (currentPage != null && currentPage.isLoadingPage) {
                return 0;
            } else {
                return 1;
            }
        }
        return 0;
    }

    /**
     * 接收到TTS关闭通知
     */
    public void onGlobalTTsClose() {
        if (!isFinishing()) {
            if (globalTTsCloseRunnable == null) {
                globalTTsCloseRunnable = new GlobalTTsCloseRunnable(this);
            }
            // 如果在阅读器内关闭TTS时，菜单为开启状态，所以需要展示"听"，这里调用showFloatView内部回自己判断逻辑
            if (mUIHandler != null) {
                mUIHandler.postDelayed(globalTTsCloseRunnable, 200);
            }
            hideReadCurrentView();
        }
    }

    private static class GlobalTTsCloseRunnable implements Runnable {

        private WeakReference<FBReader> weakReference;

        public GlobalTTsCloseRunnable(FBReader fbReader) {
            this.weakReference = new WeakReference<>(fbReader);
        }

        @Override
        public void run() {
            if (weakReference != null) {
                FBReader fbReader = weakReference.get();
                if (fbReader != null && !fbReader.isFinishing() && fbReader.isMenuAtShow()) {
                    BBATTSViewController.getInstance().showFloatView();
                    if (fbReader.mBBABubbleGuideController != null) {
                        fbReader.mBBABubbleGuideController.showTTSGuide(
                                BBATTSViewController.getInstance().getFloatView());
                    }
                }
            }
        }
    }

    private static class ShowMenuRunnable implements Runnable {
        private WeakReference<FBReader> weakReference;

        public ShowMenuRunnable(FBReader fbReader) {
            this.weakReference = new WeakReference<>(fbReader);
        }

        @Override
        public void run() {
            if (weakReference != null) {
                FBReader fbReader = weakReference.get();
                if (fbReader != null && !fbReader.isFinishing()) {
                    BBAMenuComponent.getInstance().showMenu();
                }
            }
        }
    }

    private static class DismissAllMenuRunnable implements Runnable {
        private WeakReference<FBReader> weakReference;

        public DismissAllMenuRunnable(FBReader fbReader) {
            this.weakReference = new WeakReference<>(fbReader);
        }

        @Override
        public void run() {
            if (weakReference != null) {
                FBReader fbReader = weakReference.get();
                if (fbReader != null && !fbReader.isFinishing()) {
                    BBAMenuComponent.getInstance().dismissAllMenu();
                }
            }
        }
    }

    public Runnable getDismissMenuRunnable() {
        if (dismissAllMenuRunnable == null) {
            dismissAllMenuRunnable = new DismissAllMenuRunnable(this);
        }
        return dismissAllMenuRunnable;
    }

    public Runnable getShowMenuRunnable() {
        if (showMenuRunnable == null) {
            showMenuRunnable = new ShowMenuRunnable(this);
        }
        return showMenuRunnable;
    }

    private Runnable getUpdateBannerAdViewRunnable() {
        if (updateBannerAdViewRunnable == null) {
            updateBannerAdViewRunnable = new UpdateBannerAdViewRunnable(this);
        }
        return updateBannerAdViewRunnable;
    }

    private static class UpdateBannerAdViewRunnable implements Runnable {
        private WeakReference<FBReader> weakReference;

        public UpdateBannerAdViewRunnable(FBReader fbReader) {
            this.weakReference = new WeakReference<>(fbReader);
        }

        @Override
        public void run() {
            if (weakReference != null) {
                FBReader fbReader = weakReference.get();
                if (fbReader != null && !fbReader.isFinishing()) {
                    fbReader.updateBannerAdView();
                }
            }
        }
    }

    /**
     * 2021.07.22 zhangqian58备注 版本号：12.22
     * 修改顶部状态栏字体颜色。
     */
    public void changeStatusBarColorWhenExitFullScreen() {
        try {
            // 退出当前全屏状态
            final FBReaderApp app = (FBReaderApp) ReaderBaseApplication.Instance();
            if (app != null) {
                // 2021.08.05 zhangqian58 备注：退出全屏时，实现底部虚拟按键覆盖正文效果，保证正文不重绘
                int flag = FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    flag |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                }
                if (!app.isNightMode()) {
                    // 在日间模式下对顶部状态栏字体颜色
                    // 版本23及以上才可以使用View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR修改顶部状态栏文字深色
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        // 2021.08.05 zhangqian58 备注：退出全屏时，实现底部虚拟按键覆盖正文效果，保证正文不重绘
                        flag = FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                                | View.SYSTEM_UI_FLAG_IMMERSIVE
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                    }
                }
                // 2021.09.07 备注 : 统一修改顶部状态栏和底部虚拟按键背景颜色
                getWindow().getDecorView().setSystemUiVisibility(flag);
                changeTopStatusBarBgColorAndBottomNavigation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 2021.08.05 zhangqian58 备注：
     * 退出全屏后，仅展示底部虚拟按键
     */
    public void exitFullScreenModeOnlyBottomBar() {
        if (currentFullScreen == EXIT_FULL_SCREEN_ONLY_BOTTOM_TYPE) {
            return;
        }
        try {
            final ZLAndroidLibrary zlibrary = getZLibrary();
            if (zlibrary != null && !zlibrary.isKindleFire() && !zlibrary.ShowStatusBarOption.getValue()) {
                // 退出当前全屏状态
                final FBReaderApp app = (FBReaderApp) ReaderBaseApplication.Instance();
                if (app != null) {
                    int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN;
                    if (!app.isNightMode()) {
                        // 在日间模式下对顶部状态栏字体颜色
                        // 版本23及以上才可以使用View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR修改顶部状态栏文字深色
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            flag = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
                        }

                    }
                    // 2021.09.07 备注 : 统一修改顶部状态栏和底部虚拟按键背景颜色
                    changeTopStatusBarBgColorAndBottomNavigation();
                    getWindow().getDecorView().setSystemUiVisibility(flag);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                }
            }
            // 2021.08.10 备注：修改当前全屏状态
            currentFullScreen = EXIT_FULL_SCREEN_ONLY_BOTTOM_TYPE;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 2021.09.07 zhangqian58备注 版本号：12.25
     * 根据日夜间模式更新底部虚拟按键和顶部状态栏颜色
     */
    public void changeTopStatusBarBgColorAndBottomNavigation() {
        changeTopStatusBarBgColorAndBottomNavigation(false);
    }
    /**
     * 2021.09.07 zhangqian58备注 版本号：12.25
     * 根据日夜间模式更新底部虚拟按键和顶部状态栏颜色
     */
    public void changeTopStatusBarBgColorAndBottomNavigation(boolean isAutoChange) {
        try {
            final FBReaderApp app = (FBReaderApp) ReaderBaseApplication.Instance();
            if (app != null) {
                // setNavigationBarColor方法要求21及以上
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    if (window != null) {
                        if (app.isNightMode()) {
                            // 在夜间模式下修改底部虚拟按键
                            // 2021.08.05 zhangqian58 备注：修改底虚拟按键颜色和menu背景色保持一致
                            if (transformHelper == null || !transformHelper.isFeedOepn()) {
                                window.setBackgroundDrawableResource(R.color.ff191919);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                window.setStatusBarColor(0xFF1B1B1B);
                            }
                            setNavigationBarColor(0xFF191919, isAutoChange); // 系统底部导航栏背景色设置
                        } else {
                            // 2021.09.07 备注：单机型EVA-AL100手机日夜间底部虚拟按键都为黑色
                            if (ReaderStatusBarUtil.isHUAWEIAndSDK23()) {
                                if (transformHelper == null || !transformHelper.isFeedOepn()) {
                                    window.setBackgroundDrawableResource(R.color.ff191919);
                                }
                                // 2021.09.09 备注：单机型EVA-AL100手机状态栏日间模式为白色
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (isNewThemeColor()){
                                        window.setStatusBarColor(BBAThemeResourceHelper.getThemeCardColor());
                                    } else {
                                        window.setStatusBarColor(0xFFFFFFFF);
                                    }
                                }
                                setNavigationBarColor(0xFF191919, isAutoChange);
                                return;
                            }
                            // 在日间模式下修改底部虚拟按键
                            // 2021.08.05 zhangqian58 备注：修改底虚拟按键颜色和menu背景色保持一致
                            if (transformHelper == null || !transformHelper.isFeedOepn()) {
                                window.setBackgroundDrawableResource(R.drawable.ffffff);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (isNewThemeColor()){
                                    window.setStatusBarColor(BBAThemeResourceHelper.getThemeCardColor());
                                } else {
                                    window.setStatusBarColor(0xFFFFFFFF);
                                }
                            }
                            setNavigationBarColor(0xFFFFFFFF, isAutoChange); // 系统底部导航栏背景色设置
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setRootEyeShield();
    }
    /**
     * 判断是否是新主题颜色 新的主题色&日间&色值不为-1
     * @return
     */
    private boolean isNewThemeColor() {
        return BBAThemeResourceHelper.isNewColor()
                && BBAThemeResourceHelper.getThemeCardColor() != -1
                && BBAModeChangeHelper.notDarkAndNightMode();
    }
    /**
     * 初始化背景蒙层的进入动画
     */
    private void initBgInAnimation() {
        mBgInAnimation = AnimationFactory.createAlphaAnimation(AnimationFactory.OPTION_IN,
                AnimationFactory.DURATION_GRADIENT_3, AnimationFactory.INTERPOLATOR_SLOW_IN_OUT,
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        if (mPopBackgroundView != null) {
                            mPopBackgroundView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
    }

    /**
     * 初始化底部的出去动画
     */
    private void initBgOutAnimation() {
        mBgOutAnimation = AnimationFactory.createAlphaAnimation(AnimationFactory.OPTION_OUT,
                AnimationFactory.DURATION_GRADIENT_2, AnimationFactory.INTERPOLATOR_SLOW_IN_OUT,
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // 2021.09.09 备注：在menu消失时，去除背景灰色view
                        if (mPopBackgroundView != null) {
                            mPopBackgroundView.setVisibility(GONE);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
    }

    /**
     * 半层展示，通知阅读器展示半层背景
     */
    public void showPopBackgroundView() {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPopBackgroundView != null) {
                    mPopBackgroundView.startAnimation(mBgInAnimation);
                }
            }
        });
    }

    /**
     * 半层隐藏，通知阅读器隐藏半层背景
     */
    public void dismissPopBackgroundView() {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mPopBackgroundView != null) {
                    mPopBackgroundView.startAnimation(mBgOutAnimation);
                }
            }
        });
    }

    public void checkAdShowState() {
        boolean adShowState = BBAAdUtil.isAdShowState();
        boolean hideAdView = BBAAdUtil.isHideAdView(false);
        ReaderManagerCallback callback = getReaderManagerCallback();
        if (callback != null && bbaMenuVipModel != null) {
            if (callback.isVip()) {
                bbaMenuVipModel.normalImgName = BBATempResourceProviderImpl.DrawableType.BBA_MENU_VIP;
            } else {
                bbaMenuVipModel.normalImgName = BBATempResourceProviderImpl.DrawableType.BBA_MENU_NO_VIP;
            }
            bbaMenuVipModel.updateMenuItemView();
        }
        if (adShowState != hideAdView) {
            return;
        }
        ReaderUtility.requestUpdateAdShowState(hideAdView ? String.valueOf(HIDE_AD) : String.valueOf(SHOW_AD_VIEW));
        BBAAdUtil.setLastAdState(!hideAdView);
    }

    /**
     * 网络状态改变
     */
    @Override
    public void netrworkChange() {
        if (!isFirst
                && NovelGalaxyWrapper.isNetworkConnected(this)
                && mBannerAdLayout != null) {
            View bannerView = mBannerAdLayout.getChildAt(0);
            if (mBannerAdLayout.getChildCount() <= 0
                    || (bannerDefaultView != null
                    && bannerView == bannerDefaultView
                    && bannerDefaultView.getParent() != null)) { // 没添加过广告数据，强制请求网络刷新
                ReaderManagerCallback callback = ReaderManager.getInstance(this).getReaderManagerCallback();
                if (callback != null) {
                    callback.netUpdateBannerView();
                }
            } else { // 已经添加过广告数据，刷新view
                ReaderBannerAdViewManager.getInstance().requestUpdateAdShowState(SHOW_AD_VIEW);
            }
        }

        isFirst = false;
    }

    public static boolean getIsNightMode() {
        return BBAModeChangeHelper.isNightMode();
    }

    private static class ResetRunnable implements Runnable {
        private WeakReference<FBReader> weakReference;

        public ResetRunnable(FBReader fbReader) {
            this.weakReference = new WeakReference<>(fbReader);
        }

        @Override
        public void run() {
            final BBABook bbaBook = BBAReaderComponent.getInstance().getBook();
            if (weakReference != null) {
                FBReader fbReader = weakReference.get();
                if (fbReader != null && !fbReader.isFinishing()) {
                    if (fbReader.mReaderContainer != null) {
                        fbReader.mReaderContainer.resetReaderEngine();
                    }
                }
                BBAReaderBitmapManager.getInstance().clear();
            }
            if (bbaBook != null) {
                final int showingChapterIndex = bbaBook.getCurrentChapterIndex();
                final BBABookChapter bbaBookChapter = bbaBook.getChapter(showingChapterIndex);
                BBAThreadUtils.runOnLayoutThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weakReference != null) {
                            FBReader fbReader = weakReference.get();
                            if (fbReader != null && !fbReader.isFinishing()) {
                                BBAReaderCoreApi.clearEnginePagesCache(); // 清除引擎缓存
                                BBAReaderCoreApi.clearEngine(); // 清除引擎
                                if (bbaBookChapter != null) {
                                    BBAEventBus.getInstance().dispatchEvent(REQUEST_CHAPTER_CONTENT,
                                            BBAEventData.createEventData()
                                                    .add(FILE_INDEX, showingChapterIndex)
                                                    .add(FILE_ID, bbaBookChapter.getChapterId()));
                                    BBALogUtil.d("xyl --- ResetRunnable dispatchEvent 页面开始重绘");
                                }
                                if (fbReader.mReaderContainer != null && fbReader.mReaderContainer.getAdapter() != null) {
                                    fbReader.mReaderContainer.getAdapter().setLayoutReseting(false);
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void loginCallback(int resultCode) {
        if (resultCode == SUCCESS) { // 登录成功，重新排版
            resetReaderEngine();
            BBAReaderCoreApi.clearPagingModelMap();
        }
    }

    public void setFontFamily(String fontFamily) {
        if (!TextUtils.equals(fontFamily, BBAFontController.getFontFamily())) {
            BBAFontController.setFontFamily(fontFamily);
            BBAReaderComponent.getInstance().adViewStop();
            BBAReaderCoreApi.setFontFamily(fontFamily, true);
            fontSettingModel.refreshFontFamilyAndSize();
        }
    }

    /**
     * 付费页面购买失败
     */
    public void payFailed() {
        if (mReaderContainer != null && myFBReaderApp != null) {
            ReaderManagerCallback callback = ReaderUtility.getReaderManagerCallback();
            if (callback != null) {
                // 清除付费页view缓存
                callback.removePayPreviewsCache();
            }
            BBAJumpChapterUtil.jumpToChapter(myFBReaderApp.getCurrentChapterIndex());
        }
    }

    /**
     * 显示底bar兜底广告
     */
    public void showDefaultBottomBannerAd() {
        RelativeLayout bannerAdViewLayout = getBannerAdViewLayout();
        if (bannerAdViewLayout == null || bannerAdViewLayout.getChildCount() > 0) {
            return;
        }
        // 展示默认图
        ReaderManagerCallback callback = ReaderUtility.getReaderManagerCallback();
        if (callback != null) {
            View naDefaultView = callback.getNovelAdBannerNaDefaultView(true);
            if (naDefaultView != null) {
                bannerDefaultView = naDefaultView;
            }
        }
        if (bannerAdViewLayout != null
                && bannerDefaultView != null) {
            // 如果广告视图已经有父view了 则将广告视图从父view中移除
            if (bannerDefaultView != null
                    && bannerDefaultView.getParent() != null) {
                ((ViewGroup) bannerDefaultView.getParent()).removeView(bannerDefaultView);
            }
            bannerAdViewLayout.removeAllViews();
            bannerAdViewLayout.addView(bannerDefaultView);
            ReaderBannerAdUpdateManager.getInstance().setAdType("default");
        }
    }

    /**
     * 显示底bar兜底广告，带动效
     */
    public void showBottomBannerAdDefault() {
        RelativeLayout bannerAdViewLayout = getBannerAdViewLayout();
        if (bannerAdViewLayout == null) {
            return;
        }
        // 展示默认图
        if (bannerDefaultView == null) {
            ReaderManagerCallback callback = ReaderUtility.getReaderManagerCallback();
            if (callback != null) {
                View naDefaultView = callback.getNovelAdBannerNaDefaultView(true);
                if (naDefaultView != null) {
                    bannerDefaultView = naDefaultView;
                }
            }
        }

        if (bannerAdViewLayout != null
                && bannerDefaultView != null) {
            // 如果广告视图已经有父view了 则将广告视图从父view中移除
            if (bannerDefaultView != null
                    && bannerDefaultView.getParent() != null) {
                ((ViewGroup) bannerDefaultView.getParent()).removeView(bannerDefaultView);
            }
            ReaderBannerAdUpdateManager.getInstance().updateBanner(bannerAdViewLayout, bannerDefaultView, "default");
        }
    }

    private static class FloatAdCloseRunnable implements Runnable {
        private WeakReference<FBReader> weakReference;

        public FloatAdCloseRunnable(WeakReference<FBReader> weakReference) {
            this.weakReference = weakReference;
        }

        @Override
        public void run() {
            if (weakReference == null) {
                return;
            }
            FBReader fbReader = weakReference.get();
            if (fbReader == null) {
                return;
            }
            fbReader.hideReaderPerformanceAdView();
            ReaderUtility.setFloatAdCloseMessage(true);
        }
    }

    /**
     * 界面加载完成之后调用
     */
    public void showGlobalOperationView(boolean isPayPage) {
        // 如果已经展示则直接返回
        if (isHaveShowGlobalOperationView) {
            return;
        }
        isHaveShowGlobalOperationView = true;
        // 判断当前是否是付费页面
        FBReaderApp app = (FBReaderApp) ReaderBaseApplication.Instance();
        if (app != null) {
            if (mBookInfo != null) {
                // 当前书籍的id
                String id = mBookInfo.getId();
                String from = "";
                String fromAction = StatisticsContants.UBC_FROM_NOVEL;
                if (isLocalBook()) {
                    fromAction = StatisticsContants.UBC_FROM_NATIVE_NOVEL;
                }
                if (ReaderUtility.isLogin() && isPayPage) {
                    from = "paidPurchasePage";
                } else {
                    from = "genuineReaderPage";
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("book_id", id);
                    jsonObject.put("from", from);
                    jsonObject.put("fromAction", fromAction);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ReaderManagerCallback callback = ReaderUtility.getReaderManagerCallback();
                if (callback != null) {
                    // 进行调用全局运营位视图
                    callback.sendNotify(NOTIFY_SET_GLOBAL_OPERATION_VIEW_DIALOG_SHOW_PARAMETER, jsonObject.toString());
                }
            }
        }
    }

    /**
     * 进行判断是否已经展示了全局运营位
     *
     * @return
     */
    public boolean haveShowGlobalOperationView() {
        return isHaveShowGlobalOperationView;
    }

    private static final class ReaderContext implements BBAReaderRuntime.IBBAReaderContext {
        WeakReference<FBReader> weakReference;

        public ReaderContext(FBReader fbReader) {
            this.weakReference = new WeakReference<>(fbReader);
        }

        @Override
        public Context getContext() {
            if (weakReference == null) {
                return null;
            }
            return weakReference.get();
        }
    }

    /**
     * 当前页是不是扉页（整页全是扉页）
     *
     * @return
     */
    public boolean isTitlePage() {
        if (mReaderContainer != null && mReaderContainer.getCurrentPage() != null) {
            BBAPage currentPage = mReaderContainer.getCurrentPage();
            return currentPage.isTitlePage();
        }
        return false;
    }

    /**
     * 扉页是不是正在展示（扉页露出来）
     *
     * @return
     */
    public boolean isTitlePageShowing() {
        if (mReaderContainer == null || mReaderContainer.getCurrentPage() == null) {
            return isTitlePagePositionByStorage();
        }
        if (mReaderContainer != null) {
            return mReaderContainer.isTitlePageShowing() != null;
        }
        return false;
    }

    /**
     * 通过存储查询是不是需要跳转到扉页
     *
     * @return
     */
    public boolean isTitlePagePositionByStorage() {
        if (bbaBook != null && mBookInfo != null) { // 本地保存过扉页进度 || 正版书，没存过进度，不是外部跳章，并且要展示扉页
            int chapterIndex = mBookInfo.getChapterIndex();
            if (chapterIndex > 0
                    || (chapterIndex == 0
                    && !(KEY_TITLE_PAGE_PARAGRAPH_OFFSET + ":0:0").equals(bbaBook.getChapterOffset()))) {
                return false;
            }

            // bbaBook 中的数据为空，则尝试加载数据
            if (bbaBook.getChapterOffset() == null) {
                Book book = getBook(mBookInfo);
                if (book != null) {
                    // 尝试加载进度
                    tryToLoadReadProgress(book);

                    return ((KEY_TITLE_PAGE_PARAGRAPH_OFFSET + ":0:0").equals(book.getChapterOffset()))
                            || (!bbaBook.isLocalBook() && TextUtils.isEmpty(book.getChapterOffset())
                            && bbaBook.getWithoutTitlePage() == 0);
                }
            }

            return ((KEY_TITLE_PAGE_PARAGRAPH_OFFSET + ":0:0").equals(bbaBook.getChapterOffset()))
                    || (!bbaBook.isLocalBook() && TextUtils.isEmpty(bbaBook.getChapterOffset())
                    && bbaBook.getWithoutTitlePage() == 0);
        }
        return false;
    }

    /**
     * 获取相应的进度，并同时更新相应的{@link Book}中的数据
     *
     * @param book 待更新的{@link Book}
     */
    private void tryToLoadReadProgress(Book book) {
        if (book != null) {
            ReaderManagerCallback callback =
                    ReaderManager.getInstance(this).getReaderManagerCallback();
            if (callback != null) {
                BookInfo bookInfo = callback.loadReadProgress(book.createBookInfo());
                if (bookInfo != null) {
                    if (bookInfo.getChapterIndex() >= 0
                            && !TextUtils.isEmpty(bookInfo.getChapterOffset())
                            && (book.getChapterIndex() < 0 || TextUtils.isEmpty(book.getChapterOffset()))) {
                        book.setChapterIndex(bookInfo.getChapterIndex());
                        book.setChapterOffset(bookInfo.getChapterOffset());
                    }
                    book.setGotoLast(bookInfo.getGotoLast());
                    if (bookInfo.getOldReadPositionType() != BookInfo.OLD_POSITION_TYPE_UNKNOWN
                            && !TextUtils.isEmpty(bookInfo.getOldReadPosition())) {
                        book.setOldReadPosition(bookInfo.getOldReadPositionType(), bookInfo.getOldReadPosition());
                    }
                    if (!TextUtils.isEmpty(bookInfo.getChapterId())) {
                        book.setChapterId(bookInfo.getChapterId());
                    }
                }
            }
        }
    }


    public void removeCurrentAdPage() {
        if (mReaderContainer != null) {
            int firstVisibleItemPosition = mReaderContainer.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = mReaderContainer.getLastVisiblePosition();
            for (int index = firstVisibleItemPosition; index <= lastVisibleItemPosition; index++) {
                BBAPage page = mReaderContainer.getAppointPage(index);
                if (page != null && page.pageType == BBAPage.BBAPageType.AD) {
                    mReaderContainer.removePage(index);
                }
            }
        }
    }

    private static class FBReaderSettingChangeListener implements BBASettingChangeListener {

        @Override
        public void onChange(String settingStatus, BBAReaderConfig config) {
            if (BBAMoreSettingKey.KEY_EDU_BOTTOM_BAR.equals(settingStatus)) {
                boolean eduTextSwitch = BBAReaderConfig.getInstance().isMoreSettingIsEduBottomBar();
                String source = eduTextSwitch ? "reader_learn_open" : "reader_learn_close";
                // ubc打点
                StatisticUtils.ubc753NoExt(UBC_FROM_NOVEL, UBC_TYPE_CLICK,
                        "reader_setting", source, null);
            }
        }
    }

    /**
     * 判断是否可以添加强制广告
     *
     * @return
     */
    private static boolean checkCanAddChapterHeadAd() {
        if (!BBAReaderComponent.getInstance().getOnTestResult("242")) {
            if (BBAReaderConfig.getInstance().isVerticalType()) {
                //上下模式不添加章首强制广告位
                return false;
            }
        }
        final BBABook bbaBook = BBAReaderComponent.getInstance().getBook();
        if (bbaBook == null) {
            return false;
        }
        if (bbaBook.isPirated() || bbaBook.isTransCodeBook() || bbaBook.isLocalBook()) {
            // 非正版不添加
            return false;
        }
        if (!BBAAdUtil.isAdShowState() || BBAAdUtil.isHideAdView()) {
            return false;
        }
        int showingChapter = bbaBook.getCurrentChapterIndex();
        boolean hasForce = BBAReaderComponent.getInstance().getForceAdPrepared(String.valueOf(showingChapter));
        if (!hasForce) {
            return false;
        }
        return true;
    }

    /** 获取恢复所需数据 */
    @Override
    @Keep
    public NovelPageRestoreDataWrapper getNovelRestoreData() {
        return NovelLrLegalHelper.getNovelRestoreData(myFBReaderApp, bbaBook);
    }

    /**
     * 展示本地书失败页
     */
    public void showLocalTxtContentError() {
        ReaderMonitorUtils.logAbnormalReader(this, NovelReaderAbnormalKey.MOUDLE_LOCALREADER,
                NovelReaderAbnormalKey.NOVEL_READER_OPEN_ERROR
                , NovelReaderAbnormalKey.NOVEL_LEVEL_ERROR, null);
        BBAThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isDestroyed() && !isFinishing()) {
                    dismissLoading();
                }
                if (contentErrorLayoutStub != null && contentErrorLayout == null) {
                    contentErrorLayout = contentErrorLayoutStub.inflate();
                    emptyBtnReload = findViewById(R.id.empty_btn_reload);
                }
                if (contentErrorLayout != null) {
                    contentErrorLayout.setVisibility(View.VISIBLE);
                    emptyIcon = contentErrorLayout.findViewById(R.id.empty_icon);
                }

                if (emptyIcon != null) {
                    emptyIcon.setBackground(BBAMenuComponent.getInstance().getDrawable(BBA_NO_WIFI));
                }
                if (emptyBtnReload != null) {
                    emptyBtnReload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (contentErrorLayout != null) {
                                contentErrorLayout.setVisibility(GONE);
                            }
                            showLoading();
                            BBAThreadUtils.runOnAsyncThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 打开相应书籍
                                    if (myFBReaderApp != null) {
                                        myFBReaderApp.openBook(myBook, null, null, null);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    /**
     * 13.22 设置广告提前加载时机
     *
     * @param adPreloadFreq
     */
    public void setAdPreloadFreq(int adPreloadFreq) {
        if (adPreloadFreq < 0) {
            return;
        }
        if (mReaderContainer == null) {
            return;
        }
        if (mReaderContainer.getAdapter() == null) {
            return;
        }
        mReaderContainer.getAdapter().setPreLoadAdNum(adPreloadFreq);
    }

    public boolean isFromWangpanTxt() {
        return isFromWangpanTxt;
    }

    /**
     * 展示金币领取动效
     *
     * @param isWelfare 是否是阅读器福利中心
     * @param coinNum
     */
    public void showCoinRewardView(final boolean isWelfare, final String coinNum) {
        BBAThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                if (rewardLayout != null) {
                    ISignRewardCoin signRewardCoin = null;
                    if (rewardLayout.getChildCount() == 0) {
                        ReaderManagerCallback callback = getReaderManagerCallback();
                        if (callback == null) {
                            return;
                        }
                        String param = isWelfare ? "1" : "0";
                        View view = callback.getView(NovelReaderCallbackViewType
                                .SIGN_REWARD_COIN_VIEW, param);
                        BBAUIUtil.safeAddView(rewardLayout, view);
                    }
                    View childAt = rewardLayout.getChildAt(0);
                    if (childAt instanceof ISignRewardCoin) {
                        signRewardCoin = (ISignRewardCoin) childAt;
                        signRewardCoin.setCoinNum(coinNum);
                        signRewardCoin.show();
                    }
                }
            }
        });
    }

    /**
     * 设置全屏状态
     * @param show
     */
    public void setAdFullScreen(boolean show) {
        if (mBannerAdLayout != null) {
            if (show) {
                mBannerAdLayout.setVisibility(GONE);
            } else {
                mBannerAdLayout.setVisibility(View.VISIBLE);
            }
        }
        if (mReaderContainer != null) {
            mReaderContainer.setContentFullScreen(show);
            if (!show) {
                // 取消时刷新一下当前页
                mReaderContainer.updateItemStatus();
            }
        }
    }

    public void refreshAdBitmap() {
        if (mReaderContainer != null) {
            mReaderContainer.refreshAdBitmap();
        }
    }

}