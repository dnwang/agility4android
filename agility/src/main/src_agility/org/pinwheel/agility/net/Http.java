package org.pinwheel.agility.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

@Deprecated
public final class Http {

    private Http(){

    }

	private final static String TAG = "HTTP" ;
	
	/**
	 * request type GET
	 */
	public static int GET 	= 0x0 ;
	/**
	 * request type POST
	 */
	public static int POST 	= 0x1 ;
	
	/**
	 * @param timeout	-1:unlimit (timeout in milliseconds)
	 * @param type		POST or GET
	 * @param url		
	 * @return			connect error log or Response
	 */
	public static String simpleRequest(int timeout ,int type ,String url) {
		try {
			if (GET == type) {
				return get(timeout, url) ;
			} else if (POST == type){
				List<NameValuePair> params = new ArrayList<NameValuePair>() ;
				try {
					String[] p1 = url.split("[?]") ;
					String[] p2 = p1[1].split("[&]") ;
					String[] p3 ;
					String postUrl = p1[0] ;
					int startIndex = 0 ;
					if(p2[0].contains("action=")){
						postUrl += "?" + p2[0] ;
						startIndex = 1 ;
					}
					int size = p2.length ;
					for (int i = startIndex; i < size; i++) {
						p3 = p2[i].split("[=]") ;
						params.add(new BasicNameValuePair(p3[0], p3[1])) ;
					}
					p1 = null ;
					p2 = null ;
					p3 = null ;
					return post(timeout, postUrl, new UrlEncodedFormEntity(params, HTTP.UTF_8)) ;
				} catch (Exception e) {
					if(!url.contains("[?]"))
						return post(timeout, url, new UrlEncodedFormEntity(params, HTTP.UTF_8)) ;
					else
						return "please check URL" ;
				}
			}
			return "please set type : GET or POST" ;
		} catch (Exception e) {
			return e.getMessage() ;
		} 
	}
	
	/**
	 * @param timeout	-1:unlimit (timeout in milliseconds)
	 * @param url		
	 * @return
	 */
	public static Bitmap getBitmap(int timeout, String url){
		Bitmap result = null ;
		try {
			HttpGet get = new HttpGet(url) ;
			HttpParams p = new BasicHttpParams() ;
			if(timeout != -1)
				HttpConnectionParams.setConnectionTimeout(p, timeout) ;
			HttpResponse response = new DefaultHttpClient(p).execute(get) ;
			result = BitmapFactory.decodeStream(response.getEntity().getContent()) ;
		} catch (Exception e) {
            Log.e(TAG, "Download bitmap error! url: " + url);
            if (result != null) {
                result.recycle();
            }
            result = null;
        }
		return result ;
	}
	
	/**
	 * @param timeout	-1:unlimit (timeout in milliseconds)
	 * @param url
	 * @param file		local file path
	 * @return			file or null
	 */
	public static File downloadFile(int timeout, String url, File file){
		if(file.isDirectory())
			return null ;
		InputStream inStream = null ;
		FileOutputStream fos = null ;
		try {
			HttpGet get = new HttpGet(url) ;
			HttpParams p = new BasicHttpParams() ;
			if(timeout != -1)
				HttpConnectionParams.setConnectionTimeout(p, timeout) ;
			HttpResponse response = new DefaultHttpClient(p).execute(get) ;
			inStream = response.getEntity().getContent() ;
			if(file.exists())
				file.delete() ;
			file.createNewFile() ;
			fos = new FileOutputStream(file) ;
			byte[] buf = new byte[1024] ;
			int flag = 0 ;
			while((flag = inStream.read(buf))!=-1){
				fos.write(buf, 0, flag) ;
			}
			inStream.close() ;
			fos.flush() ;
			fos.close() ;
			return file ;
		} catch (Exception e) { 
			if(fos != null)
				try {
					fos.close() ;
				} catch (IOException e1) { }
			if(inStream != null)
				try {
					inStream.close() ;
				} catch (IOException e1) { }
			return null ;
		}
	}
	
	public static String post(int timeout, String url ,HttpEntity entity) throws Exception {
		String result = "POST error" ;
		//Connect Type
		HttpPost post = new HttpPost(url) ;
		post.setEntity(entity) ;
		//setHeader
		post.setHeader("Connection", "Keep-Alive") ;
		post.setHeader("Accept-Charset", "gb2312,utf-8;q=0.7,*;q=0.7") ;
		post.setHeader("Accept", "*/*") ;
		//Params & execute
		HttpParams p = new BasicHttpParams() ;
		if(timeout != -1)
			HttpConnectionParams.setConnectionTimeout(p, timeout) ;
		HttpResponse response = new DefaultHttpClient(p).execute(post) ;
		int code = response.getStatusLine().getStatusCode() ;
		if (code == HttpStatus.SC_OK) {
			result = EntityUtils.toString(response.getEntity(), "gb2312");
		}else{
			result = code + "" ;
		}
		return result ;
	}
	
	private static String get(int timeout, String url) throws Exception {
		String result = "GET error" ;
		//Connect Type 
		HttpGet get = new HttpGet(url); 
		//setHeader
		get.setHeader("Connection", "Keep-Alive") ;
		get.setHeader("Accept-Charset", "gb2312,utf-8;q=0.7,*;q=0.7") ;
		get.setHeader("Accept", "*/*") ;
		//Params & execute
		HttpParams p = new BasicHttpParams() ;
		if(timeout != -1)
			HttpConnectionParams.setConnectionTimeout(p, timeout) ;
		HttpResponse response = new DefaultHttpClient(p).execute(get) ;
		int code = response.getStatusLine().getStatusCode() ;
		if (code == HttpStatus.SC_OK) {
			result = EntityUtils.toString(response.getEntity(), "gb2312");
		}else{
			result = code + "" ;
		}
		return result ;
	}

}

