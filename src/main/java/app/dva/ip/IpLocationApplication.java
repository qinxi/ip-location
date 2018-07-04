package app.dva.ip;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import net.ipip.datx.City;
import net.ipip.datx.IPv4FormatException;

import java.io.IOException;
import java.util.function.Consumer;

public class IpLocationApplication {

    public static void main(String[] args) {
        City city = null;
        try {
            city = new City(IpLocationApplication.class.getClass().getResourceAsStream("/17monipdb.datx"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Consumer<Vertx> consumer = c -> {
        };
        Vertx vertx = Vertx.vertx();
        consumer.accept(vertx);

        Router router = Router.router(vertx);
        //router.route().handler(BodyHandler.create());
        router.route().handler(ctx -> {
            ctx.response().putHeader("content-type", "application/json");
            ctx.next();
        });

        City finalCity = city;
        router.get("/").handler(ctx -> {
            IpLocation ipLocation = new IpLocation("", "", "");
            String ip = ctx.request().getParam("ip");
            try {
                String[]  strings = finalCity.find(ip);
                if (strings.length >= 3) {
                    ipLocation.setCountry(strings[0]);
                    ipLocation.setProvince(strings[1]);
                    ipLocation.setCity(strings[2]);
                }else if (strings.length == 2){
                    ipLocation.setCountry(strings[0]);
                    ipLocation.setProvince(strings[1]);
                } else if (strings.length == 1) {
                    ipLocation.setCountry(strings[0]);
                }
            } catch (IPv4FormatException e) {
                e.printStackTrace();
            }
            ctx.response().end(Json.encode(ipLocation));
        });
        vertx.createHttpServer().requestHandler(router::accept).listen(9999);
        System.out.println("listen on 9999");
    }
}
