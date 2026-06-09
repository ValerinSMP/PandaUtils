package darkpanda73.PandaUtils.PandaHTTPServer.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

public class SSLUtils {

	public static SSLContext createSSLContext(File keyFile, File certFile, String password) {
		try {
			PrivateKey privateKey = generateKey(keyFile);
			if (privateKey == null)
				return null;
			Certificate cert = generateCertificate(certFile);
			if (cert == null)
				return null;

			KeyStore store = KeyStore.getInstance("JKS");
			store.load(null);
			store.setCertificateEntry("certificate", cert);
			store.setKeyEntry("private-key", privateKey, password.toCharArray(), new Certificate[] { cert });

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(store, password.toCharArray());

			SSLContext context = SSLContext.getInstance("SSL");
			context.init(kmf.getKeyManagers(), null, SecureRandom.getInstanceStrong());

			return context;
		} catch (Exception e) {
			return null;
		}
	}

	private static PrivateKey generateKey(File privkey) {
		try {
			InputStream ikey = new FileInputStream(privkey);
			byte[] bytes = new byte[ikey.available()];
			ikey.read(bytes);
			String skey = new String(bytes, Charset.defaultCharset());
			ikey.close();
			skey = skey.replaceAll("-----.*-----", "").replaceAll(System.lineSeparator(), "");

			KeyFactory kf = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(skey));
			return kf.generatePrivate(keySpec);
		} catch (Exception e) {
			return null;
		}
	}

	private static Certificate generateCertificate(File fullchain) {
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			return cf.generateCertificate(new FileInputStream(fullchain));
		} catch (Exception e) {
			return null;
		}
	}

}
