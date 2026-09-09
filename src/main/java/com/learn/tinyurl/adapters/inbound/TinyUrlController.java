package com.learn.tinyurl.adapters.inbound;


import com.learn.tinyurl.api.TinyurlApi;
import com.learn.tinyurl.domain.model.TinyUrl;
import com.learn.tinyurl.model.CreateUrlRequest;
import com.learn.tinyurl.model.CreateUrlResponse;
import com.learn.tinyurl.model.Error;
import com.learn.tinyurl.ports.inbound.TinyUrlPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TinyUrlController implements TinyurlApi {

    private final TinyUrlPort tinyUrlPort;

    public TinyUrlController(TinyUrlPort tinyUrlPort) {
        this.tinyUrlPort = tinyUrlPort;
    }

    @Override
    public ResponseEntity<CreateUrlResponse> createShortUrl(CreateUrlRequest createUrlRequest) {

        TinyUrlPort.CreateShortUrlCommand createShortUrlCommand =
                new TinyUrlPort.CreateShortUrlCommand(createUrlRequest.getLongUrl(),
                        createUrlRequest.getCustomerAlias(), createUrlRequest.getExpireAt().toInstant());

        TinyUrl tinyUrl = tinyUrlPort.createTinyUrl(createShortUrlCommand);

        CreateUrlResponse createUrlResponse = new CreateUrlResponse();
        createUrlResponse.setLongUrl(tinyUrl.url());
        createUrlResponse.setShortKey(tinyUrl.shortKey());

        return ResponseEntity.status(HttpStatus.CREATED).body(createUrlResponse);
    }

    @Override
    public ResponseEntity<Error> getShortUrl(String shortKey) {

        TinyUrl tinyUrl = tinyUrlPort.resolveTinyUrl(shortKey);

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .location(tinyUrl.url())
                .build();
    }
}
