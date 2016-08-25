package be.ttime.core.service;

import be.ttime.core.service.exception.RecaptchaServiceException;
import com.google.gson.annotations.SerializedName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

@Service
@Slf4j
public class RecaptchaServiceImpl implements IRecaptchaService {

    private static class RecaptchaResponse {
        //@JsonProperty("success")
        @SerializedName("success")
        private boolean success;
        //@JsonProperty("error-codes")
        @SerializedName("error-codes")
        private Collection<String> errorCodes;

        @Override
        public String toString() {
            return "RecaptchaResponse{" +
                    "success=" + success +
                    ", errorCodes=" + errorCodes +
                    '}';
        }
    }

    private final RestTemplate restTemplate;

    @Value("${recaptcha.url}")
    private String recaptchaUrl;

    @Value("${recaptcha.secret-key}")
    private String recaptchaSecretKey;

    @Autowired
    public RecaptchaServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isResponseValid(String remoteIp, String response) {
        log.debug("Validating captcha response for remoteIp={}, response={}", remoteIp, response);
        RecaptchaResponse recaptchaResponse;
        try {
            recaptchaResponse = restTemplate.postForEntity(
                    recaptchaUrl, createBody(recaptchaSecretKey, remoteIp, response), RecaptchaResponse.class)
                    .getBody();
        } catch (RestClientException e) {
            throw new RecaptchaServiceException("Recaptcha API not available exception", e);
        }
        if (recaptchaResponse.success) {
            return true;
        } else {
            log.debug("Unsuccessful recaptchaResponse={}", recaptchaResponse);
            return false;
        }
    }

    private MultiValueMap<String, String> createBody(String secret, String remoteIp, String response) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("secret", secret);
        form.add("remoteip", remoteIp);
        form.add("response", response);
        return form;
    }

}