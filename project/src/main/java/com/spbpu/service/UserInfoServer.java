/**
 * Created by kivi on 29.05.17.
 */

package com.spbpu.service;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.spbpu.facade.Facade;
import com.spbpu.facade.FacadeImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

class UserImpl {
    String login;
    String name;
    String email;
    List<String> projects;
    List<String> tickets;
    List<String> reports;
}

public class UserInfoServer {

    private static Facade facade = new FacadeImpl();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/get", new GetHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("The server is running");
    }

    static class GetHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {
            StringBuilder response = new StringBuilder();
            Map <String,String> parms = UserInfoServer.queryToMap(httpExchange.getRequestURI().getQuery());
            try {
                Gson gson = new Gson();
                UserImpl user = UserInfoServer.getUserImpl(parms.get("user"));
                response.append(gson.toJson(user));
            } catch (Exception e) {
                response.append("error");
            }
            UserInfoServer.writeResponse(httpExchange, response.toString());
        }
    }

    public static void writeResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }


    /**
     * returns the url parameters in a map
     * @param query
     * @return map
     */
    public static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }

    private static UserImpl getUserImpl(String user) throws Exception {
        UserImpl usr = new UserImpl();
        usr.login = user;
        usr.name = facade.getUserName(user);
        usr.email = facade.getUserEmail(user);
        usr.projects = facade.getAllProjects(user);
        usr.tickets = facade.getAssignedTickets(user).stream().
                map(pair -> pair.getFirst() + ":"+ pair.getSecond().toString()).
                collect(Collectors.toList());
        usr.reports = facade.getAssignedReports(user).stream().
                map(pair -> pair.getFirst() + ":"+ pair.getSecond().toString()).
                collect(Collectors.toList());
        return usr;
    }

}