import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;


public class Main {
    public static ObjectMapper mapper = new ObjectMapper();
    public static NasaResponse nasaResponse;

    public static void main(String[] args) throws IOException {
        final CloseableHttpClient client = HttpClients.createDefault();

        final HttpUriRequest get = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=ySALeQMYd5p8ffbjUUFYylzDaaO68j79SykM34Iu");
        try (CloseableHttpResponse response = client.execute(get)) {
            final HttpEntity entity = response.getEntity();
            nasaResponse = mapper.readValue(response.getEntity().getContent(), new TypeReference<NasaResponse>() {});
        }
        
        String urlImage = null;
        String fileName = null;
        if (!nasaResponse.getMediaType().equals("image")) {
            System.out.println("Сегодняшний контент не картинка, а " + nasaResponse.getMediaType());
        }
        if (nasaResponse.getHdurl() != null) {
            String[] urlParts = nasaResponse.getHdurl().split("/");
            fileName = urlParts[urlParts.length - 1];
        }
        try (BufferedInputStream in = new BufferedInputStream(new URL(nasaResponse.getHdurl()).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            byte dataBuffer[] = new byte[4092];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 4092)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            fileOutputStream.flush();
        } catch (IOException e) {
            // handle exception
        }
    }
}