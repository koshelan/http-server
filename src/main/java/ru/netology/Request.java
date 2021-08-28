package ru.netology;

import java.util.ArrayList;

public class Request {

    private final String method;
    private final String path;
    private final String protocol;
    private final ArrayList<String> headers;
    private String body;

    public Request(String method, String path, String protocol, ArrayList headers) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.headers = headers;
        this.body = null;
    }

    public Request(String method, String path, String protocol, ArrayList headers, String body) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.headers = headers;
        this.body = body;
    }

    public Request(String method, String path, String protocol) {
        this.method = method;
        this.path = path;
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
                ", protocol='" + protocol + '\n' +
                ", headers=" + headers + '\n' +
                ", body='" + body + '\n' +
                '}';
    }
}
