package zhengzhou.individual.catsDj.util;

import android.content.Context;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import zhengzhou.individual.catsDj.R;

public class SafeClient {

    public static void init(Context context) {
        safeClient = getSafeOkHttpClient(context);
    }

    public static OkHttpClient safeClient;
    public static OkHttpClient getSafeOkHttpClient(Context context) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream input = context.getResources().openRawResource(R.raw.cer_cate);
            Certificate cer = cf.generateCertificate(input);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyStore.setCertificateEntry("asdf", cer);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            X509TrustManager userTrustManager = (X509TrustManager) trustManagers[0];

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{userTrustManager}, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, userTrustManager);
            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
