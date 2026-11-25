//// FILE: android/Manifest.java
package android;
public final class Manifest {
  public static final class permission {
    public static final String PACKAGE_USAGE_STATS = "android.permission.PACKAGE_USAGE_STATS";
    public static final String POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS";
    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";
    public static final String SCHEDULE_EXACT_ALARM = "android.permission.SCHEDULE_EXACT_ALARM";
    public static final String USE_EXACT_ALARM = "android.permission.USE_EXACT_ALARM";
    public static final String VIBRATE = "android.permission.VIBRATE";
    public static final String USE_FULL_SCREEN_INTENT = "android.permission.USE_FULL_SCREEN_INTENT";
  }
}
//// FILE: android/R.java
package android;
public final class R {
  public static final class id { public static final int background = 0x01020000; public static final int content = 0x01020002; public static final int text1 = 0x01020014; }
  public static final class style { public static final int Animation = 0x01030000; public static final int Theme_Translucent_NoTitleBar = 0x0103000f; }
  public static final class color { public static final int transparent = 0x0106000d; public static final int white = 0x0106000b; public static final int black = 0x0106000c; }
  public static final class drawable { public static final int ic_dialog_info = 0x01080041; }
  public static final class string { public static final int ok = 0x0104000a; public static final int cancel = 0x01040000; }
  public static final class attr { }
  public static final class anim { public static final int fade_in = 0x010a0000; public static final int fade_out = 0x010a0001; }
}
//// FILE: android/annotation/SuppressLint.java
package android.annotation;
import java.lang.annotation.*;
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE})
public @interface SuppressLint { String[] value(); }
//// FILE: android/annotation/TargetApi.java
package android.annotation;
import java.lang.annotation.*;
@Retention(RetentionPolicy.CLASS)
public @interface TargetApi { int value(); }
//// FILE: androidx/annotation/RequiresApi.java
package androidx.annotation;
import java.lang.annotation.*;
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PACKAGE})
public @interface RequiresApi { int value() default 1; int api() default 1; }
//// FILE: androidx/annotation/Keep.java
package androidx.annotation;
public @interface Keep { }
//// FILE: android/os/Build.java
package android.os;
public class Build {
  public static final String MANUFACTURER = "";
  public static final String MODEL = "";
  public static final String BRAND = "";
  public static final String DEVICE = "";
  public static final String PRODUCT = "";
  public static class VERSION {
    public static final int SDK_INT = 35;
    public static final String RELEASE = "";
    public static final int PREVIEW_SDK_INT = 0;
  }
  public static class VERSION_CODES {
    public static final int O = 26; public static final int O_MR1 = 27; public static final int P = 28; public static final int Q = 29;
    public static final int R = 30; public static final int S = 31; public static final int S_V2 = 32; public static final int TIRAMISU = 33;
    public static final int UPSIDE_DOWN_CAKE = 34; public static final int VANILLA_ICE_CREAM = 35; public static final int N = 24; public static final int M = 23;
  }
}
//// FILE: android/os/Bundle.java
package android.os;
public class Bundle {
  public Bundle() {}
  public Bundle(Bundle b) {}
  public void putString(String k, String v) {}
  public void putInt(String k, int v) {}
  public void putLong(String k, long v) {}
  public void putBoolean(String k, boolean v) {}
  public void putFloat(String k, float v) {}
  public String getString(String k) { return null; }
  public String getString(String k, String d) { return null; }
  public int getInt(String k) { return 0; }
  public int getInt(String k, int d) { return 0; }
  public long getLong(String k) { return 0; }
  public long getLong(String k, long d) { return 0; }
  public boolean getBoolean(String k) { return false; }
  public boolean getBoolean(String k, boolean d) { return false; }
  public float getFloat(String k) { return 0; }
  public <T> java.util.ArrayList<T> getParcelableArrayList(String k) { return null; }
  public <T> java.util.ArrayList<T> getParcelableArrayList(String k, Class<T> c) { return null; }
  public boolean containsKey(String k) { return false; }
  public java.util.ArrayList<String> getStringArrayList(String k) { return null; }
  public float[] getFloatArray(String k) { return null; }
  public void putStringArrayList(String k, java.util.ArrayList<String> v) {}
  public java.util.Set<String> keySet() { return null; }
  public Object get(String k) { return null; }
  public boolean isEmpty() { return true; }
  public int size() { return 0; }
}
//// FILE: android/os/Parcelable.java
package android.os;
public interface Parcelable { }
//// FILE: android/os/Handler.java
package android.os;
public class Handler {
  public Handler() {}
  public Handler(Looper looper) {}
  public final boolean post(Runnable r) { return true; }
  public final boolean postDelayed(Runnable r, long delayMillis) { return true; }
  public final boolean postDelayed(Runnable r, Object token, long delayMillis) { return true; }
  public final void removeCallbacks(Runnable r) {}
  public final void removeCallbacksAndMessages(Object token) {}
  public final Looper getLooper() { return null; }
}
//// FILE: android/os/Looper.java
package android.os;
public final class Looper {
  public static Looper getMainLooper() { return null; }
  public static Looper myLooper() { return null; }
  public Thread getThread() { return null; }
  public boolean isCurrentThread() { return false; }
  public static void prepare() {}
  public static void loop() {}
}
//// FILE: android/os/ParcelFileDescriptor.java
package android.os;
public class ParcelFileDescriptor implements java.io.Closeable {
  public static final int MODE_READ_ONLY = 0x10000000;
  public static final int MODE_WRITE_ONLY = 0x20000000;
  public static final int MODE_READ_WRITE = 0x30000000;
  public static ParcelFileDescriptor open(java.io.File file, int mode) throws java.io.FileNotFoundException { return null; }
  public java.io.FileDescriptor getFileDescriptor() { return null; }
  public long getStatSize() { return 0; }
  public int getFd() { return 0; }
  public void close() throws java.io.IOException {}
}
//// FILE: android/os/PowerManager.java
package android.os;
public final class PowerManager {
  public static final int PARTIAL_WAKE_LOCK = 1;
  public static final int SCREEN_DIM_WAKE_LOCK = 6;
  public static final int ACQUIRE_CAUSES_WAKEUP = 0x10000000;
  public boolean isInteractive() { return true; }
  public boolean isScreenOn() { return true; }
  public boolean isPowerSaveMode() { return false; }
  public boolean isIgnoringBatteryOptimizations(String packageName) { return true; }
  public WakeLock newWakeLock(int levelAndFlags, String tag) { return null; }
  public final class WakeLock {
    public void acquire() {}
    public void acquire(long timeout) {}
    public void release() {}
    public boolean isHeld() { return false; }
    public void setReferenceCounted(boolean value) {}
  }
}
//// FILE: android/os/Process.java
package android.os;
public class Process {
  public static final int myUid() { return 0; }
  public static final int myPid() { return 0; }
  public static final void killProcess(int pid) {}
}
//// FILE: android/os/SystemClock.java
package android.os;
public final class SystemClock {
  public static long elapsedRealtime() { return 0; }
  public static long uptimeMillis() { return 0; }
  public static long currentThreadTimeMillis() { return 0; }
  public static void sleep(long ms) {}
}
//// FILE: android/os/VibrationEffect.java
package android.os;
public abstract class VibrationEffect implements Parcelable {
  public static final int DEFAULT_AMPLITUDE = -1;
  public static final int EFFECT_CLICK = 0;
  public static final int EFFECT_DOUBLE_CLICK = 1;
  public static final int EFFECT_TICK = 2;
  public static final int EFFECT_HEAVY_CLICK = 5;
  public static VibrationEffect createOneShot(long milliseconds, int amplitude) { return null; }
  public static VibrationEffect createWaveform(long[] timings, int repeat) { return null; }
  public static VibrationEffect createWaveform(long[] timings, int[] amplitudes, int repeat) { return null; }
  public static VibrationEffect createPredefined(int effectId) { return null; }
  public static Composition startComposition() { return null; }
  public static final class Composition {
    public static final int PRIMITIVE_CLICK = 1;
    public static final int PRIMITIVE_THUD = 2;
    public static final int PRIMITIVE_SPIN = 3;
    public static final int PRIMITIVE_QUICK_RISE = 4;
    public static final int PRIMITIVE_SLOW_RISE = 5;
    public static final int PRIMITIVE_QUICK_FALL = 6;
    public static final int PRIMITIVE_TICK = 7;
    public static final int PRIMITIVE_LOW_TICK = 8;
    public Composition addPrimitive(int primitiveId) { return this; }
    public Composition addPrimitive(int primitiveId, float scale) { return this; }
    public Composition addPrimitive(int primitiveId, float scale, int delay) { return this; }
    public VibrationEffect compose() { return null; }
  }
}
//// FILE: android/os/Vibrator.java
package android.os;
public abstract class Vibrator {
  public boolean hasVibrator() { return true; }
  public boolean hasAmplitudeControl() { return true; }
  public void vibrate(VibrationEffect effect) {}
  public void vibrate(long milliseconds) {}
  public void cancel() {}
  public boolean areAllPrimitivesSupported(int... primitiveIds) { return true; }
  public int[] arePrimitivesSupported(int... primitiveIds) { return null; }
  public boolean[] arePrimitivesSupportedArray(int... primitiveIds) { return null; }
  public int areAllEffectsSupported(int... effectIds) { return 0; }
  public static final int VIBRATION_EFFECT_SUPPORT_YES = 1;
}
//// FILE: android/os/VibratorManager.java
package android.os;
public abstract class VibratorManager {
  public abstract Vibrator getDefaultVibrator();
  public abstract int[] getVibratorIds();
}
//// FILE: android/os/UserManager.java
package android.os;
public class UserManager { public boolean isUserUnlocked() { return true; } }
//// FILE: android/net/Uri.java
package android.net;
public abstract class Uri implements android.os.Parcelable, Comparable<Uri> {
  public static Uri parse(String s) { return null; }
  public static Uri fromFile(java.io.File f) { return null; }
  public static String encode(String s) { return null; }
  public abstract String getScheme();
  public abstract String getPath();
  public abstract String getLastPathSegment();
  public abstract String getHost();
  public abstract String getQueryParameter(String key);
  public abstract java.util.List<String> getPathSegments();
  public abstract String getAuthority();
  public abstract boolean isHierarchical();
  public String toString() { return ""; }
  public int compareTo(Uri o) { return 0; }
  public static Uri withAppendedPath(Uri base, String pathSegment) { return null; }
  public static final Uri EMPTY = null;
}
//// FILE: android/content/Context.java
package android.content;
public abstract class Context {
  public static final int MODE_PRIVATE = 0;
  public static final int MODE_APPEND = 0x8000;
  public static final int RECEIVER_EXPORTED = 2;
  public static final int RECEIVER_NOT_EXPORTED = 4;
  public static final int BIND_AUTO_CREATE = 1;
  public static final String ALARM_SERVICE = "alarm";
  public static final String NOTIFICATION_SERVICE = "notification";
  public static final String INPUT_METHOD_SERVICE = "input_method";
  public static final String KEYGUARD_SERVICE = "keyguard";
  public static final String POWER_SERVICE = "power";
  public static final String TELECOM_SERVICE = "telecom";
  public static final String VIBRATOR_MANAGER_SERVICE = "vibrator_manager";
  public static final String VIBRATOR_SERVICE = "vibrator";
  public static final String WINDOW_SERVICE = "window";
  public static final String USAGE_STATS_SERVICE = "usagestats";
  public static final String APP_OPS_SERVICE = "appops";
  public static final String UI_MODE_SERVICE = "uimode";
  public static final String ACCESSIBILITY_SERVICE = "accessibility";
  public static final String AUDIO_SERVICE = "audio";
  public static final String ACTIVITY_SERVICE = "activity";
  public static final String USER_SERVICE = "user";
  public static final String CONNECTIVITY_SERVICE = "connectivity";
  public static final String CLIPBOARD_SERVICE = "clipboard";
  public static final String LAYOUT_INFLATER_SERVICE = "layout_inflater";
  public abstract SharedPreferences getSharedPreferences(String name, int mode);
  public abstract Object getSystemService(String name);
  public final <T> T getSystemService(Class<T> serviceClass) { return null; }
  public abstract String getPackageName();
  public abstract android.content.pm.PackageManager getPackageManager();
  public abstract Context getApplicationContext();
  public abstract android.content.res.Resources getResources();
  public abstract ContentResolver getContentResolver();
  public abstract android.os.Looper getMainLooper();
  public abstract android.content.pm.ApplicationInfo getApplicationInfo();
  public abstract java.io.File getFilesDir();
  public abstract java.io.File getCacheDir();
  public abstract java.io.File getNoBackupFilesDir();
  public abstract java.io.File getDataDir();
  public abstract java.io.File getExternalFilesDir(String type);
  public abstract java.io.File getDir(String name, int mode);
  public abstract java.io.FileInputStream openFileInput(String name) throws java.io.FileNotFoundException;
  public abstract java.io.FileOutputStream openFileOutput(String name, int mode) throws java.io.FileNotFoundException;
  public abstract boolean deleteFile(String name);
  public abstract void startActivity(Intent intent);
  public abstract void startActivity(Intent intent, android.os.Bundle options);
  public abstract void sendBroadcast(Intent intent);
  public abstract ComponentName startService(Intent service);
  public abstract ComponentName startForegroundService(Intent service);
  public abstract boolean stopService(Intent service);
  public abstract Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter);
  public abstract Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags);
  public abstract void unregisterReceiver(BroadcastReceiver receiver);
  public abstract int checkSelfPermission(String permission);
  public int checkCallingOrSelfPermission(String permission) { return 0; }
  public abstract Context createConfigurationContext(android.content.res.Configuration overrideConfiguration);
  public abstract android.content.res.Resources.Theme getTheme();
  public final String getString(int resId) { return null; }
  public final String getString(int resId, Object... formatArgs) { return null; }
  public final CharSequence getText(int resId) { return null; }
  public final int getColor(int id) { return 0; }
  public final android.graphics.drawable.Drawable getDrawable(int id) { return null; }
  public final java.util.concurrent.Executor getMainExecutor() { return null; }
  public abstract ClassLoader getClassLoader();
  public abstract java.io.File getDatabasePath(String name);
  public boolean isRestricted() { return false; }
}
//// FILE: android/content/ContextWrapper.java
package android.content;
public class ContextWrapper extends Context {
  public ContextWrapper(Context base) {}
  public Context getBaseContext() { return null; }
  protected void attachBaseContext(Context base) {}
  public SharedPreferences getSharedPreferences(String name, int mode) { return null; }
  public Object getSystemService(String name) { return null; }
  public String getPackageName() { return null; }
  public android.content.pm.PackageManager getPackageManager() { return null; }
  public Context getApplicationContext() { return null; }
  public android.content.res.Resources getResources() { return null; }
  public ContentResolver getContentResolver() { return null; }
  public android.os.Looper getMainLooper() { return null; }
  public android.content.pm.ApplicationInfo getApplicationInfo() { return null; }
  public java.io.File getFilesDir() { return null; }
  public java.io.File getCacheDir() { return null; }
  public java.io.File getNoBackupFilesDir() { return null; }
  public java.io.File getDataDir() { return null; }
  public java.io.File getExternalFilesDir(String type) { return null; }
  public java.io.File getDir(String name, int mode) { return null; }
  public java.io.FileInputStream openFileInput(String name) throws java.io.FileNotFoundException { return null; }
  public java.io.FileOutputStream openFileOutput(String name, int mode) throws java.io.FileNotFoundException { return null; }
  public boolean deleteFile(String name) { return false; }
  public void startActivity(Intent intent) {}
  public void startActivity(Intent intent, android.os.Bundle options) {}
  public void sendBroadcast(Intent intent) {}
  public ComponentName startService(Intent service) { return null; }
  public ComponentName startForegroundService(Intent service) { return null; }
  public boolean stopService(Intent service) { return false; }
  public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) { return null; }
  public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags) { return null; }
  public void unregisterReceiver(BroadcastReceiver receiver) {}
  public int checkSelfPermission(String permission) { return 0; }
  public Context createConfigurationContext(android.content.res.Configuration c) { return null; }
  public android.content.res.Resources.Theme getTheme() { return null; }
  public ClassLoader getClassLoader() { return null; }
  public java.io.File getDatabasePath(String name) { return null; }
}
//// FILE: android/content/ContextThemeWrapper.java
package android.content;
public class ContextThemeWrapper extends ContextWrapper { public ContextThemeWrapper() { super(null); } public ContextThemeWrapper(Context base, int themeResId) { super(base); } }
//// FILE: android/content/ContentResolver.java
package android.content;
public abstract class ContentResolver {
  public final java.io.InputStream openInputStream(android.net.Uri uri) throws java.io.FileNotFoundException { return null; }
  public final java.io.OutputStream openOutputStream(android.net.Uri uri) throws java.io.FileNotFoundException { return null; }
  public final java.io.OutputStream openOutputStream(android.net.Uri uri, String mode) throws java.io.FileNotFoundException { return null; }
  public final android.os.ParcelFileDescriptor openFileDescriptor(android.net.Uri uri, String mode) throws java.io.FileNotFoundException { return null; }
  public final android.database.Cursor query(android.net.Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) { return null; }
  public final String getType(android.net.Uri uri) { return null; }
  public final void registerContentObserver(android.net.Uri uri, boolean notifyForDescendants, android.database.ContentObserver observer) {}
  public final void unregisterContentObserver(android.database.ContentObserver observer) {}
  public void takePersistableUriPermission(android.net.Uri uri, int modeFlags) {}
}
//// FILE: android/content/SharedPreferences.java
package android.content;
public interface SharedPreferences {
  java.util.Map<String, ?> getAll();
  String getString(String key, String defValue);
  java.util.Set<String> getStringSet(String key, java.util.Set<String> defValues);
  int getInt(String key, int defValue);
  long getLong(String key, long defValue);
  float getFloat(String key, float defValue);
  boolean getBoolean(String key, boolean defValue);
  boolean contains(String key);
  Editor edit();
  void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener);
  void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener);
  interface OnSharedPreferenceChangeListener { void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key); }
  interface Editor {
    Editor putString(String key, String value);
    Editor putStringSet(String key, java.util.Set<String> values);
    Editor putInt(String key, int value);
    Editor putLong(String key, long value);
    Editor putFloat(String key, float value);
    Editor putBoolean(String key, boolean value);
    Editor remove(String key);
    Editor clear();
    boolean commit();
    void apply();
  }
}
//// FILE: android/content/Intent.java
package android.content;
public class Intent implements android.os.Parcelable, Cloneable {
  public static final String ACTION_MAIN = "android.intent.action.MAIN";
  public static final String ACTION_VIEW = "android.intent.action.VIEW";
  public static final String ACTION_SEND = "android.intent.action.SEND";
  public static final String ACTION_USER_PRESENT = "android.intent.action.USER_PRESENT";
  public static final String ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";
  public static final String ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
  public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
  public static final String ACTION_MY_PACKAGE_REPLACED = "android.intent.action.MY_PACKAGE_REPLACED";
  public static final String ACTION_TIME_CHANGED = "android.intent.action.TIME_SET";
  public static final String ACTION_TIMEZONE_CHANGED = "android.intent.action.TIMEZONE_CHANGED";
  public static final String ACTION_DATE_CHANGED = "android.intent.action.DATE_CHANGED";
  public static final String ACTION_OPEN_DOCUMENT = "android.intent.action.OPEN_DOCUMENT";
  public static final String ACTION_SENDTO = "android.intent.action.SENDTO";
  public static final String CATEGORY_HOME = "android.intent.category.HOME";
  public static final String CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER";
  public static final String CATEGORY_DEFAULT = "android.intent.category.DEFAULT";
  public static final String CATEGORY_OPENABLE = "android.intent.category.OPENABLE";
  public static final String EXTRA_TEXT = "android.intent.extra.TEXT";
  public static final String EXTRA_SUBJECT = "android.intent.extra.SUBJECT";
  public static final String EXTRA_EMAIL = "android.intent.extra.EMAIL";
  public static final String EXTRA_STREAM = "android.intent.extra.STREAM";
  public static final String EXTRA_TITLE = "android.intent.extra.TITLE";
  public static final String EXTRA_COMPONENT_NAME = "android.intent.extra.COMPONENT_NAME";
  public static final int FLAG_ACTIVITY_NEW_TASK = 0x10000000;
  public static final int FLAG_ACTIVITY_CLEAR_TOP = 0x04000000;
  public static final int FLAG_ACTIVITY_CLEAR_TASK = 0x00008000;
  public static final int FLAG_ACTIVITY_SINGLE_TOP = 0x20000000;
  public static final int FLAG_ACTIVITY_REORDER_TO_FRONT = 0x00020000;
  public static final int FLAG_ACTIVITY_NO_ANIMATION = 0x00010000;
  public static final int FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS = 0x00800000;
  public static final int FLAG_ACTIVITY_NO_HISTORY = 0x40000000;
  public static final int FLAG_GRANT_READ_URI_PERMISSION = 1;
  public static final int FLAG_GRANT_WRITE_URI_PERMISSION = 2;
  public static final int FLAG_RECEIVER_FOREGROUND = 0x10000000;
  public Intent() {}
  public Intent(String action) {}
  public Intent(String action, android.net.Uri uri) {}
  public Intent(Context packageContext, Class<?> cls) {}
  public Intent(String action, android.net.Uri uri, Context packageContext, Class<?> cls) {}
  public Intent(Intent o) {}
  public static Intent createChooser(Intent target, CharSequence title) { return null; }
  public String getAction() { return null; }
  public Intent setAction(String action) { return this; }
  public android.net.Uri getData() { return null; }
  public Intent setData(android.net.Uri data) { return this; }
  public Intent setDataAndType(android.net.Uri data, String type) { return this; }
  public String getType() { return null; }
  public Intent setType(String type) { return this; }
  public String getPackage() { return null; }
  public Intent setPackage(String packageName) { return this; }
  public ComponentName getComponent() { return null; }
  public Intent setComponent(ComponentName component) { return this; }
  public Intent setClassName(String packageName, String className) { return this; }
  public Intent setClassName(Context packageContext, String className) { return this; }
  public Intent setClass(Context packageContext, Class<?> cls) { return this; }
  public int getFlags() { return 0; }
  public Intent setFlags(int flags) { return this; }
  public Intent addFlags(int flags) { return this; }
  public Intent addCategory(String category) { return this; }
  public java.util.Set<String> getCategories() { return null; }
  public boolean hasCategory(String category) { return false; }
  public android.os.Bundle getExtras() { return null; }
  public Intent putExtras(android.os.Bundle extras) { return this; }
  public Intent putExtras(Intent src) { return this; }
  public boolean hasExtra(String name) { return false; }
  public void removeExtra(String name) {}
  public Intent putExtra(String name, String value) { return this; }
  public Intent putExtra(String name, CharSequence value) { return this; }
  public Intent putExtra(String name, int value) { return this; }
  public Intent putExtra(String name, long value) { return this; }
  public Intent putExtra(String name, boolean value) { return this; }
  public Intent putExtra(String name, float value) { return this; }
  public Intent putExtra(String name, double value) { return this; }
  public Intent putExtra(String name, String[] value) { return this; }
  public Intent putExtra(String name, int[] value) { return this; }
  public Intent putExtra(String name, long[] value) { return this; }
  public Intent putExtra(String name, android.os.Parcelable value) { return this; }
  public Intent putExtra(String name, android.os.Bundle value) { return this; }
  public Intent putExtra(String name, java.io.Serializable value) { return this; }
  public Intent putStringArrayListExtra(String name, java.util.ArrayList<String> value) { return this; }
  public String getStringExtra(String name) { return null; }
  public CharSequence getCharSequenceExtra(String name) { return null; }
  public int getIntExtra(String name, int defaultValue) { return 0; }
  public long getLongExtra(String name, long defaultValue) { return 0; }
  public boolean getBooleanExtra(String name, boolean defaultValue) { return false; }
  public float getFloatExtra(String name, float defaultValue) { return 0; }
  public double getDoubleExtra(String name, double defaultValue) { return 0; }
  public String[] getStringArrayExtra(String name) { return null; }
  public int[] getIntArrayExtra(String name) { return null; }
  public long[] getLongArrayExtra(String name) { return null; }
  public java.util.ArrayList<String> getStringArrayListExtra(String name) { return null; }
  public android.os.Bundle getBundleExtra(String name) { return null; }
  public <T extends android.os.Parcelable> T getParcelableExtra(String name) { return null; }
  public <T> T getParcelableExtra(String name, Class<T> clazz) { return null; }
  public ComponentName resolveActivity(android.content.pm.PackageManager pm) { return null; }
  public String toUri(int flags) { return null; }
  public Object clone() { return null; }
  public static Intent parseUri(String uri, int flags) { return null; }
  public static Intent makeMainActivity(ComponentName mainActivity) { return null; }
  public boolean filterEquals(Intent other) { return false; }
}
//// FILE: android/content/IntentFilter.java
package android.content;
public class IntentFilter {
  public IntentFilter() {}
  public IntentFilter(String action) {}
  public final void addAction(String action) {}
  public final void addCategory(String category) {}
  public final void setPriority(int priority) {}
}
//// FILE: android/content/BroadcastReceiver.java
package android.content;
public abstract class BroadcastReceiver {
  public BroadcastReceiver() {}
  public abstract void onReceive(Context context, Intent intent);
  public final PendingResult goAsync() { return null; }
  public static class PendingResult { public final void finish() {} }
}
//// FILE: android/content/ComponentName.java
package android.content;
public final class ComponentName implements android.os.Parcelable, Comparable<ComponentName> {
  public ComponentName(String pkg, String cls) {}
  public ComponentName(Context pkg, String cls) {}
  public ComponentName(Context pkg, Class<?> cls) {}
  public String getPackageName() { return null; }
  public String getClassName() { return null; }
  public String getShortClassName() { return null; }
  public String flattenToString() { return null; }
  public String flattenToShortString() { return null; }
  public static ComponentName unflattenFromString(String str) { return null; }
  public int compareTo(ComponentName o) { return 0; }
}
//// FILE: android/content/ClipboardManager.java
package android.content;
public class ClipboardManager { }
//// FILE: android/content/res/Resources.java
package android.content.res;
public class Resources {
  public android.util.DisplayMetrics getDisplayMetrics() { return null; }
  public Configuration getConfiguration() { return null; }
  public String getString(int id) { return null; }
  public String getString(int id, Object... formatArgs) { return null; }
  public java.io.InputStream openRawResource(int id) { return null; }
  public android.graphics.Typeface getFont(int id) { return null; }
  public int getColor(int id, Theme theme) { return 0; }
  public android.graphics.drawable.Drawable getDrawable(int id, Theme theme) { return null; }
  public float getDimension(int id) { return 0; }
  public int getDimensionPixelSize(int id) { return 0; }
  public int getIdentifier(String name, String defType, String defPackage) { return 0; }
  public static Resources getSystem() { return null; }
  public final class Theme { }
}
//// FILE: android/content/res/Configuration.java
package android.content.res;
public final class Configuration {
  public static final int UI_MODE_NIGHT_MASK = 0x30;
  public static final int UI_MODE_NIGHT_YES = 0x20;
  public static final int UI_MODE_NIGHT_NO = 0x10;
  public static final int ORIENTATION_LANDSCAPE = 2;
  public static final int ORIENTATION_PORTRAIT = 1;
  public int uiMode;
  public float fontScale;
  public int orientation;
  public int screenWidthDp;
  public int screenHeightDp;
  public int densityDpi;
  public java.util.Locale locale;
  public Configuration() {}
  public Configuration(Configuration o) {}
  public void setLocale(java.util.Locale loc) {}
  public android.os.LocaleList getLocales() { return null; }
  public boolean isNightModeActive() { return false; }
}
//// FILE: android/os/LocaleList.java
package android.os;
public final class LocaleList { public java.util.Locale get(int i) { return null; } public int size() { return 0; } public static LocaleList getDefault() { return null; } public boolean isEmpty() { return true; } }
//// FILE: android/util/DisplayMetrics.java
package android.util;
public class DisplayMetrics { public float density; public int densityDpi; public int widthPixels; public int heightPixels; public float scaledDensity; public float xdpi; public float ydpi; }
//// FILE: android/content/pm/PackageManager.java
package android.content.pm;
import android.content.Intent;
public abstract class PackageManager {
  public static final int PERMISSION_GRANTED = 0;
  public static final int PERMISSION_DENIED = -1;
  public static final int GET_META_DATA = 0x80;
  public static final int MATCH_DEFAULT_ONLY = 0x10000;
  public static final int MATCH_ALL = 0x20000;
  public static final int GET_ACTIVITIES = 1;
  public static final int COMPONENT_ENABLED_STATE_ENABLED = 1;
  public static final int COMPONENT_ENABLED_STATE_DISABLED = 2;
  public static final int DONT_KILL_APP = 1;
  public static class NameNotFoundException extends Exception { public NameNotFoundException() {} public NameNotFoundException(String n) {} }
  public static final class ResolveInfoFlags { public static ResolveInfoFlags of(long v) { return null; } }
  public static final class ApplicationInfoFlags { public static ApplicationInfoFlags of(long v) { return null; } }
  public static final class PackageInfoFlags { public static PackageInfoFlags of(long v) { return null; } }
  public abstract Intent getLaunchIntentForPackage(String packageName);
  public abstract ActivityInfo getActivityInfo(android.content.ComponentName component, int flags) throws NameNotFoundException;
  public abstract ApplicationInfo getApplicationInfo(String packageName, int flags) throws NameNotFoundException;
  public ApplicationInfo getApplicationInfo(String packageName, ApplicationInfoFlags flags) throws NameNotFoundException { return null; }
  public abstract PackageInfo getPackageInfo(String packageName, int flags) throws NameNotFoundException;
  public PackageInfo getPackageInfo(String packageName, PackageInfoFlags flags) throws NameNotFoundException { return null; }
  public abstract CharSequence getApplicationLabel(ApplicationInfo info);
  public abstract android.graphics.drawable.Drawable getApplicationIcon(ApplicationInfo info);
  public abstract android.graphics.drawable.Drawable getApplicationIcon(String packageName) throws NameNotFoundException;
  public abstract java.util.List<ResolveInfo> queryIntentActivities(Intent intent, int flags);
  public java.util.List<ResolveInfo> queryIntentActivities(Intent intent, ResolveInfoFlags flags) { return null; }
  public abstract ResolveInfo resolveActivity(Intent intent, int flags);
  public ResolveInfo resolveActivity(Intent intent, ResolveInfoFlags flags) { return null; }
  public abstract java.util.List<ApplicationInfo> getInstalledApplications(int flags);
  public abstract java.util.List<PackageInfo> getInstalledPackages(int flags);
  public abstract void setComponentEnabledSetting(android.content.ComponentName componentName, int newState, int flags);
  public abstract int getComponentEnabledSetting(android.content.ComponentName componentName);
  public abstract boolean hasSystemFeature(String name);
  public abstract int checkPermission(String permName, String packageName);
}
//// FILE: android/content/pm/ApplicationInfo.java
package android.content.pm;
public class ApplicationInfo extends PackageItemInfo {
  public static final int FLAG_SYSTEM = 1;
  public static final int FLAG_UPDATED_SYSTEM_APP = 0x80;
  public static final int CATEGORY_UNDEFINED = -1;
  public static final int CATEGORY_GAME = 0;
  public static final int CATEGORY_AUDIO = 1;
  public static final int CATEGORY_VIDEO = 2;
  public static final int CATEGORY_IMAGE = 3;
  public static final int CATEGORY_SOCIAL = 4;
  public static final int CATEGORY_NEWS = 5;
  public static final int CATEGORY_MAPS = 6;
  public static final int CATEGORY_PRODUCTIVITY = 7;
  public static final int CATEGORY_ACCESSIBILITY = 8;
  public int flags;
  public int category;
  public int uid;
  public boolean enabled;
  public int targetSdkVersion;
  public String sourceDir;
  public String dataDir;
  public CharSequence loadLabel(PackageManager pm) { return null; }
  public android.graphics.drawable.Drawable loadIcon(PackageManager pm) { return null; }
}
//// FILE: android/content/pm/PackageItemInfo.java
package android.content.pm;
public class PackageItemInfo { public String packageName; public String name; public int icon; public int labelRes; public CharSequence nonLocalizedLabel; public CharSequence loadLabel(PackageManager pm) { return null; } public android.graphics.drawable.Drawable loadIcon(PackageManager pm) { return null; } }
//// FILE: android/content/pm/ComponentInfo.java
package android.content.pm;
public class ComponentInfo extends PackageItemInfo { public ApplicationInfo applicationInfo; public boolean enabled; public boolean exported; }
//// FILE: android/content/pm/ActivityInfo.java
package android.content.pm;
public class ActivityInfo extends ComponentInfo { public int launchMode; public String taskAffinity; public static final int SCREEN_ORIENTATION_PORTRAIT = 1; }
//// FILE: android/content/pm/ServiceInfo.java
package android.content.pm;
public class ServiceInfo extends ComponentInfo { }
//// FILE: android/content/pm/ResolveInfo.java
package android.content.pm;
public class ResolveInfo { public ActivityInfo activityInfo; public ServiceInfo serviceInfo; public int priority; public boolean isDefault; public CharSequence loadLabel(PackageManager pm) { return null; } public android.graphics.drawable.Drawable loadIcon(PackageManager pm) { return null; } }
//// FILE: android/content/pm/PackageInfo.java
package android.content.pm;
public class PackageInfo { public String packageName; public String versionName; public int versionCode; public long firstInstallTime; public long lastUpdateTime; public ApplicationInfo applicationInfo; public long getLongVersionCode() { return 0; } }
//// FILE: android/database/ContentObserver.java
package android.database;
public abstract class ContentObserver {
  public ContentObserver(android.os.Handler handler) {}
  public void onChange(boolean selfChange) {}
  public void onChange(boolean selfChange, android.net.Uri uri) {}
  public boolean deliverSelfNotifications() { return false; }
}
//// FILE: android/database/Cursor.java
package android.database;
public interface Cursor extends java.io.Closeable {
  boolean moveToFirst();
  boolean moveToNext();
  int getColumnIndex(String columnName);
  int getColumnIndexOrThrow(String columnName);
  String getString(int columnIndex);
  long getLong(int columnIndex);
  int getInt(int columnIndex);
  boolean isNull(int columnIndex);
  int getCount();
  void close();
}
//// FILE: android/provider/OpenableColumns.java
package android.provider;
public interface OpenableColumns { String DISPLAY_NAME = "_display_name"; String SIZE = "_size"; }
//// FILE: android/provider/Settings.java
package android.provider;
public final class Settings {
  public static final String ACTION_SETTINGS = "android.settings.SETTINGS";
  public static final String ACTION_ACCESSIBILITY_SETTINGS = "android.settings.ACCESSIBILITY_SETTINGS";
  public static final String ACTION_APPLICATION_DETAILS_SETTINGS = "android.settings.APPLICATION_DETAILS_SETTINGS";
  public static final String ACTION_APP_NOTIFICATION_SETTINGS = "android.settings.APP_NOTIFICATION_SETTINGS";
  public static final String ACTION_CHANNEL_NOTIFICATION_SETTINGS = "android.settings.CHANNEL_NOTIFICATION_SETTINGS";
  public static final String ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS = "android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS";
  public static final String ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = "android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS";
  public static final String ACTION_REQUEST_SCHEDULE_EXACT_ALARM = "android.settings.REQUEST_SCHEDULE_EXACT_ALARM";
  public static final String ACTION_USAGE_ACCESS_SETTINGS = "android.settings.USAGE_ACCESS_SETTINGS";
  public static final String ACTION_MANAGE_OVERLAY_PERMISSION = "android.settings.action.MANAGE_OVERLAY_PERMISSION";
  public static final String ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT = "android.settings.MANAGE_APP_USE_FULL_SCREEN_INTENT";
  public static final String EXTRA_APP_PACKAGE = "android.provider.extra.APP_PACKAGE";
  public static final String EXTRA_CHANNEL_ID = "android.provider.extra.CHANNEL_ID";
  public static boolean canDrawOverlays(android.content.Context context) { return false; }
  public static final class Secure {
    public static final String ENABLED_ACCESSIBILITY_SERVICES = "enabled_accessibility_services";
    public static final String ACCESSIBILITY_ENABLED = "accessibility_enabled";
    public static String getString(android.content.ContentResolver resolver, String name) { return null; }
    public static int getInt(android.content.ContentResolver cr, String name, int def) { return 0; }
    public static android.net.Uri getUriFor(String name) { return null; }
  }
  public static final class Global {
    public static final String ANIMATOR_DURATION_SCALE = "animator_duration_scale";
    public static final String TRANSITION_ANIMATION_SCALE = "transition_animation_scale";
    public static float getFloat(android.content.ContentResolver cr, String name, float def) { return 0; }
    public static float getFloat(android.content.ContentResolver cr, String name) throws SettingNotFoundException { return 0; }
    public static int getInt(android.content.ContentResolver cr, String name, int def) { return 0; }
    public static android.net.Uri getUriFor(String name) { return null; }
  }
  public static final class System {
    public static final String FONT_SCALE = "font_scale";
    public static android.net.Uri getUriFor(String name) { return null; }
    public static float getFloat(android.content.ContentResolver cr, String name, float def) { return 0; }
  }
  public static class SettingNotFoundException extends Exception { }
}
//// FILE: android/speech/RecognitionListener.java
package android.speech;
public interface RecognitionListener {
  void onReadyForSpeech(android.os.Bundle params);
  void onBeginningOfSpeech();
  void onRmsChanged(float rmsdB);
  void onBufferReceived(byte[] buffer);
  void onEndOfSpeech();
  void onError(int error);
  void onResults(android.os.Bundle results);
  void onPartialResults(android.os.Bundle partialResults);
  void onEvent(int eventType, android.os.Bundle params);
  default void onSegmentResults(android.os.Bundle segmentResults) {}
  default void onEndOfSegmentedSession() {}
  default void onLanguageDetection(android.os.Bundle results) {}
}
//// FILE: android/speech/RecognizerIntent.java
package android.speech;
public class RecognizerIntent {
  public static final String ACTION_RECOGNIZE_SPEECH = "android.speech.action.RECOGNIZE_SPEECH";
  public static final String EXTRA_BIASING_STRINGS = "android.speech.extra.BIASING_STRINGS";
  public static final String EXTRA_ENABLE_FORMATTING = "android.speech.extra.ENABLE_FORMATTING";
  public static final String EXTRA_LANGUAGE = "android.speech.extra.LANGUAGE";
  public static final String EXTRA_LANGUAGE_MODEL = "android.speech.extra.LANGUAGE_MODEL";
  public static final String EXTRA_LANGUAGE_PREFERENCE = "android.speech.extra.LANGUAGE_PREFERENCE";
  public static final String EXTRA_MAX_RESULTS = "android.speech.extra.MAX_RESULTS";
  public static final String EXTRA_PARTIAL_RESULTS = "android.speech.extra.PARTIAL_RESULTS";
  public static final String EXTRA_PREFER_OFFLINE = "android.speech.extra.PREFER_OFFLINE";
  public static final String EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS = "a";
  public static final String EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS = "b";
  public static final String EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS = "c";
  public static final String FORMATTING_OPTIMIZE_QUALITY = "quality";
  public static final String FORMATTING_OPTIMIZE_LATENCY = "latency";
  public static final String LANGUAGE_MODEL_FREE_FORM = "free_form";
  public static final String EXTRA_CALLING_PACKAGE = "calling_package";
}
//// FILE: android/speech/SpeechRecognizer.java
package android.speech;
public class SpeechRecognizer {
  public static final int ERROR_NETWORK_TIMEOUT = 1;
  public static final int ERROR_NETWORK = 2;
  public static final int ERROR_AUDIO = 3;
  public static final int ERROR_SERVER = 4;
  public static final int ERROR_CLIENT = 5;
  public static final int ERROR_SPEECH_TIMEOUT = 6;
  public static final int ERROR_NO_MATCH = 7;
  public static final int ERROR_RECOGNIZER_BUSY = 8;
  public static final int ERROR_INSUFFICIENT_PERMISSIONS = 9;
  public static final int ERROR_TOO_MANY_REQUESTS = 10;
  public static final int ERROR_SERVER_DISCONNECTED = 11;
  public static final int ERROR_LANGUAGE_NOT_SUPPORTED = 12;
  public static final int ERROR_LANGUAGE_UNAVAILABLE = 13;
  public static final String RESULTS_RECOGNITION = "results_recognition";
  public static final String CONFIDENCE_SCORES = "confidence_scores";
  public static boolean isRecognitionAvailable(android.content.Context context) { return false; }
  public static boolean isOnDeviceRecognitionAvailable(android.content.Context context) { return false; }
  public static SpeechRecognizer createSpeechRecognizer(android.content.Context context) { return null; }
  public static SpeechRecognizer createSpeechRecognizer(android.content.Context context, android.content.ComponentName serviceComponent) { return null; }
  public static SpeechRecognizer createOnDeviceSpeechRecognizer(android.content.Context context) { return null; }
  public void setRecognitionListener(RecognitionListener listener) {}
  public void startListening(android.content.Intent recognizerIntent) {}
  public void stopListening() {}
  public void cancel() {}
  public void destroy() {}
}
//// FILE: android/telecom/TelecomManager.java
package android.telecom;
public class TelecomManager { public boolean isInCall() { return false; } public boolean isInManagedCall() { return false; } public String getDefaultDialerPackage() { return null; } }
//// FILE: android/text/Spanned.java
package android.text;
public interface Spanned extends CharSequence {
  int SPAN_EXCLUSIVE_EXCLUSIVE = 33;
  int SPAN_INCLUSIVE_EXCLUSIVE = 17;
  int SPAN_EXCLUSIVE_INCLUSIVE = 34;
  int SPAN_INCLUSIVE_INCLUSIVE = 18;
  <T> T[] getSpans(int start, int end, Class<T> type);
  int getSpanStart(Object tag);
  int getSpanEnd(Object tag);
}
//// FILE: android/text/Spannable.java
package android.text;
public interface Spannable extends Spanned { void setSpan(Object what, int start, int end, int flags); void removeSpan(Object what); }
//// FILE: android/text/SpannableString.java
package android.text;
public class SpannableString implements Spannable {
  public SpannableString(CharSequence source) {}
  public void setSpan(Object what, int start, int end, int flags) {}
  public void removeSpan(Object what) {}
  public <T> T[] getSpans(int start, int end, Class<T> type) { return null; }
  public int getSpanStart(Object tag) { return 0; }
  public int getSpanEnd(Object tag) { return 0; }
  public int length() { return 0; }
  public char charAt(int i) { return 0; }
  public CharSequence subSequence(int s, int e) { return null; }
  public String toString() { return ""; }
}
//// FILE: android/text/SpannableStringBuilder.java
package android.text;
public class SpannableStringBuilder implements Spannable {
  public SpannableStringBuilder() {}
  public SpannableStringBuilder(CharSequence text) {}
  public SpannableStringBuilder append(CharSequence text) { return this; }
  public void setSpan(Object what, int start, int end, int flags) {}
  public void removeSpan(Object what) {}
  public <T> T[] getSpans(int start, int end, Class<T> type) { return null; }
  public int getSpanStart(Object tag) { return 0; }
  public int getSpanEnd(Object tag) { return 0; }
  public int length() { return 0; }
  public char charAt(int i) { return 0; }
  public CharSequence subSequence(int s, int e) { return null; }
  public String toString() { return ""; }
}
//// FILE: android/text/style/ForegroundColorSpan.java
package android.text.style;
public class ForegroundColorSpan { public ForegroundColorSpan(int color) {} public int getForegroundColor() { return 0; } }
//// FILE: android/text/style/RelativeSizeSpan.java
package android.text.style;
public class RelativeSizeSpan { public RelativeSizeSpan(float proportion) {} }
//// FILE: android/text/style/StyleSpan.java
package android.text.style;
public class StyleSpan { public StyleSpan(int style) {} }
//// FILE: android/util/Log.java
package android.util;
public final class Log {
  public static int d(String tag, String msg) { return 0; }
  public static int d(String tag, String msg, Throwable tr) { return 0; }
  public static int i(String tag, String msg) { return 0; }
  public static int w(String tag, String msg) { return 0; }
  public static int w(String tag, String msg, Throwable tr) { return 0; }
  public static int w(String tag, Throwable tr) { return 0; }
  public static int e(String tag, String msg) { return 0; }
  public static int e(String tag, String msg, Throwable tr) { return 0; }
  public static int v(String tag, String msg) { return 0; }
  public static String getStackTraceString(Throwable tr) { return null; }
}
//// FILE: android/util/LruCache.java
package android.util;
public class LruCache<K, V> {
  public LruCache(int maxSize) {}
  public final V get(K key) { return null; }
  public final V put(K key, V value) { return null; }
  public final V remove(K key) { return null; }
  protected int sizeOf(K key, V value) { return 1; }
  protected void entryRemoved(boolean evicted, K key, V oldValue, V newValue) {}
  protected V create(K key) { return null; }
  public final void evictAll() {}
  public final int size() { return 0; }
  public final int maxSize() { return 0; }
  public void resize(int maxSize) {}
  public final java.util.Map<K, V> snapshot() { return null; }
  public final void trimToSize(int maxSize) {}
}
//// FILE: android/util/SizeF.java
package android.util;
public final class SizeF { public SizeF(float w, float h) {} public float getWidth() { return 0; } public float getHeight() { return 0; } }
//// FILE: android/util/Size.java
package android.util;
public final class Size { public Size(int w, int h) {} public int getWidth() { return 0; } public int getHeight() { return 0; } }
//// FILE: android/util/TypedValue.java
package android.util;
public class TypedValue {
  public static final int COMPLEX_UNIT_PX = 0;
  public static final int COMPLEX_UNIT_DIP = 1;
  public static final int COMPLEX_UNIT_SP = 2;
  public static float applyDimension(int unit, float value, DisplayMetrics metrics) { return 0; }
}
//// FILE: android/view/Gravity.java
package android.view;
public class Gravity {
  public static final int CENTER = 17; public static final int CENTER_HORIZONTAL = 1; public static final int CENTER_VERTICAL = 16;
  public static final int START = 0x00800003; public static final int END = 0x00800005; public static final int TOP = 48; public static final int BOTTOM = 80; public static final int LEFT = 3; public static final int RIGHT = 5;
  public static final int FILL = 119; public static final int FILL_HORIZONTAL = 7;
}
//// FILE: android/view/View.java
package android.view;
public class View {
  public static final int VISIBLE = 0;
  public static final int INVISIBLE = 4;
  public static final int GONE = 8;
  public static final int IMPORTANT_FOR_ACCESSIBILITY_YES = 1;
  public static final int IMPORTANT_FOR_ACCESSIBILITY_NO = 2;
  public static final int HAPTIC_FEEDBACK_ENABLED = 1;
  public static final int SYSTEM_UI_FLAG_LAYOUT_STABLE = 0x100;
  public View(android.content.Context context) {}
  public android.content.Context getContext() { return null; }
  public android.content.res.Resources getResources() { return null; }
  public int getWidth() { return 0; }
  public int getHeight() { return 0; }
  public int getVisibility() { return 0; }
  public void setVisibility(int v) {}
  public boolean performHapticFeedback(int feedbackConstant) { return false; }
  public boolean performHapticFeedback(int feedbackConstant, int flags) { return false; }
  public boolean post(Runnable action) { return true; }
  public boolean postDelayed(Runnable action, long delayMillis) { return true; }
  public boolean removeCallbacks(Runnable action) { return true; }
  public void invalidate() {}
  public void requestLayout() {}
  public boolean isAttachedToWindow() { return false; }
  public View getRootView() { return null; }
  public android.os.IBinder getWindowToken() { return null; }
  public void setKeepScreenOn(boolean keepScreenOn) {}
  public boolean getKeepScreenOn() { return false; }
  public void setImportantForAccessibility(int mode) {}
  public void announceForAccessibility(CharSequence text) {}
  public void setContentDescription(CharSequence d) {}
  public void setBackgroundColor(int color) {}
  public void setBackground(android.graphics.drawable.Drawable background) {}
  public android.graphics.drawable.Drawable getBackground() { return null; }
  public float getElevation() { return 0; }
  public void setElevation(float e) {}
  public int getImportantForAccessibility() { return 0; }
  public float getTranslationY() { return 0; }
  public float getTranslationX() { return 0; }
  public void setTranslationX(float v) {}
  public CharSequence getContentDescription() { return null; }
  public final android.view.ViewParent getParent() { return null; }
  public boolean isInEditMode() { return false; }
  public void setAlpha(float alpha) {}
  public float getAlpha() { return 1f; }
  public ViewPropertyAnimator animate() { return null; }
  public void setOnClickListener(OnClickListener l) {}
  public void setPadding(int l, int t, int r, int b) {}
  public void setLayoutParams(ViewGroup.LayoutParams params) {}
  public ViewGroup.LayoutParams getLayoutParams() { return null; }
  public WindowInsets getRootWindowInsets() { return null; }
  public void setSystemUiVisibility(int v) {}
  public void setFocusable(boolean focusable) {}
  public boolean requestFocus() { return false; }
  public void clearFocus() {}
  public void setTranslationY(float v) {}
  public void setScaleX(float v) {}
  public void setScaleY(float v) {}
  public interface OnClickListener { void onClick(View v); }
  public interface OnAttachStateChangeListener { void onViewAttachedToWindow(View v); void onViewDetachedFromWindow(View v); }
  public void addOnAttachStateChangeListener(OnAttachStateChangeListener l) {}
  public void removeOnAttachStateChangeListener(OnAttachStateChangeListener l) {}
  public final int getMeasuredWidth() { return 0; }
  public final int getMeasuredHeight() { return 0; }
}
//// FILE: android/view/ViewParent.java
package android.view;
public interface ViewParent { ViewParent getParent(); }
//// FILE: android/view/ViewPropertyAnimator.java
package android.view;
public class ViewPropertyAnimator {
  public ViewPropertyAnimator alpha(float v) { return this; }
  public ViewPropertyAnimator setDuration(long d) { return this; }
  public ViewPropertyAnimator withEndAction(Runnable r) { return this; }
  public ViewPropertyAnimator translationY(float v) { return this; }
  public ViewPropertyAnimator scaleX(float v) { return this; }
  public ViewPropertyAnimator scaleY(float v) { return this; }
  public ViewPropertyAnimator setInterpolator(Object i) { return this; }
  public void start() {}
  public void cancel() {}
}
//// FILE: android/os/IBinder.java
package android.os;
public interface IBinder { }
//// FILE: android/view/WindowInsets.java
package android.view;
public final class WindowInsets { }
//// FILE: android/view/ViewGroup.java
package android.view;
public abstract class ViewGroup extends View {
  public ViewGroup(android.content.Context context) { super(context); }
  public void addView(View child) {}
  public void addView(View child, LayoutParams params) {}
  public void removeView(View child) {}
  public void removeAllViews() {}
  public int getChildCount() { return 0; }
  public View getChildAt(int i) { return null; }
  public static class LayoutParams {
    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;
    public int width; public int height;
    public LayoutParams(int width, int height) {}
  }
}
//// FILE: android/widget/FrameLayout.java
package android.widget;
public class FrameLayout extends android.view.ViewGroup {
  public FrameLayout(android.content.Context context) { super(context); }
  public static class LayoutParams extends android.view.ViewGroup.LayoutParams { public int gravity; public LayoutParams(int w, int h) { super(w, h); } public LayoutParams(int w, int h, int gravity) { super(w, h); } }
}
//// FILE: android/widget/LinearLayout.java
package android.widget;
public class LinearLayout extends android.view.ViewGroup {
  public static final int VERTICAL = 1; public static final int HORIZONTAL = 0;
  public LinearLayout(android.content.Context context) { super(context); }
  public void setOrientation(int o) {}
  public void setGravity(int g) {}
}
//// FILE: android/view/Window.java
package android.view;
public abstract class Window {
  public static final int FEATURE_NO_TITLE = 1;
  public abstract View getDecorView();
  public WindowManager.LayoutParams getAttributes() { return null; }
  public void setAttributes(WindowManager.LayoutParams a) {}
  public void addFlags(int flags) {}
  public void clearFlags(int flags) {}
  public void setFlags(int flags, int mask) {}
  public void setDimAmount(float amount) {}
  public void setWindowAnimations(int resId) {}
  public void setBackgroundDrawable(android.graphics.drawable.Drawable d) {}
  public void setBackgroundDrawableResource(int resId) {}
  public void setLayout(int width, int height) {}
  public void setGravity(int gravity) {}
  public void setStatusBarColor(int color) {}
  public void setNavigationBarColor(int color) {}
  public void setSoftInputMode(int mode) {}
  public void setDecorFitsSystemWindows(boolean decorFitsSystemWindows) {}
  public void setNavigationBarContrastEnforced(boolean enforce) {}
  public void setStatusBarContrastEnforced(boolean enforce) {}
  public void setBackgroundBlurRadius(int blurRadius) {}
  public final android.content.Context getContext() { return null; }
  public WindowInsetsController getInsetsController() { return null; }
}
//// FILE: android/view/WindowInsetsController.java
package android.view;
public interface WindowInsetsController { }
//// FILE: android/view/WindowManager.java
package android.view;
public interface WindowManager extends ViewManager {
  Display getDefaultDisplay();
  public static class LayoutParams extends ViewGroup.LayoutParams {
    public static final int TYPE_ACCESSIBILITY_OVERLAY = 2032;
    public static final int TYPE_APPLICATION_OVERLAY = 2038;
    public static final int FLAG_NOT_FOCUSABLE = 8;
    public static final int FLAG_NOT_TOUCH_MODAL = 32;
    public static final int FLAG_LAYOUT_IN_SCREEN = 256;
    public static final int FLAG_LAYOUT_NO_LIMITS = 512;
    public static final int FLAG_DIM_BEHIND = 2;
    public static final int FLAG_KEEP_SCREEN_ON = 128;
    public static final int FLAG_SHOW_WHEN_LOCKED = 0x80000;
    public static final int FLAG_TURN_SCREEN_ON = 0x200000;
    public static final int FLAG_BLUR_BEHIND = 4;
    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;
    public static final int SOFT_INPUT_ADJUST_RESIZE = 16;
    public static final int SOFT_INPUT_ADJUST_NOTHING = 48;
    public static final int LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES = 1;
    public static final int LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS = 3;
    public int x; public int y; public int type; public int flags; public int gravity; public float dimAmount; public int format; public int windowAnimations; public float alpha; public int softInputMode; public int layoutInDisplayCutoutMode; public float screenBrightness; public CharSequence title;
    public LayoutParams() { super(-1, -1); }
    public LayoutParams(int w, int h, int type, int flags, int format) { super(w, h); }
    public LayoutParams(int type, int flags, int format) { super(-1, -1); }
    public void setTitle(CharSequence t) {}
    public void setBlurBehindRadius(int r) {}
  }
}
//// FILE: android/view/ViewManager.java
package android.view;
public interface ViewManager { void addView(View view, ViewGroup.LayoutParams params); void updateViewLayout(View view, ViewGroup.LayoutParams params); void removeView(View view); }
//// FILE: android/view/Display.java
package android.view;
public final class Display { public float getRefreshRate() { return 60f; } public int getRotation() { return 0; } }
//// FILE: android/view/HapticFeedbackConstants.java
package android.view;
public class HapticFeedbackConstants {
  public static final int LONG_PRESS = 0; public static final int VIRTUAL_KEY = 1; public static final int KEYBOARD_TAP = 3; public static final int CLOCK_TICK = 4; public static final int CONTEXT_CLICK = 6;
  public static final int CONFIRM = 16; public static final int REJECT = 17; public static final int GESTURE_START = 12; public static final int GESTURE_END = 13; public static final int SEGMENT_TICK = 26; public static final int SEGMENT_FREQUENT_TICK = 27; public static final int TOGGLE_ON = 21; public static final int TOGGLE_OFF = 22; public static final int DRAG_START = 25; public static final int TEXT_HANDLE_MOVE = 9;
}
//// FILE: android/view/KeyEvent.java
package android.view;
public class KeyEvent { public static final int KEYCODE_BACK = 4; public final int getKeyCode() { return 0; } public final int getAction() { return 0; } }
//// FILE: android/view/MotionEvent.java
package android.view;
public final class MotionEvent { }
//// FILE: android/view/accessibility/AccessibilityEvent.java
package android.view.accessibility;
public final class AccessibilityEvent {
  public static final int TYPE_WINDOW_STATE_CHANGED = 32;
  public static final int TYPE_WINDOW_CONTENT_CHANGED = 2048;
  public static final int TYPE_WINDOWS_CHANGED = 4194304;
  public static final int TYPE_VIEW_CLICKED = 1;
  public int getEventType() { return 0; }
  public CharSequence getPackageName() { return null; }
  public CharSequence getClassName() { return null; }
  public long getEventTime() { return 0; }
  public java.util.List<CharSequence> getText() { return null; }
  public AccessibilityNodeInfo getSource() { return null; }
  public int getWindowId() { return 0; }
  public int getContentChangeTypes() { return 0; }
}
//// FILE: android/view/accessibility/AccessibilityNodeInfo.java
package android.view.accessibility;
public class AccessibilityNodeInfo { public CharSequence getPackageName() { return null; } public CharSequence getClassName() { return null; } public void recycle() {} public int getWindowId() { return 0; } }
//// FILE: android/view/accessibility/AccessibilityWindowInfo.java
package android.view.accessibility;
public final class AccessibilityWindowInfo { public static final int TYPE_APPLICATION = 1; public int getType() { return 0; } public AccessibilityNodeInfo getRoot() { return null; } public boolean isActive() { return false; } public boolean isFocused() { return false; } }
//// FILE: android/view/accessibility/AccessibilityManager.java
package android.view.accessibility;
public final class AccessibilityManager { public boolean isEnabled() { return false; } public java.util.List<android.accessibilityservice.AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int feedbackTypeFlags) { return null; } }
//// FILE: android/view/inputmethod/InputMethodManager.java
package android.view.inputmethod;
public final class InputMethodManager {
  public static final int HIDE_NOT_ALWAYS = 2;
  public static final int SHOW_IMPLICIT = 1;
  public boolean hideSoftInputFromWindow(android.os.IBinder windowToken, int flags) { return false; }
  public boolean showSoftInput(android.view.View view, int flags) { return false; }
  public boolean isActive() { return false; }
  public java.util.List<InputMethodInfo> getEnabledInputMethodList() { return null; }
}
//// FILE: android/view/inputmethod/InputMethodInfo.java
package android.view.inputmethod;
public final class InputMethodInfo { public String getPackageName() { return null; } public String getId() { return null; } }
//// FILE: android/accessibilityservice/AccessibilityService.java
package android.accessibilityservice;
public abstract class AccessibilityService extends android.app.Service {
  public static final int GLOBAL_ACTION_BACK = 1;
  public static final int GLOBAL_ACTION_HOME = 2;
  public static final int GLOBAL_ACTION_RECENTS = 3;
  public static final int GLOBAL_ACTION_NOTIFICATIONS = 4;
  public static final int GLOBAL_ACTION_LOCK_SCREEN = 8;
  public abstract void onAccessibilityEvent(android.view.accessibility.AccessibilityEvent event);
  public abstract void onInterrupt();
  protected void onServiceConnected() {}
  public final boolean performGlobalAction(int action) { return false; }
  public android.view.accessibility.AccessibilityNodeInfo getRootInActiveWindow() { return null; }
  public java.util.List<android.view.accessibility.AccessibilityWindowInfo> getWindows() { return null; }
  public final AccessibilityServiceInfo getServiceInfo() { return null; }
  public final void setServiceInfo(AccessibilityServiceInfo info) {}
  public final void disableSelf() {}
  public Object getSystemService(String name) { return null; }
  public boolean onUnbind(android.content.Intent intent) { return false; }
}
//// FILE: android/accessibilityservice/AccessibilityServiceInfo.java
package android.accessibilityservice;
public class AccessibilityServiceInfo {
  public static final int FEEDBACK_GENERIC = 16;
  public static final int FEEDBACK_ALL_MASK = -1;
  public static final int FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 2;
  public static final int FLAG_RETRIEVE_INTERACTIVE_WINDOWS = 64;
  public static final int FLAG_REPORT_VIEW_IDS = 16;
  public static final int DEFAULT = 1;
  public int eventTypes; public int feedbackType; public int flags; public long notificationTimeout; public String[] packageNames;
  public String getId() { return null; }
  public android.content.pm.ResolveInfo getResolveInfo() { return null; }
}
//// FILE: android/app/Service.java
package android.app;
public abstract class Service extends android.content.ContextWrapper {
  public static final int START_STICKY = 1;
  public static final int START_NOT_STICKY = 2;
  public static final int STOP_FOREGROUND_REMOVE = 1;
  public Service() { super(null); }
  public void onCreate() {}
  public void onDestroy() {}
  public int onStartCommand(android.content.Intent intent, int flags, int startId) { return 0; }
  public android.os.IBinder onBind(android.content.Intent intent) { return null; }
  public final void startForeground(int id, Notification notification) {}
  public final void stopForeground(int flags) {}
  public final void stopSelf() {}
  public final Application getApplication() { return null; }
}
//// FILE: android/app/Activity.java
package android.app;
public class Activity extends android.content.ContextThemeWrapper {
  public static final int RESULT_OK = -1;
  public static final int RESULT_CANCELED = 0;
  public Activity() {}
  protected void onCreate(android.os.Bundle savedInstanceState) {}
  protected void onStart() {}
  protected void onResume() {}
  protected void onPause() {}
  protected void onStop() {}
  protected void onDestroy() {}
  protected void onNewIntent(android.content.Intent intent) {}
  protected void onSaveInstanceState(android.os.Bundle outState) {}
  public void onWindowFocusChanged(boolean hasFocus) {}
  public void onUserLeaveHint() {}
  public void onAttachedToWindow() {}
  public void onBackPressed() {}
  public void onConfigurationChanged(android.content.res.Configuration newConfig) {}
  public void onTopResumedActivityChanged(boolean isTopResumedActivity) {}
  public android.content.Intent getIntent() { return null; }
  public void setIntent(android.content.Intent newIntent) {}
  public android.view.Window getWindow() { return null; }
  public android.view.WindowManager getWindowManager() { return null; }
  public void setContentView(android.view.View view) {}
  public void setContentView(int layoutResID) {}
  public void finish() {}
  public void finishAndRemoveTask() {}
  public void finishAffinity() {}
  public boolean isFinishing() { return false; }
  public boolean isDestroyed() { return false; }
  public boolean isChangingConfigurations() { return false; }
  public boolean moveTaskToBack(boolean nonRoot) { return false; }
  public void overridePendingTransition(int enterAnim, int exitAnim) {}
  public void overrideActivityTransition(int overrideType, int enterAnim, int exitAnim) {}
  public void setShowWhenLocked(boolean showWhenLocked) {}
  public void setTurnScreenOn(boolean turnScreenOn) {}
  public void recreate() {}
  public final android.window.SplashScreen getSplashScreen() { return null; }
  public void startActivityForResult(android.content.Intent intent, int requestCode) {}
  public final void runOnUiThread(Runnable action) {}
  public Application getApplication() { return null; }
  public void requestPermissions(String[] permissions, int requestCode) {}
  public boolean shouldShowRequestPermissionRationale(String permission) { return false; }
  public android.view.View getCurrentFocus() { return null; }
  public void setTaskDescription(ActivityManager.TaskDescription d) {}
  public static final int OVERRIDE_TRANSITION_OPEN = 0;
  public static final int OVERRIDE_TRANSITION_CLOSE = 1;
}
//// FILE: android/app/ActivityManager.java
package android.app;
public class ActivityManager { public static class TaskDescription { } public boolean isLowRamDevice() { return false; } public int getMemoryClass() { return 256; } }
//// FILE: android/window/SplashScreen.java
package android.window;
public interface SplashScreen {
  void setOnExitAnimationListener(OnExitAnimationListener listener);
  void clearOnExitAnimationListener();
  interface OnExitAnimationListener { void onSplashScreenExit(SplashScreenView view); }
}
//// FILE: android/window/SplashScreenView.java
package android.window;
public final class SplashScreenView extends android.widget.FrameLayout {
  public SplashScreenView(android.content.Context c) { super(c); }
  public java.time.Instant getIconAnimationStart() { return null; }
  public java.time.Duration getIconAnimationDuration() { return null; }
  public android.view.View getIconView() { return null; }
  public void remove() {}
}
//// FILE: android/app/Application.java
package android.app;
public class Application extends android.content.ContextWrapper {
  public Application() { super(null); }
  public void onCreate() {}
  public void onTerminate() {}
  public void onLowMemory() {}
  public void onTrimMemory(int level) {}
}
//// FILE: android/app/AlarmManager.java
package android.app;
public class AlarmManager {
  public static final int RTC_WAKEUP = 0;
  public static final int RTC = 1;
  public static final int ELAPSED_REALTIME_WAKEUP = 2;
  public static final int ELAPSED_REALTIME = 3;
  public static final long INTERVAL_DAY = 86400000L;
  public static final long INTERVAL_FIFTEEN_MINUTES = 900000L;
  public static final String ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED = "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED";
  public void set(int type, long triggerAtMillis, PendingIntent operation) {}
  public void setExact(int type, long triggerAtMillis, PendingIntent operation) {}
  public void setExactAndAllowWhileIdle(int type, long triggerAtMillis, PendingIntent operation) {}
  public void setAndAllowWhileIdle(int type, long triggerAtMillis, PendingIntent operation) {}
  public void setWindow(int type, long windowStartMillis, long windowLengthMillis, PendingIntent operation) {}
  public void setRepeating(int type, long triggerAtMillis, long intervalMillis, PendingIntent operation) {}
  public void setInexactRepeating(int type, long triggerAtMillis, long intervalMillis, PendingIntent operation) {}
  public void setAlarmClock(AlarmClockInfo info, PendingIntent operation) {}
  public void cancel(PendingIntent operation) {}
  public boolean canScheduleExactAlarms() { return true; }
  public AlarmClockInfo getNextAlarmClock() { return null; }
  public static final class AlarmClockInfo { public AlarmClockInfo(long triggerTime, PendingIntent showIntent) {} public long getTriggerTime() { return 0; } }
}
//// FILE: android/app/AppOpsManager.java
package android.app;
public class AppOpsManager {
  public static final int MODE_ALLOWED = 0;
  public static final int MODE_IGNORED = 1;
  public static final int MODE_ERRORED = 2;
  public static final int MODE_DEFAULT = 3;
  public static final String OPSTR_GET_USAGE_STATS = "android:get_usage_stats";
  public int checkOpNoThrow(String op, int uid, String packageName) { return 0; }
  public int unsafeCheckOpNoThrow(String op, int uid, String packageName) { return 0; }
}
//// FILE: android/app/KeyguardManager.java
package android.app;
public class KeyguardManager {
  public boolean isKeyguardLocked() { return false; }
  public boolean isDeviceLocked() { return false; }
  public boolean isKeyguardSecure() { return false; }
  public boolean isDeviceSecure() { return false; }
  public void requestDismissKeyguard(Activity activity, KeyguardDismissCallback callback) {}
  public static abstract class KeyguardDismissCallback { public void onDismissSucceeded() {} public void onDismissCancelled() {} public void onDismissError() {} }
}
//// FILE: android/app/Notification.java
package android.app;
public class Notification implements android.os.Parcelable {
  public static final String CATEGORY_REMINDER = "reminder";
  public static final String CATEGORY_ALARM = "alarm";
  public static final int FLAG_AUTO_CANCEL = 16;
  public static final int FLAG_ONGOING_EVENT = 2;
  public static final int VISIBILITY_PUBLIC = 1;
  public int flags;
  public PendingIntent contentIntent;
  public PendingIntent fullScreenIntent;
  public android.os.Bundle extras;
  public long when;
}
//// FILE: android/app/NotificationChannel.java
package android.app;
public final class NotificationChannel implements android.os.Parcelable {
  public NotificationChannel(String id, CharSequence name, int importance) {}
  public String getId() { return null; }
  public CharSequence getName() { return null; }
  public void setName(CharSequence name) {}
  public int getImportance() { return 0; }
  public void setImportance(int importance) {}
  public String getDescription() { return null; }
  public void setDescription(String description) {}
  public void enableVibration(boolean vibration) {}
  public void setVibrationPattern(long[] vibrationPattern) {}
  public void enableLights(boolean lights) {}
  public void setLightColor(int argb) {}
  public void setShowBadge(boolean showBadge) {}
  public void setSound(android.net.Uri sound, android.media.AudioAttributes audioAttributes) {}
  public android.net.Uri getSound() { return null; }
  public void setLockscreenVisibility(int lockscreenVisibility) {}
  public int getLockscreenVisibility() { return 0; }
  public void setBypassDnd(boolean bypassDnd) {}
  public boolean canBypassDnd() { return false; }
  public void setGroup(String groupId) {}
}
//// FILE: android/app/NotificationManager.java
package android.app;
public class NotificationManager {
  public static final int IMPORTANCE_NONE = 0;
  public static final int IMPORTANCE_MIN = 1;
  public static final int IMPORTANCE_LOW = 2;
  public static final int IMPORTANCE_DEFAULT = 3;
  public static final int IMPORTANCE_HIGH = 4;
  public static final int IMPORTANCE_MAX = 5;
  public static final int IMPORTANCE_UNSPECIFIED = -1000;
  public static final int INTERRUPTION_FILTER_ALL = 1;
  public void createNotificationChannel(NotificationChannel channel) {}
  public void deleteNotificationChannel(String channelId) {}
  public NotificationChannel getNotificationChannel(String channelId) { return null; }
  public java.util.List<NotificationChannel> getNotificationChannels() { return null; }
  public void notify(int id, Notification notification) {}
  public void notify(String tag, int id, Notification notification) {}
  public void cancel(int id) {}
  public void cancel(String tag, int id) {}
  public void cancelAll() {}
  public boolean areNotificationsEnabled() { return true; }
  public boolean canUseFullScreenIntent() { return true; }
  public int getImportance() { return 0; }
  public final int getCurrentInterruptionFilter() { return 1; }
  public android.service.notification.StatusBarNotification[] getActiveNotifications() { return null; }
}
//// FILE: android/service/notification/StatusBarNotification.java
package android.service.notification;
public class StatusBarNotification { public int getId() { return 0; } public String getTag() { return null; } }
//// FILE: android/app/PendingIntent.java
package android.app;
public final class PendingIntent implements android.os.Parcelable {
  public static final int FLAG_ONE_SHOT = 0x40000000;
  public static final int FLAG_NO_CREATE = 0x20000000;
  public static final int FLAG_CANCEL_CURRENT = 0x10000000;
  public static final int FLAG_UPDATE_CURRENT = 0x08000000;
  public static final int FLAG_IMMUTABLE = 0x04000000;
  public static final int FLAG_MUTABLE = 0x02000000;
  public static PendingIntent getActivity(android.content.Context context, int requestCode, android.content.Intent intent, int flags) { return null; }
  public static PendingIntent getActivity(android.content.Context context, int requestCode, android.content.Intent intent, int flags, android.os.Bundle options) { return null; }
  public static PendingIntent getBroadcast(android.content.Context context, int requestCode, android.content.Intent intent, int flags) { return null; }
  public static PendingIntent getService(android.content.Context context, int requestCode, android.content.Intent intent, int flags) { return null; }
  public void cancel() {}
  public void send() throws CanceledException {}
  public String getCreatorPackage() { return null; }
  public static class CanceledException extends Exception { }
}
//// FILE: android/app/UiModeManager.java
package android.app;
public class UiModeManager {
  public static final int MODE_NIGHT_AUTO = 0;
  public static final int MODE_NIGHT_NO = 1;
  public static final int MODE_NIGHT_YES = 2;
  public static final int MODE_NIGHT_CUSTOM = 3;
  public int getNightMode() { return 0; }
  public void setApplicationNightMode(int mode) {}
  public void setNightMode(int mode) {}
  public float getContrast() { return 0f; }
}
//// FILE: android/app/Dialog.java
package android.app;
public class Dialog { public android.view.Window getWindow() { return null; } }
//// FILE: android/app/usage/UsageEvents.java
package android.app.usage;
public final class UsageEvents {
  public boolean hasNextEvent() { return false; }
  public boolean getNextEvent(Event eventOut) { return false; }
  public static final class Event {
    public static final int NONE = 0;
    public static final int MOVE_TO_FOREGROUND = 1;
    public static final int MOVE_TO_BACKGROUND = 2;
    public static final int ACTIVITY_RESUMED = 1;
    public static final int ACTIVITY_PAUSED = 2;
    public static final int ACTIVITY_STOPPED = 23;
    public static final int SCREEN_INTERACTIVE = 15;
    public static final int SCREEN_NON_INTERACTIVE = 16;
    public static final int KEYGUARD_SHOWN = 17;
    public static final int KEYGUARD_HIDDEN = 18;
    public static final int DEVICE_SHUTDOWN = 26;
    public static final int FOREGROUND_SERVICE_START = 19;
    public Event() {}
    public String getPackageName() { return null; }
    public String getClassName() { return null; }
    public long getTimeStamp() { return 0; }
    public int getEventType() { return 0; }
  }
}
//// FILE: android/app/usage/UsageStats.java
package android.app.usage;
public final class UsageStats {
  public String getPackageName() { return null; }
  public long getTotalTimeInForeground() { return 0; }
  public long getTotalTimeVisible() { return 0; }
  public long getLastTimeUsed() { return 0; }
  public long getFirstTimeStamp() { return 0; }
  public long getLastTimeStamp() { return 0; }
}
//// FILE: android/app/usage/UsageStatsManager.java
package android.app.usage;
public final class UsageStatsManager {
  public static final int INTERVAL_DAILY = 0;
  public static final int INTERVAL_WEEKLY = 1;
  public static final int INTERVAL_BEST = 4;
  public UsageEvents queryEvents(long beginTime, long endTime) { return null; }
  public java.util.List<UsageStats> queryUsageStats(int intervalType, long beginTime, long endTime) { return null; }
  public java.util.Map<String, UsageStats> queryAndAggregateUsageStats(long beginTime, long endTime) { return null; }
  public boolean isAppInactive(String packageName) { return false; }
}
//// FILE: android/appwidget/AppWidgetManager.java
package android.appwidget;
public class AppWidgetManager {
  public static final String ACTION_APPWIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";
  public static final String EXTRA_APPWIDGET_IDS = "appWidgetIds";
  public static final String EXTRA_APPWIDGET_ID = "appWidgetId";
  public static final int INVALID_APPWIDGET_ID = 0;
  public static final String OPTION_APPWIDGET_MIN_WIDTH = "appWidgetMinWidth";
  public static final String OPTION_APPWIDGET_MIN_HEIGHT = "appWidgetMinHeight";
  public static final String OPTION_APPWIDGET_MAX_WIDTH = "appWidgetMaxWidth";
  public static final String OPTION_APPWIDGET_MAX_HEIGHT = "appWidgetMaxHeight";
  public static final String OPTION_APPWIDGET_SIZES = "appWidgetSizes";
  public static AppWidgetManager getInstance(android.content.Context context) { return null; }
  public int[] getAppWidgetIds(android.content.ComponentName provider) { return null; }
  public void updateAppWidget(int appWidgetId, android.widget.RemoteViews views) {}
  public void updateAppWidget(int[] appWidgetIds, android.widget.RemoteViews views) {}
  public void updateAppWidget(android.content.ComponentName provider, android.widget.RemoteViews views) {}
  public void partiallyUpdateAppWidget(int appWidgetId, android.widget.RemoteViews views) {}
  public android.os.Bundle getAppWidgetOptions(int appWidgetId) { return null; }
  public boolean isRequestPinAppWidgetSupported() { return false; }
  public boolean requestPinAppWidget(android.content.ComponentName provider, android.os.Bundle extras, android.app.PendingIntent successCallback) { return false; }
  public void notifyAppWidgetViewDataChanged(int appWidgetId, int viewId) {}
}
//// FILE: android/appwidget/AppWidgetProvider.java
package android.appwidget;
public class AppWidgetProvider extends android.content.BroadcastReceiver {
  public AppWidgetProvider() {}
  public void onReceive(android.content.Context context, android.content.Intent intent) {}
  public void onUpdate(android.content.Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {}
  public void onAppWidgetOptionsChanged(android.content.Context context, AppWidgetManager appWidgetManager, int appWidgetId, android.os.Bundle newOptions) {}
  public void onDeleted(android.content.Context context, int[] appWidgetIds) {}
  public void onEnabled(android.content.Context context) {}
  public void onDisabled(android.content.Context context) {}
}
//// FILE: android/widget/RemoteViews.java
package android.widget;
public class RemoteViews implements android.os.Parcelable {
  public RemoteViews(String packageName, int layoutId) {}
  public RemoteViews(java.util.Map<android.util.SizeF, RemoteViews> remoteViews) {}
  public RemoteViews(RemoteViews landscape, RemoteViews portrait) {}
  public void setTextViewText(int viewId, CharSequence text) {}
  public void setTextViewTextSize(int viewId, int units, float size) {}
  public void setTextColor(int viewId, int color) {}
  public void setImageViewResource(int viewId, int srcId) {}
  public void setImageViewBitmap(int viewId, android.graphics.Bitmap bitmap) {}
  public void setViewVisibility(int viewId, int visibility) {}
  public void setOnClickPendingIntent(int viewId, android.app.PendingIntent pendingIntent) {}
  public void setInt(int viewId, String methodName, int value) {}
  public void setFloat(int viewId, String methodName, float value) {}
  public void setBoolean(int viewId, String methodName, boolean value) {}
  public void setProgressBar(int viewId, int max, int progress, boolean indeterminate) {}
  public void setContentDescription(int viewId, CharSequence contentDescription) {}
  public void setViewPadding(int viewId, int left, int top, int right, int bottom) {}
  public void setViewLayoutWidth(int viewId, float width, int units) {}
  public void setViewLayoutHeight(int viewId, float height, int units) {}
  public void setColorStateList(int viewId, String methodName, android.content.res.ColorStateList colorStateList) {}
  public void setColorInt(int viewId, String methodName, int notNight, int night) {}
  public void setCharSequence(int viewId, String methodName, CharSequence value) {}
  public void removeAllViews(int viewId) {}
  public void addView(int viewId, RemoteViews nestedView) {}
  public void setLong(int viewId, String methodName, long value) {}
  public void setChronometer(int viewId, long base, String format, boolean started) {}
  public void setChronometerCountDown(int viewId, boolean isCountDown) {}
  public void setBitmap(int viewId, String methodName, android.graphics.Bitmap value) {}
  public void setImageViewIcon(int viewId, android.graphics.drawable.Icon icon) {}
}
//// FILE: android/graphics/drawable/Icon.java
package android.graphics.drawable;
public final class Icon { public static Icon createWithResource(android.content.Context c, int resId) { return null; } public static Icon createWithBitmap(android.graphics.Bitmap b) { return null; } }
//// FILE: android/content/res/ColorStateList.java
package android.content.res;
public class ColorStateList { public static ColorStateList valueOf(int color) { return null; } }
//// FILE: android/widget/TextView.java
package android.widget;
public class TextView extends android.view.View {
  public TextView(android.content.Context context) { super(context); }
  public void setText(CharSequence text) {}
  public CharSequence getText() { return null; }
  public void setTextColor(int color) {}
  public void setTextSize(float size) {}
  public float getTextSize() { return 0; }
  public void setTextSize(int unit, float size) {}
  public android.graphics.Typeface getTypeface() { return null; }
  public void setTypeface(android.graphics.Typeface tf) {}
  public void setTypeface(android.graphics.Typeface tf, int style) {}
  public void setGravity(int gravity) {}
  public void setLineSpacing(float add, float mult) {}
  public void setMaxLines(int maxLines) {}
  public void setIncludeFontPadding(boolean includepad) {}
  public void setLetterSpacing(float letterSpacing) {}
  public android.text.TextPaint getPaint() { return null; }
}
//// FILE: android/text/TextPaint.java
package android.text;
public class TextPaint extends android.graphics.Paint { }
//// FILE: android/widget/Toast.java
package android.widget;
public class Toast {
  public static final int LENGTH_SHORT = 0;
  public static final int LENGTH_LONG = 1;
  public static Toast makeText(android.content.Context context, CharSequence text, int duration) { return null; }
  public static Toast makeText(android.content.Context context, int resId, int duration) { return null; }
  public void show() {}
  public void cancel() {}
}
//// FILE: android/media/AudioAttributes.java
package android.media;
public final class AudioAttributes implements android.os.Parcelable {
  public static final int USAGE_UNKNOWN = 0;
  public static final int USAGE_MEDIA = 1;
  public static final int USAGE_ALARM = 4;
  public static final int USAGE_NOTIFICATION = 5;
  public static final int USAGE_NOTIFICATION_EVENT = 10;
  public static final int USAGE_ASSISTANCE_SONIFICATION = 13;
  public static final int USAGE_GAME = 14;
  public static final int CONTENT_TYPE_UNKNOWN = 0;
  public static final int CONTENT_TYPE_SPEECH = 1;
  public static final int CONTENT_TYPE_MUSIC = 2;
  public static final int CONTENT_TYPE_SONIFICATION = 4;
  public static class Builder {
    public Builder() {}
    public Builder setUsage(int usage) { return this; }
    public Builder setContentType(int contentType) { return this; }
    public Builder setFlags(int flags) { return this; }
    public AudioAttributes build() { return null; }
  }
}
//// FILE: android/media/SoundPool.java
package android.media;
public class SoundPool {
  public int load(android.content.Context context, int resId, int priority) { return 0; }
  public final int play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) { return 0; }
  public final void stop(int streamID) {}
  public final boolean unload(int soundID) { return false; }
  public final void release() {}
  public final void autoPause() {}
  public final void autoResume() {}
  public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {}
  public interface OnLoadCompleteListener { void onLoadComplete(SoundPool soundPool, int sampleId, int status); }
  public static class Builder {
    public Builder() {}
    public Builder setMaxStreams(int maxStreams) { return this; }
    public Builder setAudioAttributes(AudioAttributes attributes) { return this; }
    public SoundPool build() { return null; }
  }
}
//// FILE: android/media/AudioManager.java
package android.media;
public class AudioManager { public static final int RINGER_MODE_SILENT = 0; public static final int RINGER_MODE_VIBRATE = 1; public static final int RINGER_MODE_NORMAL = 2; public int getRingerMode() { return 2; } public int getMode() { return 0; } public static final int MODE_IN_CALL = 2; public static final int MODE_IN_COMMUNICATION = 3; }
//// FILE: android/media/RingtoneManager.java
package android.media;
public class RingtoneManager { public static final int TYPE_NOTIFICATION = 2; public static final int TYPE_ALARM = 4; public static android.net.Uri getDefaultUri(int type) { return null; } }
//// FILE: android/graphics/Bitmap.java
package android.graphics;
public final class Bitmap implements android.os.Parcelable {
  public enum Config { ALPHA_8, RGB_565, ARGB_4444, ARGB_8888, RGBA_F16, HARDWARE, RGBA_1010102 }
  public enum CompressFormat { JPEG, PNG, WEBP, WEBP_LOSSY, WEBP_LOSSLESS }
  public static Bitmap createBitmap(int width, int height, Config config) { return null; }
  public static Bitmap createBitmap(Bitmap src) { return null; }
  public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height) { return null; }
  public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height, Matrix m, boolean filter) { return null; }
  public static Bitmap createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter) { return null; }
  public final int getWidth() { return 0; }
  public final int getHeight() { return 0; }
  public final Config getConfig() { return null; }
  public final boolean isRecycled() { return false; }
  public void recycle() {}
  public void prepareToDraw() {}
  public void eraseColor(int c) {}
  public int getPixel(int x, int y) { return 0; }
  public void setPixel(int x, int y, int color) {}
  public void getPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {}
  public void setPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {}
  public boolean compress(CompressFormat format, int quality, java.io.OutputStream stream) { return false; }
  public final int getByteCount() { return 0; }
  public final int getAllocationByteCount() { return 0; }
  public final boolean hasAlpha() { return false; }
  public void setHasAlpha(boolean hasAlpha) {}
  public Bitmap copy(Config config, boolean isMutable) { return null; }
  public final boolean isMutable() { return false; }
  public void setDensity(int density) {}
}
//// FILE: android/graphics/BitmapFactory.java
package android.graphics;
public class BitmapFactory {
  public static class Options {
    public boolean inJustDecodeBounds; public int inSampleSize; public int outWidth; public int outHeight; public Bitmap.Config inPreferredConfig; public boolean inMutable; public int inDensity; public int inTargetDensity; public boolean inScaled; public String outMimeType;
    public Options() {}
  }
  public static Bitmap decodeByteArray(byte[] data, int offset, int length) { return null; }
  public static Bitmap decodeByteArray(byte[] data, int offset, int length, Options opts) { return null; }
  public static Bitmap decodeFile(String pathName) { return null; }
  public static Bitmap decodeFile(String pathName, Options opts) { return null; }
  public static Bitmap decodeResource(android.content.res.Resources res, int id) { return null; }
  public static Bitmap decodeResource(android.content.res.Resources res, int id, Options opts) { return null; }
  public static Bitmap decodeStream(java.io.InputStream is) { return null; }
  public static Bitmap decodeStream(java.io.InputStream is, Rect outPadding, Options opts) { return null; }
}
//// FILE: android/graphics/Canvas.java
package android.graphics;
public class Canvas {
  public Canvas() {}
  public Canvas(Bitmap bitmap) {}
  public int getWidth() { return 0; }
  public int getHeight() { return 0; }
  public int save() { return 0; }
  public void restore() {}
  public void restoreToCount(int saveCount) {}
  public int saveLayer(RectF bounds, Paint paint) { return 0; }
  public int saveLayer(float l, float t, float r, float b, Paint paint) { return 0; }
  public void translate(float dx, float dy) {}
  public void scale(float sx, float sy) {}
  public void scale(float sx, float sy, float px, float py) {}
  public void rotate(float degrees) {}
  public void rotate(float degrees, float px, float py) {}
  public void concat(Matrix matrix) {}
  public boolean clipRect(RectF rect) { return true; }
  public boolean clipRect(Rect rect) { return true; }
  public boolean clipRect(float l, float t, float r, float b) { return true; }
  public boolean clipPath(Path path) { return true; }
  public void drawColor(int color) {}
  public void drawColor(int color, PorterDuff.Mode mode) {}
  public void drawRGB(int r, int g, int b) {}
  public void drawPaint(Paint paint) {}
  public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) {}
  public void drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint) {}
  public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {}
  public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {}
  public void drawText(String text, float x, float y, Paint paint) {}
  public void drawText(CharSequence text, int start, int end, float x, float y, Paint paint) {}
  public void drawText(String text, int start, int end, float x, float y, Paint paint) {}
  public void drawTextOnPath(String text, Path path, float hOffset, float vOffset, Paint paint) {}
  public void drawCircle(float cx, float cy, float radius, Paint paint) {}
  public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) {}
  public void drawLines(float[] pts, Paint paint) {}
  public void drawRect(float left, float top, float right, float bottom, Paint paint) {}
  public void drawRect(RectF rect, Paint paint) {}
  public void drawRect(Rect r, Paint paint) {}
  public void drawRoundRect(RectF rect, float rx, float ry, Paint paint) {}
  public void drawRoundRect(float left, float top, float right, float bottom, float rx, float ry, Paint paint) {}
  public void drawOval(RectF oval, Paint paint) {}
  public void drawOval(float left, float top, float right, float bottom, Paint paint) {}
  public void drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint) {}
  public void drawArc(float left, float top, float right, float bottom, float startAngle, float sweepAngle, boolean useCenter, Paint paint) {}
  public void drawPath(Path path, Paint paint) {}
  public void drawPoint(float x, float y, Paint paint) {}
  public void drawPoints(float[] pts, int offset, int count, Paint paint) {}
  public void drawPoints(float[] pts, Paint paint) {}
  public void drawRenderNode(RenderNode renderNode) {}
  public boolean isHardwareAccelerated() { return false; }
  public void setBitmap(Bitmap bitmap) {}
}
//// FILE: android/graphics/RenderNode.java
package android.graphics;
public final class RenderNode { public RenderNode(String name) {} }
//// FILE: android/graphics/PorterDuff.java
package android.graphics;
public class PorterDuff { public enum Mode { CLEAR, SRC, DST, SRC_OVER, DST_OVER, SRC_IN, DST_IN, SRC_OUT, DST_OUT, SRC_ATOP, DST_ATOP, XOR, DARKEN, LIGHTEN, MULTIPLY, SCREEN, ADD, OVERLAY } }
//// FILE: android/graphics/PorterDuffXfermode.java
package android.graphics;
public class PorterDuffXfermode extends Xfermode { public PorterDuffXfermode(PorterDuff.Mode mode) {} }
//// FILE: android/graphics/Xfermode.java
package android.graphics;
public class Xfermode { }
//// FILE: android/graphics/Matrix.java
package android.graphics;
public class Matrix {
  public Matrix() {}
  public void reset() {}
  public void setScale(float sx, float sy) {}
  public void setTranslate(float dx, float dy) {}
  public boolean postScale(float sx, float sy) { return true; }
  public boolean postTranslate(float dx, float dy) { return true; }
  public boolean preScale(float sx, float sy) { return true; }
  public boolean preTranslate(float dx, float dy) { return true; }
  public boolean postRotate(float degrees, float px, float py) { return true; }
  public void mapPoints(float[] pts) {}
  public boolean invert(Matrix inverse) { return true; }
}
//// FILE: android/graphics/Color.java
package android.graphics;
public class Color {
  public static final int BLACK = 0xFF000000;
  public static final int WHITE = 0xFFFFFFFF;
  public static final int TRANSPARENT = 0;
  public static final int RED = 0xFFFF0000;
  public static final int GREEN = 0xFF00FF00;
  public static final int BLUE = 0xFF0000FF;
  public static final int GRAY = 0xFF888888;
  public static final int DKGRAY = 0xFF444444;
  public static final int LTGRAY = 0xFFCCCCCC;
  public static final int YELLOW = 0xFFFFFF00;
  public static int argb(int alpha, int red, int green, int blue) { return 0; }
  public static int argb(float alpha, float red, float green, float blue) { return 0; }
  public static int rgb(int red, int green, int blue) { return 0; }
  public static int rgb(float red, float green, float blue) { return 0; }
  public static int alpha(int color) { return 0; }
  public static int red(int color) { return 0; }
  public static int green(int color) { return 0; }
  public static int blue(int color) { return 0; }
  public static int parseColor(String colorString) { return 0; }
  public static void colorToHSV(int color, float[] hsv) {}
  public static int HSVToColor(float[] hsv) { return 0; }
  public static int HSVToColor(int alpha, float[] hsv) { return 0; }
  public static float luminance(int color) { return 0; }
}
//// FILE: android/graphics/DashPathEffect.java
package android.graphics;
public class DashPathEffect extends PathEffect { public DashPathEffect(float[] intervals, float phase) {} }
//// FILE: android/graphics/CornerPathEffect.java
package android.graphics;
public class CornerPathEffect extends PathEffect { public CornerPathEffect(float radius) {} }
//// FILE: android/graphics/PathEffect.java
package android.graphics;
public class PathEffect { }
//// FILE: android/graphics/Path.java
package android.graphics;
public class Path {
  public enum Direction { CW, CCW }
  public enum FillType { WINDING, EVEN_ODD, INVERSE_WINDING, INVERSE_EVEN_ODD }
  public enum Op { DIFFERENCE, INTERSECT, UNION, XOR, REVERSE_DIFFERENCE }
  public Path() {}
  public Path(Path src) {}
  public void reset() {}
  public void rewind() {}
  public void moveTo(float x, float y) {}
  public void lineTo(float x, float y) {}
  public void quadTo(float x1, float y1, float x2, float y2) {}
  public void cubicTo(float x1, float y1, float x2, float y2, float x3, float y3) {}
  public void arcTo(RectF oval, float startAngle, float sweepAngle, boolean forceMoveTo) {}
  public void close() {}
  public void addCircle(float x, float y, float radius, Direction dir) {}
  public void addRect(float l, float t, float r, float b, Direction dir) {}
  public void addRect(RectF rect, Direction dir) {}
  public void addRoundRect(RectF rect, float rx, float ry, Direction dir) {}
  public void addRoundRect(RectF rect, float[] radii, Direction dir) {}
  public void addOval(RectF oval, Direction dir) {}
  public void addPath(Path src) {}
  public void setFillType(FillType ft) {}
  public boolean op(Path path, Op op) { return true; }
  public void computeBounds(RectF bounds, boolean exact) {}
  public void transform(Matrix matrix) {}
  public void offset(float dx, float dy) {}
  public boolean isEmpty() { return true; }
}
//// FILE: android/graphics/Paint.java
package android.graphics;
public class Paint {
  public static final int ANTI_ALIAS_FLAG = 1;
  public static final int FILTER_BITMAP_FLAG = 2;
  public static final int DITHER_FLAG = 4;
  public static final int SUBPIXEL_TEXT_FLAG = 128;
  public enum Style { FILL, STROKE, FILL_AND_STROKE }
  public enum Cap { BUTT, ROUND, SQUARE }
  public enum Join { MITER, ROUND, BEVEL }
  public enum Align { LEFT, CENTER, RIGHT }
  public static class FontMetrics { public float ascent; public float descent; public float top; public float bottom; public float leading; }
  public Paint() {}
  public Paint(int flags) {}
  public Paint(Paint paint) {}
  public int getColor() { return 0; }
  public void setColor(int color) {}
  public int getAlpha() { return 0; }
  public void setAlpha(int a) {}
  public float getStrokeWidth() { return 0; }
  public void setStrokeWidth(float width) {}
  public Style getStyle() { return null; }
  public void setStyle(Style style) {}
  public Cap getStrokeCap() { return null; }
  public void setStrokeCap(Cap cap) {}
  public Join getStrokeJoin() { return null; }
  public void setStrokeJoin(Join join) {}
  public Align getTextAlign() { return null; }
  public void setTextAlign(Align align) {}
  public float getTextSize() { return 0; }
  public void setTextSize(float textSize) {}
  public Typeface getTypeface() { return null; }
  public Typeface setTypeface(Typeface typeface) { return typeface; }
  public boolean isAntiAlias() { return true; }
  public void setAntiAlias(boolean aa) {}
  public boolean isFakeBoldText() { return false; }
  public void setFakeBoldText(boolean fakeBoldText) {}
  public float getLetterSpacing() { return 0; }
  public void setLetterSpacing(float letterSpacing) {}
  public PathEffect getPathEffect() { return null; }
  public PathEffect setPathEffect(PathEffect effect) { return effect; }
  public Shader getShader() { return null; }
  public Shader setShader(Shader shader) { return shader; }
  public ColorFilter getColorFilter() { return null; }
  public ColorFilter setColorFilter(ColorFilter filter) { return filter; }
  public Xfermode getXfermode() { return null; }
  public Xfermode setXfermode(Xfermode xfermode) { return xfermode; }
  public MaskFilter setMaskFilter(MaskFilter maskfilter) { return maskfilter; }
  public void setShadowLayer(float radius, float dx, float dy, int shadowColor) {}
  public void clearShadowLayer() {}
  public float measureText(String text) { return 0; }
  public float measureText(CharSequence text, int start, int end) { return 0; }
  public void getTextBounds(String text, int start, int end, Rect bounds) {}
  public FontMetrics getFontMetrics() { return null; }
  public float ascent() { return 0; }
  public float descent() { return 0; }
  public void setFilterBitmap(boolean filter) {}
  public boolean isFilterBitmap() { return false; }
  public void setDither(boolean dither) {}
  public void setFlags(int flags) {}
  public int getFlags() { return 0; }
  public String getFontFeatureSettings() { return null; }
  public void setFontFeatureSettings(String settings) {}
  public void setBlendMode(BlendMode blendmode) {}
}
//// FILE: android/graphics/BlendMode.java
package android.graphics;
public enum BlendMode { CLEAR, SRC, DST, SRC_OVER, DST_OVER, SRC_IN, DST_IN, SRC_OUT, DST_OUT, SRC_ATOP, DST_ATOP, XOR, PLUS, MODULATE, SCREEN, OVERLAY, DARKEN, LIGHTEN, COLOR_DODGE, COLOR_BURN, HARD_LIGHT, SOFT_LIGHT, DIFFERENCE, EXCLUSION, MULTIPLY, HUE, SATURATION, COLOR, LUMINOSITY }
//// FILE: android/graphics/MaskFilter.java
package android.graphics;
public class MaskFilter { }
//// FILE: android/graphics/BlurMaskFilter.java
package android.graphics;
public class BlurMaskFilter extends MaskFilter { public enum Blur { NORMAL, SOLID, OUTER, INNER } public BlurMaskFilter(float radius, Blur style) {} }
//// FILE: android/graphics/ColorFilter.java
package android.graphics;
public class ColorFilter { }
//// FILE: android/graphics/PorterDuffColorFilter.java
package android.graphics;
public class PorterDuffColorFilter extends ColorFilter { public PorterDuffColorFilter(int color, PorterDuff.Mode mode) {} }
//// FILE: android/graphics/ColorMatrixColorFilter.java
package android.graphics;
public class ColorMatrixColorFilter extends ColorFilter { public ColorMatrixColorFilter(float[] array) {} public ColorMatrixColorFilter(ColorMatrix matrix) {} }
//// FILE: android/graphics/ColorMatrix.java
package android.graphics;
public class ColorMatrix { public ColorMatrix() {} public ColorMatrix(float[] src) {} public void setSaturation(float sat) {} public final float[] getArray() { return null; } }
//// FILE: android/graphics/PixelFormat.java
package android.graphics;
public class PixelFormat { public static final int TRANSLUCENT = -3; public static final int TRANSPARENT = -2; public static final int OPAQUE = -1; public static final int RGBA_8888 = 1; }
//// FILE: android/graphics/Point.java
package android.graphics;
public class Point { public int x; public int y; public Point() {} public Point(int x, int y) {} }
//// FILE: android/graphics/PointF.java
package android.graphics;
public class PointF { public float x; public float y; public PointF() {} public PointF(float x, float y) {} }
//// FILE: android/graphics/Rect.java
package android.graphics;
public final class Rect {
  public int left; public int top; public int right; public int bottom;
  public Rect() {}
  public Rect(int left, int top, int right, int bottom) {}
  public Rect(Rect r) {}
  public final int width() { return 0; }
  public final int height() { return 0; }
  public final int centerX() { return 0; }
  public final int centerY() { return 0; }
  public void set(int left, int top, int right, int bottom) {}
  public boolean contains(int x, int y) { return false; }
  public boolean intersect(Rect r) { return false; }
  public void offset(int dx, int dy) {}
  public void inset(int dx, int dy) {}
  public final boolean isEmpty() { return true; }
}
//// FILE: android/graphics/RectF.java
package android.graphics;
public class RectF {
  public float left; public float top; public float right; public float bottom;
  public RectF() {}
  public RectF(float left, float top, float right, float bottom) {}
  public RectF(RectF r) {}
  public RectF(Rect r) {}
  public final float width() { return 0; }
  public final float height() { return 0; }
  public final float centerX() { return 0; }
  public final float centerY() { return 0; }
  public void set(float left, float top, float right, float bottom) {}
  public void set(RectF src) {}
  public boolean contains(float x, float y) { return false; }
  public boolean intersect(RectF r) { return false; }
  public void offset(float dx, float dy) {}
  public void inset(float dx, float dy) {}
  public void union(RectF r) {}
  public final boolean isEmpty() { return true; }
  public void round(Rect dst) {}
}
//// FILE: android/graphics/RuntimeShader.java
package android.graphics;
public class RuntimeShader extends Shader {
  public RuntimeShader(String shader) {}
  public void setFloatUniform(String uniformName, float value) {}
  public void setFloatUniform(String uniformName, float value1, float value2) {}
  public void setFloatUniform(String uniformName, float value1, float value2, float value3) {}
  public void setFloatUniform(String uniformName, float value1, float value2, float value3, float value4) {}
  public void setFloatUniform(String uniformName, float[] values) {}
  public void setIntUniform(String uniformName, int value) {}
  public void setColorUniform(String uniformName, int color) {}
  public void setInputShader(String shaderName, Shader shader) {}
}
//// FILE: android/graphics/Shader.java
package android.graphics;
public class Shader {
  public Shader() {}
  public enum TileMode { CLAMP, REPEAT, MIRROR, DECAL }
  public void setLocalMatrix(Matrix localM) {}
}
//// FILE: android/graphics/LinearGradient.java
package android.graphics;
public class LinearGradient extends Shader {
  public LinearGradient(float x0, float y0, float x1, float y1, int color0, int color1, TileMode tile) {}
  public LinearGradient(float x0, float y0, float x1, float y1, int[] colors, float[] positions, TileMode tile) {}
}
//// FILE: android/graphics/RadialGradient.java
package android.graphics;
public class RadialGradient extends Shader {
  public RadialGradient(float centerX, float centerY, float radius, int centerColor, int edgeColor, TileMode tileMode) {}
  public RadialGradient(float centerX, float centerY, float radius, int[] colors, float[] stops, TileMode tileMode) {}
}
//// FILE: android/graphics/BitmapShader.java
package android.graphics;
public class BitmapShader extends Shader { public BitmapShader(Bitmap bitmap, TileMode tileX, TileMode tileY) {} }
//// FILE: android/graphics/RenderEffect.java
package android.graphics;
public final class RenderEffect {
  public static RenderEffect createBlurEffect(float radiusX, float radiusY, Shader.TileMode edgeTreatment) { return null; }
  public static RenderEffect createBlurEffect(float radiusX, float radiusY, RenderEffect inputEffect, Shader.TileMode edgeTreatment) { return null; }
  public static RenderEffect createChainEffect(RenderEffect outer, RenderEffect inner) { return null; }
  public static RenderEffect createRuntimeShaderEffect(RuntimeShader shader, String uniformShaderName) { return null; }
  public static RenderEffect createOffsetEffect(float offsetX, float offsetY) { return null; }
  public static RenderEffect createColorFilterEffect(ColorFilter colorFilter) { return null; }
  public static RenderEffect createShaderEffect(Shader shader) { return null; }
  public static RenderEffect createBlendModeEffect(RenderEffect dst, RenderEffect src, BlendMode blendMode) { return null; }
}
//// FILE: android/graphics/Typeface.java
package android.graphics;
public class Typeface {
  public static final int NORMAL = 0;
  public static final int BOLD = 1;
  public static final int ITALIC = 2;
  public static final int BOLD_ITALIC = 3;
  public static final Typeface DEFAULT = null;
  public static final Typeface DEFAULT_BOLD = null;
  public static final Typeface SANS_SERIF = null;
  public static final Typeface SERIF = null;
  public static final Typeface MONOSPACE = null;
  public static Typeface create(String familyName, int style) { return null; }
  public static Typeface create(Typeface family, int style) { return null; }
  public static Typeface create(Typeface family, int weight, boolean italic) { return null; }
  public static Typeface createFromAsset(android.content.res.AssetManager mgr, String path) { return null; }
  public static Typeface createFromFile(java.io.File file) { return null; }
  public int getStyle() { return 0; }
  public int getWeight() { return 400; }
}
//// FILE: android/content/res/AssetManager.java
package android.content.res;
public final class AssetManager { public final java.io.InputStream open(String fileName) throws java.io.IOException { return null; } }
//// FILE: android/graphics/drawable/Drawable.java
package android.graphics.drawable;
public abstract class Drawable {
  public int getIntrinsicWidth() { return -1; }
  public int getIntrinsicHeight() { return -1; }
  public void setBounds(int left, int top, int right, int bottom) {}
  public final android.graphics.Rect getBounds() { return null; }
  public abstract void draw(android.graphics.Canvas canvas);
  public abstract void setAlpha(int alpha);
  public abstract void setColorFilter(android.graphics.ColorFilter colorFilter);
  public abstract int getOpacity();
  public void setTint(int tintColor) {}
  public Drawable mutate() { return this; }
}
//// FILE: android/graphics/drawable/BitmapDrawable.java
package android.graphics.drawable;
public class BitmapDrawable extends Drawable {
  public BitmapDrawable(android.content.res.Resources res, android.graphics.Bitmap bitmap) {}
  public final android.graphics.Bitmap getBitmap() { return null; }
  public void draw(android.graphics.Canvas canvas) {}
  public void setAlpha(int alpha) {}
  public void setColorFilter(android.graphics.ColorFilter colorFilter) {}
  public int getOpacity() { return 0; }
}
//// FILE: android/graphics/drawable/ColorDrawable.java
package android.graphics.drawable;
public class ColorDrawable extends Drawable {
  public ColorDrawable() {}
  public ColorDrawable(int color) {}
  public void draw(android.graphics.Canvas canvas) {}
  public void setAlpha(int alpha) {}
  public void setColorFilter(android.graphics.ColorFilter colorFilter) {}
  public int getOpacity() { return 0; }
}
//// FILE: android/graphics/drawable/GradientDrawable.java
package android.graphics.drawable;
public class GradientDrawable extends Drawable {
  public static final int RECTANGLE = 0;
  public static final int OVAL = 1;
  public enum Orientation { TOP_BOTTOM, TR_BL, RIGHT_LEFT, BR_TL, BOTTOM_TOP, BL_TR, LEFT_RIGHT, TL_BR }
  public GradientDrawable() {}
  public GradientDrawable(Orientation orientation, int[] colors) {}
  public void setShape(int shape) {}
  public void setColor(int argb) {}
  public void setColors(int[] colors) {}
  public void setCornerRadius(float radius) {}
  public float getCornerRadius() { return 0; }
  public void setCornerRadii(float[] radii) {}
  public void setStroke(int width, int color) {}
  public void setStroke(int width, int color, float dashWidth, float dashGap) {}
  public void setSize(int width, int height) {}
  public void setGradientType(int gradient) {}
  public void setOrientation(Orientation orientation) {}
  public void draw(android.graphics.Canvas canvas) {}
  public void setAlpha(int alpha) {}
  public void setColorFilter(android.graphics.ColorFilter colorFilter) {}
  public int getOpacity() { return 0; }
}
//// FILE: android/graphics/pdf/PdfRenderer.java
package android.graphics.pdf;
public final class PdfRenderer implements AutoCloseable {
  public PdfRenderer(android.os.ParcelFileDescriptor input) throws java.io.IOException {}
  public int getPageCount() { return 0; }
  public boolean shouldScaleForPrinting() { return false; }
  public Page openPage(int index) { return null; }
  public void close() {}
  public final class Page implements AutoCloseable {
    public static final int RENDER_MODE_FOR_DISPLAY = 1;
    public static final int RENDER_MODE_FOR_PRINT = 2;
    public int getIndex() { return 0; }
    public int getWidth() { return 0; }
    public int getHeight() { return 0; }
    public void render(android.graphics.Bitmap destination, android.graphics.Rect destClip, android.graphics.Matrix transform, int renderMode) {}
    public java.util.List<android.graphics.pdf.content.PdfPageTextContent> getTextContents() { return null; }
    public void close() {}
  }
}
//// FILE: android/graphics/pdf/content/PdfPageTextContent.java
package android.graphics.pdf.content;
public final class PdfPageTextContent {
  public String getText() { return null; }
  public java.util.List<android.graphics.RectF> getBounds() { return null; }
}
//// FILE: org/json/JSONException.java
package org.json;
public class JSONException extends RuntimeException { public JSONException(String s) { super(s); } }
//// FILE: org/json/JSONObject.java
package org.json;
public class JSONObject {
  public static final Object NULL = new Object();
  public JSONObject() {}
  public JSONObject(String json) throws JSONException {}
  public JSONObject(java.util.Map<?, ?> copyFrom) {}
  public JSONObject put(String name, boolean value) throws JSONException { return this; }
  public JSONObject put(String name, double value) throws JSONException { return this; }
  public JSONObject put(String name, int value) throws JSONException { return this; }
  public JSONObject put(String name, long value) throws JSONException { return this; }
  public JSONObject put(String name, Object value) throws JSONException { return this; }
  public JSONObject putOpt(String name, Object value) throws JSONException { return this; }
  public Object get(String name) throws JSONException { return null; }
  public Object opt(String name) { return null; }
  public boolean has(String name) { return false; }
  public boolean isNull(String name) { return false; }
  public Object remove(String name) { return null; }
  public int length() { return 0; }
  public java.util.Iterator<String> keys() { return null; }
  public JSONArray names() { return null; }
  public String getString(String name) throws JSONException { return null; }
  public String optString(String name) { return null; }
  public String optString(String name, String fallback) { return null; }
  public int getInt(String name) throws JSONException { return 0; }
  public int optInt(String name) { return 0; }
  public int optInt(String name, int fallback) { return 0; }
  public long getLong(String name) throws JSONException { return 0; }
  public long optLong(String name) { return 0; }
  public long optLong(String name, long fallback) { return 0; }
  public double getDouble(String name) throws JSONException { return 0; }
  public double optDouble(String name) { return 0; }
  public double optDouble(String name, double fallback) { return 0; }
  public boolean getBoolean(String name) throws JSONException { return false; }
  public boolean optBoolean(String name) { return false; }
  public boolean optBoolean(String name, boolean fallback) { return false; }
  public JSONArray getJSONArray(String name) throws JSONException { return null; }
  public JSONArray optJSONArray(String name) { return null; }
  public JSONObject getJSONObject(String name) throws JSONException { return null; }
  public JSONObject optJSONObject(String name) { return null; }
  public String toString() { return ""; }
  public String toString(int indentSpaces) throws JSONException { return ""; }
  public static String quote(String data) { return null; }
}
//// FILE: org/json/JSONArray.java
package org.json;
public class JSONArray {
  public JSONArray() {}
  public JSONArray(String json) throws JSONException {}
  public JSONArray(java.util.Collection<?> copyFrom) {}
  public JSONArray(Object array) throws JSONException {}
  public int length() { return 0; }
  public JSONArray put(boolean value) { return this; }
  public JSONArray put(double value) throws JSONException { return this; }
  public JSONArray put(int value) { return this; }
  public JSONArray put(long value) { return this; }
  public JSONArray put(Object value) { return this; }
  public JSONArray put(int index, Object value) throws JSONException { return this; }
  public Object get(int index) throws JSONException { return null; }
  public Object opt(int index) { return null; }
  public Object remove(int index) { return null; }
  public boolean isNull(int index) { return false; }
  public String getString(int index) throws JSONException { return null; }
  public String optString(int index) { return null; }
  public String optString(int index, String fallback) { return null; }
  public int getInt(int index) throws JSONException { return 0; }
  public int optInt(int index) { return 0; }
  public int optInt(int index, int fallback) { return 0; }
  public long getLong(int index) throws JSONException { return 0; }
  public long optLong(int index) { return 0; }
  public long optLong(int index, long fallback) { return 0; }
  public double getDouble(int index) throws JSONException { return 0; }
  public double optDouble(int index) { return 0; }
  public double optDouble(int index, double fallback) { return 0; }
  public boolean getBoolean(int index) throws JSONException { return false; }
  public boolean optBoolean(int index) { return false; }
  public boolean optBoolean(int index, boolean fallback) { return false; }
  public JSONArray getJSONArray(int index) throws JSONException { return null; }
  public JSONArray optJSONArray(int index) { return null; }
  public JSONObject getJSONObject(int index) throws JSONException { return null; }
  public JSONObject optJSONObject(int index) { return null; }
  public String join(String separator) throws JSONException { return null; }
  public String toString() { return ""; }
  public String toString(int indentSpaces) throws JSONException { return ""; }
}
//// FILE: androidx/core/content/ContextCompat.java
package androidx.core.content;
public class ContextCompat {
  public static final int RECEIVER_EXPORTED = 2;
  public static final int RECEIVER_NOT_EXPORTED = 4;
  public static int checkSelfPermission(android.content.Context context, String permission) { return 0; }
  public static int getColor(android.content.Context context, int id) { return 0; }
  public static android.graphics.drawable.Drawable getDrawable(android.content.Context context, int id) { return null; }
  public static <T> T getSystemService(android.content.Context context, Class<T> serviceClass) { return null; }
  public static void startForegroundService(android.content.Context context, android.content.Intent intent) {}
  public static android.content.Intent registerReceiver(android.content.Context context, android.content.BroadcastReceiver receiver, android.content.IntentFilter filter, int flags) { return null; }
  public static java.util.concurrent.Executor getMainExecutor(android.content.Context context) { return null; }
}
//// FILE: androidx/core/app/NotificationCompat.java
package androidx.core.app;
public class NotificationCompat {
  public static final String CATEGORY_REMINDER = "reminder";
  public static final String CATEGORY_ALARM = "alarm";
  public static final String CATEGORY_STATUS = "status";
  public static final int DEFAULT_ALL = -1;
  public static final int DEFAULT_SOUND = 1;
  public static final int DEFAULT_VIBRATE = 2;
  public static final int PRIORITY_DEFAULT = 0;
  public static final int PRIORITY_LOW = -1;
  public static final int PRIORITY_MIN = -2;
  public static final int PRIORITY_HIGH = 1;
  public static final int PRIORITY_MAX = 2;
  public static final int VISIBILITY_PUBLIC = 1;
  public static final int VISIBILITY_PRIVATE = 0;
  public static final int VISIBILITY_SECRET = -1;
  public static final int FOREGROUND_SERVICE_IMMEDIATE = 1;
  public static abstract class Style { }
  public static class BigTextStyle extends Style {
    public BigTextStyle() {}
    public BigTextStyle bigText(CharSequence cs) { return this; }
    public BigTextStyle setBigContentTitle(CharSequence title) { return this; }
    public BigTextStyle setSummaryText(CharSequence cs) { return this; }
  }
  public static class Action {
    public Action(int icon, CharSequence title, android.app.PendingIntent intent) {}
  }
  public static class Builder {
    public Builder(android.content.Context context, String channelId) {}
    public Builder setSmallIcon(int icon) { return this; }
    public Builder setLargeIcon(android.graphics.Bitmap icon) { return this; }
    public Builder setContentTitle(CharSequence title) { return this; }
    public Builder setContentText(CharSequence text) { return this; }
    public Builder setSubText(CharSequence text) { return this; }
    public Builder setTicker(CharSequence tickerText) { return this; }
    public Builder setStyle(Style style) { return this; }
    public Builder setPriority(int pri) { return this; }
    public Builder setCategory(String category) { return this; }
    public Builder setVisibility(int visibility) { return this; }
    public Builder setAutoCancel(boolean autoCancel) { return this; }
    public Builder setOngoing(boolean ongoing) { return this; }
    public Builder setOnlyAlertOnce(boolean onlyAlertOnce) { return this; }
    public Builder setSilent(boolean silent) { return this; }
    public Builder setContentIntent(android.app.PendingIntent intent) { return this; }
    public Builder setDeleteIntent(android.app.PendingIntent intent) { return this; }
    public Builder setFullScreenIntent(android.app.PendingIntent intent, boolean highPriority) { return this; }
    public Builder setDefaults(int defaults) { return this; }
    public Builder setColor(int argb) { return this; }
    public Builder setColorized(boolean colorize) { return this; }
    public Builder setWhen(long when) { return this; }
    public Builder setShowWhen(boolean show) { return this; }
    public Builder setTimeoutAfter(long durationMs) { return this; }
    public Builder setGroup(String groupKey) { return this; }
    public Builder setNumber(int number) { return this; }
    public Builder setVibrate(long[] pattern) { return this; }
    public Builder setSound(android.net.Uri sound) { return this; }
    public Builder setForegroundServiceBehavior(int behavior) { return this; }
    public Builder addAction(int icon, CharSequence title, android.app.PendingIntent intent) { return this; }
    public Builder addAction(Action action) { return this; }
    public Builder setLocalOnly(boolean b) { return this; }
    public android.app.Notification build() { return null; }
  }
}
//// FILE: androidx/core/app/NotificationManagerCompat.java
package androidx.core.app;
public final class NotificationManagerCompat {
  public static final int IMPORTANCE_NONE = 0;
  public static final int IMPORTANCE_HIGH = 4;
  public static final int IMPORTANCE_DEFAULT = 3;
  public static NotificationManagerCompat from(android.content.Context context) { return null; }
  public boolean areNotificationsEnabled() { return true; }
  public int getImportance() { return 0; }
  public void notify(int id, android.app.Notification notification) {}
  public void notify(String tag, int id, android.app.Notification notification) {}
  public void cancel(int id) {}
  public void cancel(String tag, int id) {}
  public void cancelAll() {}
  public android.app.NotificationChannel getNotificationChannel(String channelId) { return null; }
  public void createNotificationChannel(android.app.NotificationChannel channel) {}
  public void deleteNotificationChannel(String channelId) {}
  public boolean canUseFullScreenIntent() { return true; }
}
//// FILE: androidx/core/view/WindowCompat.java
package androidx.core.view;
public final class WindowCompat {
  public static WindowInsetsControllerCompat getInsetsController(android.view.Window window, android.view.View view) { return null; }
  public static void setDecorFitsSystemWindows(android.view.Window window, boolean decorFitsSystemWindows) {}
}
//// FILE: androidx/core/view/WindowInsetsCompat.java
package androidx.core.view;
public class WindowInsetsCompat {
  public static final class Type {
    public static int statusBars() { return 1; }
    public static int navigationBars() { return 2; }
    public static int systemBars() { return 3; }
    public static int ime() { return 8; }
    public static int displayCutout() { return 128; }
  }
}
//// FILE: androidx/core/view/WindowInsetsControllerCompat.java
package androidx.core.view;
public final class WindowInsetsControllerCompat {
  public static final int BEHAVIOR_DEFAULT = 1;
  public static final int BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE = 2;
  public WindowInsetsControllerCompat(android.view.Window window, android.view.View view) {}
  public void show(int types) {}
  public void hide(int types) {}
  public int getSystemBarsBehavior() { return 0; }
  public void setSystemBarsBehavior(int behavior) {}
  public boolean isAppearanceLightStatusBars() { return false; }
  public void setAppearanceLightStatusBars(boolean isLight) {}
  public boolean isAppearanceLightNavigationBars() { return false; }
  public void setAppearanceLightNavigationBars(boolean isLight) {}
}
