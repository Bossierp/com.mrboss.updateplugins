package com.mrboss.updateplugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Update {
	String versionNo = "";
	String updateUrl = "";
	Handler uiHandler = new Handler(); // 用于processDialog界面更新
	ProgressDialog pd = null;
	int iProcessIndex = 0; // 进度条的进度
	int iProcessMax = 100; // 进度条的最大值
	String apkName = "";
	Context mContext;
	int iApkSize = 0; // apk的大小
	//
	public Update(Context context) {
		this.mContext = context;
	}

	public String getServerMessage(String url, String ver, String name){
		updateUrl = url;
		versionNo = ver;
		apkName = name;
		return "true";
	}

	public void doUpdate(){
		pd = new ProgressDialog(mContext);
		pd.setTitle("正在下载");
		pd.setMessage(apkName + "( V " + versionNo + ")");
		// 设置ProgressDialog 的进度条是否不明确
		pd.setIndeterminate(false);
		pd.setMax(iProcessMax);
		pd.setProgress(0);
		// 设置进度条风格，风格为长形
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setButton("取 消", new SureButtonListener());
		pd.setCancelable(true);
		downFile(updateUrl);
	}

	/**
	 * 下载apk
	 */
	public void downFile(final String url) {
		pd.show();
		new Thread() {
			public void run() {
				try {
					// InputStream is = entity.getContent();
					URL urla = new URL(url);
					HttpURLConnection connection = (HttpURLConnection) urla
					                               .openConnection();
					iApkSize = connection.getContentLength();

					InputStream is = connection.getInputStream();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						File file = null;
						try{
							//先获取sd卡中的文件夹
							file = new File(
									Environment
											.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
									"MrBossErp.apk");
						}
						catch (Exception e){
							//如果sd卡不存在的话便获取应用内存
							file = new File(
									mContext.getFilesDir(),
									"MrBossErp.apk");
						}

						fileOutputStream = new FileOutputStream(file);
						byte[] b = new byte[1024];
						int charb = -1;
						int count = 0;
						while ((charb = is.read(b)) != -1) {
							count += charb;
							fileOutputStream.write(b, 0, charb);
							int iIndex = (int) ((((double) count) / iApkSize) * 100);
							// 至少增加1%才更新processDialog一次
							if (iProcessIndex < iIndex) {
								iProcessIndex = (iIndex <= iProcessMax ? iIndex
								                 : iProcessMax);
								uiHandler.post(runnableUi);
							}
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					DoInstall();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} .start();
	}

	Handler installHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			install();
		}
	};

	/**
	 * 下载完成，通过handler将下载对话框取消
	 */
	public void DoInstall() {
		new Thread() {
			public void run() {
				Message message = installHandler.obtainMessage();
				installHandler.sendMessage(message);
			}
		} .start();
	}

	/**
	 * 安装应用
	 */
	public void install() {
		// 调用系统执行安装的接口
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(
		    Uri.fromFile(new File(
		                     Environment
		                     .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
		                     "MrBossErp.apk")),
		    "application/vnd.android.package-archive");
		mContext.startActivity(intent);
		// 程序自己退出
		System.exit(0);
	}

	// 构建Runnable对象，在runnable中更新界面
	Runnable runnableUi = new Runnable() {
		@Override
		public void run() {
			// 更新界面
			pd.setProgress(iProcessIndex);
			if (iProcessIndex == iProcessMax) {
				pd.cancel();
			}
		}
	};

	private String toString(InputStream input) {
		String content = null;
		try {
			InputStreamReader ir = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(ir);

			StringBuilder sbuff = new StringBuilder();
			while (null != br) {
				String temp = br.readLine();
				if (null == temp)break;
				sbuff.append(temp).append(System.getProperty("line.separator"));
			}

			content = sbuff.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return content;
	}
}