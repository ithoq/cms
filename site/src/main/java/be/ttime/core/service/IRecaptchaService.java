package be.ttime.core.service;

public interface IRecaptchaService {
    boolean isResponseValid(String remoteIp, String response);
}
