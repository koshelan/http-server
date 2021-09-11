package ru.netology;

import java.util.List;
import java.util.Optional;

public class RequestField {

    private final List<String> headers;
    private String body;


    public RequestField(List<String> headers) {
        this.headers = headers;
        body = null;
    }

    public RequestField(List<String> headers, String body) {
        this.headers = headers;
        this.body = body;
    }

    @Override
    public String toString() {
        return "RequestField{" +
                "headers=" + headers +
                '}' + '\n';
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public Optional<String> getHeaderByName(String name) {
        return headers.stream()
                      .filter(o -> o.startsWith(name))
                      .map(o -> o.indexOf("=\"") != -1 ? o.substring(o.indexOf("=\"") + 2, o.length() - 1)
                                                       : o.substring(o.indexOf(" ") + 1))
                      .findFirst();
    }

}
