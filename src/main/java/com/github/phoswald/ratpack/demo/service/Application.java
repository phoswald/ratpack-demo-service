package com.github.phoswald.ratpack.demo.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import ratpack.handling.Context;
import ratpack.http.Request;
import ratpack.server.RatpackServer;

public class Application {

    private static final String SESSION_COOKIE = "MY_SESSION";
    private static final Logger logger = Logger.getLogger(Application.class);
    private static final Map<String, String> map = new HashMap<>();

    public static void main(String[] args) throws Exception {
        logger.info("A simple and cool HTTP server is starting :-)");
        RatpackServer.start(server -> server
                .serverConfig(c -> c.port(8080))
                .handlers(chain -> chain
                        .get(ctx -> printResource(ctx, "/index.html", "text/html"))
                        .get("health", ctx -> ctx.render("OK\n"))
                        .get("about", ctx -> ctx.render(Application.class.getPackage().getName() + " " + Application.class.getPackage().getImplementationVersion() + "\n"))
                        .get("now", ctx -> ctx.render(ZonedDateTime.now() + "\n"))
                        .get("locnow", ctx -> ctx.render(LocalDateTime.now() + "\n"))
                        .get("locale", ctx -> ctx.render(Locale.getDefault() + "\n"))
                        .get("timezone", ctx -> ctx.render(TimeZone.getDefault().getID() + "\n"))
                        .get("charset", ctx -> ctx.render(Charset.defaultCharset().name() + "\n"))
                        .get("req", ctx -> printRequest(ctx))
                        .get("greet", ctx -> ctx.render("Hello, " + Optional.ofNullable(ctx.getRequest().getQueryParams().get("name")).orElse("Stranger") + "!\n"))
                        .get("args", ctx -> ctx.render(Arrays.asList(args).toString() + "\n"))
                        .get("env", ctx -> printMap(ctx, System.getenv()))
                        .get("env/:name", ctx -> printMapEntry(ctx, System.getenv(), ctx.getPathTokens().get("name")))
                        .get("prop", ctx -> printMap(ctx, System.getProperties()))
                        .get("prop/:name", ctx -> printMapEntry(ctx, System.getProperties(), ctx.getPathTokens().get("name")))
                        .get("heap", ctx -> printHeap(ctx))
                        .get("mem", ctx -> printMap(ctx, map))
                        .path("mem/:key", ctx2 -> ctx2.byMethod(chain2 -> chain2
                                .get(ctx -> printMapEntry(ctx, map, ctx.getPathTokens().get("key")))
                                .put(ctx -> storeMapEntry(ctx, map, ctx.getPathTokens().get("key"), ctx.getRequest().getQueryParams().get("value")))
                        ))
                        .path("file", ctx2 -> ctx2.byMethod(chain2 -> chain2
                                .get(ctx -> printFile(ctx, Paths.get(Optional.ofNullable(ctx.getRequest().getQueryParams().get("path")).orElse("/"))))
                                .put(ctx -> storeFile(ctx, Paths.get(Optional.ofNullable(ctx.getRequest().getQueryParams().get("path")).orElse("/")), ctx.getRequest().getQueryParams().get("content")))
                        ))
                        .get("session", ctx -> handleSession(ctx, ctx.getRequest().getQueryParams().get("logout") != null))
                        .post("log", ctx -> { logger.info(Optional.ofNullable(ctx.getRequest().getQueryParams().get("message"))); ctx.render("OK\n"); })
                        .post("exit", ctx -> System.exit(1))));
    }

    private static void printRequest(Context ctx) {
        Request req = ctx.getRequest();
        StringBuilder sb = new StringBuilder();
        sb.append(req.getMethod() + " " + req.getPath() + " " + req.getProtocol() + "\n");
        req.getHeaders().asMultiValueMap()
                .forEach((key, value) -> sb.append(key + ": " + value + "\n"));
        sb.append("\n");
        sb.append("Remote address = " + req.getRemoteAddress() + "\n");
        sb.append("Local address = " + req.getLocalAddress() + "\n");
        sb.append("Raw URI = " + req.getRawUri() + "\n");
        sb.append("URI = " + req.getUri() + "\n");
        sb.append("Query = " + req.getQuery() + "\n");
        sb.append("Query Params = " + req.getQueryParams().size() + "\n");
        req.getQueryParams()
                .forEach((key, value) -> sb.append(" - " + key + ": " + value + "\n"));
        sb.append("Cookies = " + req.getCookies().size() + "\n");
        req.getCookies()
                .forEach(cookie -> sb.append(" - " + cookie.name() + "=" + cookie.value() + "\n"));
        ctx.render(sb.toString());
    }

    private static void printMap(Context ctx, Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        map.entrySet().stream()
                .sorted(Comparator.comparing(e -> (String) e.getKey()))
                .forEach(e -> sb.append(e.getKey() + "=" + e.getValue().toString().replace('\n', '|') + "\n"));
        if (sb.length() == 0) {
            sb.append("<empty>\n");
        }
        ctx.render(sb.toString());
    }

    private static void printMapEntry(Context ctx, Map<?, ?> map, String key) {
        ctx.render(Optional.ofNullable(map.get(key))
                .map(val -> val.toString())
                .orElse("<not defined>") + "\n");
    }

    private static void storeMapEntry(Context ctx, Map<String, String> map, String key, String value) {
        if (value != null && value.length() > 0) {
            map.put(key, value);
        } else {
            map.remove(key);
        }
        ctx.render("OK\n");
    }

    private static void printResource(Context ctx, String name, String contentType) {
       try(InputStream stm = Application.class.getResourceAsStream(name)) {
          ByteArrayOutputStream bytes = new ByteArrayOutputStream();
          int current;
          while((current = stm.read()) != -1) {
             bytes.write(current);
          }
          ctx.getResponse().send(contentType, bytes.toByteArray());
       } catch (IOException e) {
          logger.error("Unexpected trouble", e);
          ctx.getResponse().status(500);
          ctx.render("");
      }
    }

    private static void printHeap(Context ctx) {
        StringBuilder sb = new StringBuilder();
        Runtime rt = Runtime.getRuntime();
        sb.append("max:   at most   " + (rt.maxMemory() == Long.MAX_VALUE ? "(unlimited)\n" : ((rt.maxMemory() >> 20) + " MB\n")));
        sb.append("total: currently " + (rt.totalMemory() >> 20) + " MB\n");
        sb.append("free:  currently " + (rt.freeMemory() >> 20) + " MB\n");
        ctx.render(sb.toString());
    }

    private static void printFile(Context ctx, Path path) {
        try {
            if(Files.isRegularFile(path)) {
        		logger.info("Printing " + path);
                ctx.getResponse().sendFile(path);
            } else if(Files.isDirectory(path)) {
                String content = Files.list(path)
                        .map(p -> p.getFileName().toString() + (Files.isDirectory(p) ? "/" : ""))
                        .sorted()
                        .collect(Collectors.joining("\n", "", "\n"));
                ctx.render(content);
            } else {
                ctx.getResponse().status(404);
                ctx.render("");
            }
        } catch (IOException e) {
            logger.error("Unexpected trouble", e);
            ctx.getResponse().status(500);
            ctx.render("");
        }
    }

    private static void storeFile(Context ctx, Path path, String content) {
    	try {
    		logger.info("Storing " + path + ": " + content);
	    	Files.createDirectories(path.getParent());
	    	Files.write(path, Arrays.asList(content));
            ctx.render("OK\n");
    	} catch (IOException e) {
            logger.error("Unexpected trouble", e);
            ctx.getResponse().status(500);
            ctx.render("");
		}
    }

    private static void handleSession(Context ctx, boolean logout) {
        if (logout) {
            ctx.getResponse().expireCookie(SESSION_COOKIE);
            ctx.render("Session: none\n");
        } else {
            String cookie = ctx.getRequest().oneCookie(SESSION_COOKIE);
            if (cookie == null) {
                cookie = "S" + Instant.now().toEpochMilli();
                ctx.getResponse().cookie(SESSION_COOKIE, cookie);
            }
            ctx.render("Session: " + cookie + "\n");
        }
    }
}
