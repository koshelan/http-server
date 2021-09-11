package ru.netology;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Request {

    public static final String BODY_SEPARATOR = "\r\n\r\n";
    static final Pattern separators = Pattern.compile("[=\\s]");
    private final String method;
    private final String path;
    private final String query;
    private final String protocol;
    private final ArrayList<String> headers;
    private Optional<String> optionalS;
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
                      .map(o -> {
                          Matcher m = separators.matcher(o);
                          return m.find() ? o.substring(m.start())
                                          : null;
                      })
                      .map(String::trim)
                      .findFirst();
    }

    public List<String> getPostParams() {
        return getHeaderByName("Content-Type").isPresent()
                       && ("application/x-www-form-urlencoded").equalsIgnoreCase(getHeaderByName("Content-Type").get())
               ? Arrays.stream(body.split("&"))
                       .collect(Collectors.toList())
               : null;
    }

    public List<String> getPostParam(String name) {
        return getHeaderByName("Content-Type").isPresent()
                       && ("application/x-www-form-urlencoded").equalsIgnoreCase(getHeaderByName("Content-Type").get())
               ? Arrays.stream(body.split("&"))
                       .filter(o -> o.startsWith(name))
                       .map(o -> o.substring(name.length() + 1))
                       .map(String::trim)
                       .map(o -> URLDecoder.decode(o, StandardCharsets.UTF_8))
                       .collect(Collectors.toList())
               : null;
    }

    public List<RequestField> getParts() {
        String s;
        if (getHeaderByName("Content-Type").isPresent()
                && (s = getHeaderByName("Content-Type").get()).startsWith("multipart/form-data")
        ) {
            s = s.substring(s.indexOf("boundary=")).replace("boundary=", "") + "\r\n";
            var parts = body.split(s);
            List<RequestField> result = new ArrayList<>();
            for (String str : parts) {
                if (!str.isBlank() && !str.startsWith("--")) {
                    var fieldPars = str.split(BODY_SEPARATOR);
                    var fieldHeaders = Arrays.stream(fieldPars[0].split("[;\n]"))
                                             .map(String::trim)
                                             .collect(Collectors.toList());
                    if ((fieldPars.length == 2) && (!fieldPars[1].isBlank())) {
                        result.add(new RequestField(fieldHeaders,
                                                    str.substring(str.indexOf(BODY_SEPARATOR) + BODY_SEPARATOR.length(),
                                                                  str.length() - 3)));
                    } else {
                        result.add(new RequestField(fieldHeaders));
                    }
                }
            }
            return result;
        } else {
            return null;
        }
    }

    public List<RequestField> getPart(String name) {
        var fields = getParts();
        return fields == null ? null
                              : fields.stream()
                                      .filter(o -> ((optionalS = o.getHeaderByName("name")).isPresent()
                                              && optionalS.get().equalsIgnoreCase(name)))
                                      .collect(Collectors.toList());
    }

}
