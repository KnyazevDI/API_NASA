import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;

import static java.lang.System.out;

public class Main {
    public static final String REMOTE_URI = "https://api.nasa.gov/planetary/apod?api_key" +
            "=PAaKKFwqyWpAwaSGtkFXvRdGS8qfjsPpZSVT3iMk";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build()) {

        HttpGet request = new HttpGet(REMOTE_URI);
        CloseableHttpResponse response = httpClient.execute(request);

        Answer answer = mapper.readValue(
                response.getEntity().getContent(),
                new TypeReference<>() {
                }
        );

        String newRemoteUrl = answer.getUrl();
        HttpGet request2 = new HttpGet(newRemoteUrl);
        CloseableHttpResponse response2 = httpClient.execute(request2);

        FileOutputStream fos = new FileOutputStream("D://" + getSaveName(newRemoteUrl));
            HttpEntity entity = response2.getEntity();
            entity.writeTo(fos);

        } catch (IOException ex) {
            out.println(ex.getMessage());
        }
    }

    private static String getSaveName(String newRemoteUrl) {
        String[] saveName = newRemoteUrl.split("/");
        return saveName[saveName.length-1];
    }
}
