package ru.netology;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Request {

    private final String method;
    private final String path;
    private final String query;
    private final String protocol;
    private final ArrayList<String> headers;
    private String body;

    public Request(String method, String path, String protocol) {
        this.method = method;
        this.path = path;
        this.query = null;
        this.protocol = protocol;
        this.headers = new ArrayList();
        this.body = null;
    }

    public Request(String method, String path, String query, String protocol) {
        this.method = method;
        this.path = path;
        this.query = query;
        this.protocol = protocol;
        this.headers = new ArrayList();
        this.body = null;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public ArrayList<String> getHeaders() {
        return headers;
    }

    public void addHeader(String header) {
        headers.add(header);
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\n' +
                ", path='" + path + '\n' +
                ", query='" + query + '\n' +
                ", protocol='" + protocol + '\n' +
                ", headers=" + headers + '\n' +
                ", body='" + body + '\n' +
                '}';
    }

    public List<String> getQueryParams() {
        return Arrays.stream(query.split("&")).collect(Collectors.toList());
    }

    public List<String> getQueryParam(String name) {
        return Arrays.stream(query.split("&"))
                     .filter(o -> o.startsWith(name))
                     .map(o -> o.substring(name.length() + 1))
                     .map(String::trim)
                     .map(o -> URLDecoder.decode(o, StandardCharsets.UTF_8))
                     .collect(Collectors.toList());
    }

    public Optional<String> getHeaderByName(String header) {
        return headers.stream()
                      .filter(o -> o.startsWith(header))
                      .map(o -> o.substring(o.indexOf(" ")))
                      .map(String::trim)
                      .findFirst();
    }

}
