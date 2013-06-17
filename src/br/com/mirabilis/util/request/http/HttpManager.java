package br.com.mirabilis.util.request.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import br.com.mirabilis.system.wifi.Wifi;
import br.com.mirabilis.util.request.ResponseData;
import br.com.mirabilis.util.request.http.exception.HttpManagerException;
import br.com.mirabilis.util.request.http.listener.HttpListener;

/**
 * Classe respons�vel pela realiza��o de requisi��es http.
 * @author Rodrigo Sim�es Rosa.
 */
public class HttpManager {
	
	private Context context;
	private String cryptFormat;
	private int timeoutConnection;
	private int timeoutSocket;
	
	private static final String ERROR_HTTP = "Error http : ";
	private static final String ENTITY_NULL = "Objeto entity nulo";
	
	public HttpManager(Context context) {
		this.context = context;
	}
	
	/**
	 * Bloco de inicializa��o.
	 */
	{
		cryptFormat = HTTP.UTF_8;
		timeoutConnection = 0;
		timeoutSocket = 5000;
	}
	
	/**
	 * M�todo respons�vel pela chamada get.
	 * @param url
	 * @param listener
	 */
	public ResponseData<InputStream> get(String url){
		return get(url, this.timeoutConnection, this.timeoutSocket);
	}
	
	/**
	 * M�todo respons�vel pela chamada get.
	 * @param url
	 * @param listener
	 * @param timeoutConnection Timeout que ser� executado at� a conex�o estabelecer.
	 * @param timeoutSocket Timeout que ser� o tempo de espera que o client ir� aguardar.
	 */
	@SuppressWarnings("finally")
	public ResponseData<InputStream> get(String url, int timeoutConnection, int timeoutSocket){
		
		HttpClient httpClient = new DefaultHttpClient(getHttpParams(timeoutConnection, timeoutSocket));
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse = null;
		HttpEntity httpEntity = null;
		
		String message = null;
		InputStream data = null;
		boolean successfully = false;
		
		try {
			checkWifi();
			httpResponse = httpClient.execute(httpGet);
			httpEntity = httpResponse.getEntity();
			
			int status = httpResponse.getStatusLine().getStatusCode();
			if(status == HttpStatus.SC_OK){
				if(httpEntity != null){
					data = httpEntity.getContent();
					successfully = true;
				}else{
					message = ENTITY_NULL;
				}
			}else{
				message = ERROR_HTTP + status;
			}
		} catch (ClientProtocolException e) {
			message = e.getMessage();
		} catch (IOException e) {
			message = e.getMessage();
		} catch (HttpManagerException e) {
			message = e.getMessage();
		} finally {
			return new ResponseData<InputStream>(successfully, message, data);
		}
	}
	
	/**
	 * M�todo respons�vel pela chamada post enviando um XML.
	 * @param url
	 * @param xml
	 */
	public void postXML(String url, String xml, HttpListener<InputStream> listener){
		postXML(url, xml, listener, this.cryptFormat, this.timeoutConnection, this.timeoutSocket);
	}
	
	/**
	 * M�todo respons�vel pela chamada post enviando um XML.
	 * @param url
	 * @param listener
	 */
	public void postXML(String url, String xml, HttpListener<InputStream> listener, String cryptFormat, int timeoutConnection, int timeoutSocket){
		
		HttpClient httpClient = new DefaultHttpClient(getHttpParams(timeoutConnection,timeoutSocket));
		HttpPost httpPost = new HttpPost(url);
		HttpResponse httpResponse = null;
		HttpEntity httpEntity = null;
		
		boolean successfully = false;
		InputStream data = null;
		String message = null;
		try {
			checkWifi();
			StringEntity stringEntity = new StringEntity(xml, cryptFormat);
			stringEntity.setContentType(ContentType.XML.toString());
			
	        httpPost.setEntity(stringEntity);
	        
	        httpPost.addHeader(HeaderType.ACCEPT.toString(), HeaderType.XML.toString());
			httpPost.addHeader(HTTP.CONTENT_TYPE, HeaderType.XML.toString());
			
			httpResponse = httpClient.execute(httpPost);
			httpEntity = httpResponse.getEntity();
			int status = httpResponse.getStatusLine().getStatusCode();
			if(status == HttpStatus.SC_OK){
				if(httpEntity != null){
					data = httpResponse.getEntity().getContent();
					message = EntityUtils.toString(httpEntity);
					successfully = true;
				}else{
					message = ENTITY_NULL;
				}
			}else{
				message = ERROR_HTTP + status;
			}
		} catch (ClientProtocolException e) {
			message = e.getMessage();
		} catch (IOException e) {
			message = e.getMessage();
		} catch (HttpManagerException e) {
			message = e.getMessage();
		} finally {
			listener.onResponseData(new ResponseData<InputStream>(successfully, message, data));
		}
	}
	
	
	/**
	 * M�todo respons�vel pela chamada post enviando um Json.
	 * @param url
	 * @param listener
	 */
	public void postJson(String url, JSONObject json,HttpListener<InputStream> listener, String cryptFormat, int timeoutConnection, int timeoutSocket){
		
		HttpClient httpClient = new DefaultHttpClient(getHttpParams(timeoutConnection,timeoutSocket));
		HttpPost httpPost = new HttpPost(url);
		HttpResponse httpResponse = null;
		HttpEntity httpEntity = null;
		
		boolean successfully = false;
		InputStream data = null;
		String message = null;
		try {
			checkWifi();
			ByteArrayEntity baEntity = new ByteArrayEntity(json.toString().getBytes("UTF8"));
			baEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,ContentType.JSON.toString()));
			httpPost.setEntity(baEntity);
	        httpResponse = httpClient.execute(httpPost);
			httpEntity = httpResponse.getEntity();

			int status = httpResponse.getStatusLine().getStatusCode();
			if(status == HttpStatus.SC_OK){
				if(httpEntity != null){
					data = httpResponse.getEntity().getContent();
					message = EntityUtils.toString(httpEntity);
					successfully = true;
				}else{
					message = ENTITY_NULL;
				}
			}else{
				message = ERROR_HTTP + status;
			}
		} catch (ClientProtocolException e) {
			message = e.getMessage();
		} catch (IOException e) {
			message = e.getMessage();
		} catch (HttpManagerException e) {
			message = e.getMessage();
		} finally {
			listener.onResponseData(new ResponseData<InputStream>(successfully, message, data));
		}
	}

	/**
	 * Retorna os par�metros necess�rios para realizar a configura��o.
	 * @return
	 */
	private HttpParams getHttpParams(int connection, int socket) {
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, connection);
		HttpConnectionParams.setSoTimeout(params, socket);
		return params;
	}

	/**
	 * Verifica se existe conex�o com wifi e se existe uma rede conectada.
	 * @throws HttpManagerException
	 */
	private void checkWifi() throws HttpManagerException{
		if(!Wifi.isWifiEnabled(this.context) || !Wifi.isWifiConnected(context)){
			throw new HttpManagerException("O Wifi est� desabilitado ou n�o existe nenhuma antena conectada.");
		}
	}
	
	/**
	 * Enumera��o respons�vel por armazenar os tipos de dados a serem enviados via POST.
	 * @author Rodrigo Sim�es Rosa
	 */
	public enum ContentType{
		XML("text/xml"), 
		JSON("application/json");
		
		private String value;
		private ContentType(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return this.value;
		}
	}
	
	/**
	 * Enumera��o respons�vel por armazer os tipos de header e suas propriedades.
	 * @author Rodrigo
	 *
	 */
	public enum HeaderType {
		XML("application/xml"),
		ACCEPT("Accept");
		
		private String value;
		private HeaderType(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return this.value;
		}
	}
}
