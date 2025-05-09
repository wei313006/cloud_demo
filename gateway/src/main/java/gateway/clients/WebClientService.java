package gateway.clients;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WebClientService {

    private final WebClient.Builder webClientBuilder;

    public WebClientService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public <T> Mono<T> sendGet(String url, ParameterizedTypeReference<T> typeReference) {
        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(typeReference);
    }

    public <T> Mono<T> sendDelete(String url, ParameterizedTypeReference<T> typeReference) {
        return webClientBuilder.build()
                .delete()
                .uri(url)
                .retrieve()
                .bodyToMono(typeReference);
    }

    public <T, R> Mono<R> sendPost(String url, T body,ParameterizedTypeReference<R> typeReference) {
        return webClientBuilder.build()
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(typeReference);
    }


    public <T, R> Mono<R> sendPut(String url, T body, ParameterizedTypeReference<R> typeReference) {
        return webClientBuilder.build()
                .put()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(typeReference);
    }

}
