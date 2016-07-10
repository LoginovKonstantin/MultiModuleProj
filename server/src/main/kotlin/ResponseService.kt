import io.vertx.core.Future
import java.util.*

/**
 * Created by 4 on 09.06.2016.
 */
data class User(val email: String,
                val pass: String,
                val date: String,
                var ip: String,
                var countInput: String,
                var id: String,
                var lastSeen: Long) {
    override fun toString(): String {
        return "$email,$pass,$date,$ip,$countInput,$id,$lastSeen"
    }
}

data class Chat(var nameChat: String)
data class UserOnEmail(var ip: String, var countInput: String, var date: String)

class ResponseService {

    fun registrationSuccess(): Future<String> {
        return Future.succeededFuture("registrationSuccess");
    }

    fun createNewChatFail(): Future<String> {
        return Future.succeededFuture("createNewChatFail");
    }

    fun registrationFail(): Future<String> {
        return Future.succeededFuture("registrationFail");
    }

    fun loginSuccess(): Future<String> {
        return Future.succeededFuture("loginSuccess")
    }

    fun loginFail(): Future<String> {
        return Future.succeededFuture("loginFail")
    }

    fun getUser(user: User): Future<String> {
        return Future.succeededFuture(User(
                user.email, user.pass,
                user.date, user.ip,
                user.countInput,
                user.id,user.lastSeen).toString())
    }

    fun getUser(email: String, pass: String, date: String, ip: String, countInput: String, id: String, lastSeen: Long): Future<String> {
        return Future.succeededFuture(User(email, pass, date, ip, countInput, id, lastSeen).toString())
    }

    fun getNameExistChats(chats: ArrayList<Chat>?):Future<String>{
        var namesChats = "";
        chats!!.forEach {
            if(it.equals(chats.last())){
                namesChats += it.nameChat
            }else{
                namesChats += it.nameChat + ";" }
            }
        return Future.succeededFuture(namesChats)
    }

    fun chatsNotExist():Future<String>{
        return Future.succeededFuture("getExistChatsFail")
    }

    fun createNewChatSuccess(chat: Chat): Future<Chat> {
        return Future.succeededFuture(chat);
    }
}