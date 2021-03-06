package net.kollnig.consent.library;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import net.kollnig.consent.ConsentManager;
import net.kollnig.consent.R;

import java.lang.reflect.Method;

import lab.galaxy.yahfa.HookMain;

public class GoogleAdsLibrary extends Library {
    public static final String LIBRARY_IDENTIFIER = "google_ads";

    @NonNull
    @Override
    public String getId() {
        return LIBRARY_IDENTIFIER;
    }

    static final String TAG = "HOOKED";

    public static void replacementMethod(@NonNull Context context) {
        Log.d(TAG, "successfully hooked GAds");

        if (!Boolean.TRUE.equals(ConsentManager.getInstance().hasConsent(LIBRARY_IDENTIFIER)))
            return;

        originalMethod(context);
    }

    // this method will be replaced by hook
    public static void originalMethod(@NonNull Context context) {
        throw new RuntimeException("Could not overwrite original Firebase method");
    }

    public static void replacementMethod(@NonNull Context context, @NonNull Object listener) {
        Log.d(TAG, "successfully hooked GAds");

        if (!Boolean.TRUE.equals(ConsentManager.getInstance().hasConsent(LIBRARY_IDENTIFIER)))
            return;

        originalMethod(context, listener);
    }

    // this method will be replaced by hook
    public static void originalMethod(@NonNull Context context, @NonNull Object listener) {
        throw new RuntimeException("Could not overwrite original Firebase method");
    }

    public static void replacementLoadAd(Object thiz, @NonNull Object adRequest) {
        Log.d(TAG, "successfully hooked GAds");

        if (!Boolean.TRUE.equals(ConsentManager.getInstance().hasConsent(LIBRARY_IDENTIFIER)))
            return;

        originalLoadAd(thiz, adRequest);
    }

    // this method will be replaced by hook
    public static void originalLoadAd(Object thiz, @NonNull Object adRequest) {
        throw new RuntimeException("Could not overwrite original Firebase method");
    }

    @Override
    public Library initialise(Context context) throws LibraryInteractionException {
        super.initialise(context);

        // public void loadAd(@NonNull AdRequest adRequest) {
        try {
            Class<?> baseClass = Class.forName("com.google.android.gms.ads.BaseAdView");
            String methodName = "loadAd";
            String methodSig = "(Lcom/google/android/gms/ads/AdRequest;)V";

            try {
                Method methodOrig = (Method) HookMain.findMethodNative(baseClass, methodName, methodSig);
                Method methodHook = GoogleAdsLibrary.class.getMethod("replacementLoadAd", Object.class, Object.class);
                Method methodBackup = GoogleAdsLibrary.class.getMethod("originalLoadAd", Object.class, Object.class);
                HookMain.backupAndHook(methodOrig, methodHook, methodBackup);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Could not overwrite method");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //.method public static initialize(Landroid/content/Context;)V
        //.method public static initialize(Landroid/content/Context;Lcom/google/android/gms/ads/initialization/OnInitializationCompleteListener;)V
        Class<?> baseClass = findBaseClass();
        String methodName = "initialize";
        String methodSig = "(Landroid/content/Context;)V";

        try {
            Method methodOrig = (Method) HookMain.findMethodNative(baseClass, methodName, methodSig);
            Method methodHook = GoogleAdsLibrary.class.getMethod("replacementMethod", Context.class);
            Method methodBackup = GoogleAdsLibrary.class.getMethod("originalMethod", Context.class);
            HookMain.backupAndHook(methodOrig, methodHook, methodBackup);

            String methodSig2 = "(Landroid/content/Context;Lcom/google/android/gms/ads/initialization/OnInitializationCompleteListener;)V";
            Method methodOrig2 = (Method) HookMain.findMethodNative(baseClass, methodName, methodSig2);
            Method methodHook2 = GoogleAdsLibrary.class.getMethod("replacementMethod", Context.class, Object.class);
            Method methodBackup2 = GoogleAdsLibrary.class.getMethod("originalMethod", Context.class, Object.class);
            HookMain.backupAndHook(methodOrig2, methodHook2, methodBackup2);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not overwrite method");
        }

        return this;
    }

    @Override
    public void passConsentToLibrary(boolean consent) {
        if (!consent)
            getContext().deleteSharedPreferences("admob");
    }

    @Override
    String getBaseClass() {
        return "com.google.android.gms.ads.MobileAds";
    }

    @Override
    public int getConsentMessage() {
        return R.string.google_ads_consent_msg;
    }

    @Override
    public int getName() {
        return R.string.google_ads;
    }
}
