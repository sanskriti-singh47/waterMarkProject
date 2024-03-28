package IlovePdfWaterMark.IlovePdf;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ILovePdfWatermark {
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	public static String getResponse() throws IOException {
		OkHttpClient client = new OkHttpClient()
				.newBuilder()
				.build();
		RequestBody body = new MultipartBody
				.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("public_key",
						"project_public_8005c1736c474b6245e8e3d5064e9517_F5Hf3fc9108c981e917dc81d2a7726c2d73f6")
				.build();
		Request request = new Request
				.Builder()
				.url("https://api.ilovepdf.com/v1/auth")
				.method("POST", body)
				.addHeader("Content-Type", "application/json")
				.addHeader("Cookie", "_csrf=RWiQpwJmISuoVClp0ek-VXxPvptVFhYP")
				.build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	public static String getServerAndTaskId(String token) throws IOException {
		OkHttpClient client = new OkHttpClient()
				.newBuilder()
				.build();
		Request request = new Request
				.Builder()
				.url("https://api.ilovepdf.com/v1/start/watermark")
				.method("GET", null)
				.addHeader("Authorization", "Bearer " + token)
				.build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	public static String getServerFileName(String server, String task, String token) throws IOException {
		OkHttpClient client = new OkHttpClient()
				.newBuilder()
				.build();
		RequestBody body = new MultipartBody
				.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("task", task)
				.addFormDataPart("file", "dummy.pdf", 
						RequestBody.create(
						new File("dummy.pdf"), 
						MediaType.parse("application/octet-stream")))
				.build();
		Request request = new Request
				.Builder()
				.url("https://" + server + "/v1/upload")
				.method("POST", body)
				.addHeader("Authorization", "Bearer " + token)
				.build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	public static String operations(String token, String server, String task, String newFilename)
			throws IOException, JSONException {
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		 JSONObject outerobj = new JSONObject();
		 outerobj.put("task", task);
		 outerobj.put("text", "Water Mark Demo");
		 outerobj.put("text-align", "center");
		 outerobj.put("font-size", "30");
		 outerobj.put("font-style", "Regular");
		 outerobj.put("font-famiy", "Arial Unicode Ms");
		 outerobj.put("opacity","0.2");
		 outerobj.put("Transparency", "20");
		 outerobj.put("rotation", "45");
		 outerobj.put("font-color", "#666666");
		 outerobj.put("pagesize", "fit");
		 outerobj.put("vertical_position", "middle");
		 outerobj.put("page", "all");
		 outerobj.put("horizontal_position", "center");
		 outerobj.put("layer", "above");
		 outerobj.put("tool", "watermark");
		 JSONArray js = new JSONArray();
		 JSONObject innerobj = new JSONObject();
		 innerobj.put("server_filename", newFilename);
		 innerobj.put("filename", "result.pdf");
		 js.put(innerobj);
		 outerobj.put("files", js);
		 RequestBody body = RequestBody.create(outerobj.toString(),MediaType.parse("application/json"));
		Request request = new Request
				.Builder()
				.url("https://" + server + "/v1/process")
				.method("POST",body)
				.addHeader("Authorization", "Bearer " + token)
				.addHeader("Content-Type", "application/json")
				.build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	public static String download(String token, String task, String server) throws IOException {

		Request request = new Request
				.Builder()
				.url("https://" + server + "/v1/download/" + task)
				.method("GET", null)
				.addHeader("Authorization", "Bearer " + token)
				.build();
		OkHttpClient client = new OkHttpClient()
				.newBuilder()
				.build();
		Response response = client.newCall(request).execute();
		if (response.isSuccessful()) {
		      Files.copy(
		        response.body().byteStream(),
		        FileSystems.getDefault().getPath("result2.pdf"),
		        StandardCopyOption.REPLACE_EXISTING
		      );
		    } else {
		      // Handle the error
		      throw new IOException(response.body().string());
		    }
		return response.body().string();
	}

	public static void main(String[] args) throws Exception {
		/* Token */
		String str = getResponse();
		String token = str.substring(10, str.length() - 2);
		System.out.println("Token : " + token + "\n");
		/* Server & TaskId */
		String[] ss = (getServerAndTaskId(token)).split(",");
		String server = ss[0].substring(11, ss[0].length() - 1);
		System.out.println("Server : " + server + "\n");
		String task = ss[1].substring(8, ss[1].length() - 2);
		System.out.println("Task : " + task + "\n");
		/* Server_filename */
		String fileName = getServerFileName(server, task, token);
		String newFilename = fileName.substring(20, fileName.length() - 2);
		System.out.println("Server_filename : " + newFilename + "\n");
		/* Process */
		String process = operations(token, server, task, newFilename);
		System.out.println("Process : " + process + "\n");
		/* Download_file */
		String dwn = download(token, task, server);
		System.out.println("Download : " + dwn + "\n");
	}
}
