/**
 * Created by 4 on 03.06.2016.
 */
import com.google.gson.Gson
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import redis.clients.jedis.Jedis
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

//пользователи онлайн
var usersOnline: ArrayList<User> = arrayListOf()
//созданые чаты
var chats: ArrayList<Chat> = arrayListOf()

object Vertx3KotlinRestJdbcTutorial2{

    val gson = Gson()
    var idUser = 0;
    var idChat = 0;

    @JvmStatic fun main(args: Array<String>) {

        ThreadCheckUsers()
        val jedis: Jedis = Jedis("localhost", 6379)
        val port = 8080
        val vertx = Vertx.vertx()
        val server = vertx.createHttpServer()
        val router = Router.router(vertx)

        val responseService = ResponseService()

        /**
         * Запрос при авторизации, проверка на существование пользователя,
         * проверка на совпадение логина и пароля, инкремент количества входов
         */
        router.get("/login/:email/:pass").handler { ctx ->
            idUser++
            val email = ctx.request().getParam("email")
            val pass = ctx.request().getParam("pass")

            if (jedis.hexists(email, "password") && jedis.hget(email, "password").equals(pass)) {
                val newCountInput = (jedis.hget(email, "countInput")).toInt() + 1
                val newIp = ctx.request().remoteAddress()

                jedis.hset(email, "countInput", newCountInput.toString())
                jedis.hset(email, "ip", newIp.toString())
                jedis.save()

                val user = User(email,
                        jedis.hget(email, "password"),
                        jedis.hget(email, "date"),
                        jedis.hget(email, "ip"),
                        jedis.hget(email, "countInput"),
                        idUser.toString(),
                        System.currentTimeMillis())
                user.countInput = (user.countInput.toInt() - 1).toString()
                if(usersOnline.contains(user)){ usersOnline.remove(user)}
                user.countInput = (user.countInput.toInt() + 1).toString()
                usersOnline.add(user)
                println("entrance " + user)
                jsonResponse(ctx, responseService.getUser(user))
            } else {
                jsonResponse(ctx, responseService.loginFail());
            }
        }

        /**
         * Создание чата
         */
        router.get("/newChat/:nameChat/:authorChat").handler{ctx ->
            val nameNewChat = ctx.request().getParam("nameChat")
            val author = ctx.request().getParam("authorChat")

            var chatExist = false
            jedis.hgetAll("chats").forEach { if(it.value.equals(nameNewChat)) chatExist = true }
            if(chatExist){
                jsonResponse(ctx, responseService.createNewChatFail())
            }else{
                jedis.hset(nameNewChat, "authorChat", author)
                jedis.hset(nameNewChat, "date", SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis()))

                jedis.hset("chats", nameNewChat, nameNewChat)
                jedis.save()
                jsonResponse(ctx, responseService.createNewChatSuccess(Chat(nameNewChat)))
            }
        }

        /**
         *  Взять все чаты
         */
        router.get("/getExistChats").handler { ctx ->
            if(jedis.hgetAll("chats").size > 0){
                val chats: ArrayList<Chat> = arrayListOf()
                jedis.hgetAll("chats").forEach { chats.add(Chat(it.value));println(it.value) }
                jsonResponse(ctx, responseService.getNameExistChats(chats))
            }else{
                jsonResponse(ctx, responseService.chatsNotExist())
            }
        }

        /**
        * Взять пользователя по id
        */
        router.get("/getUserId/:id").handler { ctx ->
            val currentId = ctx.request().getParam("id")
            for(i in 0..usersOnline.size - 1){
                if(usersOnline[i].id.equals(currentId)){
                    val user = User(usersOnline[i].email, usersOnline[i].pass,
                            usersOnline[i].date, usersOnline[i].ip,
                            usersOnline[i].countInput, usersOnline[i].id,
                            System.currentTimeMillis())
                    if(usersOnline.size == 1){
                        usersOnline = arrayListOf()
                    }else {
                        var restartUser: User? = null
                        for (i in 0..usersOnline.size - 1) {
                            if (usersOnline[i].id.equals(currentId)) {
                                restartUser = usersOnline[i]
                            }
                        }
                        if(restartUser != null)
                            usersOnline.remove(restartUser)
                    }
                    usersOnline.add(user)
                    jsonResponse(ctx, responseService.getUser(user))
                }
            }
        }

        /**
         *Запрос при выходе пользователя
         */
        router.get("/exit/:email").handler { ctx ->
            val email = ctx.request().getParam("email")
            println(email)
            if(usersOnline.size == 1){
                usersOnline = arrayListOf()
            }else{
                var exitUser: User? = null
                for(i in 0..usersOnline.size - 1){
                    if(usersOnline[i].email.equals(email)){
                        println("exit " + usersOnline[i])
                        exitUser = usersOnline[i]
                    }
                }
                if(exitUser != null)
                    usersOnline.remove(exitUser)
            }
        }

        /**
         * Запрос при регистрации, в redis заносится информация о новом пользователе
         */
        router.get("/registration/:email/:pass").handler { ctx ->
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