import io.vertx.core.Future

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

class ResponseService {

    fun registrationSuccess(): Future<String> {
        return Future.succeededFuture("registrationSuccess");
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
}