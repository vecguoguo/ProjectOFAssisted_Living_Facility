package com.neusoft.yyzx.web;

import com.neusoft.yyzx.common.exception.BusinessException;
import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.common.result.ResultCode;
import com.neusoft.yyzx.entity.User;
import com.neusoft.yyzx.web.controller.AuthController;
import com.neusoft.yyzx.web.controller.BackdownController;
import com.neusoft.yyzx.web.controller.BedController;
import com.neusoft.yyzx.web.controller.CustomerController;
import com.neusoft.yyzx.web.controller.CustomerNurseController;
import com.neusoft.yyzx.web.controller.NurseContentController;
import com.neusoft.yyzx.web.controller.NurseLevelController;
import com.neusoft.yyzx.web.controller.NurseRecordController;
import com.neusoft.yyzx.web.controller.OutwardController;
import com.neusoft.yyzx.web.controller.UserController;
import com.neusoft.yyzx.web.mvc.Json;
import com.neusoft.yyzx.web.mvc.Router;
import com.neusoft.yyzx.web.security.TokenManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Web 层启动类：为 Vue3 前端提供 REST API。
 * <p>
 * 完全基于 JDK 内置 {@link com.sun.net.httpserver.HttpServer} + 自研极简 MVC（{@link Router}/{@link Json}），
 * 不依赖 Spring。控制台入口 {@link com.neusoft.yyzx.Main} 保持不变，两者共用同一套 Service / DAO / 实体。
 * <p>
 * 对外契约与原 Spring Boot 版完全一致：路径前缀 /api、统一响应体 {code,message,data}、
 * 请求头 token 鉴权、全链路 HTTP 200（业务码写在 body），CORS 放行任意来源。
 */
public class WebApplication {

    private static final Logger LOG = Logger.getLogger("yyzx-web");

    /** 请求头中的 token 名称 */
    public static final String TOKEN_HEADER = "token";
    /** 免鉴权路径（登录） */
    private static final String LOGIN_PATH = "/api/auth/login";
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        Router router = buildRouter();

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api", exchange -> handle(exchange, router));
        server.setExecutor(Executors.newFixedThreadPool(16));
        server.start();
        LOG.info("东软颐养中心 Web 服务（纯 JDK HttpServer）已启动，监听端口 " + PORT);
    }

    private static Router buildRouter() {
        Router router = new Router();
        router.register(new AuthController());
        router.register(new UserController());
        router.register(new BedController());
        router.register(new CustomerController());
        router.register(new CustomerNurseController());
        router.register(new NurseLevelController());
        router.register(new NurseContentController());
        router.register(new NurseRecordController());
        router.register(new OutwardController());
        router.register(new BackdownController());
        return router;
    }

    private static void handle(HttpExchange exchange, Router router) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        try {
            addCors(exchange);
            // 跨域预检：直接返回 204
            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            User currentUser = null;
            String token = firstHeader(exchange, TOKEN_HEADER);
            boolean needAuth = !LOGIN_PATH.equals(path);
            if (needAuth) {
                currentUser = TokenManager.get(token);
                if (currentUser == null) {
                    respond(exchange, Result.fail(ResultCode.NOT_LOGIN));
                    return;
                }
            }

            Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
            Map<String, Object> body = readBody(exchange);

            Object result = router.dispatch(method, path, query, body, currentUser, token);
            if (result == null) {
                Result<Void> notFound = Result.fail("接口不存在: " + method + " " + path);
                notFound.setCode(404);
                respond(exchange, notFound);
            } else {
                respond(exchange, result);
            }
        } catch (BusinessException e) {
            LOG.log(Level.WARNING, "业务异常: code=" + e.getCode() + ", message=" + e.getMessage());
            Result<Void> r = Result.fail(e.getMessage());
            r.setCode(e.getCode());
            respond(exchange, r);
        } catch (IllegalArgumentException e) {
            LOG.log(Level.WARNING, "参数异常: " + e.getMessage());
            // 保留具体原因（如“缺少必要参数: roomNo”“参数格式错误: abc”），便于前端/调用方定位；
            // 无具体原因时才回退为通用的“参数校验失败”。
            String msg = (e.getMessage() == null || e.getMessage().isBlank())
                    ? ResultCode.PARAM_ERROR.getMessage()
                    : e.getMessage();
            Result<Void> r = Result.fail(ResultCode.PARAM_ERROR);
            r.setMessage(msg);
            respond(exchange, r);
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, "系统异常", e);
            respond(exchange, Result.fail(ResultCode.FAIL));
        } finally {
            exchange.close();
        }
    }

    /* ---------- HTTP 辅助 ---------- */

    private static void addCors(HttpExchange exchange) {
        String origin = firstHeader(exchange, "Origin");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", origin == null ? "*" : origin);
        if (origin != null) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
            exchange.getResponseHeaders().add("Vary", "Origin");
        }
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "*");
        exchange.getResponseHeaders().set("Access-Control-Max-Age", "3600");
    }

    private static String firstHeader(HttpExchange exchange, String name) {
        return exchange.getRequestHeaders().getFirst(name);
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> map = new HashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) {
            return map;
        }
        for (String pair : rawQuery.split("&")) {
            if (pair.isEmpty()) {
                continue;
            }
            int idx = pair.indexOf('=');
            String key = idx >= 0 ? pair.substring(0, idx) : pair;
            String val = idx >= 0 ? pair.substring(idx + 1) : "";
            map.put(decode(key), decode(val));
        }
        return map;
    }

    private static String decode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    private static Map<String, Object> readBody(HttpExchange exchange) throws IOException {
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        if (bytes.length == 0) {
            return new HashMap<>();
        }
        String json = new String(bytes, StandardCharsets.UTF_8).trim();
        if (json.isEmpty()) {
            return new HashMap<>();
        }
        return Json.parseObject(json);
    }

    private static void respond(HttpExchange exchange, Object body) throws IOException {
        byte[] out = Json.write(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=UTF-8");
        exchange.sendResponseHeaders(200, out.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(out);
        }
    }
}
