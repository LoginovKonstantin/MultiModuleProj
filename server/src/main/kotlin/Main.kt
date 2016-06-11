/**
 * Created by 4 on 03.06.2016.
 */
import com.google.gson.Gson
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.CorsHandler
import redis.clients.jedis.Jedis
import java.text.SimpleDateFormat
import kotlin.reflect.KClass

object Vertx3KotlinRestJdbcTutorial2 {

    val gson = Gson()
    val clientAddress = "http://localhost:63342"

    @JvmStatic fun main(args: Array<String>) {

        val jedis: Jedis = Jedis("localhost", 6379)
        val port = 8080
        val vertx = Vertx.vertx()
        val server = vertx.createHttpServer()
        val router = Router.router(vertx)

        val responseService = ResponseService()

        //при входе(авторизации)
        router.get("/:login/:email/:pass").handler { ctx ->
            //для кроссдоменного запроса
            router.route().handler(CorsHandler.create(clientAddress).allowedMethod(HttpMethod.GET))
            val email = ctx.request().getParam("email")
            if(jedis.hexists(email, "password")){
                val newCountInput = (jedis.hget(email, "countInput")).toInt() + 1
                jedis.hset(email, "countInput", newCountInput.toString())
                jsonResponse(ctx, responseService.getUser(email,
                        jedis.hget(email, "password"),
                        jedis.hget(email, "date"),
                        jedis.hget(email, "ip"),
                        jedis.hget(email, "countInput")))
            }else{
                jsonResponse(ctx, responseService.loginFail());
            }
        }

        //при регистрации
        router.get("/:registration/:email/:pass").handler { ctx ->
            router.route().handler(CorsHandler.create(clientAddress).allowedMethod(HttpMethod.GET))
            val email = ctx.request().getParam("email")
            val pass = ctx.request().getParam("pass")
            val date = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
            val ip = ctx.request().remoteAddress()

            if(jedis.hexists(email, "password")){
                jsonResponse(ctx, responseService.registrationFail());
            }else{
                jedis.hset(email, "password", pass)
                jedis.hset(email, "date", date)
                jedis.hset(email, "ip", ip.toString())
                jedis.hset(email, "countInput", 0.toString())
                jsonResponse(ctx, responseService.registrationSuccess());
            }
        }

        server.requestHandler { router.accept(it) }.listen(port) {
            if (it.succeeded()) println("Server listening at $port")
            else println(it.cause())
        }
    }

    fun <T> jsonRequest(ctx: RoutingContext, clazz: KClass<out Any>): T =
            gson.fromJson(ctx.bodyAsString, clazz.java) as T


    fun <T> jsonResponse(ctx: RoutingContext, future: Future<T>) {
        future.setHandler {
            if (it.succeeded()) {
                val res = if (it.result() == null) "" else gson.toJson(it.result())
                ctx.response().end(res)
            } else {
                ctx.response().setStatusCode(500).end(it.cause().toString())
            }
        }
    }
}