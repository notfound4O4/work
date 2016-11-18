import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.junit.Test;

public class JsonTest {

	@Test
	public void jsonTest() {

		try {
			// 연결
			URL url = new URL("http://api.androidhive.info/contacts1");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST"); // 보내는 타입
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept-Language", "ko-kr,ko;q=0.8,en-us;q=0.5,en;q=0.3");
			// 데이터
			String param = "{\"title\": \"asdasd\", \"body\" : \"ddddddddd\"}";
			
			// 전송
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

			osw.write(param);
			osw.flush();

			// 응답
			BufferedReader br = null;
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

			String line = null;
			String out = "";
			while ((line = br.readLine()) != null) {
				out += line;
			}
			System.out.println(out.replaceAll(" ", ""));

			// 닫기
			osw.close();
			br.close();
		} catch (MalformedURLException e) {
			System.out.println(e.getCause());
			System.out.println("1");
			e.printStackTrace();
		} catch (ProtocolException e) {
			System.out.println(e.getCause());
			System.out.println("2");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getCause());
			System.out.println("3");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println(e.toString());
			System.out.println("4");
			e.printStackTrace();
		}
	}
}
