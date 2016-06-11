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

    @JvmStatic fun main(args: Array<String>) {
        val jedis: Jedis = Jedis("localhost", 6379)
        jedis.set("foo", "bar");
        println(jedis.get("foo"))
        jedis.del("foo")
        val port = 8080
        val vertx = Vertx.vertx()
        val server = vertx.createHttpServer()
        val router = Router.router(vertx)

        val userService = MemoryUserService()
        val responseService = ResponseService()

        router.get("/:login").handler { ctx ->
            router.route().handler(CorsHandler.create("http://localhost:63342").allowedMethod(HttpMethod.GET))
            val login = ctx.request().getParam("login")
            jsonResponse(ctx, userService.getUser(login))
        }
        router.get("/:registration/:email/:pass").handler { ctx ->
            router.route().handler(CorsHandler.create("http://localhost:63342").allowedMethod(HttpMethod.GET))
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

        router.post("/").handler { ctx ->
            val user = jsonRequest<User>(ctx, User::class)
            jsonResponse(ctx, userService.addUser(user))
        }

        router.delete("/:userId").handler { ctx ->
            val userId = ctx.request().getParam("userId")
            jsonResponse(ctx, userService.remUser(userId))
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