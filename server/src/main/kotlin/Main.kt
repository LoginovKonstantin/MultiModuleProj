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
import java.util.*
import java.util.function.Predicate
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

        //пользователи онлайн
        var usersOnline: ArrayList<User> = arrayListOf()
        //созданые чаты
//        var chats: ArrayList<> = arrayListOf()

        /**
         * Запрос при авторизации, проверка на существование пользователя,
         * проверка на совпадение логина и пароля, инкремент количества входов
         * **/
        router.get("/login/:email/:pass").handler { ctx ->
            //для кроссдоменного запроса
            router.route().handler(CorsHandler.create(clientAddress).allowedMethod(HttpMethod.GET))

            val email = ctx.request().getParam("email")
            val pass = ctx.request().getParam("pass")

            if (jedis.hexists(email, "password") && jedis.hget(email, "password").equals(pass)) {
                val newCountInput = (jedis.hget(email, "countInput")).toInt() + 1
                val newIp = ctx.request().remoteAddress()

                jedis.hset(email, "countInput", newCountInput.toString())
                jedis.hset(email, "ip", newIp.toString())
                jedis.save()

                var user = User(email, jedis.hget(email, "password"), jedis.hget(email, "date"), jedis.hget(email, "ip"), jedis.hget(email, "countInput"))
                user.countInput = (user.countInput.toInt() - 1).toString()
                if(usersOnline.contains(user)){ usersOnline.remove(user)}
                user.countInput = (user.countInput.toInt() + 1).toString()
                usersOnline.add(user)

                jsonResponse(ctx, responseService.getUser(user))
            } else {
                jsonResponse(ctx, responseService.loginFail());
            }
        }

        /**
         * Запрос при регистрации, в redis заносится информация о новом пользователе
         * **/
        router.get("/registration/:email/:pass").handler { ctx ->
            router.route().handler(CorsHandler.create(clientAddress).allowedMethod(HttpMethod.GET))

            val email = ctx.request().getParam("email")
            val pass = ctx.request().getParam("pass")
            val date = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
            val ip = ctx.request().remoteAddress()

            if (jedis.hexists(email, "password")) {
                jsonResponse(ctx, responseService.registrationFail());
            } else {
                jedis.hset(email, "password", pass)
                jedis.hset(email, "date", date)
                jedis.hset(email, "ip", ip.toString())
                jedis.hset(email, "countInput", 0.toString())
                jedis.save()
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